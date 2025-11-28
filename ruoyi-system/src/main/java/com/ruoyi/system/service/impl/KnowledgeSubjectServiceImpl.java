package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.KnowledgeSubject;
import com.ruoyi.system.mapper.KnowledgeSubjectMapper;
import com.ruoyi.system.service.IKnowledgeSubjectService;

/**
 * 知识点科目Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-11-28
 */
@Service
public class KnowledgeSubjectServiceImpl implements IKnowledgeSubjectService 
{
    @Autowired
    private KnowledgeSubjectMapper knowledgeSubjectMapper;

    /**
     * 查询知识点科目
     * 
     * @param subjectId 知识点科目主键
     * @return 知识点科目
     */
    @Override
    public KnowledgeSubject selectKnowledgeSubjectBySubjectId(Long subjectId)
    {
        return knowledgeSubjectMapper.selectKnowledgeSubjectBySubjectId(subjectId);
    }

    /**
     * 查询知识点科目列表
     * 
     * @param knowledgeSubject 知识点科目
     * @return 知识点科目
     */
    @Override
    public List<KnowledgeSubject> selectKnowledgeSubjectList(KnowledgeSubject knowledgeSubject)
    {
        return knowledgeSubjectMapper.selectKnowledgeSubjectList(knowledgeSubject);
    }

    /**
     * 新增知识点科目
     * 
     * @param knowledgeSubject 知识点科目
     * @return 结果
     */
    @Override
    public int insertKnowledgeSubject(KnowledgeSubject knowledgeSubject)
    {
        return knowledgeSubjectMapper.insertKnowledgeSubject(knowledgeSubject);
    }

    /**
     * 修改知识点科目
     * 
     * @param knowledgeSubject 知识点科目
     * @return 结果
     */
    @Override
    public int updateKnowledgeSubject(KnowledgeSubject knowledgeSubject)
    {
        return knowledgeSubjectMapper.updateKnowledgeSubject(knowledgeSubject);
    }

    /**
     * 批量删除知识点科目
     * 
     * @param subjectIds 需要删除的知识点科目主键
     * @return 结果
     */
    @Override
    public int deleteKnowledgeSubjectBySubjectIds(Long[] subjectIds)
    {
        return knowledgeSubjectMapper.deleteKnowledgeSubjectBySubjectIds(subjectIds);
    }

    /**
     * 删除知识点科目信息
     * 
     * @param subjectId 知识点科目主键
     * @return 结果
     */
    @Override
    public int deleteKnowledgeSubjectBySubjectId(Long subjectId)
    {
        return knowledgeSubjectMapper.deleteKnowledgeSubjectBySubjectId(subjectId);
    }
}
