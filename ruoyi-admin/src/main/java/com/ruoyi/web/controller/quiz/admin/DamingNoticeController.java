package com.ruoyi.web.controller.quiz.admin;

import com.dm.quiz.domain.DamingNotice;
import com.dm.quiz.service.IDamingNoticeService;
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
 * 公告管理Controller
 *
 * @author daming
 * @date 2025-11-23
 */
@RestController
@RequestMapping("/quiz/notice")
public class DamingNoticeController extends BaseController {

    @Autowired
    private IDamingNoticeService damingNoticeService;

    /**
     * 查询公告列表
     */
    @PreAuthorize("@ss.hasPermi('quiz:notice:list')")
    @GetMapping("/list")
    public TableDataInfo list(DamingNotice damingNotice) {
        startPage();
        List<DamingNotice> list = damingNoticeService.selectDamingNoticeList(damingNotice);
        return getDataTable(list);
    }

    /**
     * 导出公告列表
     */
    @PreAuthorize("@ss.hasPermi('quiz:notice:export')")
    @Log(title = "公告管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DamingNotice damingNotice) {
        List<DamingNotice> list = damingNoticeService.selectDamingNoticeList(damingNotice);
        ExcelUtil<DamingNotice> util = new ExcelUtil<>(DamingNotice.class);
        util.exportExcel(response, list, "公告数据");
    }

    /**
     * 获取公告详细信息
     */
    @PreAuthorize("@ss.hasPermi('quiz:notice:query')")
    @GetMapping("/{noticeId}")
    public AjaxResult getInfo(@PathVariable Long noticeId) {
        return success(damingNoticeService.selectDamingNoticeByNoticeId(noticeId));
    }

    /**
     * 新增公告
     */
    @PreAuthorize("@ss.hasPermi('quiz:notice:add')")
    @Log(title = "公告管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DamingNotice damingNotice) {
        damingNotice.setCreateUser(SecurityUtils.getUsername());
        return toAjax(damingNoticeService.insertDamingNotice(damingNotice));
    }

    /**
     * 修改公告
     */
    @PreAuthorize("@ss.hasPermi('quiz:notice:edit')")
    @Log(title = "公告管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DamingNotice damingNotice) {
        damingNotice.setUpdateUser(SecurityUtils.getUsername());
        return toAjax(damingNoticeService.updateDamingNotice(damingNotice));
    }

    /**
     * 删除公告
     */
    @PreAuthorize("@ss.hasPermi('quiz:notice:remove')")
    @Log(title = "公告管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{noticeIds}")
    public AjaxResult remove(@PathVariable Long[] noticeIds) {
        return toAjax(damingNoticeService.deleteDamingNoticeByNoticeIds(noticeIds));
    }

    /**
     * 发布公告
     */
    @PreAuthorize("@ss.hasPermi('quiz:notice:publish')")
    @Log(title = "发布公告", businessType = BusinessType.UPDATE)
    @PutMapping("/publish/{noticeId}")
    public AjaxResult publish(@PathVariable Long noticeId) {
        return toAjax(damingNoticeService.publishNotice(noticeId));
    }

    /**
     * 取消发布公告
     */
    @PreAuthorize("@ss.hasPermi('quiz:notice:unpublish')")
    @Log(title = "取消发布公告", businessType = BusinessType.UPDATE)
    @PutMapping("/unpublish/{noticeId}")
    public AjaxResult unpublish(@PathVariable Long noticeId) {
        return toAjax(damingNoticeService.unpublishNotice(noticeId));
    }
}
