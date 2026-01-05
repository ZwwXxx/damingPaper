package com.dm.quiz.dto;

import com.dm.quiz.viewmodel.QuestionOptionVM;

import java.util.List;

public class QuestionDto {
    private Long id;
    // 选项组
    private List<QuestionOptionVM> items;
    // 解析
    private String analysis;
    // 标准答案
    private String correct;
    // 多选题答案列表
    private String[] correctArray;
    // 该题总分（多选就几个选项加一起）
    private Integer score;
    // 题目
    private String questionTitle;
    //所属科目
    private Integer subjectId;
    // 题目类型，1单选，2多选，3主观
    private Integer questionType;
    // 题目序号表示第几题
    private Integer itemOrder;
    // 填空题答案是否要求按顺序（true=要求按顺序，false=不要求按顺序）
    private Boolean requireOrder;
    // 解析内容格式（html=富文本，markdown=Markdown格式，默认html）
    private String analysisFormat;
    // 题干内容格式（html=富文本，markdown=Markdown格式，默认html）
    private String questionTitleFormat;
    // 选项内容格式（html=富文本，markdown=Markdown格式，默认html）
    private String optionFormat;
    // 标准答案内容格式（html=富文本，markdown=Markdown格式，默认html，仅主观题）
    private String correctFormat;

    public Integer getItemOrder() {
        return itemOrder;
    }

    public void setItemOrder(Integer itemOrder) {
        this.itemOrder = itemOrder;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<QuestionOptionVM> getItems() {
        return items;
    }

    public void setItems(List<QuestionOptionVM> items) {
        this.items = items;
    }

    public String getAnalysis() {
        return analysis;
    }

    public void setAnalysis(String analysis) {
        this.analysis = analysis;
    }

    public String getCorrect() {
        return correct;
    }

    public void setCorrect(String correct) {
        this.correct = correct;
    }

    public String[] getCorrectArray() {
        return correctArray;
    }

    public void setCorrectArray(String[] correctArray) {
        this.correctArray = correctArray;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getQuestionTitle() {
        return questionTitle;
    }

    public void setQuestionTitle(String questionTitle) {
        this.questionTitle = questionTitle;
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

    public Boolean getRequireOrder() {
        return requireOrder;
    }

    public void setRequireOrder(Boolean requireOrder) {
        this.requireOrder = requireOrder;
    }

    public String getAnalysisFormat() {
        return analysisFormat;
    }

    public void setAnalysisFormat(String analysisFormat) {
        this.analysisFormat = analysisFormat;
    }

    public String getQuestionTitleFormat() {
        return questionTitleFormat;
    }

    public void setQuestionTitleFormat(String questionTitleFormat) {
        this.questionTitleFormat = questionTitleFormat;
    }

    public String getOptionFormat() {
        return optionFormat;
    }

    public void setOptionFormat(String optionFormat) {
        this.optionFormat = optionFormat;
    }

    public String getCorrectFormat() {
        return correctFormat;
    }

    public void setCorrectFormat(String correctFormat) {
        this.correctFormat = correctFormat;
    }
}
