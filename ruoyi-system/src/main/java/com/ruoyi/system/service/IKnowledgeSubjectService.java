package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.KnowledgeSubject;

/**
 * 知识点科目Service接口
 * 
 * @author ruoyi
 * @date 2025-11-28
 */
public interface IKnowledgeSubjectService 
{
    /**
     * 查询知识点科目
     * 
     * @param subjectId 知识点科目主键
     * @return 知识点科目
     */
    public KnowledgeSubject selectKnowledgeSubjectBySubjectId(Long subjectId);

    /**
     * 查询知识点科目列表
     * 
     * @param knowledgeSubject 知识点科目
     * @return 知识点科目集合
     */
    public List<KnowledgeSubject> selectKnowledgeSubjectList(KnowledgeSubject knowledgeSubject);

    /**
     * 新增知识点科目
     * 
     * @param knowledgeSubject 知识点科目
     * @return 结果
     */
    public int insertKnowledgeSubject(KnowledgeSubject knowledgeSubject);

    /**
     * 修改知识点科目
     * 
     * @param knowledgeSubject 知识点科目
     * @return 结果
     */
    public int updateKnowledgeSubject(KnowledgeSubject knowledgeSubject);

    /**
     * 批量删除知识点科目
     * 
     * @param subjectIds 需要删除的知识点科目主键集合
     * @return 结果
     */
    public int deleteKnowledgeSubjectBySubjectIds(Long[] subjectIds);

    /**
     * 删除知识点科目信息
     * 
     * @param subjectId 知识点科目主键
     * @return 结果
     */
    public int deleteKnowledgeSubjectBySubjectId(Long subjectId);
}
