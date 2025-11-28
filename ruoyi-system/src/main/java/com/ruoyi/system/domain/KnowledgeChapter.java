package com.ruoyi.system.domain;

import java.util.List;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 知识点章节对象 knowledge_chapter
 * 
 * @author ruoyi
 * @date 2025-11-28
 */
public class KnowledgeChapter extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 章节ID */
    private Long chapterId;

    /** 所属科目ID */
    private Long subjectId;

    /** 父章节ID */
    private Long parentId;

    /** 章节名称 */
    private String chapterName;

    /** 章节编码 */
    private String chapterCode;

    /** 章节描述 */
    private String description;

    /** 排序 */
    private Integer sortOrder;

    /** 层级 */
    private Integer level;

    /** 状态（0-停用 1-启用） */
    private Integer status;

    /** 子章节列表（用于树形结构） */
    private List<KnowledgeChapter> children;

    public void setChapterId(Long chapterId) 
    {
        this.chapterId = chapterId;
    }

    public Long getChapterId() 
    {
        return chapterId;
    }

    public void setSubjectId(Long subjectId) 
    {
        this.subjectId = subjectId;
    }

    public Long getSubjectId() 
    {
        return subjectId;
    }

    public void setParentId(Long parentId) 
    {
        this.parentId = parentId;
    }

    public Long getParentId() 
    {
        return parentId;
    }

    public void setChapterName(String chapterName) 
    {
        this.chapterName = chapterName;
    }

    public String getChapterName() 
    {
        return chapterName;
    }

    public void setChapterCode(String chapterCode) 
    {
        this.chapterCode = chapterCode;
    }

    public String getChapterCode() 
    {
        return chapterCode;
    }

    public void setDescription(String description) 
    {
        this.description = description;
    }

    public String getDescription() 
    {
        return description;
    }

    public void setSortOrder(Integer sortOrder) 
    {
        this.sortOrder = sortOrder;
    }

    public Integer getSortOrder() 
    {
        return sortOrder;
    }

    public void setLevel(Integer level) 
    {
        this.level = level;
    }

    public Integer getLevel() 
    {
        return level;
    }

    public void setStatus(Integer status) 
    {
        this.status = status;
    }

    public Integer getStatus() 
    {
        return status;
    }

    public List<KnowledgeChapter> getChildren() 
    {
        return children;
    }

    public void setChildren(List<KnowledgeChapter> children) 
    {
        this.children = children;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("chapterId", getChapterId())
            .append("subjectId", getSubjectId())
            .append("parentId", getParentId())
            .append("chapterName", getChapterName())
            .append("chapterCode", getChapterCode())
            .append("description", getDescription())
            .append("sortOrder", getSortOrder())
            .append("level", getLevel())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
