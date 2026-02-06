package com.ruoyi.web.controller.quiz.student;

import com.dm.quiz.domain.DamingNotice;
import com.dm.quiz.service.IDamingNoticeReadService;
import com.dm.quiz.service.IDamingNoticeService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Autowired
    private IDamingNoticeReadService damingNoticeReadService;

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
        
        // 标记为已读
        try {
            Long userId = SecurityUtils.getUserId();
            if (userId != null) {
                damingNoticeReadService.markNoticeAsRead(userId, noticeId);
            }
        } catch (Exception e) {
            // 用户未登录，忽略
        }
        
        return success(damingNoticeService.selectDamingNoticeByNoticeId(noticeId));
    }

    /**
     * 获取最新公告（不分页，取前5条）
     * 返回公告列表和未读状态
     */
    @GetMapping("/latest")
    public AjaxResult getLatest() {
        DamingNotice damingNotice = new DamingNotice();
        List<DamingNotice> list = damingNoticeService.selectPublishedNoticeList(damingNotice);
        // 只返回前5条
        if (list.size() > 5) {
            list = list.subList(0, 5);
        }
        
        // 获取用户ID（未登录也允许访问）
        Long userId = null;
        try {
            userId = SecurityUtils.getUserId();
        } catch (Exception e) {
            // 未登录用户，忽略
        }
        Map<String, Object> result = new HashMap<>();
        result.put("notices", list);
        
        if (userId != null) {
            // 获取用户已读的公告ID列表
            List<Long> readNoticeIds = damingNoticeReadService.getReadNoticeIds(userId);
            // 获取所有公告ID
            List<Long> allNoticeIds = list.stream()
                    .map(DamingNotice::getNoticeId)
                    .collect(Collectors.toList());
            // 计算未读公告ID列表
            List<Long> unreadNoticeIds = allNoticeIds.stream()
                    .filter(id -> !readNoticeIds.contains(id))
                    .collect(Collectors.toList());
            
            result.put("unreadNoticeIds", unreadNoticeIds);
            result.put("hasUnread", !unreadNoticeIds.isEmpty());
        } else {
            // 未登录，所有公告都视为未读
            List<Long> allNoticeIds = list.stream()
                    .map(DamingNotice::getNoticeId)
                    .collect(Collectors.toList());
            result.put("unreadNoticeIds", allNoticeIds);
            result.put("hasUnread", !list.isEmpty());
        }
        
        return success(result);
    }

    /**
     * 标记公告为已读
     */
    @PostMapping("/read/{noticeId}")
    public AjaxResult markAsRead(@PathVariable Long noticeId) {
        Long userId;
        try {
            userId = SecurityUtils.getUserId();
        } catch (Exception e) {
            return error("用户未登录");
        }
        
        int result = damingNoticeReadService.markNoticeAsRead(userId, noticeId);
        return result > 0 ? success("标记成功") : success("已标记");
    }

    /**
     * 批量标记公告为已读
     */
    @PostMapping("/read/batch")
    public AjaxResult batchMarkAsRead(@RequestBody List<Long> noticeIds) {
        Long userId;
        try {
            userId = SecurityUtils.getUserId();
        } catch (Exception e) {
            return error("用户未登录");
        }
        
        int result = damingNoticeReadService.batchMarkNoticeAsRead(userId, noticeIds);
        return success("成功标记 " + result + " 条公告");
    }
}
