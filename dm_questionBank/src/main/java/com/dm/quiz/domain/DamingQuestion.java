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

    /** 题目类型 */
    @Excel(name = "题目类型")
    private Integer questionType;

    /** 题目内容id */
    @Excel(name = "题目内容id")
    private Long questionInfoId;

    /** 正确答案 */
    //不是把它拆表出去了吗？因为后续要与答卷的答题进行correct比较，就不再次查询content-info表了
    @Excel(name = "正确答案")
    private String correct;

    /** 总分 */
    @Excel(name = "总分")
    private Integer score;

    /** 是否删除 */
    private Integer delFlag;

    /** 科目 */
    @Excel(name = "科目")
    private Integer subjectId;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setQuestionType(Integer questionType)
    {
        this.questionType = questionType;
    }

    public Integer getQuestionType()
    {
        return questionType;
    }
    public void setQuestionInfoId(Long questionInfoId)
    {
        this.questionInfoId = questionInfoId;
    }

    public Long getQuestionInfoId()
    {
        return questionInfoId;
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

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("questionType", getQuestionType())
                .append("questionInfoId", getQuestionInfoId())
                .append("correct", getCorrect())
                .append("score", getScore())
                .append("delFlag", getDelFlag())
                .append("subjectId", getSubjectId())
                .append("createTime", getCreateTime())
                .append("updateTime", getUpdateTime())
                .toString();
    }
}
