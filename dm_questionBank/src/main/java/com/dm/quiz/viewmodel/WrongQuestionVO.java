package com.dm.quiz.viewmodel;

import java.io.Serializable;
import java.util.List;
import com.ruoyi.system.domain.KnowledgePoint;

/**
 * 学生错题记录视图
 */
public class WrongQuestionVO implements Serializable {

    private Long answerId;
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
    private String userAnswer;
    private Integer questionScore;
    private Integer finalScore;
    private String createTime;
    private Integer wrongCount;
    /**
     * 题目关联的知识点（用于错题本跳转复习）
     */
    private List<KnowledgePoint> knowledgePoints;
    /**
     * 最后一次错误记录的 ID，用于跳转解析
     */
    private Long latestAnswerId;

    public Long getAnswerId() {
        return answerId;
    }

    public void setAnswerId(Long answerId) {
        this.answerId = answerId;
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

    public String getUserAnswer() {
        return userAnswer;
    }

    public void setUserAnswer(String userAnswer) {
        this.userAnswer = userAnswer;
    }

    public Integer getQuestionScore() {
        return questionScore;
    }

    public void setQuestionScore(Integer questionScore) {
        this.questionScore = questionScore;
    }

    public Integer getFinalScore() {
        return finalScore;
    }

    public void setFinalScore(Integer finalScore) {
        this.finalScore = finalScore;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Integer getWrongCount() {
        return wrongCount;
    }

    public void setWrongCount(Integer wrongCount) {
        this.wrongCount = wrongCount;
    }

    public List<KnowledgePoint> getKnowledgePoints() {
        return knowledgePoints;
    }

    public void setKnowledgePoints(List<KnowledgePoint> knowledgePoints) {
        this.knowledgePoints = knowledgePoints;
    }

    public Long getLatestAnswerId() {
        return latestAnswerId;
    }

    public void setLatestAnswerId(Long latestAnswerId) {
        this.latestAnswerId = latestAnswerId;
    }
}

