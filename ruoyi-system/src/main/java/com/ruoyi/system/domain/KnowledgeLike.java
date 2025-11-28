package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 知识点点赞对象 knowledge_like
 * 
 * @author ruoyi
 * @date 2025-11-28
 */
public class KnowledgeLike
{
    private static final long serialVersionUID = 1L;

    /** 点赞ID */
    private Long likeId;

    /** 用户ID */
    private Long userId;

    /** 知识点ID */
    private Long pointId;

    /** 点赞时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    public void setLikeId(Long likeId) 
    {
        this.likeId = likeId;
    }

    public Long getLikeId() 
    {
        return likeId;
    }

    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }

    public void setPointId(Long pointId) 
    {
        this.pointId = pointId;
    }

    public Long getPointId() 
    {
        return pointId;
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
            .append("likeId", getLikeId())
            .append("userId", getUserId())
            .append("pointId", getPointId())
            .append("createTime", getCreateTime())
            .toString();
    }
}
