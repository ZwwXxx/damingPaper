package com.dm.quiz.viewmodel;

import java.util.List;

public class PaperQuestionTypeVM {
    private String name;
    private List<PaperQuestionVM> paperQuestionVMS;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<PaperQuestionVM> getPaperQuestionVMS() {
        return paperQuestionVMS;
    }

    public void setPaperQuestionVMS(List<PaperQuestionVM> paperQuestionVMS) {
        this.paperQuestionVMS = paperQuestionVMS;
    }
}
