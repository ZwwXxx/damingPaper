package com.dm.quiz.service;

import com.dm.quiz.dto.dashboard.*;

/**
 * 首页报表Service接口
 *
 * @author Cascade
 * @date 2025-11-22
 */
public interface IDashboardService {
    
    /**
     * 获取首页概览统计数据
     *
     * @return 概览统计数据
     */
    OverviewStatisticsDto getOverviewStatistics();
    
    /**
     * 获取用户活跃度统计数据
     *
     * @return 用户活跃度数据
     */
    UserActivityDto getUserActivity();
    
    /**
     * 获取考试数据统计
     *
     * @return 考试统计数据
     */
    ExamStatisticsDto getExamStatistics();
    
    /**
     * 获取题目统计数据
     *
     * @return 题目统计数据
     */
    QuestionStatisticsDto getQuestionStatistics();
    
    /**
     * 获取试卷统计数据
     *
     * @return 试卷统计数据
     */
    PaperStatisticsDto getPaperStatistics();
    
    /**
     * 获取时间分布数据
     *
     * @return 时间分布数据
     */
    TimeDistributionDto getTimeDistribution();
}
