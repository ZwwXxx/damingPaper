package com.ruoyi.system.domain.dto;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;

/**
 * 知识点列表DTO - 用于收藏列表等场景的精简数据传输
 * 
 * @author ruoyi
 * @date 2025-11-29
 */
public class KnowledgePointListDTO 
{
    /** 知识点ID */
    private Long pointId;

    /** 科目ID */
    private Long subjectId;

    /** 标题 */
    @Excel(name = "标题")
    private String title;

    /** 摘要 (限制200字) */
    @Excel(name = "摘要")
    private String summary;

    /** 难度等级 */
    @Excel(name = "难度等级")
    private Integer difficulty;

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

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /** 科目名称 */
    @Excel(name = "科目名称")
    private String subjectName;

    /** 用户交互状态 */
    private Boolean isLiked = false;
    private Boolean isCollected = false;

    // ==================== Getters and Setters ====================

    public Long getPointId() {
        return pointId;
    }

    public void setPointId(Long pointId) {
        this.pointId = pointId;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Integer getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Integer difficulty) {
        this.difficulty = difficulty;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public Integer getCollectCount() {
        return collectCount;
    }

    public void setCollectCount(Integer collectCount) {
        this.collectCount = collectCount;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public Boolean getIsLiked() {
        return isLiked;
    }

    public void setIsLiked(Boolean isLiked) {
        this.isLiked = isLiked;
    }

    public Boolean getIsCollected() {
        return isCollected;
    }

    public void setIsCollected(Boolean isCollected) {
        this.isCollected = isCollected;
    }

    @Override
    public String toString() {
        return "KnowledgePointListDTO{" +
                "pointId=" + pointId +
                ", title='" + title + '\'' +
                ", summary='" + summary + '\'' +
                ", difficulty=" + difficulty +
                ", authorName='" + authorName + '\'' +
                ", viewCount=" + viewCount +
                ", likeCount=" + likeCount +
                ", collectCount=" + collectCount +
                ", subjectName='" + subjectName + '\'' +
                '}';
    }
}
