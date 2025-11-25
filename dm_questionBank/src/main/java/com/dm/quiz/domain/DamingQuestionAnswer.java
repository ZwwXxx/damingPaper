package com.dm.quiz.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 题目回答情况表对象 daming_question_answer
 *
 * @author zww
 * @date 2024-10-14
 */
public class DamingQuestionAnswer extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 达卷id
     */
    private Long answerId;

    /**
     * 提交人
     */
    @Excel(name = "提交人")
    private String createUser;

    /**
     * 试卷id
     */
    @Excel(name = "试卷id")
    private Long paperId;

    /**
     * 试卷记录id
     */
    @Excel(name = "试卷记录id")
    private Long paperAnswerId;

    /**
     * 用户答案
     */
    @Excel(name = "用户答案")
    private String userAnswer;

    /**
     * $column.columnComment
     */
    @Excel(name = "题目类型")
    private Integer questionType;

    /**
     * 题目id
     */
    @Excel(name = "题目id")
    private Long questionId;

    /**
     * 科目id
     */
    @Excel(name = "科目id")
    private Integer subjectId;

    /**
     * 用户得分
     */
    @Excel(name = "用户得分")
    private Integer finalScore;

    /**
     * 题目分数
     */
    @Excel(name = "题目分数")
    private Integer questionScore;

    /**
     * 是否正确
     */
    @Excel(name = "是否正确")
    private Boolean isCorrect;

    /**
     * 题目顺序
     */
    @Excel(name = "题目顺序")
    private Integer itemOrder;
    /**
     * 批改状态 0-无需 1-待批改 2-已批改
     */
    private Integer reviewStatus;
    /**
     * 批改备注
     */
    private String reviewComment;
    /**
     * 查询开始时间
     */
    private String beginTime;
    /**
     * 查询结束时间
     */
    private String endTime;
    /**
     * 试卷名称(查询/展示)
     */
    private String paperName;

    public Boolean getCorrect() {
        return isCorrect;
    }

    public void setCorrect(Boolean correct) {
        isCorrect = correct;
    }



    public Integer getQuestionType() {
        return questionType;
    }

    public void setQuestionType(Integer questionType) {
        this.questionType = questionType;
    }

    public void setAnswerId(Long answerId) {
        this.answerId = answerId;
    }

    public Long getAnswerId() {
        return answerId;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setPaperId(Long paperId) {
        this.paperId = paperId;
    }

    public Long getPaperId() {
        return paperId;
    }

    public void setPaperAnswerId(Long paperAnswerId) {
        this.paperAnswerId = paperAnswerId;
    }

    public Long getPaperAnswerId() {
        return paperAnswerId;
    }

    public void setUserAnswer(String userAnswer) {
        this.userAnswer = userAnswer;
    }

    public String getUserAnswer() {
        return userAnswer;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setSubjectId(Integer subjectId) {
        this.subjectId = subjectId;
    }

    public Integer getSubjectId() {
        return subjectId;
    }

    public void setFinalScore(Integer finalScore) {
        this.finalScore = finalScore;
    }

    public Integer getFinalScore() {
        return finalScore;
    }

    public void setQuestionScore(Integer questionScore) {
        this.questionScore = questionScore;
    }

    public Integer getQuestionScore() {
        return questionScore;
    }

    public void setIsCorrect(Boolean isCorrect) {
        this.isCorrect = isCorrect;
    }

    public Boolean getIsCorrect() {
        return isCorrect;
    }

    public void setItemOrder(Integer itemOrder) {
        this.itemOrder = itemOrder;
    }

    public Integer getItemOrder() {
        return itemOrder;
    }

    public Integer getReviewStatus() {
        return reviewStatus;
    }

    public void setReviewStatus(Integer reviewStatus) {
        this.reviewStatus = reviewStatus;
    }

    public String getReviewComment() {
        return reviewComment;
    }

    public void setReviewComment(String reviewComment) {
        this.reviewComment = reviewComment;
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

    public String getPaperName() {
        return paperName;
    }

    public void setPaperName(String paperName) {
        this.paperName = paperName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("answerId", getAnswerId())
                .append("createUser", getCreateUser())
                .append("paperId", getPaperId())
                .append("paperAnswerId", getPaperAnswerId())
                .append("userAnswer", getUserAnswer())
                .append("createTime", getCreateTime())
                .append("questionId", getQuestionId())
                .append("subjectId", getSubjectId())
                .append("finalScore", getFinalScore())
                .append("questionScore", getQuestionScore())
                .append("isCorrect", getIsCorrect())
                .append("itemOrder", getItemOrder())
                .append("reviewStatus", getReviewStatus())
                .append("reviewComment", getReviewComment())
                .toString();
    }
}
