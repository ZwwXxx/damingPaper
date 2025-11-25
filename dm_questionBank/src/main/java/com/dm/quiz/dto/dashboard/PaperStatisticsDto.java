package com.dm.quiz.dto.dashboard;

import java.util.List;

/**
 * 试卷统计DTO
 * 
 * @author Cascade
 * @date 2025-11-22
 */
public class PaperStatisticsDto {
    
    /** 热门试卷TOP10 */
    private List<TopPaperDto> topPapers;
    
    /** 试卷使用率 */
    private Double usageRate;
    
    /** 已发布试卷数 */
    private Long publishedCount;
    
    /** 已使用试卷数 */
    private Long usedCount;

    public List<TopPaperDto> getTopPapers() {
        return topPapers;
    }

    public void setTopPapers(List<TopPaperDto> topPapers) {
        this.topPapers = topPapers;
    }

    public Double getUsageRate() {
        return usageRate;
    }

    public void setUsageRate(Double usageRate) {
        this.usageRate = usageRate;
    }

    public Long getPublishedCount() {
        return publishedCount;
    }

    public void setPublishedCount(Long publishedCount) {
        this.publishedCount = publishedCount;
    }

    public Long getUsedCount() {
        return usedCount;
    }

    public void setUsedCount(Long usedCount) {
        this.usedCount = usedCount;
    }
    
    /**
     * 热门试卷数据
     */
    public static class TopPaperDto {
        /** 试卷ID */
        private Long paperId;
        
        /** 试卷名称 */
        private String paperName;
        
        /** 考试人次 */
        private Long examCount;
        
        /** 平均分 */
        private Double avgScore;
        
        /** 科目名称 */
        private String subjectName;

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

        public String getSubjectName() {
            return subjectName;
        }

        public void setSubjectName(String subjectName) {
            this.subjectName = subjectName;
        }
    }
}
