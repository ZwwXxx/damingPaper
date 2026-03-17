package com.ruoyi.web.controller.quiz.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dm.quiz.domain.PracticeColumn;
import com.dm.quiz.service.IPracticeColumnService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.SecurityUtils;

/**
 * 后台管理：专项刷题栏目（用于创建栏目、配置是否参与刷题、绑定题目）
 */
@RestController
@RequestMapping("/quiz/practice/column")
public class PracticeColumnAdminController extends BaseController {

    @Autowired
    private IPracticeColumnService practiceColumnService;

    @PreAuthorize("@ss.hasPermi('quiz:practice:column:list')")
    @GetMapping("/list")
    public TableDataInfo list(PracticeColumn query) {
        startPage();
        List<PracticeColumn> list = practiceColumnService.selectList(query);
        return getDataTable(list);
    }

    /**
     * 下拉选项：一级分组（章节/大类）
     */
    @PreAuthorize("@ss.hasAnyPermi('quiz:practice:column:list,quiz:question:query,quiz:question:edit,quiz:question:add')")
    @GetMapping("/groupOptions")
    public AjaxResult groupOptions(@RequestParam(value = "subjectId", required = false) Integer subjectId) {
        return AjaxResult.success(practiceColumnService.selectGroupOptions(subjectId));
    }

    /**
     * 下拉选项：二级栏目列表（可按 subjectId/groupName 过滤）
     */
    @PreAuthorize("@ss.hasAnyPermi('quiz:practice:column:list,quiz:question:query,quiz:question:edit,quiz:question:add')")
    @GetMapping("/options")
    public AjaxResult options(@RequestParam(value = "subjectId", required = false) Integer subjectId,
                              @RequestParam(value = "groupName", required = false) String groupName) {
        PracticeColumn query = new PracticeColumn();
        if (subjectId != null) {
            query.setSubjectId(subjectId);
        }
        if (groupName != null && !groupName.trim().isEmpty()) {
            query.setGroupName(groupName.trim());
        }
        List<PracticeColumn> list = practiceColumnService.selectList(query);
        return AjaxResult.success(list);
    }

    @PreAuthorize("@ss.hasPermi('quiz:practice:column:query')")
    @GetMapping("/{columnId}")
    public AjaxResult get(@PathVariable Long columnId) {
        return AjaxResult.success(practiceColumnService.selectById(columnId));
    }

    /**
     * 查询栏目已绑定的题目ID列表（按 sort_order）
     *
     * 说明：
     * - 页面“配置题目”按钮使用的是权限标识 quiz:practice:column:bind
     * - 若这里仅校验 quiz:practice:column:query，则只有拥有查询权限的角色才能回显
     *   已绑定题目，容易出现“能打开配置弹窗但看不到已选题”的现象
     * - 因此放宽为：拥有“查询”或“绑定”任一权限即可
     */
    @PreAuthorize("@ss.hasPermi('quiz:practice:column:query') or @ss.hasPermi('quiz:practice:column:bind')")
    @GetMapping("/{columnId}/questions")
    public AjaxResult getQuestions(@PathVariable Long columnId) {
        return AjaxResult.success(practiceColumnService.selectQuestionIdsByColumnId(columnId));
    }

    @PreAuthorize("@ss.hasPermi('quiz:practice:column:add')")
    @PostMapping
    public AjaxResult add(@RequestBody PracticeColumn column) {
        String username = SecurityUtils.getUsername();
        column.setCreateBy(username);
        column.setUpdateBy(username);
        if (column.getEnablePractice() == null) {
            column.setEnablePractice(1);
        }
        if (column.getGroupSort() == null) {
            column.setGroupSort(0);
        }
        if (column.getSortOrder() == null) {
            column.setSortOrder(0);
        }
        return toAjax(practiceColumnService.insert(column));
    }

    @PreAuthorize("@ss.hasPermi('quiz:practice:column:edit')")
    @PutMapping
    public AjaxResult edit(@RequestBody PracticeColumn column) {
        column.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(practiceColumnService.update(column));
    }

    @PreAuthorize("@ss.hasPermi('quiz:practice:column:remove')")
    @DeleteMapping("/{columnId}")
    public AjaxResult remove(@PathVariable Long columnId) {
        return toAjax(practiceColumnService.deleteById(columnId));
    }

    /**
     * 绑定栏目题目（全量覆盖）
     * body: [1,2,3,...] 题目ID数组
     */
    @PreAuthorize("@ss.hasPermi('quiz:practice:column:bind')")
    @PostMapping("/{columnId}/questions")
    public AjaxResult saveQuestions(@PathVariable Long columnId, @RequestBody List<Long> questionIds) {
        practiceColumnService.saveColumnQuestions(columnId, questionIds);
        return success("保存成功");
    }
}

