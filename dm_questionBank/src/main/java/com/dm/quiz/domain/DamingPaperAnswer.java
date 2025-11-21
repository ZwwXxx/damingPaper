package com.dm.quiz.domain;

import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 试卷作答情况对象 daming_paper_answer
 *
 * @author zww
 * @date 2024-10-13
 */
public class DamingPaperAnswer extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 达卷记录ID */
    private Long paperAnswerId;

    /** 试卷名 */
    @Excel(name = "试卷名")
    private String paperName;

    /** 试卷ID */
    @Excel(name = "试卷ID")
    private Long paperId;

    /** 回答人 */
    @Excel(name = "回答人")
    private String createUser;

    /** 试卷总分 */
    @Excel(name = "试卷总分")
    private Integer paperScore;

    /** 用户得分 */
    @Excel(name = "用户得分")
    private Integer finalScore;

    /** 客观题得分 */
    private Integer objectiveScore;

    /** 主观题得分 */
    private Integer subjectiveScore;

    /** 批改状态 0-无需 1-待批改 2-已批改 */
    private Integer reviewStatus;

    /** 批改老师 */
    private String reviewUser;

    /** 批改时间 */
    private Date reviewTime;

    /** 批改备注 */
    private String reviewRemark;

    /** 题目数量 */
    @Excel(name = "题目数量")
    private Integer questionCount;

    /** 答对数量 */
    @Excel(name = "答对数量")
    private Integer correctCount;

    /** 所属科目 */
    @Excel(name = "所属科目")
    private Integer subjectId;
    /**
     * 耗时时间
     * @return
     */
    @Excel(name = "耗时时间")
    private Integer doTime;

    /** 查询开始时间 */
    private String beginTime;
    /** 查询结束时间 */
    private String endTime;

    public Integer getDoTime() {
        return doTime;
    }

    public void setDoTime(Integer doTime) {
        this.doTime = doTime;
    }

    public void setPaperAnswerId(Long paperAnswerId)
    {
        this.paperAnswerId = paperAnswerId;
    }

    public Long getPaperAnswerId()
    {
        return paperAnswerId;
    }
    public void setPaperName(String paperName)
    {
        this.paperName = paperName;
    }

    public String getPaperName()
    {
        return paperName;
    }
    public void setPaperId(Long paperId)
    {
        this.paperId = paperId;
    }

    public Long getPaperId()
    {
        return paperId;
    }
    public void setCreateUser(String createUser)
    {
        this.createUser = createUser;
    }

    public String getCreateUser()
    {
        return createUser;
    }
    public void setPaperScore(Integer paperScore)
    {
        this.paperScore = paperScore;
    }

    public Integer getPaperScore()
    {
        return paperScore;
    }
    public void setFinalScore(Integer finalScore)
    {
        this.finalScore = finalScore;
    }

    public Integer getFinalScore()
    {
        return finalScore;
    }
    public Integer getObjectiveScore() {
        return objectiveScore;
    }

    public void setObjectiveScore(Integer objectiveScore) {
        this.objectiveScore = objectiveScore;
    }

    public Integer getSubjectiveScore() {
        return subjectiveScore;
    }

    public void setSubjectiveScore(Integer subjectiveScore) {
        this.subjectiveScore = subjectiveScore;
    }

    public Integer getReviewStatus() {
        return reviewStatus;
    }

    public void setReviewStatus(Integer reviewStatus) {
        this.reviewStatus = reviewStatus;
    }

    public String getReviewUser() {
        return reviewUser;
    }

    public void setReviewUser(String reviewUser) {
        this.reviewUser = reviewUser;
    }

    public Date getReviewTime() {
        return reviewTime;
    }

    public void setReviewTime(Date reviewTime) {
        this.reviewTime = reviewTime;
    }

    public String getReviewRemark() {
        return reviewRemark;
    }

    public void setReviewRemark(String reviewRemark) {
        this.reviewRemark = reviewRemark;
    }
    public void setQuestionCount(Integer questionCount)
    {
        this.questionCount = questionCount;
    }

    public Integer getQuestionCount()
    {
        return questionCount;
    }
    public void setCorrectCount(Integer correctCount)
    {
        this.correctCount = correctCount;
    }

    public Integer getCorrectCount()
    {
        return correctCount;
    }
    public void setSubjectId(Integer subjectId)
    {
        this.subjectId = subjectId;
    }

    public Integer getSubjectId()
    {
        return subjectId;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("paperAnswerId", getPaperAnswerId())
                .append("paperName", getPaperName())
                .append("paperId", getPaperId())
                .append("createUser", getCreateUser())
                .append("paperScore", getPaperScore())
                .append("finalScore", getFinalScore())
                .append("objectiveScore", getObjectiveScore())
                .append("subjectiveScore", getSubjectiveScore())
                .append("reviewStatus", getReviewStatus())
                .append("reviewUser", getReviewUser())
                .append("reviewTime", getReviewTime())
                .append("reviewRemark", getReviewRemark())
                .append("questionCount", getQuestionCount())
                .append("correctCount", getCorrectCount())
                .append("createTime", getCreateTime())
                .append("subjectId", getSubjectId())
                .append("beginTime", getBeginTime())
                .append("endTime", getEndTime())
                .toString();
    }
}
