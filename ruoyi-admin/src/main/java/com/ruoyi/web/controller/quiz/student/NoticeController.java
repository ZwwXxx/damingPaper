package com.ruoyi.web.controller.quiz.student;

import com.dm.quiz.domain.DamingNotice;
import com.dm.quiz.service.IDamingNoticeService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 前台公告Controller
 *
 * @author daming
 * @date 2025-11-23
 */
@RestController
@RequestMapping("/student/notice")
public class NoticeController extends BaseController {

    @Autowired
    private IDamingNoticeService damingNoticeService;

    /**
     * 查询前台公告列表（只查询已发布的）
     */
    @GetMapping("/list")
    public TableDataInfo list(DamingNotice damingNotice) {
        startPage();
        List<DamingNotice> list = damingNoticeService.selectPublishedNoticeList(damingNotice);
        return getDataTable(list);
    }

    /**
     * 获取公告详细信息
     */
    @GetMapping("/{noticeId}")
    public AjaxResult getInfo(@PathVariable Long noticeId) {
        // 增加浏览次数
        damingNoticeService.incrementViewCount(noticeId);
        return success(damingNoticeService.selectDamingNoticeByNoticeId(noticeId));
    }

    /**
     * 获取最新公告（不分页，取前5条）
     */
    @GetMapping("/latest")
    public AjaxResult getLatest() {
        DamingNotice damingNotice = new DamingNotice();
        List<DamingNotice> list = damingNoticeService.selectPublishedNoticeList(damingNotice);
        // 只返回前5条
        if (list.size() > 5) {
            list = list.subList(0, 5);
        }
        return success(list);
    }
}
