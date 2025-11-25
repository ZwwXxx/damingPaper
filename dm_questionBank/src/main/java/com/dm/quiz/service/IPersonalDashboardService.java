package com.dm.quiz.service;

import com.dm.quiz.dto.dashboard.front.*;
import java.util.List;

/**
 * 个人学习报表Service接口
 * 
 * @author daming
 */
public interface IPersonalDashboardService {
    
    /**
     * 获取个人学习概览
     * 
     * @param username 用户名
     * @return 学习概览数据
     */
    PersonalOverviewDto getOverview(String username);
    
    /**
     * 获取个人考试趋势
     * 
     * @param username 用户名
     * @param limit 返回记录数
     * @param paperName 试卷名称（可选）
     * @return 考试趋势列表
     */
    List<ExamTrendDto> getExamTrend(String username, Integer limit, String paperName);
    
    /**
     * 获取个人所有考试过的试卷列表
     * 
     * @param username 用户名
     * @return 试卷名称列表
     */
    List<String> getPaperList(String username);
    
    /**
     * 获取个人各科目成绩统计
     * 
     * @param username 用户名
     * @return 科目成绩列表
     */
    List<SubjectScoreDto> getSubjectScores(String username);
    
    /**
     * 获取个人错题统计
     * 
     * @param username 用户名
     * @return 错题统计数据
     */
    WrongQuestionStatDto getWrongQuestionStat(String username);
    
    /**
     * 获取个人学习时间分布
     * 
     * @param username 用户名
     * @param days 最近天数
     * @return 学习时间分布数据
     */
    StudyTimeDto getStudyTimeDistribution(String username, Integer days);
}
