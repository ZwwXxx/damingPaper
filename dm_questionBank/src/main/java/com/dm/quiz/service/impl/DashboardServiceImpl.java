package com.dm.quiz.service.impl;

import com.dm.quiz.dto.dashboard.*;
import com.dm.quiz.dto.dashboard.ExamStatisticsDto.ExamTrendDto;
import com.dm.quiz.dto.dashboard.ExamStatisticsDto.ReviewProgressDto;
import com.dm.quiz.dto.dashboard.ExamStatisticsDto.PaperExamStatDto;
import com.dm.quiz.dto.dashboard.QuestionStatisticsDto.SubjectDistributionDto;
import com.dm.quiz.dto.dashboard.QuestionStatisticsDto.TopFavoriteDto;
import com.dm.quiz.dto.dashboard.QuestionStatisticsDto.TypeDistributionDto;
import com.dm.quiz.dto.dashboard.PaperStatisticsDto.TopPaperDto;
import com.dm.quiz.dto.dashboard.TimeDistributionDto.HourlyDistributionDto;
import com.dm.quiz.dto.dashboard.UserActivityDto.LoginTrendDto;
import com.dm.quiz.mapper.DashboardMapper;
import com.dm.quiz.service.IDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 首页报表Service业务层处理
 *
 * @author Cascade
 * @date 2025-11-22
 */
@Service
public class DashboardServiceImpl implements IDashboardService {

    @Autowired
    private DashboardMapper dashboardMapper;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public OverviewStatisticsDto getOverviewStatistics() {
        OverviewStatisticsDto dto = new OverviewStatisticsDto();

        // 获取当前时间和昨天的时间范围
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        String todayStart = today.atStartOfDay().format(DATETIME_FORMATTER);
        String yesterdayStart = yesterday.atStartOfDay().format(DATETIME_FORMATTER);
        String yesterdayEnd = today.atStartOfDay().format(DATETIME_FORMATTER);

        // 统计总用户数
        Long totalUsers = dashboardMapper.countTotalUsers();
        Long yesterdayNewUsers = dashboardMapper.countNewUsers(yesterdayStart, yesterdayEnd);
        dto.setTotalUsers(totalUsers);
        dto.setTotalUsersGrowth(formatGrowth(yesterdayNewUsers));

        // 统计总题目数
        Long totalQuestions = dashboardMapper.countTotalQuestions();
        Long yesterdayNewQuestions = dashboardMapper.countNewQuestions(yesterdayStart, yesterdayEnd);
        dto.setTotalQuestions(totalQuestions);
        dto.setTotalQuestionsGrowth(formatGrowth(yesterdayNewQuestions));

        // 统计总试卷数
        Long totalPapers = dashboardMapper.countTotalPapers();
        Long yesterdayNewPapers = dashboardMapper.countNewPapers(yesterdayStart, yesterdayEnd);
        dto.setTotalPapers(totalPapers);
        dto.setTotalPapersGrowth(formatGrowth(yesterdayNewPapers));

        // 统计总考试次数
        Long totalExams = dashboardMapper.countTotalExams();
        Long yesterdayExams = dashboardMapper.countExamsByDateRange(yesterdayStart, yesterdayEnd);
        dto.setTotalExams(totalExams);
        dto.setTotalExamsGrowth(formatGrowth(yesterdayExams));

        return dto;
    }

    @Override
    public UserActivityDto getUserActivity() {
        UserActivityDto dto = new UserActivityDto();

        LocalDate today = LocalDate.now();
        String todayStart = today.atStartOfDay().format(DATETIME_FORMATTER);
        String tomorrowStart = today.plusDays(1).atStartOfDay().format(DATETIME_FORMATTER);
        String weekStart = today.minusDays(7).atStartOfDay().format(DATETIME_FORMATTER);
        String monthStart = today.minusDays(30).atStartOfDay().format(DATETIME_FORMATTER);

        // 今日登录用户数
        Long todayLogins = dashboardMapper.countUniqueLoginUsers(todayStart, tomorrowStart);
        dto.setTodayLogins(todayLogins);

        // 本周登录用户数
        Long weekLogins = dashboardMapper.countUniqueLoginUsers(weekStart, tomorrowStart);
        dto.setWeekLogins(weekLogins);

        // 本月登录用户数
        Long monthLogins = dashboardMapper.countUniqueLoginUsers(monthStart, tomorrowStart);
        dto.setMonthLogins(monthLogins);

        // 本月新增用户数
        Long newUsers = dashboardMapper.countNewUsers(monthStart, tomorrowStart);
        dto.setNewUsers(newUsers);

        // 登录趋势数据(最近30天)
        List<Map<String, Object>> trendData = dashboardMapper.getLoginTrend(30);
        List<LoginTrendDto> loginTrend = new ArrayList<>();
        for (Map<String, Object> data : trendData) {
            LoginTrendDto trend = new LoginTrendDto();
            trend.setDate((String) data.get("date"));
            trend.setLogins(((Number) data.get("logins")).longValue());
            trend.setUniqueUsers(((Number) data.get("uniqueUsers")).longValue());
            loginTrend.add(trend);
        }
        dto.setLoginTrend(loginTrend);

        return dto;
    }

