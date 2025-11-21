package com.dm.quiz.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

public class PaperReviewSubmitRequest {
    @NotNull(message = "答卷ID不能为空")
    private Long paperAnswerId;

    @NotEmpty(message = "评分明细不能为空")
    private List<ReviewQuestionScoreDto> questionScores;

    private String reviewRemark;

    public Long getPaperAnswerId() {
        return paperAnswerId;
    }

    public void setPaperAnswerId(Long paperAnswerId) {
        this.paperAnswerId = paperAnswerId;
    }

    public List<ReviewQuestionScoreDto> getQuestionScores() {
        return questionScores;
    }

    public void setQuestionScores(List<ReviewQuestionScoreDto> questionScores) {
        this.questionScores = questionScores;
    }

    public String getReviewRemark() {
        return reviewRemark;
    }

    public void setReviewRemark(String reviewRemark) {
        this.reviewRemark = reviewRemark;
    }
}

