package com.ruoyi.web.controller.quiz.admin;

import com.dm.quiz.domain.DamingSubject;
import com.dm.quiz.service.IDamingSubjectService;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 科目管理
 */
@RestController
@RequestMapping("/quiz/subject")
public class DamingSubjectController extends BaseController {

    @Autowired
    private IDamingSubjectService damingSubjectService;

    @PreAuthorize("@ss.hasPermi('quiz:subject:list')")
    @GetMapping("/list")
    public TableDataInfo list(DamingSubject damingSubject) {
        startPage();
        List<DamingSubject> list = damingSubjectService.selectDamingSubjectList(damingSubject);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('quiz:subject:export')")
    @Log(title = "科目管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DamingSubject damingSubject) {
        List<DamingSubject> list = damingSubjectService.selectDamingSubjectList(damingSubject);
        ExcelUtil<DamingSubject> util = new ExcelUtil<>(DamingSubject.class);
        util.exportExcel(response, list, "科目数据");
    }

    @PreAuthorize("@ss.hasPermi('quiz:subject:query')")
    @GetMapping("/{subjectId}")
    public AjaxResult getInfo(@PathVariable Integer subjectId) {
        return success(damingSubjectService.selectDamingSubjectBySubjectId(subjectId));
    }

    @PreAuthorize("@ss.hasPermi('quiz:subject:add')")
    @Log(title = "科目管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DamingSubject damingSubject) {
        return toAjax(damingSubjectService.insertDamingSubject(damingSubject));
    }

    @PreAuthorize("@ss.hasPermi('quiz:subject:edit')")
    @Log(title = "科目管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DamingSubject damingSubject) {
        return toAjax(damingSubjectService.updateDamingSubject(damingSubject));
    }

    @PreAuthorize("@ss.hasPermi('quiz:subject:remove')")
    @Log(title = "科目管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{subjectIds}")
    public AjaxResult remove(@PathVariable Integer[] subjectIds) {
        return toAjax(damingSubjectService.deleteDamingSubjectBySubjectIds(subjectIds));
    }

    /**
     * 下拉选项
     */
    @GetMapping("/options")
    public AjaxResult options() {
        return success(damingSubjectService.selectActiveSubjects());
    }
}

