package com.dm.quiz.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

import java.util.Date;

/**
 * 试卷对象 daming_paper
 *
 * @author zww
 * @date 2024-10-10
 */
public class DamingPaper extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 试卷主键（JSON 输出为字符串，避免前端 Number 精度丢失） */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long paperId;

    /**  */
    @Excel(name = "试卷名")
    private String paperName;

    /**  */
    @Excel(name = "科目id")
    private Integer subjectId;

    /**  */
    @Excel(name = "总分值")
    private Integer score;

    @Excel(name = "考试时长")
    private Integer suggestTime;

    /**  */
    @Excel(name = "题目总数")
    private Integer questionCount;

    /**  */
    @Excel(name = "是否删除，0未删除，2已删除")
    private Integer delFlag;

    /**
     * 试卷内容id
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long paperInfoId;

    /** $column.columnComment */
    @Excel(name = "试卷类型")
    private Integer paperType;

    /** 是否开启防作弊 */
    private Boolean enableAntiCheat;

    /** 是否开启防止切页/切屏检测 */
    private Boolean enableAntiCheatCutScreen;

    /** 是否开启单题展示模式（上一题/下一题） */
    private Boolean enableAntiCheatSingleQuestion;

    /** 是否开启禁用右键/复制/快捷键等操作 */
    private Boolean enableAntiCheatDisableActions;

    /** 是否开启开发者工具检测 */
    private Boolean enableAntiCheatDevToolsDetection;

    /** 是否开启浏览器环境检测（自动化/可疑扩展） */
    private Boolean enableAntiCheatBrowserEnvironmentDetection;

    /** 是否开启题目乱序展示（防止顺序作弊） */
    private Boolean enableAntiCheatShuffle;

    /** 考试开始时间 */
    @Excel(name = "开始时间", dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    /** 考试截止时间 */
    @Excel(name = "截止时间", dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    /**
     * 题号规则：1=按题型分组编号，2=按加入顺序全局编号（默认2）
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

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getSuggestTime() {
        return suggestTime;
    }

    public void setSuggestTime(Integer suggestTime) {
        this.suggestTime = suggestTime;
    }

    public Long getPaperInfoId() {
        return paperInfoId;
    }

    public void setPaperInfoId(Long paperInfoId) {
        this.paperInfoId = paperInfoId;
    }

    public void setPaperId(Long paperId)
    {
        this.paperId = paperId;
    }

    public Long getPaperId()
    {
        return paperId;
    }
    public void setPaperName(String paperName)
    {
        this.paperName = paperName;
    }

    public String getPaperName()
    {
        return paperName;
    }
    public void setSubjectId(Integer subjectId)
    {
        this.subjectId = subjectId;
    }

    public Integer getSubjectId()
    {
        return subjectId;
    }
    public void setScore(Integer score)
    {
        this.score = score;
    }

    public Integer getScore()
    {
        return score;
    }
    public void setQuestionCount(Integer questionCount)
    {
        this.questionCount = questionCount;
    }

    public Integer getQuestionCount()
    {
        return questionCount;
    }
    public void setDelFlag(Integer delFlag)
    {
        this.delFlag = delFlag;
    }

    public Integer getDelFlag()
    {
        return delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("paperId", getPaperId())
                .append("paperName", getPaperName())
                .append("subjectId", getSubjectId())
                .append("score", getScore())
                .append("questionCount", getQuestionCount())
                .append("delFlag", getDelFlag())
                .append("createTime", getCreateTime())
                .append("updateTime", getUpdateTime())
                .append("paperType", getPaperType())
                .append("enableAntiCheat", getEnableAntiCheat())
                .append("enableAntiCheatCutScreen", getEnableAntiCheatCutScreen())
                .append("enableAntiCheatSingleQuestion", getEnableAntiCheatSingleQuestion())
                .append("enableAntiCheatDisableActions", getEnableAntiCheatDisableActions())
                .append("enableAntiCheatDevToolsDetection", getEnableAntiCheatDevToolsDetection())
                .append("enableAntiCheatBrowserEnvironmentDetection", getEnableAntiCheatBrowserEnvironmentDetection())
                .append("enableAntiCheatShuffle", getEnableAntiCheatShuffle())
                .append("startTime", getStartTime())
                .append("endTime", getEndTime())
                .append("numberMode", getNumberMode())
                .toString();
    }
}
