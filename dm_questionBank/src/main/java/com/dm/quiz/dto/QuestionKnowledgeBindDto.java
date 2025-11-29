package com.dm.quiz.dto;

import java.util.List;

public class QuestionKnowledgeBindDto {
    private Long questionId;
    private List<Long> knowledgePointIds;
    private Integer relationType;
    private String reason;

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public List<Long> getKnowledgePointIds() {
        return knowledgePointIds;
    }

    public void setKnowledgePointIds(List<Long> knowledgePointIds) {
        this.knowledgePointIds = knowledgePointIds;
    }

    public Integer getRelationType() {
        return relationType;
    }

    public void setRelationType(Integer relationType) {
        this.relationType = relationType;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
