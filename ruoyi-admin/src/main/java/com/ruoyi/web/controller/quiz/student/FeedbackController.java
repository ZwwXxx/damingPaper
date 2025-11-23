package com.ruoyi.web.controller.quiz.student;

import com.dm.quiz.domain.DamingFeedback;
import com.dm.quiz.service.IDamingFeedbackService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 前台反馈Controller
 *
 * @author daming
 * @date 2025-11-23
 */
@RestController
@RequestMapping("/student/feedback")
public class FeedbackController extends BaseController {

    @Autowired
    private IDamingFeedbackService damingFeedbackService;

    /**
     * 查询用户自己的反馈列表
     */
    @GetMapping("/my")
    public TableDataInfo myList() {
        String userId = SecurityUtils.getUsername();
        startPage();
        List<DamingFeedback> list = damingFeedbackService.selectUserFeedbackList(userId);
        return getDataTable(list);
    }

    /**
     * 获取反馈详细信息
     */
    @GetMapping("/{feedbackId}")
    public AjaxResult getInfo(@PathVariable Long feedbackId) {
        DamingFeedback feedback = damingFeedbackService.selectDamingFeedbackByFeedbackId(feedbackId);
        // 验证是否是用户自己的反馈
        if (feedback != null && !feedback.getUserId().equals(SecurityUtils.getUsername())) {
            return error("无权查看此反馈");
        }
        return success(feedback);
    }

    /**
     * 提交反馈
     */
    @PostMapping
    public AjaxResult add(@RequestBody DamingFeedback damingFeedback) {
        String userId = SecurityUtils.getUsername();
        String userName = userId; // 默认使用userId
        
        // 获取前台用户信息（使用DamingUser）
        if (SecurityUtils.getLoginUser() != null 
            && SecurityUtils.getLoginUser().getDamingUser() != null) {
            userName = SecurityUtils.getLoginUser().getDamingUser().getNickName();
            if (userName == null || userName.trim().isEmpty()) {
                userName = userId;
            }
        }
        
        damingFeedback.setUserId(userId);
        damingFeedback.setUserName(userName);
        damingFeedback.setCreateUser(userId);
        damingFeedback.setStatus(0); // 待处理
        
        return toAjax(damingFeedbackService.insertDamingFeedback(damingFeedback));
    }

    /**
     * 删除自己的反馈（仅待处理状态可删除）
     */
    @DeleteMapping("/{feedbackId}")
    public AjaxResult remove(@PathVariable Long feedbackId) {
        DamingFeedback feedback = damingFeedbackService.selectDamingFeedbackByFeedbackId(feedbackId);
        
        // 验证是否是用户自己的反馈
        if (feedback == null || !feedback.getUserId().equals(SecurityUtils.getUsername())) {
            return error("无权删除此反馈");
        }
        
        // 只有待处理状态的反馈可以删除
        if (feedback.getStatus() != 0) {
            return error("只能删除待处理状态的反馈");
        }
        
        return toAjax(damingFeedbackService.deleteDamingFeedbackByFeedbackId(feedbackId));
    }
}
