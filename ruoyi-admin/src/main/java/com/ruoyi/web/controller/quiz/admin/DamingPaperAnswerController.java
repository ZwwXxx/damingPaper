package com.ruoyi.web.controller.quiz.admin;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.common.utils.SecurityUtils;
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
import com.dm.quiz.domain.DamingPaperAnswer;
import com.dm.quiz.service.IDamingPaperAnswerService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 试卷作答情况Controller
 *
 * @author zww
 * @date 2024-10-13
 */
@RestController
@RequestMapping("/quiz/paperAnswer")
public class DamingPaperAnswerController extends BaseController
{
    @Autowired
    private IDamingPaperAnswerService damingPaperAnswerService;

    /**
     * 查询试卷作答情况列表
     */
    @PreAuthorize("@ss.hasPermi('quiz:paperAnswer:list')")
    @GetMapping("/list")
    public TableDataInfo list(DamingPaperAnswer damingPaperAnswer)
    {

        startPage();
        List<DamingPaperAnswer> list = damingPaperAnswerService.selectDamingPaperAnswerList(damingPaperAnswer);
        return getDataTable(list);
    }

    /**
     * 导出试卷作答情况列表
     */
    @PreAuthorize("@ss.hasPermi('quiz:paperAnswer:export')")
    @Log(title = "试卷作答情况", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DamingPaperAnswer damingPaperAnswer)
    {

        List<DamingPaperAnswer> list = damingPaperAnswerService.selectDamingPaperAnswerList(damingPaperAnswer);
        ExcelUtil<DamingPaperAnswer> util = new ExcelUtil<DamingPaperAnswer>(DamingPaperAnswer.class);
        util.exportExcel(response, list, "试卷作答情况数据");
    }

    /**
     * 获取试卷作答情况详细信息
     */
    @PreAuthorize("@ss.hasPermi('quiz:paperAnswer:query')")
    @GetMapping(value = "/{paperAnswerId}")
    public AjaxResult getInfo(@PathVariable("paperAnswerId") Long paperAnswerId)
    {
        return success(damingPaperAnswerService.selectDamingPaperAnswerByPaperAnswerId(paperAnswerId));
    }

    /**
     * 新增试卷作答情况
     */
    @PreAuthorize("@ss.hasPermi('quiz:paperAnswer:add')")
    @Log(title = "试卷作答情况", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DamingPaperAnswer damingPaperAnswer)
    {
        return toAjax(damingPaperAnswerService.insertDamingPaperAnswer(damingPaperAnswer));
    }

    /**
     * 修改试卷作答情况
     */
    @PreAuthorize("@ss.hasPermi('quiz:paperAnswer:edit')")
    @Log(title = "试卷作答情况", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DamingPaperAnswer damingPaperAnswer)
    {
        return toAjax(damingPaperAnswerService.updateDamingPaperAnswer(damingPaperAnswer));
    }

    /**
     * 删除试卷作答情况
     */
    @PreAuthorize("@ss.hasPermi('quiz:paperAnswer:remove')")
    @Log(title = "试卷作答情况", businessType = BusinessType.DELETE)
	@DeleteMapping("/{paperAnswerIds}")
    public AjaxResult remove(@PathVariable Long[] paperAnswerIds)
    {
        return toAjax(damingPaperAnswerService.deleteDamingPaperAnswerByPaperAnswerIds(paperAnswerIds));
    }
}
