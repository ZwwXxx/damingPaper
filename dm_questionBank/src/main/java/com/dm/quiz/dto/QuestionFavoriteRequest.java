package com.dm.quiz.dto;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 收藏题目请求
 */
public class QuestionFavoriteRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "题目ID不能为空")
    private Long questionId;

    @NotNull(message = "考试记录ID不能为空")
    private Long paperAnswerId;

    private Long paperId;

    private Integer subjectId;

    private String remark;

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Long getPaperAnswerId() {
        return paperAnswerId;
    }

    public void setPaperAnswerId(Long paperAnswerId) {
        this.paperAnswerId = paperAnswerId;
    }

    public Long getPaperId() {
        return paperId;
    }

    public void setPaperId(Long paperId) {
        this.paperId = paperId;
    }

    public Integer getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Integer subjectId) {
        this.subjectId = subjectId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}


