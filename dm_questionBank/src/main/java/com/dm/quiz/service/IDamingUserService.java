package com.dm.quiz.service;

import java.util.List;
import com.ruoyi.common.core.domain.model.DamingUser;

/**
 * 刷题用户Service接口
 *
 * @author zww
 * @date 2024-10-18
 */
public interface IDamingUserService
{
    /**
     * 查询刷题用户
     *
     * @param userId 刷题用户主键
     * @return 刷题用户
     */
    public DamingUser selectDamingUserByUserId(Long userId);

    /**
     * 查询刷题用户列表
     *
     * @param damingUser 刷题用户
     * @return 刷题用户集合
     */
    public List<DamingUser> selectDamingUserList(DamingUser damingUser);

    /**
     * 新增刷题用户
     *
     * @param damingUser 刷题用户
     * @return 结果
     */
    public int insertDamingUser(DamingUser damingUser);

    /**
     * 修改刷题用户
     *
     * @param damingUser 刷题用户
     * @return 结果
     */
    public int updateDamingUser(DamingUser damingUser);

    /**
     * 批量删除刷题用户
     *
     * @param userIds 需要删除的刷题用户主键集合
     * @return 结果
     */
    public int deleteDamingUserByUserIds(Long[] userIds);

    /**
     * 删除刷题用户信息
     *
     * @param userId 刷题用户主键
     * @return 结果
     */
    public int deleteDamingUserByUserId(Long userId);
}
