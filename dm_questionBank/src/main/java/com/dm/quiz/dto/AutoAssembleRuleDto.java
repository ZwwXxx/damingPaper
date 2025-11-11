package com.dm.quiz.dto;

/**
 * 自动组卷规则
 */
public class AutoAssembleRuleDto {
    /**
     * 分卷名称（若为空则使用题型名称）
     */
    private String sectionName;

    /**
     * 题型
     */
    private Integer questionType;

    /**
     * 需要的题目数量
     */
    private Integer questionCount;

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public Integer getQuestionType() {
        return questionType;
    }

    public void setQuestionType(Integer questionType) {
        this.questionType = questionType;
    }

    public Integer getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(Integer questionCount) {
        this.questionCount = questionCount;
    }
}
