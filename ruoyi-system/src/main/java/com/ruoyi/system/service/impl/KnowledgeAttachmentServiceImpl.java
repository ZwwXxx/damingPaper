package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.KnowledgeAttachment;
import com.ruoyi.system.domain.dto.KnowledgeAttachmentGroupDTO;
import com.ruoyi.system.mapper.KnowledgeAttachmentMapper;
import com.ruoyi.system.service.IKnowledgeAttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KnowledgeAttachmentServiceImpl implements IKnowledgeAttachmentService {

    @Autowired
    private KnowledgeAttachmentMapper knowledgeAttachmentMapper;

    @Override
    public KnowledgeAttachment selectKnowledgeAttachmentById(Long attachmentId) {
        return knowledgeAttachmentMapper.selectKnowledgeAttachmentById(attachmentId);
    }

    @Override
    public List<KnowledgeAttachment> selectKnowledgeAttachmentByPointId(Long pointId) {
        return knowledgeAttachmentMapper.selectKnowledgeAttachmentByPointId(pointId);
    }

    @Override
    public List<KnowledgeAttachment> selectKnowledgeAttachmentList(KnowledgeAttachment attachment) {
        return knowledgeAttachmentMapper.selectKnowledgeAttachmentList(attachment);
    }

    @Override
    public List<KnowledgeAttachmentGroupDTO> selectAttachmentGroupList(String title, Long subjectId) {
        return knowledgeAttachmentMapper.selectAttachmentGroupList(title, subjectId);
    }

    @Override
    public List<KnowledgeAttachment> selectKnowledgeAttachmentByHash(String fileHash) {
        return knowledgeAttachmentMapper.selectKnowledgeAttachmentByHash(fileHash);
    }

    @Override
    public KnowledgeAttachment selectOneByPointIdAndFileName(Long pointId, String fileName) {
        return knowledgeAttachmentMapper.selectOneByPointIdAndFileName(pointId, fileName);
    }

    @Override
    public int insertKnowledgeAttachment(KnowledgeAttachment attachment) {
        return knowledgeAttachmentMapper.insertKnowledgeAttachment(attachment);
    }

    @Override
    public int updateKnowledgeAttachment(KnowledgeAttachment attachment) {
        return knowledgeAttachmentMapper.updateKnowledgeAttachment(attachment);
    }

    @Override
    public int deleteKnowledgeAttachmentById(Long attachmentId) {
        return knowledgeAttachmentMapper.deleteKnowledgeAttachmentById(attachmentId);
    }

    @Override
    public int deleteKnowledgeAttachmentByIds(Long[] attachmentIds) {
        return knowledgeAttachmentMapper.deleteKnowledgeAttachmentByIds(attachmentIds);
    }
}

