package com.dm.quiz.dto;

import java.util.List;

/**
 * 自动组卷请求参数
 */
public class AutoAssemblePaperRequest {
    /**
     * 试卷名称
     */
    private String paperName;
    /**
     * 科目
     */
    private Integer subjectId;
    /**
     * 试卷类型
     */
    private Integer paperType;
    /**
     * 建议考试时长
     */
    private Integer suggestTime;
    /**
     * 是否开启防作弊
     */
    private Boolean enableAntiCheat;
    /**
     * 规则列表（按题型+数量自动组卷时使用）
     */
    private List<AutoAssembleRuleDto> rules;
    /**
     * 按日期自动组卷 - 开始日期（yyyy-MM-dd）
     */
    private String beginTime;
    /**
     * 按日期自动组卷 - 结束日期（yyyy-MM-dd）
     */
    private String endTime;

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

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
