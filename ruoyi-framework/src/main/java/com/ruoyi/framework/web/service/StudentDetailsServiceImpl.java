package com.ruoyi.framework.web.service;

import com.dm.quiz.mapper.DamingUserMapper;
import com.ruoyi.common.core.domain.entity.LoginUser;
import com.ruoyi.common.core.domain.model.DamingUser;
import com.ruoyi.common.enums.UserStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.exception.user.UserPasswordNotMatchException;
import com.ruoyi.common.utils.MessageUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.framework.security.context.AuthenticationContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component("StudentDetailsServiceImpl")
public class StudentDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private DamingUserMapper damingUserMapper;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("过来了！");
        DamingUser damingUser = damingUserMapper.selectDamingUserByUserName(username);
        if (StringUtils.isNull(damingUser))
        {
            throw new ServiceException(MessageUtils.message("user.not.exists"));
        }
        else if (UserStatus.DELETED.getCode().equals(damingUser.getDelFlag()))
        {
            throw new ServiceException(MessageUtils.message("user.password.delete"));
        }

        // 比对密码
        Authentication context = AuthenticationContextHolder.getContext();
        String rawPassword = context.getCredentials().toString();
        boolean matches = SecurityUtils.matchesPassword(rawPassword, damingUser.getPassword());
        if (!matches){
            throw new UserPasswordNotMatchException();
        }
        LoginUser loginUser = new LoginUser(damingUser.getUserId(), damingUser);
        // new LoginUser(user.getUserId(), user.getDeptId(),
        //         user, permissionService.getMenuPermission(user));
        return loginUser;
    }
}
