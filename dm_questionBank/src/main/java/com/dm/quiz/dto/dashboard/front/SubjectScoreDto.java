package com.dm.quiz.dto.dashboard.front;

import java.io.Serializable;

/**
 * 个人科目成绩DTO
 * 
 * @author daming
 */
public class SubjectScoreDto implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 科目ID */
    private Long subjectId;
    
    /** 科目名称 */
    private String subjectName;
    
    /** 平均分 */
    private Double avgScore;
    
    /** 平均得分率 */
    private Double avgScorePercent;
    
    /** 最高分 */
    private Double maxScore;
    
    /** 最低分 */
    private Double minScore;
    
    /** 考试次数 */
    private Long examCount;

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public Double getAvgScore() {
        return avgScore;
    }

    public void setAvgScore(Double avgScore) {
        this.avgScore = avgScore;
    }

    public Double getAvgScorePercent() {
        return avgScorePercent;
    }

    public void setAvgScorePercent(Double avgScorePercent) {
        this.avgScorePercent = avgScorePercent;
    }

    public Double getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(Double maxScore) {
        this.maxScore = maxScore;
    }

    public Double getMinScore() {
        return minScore;
    }

    public void setMinScore(Double minScore) {
        this.minScore = minScore;
    }

    public Long getExamCount() {
        return examCount;
    }

    public void setExamCount(Long examCount) {
        this.examCount = examCount;
    }
}
