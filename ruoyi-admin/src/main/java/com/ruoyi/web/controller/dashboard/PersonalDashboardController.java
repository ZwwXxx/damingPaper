package com.ruoyi.web.controller.dashboard;

import com.dm.quiz.dto.dashboard.front.*;
import com.dm.quiz.service.IPersonalDashboardService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 个人学习报表Controller
 * 
 * @author daming
 */
@RestController
@RequestMapping("/api/personal/dashboard")
public class PersonalDashboardController extends BaseController {

    @Autowired
    private IPersonalDashboardService personalDashboardService;

    /**
     * 获取个人学习概览
     */
    @GetMapping("/overview")
    public AjaxResult getOverview() {
        String username = SecurityUtils.getUsername();
        System.out.println("=== 当前登录用户: " + username);
        PersonalOverviewDto data = personalDashboardService.getOverview(username);
        return AjaxResult.success(data);
    }

    /**
     * 获取个人考试趋势
     */
    @GetMapping("/exam-trend")
    public AjaxResult getExamTrend(
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(required = false) String paperName) {
        String username = SecurityUtils.getUsername();
        List<ExamTrendDto> data = personalDashboardService.getExamTrend(username, limit, paperName);
        return AjaxResult.success(data);
    }

    /**
     * 获取个人所有考试过的试卷列表
     */
    @GetMapping("/paper-list")
    public AjaxResult getPaperList() {
        String username = SecurityUtils.getUsername();
        List<String> data = personalDashboardService.getPaperList(username);
        return AjaxResult.success(data);
    }

    /**
     * 获取个人各科目成绩统计
     */
    @GetMapping("/subject-score")
    public AjaxResult getSubjectScores() {
        String username = SecurityUtils.getUsername();
        List<SubjectScoreDto> data = personalDashboardService.getSubjectScores(username);
        return AjaxResult.success(data);
    }

    /**
     * 获取个人错题统计
     */
    @GetMapping("/wrong-question")
    public AjaxResult getWrongQuestionStat() {
        String username = SecurityUtils.getUsername();
        WrongQuestionStatDto data = personalDashboardService.getWrongQuestionStat(username);
        return AjaxResult.success(data);
    }

    /**
     * 获取个人学习时间分布
     */
    @GetMapping("/study-time")
    public AjaxResult getStudyTimeDistribution(@RequestParam(defaultValue = "30") Integer days) {
        String username = SecurityUtils.getUsername();
        StudyTimeDto data = personalDashboardService.getStudyTimeDistribution(username, days);
        return AjaxResult.success(data);
    }
}
