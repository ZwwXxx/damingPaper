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
     * 防作弊-防止切页/切屏检测
     */
    private Boolean enableAntiCheatCutScreen;
    /**
     * 防作弊-单题展示模式
     */
    private Boolean enableAntiCheatSingleQuestion;
    /**
     * 防作弊-禁用右键/复制/快捷键
     */
    private Boolean enableAntiCheatDisableActions;
    /**
     * 防作弊-开发者工具检测
     */
    private Boolean enableAntiCheatDevToolsDetection;
    /**
     * 防作弊-浏览器环境检测（可疑扩展/自动化工具）
     */
    private Boolean enableAntiCheatBrowserEnvironmentDetection;
    /**
     * 防作弊-题目乱序展示
     */
    private Boolean enableAntiCheatShuffle;
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
