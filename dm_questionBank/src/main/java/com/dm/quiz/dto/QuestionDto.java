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
}
