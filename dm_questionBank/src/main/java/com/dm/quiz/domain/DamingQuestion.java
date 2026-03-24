package com.dm.quiz.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 题目管理对象 daming_question
 *
 * @author zww
 * @date 2024-10-13
 */
public class DamingQuestion extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 题目id */
    private Long id;

    /** 父题ID（完形/阅读等复合题） */
    private Long parentId;

    /** 题目类型 */
    @Excel(name = "题目类型")
    private Integer questionType;

    /** 完形第几空索引（从1开始），普通题为空 */
    private Integer clozeIndex;

    /** 原卷题号（复合题可填首子题题号） */
    private Integer originOrder;

    /** 题目内容id */
    @Excel(name = "题目内容id")
    private Long questionInfoId;

    /** 动画解析ID（可选） */
    private Long animationId;

    /** 正确答案 */
    //不是把它拆表出去了吗？因为后续要与答卷的答题进行correct比较，就不再次查询content-info表了
    @Excel(name = "正确答案")
    private String correct;

    /** 总分 */
    @Excel(name = "总分")
    private Integer score;

    /** 难度：1=简单，2=中等，3=困难 */
    @Excel(name = "难度")
    private Integer difficulty;

    /** 是否删除 */
    private Integer delFlag;

    /** 科目 */
    @Excel(name = "科目")
    private Integer subjectId;

    /** 题目年份（用于按年份筛题/组卷） */
    @Excel(name = "题目年份")
    private Integer examYear;

    /**
     * 考试批次：1=上半年，2=下半年；为空表示该年份仅考一次
     */
    @Excel(name = "考试批次")
    private Integer examHalf;

    /**
     * 题干（仅用于列表查询/模糊搜索，不落库；真实题干存于 daming_content_info.content 的 JSON 中）
     */
    private String questionTitle;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public Long getParentId()
    {
        return parentId;
    }

    public void setParentId(Long parentId)
    {
        this.parentId = parentId;
    }
    public void setQuestionType(Integer questionType)
    {
        this.questionType = questionType;
    }

    public Integer getQuestionType()
    {
        return questionType;
    }
    public Integer getClozeIndex()
    {
        return clozeIndex;
    }

    public void setClozeIndex(Integer clozeIndex)
    {
        this.clozeIndex = clozeIndex;
    }

    public Integer getOriginOrder() {
        return originOrder;
    }

    public void setOriginOrder(Integer originOrder) {
        this.originOrder = originOrder;
    }
    public void setQuestionInfoId(Long questionInfoId)
    {
        this.questionInfoId = questionInfoId;
    }

    public Long getQuestionInfoId()
    {
        return questionInfoId;
    }

    public Long getAnimationId()
    {
        return animationId;
    }

    public void setAnimationId(Long animationId)
    {
        this.animationId = animationId;
    }

    public void setCorrect(String correct)
    {
        this.correct = correct;
    }

    public String getCorrect()
    {
        return correct;
    }
    public void setScore(Integer score)
    {
        this.score = score;
    }

    public Integer getScore()
    {
        return score;
    }
    public Integer getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Integer difficulty) {
        this.difficulty = difficulty;
    }
    public void setDelFlag(Integer delFlag)
    {
        this.delFlag = delFlag;
    }

    public Integer getDelFlag()
    {
        return delFlag;
    }
    public void setSubjectId(Integer subjectId)
    {
        this.subjectId = subjectId;
    }

    public Integer getSubjectId()
    {
        return subjectId;
    }

    public Integer getExamYear() {
        return examYear;
    }

    public void setExamYear(Integer examYear) {
        this.examYear = examYear;
    }

    public Integer getExamHalf() {
        return examHalf;
    }

    public void setExamHalf(Integer examHalf) {
        this.examHalf = examHalf;
    }

    public String getQuestionTitle() {
        return questionTitle;
    }

    public void setQuestionTitle(String questionTitle) {
        this.questionTitle = questionTitle;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("parentId", getParentId())
                .append("questionType", getQuestionType())
                .append("clozeIndex", getClozeIndex())
                .append("originOrder", getOriginOrder())
                .append("questionInfoId", getQuestionInfoId())
                .append("animationId", getAnimationId())
                .append("correct", getCorrect())
                .append("score", getScore())
                .append("difficulty", getDifficulty())
                .append("delFlag", getDelFlag())
                .append("subjectId", getSubjectId())
                .append("examYear", getExamYear())
                .append("examHalf", getExamHalf())
                .append("questionTitle", getQuestionTitle())
                .append("createTime", getCreateTime())
                .append("updateTime", getUpdateTime())
                .toString();
    }
}
