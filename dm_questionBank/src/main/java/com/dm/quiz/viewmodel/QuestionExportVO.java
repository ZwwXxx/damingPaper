package com.dm.quiz.viewmodel;

import com.ruoyi.common.annotation.Excel;

/**
 * 题目导入导出结构
 */
public class QuestionExportVO {

    @Excel(name = "题目ID")
    private Long id;

    @Excel(name = "科目ID")
    private Integer subjectId;

    @Excel(name = "题目类型", readConverterExp = "1=单选题,2=多选题")
    private Integer questionType;

    @Excel(name = "题干")
    private String questionTitle;

    @Excel(name = "选项(JSON数组)")
    private String optionsJson;

    @Excel(name = "正确答案(多选用英文逗号分隔)")
    private String correct;

    @Excel(name = "解析")
    private String analysis;

    @Excel(name = "分数")
    private Integer score;

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

    public String getQuestionTitle() {
        return questionTitle;
    }

    public void setQuestionTitle(String questionTitle) {
        this.questionTitle = questionTitle;
    }

    public String getOptionsJson() {
        return optionsJson;
    }

    public void setOptionsJson(String optionsJson) {
        this.optionsJson = optionsJson;
    }

    public String getCorrect() {
        return correct;
    }

    public void setCorrect(String correct) {
        this.correct = correct;
    }

    public String getAnalysis() {
        return analysis;
    }

    public void setAnalysis(String analysis) {
        this.analysis = analysis;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }
}

