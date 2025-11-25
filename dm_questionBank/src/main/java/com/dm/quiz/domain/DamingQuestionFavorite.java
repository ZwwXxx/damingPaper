package com.dm.quiz.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 题目收藏表对象 daming_question_favorite
 */
public class DamingQuestionFavorite extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long favoriteId;

    /** 题目ID */
    @Excel(name = "题目ID")
    private Long questionId;

    /** 试卷ID */
    @Excel(name = "试卷ID")
    private Long paperId;

    /** 考试记录ID */
    @Excel(name = "考试记录ID")
    private Long paperAnswerId;

    /** 科目ID */
    @Excel(name = "科目ID")
    private Integer subjectId;

    /** 收藏人 */
    @Excel(name = "收藏人")
    private String createUser;

    /** 查询开始时间 */
    private String beginTime;

    /** 查询结束时间 */
    private String endTime;

    /** 试卷名称关键字（查询用） */
    private String paperName;

    public Long getFavoriteId() {
        return favoriteId;
    }

    public void setFavoriteId(Long favoriteId) {
        this.favoriteId = favoriteId;
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

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
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

    public String getPaperName() {
        return paperName;
    }

    public void setPaperName(String paperName) {
        this.paperName = paperName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("favoriteId", favoriteId)
                .append("questionId", questionId)
                .append("paperId", paperId)
                .append("paperAnswerId", paperAnswerId)
                .append("subjectId", subjectId)
                .append("createUser", createUser)
                .append("paperName", paperName)
                .append("createTime", getCreateTime())
                .append("remark", getRemark())
                .toString();
    }
}


