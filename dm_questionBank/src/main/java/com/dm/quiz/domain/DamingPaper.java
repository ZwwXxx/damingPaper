package com.dm.quiz.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 试卷对象 daming_paper
 *
 * @author zww
 * @date 2024-10-10
 */
public class DamingPaper extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /**  */
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
     * @param paperId
     */
    private Long paperInfoId;

    /** $column.columnComment */
    @Excel(name = "试卷类型")
    private Integer paperType;

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
            .toString();
    }
}
