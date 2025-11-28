package com.ruoyi.system.domain;

import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 知识点对象 knowledge_point
 * 
 * @author ruoyi
 * @date 2025-11-28
 */
public class KnowledgePoint extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 知识点ID */
    private Long pointId;

    /** 所属科目ID */
    private Long subjectId;

    /** 所属章节ID */
    private Long chapterId;

    /** 知识点标题 */
    private String title;

    /** 摘要 */
    private String summary;

    /** 知识点内容（Markdown格式） */
    private String content;

    /** HTML内容（前端渲染用） */
    private String contentHtml;

    /** 难度等级（1-简单 2-中等 3-困难） */
    private Integer difficulty;

    /** 重要程度（1-了解 2-理解 3-掌握 4-精通） */
    private Integer importance;

    /** 作者ID */
    private Long authorId;

    /** 作者名称 */
    private String authorName;

    /** 浏览次数 */
    private Integer viewCount;

    /** 点赞数 */
    private Integer likeCount;

    /** 收藏数 */
    private Integer collectCount;

    /** 评论数 */
    private Integer commentCount;

    /** 学习人数 */
    private Integer learnCount;

    /** 是否推荐（0-否 1-是） */
    private Integer isRecommend;

    /** 是否置顶（0-否 1-是） */
    private Integer isTop;

    /** 状态（0-草稿 1-已发布 2-审核中 3-已下架） */
    private Integer status;

    /** 审核状态（0-待审核 1-通过 2-拒绝） */
    private Integer auditStatus;

    /** 审核备注 */
    private String auditRemark;

    /** 版本号 */
    private Integer version;

    /** 发布时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date publishTime;

    // 扩展字段（不在数据库中，用于前端展示）
    
    /** 科目名称 */
    private String subjectName;

    /** 章节名称 */
    private String chapterName;

    /** 标签列表 */
    private List<KnowledgeTag> tags;

    /** 当前用户是否已点赞 */
    private Boolean isLiked;

    /** 当前用户是否已收藏 */
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

    public void setChapterId(Long chapterId) 
    {
        this.chapterId = chapterId;
    }

    public Long getChapterId() 
    {
        return chapterId;
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

    public void setContent(String content) 
    {
        this.content = content;
    }

    public String getContent() 
    {
        return content;
    }

    public void setContentHtml(String contentHtml) 
    {
        this.contentHtml = contentHtml;
    }

    public String getContentHtml() 
    {
        return contentHtml;
    }

    public void setDifficulty(Integer difficulty) 
    {
        this.difficulty = difficulty;
    }

    public Integer getDifficulty() 
    {
        return difficulty;
    }

    public void setImportance(Integer importance) 
    {
        this.importance = importance;
    }

    public Integer getImportance() 
    {
        return importance;
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

    public void setLearnCount(Integer learnCount) 
    {
        this.learnCount = learnCount;
    }

    public Integer getLearnCount() 
    {
        return learnCount;
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

    public void setAuditRemark(String auditRemark) 
    {
        this.auditRemark = auditRemark;
    }

    public String getAuditRemark() 
    {
        return auditRemark;
    }

    public void setVersion(Integer version) 
    {
        this.version = version;
    }

    public Integer getVersion() 
    {
        return version;
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

    public String getChapterName() 
    {
        return chapterName;
    }

    public void setChapterName(String chapterName) 
    {
        this.chapterName = chapterName;
    }

    public List<KnowledgeTag> getTags() 
    {
        return tags;
    }

    public void setTags(List<KnowledgeTag> tags) 
    {
        this.tags = tags;
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

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("pointId", getPointId())
            .append("subjectId", getSubjectId())
            .append("chapterId", getChapterId())
            .append("title", getTitle())
            .append("summary", getSummary())
            .append("content", getContent())
            .append("contentHtml", getContentHtml())
            .append("difficulty", getDifficulty())
            .append("importance", getImportance())
            .append("authorId", getAuthorId())
            .append("authorName", getAuthorName())
            .append("viewCount", getViewCount())
            .append("likeCount", getLikeCount())
            .append("collectCount", getCollectCount())
            .append("commentCount", getCommentCount())
            .append("learnCount", getLearnCount())
            .append("isRecommend", getIsRecommend())
            .append("isTop", getIsTop())
            .append("status", getStatus())
            .append("auditStatus", getAuditStatus())
            .append("auditRemark", getAuditRemark())
            .append("version", getVersion())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("publishTime", getPublishTime())
            .toString();
    }
}