    @Override
    public ExamStatisticsDto getExamStatistics() {
        ExamStatisticsDto dto = new ExamStatisticsDto();

        LocalDate today = LocalDate.now();
        String todayStart = today.atStartOfDay().format(DATETIME_FORMATTER);
        String tomorrowStart = today.plusDays(1).atStartOfDay().format(DATETIME_FORMATTER);
        String weekStart = today.minusDays(7).atStartOfDay().format(DATETIME_FORMATTER);
        String monthStart = today.minusDays(30).atStartOfDay().format(DATETIME_FORMATTER);

        // 今日考试人次
        Long todayExams = dashboardMapper.countExamsByDateRange(todayStart, tomorrowStart);
        dto.setTodayExams(todayExams);

        // 本周考试人次
        Long weekExams = dashboardMapper.countExamsByDateRange(weekStart, tomorrowStart);
        dto.setWeekExams(weekExams);

        // 本月考试人次
        Long monthExams = dashboardMapper.countExamsByDateRange(monthStart, tomorrowStart);
        dto.setMonthExams(monthExams);

        // 及格率(60分)
        Double passRate = dashboardMapper.getPassRate(60);
        dto.setPassRate(passRate != null ? passRate : 0.0);

        // 优秀率(85分)
        Double excellentRate = dashboardMapper.getExcellentRate(85);
        dto.setExcellentRate(excellentRate != null ? excellentRate : 0.0);

        // 考试趋势数据(最近30天，按日期)
        List<Map<String, Object>> trendData = dashboardMapper.getExamTrend(30);
        List<ExamTrendDto> examTrend = new ArrayList<>();
        for (Map<String, Object> data : trendData) {
            ExamTrendDto trend = new ExamTrendDto();
            trend.setDate((String) data.get("date"));
            trend.setCount(((Number) data.get("count")).longValue());
            examTrend.add(trend);
        }
        dto.setExamTrend(examTrend);
        
        // 按试卷分类的考试统计(最近30天热门试卷TOP5)
        List<Map<String, Object>> paperData = dashboardMapper.getExamTrendByPaper(30, 5);
        System.out.println("=== 查询到的试卷统计数据数量: " + (paperData != null ? paperData.size() : 0));
        if (paperData != null && !paperData.isEmpty()) {
            System.out.println("=== 第一条数据: " + paperData.get(0));
        }
        
        List<PaperExamStatDto> paperExamStats = new ArrayList<>();
        if (paperData != null) {
            for (Map<String, Object> data : paperData) {
                PaperExamStatDto stat = new PaperExamStatDto();
                stat.setPaperId(((Number) data.get("paperId")).longValue());
                stat.setPaperName((String) data.get("paperName"));
                stat.setExamCount(((Number) data.get("examCount")).longValue());
                Object avgScoreObj = data.get("avgScore");
                stat.setAvgScore(avgScoreObj != null ? ((Number) avgScoreObj).doubleValue() : 0.0);
                Object avgScorePercentObj = data.get("avgScorePercent");
                stat.setAvgScorePercent(avgScorePercentObj != null ? ((Number) avgScorePercentObj).doubleValue() : 0.0);
                Object totalScoreObj = data.get("totalScore");
                stat.setTotalScore(totalScoreObj != null ? ((Number) totalScoreObj).intValue() : 100);
                paperExamStats.add(stat);
            }
        }
        System.out.println("=== 转换后的DTO数量: " + paperExamStats.size());
        dto.setPaperExamStats(paperExamStats);

        // 批改进度
        Map<String, Object> progressData = dashboardMapper.getReviewProgress();
        ReviewProgressDto progress = new ReviewProgressDto();
        progress.setPending(((Number) progressData.get("pending")).longValue());
        progress.setReviewed(((Number) progressData.get("reviewed")).longValue());
        Object rateObj = progressData.get("rate");
        progress.setRate(rateObj != null ? ((Number) rateObj).doubleValue() : 0.0);
        dto.setReviewProgress(progress);

        return dto;
    }

