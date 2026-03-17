package com.ruoyi.system.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * 知识点附件分组统计（按知识点聚合）
 */
public class KnowledgeAttachmentGroupDTO {
    private Long pointId;
    private String title;
    private Long subjectId;
    private Long attachmentCount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastCreateTime;

    public Long getPointId() {
        return pointId;
    }

    public void setPointId(Long pointId) {
        this.pointId = pointId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public Long getAttachmentCount() {
        return attachmentCount;
    }

    public void setAttachmentCount(Long attachmentCount) {
        this.attachmentCount = attachmentCount;
    }

    public Date getLastCreateTime() {
        return lastCreateTime;
    }

    public void setLastCreateTime(Date lastCreateTime) {
        this.lastCreateTime = lastCreateTime;
    }
}

