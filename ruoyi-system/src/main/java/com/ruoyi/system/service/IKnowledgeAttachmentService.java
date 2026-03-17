package com.ruoyi.system.service;

import com.ruoyi.system.domain.KnowledgeAttachment;
import com.ruoyi.system.domain.dto.KnowledgeAttachmentGroupDTO;

import java.util.List;

public interface IKnowledgeAttachmentService {

    KnowledgeAttachment selectKnowledgeAttachmentById(Long attachmentId);

    List<KnowledgeAttachment> selectKnowledgeAttachmentByPointId(Long pointId);

    List<KnowledgeAttachment> selectKnowledgeAttachmentList(KnowledgeAttachment attachment);

    List<KnowledgeAttachmentGroupDTO> selectAttachmentGroupList(String title, Long subjectId);

    List<KnowledgeAttachment> selectKnowledgeAttachmentByHash(String fileHash);

    KnowledgeAttachment selectOneByPointIdAndFileName(Long pointId, String fileName);

    int insertKnowledgeAttachment(KnowledgeAttachment attachment);

    int updateKnowledgeAttachment(KnowledgeAttachment attachment);

    int deleteKnowledgeAttachmentById(Long attachmentId);

    int deleteKnowledgeAttachmentByIds(Long[] attachmentIds);
}

