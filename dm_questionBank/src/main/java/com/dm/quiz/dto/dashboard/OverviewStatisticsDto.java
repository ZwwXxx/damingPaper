package com.dm.quiz.dto.dashboard;

/**
 * 首页概览统计DTO
 * 
 * @author Cascade
 * @date 2025-11-22
 */
public class OverviewStatisticsDto {
    
    /** 总用户数 */
    private Long totalUsers;
    
    /** 用户增长率 */
    private String totalUsersGrowth;
    
    /** 总题目数 */
    private Long totalQuestions;
    
    /** 题目增长率 */
    private String totalQuestionsGrowth;
    
    /** 总试卷数 */
    private Long totalPapers;
    
    /** 试卷增长率 */
    private String totalPapersGrowth;
    
    /** 总考试次数 */
    private Long totalExams;
    
    /** 考试次数增长率 */
    private String totalExamsGrowth;

    public Long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(Long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public String getTotalUsersGrowth() {
        return totalUsersGrowth;
    }

    public void setTotalUsersGrowth(String totalUsersGrowth) {
        this.totalUsersGrowth = totalUsersGrowth;
    }

    public Long getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(Long totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public String getTotalQuestionsGrowth() {
        return totalQuestionsGrowth;
    }

    public void setTotalQuestionsGrowth(String totalQuestionsGrowth) {
        this.totalQuestionsGrowth = totalQuestionsGrowth;
    }

    public Long getTotalPapers() {
        return totalPapers;
    }

    public void setTotalPapers(Long totalPapers) {
        this.totalPapers = totalPapers;
    }

    public String getTotalPapersGrowth() {
        return totalPapersGrowth;
    }

    public void setTotalPapersGrowth(String totalPapersGrowth) {
        this.totalPapersGrowth = totalPapersGrowth;
    }

    public Long getTotalExams() {
        return totalExams;
    }

    public void setTotalExams(Long totalExams) {
        this.totalExams = totalExams;
    }

    public String getTotalExamsGrowth() {
        return totalExamsGrowth;
    }

    public void setTotalExamsGrowth(String totalExamsGrowth) {
        this.totalExamsGrowth = totalExamsGrowth;
    }
}
