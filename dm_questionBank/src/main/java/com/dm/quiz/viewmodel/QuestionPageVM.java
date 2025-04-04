package com.dm.quiz.viewmodel;

import com.dm.quiz.domain.DamingQuestion;

public class QuestionPageVM extends DamingQuestion {
    private String questionTitle;

    public String getQuestionTitle() {
        return questionTitle;
    }

    public void setQuestionTitle(String questionTitle) {
        this.questionTitle = questionTitle;
    }
}
