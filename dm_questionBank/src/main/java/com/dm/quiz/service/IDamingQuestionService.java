package com.dm.quiz.service;

import java.util.List;
import com.dm.quiz.domain.DamingQuestion;
import com.dm.quiz.dto.QuestionDto;
import com.ruoyi.common.core.domain.AjaxResult;

/**
 * 题目表Service接口
 *
 * @author zww
 * @date 2024-10-08
 */
public interface IDamingQuestionService
{
    /**
     * 查询题目表
     *
     * @param id 题目表主键
     * @return 题目表
     */
    public AjaxResult selectDamingQuestionById(Long id);

    /**
     * 查询题目表列表
     *
     * @param damingQuestion 题目表
     * @return 题目表集合
     */
    public List<DamingQuestion> selectDamingQuestionList(DamingQuestion damingQuestion);

    /**
     * 新增题目表
     *
     * @param questionDto 题目表
     * @return 结果
     */
    public int insertDamingQuestion(com.dm.quiz.dto.QuestionDto questionDto);

    /**
     * 修改题目表
     *
     * @param questionDto 题目表
     * @return 结果
     */
    public int updateDamingQuestion(com.dm.quiz.dto.QuestionDto questionDto);

    /**
     * 批量删除题目表
     *
     * @param ids 需要删除的题目表主键集合
     * @return 结果
     */
    public int deleteDamingQuestionByIds(Long[] ids);

    /**
     * 删除题目表信息
     *
     * @param id 题目表主键
     * @return 结果
     */
    public int deleteDamingQuestionById(Long id);

    /**
     * 将question转化为questionDto
     */
    QuestionDto getQuestionDto ( DamingQuestion damingQuestion);
}
