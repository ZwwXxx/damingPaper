package com.dm.quiz.dto.dashboard.front;

import java.io.Serializable;
import java.util.List;

/**
 * 个人学习时间分布DTO
 * 
 * @author daming
 */
public class StudyTimeDto implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 学习天数 */
    private Long studyDays;
    
    /** 连续学习天数 */
    private Long continuousDays;
    
    /** 24小时分布 */
    private List<HourDistribution> hourDistribution;

    public Long getStudyDays() {
        return studyDays;
    }

    public void setStudyDays(Long studyDays) {
        this.studyDays = studyDays;
    }

    public Long getContinuousDays() {
        return continuousDays;
    }

    public void setContinuousDays(Long continuousDays) {
        this.continuousDays = continuousDays;
    }

    public List<HourDistribution> getHourDistribution() {
        return hourDistribution;
    }

    public void setHourDistribution(List<HourDistribution> hourDistribution) {
        this.hourDistribution = hourDistribution;
    }

    /**
     * 小时分布
     */
    public static class HourDistribution implements Serializable {
        private static final long serialVersionUID = 1L;
        
        private Integer hour;
        private Long count;

        public Integer getHour() {
            return hour;
        }

        public void setHour(Integer hour) {
            this.hour = hour;
        }

        public Long getCount() {
            return count;
        }

        public void setCount(Long count) {
            this.count = count;
        }
    }
}
