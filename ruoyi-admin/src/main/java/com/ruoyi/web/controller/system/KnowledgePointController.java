package com.ruoyi.web.controller.system;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.KnowledgePoint;
import com.ruoyi.system.service.IKnowledgePointService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 知识点管理Controller
 * 
 * @author ruoyi
 * @date 2025-11-28
 */
@RestController
@RequestMapping("/system/knowledge/point")
public class KnowledgePointController extends BaseController
{
    @Autowired
    private IKnowledgePointService knowledgePointService;

    /**
     * 查询知识点列表
     */
    @PreAuthorize("@ss.hasPermi('knowledge:point:list')")
    @GetMapping("/list")
    public TableDataInfo list(KnowledgePoint knowledgePoint)
    {
        startPage();
        List<KnowledgePoint> list = knowledgePointService.selectKnowledgePointList(knowledgePoint);
        return getDataTable(list);
    }

    /**
     * 导出知识点列表
     */
    @PreAuthorize("@ss.hasPermi('knowledge:point:export')")
    @Log(title = "知识点", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, KnowledgePoint knowledgePoint)
    {
        List<KnowledgePoint> list = knowledgePointService.selectKnowledgePointList(knowledgePoint);
        ExcelUtil<KnowledgePoint> util = new ExcelUtil<KnowledgePoint>(KnowledgePoint.class);
        util.exportExcel(response, list, "知识点数据");
    }

    /**
     * 获取知识点详细信息
     */
    @PreAuthorize("@ss.hasPermi('knowledge:point:query')")
    @GetMapping(value = "/{pointId}")
    public AjaxResult getInfo(@PathVariable("pointId") Long pointId)
    {
        return success(knowledgePointService.selectKnowledgePointByPointId(pointId));
    }

    /**
     * 新增知识点
     */
    @PreAuthorize("@ss.hasPermi('knowledge:point:add')")
    @Log(title = "知识点", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody KnowledgePoint knowledgePoint)
    {
        knowledgePoint.setCreateBy(getUsername());
        return toAjax(knowledgePointService.insertKnowledgePoint(knowledgePoint));
    }

    /**
     * 修改知识点
     */
    @PreAuthorize("@ss.hasPermi('knowledge:point:edit')")
    @Log(title = "知识点", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody KnowledgePoint knowledgePoint)
    {
        knowledgePoint.setUpdateBy(getUsername());
        return toAjax(knowledgePointService.updateKnowledgePoint(knowledgePoint));
    }

    /**
     * 删除知识点
     */
    @PreAuthorize("@ss.hasPermi('knowledge:point:remove')")
    @Log(title = "知识点", businessType = BusinessType.DELETE)
	@DeleteMapping("/{pointIds}")
    public AjaxResult remove(@PathVariable Long[] pointIds)
    {
        return toAjax(knowledgePointService.deleteKnowledgePointByPointIds(pointIds));
    }

    /**
     * 审核知识点
     */
    @PreAuthorize("@ss.hasPermi('knowledge:point:audit')")
    @Log(title = "知识点审核", businessType = BusinessType.UPDATE)
    @PutMapping("/audit/{pointId}")
    public AjaxResult audit(@PathVariable Long pointId, @RequestBody KnowledgePoint knowledgePoint)
    {
        knowledgePoint.setPointId(pointId);
        knowledgePoint.setUpdateBy(getUsername());
        return toAjax(knowledgePointService.auditKnowledgePoint(knowledgePoint));
    }

    /**
     * 批量审核知识点
     */
    @PreAuthorize("@ss.hasPermi('knowledge:point:audit')")
    @Log(title = "知识点批量审核", businessType = BusinessType.UPDATE)
    @PutMapping("/audit/batch")
    public AjaxResult batchAudit(@RequestBody KnowledgePoint knowledgePoint)
    {
        knowledgePoint.setUpdateBy(getUsername());
        return toAjax(knowledgePointService.batchAuditKnowledgePoint(knowledgePoint));
    }

    /**
     * 发布/下架知识点
     */
    @PreAuthorize("@ss.hasPermi('knowledge:point:edit')")
    @Log(title = "知识点状态切换", businessType = BusinessType.UPDATE)
    @PutMapping("/status/{pointId}")
    public AjaxResult changeStatus(@PathVariable Long pointId, @RequestBody KnowledgePoint knowledgePoint)
    {
        knowledgePoint.setPointId(pointId);
        knowledgePoint.setUpdateBy(getUsername());
        return toAjax(knowledgePointService.updateKnowledgePointStatus(knowledgePoint));
    }
}
