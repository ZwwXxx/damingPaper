package com.dm.quiz.mapper;

import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

/**
 * 个人学习报表Mapper接口
 * 
 * @author daming
 */
public interface PersonalDashboardMapper {
    
    /**
     * 获取用户累计做题数
     */
    Long getTotalQuestions(@Param("userId") String username);
    
    /**
     * 获取用户累计考试次数
     */
    Long getTotalExams(@Param("userId") String username);
    
    /**
     * 获取用户累计错题数
     */
    Long getTotalWrongQuestions(@Param("userId") String username);
    
    /**
     * 获取用户累计收藏数
     */
    Long getTotalFavorites(@Param("userId") String username);
    
    /**
     * 获取用户最近N天做题数
     */
    Long getRecentQuestions(@Param("userId") String username, @Param("days") Integer days);
    
    /**
     * 获取用户最近N天考试次数
     */
    Long getRecentExams(@Param("userId") String username, @Param("days") Integer days);
    
    /**
     * 获取用户累计学习时长（分钟）
     */
    Long getTotalStudyTime(@Param("userId") String username);
    
    /**
     * 获取用户最近N次考试记录
     */
    List<Map<String, Object>> getExamTrend(
        @Param("userId") String username, 
        @Param("limit") Integer limit,
        @Param("paperName") String paperName
    );
    
    /**
     * 获取用户所有考试过的试卷列表
     */
    List<String> getPaperList(@Param("userId") String username);
    
    /**
     * 获取用户按科目的成绩统计
     */
    List<Map<String, Object>> getSubjectScores(@Param("userId") String username);
    
    /**
     * 获取用户错题按科目分布
     */
    List<Map<String, Object>> getWrongQuestionsBySubject(@Param("userId") String username);
    
    /**
     * 获取用户错题按题型分布
     */
    List<Map<String, Object>> getWrongQuestionsByType(@Param("userId") String username);
    
    /**
     * 获取用户学习天数（最近N天）
     */
    Long getStudyDays(@Param("userId") String username, @Param("days") Integer days);
    
    /**
     * 获取用户连续学习天数
     */
    Long getContinuousStudyDays(@Param("userId") String username);
    
    /**
     * 获取用户24小时学习时间分布（最近N天）
     */
    List<Map<String, Object>> getHourDistribution(@Param("userId") String username, @Param("days") Integer days);
}
