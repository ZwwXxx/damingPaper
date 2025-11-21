package com.dm.quiz.service;

import java.util.List;
import com.dm.quiz.domain.DamingPaperAnswer;
import com.dm.quiz.dto.PaperAnswerDto;
import com.dm.quiz.dto.PaperReviewDto;
import com.dm.quiz.dto.PaperReviewSubmitRequest;
import com.dm.quiz.dto.SubmitAnswerResult;

/**
 * 试卷作答情况Service接口
 *
 * @author zww
 * @date 2024-10-13
 */
public interface IDamingPaperAnswerService
{
    /**
     * 查询试卷作答情况
     *
     * @param paperAnswerId 试卷作答情况主键
     * @return 试卷作答情况
     */
    public DamingPaperAnswer selectDamingPaperAnswerByPaperAnswerId(Long paperAnswerId);

    /**
     * 查询试卷作答情况列表
     *
     * @param damingPaperAnswer 试卷作答情况
     * @return 试卷作答情况集合
     */
    public List<DamingPaperAnswer> selectDamingPaperAnswerList(DamingPaperAnswer damingPaperAnswer);

    /**
     * 新增试卷作答情况
     *
     * @param damingPaperAnswer 试卷作答情况
     * @return 结果
     */
    public int insertDamingPaperAnswer(DamingPaperAnswer damingPaperAnswer);

    /**
     * 修改试卷作答情况
     *
     * @param damingPaperAnswer 试卷作答情况
     * @return 结果
     */
    public int updateDamingPaperAnswer(DamingPaperAnswer damingPaperAnswer);

    /**
     * 批量删除试卷作答情况
     *
     * @param paperAnswerIds 需要删除的试卷作答情况主键集合
     * @return 结果
     */
    public int deleteDamingPaperAnswerByPaperAnswerIds(Long[] paperAnswerIds);

    /**
     * 删除试卷作答情况信息
     *
     * @param paperAnswerId 试卷作答情况主键
     * @return 结果
     */
    public int deleteDamingPaperAnswerByPaperAnswerId(Long paperAnswerId);

    SubmitAnswerResult submitAnswer(PaperAnswerDto paperAnswerDto);

    PaperReviewDto getPaperReviewDto(Long paperAnswerId);

    void reviewPaper(PaperReviewSubmitRequest request);
}
