package com.dm.quiz.dto;


import com.ruoyi.common.core.domain.BaseEntity;

public class QuestionPageDto extends BaseEntity {
    private Long id;
    private Integer subjectId;
    private Integer questionType;
    // private String questionTitle;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Integer subjectId) {
        this.subjectId = subjectId;
    }

    public Integer getQuestionType() {
        return questionType;
    }

    public void setQuestionType(Integer questionType) {
        this.questionType = questionType;
    }
}
