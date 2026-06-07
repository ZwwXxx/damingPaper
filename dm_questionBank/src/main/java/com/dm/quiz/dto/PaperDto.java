package com.dm.quiz.dto;


import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.List;

/**
 * 前端传来的，后续也拿这个格式给前端
 */
public class PaperDto {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long paperId;
    private Integer subjectId;
    private String paperName;
    private List<PaperQuestionTypeDto> paperQuestionTypeDto;
    private Integer score;
    private Integer suggestTime;
    private Integer paperType;
    private Boolean enableAntiCheat;
    private Boolean enableAntiCheatCutScreen;
    private Boolean enableAntiCheatSingleQuestion;
    private Boolean enableAntiCheatDisableActions;
    private Boolean enableAntiCheatDevToolsDetection;
    private Boolean enableAntiCheatBrowserEnvironmentDetection;
    private Boolean enableAntiCheatShuffle;
    private Integer questionCount;
    private String startTime;
    private String endTime;
    /**
     * 题号规则：1=按题型分组编号，2=按加入顺序全局编号
     */
    private Integer numberMode;

    public Integer getNumberMode() {
        return numberMode;
    }

    public void setNumberMode(Integer numberMode) {
        this.numberMode = numberMode;
    }

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

    public Boolean getEnableAntiCheatCutScreen() {
        return enableAntiCheatCutScreen;
    }

    public void setEnableAntiCheatCutScreen(Boolean enableAntiCheatCutScreen) {
        this.enableAntiCheatCutScreen = enableAntiCheatCutScreen;
    }

    public Boolean getEnableAntiCheatSingleQuestion() {
        return enableAntiCheatSingleQuestion;
    }

    public void setEnableAntiCheatSingleQuestion(Boolean enableAntiCheatSingleQuestion) {
        this.enableAntiCheatSingleQuestion = enableAntiCheatSingleQuestion;
    }

    public Boolean getEnableAntiCheatDisableActions() {
        return enableAntiCheatDisableActions;
    }

    public void setEnableAntiCheatDisableActions(Boolean enableAntiCheatDisableActions) {
        this.enableAntiCheatDisableActions = enableAntiCheatDisableActions;
    }

    public Boolean getEnableAntiCheatDevToolsDetection() {
        return enableAntiCheatDevToolsDetection;
    }

    public void setEnableAntiCheatDevToolsDetection(Boolean enableAntiCheatDevToolsDetection) {
        this.enableAntiCheatDevToolsDetection = enableAntiCheatDevToolsDetection;
    }

    public Boolean getEnableAntiCheatBrowserEnvironmentDetection() {
        return enableAntiCheatBrowserEnvironmentDetection;
    }

    public void setEnableAntiCheatBrowserEnvironmentDetection(Boolean enableAntiCheatBrowserEnvironmentDetection) {
        this.enableAntiCheatBrowserEnvironmentDetection = enableAntiCheatBrowserEnvironmentDetection;
    }

    public Boolean getEnableAntiCheatShuffle() {
        return enableAntiCheatShuffle;
    }

    public void setEnableAntiCheatShuffle(Boolean enableAntiCheatShuffle) {
        this.enableAntiCheatShuffle = enableAntiCheatShuffle;
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

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
