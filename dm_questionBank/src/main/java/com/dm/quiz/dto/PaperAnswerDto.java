package com.dm.quiz.dto;

import java.util.List;

public class PaperAnswerDto {
    // 题目回答列表
    private List<QuestionAnswerDto> questionAnswerDtos;
    // 试卷id，根据该试卷id找到 试卷答案
    private Long paperId;
    // 耗时,记录试卷回答记录
    private Integer doTime;

    // 试卷最终分数
    private Integer finalScore;

    public Integer getFinalScore() {
        return finalScore;
    }

    public void setFinalScore(Integer finalScore) {
        this.finalScore = finalScore;
    }

    public List<QuestionAnswerDto> getQuestionAnswerDtos() {
        return questionAnswerDtos;
    }

    public void setQuestionAnswerDtos(List<QuestionAnswerDto> questionAnswerDtos) {
        this.questionAnswerDtos = questionAnswerDtos;
    }

    public Long getPaperId() {
        return paperId;
    }

    public void setPaperId(Long paperId) {
        this.paperId = paperId;
    }

    public Integer getDoTime() {
        return doTime;
    }

    public void setDoTime(Integer doTime) {
        this.doTime = doTime;
    }
}
