package com.ruoyi.web.controller.quiz.admin;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.dm.quiz.dto.AutoAssemblePaperRequest;
import com.dm.quiz.dto.PaperDto;
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
import com.dm.quiz.domain.DamingPaper;
import com.dm.quiz.service.IDamingPaperService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 试卷Controller
 *
 * @author zww
 * @date 2024-10-10
 */
@RestController
@RequestMapping("/quiz/paper")
public class DamingPaperController extends BaseController
{
    @Autowired
    private IDamingPaperService damingPaperService;

    /**
     * 查询试卷列表
     */
    @PreAuthorize("@ss.hasPermi('quiz:paper:list')")
    @GetMapping("/list")
    public TableDataInfo list(DamingPaper damingPaper)
    {
        startPage();
        List<DamingPaper> list = damingPaperService.selectDamingPaperList(damingPaper);
        return getDataTable(list);
    }

    /**
     * 导出试卷列表
     */
    @PreAuthorize("@ss.hasPermi('quiz:paper:export')")
    @Log(title = "试卷", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DamingPaper damingPaper)
    {
        List<DamingPaper> list = damingPaperService.selectDamingPaperList(damingPaper);
        ExcelUtil<DamingPaper> util = new ExcelUtil<DamingPaper>(DamingPaper.class);
        util.exportExcel(response, list, "试卷数据");
    }

    /**
     * 获取试卷详细信息
     */
    @PreAuthorize("@ss.hasPermi('quiz:paper:query')")
    @GetMapping(value = "/{paperId}")
    public AjaxResult getInfo(@PathVariable("paperId") Long paperId)
    {
        return success(damingPaperService.paperIdtoDto(paperId));
    }

    /**
     * 新增试卷
     */
    @PreAuthorize("@ss.hasPermi('quiz:paper:add')")
    @Log(title = "试卷", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody PaperDto paperDto)
    {
        DamingPaper damingPaper = damingPaperService.insertDamingPaper(paperDto);
        if (damingPaper==null){
            return error("参数填写错误");
        }
        //包含分数和新题型，有顺序的
        PaperDto paperIdtoDto = damingPaperService.paperIdtoDto(damingPaper.getPaperId());
        return success(paperIdtoDto);
    }

    /**
     * 自动组卷预览
     */
    @PreAuthorize("@ss.hasPermi('quiz:paper:add')")
    @Log(title = "试卷", businessType = BusinessType.OTHER)
    @PostMapping("/auto-compose")
    public AjaxResult autoCompose(@RequestBody AutoAssemblePaperRequest request) {
        return success(damingPaperService.autoAssemblePaper(request));
    }

    /**
     * 按日期范围自动组卷预览（将时间范围内的所有题型题目自动入卷）
     */
    @PreAuthorize("@ss.hasPermi('quiz:paper:add')")
    @Log(title = "试卷", businessType = BusinessType.OTHER)
    @PostMapping("/auto-compose-by-date")
    public AjaxResult autoComposeByDate(@RequestBody AutoAssemblePaperRequest request) {
        return success(damingPaperService.autoAssemblePaperByDate(request));
    }

    /**
     * 修改试卷
     */
    @PreAuthorize("@ss.hasPermi('quiz:paper:edit')")
    @Log(title = "试卷", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody PaperDto paperDto)
    {
        return toAjax(damingPaperService.updateDamingPaper(paperDto));
    }

    /**
     * 删除试卷
     */
    @PreAuthorize("@ss.hasPermi('quiz:paper:remove')")
    @Log(title = "试卷", businessType = BusinessType.DELETE)
	@DeleteMapping("/{paperIds}")
    public AjaxResult remove(@PathVariable Long[] paperIds)
    {
        int i = damingPaperService.deleteDamingPaperByPaperIds(paperIds);
        if (i==2){
           return error("该试卷下有答卷或答题记录，请先删除");
        }

        return success("删除成功");
    }
}
