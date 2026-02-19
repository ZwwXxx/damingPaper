package com.ruoyi.web.controller.quiz.admin;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.KnowledgePoint;
import com.ruoyi.system.domain.KnowledgeSubject;
import com.ruoyi.system.service.IKnowledgePointService;
import com.ruoyi.system.service.IKnowledgeSubjectService;

/**
 * 后台知识点库管理 Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/quiz/knowledge")
public class KnowledgePointAdminController extends BaseController {

    @Autowired
    private IKnowledgePointService knowledgePointService;

    @Autowired
    private IKnowledgeSubjectService knowledgeSubjectService;

    /**
     * 查询知识点列表（管理端，支持全部状态筛选）
     */
    @PreAuthorize("@ss.hasPermi('knowledge:point:query')")
    @GetMapping("/list")
    public TableDataInfo list(KnowledgePoint knowledgePoint) {
        startPage();
        List<KnowledgePoint> list = knowledgePointService.selectKnowledgePointListForAdmin(knowledgePoint);
        return getDataTable(list);
    }

    /**
     * 获取知识点详情（管理端）
     */
    @PreAuthorize("@ss.hasPermi('knowledge:point:detail')")
    @GetMapping("/{pointId}")
    public AjaxResult getInfo(@PathVariable Long pointId) {
        KnowledgePoint point = knowledgePointService.selectKnowledgePointByPointId(pointId);
        if (point == null) {
            return error("知识点不存在");
        }
        return success(point);
    }

    /**
     * 新增知识点（管理端直接发布，无需审核）
     */
    @PreAuthorize("@ss.hasPermi('knowledge:point:add')")
    @Log(title = "知识点管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody KnowledgePoint knowledgePoint) {
        String username = SecurityUtils.getUsername();
        Long userId = SecurityUtils.getUserId();
        knowledgePoint.setCreateBy(username);
        knowledgePoint.setUpdateBy(username);
        knowledgePoint.setAuthorId(userId);
        knowledgePoint.setAuthorName(username);
        knowledgePoint.setStatus(1);
        knowledgePoint.setAuditStatus(1); // 管理员新增直接通过
        knowledgePoint.setViewCount(0);
        knowledgePoint.setLikeCount(0);
        knowledgePoint.setCollectCount(0);
        knowledgePoint.setCommentCount(0);
        knowledgePoint.setLearnCount(0);
        if (knowledgePoint.getIsRecommend() == null) {
            knowledgePoint.setIsRecommend(0);
        }
        if (knowledgePoint.getIsTop() == null) {
            knowledgePoint.setIsTop(0);
        }
        return toAjax(knowledgePointService.insertKnowledgePoint(knowledgePoint));
    }

    /**
     * 修改知识点
     */
    @PreAuthorize("@ss.hasPermi('knowledge:point:edit')")
    @Log(title = "知识点管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody KnowledgePoint knowledgePoint) {
        knowledgePoint.setUpdateBy(SecurityUtils.getUsername());
        knowledgePoint.setUpdateTime(new Date());
        return toAjax(knowledgePointService.updateKnowledgePoint(knowledgePoint));
    }

    /**
     * 删除知识点
     */
    @PreAuthorize("@ss.hasPermi('knowledge:point:remove')")
    @Log(title = "知识点管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{pointIds}")
    public AjaxResult remove(@PathVariable Long[] pointIds) {
        return toAjax(knowledgePointService.deleteKnowledgePointByPointIds(pointIds));
    }

    /**
     * 审核知识点（通过/拒绝）
     */
    @PreAuthorize("@ss.hasPermi('knowledge:point:audit')")
    @Log(title = "知识点审核", businessType = BusinessType.UPDATE)
    @PutMapping("/audit")
    public AjaxResult audit(@RequestBody KnowledgePoint knowledgePoint) {
        if (knowledgePoint.getPointId() == null) {
            return error("知识点ID不能为空");
        }
        if (knowledgePoint.getAuditStatus() == null) {
            return error("审核状态不能为空");
        }
        knowledgePoint.setUpdateBy(SecurityUtils.getUsername());
        knowledgePoint.setUpdateTime(new Date());
        return toAjax(knowledgePointService.auditKnowledgePoint(knowledgePoint));
    }

    /**
     * 批量审核知识点
     */
    @PreAuthorize("@ss.hasPermi('knowledge:point:audit')")
    @Log(title = "知识点批量审核", businessType = BusinessType.UPDATE)
    @PutMapping("/batchAudit")
    public AjaxResult batchAudit(@RequestBody KnowledgePoint knowledgePoint) {
        if (knowledgePoint.getPointIds() == null || knowledgePoint.getPointIds().length == 0) {
            return error("请选择要审核的知识点");
        }
        if (knowledgePoint.getAuditStatus() == null) {
            return error("审核状态不能为空");
        }
        knowledgePoint.setUpdateBy(SecurityUtils.getUsername());
        knowledgePoint.setUpdateTime(new Date());
        int count = 0;
        for (Long pointId : knowledgePoint.getPointIds()) {
            KnowledgePoint p = new KnowledgePoint();
            p.setPointId(pointId);
            p.setAuditStatus(knowledgePoint.getAuditStatus());
            p.setAuditRemark(knowledgePoint.getAuditRemark());
            p.setUpdateBy(knowledgePoint.getUpdateBy());
            p.setUpdateTime(knowledgePoint.getUpdateTime());
            count += knowledgePointService.auditKnowledgePoint(p);
        }
        return toAjax(count);
    }

    /**
     * 获取知识点科目列表（用于下拉选择）
     */
    @PreAuthorize("@ss.hasPermi('knowledge:point:query')")
    @GetMapping("/subjects")
    public AjaxResult listSubjects() {
        KnowledgeSubject query = new KnowledgeSubject();
        query.setStatus(1);
        List<KnowledgeSubject> list = knowledgeSubjectService.selectKnowledgeSubjectList(query);
        return success(list);
    }
}
