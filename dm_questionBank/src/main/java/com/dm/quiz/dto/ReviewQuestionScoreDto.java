package com.dm.quiz.dto;

import javax.validation.constraints.NotNull;

public class ReviewQuestionScoreDto {
    @NotNull(message = "答题记录ID不能为空")
    private Long answerId;

    @NotNull(message = "得分不能为空")
    private Integer score;

    private String comment;

    public Long getAnswerId() {
        return answerId;
    }

    public void setAnswerId(Long answerId) {
        this.answerId = answerId;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}

