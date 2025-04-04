package com.dm.quiz.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;

import java.util.Date;

/**
 * 题目信息对象 daming_content_info
 *
 * @author zww
 * @date 2024-10-09
 */
public class DamingContentInfo
{
    private static final long serialVersionUID = 1L;

    /** 题目信息id */
    @Excel(name = "题目信息ID")
    private Long id;

    /** 题目信息内容 */
    @Excel(name = "题目信息内容")
    private String content;
    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "题目信息内容创建时间")
    private Date createTime;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setContent(String content)
    {
        this.content = content;
    }

    public String getContent()
    {
        return content;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("content", getContent())
            .append("createTime", getCreateTime())
            .toString();
    }
}
