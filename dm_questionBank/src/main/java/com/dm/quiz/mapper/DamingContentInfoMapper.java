package com.dm.quiz.mapper;

import java.util.List;
import com.dm.quiz.domain.DamingContentInfo;

/**
 * 题目信息Mapper接口
 *
 * @author zww
 * @date 2024-10-09
 */
public interface DamingContentInfoMapper
{
    /**
     * 查询题目信息
     *
     * @param id 题目信息主键
     * @return 题目信息
     */
    public DamingContentInfo selectDamingContentInfoById(Long id);

    /**
     * 查询题目信息列表
     *
     * @param damingContentInfo 题目信息
     * @return 题目信息集合
     */
    public List<DamingContentInfo> selectDamingQuestionInfoList(DamingContentInfo damingContentInfo);

    /**
     * 新增题目信息
     *
     * @param damingContentInfo 题目信息
     * @return 结果
     */
    public int insertContentInfo(DamingContentInfo damingContentInfo);

    /**
     * 修改题目信息
     *
     * @param damingContentInfo 题目信息
     * @return 结果
     */
    public int updateDamingQuestionInfo(DamingContentInfo damingContentInfo);

    /**
     * 删除题目信息
     *
     * @param id 题目信息主键
     * @return 结果
     */
    public int deleteDamingQuestionInfoById(Long id);

    /**
     * 批量删除题目信息
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDamingQuestionInfoByIds(Long[] ids);
}
