package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.KnowledgeChapter;

/**
 * 知识点章节Mapper接口
 * 
 * @author ruoyi
 * @date 2025-11-28
 */
public interface KnowledgeChapterMapper 
{
    /**
     * 查询知识点章节
     * 
     * @param chapterId 知识点章节主键
     * @return 知识点章节
     */
    public KnowledgeChapter selectKnowledgeChapterByChapterId(Long chapterId);

    /**
     * 查询知识点章节列表
     * 
     * @param knowledgeChapter 知识点章节
     * @return 知识点章节集合
     */
    public List<KnowledgeChapter> selectKnowledgeChapterList(KnowledgeChapter knowledgeChapter);

    /**
     * 新增知识点章节
     * 
     * @param knowledgeChapter 知识点章节
     * @return 结果
     */
    public int insertKnowledgeChapter(KnowledgeChapter knowledgeChapter);

    /**
     * 修改知识点章节
     * 
     * @param knowledgeChapter 知识点章节
     * @return 结果
     */
    public int updateKnowledgeChapter(KnowledgeChapter knowledgeChapter);

    /**
     * 删除知识点章节
     * 
     * @param chapterId 知识点章节主键
     * @return 结果
     */
    public int deleteKnowledgeChapterByChapterId(Long chapterId);

    /**
     * 批量删除知识点章节
     * 
     * @param chapterIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteKnowledgeChapterByChapterIds(Long[] chapterIds);
}