    @Override
    public QuestionStatisticsDto getQuestionStatistics() {
        QuestionStatisticsDto dto = new QuestionStatisticsDto();

        // 科目分布
        List<Map<String, Object>> subjectData = dashboardMapper.getQuestionDistributionBySubject();
        List<SubjectDistributionDto> subjectDistribution = new ArrayList<>();
        for (Map<String, Object> data : subjectData) {
            SubjectDistributionDto subject = new SubjectDistributionDto();
            subject.setSubjectName((String) data.get("subjectName"));
            subject.setCount(((Number) data.get("count")).longValue());
            subjectDistribution.add(subject);
        }
        dto.setSubjectDistribution(subjectDistribution);

        // 题型分布
        List<Map<String, Object>> typeData = dashboardMapper.getQuestionDistributionByType();
        List<TypeDistributionDto> typeDistribution = new ArrayList<>();
        for (Map<String, Object> data : typeData) {
            TypeDistributionDto type = new TypeDistributionDto();
            type.setType(((Number) data.get("type")).intValue());
            type.setTypeName((String) data.get("typeName"));
            type.setCount(((Number) data.get("count")).longValue());
            typeDistribution.add(type);
        }
        dto.setTypeDistribution(typeDistribution);

        // 热门收藏题目TOP10
        List<Map<String, Object>> favoriteData = dashboardMapper.getTopFavoriteQuestions(10);
        List<TopFavoriteDto> topFavorites = new ArrayList<>();
        for (Map<String, Object> data : favoriteData) {
            TopFavoriteDto favorite = new TopFavoriteDto();
            favorite.setQuestionId(((Number) data.get("questionId")).longValue());
            favorite.setPreview((String) data.get("preview"));
            favorite.setFavCount(((Number) data.get("favCount")).longValue());
            topFavorites.add(favorite);
        }
        dto.setTopFavorites(topFavorites);

        return dto;
    }

    @Override
    public PaperStatisticsDto getPaperStatistics() {
        PaperStatisticsDto dto = new PaperStatisticsDto();

        // 热门试卷TOP10
        List<Map<String, Object>> paperData = dashboardMapper.getTopPapers(10);
        List<TopPaperDto> topPapers = new ArrayList<>();
        for (Map<String, Object> data : paperData) {
            TopPaperDto paper = new TopPaperDto();
            paper.setPaperId(((Number) data.get("paperId")).longValue());
            paper.setPaperName((String) data.get("paperName"));
            paper.setExamCount(((Number) data.get("examCount")).longValue());
            Object avgScoreObj = data.get("avgScore");
            paper.setAvgScore(avgScoreObj != null ? ((Number) avgScoreObj).doubleValue() : 0.0);
            paper.setSubjectName((String) data.get("subjectName"));
            topPapers.add(paper);
        }
        dto.setTopPapers(topPapers);

        // 试卷使用情况
        Map<String, Object> usageData = dashboardMapper.getPaperUsage();
        dto.setPublishedCount(((Number) usageData.get("publishedCount")).longValue());
        dto.setUsedCount(((Number) usageData.get("usedCount")).longValue());
        Object usageRateObj = usageData.get("usageRate");
        dto.setUsageRate(usageRateObj != null ? ((Number) usageRateObj).doubleValue() : 0.0);

        return dto;
    }

    @Override
    public TimeDistributionDto getTimeDistribution() {
        TimeDistributionDto dto = new TimeDistributionDto();

        // 24小时分布
        List<Map<String, Object>> hourlyData = dashboardMapper.getExamHourlyDistribution();
        List<HourlyDistributionDto> hourlyDistribution = new ArrayList<>();
        
        // 初始化24小时的数据(0-23)
        for (int i = 0; i < 24; i++) {
            HourlyDistributionDto hourly = new HourlyDistributionDto();
            hourly.setHour(i);
            hourly.setCount(0L);
            hourlyDistribution.add(hourly);
        }
        
        // 填充实际数据
        for (Map<String, Object> data : hourlyData) {
            int hour = ((Number) data.get("hour")).intValue();
            Long count = ((Number) data.get("count")).longValue();
            hourlyDistribution.get(hour).setCount(count);
        }
        
        dto.setHourlyDistribution(hourlyDistribution);

        return dto;
    }

    /**
     * 格式化增长数据
     *
     * @param value 增长值
     * @return 格式化后的字符串
     */
    private String formatGrowth(Long value) {
        if (value == null || value == 0) {
            return "0";
        }
        return (value > 0 ? "+" : "") + value;
    }
}
