package com.dm.quiz.viewmodel;


// @Data
public class QuestionOptionVM {
    // 选项名
    private String prefix;
    // 选项内容
    private String content;
    // 选项分值（多选题）
    private Integer score;

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }
}
