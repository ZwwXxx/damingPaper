package com.dm.quiz.viewmodel;

import java.io.Serializable;
import java.util.List;

/**
 * 收藏题目视图
 */
public class FavoriteQuestionVO implements Serializable {

    private Long favoriteId;
    private Long questionId;
    private Long paperId;
    private Long paperAnswerId;
    private Integer subjectId;
    private String paperName;
    private String questionTitle;
    private Integer questionType;
    private List<QuestionOptionVM> options;
    private String correct;
    private String[] correctArray;
    private String createTime;

    public Long getFavoriteId() {
        return favoriteId;
    }

    public void setFavoriteId(Long favoriteId) {
        this.favoriteId = favoriteId;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Long getPaperId() {
        return paperId;
    }

    public void setPaperId(Long paperId) {
        this.paperId = paperId;
    }

    public Long getPaperAnswerId() {
        return paperAnswerId;
    }

    public void setPaperAnswerId(Long paperAnswerId) {
        this.paperAnswerId = paperAnswerId;
    }

    public Integer getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Integer subjectId) {
        this.subjectId = subjectId;
    }

    public String getPaperName() {
        return paperName;
    }

    public void setPaperName(String paperName) {
        this.paperName = paperName;
    }

    public String getQuestionTitle() {
        return questionTitle;
    }

    public void setQuestionTitle(String questionTitle) {
        this.questionTitle = questionTitle;
    }

    public Integer getQuestionType() {
        return questionType;
    }

    public void setQuestionType(Integer questionType) {
        this.questionType = questionType;
    }

    public List<QuestionOptionVM> getOptions() {
        return options;
    }

    public void setOptions(List<QuestionOptionVM> options) {
        this.options = options;
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

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}


