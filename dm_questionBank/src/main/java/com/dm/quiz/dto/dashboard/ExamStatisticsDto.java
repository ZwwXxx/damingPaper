package com.dm.quiz.dto.dashboard;

import java.util.List;

/**
 * 考试数据统计DTO
 * 
 * @author Cascade
 * @date 2025-11-22
 */
public class ExamStatisticsDto {
    
    /** 今日考试人次 */
    private Long todayExams;
    
    /** 本周考试人次 */
    private Long weekExams;
    
    /** 本月考试人次 */
    private Long monthExams;
    
    /** 及格率 */
    private Double passRate;
    
    /** 优秀率 */
    private Double excellentRate;
    
    /** 考试趋势数据（按日期） */
    private List<ExamTrendDto> examTrend;
    
    /** 按试卷分类的考试统计 */
    private List<PaperExamStatDto> paperExamStats;
    
    /** 批改进度 */
    private ReviewProgressDto reviewProgress;

    public Long getTodayExams() {
        return todayExams;
    }

    public void setTodayExams(Long todayExams) {
        this.todayExams = todayExams;
    }

    public Long getWeekExams() {
        return weekExams;
    }

    public void setWeekExams(Long weekExams) {
        this.weekExams = weekExams;
    }

    public Long getMonthExams() {
        return monthExams;
    }

    public void setMonthExams(Long monthExams) {
        this.monthExams = monthExams;
    }

    public Double getPassRate() {
        return passRate;
    }

    public void setPassRate(Double passRate) {
        this.passRate = passRate;
    }

    public Double getExcellentRate() {
        return excellentRate;
    }

    public void setExcellentRate(Double excellentRate) {
        this.excellentRate = excellentRate;
    }

    public List<ExamTrendDto> getExamTrend() {
        return examTrend;
    }

    public void setExamTrend(List<ExamTrendDto> examTrend) {
        this.examTrend = examTrend;
    }

    public List<PaperExamStatDto> getPaperExamStats() {
        return paperExamStats;
    }

    public void setPaperExamStats(List<PaperExamStatDto> paperExamStats) {
        this.paperExamStats = paperExamStats;
    }

    public ReviewProgressDto getReviewProgress() {
        return reviewProgress;
    }

    public void setReviewProgress(ReviewProgressDto reviewProgress) {
        this.reviewProgress = reviewProgress;
    }
    
    /**
     * 考试趋势数据
     */
    public static class ExamTrendDto {
        /** 日期 */
        private String date;
        
        /** 考试人次 */
        private Long count;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public Long getCount() {
            return count;
        }

        public void setCount(Long count) {
            this.count = count;
        }
    }
    
    /**
     * 批改进度数据
     */
    public static class ReviewProgressDto {
        /** 待批改数 */
        private Long pending;
        
        /** 已批改数 */
        private Long reviewed;
        
        /** 批改率 */
        private Double rate;

        public Long getPending() {
            return pending;
        }

        public void setPending(Long pending) {
            this.pending = pending;
        }

        public Long getReviewed() {
            return reviewed;
        }

        public void setReviewed(Long reviewed) {
            this.reviewed = reviewed;
        }

        public Double getRate() {
            return rate;
        }

        public void setRate(Double rate) {
            this.rate = rate;
        }
    }
    
    /**
     * 按试卷分类的考试统计数据
     */
    public static class PaperExamStatDto {
        /** 试卷ID */
        private Long paperId;
        
        /** 试卷名称 */
        private String paperName;
        
        /** 考试人次 */
        private Long examCount;
        
        /** 平均分（原始分） */
        private Double avgScore;
        
        /** 平均分百分比 */
        private Double avgScorePercent;
        
        /** 试卷总分 */
        private Integer totalScore;

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

        public Long getExamCount() {
            return examCount;
        }

        public void setExamCount(Long examCount) {
            this.examCount = examCount;
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

        public Integer getTotalScore() {
            return totalScore;
        }

        public void setTotalScore(Integer totalScore) {
            this.totalScore = totalScore;
        }
    }
}
