package com.dm.quiz.dto.dashboard;

import java.util.List;

/**
 * 时间分布统计DTO
 * 
 * @author Cascade
 * @date 2025-11-22
 */
public class TimeDistributionDto {
    
    /** 24小时分布数据 */
    private List<HourlyDistributionDto> hourlyDistribution;

    public List<HourlyDistributionDto> getHourlyDistribution() {
        return hourlyDistribution;
    }

    public void setHourlyDistribution(List<HourlyDistributionDto> hourlyDistribution) {
        this.hourlyDistribution = hourlyDistribution;
    }
    
    /**
     * 小时分布数据
     */
    public static class HourlyDistributionDto {
        /** 小时(0-23) */
        private Integer hour;
        
        /** 数量 */
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
