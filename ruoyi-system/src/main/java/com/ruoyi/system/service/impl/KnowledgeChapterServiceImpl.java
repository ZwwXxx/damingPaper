package com.ruoyi.system.service.impl;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.KnowledgeChapter;
import com.ruoyi.system.mapper.KnowledgeChapterMapper;
import com.ruoyi.system.service.IKnowledgeChapterService;

/**
 * 知识点章节Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-11-28
 */
@Service
public class KnowledgeChapterServiceImpl implements IKnowledgeChapterService 
{
    @Autowired
    private KnowledgeChapterMapper knowledgeChapterMapper;

    /**
     * 查询知识点章节
     * 
     * @param chapterId 知识点章节主键
     * @return 知识点章节
     */
    @Override
    public KnowledgeChapter selectKnowledgeChapterByChapterId(Long chapterId)
    {
        return knowledgeChapterMapper.selectKnowledgeChapterByChapterId(chapterId);
    }

    /**
     * 查询知识点章节列表
     * 
     * @param knowledgeChapter 知识点章节
     * @return 知识点章节
     */
    @Override
    public List<KnowledgeChapter> selectKnowledgeChapterList(KnowledgeChapter knowledgeChapter)
    {
        return knowledgeChapterMapper.selectKnowledgeChapterList(knowledgeChapter);
    }

    /**
     * 构建章节树形结构
     * 
     * @param subjectId 科目ID
     * @return 章节树
     */
    @Override
    public List<KnowledgeChapter> buildChapterTree(Long subjectId)
    {
        KnowledgeChapter query = new KnowledgeChapter();
        query.setSubjectId(subjectId);
        query.setStatus(1); // 只查询启用的章节
        List<KnowledgeChapter> chapterList = knowledgeChapterMapper.selectKnowledgeChapterList(query);
        return buildTree(chapterList, 0L);
    }

    /**
     * 递归构建树形结构
     * 
     * @param list 章节列表
     * @param parentId 父节点ID
     * @return 树形结构
     */
    private List<KnowledgeChapter> buildTree(List<KnowledgeChapter> list, Long parentId)
    {
        List<KnowledgeChapter> tree = new ArrayList<>();
        for (KnowledgeChapter chapter : list)
        {
            if (parentId.equals(chapter.getParentId()))
            {
                chapter.setChildren(buildTree(list, chapter.getChapterId()));
                tree.add(chapter);
            }
        }
        return tree;
    }

    /**
     * 新增知识点章节
     * 
     * @param knowledgeChapter 知识点章节
     * @return 结果
     */
    @Override
    public int insertKnowledgeChapter(KnowledgeChapter knowledgeChapter)
    {
        return knowledgeChapterMapper.insertKnowledgeChapter(knowledgeChapter);
    }

    /**
     * 修改知识点章节
     * 
     * @param knowledgeChapter 知识点章节
     * @return 结果
     */
    @Override
    public int updateKnowledgeChapter(KnowledgeChapter knowledgeChapter)
    {
        return knowledgeChapterMapper.updateKnowledgeChapter(knowledgeChapter);
    }

    /**
     * 批量删除知识点章节
     * 
     * @param chapterIds 需要删除的知识点章节主键
     * @return 结果
     */
    @Override
    public int deleteKnowledgeChapterByChapterIds(Long[] chapterIds)
    {
        return knowledgeChapterMapper.deleteKnowledgeChapterByChapterIds(chapterIds);
    }

    /**
     * 删除知识点章节信息
     * 
     * @param chapterId 知识点章节主键
     * @return 结果
     */
    @Override
    public int deleteKnowledgeChapterByChapterId(Long chapterId)
    {
        return knowledgeChapterMapper.deleteKnowledgeChapterByChapterId(chapterId);
    }
}
