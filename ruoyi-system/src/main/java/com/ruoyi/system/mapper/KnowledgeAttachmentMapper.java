package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.KnowledgeAttachment;
import com.ruoyi.system.domain.dto.KnowledgeAttachmentGroupDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 知识点附件 Mapper
 */
public interface KnowledgeAttachmentMapper {

    KnowledgeAttachment selectKnowledgeAttachmentById(@Param("attachmentId") Long attachmentId);

    List<KnowledgeAttachment> selectKnowledgeAttachmentByPointId(@Param("pointId") Long pointId);

    List<KnowledgeAttachment> selectKnowledgeAttachmentList(KnowledgeAttachment attachment);

    List<KnowledgeAttachmentGroupDTO> selectAttachmentGroupList(@Param("title") String title,
                                                                @Param("subjectId") Long subjectId);

    List<KnowledgeAttachment> selectKnowledgeAttachmentByHash(@Param("fileHash") String fileHash);

    KnowledgeAttachment selectOneByPointIdAndFileName(@Param("pointId") Long pointId,
                                                      @Param("fileName") String fileName);

    int insertKnowledgeAttachment(KnowledgeAttachment attachment);

    int updateKnowledgeAttachment(KnowledgeAttachment attachment);

    int deleteKnowledgeAttachmentById(@Param("attachmentId") Long attachmentId);

    int deleteKnowledgeAttachmentByIds(@Param("attachmentIds") Long[] attachmentIds);
}

