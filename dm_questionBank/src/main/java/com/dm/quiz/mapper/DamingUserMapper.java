package com.dm.quiz.mapper;

import com.ruoyi.common.core.domain.model.DamingUser;

import java.util.List;

/**
 * 刷题用户Mapper接口
 *
 * @author zww
 * @date 2024-10-18
 */
public interface DamingUserMapper
{
    /**
     * 查询刷题用户
     *
     * @param userId 刷题用户主键
     * @return 刷题用户
     */
    public DamingUser selectDamingUserByUserId(Long userId);
    /**
     * 查询刷题用户
     *
     * @param userId 刷题用户名
     * @return 刷题用户
     */
    public DamingUser selectDamingUserByUserName(String userName);


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
     * 删除刷题用户
     *
     * @param userId 刷题用户主键
     * @return 结果
     */
    public int deleteDamingUserByUserId(Long userId);

    /**
     * 批量删除刷题用户
     *
     * @param userIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDamingUserByUserIds(Long[] userIds);
    
    /**
     * 根据微信OpenID查询用户
     *
     * @param wxOpenId 微信OpenID
     * @return 刷题用户
     */
    public DamingUser selectByWxOpenId(String wxOpenId);
    
    /**
     * 更新用户微信信息
     *
     * @param damingUser 刷题用户（包含微信信息）
     * @return 结果
     */
    public int updateWxInfo(DamingUser damingUser);
}
