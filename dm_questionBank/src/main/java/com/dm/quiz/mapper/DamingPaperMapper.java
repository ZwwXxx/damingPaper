package com.dm.quiz.mapper;

import java.util.List;
import com.dm.quiz.domain.DamingPaper;

/**
 * 试卷Mapper接口
 *
 * @author zww
 * @date 2024-10-10
 */
public interface DamingPaperMapper
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
     * @param damingPaper 试卷
     * @return 结果
     */
    public int insertDamingPaper(DamingPaper damingPaper);

    /**
     * 修改试卷
     *
     * @param damingPaper 试卷
     * @return 结果
     */
    public int updateDamingPaper(DamingPaper damingPaper);

    /**
     * 删除试卷
     *
     * @param paperId 试卷主键
     * @return 结果
     */
    public int deleteDamingPaperByPaperId(Long paperId);

    /**
     * 批量删除试卷
     *
     * @param paperIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDamingPaperByPaperIds(Long[] paperIds);
}
