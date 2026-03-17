package com.ruoyi.system.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * 知识点附件对象 knowledge_attachment
 *
 * @author ruoyi
 */
public class KnowledgeAttachment extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 附件ID */
    private Long attachmentId;

    /** 知识点ID */
    private Long pointId;

    /** 文件名 */
    private String fileName;

    /** 文件URL（建议存OSS完整URL） */
    private String fileUrl;

    /** 文件类型（html/video/image/pdf等） */
    private String fileType;

    /**
     * 预览URL（后端动态生成，如 OSS 签名URL，覆盖 Content-Disposition 为 inline）
     * 非数据库字段。
     */
    private String previewUrl;

    /** 文件内容哈希（用于去重） */
    private String fileHash;

    /** 文件大小（字节） */
    private Long fileSize;

    /** 排序 */
    private Integer sortOrder;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    public Long getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(Long attachmentId) {
        this.attachmentId = attachmentId;
    }

    public Long getPointId() {
        return pointId;
    }

    public void setPointId(Long pointId) {
        this.pointId = pointId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileHash() {
        return fileHash;
    }

    public void setFileHash(String fileHash) {
        this.fileHash = fileHash;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    @Override
    public Date getCreateTime() {
        return createTime;
    }

    @Override
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("attachmentId", getAttachmentId())
                .append("pointId", getPointId())
                .append("fileName", getFileName())
                .append("fileUrl", getFileUrl())
                .append("previewUrl", getPreviewUrl())
                .append("fileType", getFileType())
                .append("fileSize", getFileSize())
                .append("fileHash", getFileHash())
                .append("sortOrder", getSortOrder())
                .append("createTime", getCreateTime())
                .toString();
    }
}

