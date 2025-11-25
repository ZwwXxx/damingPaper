package com.dm.quiz.service;

import com.dm.quiz.mapper.DamingUserMapper;
import com.ruoyi.common.core.domain.model.DamingUser;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 前台用户Service
 * 
 * @author ruoyi
 */
@Slf4j
@Service
public class DamingUserService {
    
    @Autowired
    private DamingUserMapper userMapper;
    
    /**
     * 根据微信信息查询或创建用户
     * 
     * @param userInfo 微信用户信息
     * @return 用户信息
     */
    public DamingUser findOrCreateByWxInfo(WxOAuth2UserInfo userInfo) {
        String openId = userInfo.getOpenid();
        
        // 先查询是否已存在
        DamingUser user = userMapper.selectByWxOpenId(openId);
        
        if (user != null) {
            // 用户已存在，直接返回，不更新信息
            log.info("用户已存在，直接登录 - userId: {}, userName: {}", user.getUserId(), user.getUserName());
            return user;
        } else {
            // 用户不存在，创建新用户
            log.info("创建新用户 - openId: {}, nickname: {}", openId, userInfo.getNickname());
            user = new DamingUser();
            
            // 生成用户名：微信用户 + openId后8位
            String shortOpenId = openId.substring(openId.length() - 8);
            user.setUserName("wx_" + shortOpenId);
            
            // 设置昵称：如果nickname为空，则使用默认格式
            String nickName = userInfo.getNickname();
            if (nickName == null || nickName.trim().isEmpty()) {
                nickName = "微信用户" + shortOpenId;
                log.info("nickname为空，使用默认昵称: {}", nickName);
            }
            user.setNickName(nickName);
            
            user.setAvatar(userInfo.getHeadImgUrl());
            user.setWxOpenId(openId);
            user.setWxUnionId(userInfo.getUnionId());
            user.setDelFlag("0"); // 未删除
            user.setCreateTime(new Date());
            userMapper.insertDamingUser(user);
            
            log.info("✅ 创建用户成功 - userId: {}, userName: {}", user.getUserId(), user.getUserName());
            return user;
        }
    }
    
    /**
     * 根据OpenID查询用户
     */
    public DamingUser getByWxOpenId(String wxOpenId) {
        return userMapper.selectByWxOpenId(wxOpenId);
    }
}
