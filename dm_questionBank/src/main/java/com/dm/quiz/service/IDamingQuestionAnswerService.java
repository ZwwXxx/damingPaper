package com.dm.quiz.service;

import java.util.List;
import com.dm.quiz.domain.DamingQuestionAnswer;

/**
 * 题目回答情况表Service接口
 *
 * @author zww
 * @date 2024-10-13
 */
public interface IDamingQuestionAnswerService
{
    /**
     * 查询题目回答情况表
     *
     * @param answerId 题目回答情况表主键
     * @return 题目回答情况表
     */
    public DamingQuestionAnswer selectDamingQuestionAnswerByAnswerId(Long answerId);

    /**
     * 查询题目回答情况表列表
     *
     * @param damingQuestionAnswer 题目回答情况表
     * @return 题目回答情况表集合
     */
    public List<DamingQuestionAnswer> selectDamingQuestionAnswerList(DamingQuestionAnswer damingQuestionAnswer);

    /**
     * 新增题目回答情况表
     *
     * @param damingQuestionAnswer 题目回答情况表
     * @return 结果
     */
    public int insertDamingQuestionAnswer(DamingQuestionAnswer damingQuestionAnswer);

    /**
     * 修改题目回答情况表
     *
     * @param damingQuestionAnswer 题目回答情况表
     * @return 结果
     */
    public int updateDamingQuestionAnswer(DamingQuestionAnswer damingQuestionAnswer);

    /**
     * 批量删除题目回答情况表
     *
     * @param answerIds 需要删除的题目回答情况表主键集合
     * @return 结果
     */
    public int deleteDamingQuestionAnswerByAnswerIds(Long[] answerIds);

    /**
     * 删除题目回答情况表信息
     *
     * @param answerId 题目回答情况表主键
     * @return 结果
     */
    public int deleteDamingQuestionAnswerByAnswerId(Long answerId);
}
