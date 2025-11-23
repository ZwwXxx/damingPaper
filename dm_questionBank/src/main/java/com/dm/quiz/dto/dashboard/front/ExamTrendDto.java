package com.dm.quiz.dto.dashboard.front;

import java.io.Serializable;
import java.util.Date;

/**
 * 个人考试趋势DTO
 * 
 * @author daming
 */
public class ExamTrendDto implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 试卷答题ID */
    private Long paperAnswerId;
    
    /** 试卷ID */
    private Long paperId;
    
    /** 试卷名称 */
    private String paperName;
    
    /** 试卷总分 */
    private Integer paperScore;
    
    /** 得分 */
    private Double finalScore;
    
    /** 得分率 */
    private Double scorePercent;
    
    /** 考试时间 */
    private Date createTime;
    
    /** 耗时（分钟） */
    private Integer doTime;

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

    public String getPaperName() {
        return paperName;
    }

    public void setPaperName(String paperName) {
        this.paperName = paperName;
    }

    public Integer getPaperScore() {
        return paperScore;
    }

    public void setPaperScore(Integer paperScore) {
        this.paperScore = paperScore;
    }

    public Double getFinalScore() {
        return finalScore;
    }

    public void setFinalScore(Double finalScore) {
        this.finalScore = finalScore;
    }

    public Double getScorePercent() {
        return scorePercent;
    }

    public void setScorePercent(Double scorePercent) {
        this.scorePercent = scorePercent;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getDoTime() {
        return doTime;
    }

    public void setDoTime(Integer doTime) {
        this.doTime = doTime;
    }
}
