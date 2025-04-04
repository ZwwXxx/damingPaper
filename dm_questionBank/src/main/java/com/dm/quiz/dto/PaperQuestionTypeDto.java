package com.dm.quiz.dto;

import java.util.List;

/**
 * 题型对应题目表
 */
public class PaperQuestionTypeDto {
    /**
     * 题型名字
     */
    private String name;
    /**
     * 题型下的所有题目
     */
    private List<QuestionDto> questionDtos;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<QuestionDto> getQuestionDtos() {
        return questionDtos;
    }

    public void setQuestionDtos(List<QuestionDto> questionDtos) {
        this.questionDtos = questionDtos;
    }
}
