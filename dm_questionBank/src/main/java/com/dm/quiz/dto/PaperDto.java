package com.dm.quiz.dto;


import java.util.List;

/**
 * 前端传来的，后续也拿这个格式给前端
 */
public class PaperDto {
    private Long paperId;
    private Integer subjectId;
    private String paperName;
    private List<PaperQuestionTypeDto> paperQuestionTypeDto;
    private Integer score;
    private Integer suggestTime;
    private Integer paperType;
    private Boolean enableAntiCheat;
    private Integer questionCount;

    public Integer getPaperType() {
        return paperType;
    }

    public void setPaperType(Integer paperType) {
        this.paperType = paperType;
    }

    public Boolean getEnableAntiCheat() {
        return enableAntiCheat;
    }

    public void setEnableAntiCheat(Boolean enableAntiCheat) {
        this.enableAntiCheat = enableAntiCheat;
    }

    public Integer getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(Integer questionCount) {
        this.questionCount = questionCount;
    }

    public Integer getSuggestTime() {
        return suggestTime;
    }

    public void setSuggestTime(Integer suggestTime) {
        this.suggestTime = suggestTime;
    }

    public Long getPaperId() {
        return paperId;
    }

    public void setPaperId(Long paperId) {
        this.paperId = paperId;
    }

    public void setPaperQuestionTypeDto(List<PaperQuestionTypeDto> paperQuestionTypeDto) {
        this.paperQuestionTypeDto = paperQuestionTypeDto;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
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

    public List<PaperQuestionTypeDto> getPaperQuestionTypeDto() {
        return paperQuestionTypeDto;
    }

    public void setPaperQuestionTypeVMS(List<PaperQuestionTypeDto> paperQuestionTypeDto) {
        this.paperQuestionTypeDto = paperQuestionTypeDto;
    }
}
