package com.dm.quiz.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.dm.quiz.mapper.DamingQuestionAnswerMapper;
import com.dm.quiz.domain.DamingQuestionAnswer;
import com.dm.quiz.service.IDamingQuestionAnswerService;

/**
 * 题目回答情况表Service业务层处理
 *
 * @author zww
 * @date 2024-10-13
 */
@Service
public class DamingQuestionAnswerServiceImpl implements IDamingQuestionAnswerService
{
    @Autowired
    private DamingQuestionAnswerMapper damingQuestionAnswerMapper;

    /**
     * 查询题目回答情况表
     *
     * @param answerId 题目回答情况表主键
     * @return 题目回答情况表
     */
    @Override
    public DamingQuestionAnswer selectDamingQuestionAnswerByAnswerId(Long answerId)
    {
        return damingQuestionAnswerMapper.selectDamingQuestionAnswerByAnswerId(answerId);
    }

    /**
     * 查询题目回答情况表列表
     *
     * @param damingQuestionAnswer 题目回答情况表
     * @return 题目回答情况表
     */
    @Override
    public List<DamingQuestionAnswer> selectDamingQuestionAnswerList(DamingQuestionAnswer damingQuestionAnswer)

    {
        if (!SecurityUtils.getUsername().equals("admin")) {
            damingQuestionAnswer.setCreateBy(SecurityUtils.getUsername());
        }
        return damingQuestionAnswerMapper.selectDamingQuestionAnswerList(damingQuestionAnswer);
    }

    /**
     * 新增题目回答情况表
     *
     * @param damingQuestionAnswer 题目回答情况表
     * @return 结果
     */
    @Override
    public int insertDamingQuestionAnswer(DamingQuestionAnswer damingQuestionAnswer)
    {
        damingQuestionAnswer.setCreateTime(DateUtils.getNowDate());
        return damingQuestionAnswerMapper.insertDamingQuestionAnswer(damingQuestionAnswer);
    }

    /**
     * 修改题目回答情况表
     *
     * @param damingQuestionAnswer 题目回答情况表
     * @return 结果
     */
    @Override
    public int updateDamingQuestionAnswer(DamingQuestionAnswer damingQuestionAnswer)
    {
        return damingQuestionAnswerMapper.updateDamingQuestionAnswer(damingQuestionAnswer);
    }

    /**
     * 批量删除题目回答情况表
     *
     * @param answerIds 需要删除的题目回答情况表主键
     * @return 结果
     */
    @Override
    public int deleteDamingQuestionAnswerByAnswerIds(Long[] answerIds)
    {
        return damingQuestionAnswerMapper.deleteDamingQuestionAnswerByAnswerIds(answerIds);
    }

    /**
     * 删除题目回答情况表信息
     *
     * @param answerId 题目回答情况表主键
     * @return 结果
     */
    @Override
    public int deleteDamingQuestionAnswerByAnswerId(Long answerId)
    {
        return damingQuestionAnswerMapper.deleteDamingQuestionAnswerByAnswerId(answerId);
    }
}
