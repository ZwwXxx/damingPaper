package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 知识点评论点赞对象 knowledge_comment_like
 * 
 * @author daming
 * @date 2025-11-28
 */
public class KnowledgeCommentLike extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 点赞ID */
    private Long likeId;

    /** 评论ID */
    private Long commentId;

    /** 用户ID */
    private Long userId;

    public void setLikeId(Long likeId) 
    {
        this.likeId = likeId;
    }

    public Long getLikeId() 
    {
        return likeId;
    }

    public void setCommentId(Long commentId) 
    {
        this.commentId = commentId;
    }

    public Long getCommentId() 
    {
        return commentId;
    }

    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("likeId", getLikeId())
            .append("commentId", getCommentId())
            .append("userId", getUserId())
            .append("createTime", getCreateTime())
            .toString();
    }
}
