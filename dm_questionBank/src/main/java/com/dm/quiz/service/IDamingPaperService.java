package com.dm.quiz.service;

import java.util.List;

import com.dm.quiz.domain.DamingPaper;
import com.dm.quiz.dto.AutoAssemblePaperRequest;
import com.dm.quiz.dto.PaperDto;

/**
 * 试卷Service接口
 *
 * @author zww
 * @date 2024-10-10
 */
public interface IDamingPaperService
{
    /**
     * 查询试卷
     *
     * @param paperId 试卷主键
     * @return 试卷
     */
    public DamingPaper selectDamingPaperByPaperId(Long paperId);

    /**
     * 查询试卷列表
     *
     * @param damingPaper 试卷
     * @return 试卷集合
     */
    public List<DamingPaper> selectDamingPaperList(DamingPaper damingPaper);

    /**
     * 新增试卷
     *
     * @param paperDto 试卷
     * @return 结果
     */
    public DamingPaper insertDamingPaper(PaperDto paperDto);

    /**
     * 修改试卷
     *
     * @param paperDto 试卷
     * @return 结果
     */
    public int updateDamingPaper(PaperDto paperDto);

    /**
     * 批量删除试卷
     *
     * @param paperIds 需要删除的试卷主键集合
     * @return 结果
     */
    public int deleteDamingPaperByPaperIds(Long[] paperIds);

    /**
     * 删除试卷信息
     *
     * @param paperId 试卷主键
     * @return 结果
     */
    public int deleteDamingPaperByPaperId(Long paperId);

    /**
     * 试卷VM转为DTO给前端
     */
    PaperDto paperIdtoDto (Long paperId);

    /**
     * 根据规则自动组卷（仅返回建议内容，不落库）
     */
    PaperDto autoAssemblePaper(AutoAssemblePaperRequest request);

    /**
     * 按日期范围自动组卷（将时间范围内的所有题目，按题型自动分组，仅返回建议内容，不落库）
     */
    PaperDto autoAssemblePaperByDate(AutoAssemblePaperRequest request);
}
