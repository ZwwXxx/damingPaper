package com.ruoyi.system.domain;

import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 知识点评论对象 knowledge_comment
 * 
 * @author daming
 * @date 2025-11-28
 */
public class KnowledgeComment extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 评论ID */
    private Long commentId;

    /** 知识点ID */
    @Excel(name = "知识点ID")
    private Long pointId;

    /** 评论用户ID */
    @Excel(name = "评论用户ID")
    private Long userId;

    /** 用户名 */
    @Excel(name = "用户名")
    private String userName;

    /** 用户昵称 */
    @Excel(name = "用户昵称")
    private String nickName;

    /** 用户头像 */
    private String avatar;

    /** 评论内容 */
    @Excel(name = "评论内容")
    private String content;

    /** 父评论ID（0表示一级评论） */
    @Excel(name = "父评论ID")
    private Long parentId;

    /** 回复目标用户ID */
    private Long replyToUserId;

    /** 回复目标用户名 */
    private String replyToUserName;

    /** 回复目标用户昵称 */
    @Excel(name = "回复目标用户昵称")
    private String replyToNickName;

    /** 回复目标评论ID */
    @Excel(name = "回复目标评论ID")
    private Long replyToCommentId;

    /** 点赞数 */
    @Excel(name = "点赞数")
    private Long likeCount;

    /** 状态（0删除 1正常） */
    @Excel(name = "状态", readConverterExp = "0=删除,1=正常")
    private Integer status;

    /** 当前用户是否点赞（非数据库字段） */
    private Boolean hasLiked;

    /** 子评论列表（非数据库字段） */
    private List<KnowledgeComment> children;

    public void setCommentId(Long commentId) 
    {
        this.commentId = commentId;
    }

    public Long getCommentId() 
    {
        return commentId;
    }

    public void setPointId(Long pointId) 
    {
        this.pointId = pointId;
    }

    public Long getPointId() 
    {
        return pointId;
    }

    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }

    public void setUserName(String userName) 
    {
        this.userName = userName;
    }

    public String getUserName() 
    {
        return userName;
    }

    public void setNickName(String nickName) 
    {
        this.nickName = nickName;
    }

    public String getNickName() 
    {
        return nickName;
    }

    public void setAvatar(String avatar) 
    {
        this.avatar = avatar;
    }

    public String getAvatar() 
    {
        return avatar;
    }

    public void setContent(String content) 
    {
        this.content = content;
    }

    public String getContent() 
    {
        return content;
    }

    public void setParentId(Long parentId) 
    {
        this.parentId = parentId;
    }

    public Long getParentId() 
    {
        return parentId;
    }

    public void setReplyToUserId(Long replyToUserId) 
    {
        this.replyToUserId = replyToUserId;
    }

    public Long getReplyToUserId() 
    {
        return replyToUserId;
    }

    public void setReplyToUserName(String replyToUserName) 
    {
        this.replyToUserName = replyToUserName;
    }

    public String getReplyToUserName() 
    {
        return replyToUserName;
    }

    public void setReplyToNickName(String replyToNickName) 
    {
        this.replyToNickName = replyToNickName;
    }

    public String getReplyToNickName() 
    {
        return replyToNickName;
    }

    public void setReplyToCommentId(Long replyToCommentId) 
    {
        this.replyToCommentId = replyToCommentId;
    }

    public Long getReplyToCommentId() 
    {
        return replyToCommentId;
    }

    public void setLikeCount(Long likeCount) 
    {
        this.likeCount = likeCount;
    }

    public Long getLikeCount() 
    {
        return likeCount;
    }

    public void setStatus(Integer status) 
    {
        this.status = status;
    }

    public Integer getStatus() 
    {
        return status;
    }

    public Boolean getHasLiked() 
    {
        return hasLiked;
    }

    public void setHasLiked(Boolean hasLiked) 
    {
        this.hasLiked = hasLiked;
    }

    public List<KnowledgeComment> getChildren() 
    {
        return children;
    }

    public void setChildren(List<KnowledgeComment> children) 
    {
        this.children = children;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("commentId", getCommentId())
            .append("pointId", getPointId())
            .append("userId", getUserId())
            .append("userName", getUserName())
            .append("nickName", getNickName())
            .append("avatar", getAvatar())
            .append("content", getContent())
            .append("parentId", getParentId())
            .append("replyToUserId", getReplyToUserId())
            .append("replyToUserName", getReplyToUserName())
            .append("replyToNickName", getReplyToNickName())
            .append("replyToCommentId", getReplyToCommentId())
            .append("likeCount", getLikeCount())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
