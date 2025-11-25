package com.ruoyi.web.controller.dashboard;

import com.dm.quiz.dto.dashboard.*;
import com.dm.quiz.service.IDashboardService;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 首页报表Controller
 *
 * @author Cascade
 * @date 2025-11-22
 */
@RestController
@RequestMapping("/dashboard")
public class DashboardController extends BaseController {

    @Autowired
    private IDashboardService dashboardService;

    /**
     * 获取首页概览统计数据
     */
    @PreAuthorize("@ss.hasPermi('dashboard:overview:query')")
    @GetMapping("/overview")
    public AjaxResult getOverview() {
        OverviewStatisticsDto data = dashboardService.getOverviewStatistics();
        return success(data);
    }

    /**
     * 获取用户活跃度统计数据
     */
    @PreAuthorize("@ss.hasPermi('dashboard:user-activity:query')")
    @GetMapping("/user-activity")
    public AjaxResult getUserActivity() {
        UserActivityDto data = dashboardService.getUserActivity();
        return success(data);
    }

    /**
     * 获取考试数据统计
     */
    @PreAuthorize("@ss.hasPermi('dashboard:exam-statistics:query')")
    @GetMapping("/exam-statistics")
    public AjaxResult getExamStatistics() {
        ExamStatisticsDto data = dashboardService.getExamStatistics();
        return success(data);
    }

    /**
     * 获取题目统计数据
     */
    @PreAuthorize("@ss.hasPermi('dashboard:question-statistics:query')")
    @GetMapping("/question-statistics")
    public AjaxResult getQuestionStatistics() {
        QuestionStatisticsDto data = dashboardService.getQuestionStatistics();
        return success(data);
    }

    /**
     * 获取试卷统计数据
     */
    @PreAuthorize("@ss.hasPermi('dashboard:paper-statistics:query')")
    @GetMapping("/paper-statistics")
    public AjaxResult getPaperStatistics() {
        PaperStatisticsDto data = dashboardService.getPaperStatistics();
        return success(data);
    }

    /**
     * 获取时间分布数据
     */
    @PreAuthorize("@ss.hasPermi('dashboard:time-distribution:query')")
    @GetMapping("/time-distribution")
    public AjaxResult getTimeDistribution() {
        TimeDistributionDto data = dashboardService.getTimeDistribution();
        return success(data);
    }
}
