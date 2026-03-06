package com.dm.quiz.service.impl;

import java.util.List;

import com.ruoyi.common.core.domain.model.DamingUser;
import com.ruoyi.common.utils.oss.OssSignUrlHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.dm.quiz.mapper.DamingUserMapper;
import com.dm.quiz.service.IDamingUserService;

/**
 * 刷题用户Service业务层处理
 *
 * @author zww
 * @date 2024-10-18
 */
@Service
public class DamingUserServiceImpl implements IDamingUserService
{
    @Autowired
    private DamingUserMapper damingUserMapper;
    
    @Autowired
    private OssSignUrlHelper ossSignUrlHelper;
    

    /**
     * 查询刷题用户
     *
     * @param userId 刷题用户主键
     * @return 刷题用户
     */
    @Override
    public DamingUser selectDamingUserByUserId(Long userId)
    {
        DamingUser user = damingUserMapper.selectDamingUserByUserId(userId);
        processAvatar(user);
        return user;
    }

    /**
     * 查询刷题用户列表
     *
     * @param damingUser 刷题用户
     * @return 刷题用户
     */
    @Override
    public List<DamingUser> selectDamingUserList(DamingUser damingUser)
    {
        List<DamingUser> list = damingUserMapper.selectDamingUserList(damingUser);
        if (list != null && !list.isEmpty()) {
            for (DamingUser user : list) {
                processAvatar(user);
            }
        }
        return list;
    }

    /**
     * 新增刷题用户
     *
     * @param damingUser 刷题用户
     * @return 结果
     */
    @Override
    public int insertDamingUser(DamingUser damingUser)
    {
        return damingUserMapper.insertDamingUser(damingUser);
    }

    /**
     * 修改刷题用户
     *
     * @param damingUser 刷题用户
     * @return 结果
     */
    @Override
    public int updateDamingUser(DamingUser damingUser)
    {
        return damingUserMapper.updateDamingUser(damingUser);
    }

    /**
     * 批量删除刷题用户
     *
     * @param userIds 需要删除的刷题用户主键
     * @return 结果
     */
    @Override
    public int deleteDamingUserByUserIds(Long[] userIds)
    {
        return damingUserMapper.deleteDamingUserByUserIds(userIds);
    }

    /**
     * 删除刷题用户信息
     *
     * @param userId 刷题用户主键
     * @return 结果
     */
    @Override
    public int deleteDamingUserByUserId(Long userId)
    {
        return damingUserMapper.deleteDamingUserByUserId(userId);
    }
    
    private void processAvatar(DamingUser user)
    {
        if (user == null) {
            return;
        }
        String avatar = user.getAvatar();
        if (avatar != null && !avatar.trim().isEmpty()) {
            user.setAvatar(ossSignUrlHelper.convertToSignedUrl(avatar));
        }
    }
    
}
