package com.dm.quiz.dto;

import java.util.List;

/**
 * 自动组卷请求参数
 */
public class AutoAssemblePaperRequest {
    private String paperName;
    private Integer subjectId;
    private Integer paperType;
    private Integer suggestTime;
    private Boolean enableAntiCheat;
    private List<AutoAssembleRuleDto> rules;

    public String getPaperName() {
        return paperName;
    }

    public void setPaperName(String paperName) {
        this.paperName = paperName;
    }

    public Integer getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Integer subjectId) {
        this.subjectId = subjectId;
    }

    public Integer getPaperType() {
        return paperType;
    }

    public void setPaperType(Integer paperType) {
        this.paperType = paperType;
    }

    public Integer getSuggestTime() {
        return suggestTime;
    }

    public void setSuggestTime(Integer suggestTime) {
        this.suggestTime = suggestTime;
    }

    public Boolean getEnableAntiCheat() {
        return enableAntiCheat;
    }

    public void setEnableAntiCheat(Boolean enableAntiCheat) {
        this.enableAntiCheat = enableAntiCheat;
    }

    public List<AutoAssembleRuleDto> getRules() {
        return rules;
    }

    public void setRules(List<AutoAssembleRuleDto> rules) {
        this.rules = rules;
    }
}
