package com.dm.quiz.mapper;

import java.util.List;
import com.dm.quiz.domain.DamingPaperAnswer;

/**
 * 试卷作答情况Mapper接口
 *
 * @author zww
 * @date 2024-10-13
 */
public interface DamingPaperAnswerMapper
{
    // 根据试卷id查询试卷作答记录
    List<DamingPaperAnswer> selectDamingPaperAnswerByPaperId(Long paperId);
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
     * 删除试卷作答情况
     *
     * @param paperAnswerId 试卷作答情况主键
     * @return 结果
     */
    public int deleteDamingPaperAnswerByPaperAnswerId(Long paperAnswerId);

    /**
     * 批量删除试卷作答情况
     *
     * @param paperAnswerIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDamingPaperAnswerByPaperAnswerIds(Long[] paperAnswerIds);
}
