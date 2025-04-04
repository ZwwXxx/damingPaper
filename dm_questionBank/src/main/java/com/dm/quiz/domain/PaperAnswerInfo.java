package com.dm.quiz.domain;

import java.util.List;

public class PaperAnswerInfo {
    private List<DamingQuestionAnswer> damingQuestionAnswerList;
    private DamingPaperAnswer damingPaperAnswer;



    public List<DamingQuestionAnswer> getDamingQuestionAnswerList() {
        return damingQuestionAnswerList;
    }

    public void setDamingQuestionAnswerList(List<DamingQuestionAnswer> damingQuestionAnswerList) {
        this.damingQuestionAnswerList = damingQuestionAnswerList;
    }

    public DamingPaperAnswer getDamingPaperAnswer() {
        return damingPaperAnswer;
    }

    public void setDamingPaperAnswer(DamingPaperAnswer damingPaperAnswer) {
        this.damingPaperAnswer = damingPaperAnswer;
    }
}
