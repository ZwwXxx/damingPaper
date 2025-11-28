package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 知识点标签对象 knowledge_tag
 * 
 * @author ruoyi
 * @date 2025-11-28
 */
public class KnowledgeTag
{
    private static final long serialVersionUID = 1L;

    /** 标签ID */
    private Long tagId;

    /** 标签名称 */
    private String tagName;

    /** 标签颜色 */
    private String tagColor;

    /** 使用次数 */
    private Integer useCount;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    public void setTagId(Long tagId) 
    {
        this.tagId = tagId;
    }

    public Long getTagId() 
    {
        return tagId;
    }

    public void setTagName(String tagName) 
    {
        this.tagName = tagName;
    }

    public String getTagName() 
    {
        return tagName;
    }

    public void setTagColor(String tagColor) 
    {
        this.tagColor = tagColor;
    }

    public String getTagColor() 
    {
        return tagColor;
    }

    public void setUseCount(Integer useCount) 
    {
        this.useCount = useCount;
    }

    public Integer getUseCount() 
    {
        return useCount;
    }

    public void setCreateTime(Date createTime) 
    {
        this.createTime = createTime;
    }

    public Date getCreateTime() 
    {
        return createTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("tagId", getTagId())
            .append("tagName", getTagName())
            .append("tagColor", getTagColor())
            .append("useCount", getUseCount())
            .append("createTime", getCreateTime())
            .toString();
    }
}
