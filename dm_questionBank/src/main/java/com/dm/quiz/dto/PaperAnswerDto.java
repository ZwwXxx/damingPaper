package com.dm.quiz.dto;

import java.util.List;

public class PaperAnswerDto {
    // 题目回答列表
    private List<QuestionAnswerDto> questionAnswerDtos;
    // 试卷id，根据该试卷id找到 试卷答案
    private Long paperId;
    // 答卷记录ID
    private Long paperAnswerId;
    // 耗时,记录试卷回答记录
    private Integer doTime;

    // 试卷最终分数
    private Integer finalScore;
    // 客观题得分
    private Integer objectiveScore;
    // 主观题得分
    private Integer subjectiveScore;
    // 批改状态
    private Integer reviewStatus;
    // 答题人
    private String createUser;
    // 批注
    private String reviewRemark;

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getReviewRemark() {
        return reviewRemark;
    }

    public void setReviewRemark(String reviewRemark) {
        this.reviewRemark = reviewRemark;
    }

    public Integer getReviewStatus() {
        return reviewStatus;
    }

    public void setReviewStatus(Integer reviewStatus) {
        this.reviewStatus = reviewStatus;
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

    public Integer getFinalScore() {
        return finalScore;
    }

    public void setFinalScore(Integer finalScore) {
        this.finalScore = finalScore;
    }

    public List<QuestionAnswerDto> getQuestionAnswerDtos() {
        return questionAnswerDtos;
    }

    public void setQuestionAnswerDtos(List<QuestionAnswerDto> questionAnswerDtos) {
        this.questionAnswerDtos = questionAnswerDtos;
    }

    public Long getPaperId() {
        return paperId;
    }

    public void setPaperId(Long paperId) {
        this.paperId = paperId;
    }

    public Long getPaperAnswerId() {
        return paperAnswerId;
    }

    public void setPaperAnswerId(Long paperAnswerId) {
        this.paperAnswerId = paperAnswerId;
    }

    public Integer getDoTime() {
        return doTime;
    }

    public void setDoTime(Integer doTime) {
        this.doTime = doTime;
    }
}
