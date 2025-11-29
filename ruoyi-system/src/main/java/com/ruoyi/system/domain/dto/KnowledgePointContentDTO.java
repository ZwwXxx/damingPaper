package com.ruoyi.system.domain.dto;

/**
 * 知识点内容详情DTO - 仅包含大字段内容，用于详情页内容展示
 * 
 * @author ruoyi
 * @date 2025-11-29
 */
public class KnowledgePointContentDTO 
{
    /** 知识点ID */
    private Long pointId;

    /** 知识点内容(Markdown格式) */
    private String content;

    /** 知识点内容(HTML渲染后) */
    private String contentHtml;

    /** 审核备注 */
    private String auditRemark;

    public Long getPointId() 
    {
        return pointId;
    }

    public void setPointId(Long pointId) 
    {
        this.pointId = pointId;
    }

    public String getContent() 
    {
        return content;
    }

    public void setContent(String content) 
    {
        this.content = content;
    }

    public String getContentHtml() 
    {
        return contentHtml;
    }

    public void setContentHtml(String contentHtml) 
    {
        this.contentHtml = contentHtml;
    }

    public String getAuditRemark() 
    {
        return auditRemark;
    }

    public void setAuditRemark(String auditRemark) 
    {
        this.auditRemark = auditRemark;
    }

    @Override
    public String toString() 
    {
        return "KnowledgePointContentDTO{" +
                "pointId=" + pointId +
                ", content='" + (content != null ? content.substring(0, Math.min(100, content.length())) + "..." : null) + '\'' +
                ", contentHtml='" + (contentHtml != null ? "[HTML Content]" : null) + '\'' +
                ", auditRemark='" + auditRemark + '\'' +
                '}';
    }
}
