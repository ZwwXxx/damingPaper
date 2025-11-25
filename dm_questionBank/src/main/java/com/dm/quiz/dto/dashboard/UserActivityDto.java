package com.dm.quiz.dto.dashboard;

import java.util.List;

/**
 * 用户活跃度统计DTO
 * 
 * @author Cascade
 * @date 2025-11-22
 */
public class UserActivityDto {
    
    /** 今日登录用户数 */
    private Long todayLogins;
    
    /** 本周登录用户数 */
    private Long weekLogins;
    
    /** 本月登录用户数 */
    private Long monthLogins;
    
    /** 本月新增用户数 */
    private Long newUsers;
    
    /** 登录趋势数据 */
    private List<LoginTrendDto> loginTrend;

    public Long getTodayLogins() {
        return todayLogins;
    }

    public void setTodayLogins(Long todayLogins) {
        this.todayLogins = todayLogins;
    }

    public Long getWeekLogins() {
        return weekLogins;
    }

    public void setWeekLogins(Long weekLogins) {
        this.weekLogins = weekLogins;
    }

    public Long getMonthLogins() {
        return monthLogins;
    }

    public void setMonthLogins(Long monthLogins) {
        this.monthLogins = monthLogins;
    }

    public Long getNewUsers() {
        return newUsers;
    }

    public void setNewUsers(Long newUsers) {
        this.newUsers = newUsers;
    }

    public List<LoginTrendDto> getLoginTrend() {
        return loginTrend;
    }

    public void setLoginTrend(List<LoginTrendDto> loginTrend) {
        this.loginTrend = loginTrend;
    }
    
    /**
     * 登录趋势数据
     */
    public static class LoginTrendDto {
        /** 日期 */
        private String date;
        
        /** 登录人次 */
        private Long logins;
        
        /** 唯一用户数 */
        private Long uniqueUsers;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public Long getLogins() {
            return logins;
        }

        public void setLogins(Long logins) {
            this.logins = logins;
        }

        public Long getUniqueUsers() {
            return uniqueUsers;
        }

        public void setUniqueUsers(Long uniqueUsers) {
            this.uniqueUsers = uniqueUsers;
        }
    }
}
