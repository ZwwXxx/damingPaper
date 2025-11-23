package com.dm.quiz.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 首页报表Mapper接口
 *
 * @author Cascade
 * @date 2025-11-22
 */
public interface DashboardMapper {
    
    /**
     * 统计总用户数
     *
     * @return 用户总数
     */
    Long countTotalUsers();
    
    /**
     * 统计指定日期范围内的新增用户数
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 新增用户数
     */
    Long countNewUsers(@Param("startDate") String startDate, @Param("endDate") String endDate);
    
    /**
     * 统计总题目数
     *
     * @return 题目总数
     */
    Long countTotalQuestions();
    
    /**
     * 统计指定日期范围内的新增题目数
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 新增题目数
     */
    Long countNewQuestions(@Param("startDate") String startDate, @Param("endDate") String endDate);
    
    /**
     * 统计总试卷数
     *
     * @return 试卷总数
     */
    Long countTotalPapers();
    
    /**
     * 统计指定日期范围内的新增试卷数
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 新增试卷数
     */
    Long countNewPapers(@Param("startDate") String startDate, @Param("endDate") String endDate);
    
    /**
     * 统计总考试次数
     *
     * @return 考试总次数
     */
    Long countTotalExams();
    
    /**
     * 统计指定日期范围内的考试次数
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 考试次数
     */
    Long countExamsByDateRange(@Param("startDate") String startDate, @Param("endDate") String endDate);
    
    /**
     * 统计指定日期范围内的唯一登录用户数
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 唯一登录用户数
     */
    Long countUniqueLoginUsers(@Param("startDate") String startDate, @Param("endDate") String endDate);
    
    /**
     * 查询最近N天的登录趋势
     *
     * @param days 天数
     * @return 登录趋势数据列表
     */
    List<Map<String, Object>> getLoginTrend(@Param("days") Integer days);
    
    /**
     * 查询最近N天的考试趋势（按日期）
     *
     * @param days 天数
     * @return 考试趋势数据列表
     */
    List<Map<String, Object>> getExamTrend(@Param("days") Integer days);
    
    /**
     * 按试卷统计考试趋势（最近N天的热门试卷）
     *
     * @param days 天数
     * @param limit 返回试卷数量
     * @return 按试卷分类的考试趋势
     */
    List<Map<String, Object>> getExamTrendByPaper(@Param("days") Integer days, @Param("limit") Integer limit);
    
    /**
     * 统计平均分
     *
     * @return 平均分
     */
    Double getAvgScore();
    
    /**
     * 统计及格率
     *
     * @param passScore 及格分数
     * @return 及格率
     */
    Double getPassRate(@Param("passScore") Integer passScore);
    
    /**
     * 统计优秀率
     *
     * @param excellentScore 优秀分数
     * @return 优秀率
     */
    Double getExcellentRate(@Param("excellentScore") Integer excellentScore);
    
    /**
     * 统计批改进度
     *
     * @return 批改进度数据
     */
    Map<String, Object> getReviewProgress();
    
    /**
     * 按科目统计题目分布
     *
     * @return 科目题目分布列表
     */
    List<Map<String, Object>> getQuestionDistributionBySubject();
    
    /**
     * 按题型统计题目分布
     *
     * @return 题型题目分布列表
     */
    List<Map<String, Object>> getQuestionDistributionByType();
    
    /**
     * 查询热门收藏题目TOP N
     *
     * @param limit 查询数量
     * @return 热门收藏题目列表
     */
    List<Map<String, Object>> getTopFavoriteQuestions(@Param("limit") Integer limit);
    
    /**
     * 查询热门试卷TOP N
     *
     * @param limit 查询数量
     * @return 热门试卷列表
     */
    List<Map<String, Object>> getTopPapers(@Param("limit") Integer limit);
    
    /**
     * 统计试卷使用情况
     *
     * @return 试卷使用情况数据
     */
    Map<String, Object> getPaperUsage();
    
    /**
     * 按小时统计考试时间分布
     *
     * @return 小时分布数据列表
     */
    List<Map<String, Object>> getExamHourlyDistribution();
}
