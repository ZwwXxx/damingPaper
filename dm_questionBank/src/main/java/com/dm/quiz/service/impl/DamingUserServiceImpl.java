package com.dm.quiz.service.impl;

import java.util.List;

import com.ruoyi.common.core.domain.model.DamingUser;
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
    

    /**
     * 查询刷题用户
     *
     * @param userId 刷题用户主键
     * @return 刷题用户
     */
    @Override
    public DamingUser selectDamingUserByUserId(Long userId)
    {
        return damingUserMapper.selectDamingUserByUserId(userId);
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
        return damingUserMapper.selectDamingUserList(damingUser);
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
    
}
