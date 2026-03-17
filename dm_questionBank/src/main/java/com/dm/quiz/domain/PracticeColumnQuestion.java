package com.dm.quiz.domain;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 专项刷题栏目-题目关联对象 daming_practice_column_question
 */
public class PracticeColumnQuestion extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long columnId;
    private Long questionId;
    private Integer sortOrder;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getColumnId() {
        return columnId;
    }

    public void setColumnId(Long columnId) {
        this.columnId = columnId;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
}

