package com.dm.quiz.dto.dashboard.front;

import java.io.Serializable;

/**
 * 个人学习概览DTO
 * 
 * @author daming
 */
public class PersonalOverviewDto implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 累计做题数 */
    private Long totalQuestions;
    
    /** 累计考试次数 */
    private Long totalExams;
    
    /** 累计错题数 */
    private Long totalWrongQuestions;
    
    /** 累计收藏数 */
    private Long totalFavorites;
    
    /** 最近7天做题数 */
    private Long weekQuestions;
    
    /** 最近7天考试次数 */
    private Long weekExams;
    
    /** 累计学习时长（分钟） */
    private Long totalStudyTime;

    public Long getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(Long totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public Long getTotalExams() {
        return totalExams;
    }

    public void setTotalExams(Long totalExams) {
        this.totalExams = totalExams;
    }

    public Long getTotalWrongQuestions() {
        return totalWrongQuestions;
    }

    public void setTotalWrongQuestions(Long totalWrongQuestions) {
        this.totalWrongQuestions = totalWrongQuestions;
    }

    public Long getTotalFavorites() {
        return totalFavorites;
    }

    public void setTotalFavorites(Long totalFavorites) {
        this.totalFavorites = totalFavorites;
    }

    public Long getWeekQuestions() {
        return weekQuestions;
    }

    public void setWeekQuestions(Long weekQuestions) {
        this.weekQuestions = weekQuestions;
    }

    public Long getWeekExams() {
        return weekExams;
    }

    public void setWeekExams(Long weekExams) {
        this.weekExams = weekExams;
    }

    public Long getTotalStudyTime() {
        return totalStudyTime;
    }

    public void setTotalStudyTime(Long totalStudyTime) {
        this.totalStudyTime = totalStudyTime;
    }
}
