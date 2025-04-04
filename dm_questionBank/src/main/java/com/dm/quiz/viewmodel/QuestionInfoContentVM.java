package com.dm.quiz.viewmodel;

import java.util.List;

public class QuestionInfoContentVM {
    private String analysis;
    private List<QuestionOptionVM> questionOptionVMList;
    private String correct;
    private String questionTitle;

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
}
