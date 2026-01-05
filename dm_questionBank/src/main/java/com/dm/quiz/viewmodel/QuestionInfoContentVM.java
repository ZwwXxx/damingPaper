package com.dm.quiz.viewmodel;

import java.util.List;

public class QuestionInfoContentVM {
    private String analysis;
    private List<QuestionOptionVM> questionOptionVMList;
    private String correct;
    private String questionTitle;
    // 填空题答案是否要求按顺序（true=要求按顺序，false=不要求按顺序，默认false）
    private Boolean requireOrder;
    // 解析内容格式（html=富文本，markdown=Markdown格式，默认html）
    private String analysisFormat;
    // 题干内容格式（html=富文本，markdown=Markdown格式，默认html）
    private String questionTitleFormat;
    // 选项内容格式（html=富文本，markdown=Markdown格式，默认html）
    private String optionFormat;
    // 标准答案内容格式（html=富文本，markdown=Markdown格式，默认html，仅主观题）
    private String correctFormat;

    public String getAnalysis() {
        return analysis;
    }

    public void setAnalysis(String analysis) {
        this.analysis = analysis;
    }

    public List<QuestionOptionVM> getQuestionOptionList() {
        return questionOptionVMList;
    }

    public void setQuestionOptionList(List<QuestionOptionVM> questionOptionVMList) {
        this.questionOptionVMList = questionOptionVMList;
    }

    public String getCorrect() {
        return correct;
    }

    public void setCorrect(String correct) {
        this.correct = correct;
    }

    public String getQuestionTitle() {
        return questionTitle;
    }

    public void setQuestionTitle(String questionTitle) {
        this.questionTitle = questionTitle;
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
