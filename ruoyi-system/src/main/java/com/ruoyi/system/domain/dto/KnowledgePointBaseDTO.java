package com.ruoyi.system.domain.dto;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;

/**
 * 知识点基础信息DTO - 列表查询专用，不包含大字段
 * 
 * @author ruoyi
 * @date 2025-11-29
 */
public class KnowledgePointBaseDTO 
{
    /** 知识点ID */
    private Long pointId;

    /** 科目ID */
    @Excel(name = "科目ID")
    private Long subjectId;

    /** 标题 */
    @Excel(name = "标题")
    private String title;

    /** 摘要 */
    @Excel(name = "摘要")
    private String summary;

    /** 难度等级 */
    @Excel(name = "难度等级")
    private Integer difficulty;

    /** 作者用户ID */
    @Excel(name = "作者用户ID")
    private Long authorId;

    /** 作者姓名 */
    @Excel(name = "作者姓名")
    private String authorName;

    /** 浏览次数 */
    @Excel(name = "浏览次数")
    private Integer viewCount;

    /** 点赞数 */
    @Excel(name = "点赞数")
    private Integer likeCount;

    /** 收藏数 */
    @Excel(name = "收藏数")
    private Integer collectCount;

    /** 评论数 */
    @Excel(name = "评论数")
    private Integer commentCount;

    /** 是否推荐 */
    @Excel(name = "是否推荐")
    private Integer isRecommend;

    /** 是否置顶 */
    @Excel(name = "是否置顶")
    private Integer isTop;

    /** 状态 */
    @Excel(name = "状态")
    private Integer status;

    /** 审核状态 */
    @Excel(name = "审核状态")
    private Integer auditStatus;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date createTime;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date updateTime;

    /** 发布时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "发布时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date publishTime;

    /** 科目名称 */
    private String subjectName;

    /** 用户是否已点赞 */
    private Boolean isLiked;

    /** 用户是否已收藏 */
    private Boolean isCollected;

    public void setPointId(Long pointId) 
    {
        this.pointId = pointId;
    }

    public Long getPointId() 
    {
        return pointId;
    }
    public void setSubjectId(Long subjectId) 
    {
        this.subjectId = subjectId;
    }

    public Long getSubjectId() 
    {
        return subjectId;
    }
    public void setTitle(String title) 
    {
        this.title = title;
    }

    public String getTitle() 
    {
        return title;
    }
    public void setSummary(String summary) 
    {
        this.summary = summary;
    }

    public String getSummary() 
    {
        return summary;
    }
    public void setDifficulty(Integer difficulty) 
    {
        this.difficulty = difficulty;
    }

    public Integer getDifficulty() 
    {
        return difficulty;
    }
    public void setAuthorId(Long authorId) 
    {
        this.authorId = authorId;
    }

    public Long getAuthorId() 
    {
        return authorId;
    }
    public void setAuthorName(String authorName) 
    {
        this.authorName = authorName;
    }

    public String getAuthorName() 
    {
        return authorName;
    }
    public void setViewCount(Integer viewCount) 
    {
        this.viewCount = viewCount;
    }

    public Integer getViewCount() 
    {
        return viewCount;
    }
    public void setLikeCount(Integer likeCount) 
    {
        this.likeCount = likeCount;
    }

    public Integer getLikeCount() 
    {
        return likeCount;
    }
    public void setCollectCount(Integer collectCount) 
    {
        this.collectCount = collectCount;
    }

    public Integer getCollectCount() 
    {
        return collectCount;
    }
    public void setCommentCount(Integer commentCount) 
    {
        this.commentCount = commentCount;
    }

    public Integer getCommentCount() 
    {
        return commentCount;
    }
    public void setIsRecommend(Integer isRecommend) 
    {
        this.isRecommend = isRecommend;
    }

    public Integer getIsRecommend() 
    {
        return isRecommend;
    }
    public void setIsTop(Integer isTop) 
    {
        this.isTop = isTop;
    }

    public Integer getIsTop() 
    {
        return isTop;
    }
    public void setStatus(Integer status) 
    {
        this.status = status;
    }

    public Integer getStatus() 
    {
        return status;
    }
    public void setAuditStatus(Integer auditStatus) 
    {
        this.auditStatus = auditStatus;
    }

    public Integer getAuditStatus() 
    {
        return auditStatus;
    }
    public void setCreateTime(Date createTime) 
    {
        this.createTime = createTime;
    }

    public Date getCreateTime() 
    {
        return createTime;
    }
    public void setUpdateTime(Date updateTime) 
    {
        this.updateTime = updateTime;
    }

    public Date getUpdateTime() 
    {
        return updateTime;
    }
    public void setPublishTime(Date publishTime) 
    {
        this.publishTime = publishTime;
    }

    public Date getPublishTime() 
    {
        return publishTime;
    }

    public String getSubjectName() 
    {
        return subjectName;
    }

    public void setSubjectName(String subjectName) 
    {
        this.subjectName = subjectName;
    }

    public Boolean getIsLiked() 
    {
        return isLiked;
    }

    public void setIsLiked(Boolean isLiked) 
    {
        this.isLiked = isLiked;
    }

    public Boolean getIsCollected() 
    {
        return isCollected;
    }

    public void setIsCollected(Boolean isCollected) 
    {
        this.isCollected = isCollected;
    }
}
