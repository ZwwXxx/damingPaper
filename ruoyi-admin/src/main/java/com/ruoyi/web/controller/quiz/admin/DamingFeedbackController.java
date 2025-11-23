package com.ruoyi.web.controller.quiz.admin;

import com.dm.quiz.domain.DamingFeedback;
import com.dm.quiz.service.IDamingFeedbackService;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 反馈管理Controller
 *
 * @author daming
 * @date 2025-11-23
 */
@RestController
@RequestMapping("/quiz/feedback")
public class DamingFeedbackController extends BaseController {

    @Autowired
    private IDamingFeedbackService damingFeedbackService;

    /**
     * 查询反馈列表
     */
    @PreAuthorize("@ss.hasPermi('quiz:feedback:list')")
    @GetMapping("/list")
    public TableDataInfo list(DamingFeedback damingFeedback) {
        startPage();
        List<DamingFeedback> list = damingFeedbackService.selectDamingFeedbackList(damingFeedback);
        return getDataTable(list);
    }

    /**
     * 导出反馈列表
     */
    @PreAuthorize("@ss.hasPermi('quiz:feedback:export')")
    @Log(title = "反馈管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DamingFeedback damingFeedback) {
        List<DamingFeedback> list = damingFeedbackService.selectDamingFeedbackList(damingFeedback);
        ExcelUtil<DamingFeedback> util = new ExcelUtil<>(DamingFeedback.class);
        util.exportExcel(response, list, "反馈数据");
    }

    /**
     * 获取反馈详细信息
     */
    @PreAuthorize("@ss.hasPermi('quiz:feedback:query')")
    @GetMapping("/{feedbackId}")
    public AjaxResult getInfo(@PathVariable Long feedbackId) {
        return success(damingFeedbackService.selectDamingFeedbackByFeedbackId(feedbackId));
    }

    /**
     * 新增反馈
     */
    @PreAuthorize("@ss.hasPermi('quiz:feedback:add')")
    @Log(title = "反馈管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DamingFeedback damingFeedback) {
        damingFeedback.setCreateUser(SecurityUtils.getUsername());
        return toAjax(damingFeedbackService.insertDamingFeedback(damingFeedback));
    }

    /**
     * 修改反馈
     */
    @PreAuthorize("@ss.hasPermi('quiz:feedback:edit')")
    @Log(title = "反馈管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DamingFeedback damingFeedback) {
        damingFeedback.setUpdateUser(SecurityUtils.getUsername());
        return toAjax(damingFeedbackService.updateDamingFeedback(damingFeedback));
    }

    /**
     * 删除反馈
     */
    @PreAuthorize("@ss.hasPermi('quiz:feedback:remove')")
    @Log(title = "反馈管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{feedbackIds}")
    public AjaxResult remove(@PathVariable Long[] feedbackIds) {
        return toAjax(damingFeedbackService.deleteDamingFeedbackByFeedbackIds(feedbackIds));
    }

    /**
     * 处理反馈
     */
    @PreAuthorize("@ss.hasPermi('quiz:feedback:handle')")
    @Log(title = "处理反馈", businessType = BusinessType.UPDATE)
    @PutMapping("/handle")
    public AjaxResult handle(@RequestBody DamingFeedback damingFeedback) {
        String handler = SecurityUtils.getUsername();
        return toAjax(damingFeedbackService.handleFeedback(
                damingFeedback.getFeedbackId(),
                damingFeedback.getStatus(),
                damingFeedback.getReplyContent(),
                handler
        ));
    }

    /**
     * 统计反馈数量（按状态）
     */
    @PreAuthorize("@ss.hasPermi('quiz:feedback:list')")
    @GetMapping("/stats/status")
    public AjaxResult countByStatus() {
        return success(damingFeedbackService.countByStatus());
    }

    /**
     * 统计反馈数量（按类型）
     */
    @PreAuthorize("@ss.hasPermi('quiz:feedback:list')")
    @GetMapping("/stats/type")
    public AjaxResult countByType() {
        return success(damingFeedbackService.countByType());
    }
}
