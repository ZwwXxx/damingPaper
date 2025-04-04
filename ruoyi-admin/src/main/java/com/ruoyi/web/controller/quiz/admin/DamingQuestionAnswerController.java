package com.ruoyi.web.controller.quiz.admin;

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
import com.dm.quiz.domain.DamingQuestionAnswer;
import com.dm.quiz.service.IDamingQuestionAnswerService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 题目回答情况表Controller
 *
 * @author zww
 * @date 2024-10-13
 */
@RestController
@RequestMapping("/quiz/questionAnswer")
public class DamingQuestionAnswerController extends BaseController
{
    @Autowired
    private IDamingQuestionAnswerService damingQuestionAnswerService;

    /**
     * 查询题目回答情况表列表
     */
    @PreAuthorize("@ss.hasPermi('quiz:questionAnswer:list')")
    @GetMapping("/list")
    public TableDataInfo list(DamingQuestionAnswer damingQuestionAnswer)
    {
        startPage();
        List<DamingQuestionAnswer> list = damingQuestionAnswerService.selectDamingQuestionAnswerList(damingQuestionAnswer);
        return getDataTable(list);
    }

    /**
     * 导出题目回答情况表列表
     */
    @PreAuthorize("@ss.hasPermi('quiz:questionAnswer:export')")
    @Log(title = "题目回答情况表", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DamingQuestionAnswer damingQuestionAnswer)
    {
        List<DamingQuestionAnswer> list = damingQuestionAnswerService.selectDamingQuestionAnswerList(damingQuestionAnswer);
        ExcelUtil<DamingQuestionAnswer> util = new ExcelUtil<DamingQuestionAnswer>(DamingQuestionAnswer.class);
        util.exportExcel(response, list, "题目回答情况表数据");
    }

    /**
     * 获取题目回答情况表详细信息
     */
    @PreAuthorize("@ss.hasPermi('quiz:questionAnswer:query')")
    @GetMapping(value = "/{answerId}")
    public AjaxResult getInfo(@PathVariable("answerId") Long answerId)
    {
        return success(damingQuestionAnswerService.selectDamingQuestionAnswerByAnswerId(answerId));
    }

    /**
     * 新增题目回答情况表
     */
    @PreAuthorize("@ss.hasPermi('quiz:questionAnswer:add')")
    @Log(title = "题目回答情况表", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DamingQuestionAnswer damingQuestionAnswer)
    {
        return toAjax(damingQuestionAnswerService.insertDamingQuestionAnswer(damingQuestionAnswer));
    }

    /**
     * 修改题目回答情况表
     */
    @PreAuthorize("@ss.hasPermi('quiz:questionAnswer:edit')")
    @Log(title = "题目回答情况表", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DamingQuestionAnswer damingQuestionAnswer)
    {
        return toAjax(damingQuestionAnswerService.updateDamingQuestionAnswer(damingQuestionAnswer));
    }

    /**
     * 删除题目回答情况表
     */
    @PreAuthorize("@ss.hasPermi('quiz:questionAnswer:remove')")
    @Log(title = "题目回答情况表", businessType = BusinessType.DELETE)
	@DeleteMapping("/{answerIds}")
    public AjaxResult remove(@PathVariable Long[] answerIds)
    {
        return toAjax(damingQuestionAnswerService.deleteDamingQuestionAnswerByAnswerIds(answerIds));
    }
}
