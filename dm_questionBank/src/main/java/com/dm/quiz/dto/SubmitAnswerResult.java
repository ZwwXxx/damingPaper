package com.dm.quiz.dto;

import java.io.Serializable;

public class SubmitAnswerResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer finalScore;
    private Integer objectiveScore;
    private Integer reviewStatus;

    public Integer getFinalScore() {
        return finalScore;
    }

    public void setFinalScore(Integer finalScore) {
        this.finalScore = finalScore;
    }

    public Integer getObjectiveScore() {
        return objectiveScore;
    }

    public void setObjectiveScore(Integer objectiveScore) {
        this.objectiveScore = objectiveScore;
    }

    public Integer getReviewStatus() {
        return reviewStatus;
    }

    public void setReviewStatus(Integer reviewStatus) {
        this.reviewStatus = reviewStatus;
    }
}

