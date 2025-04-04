package com.ruoyi.web.controller.quiz.admin;

import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson2.JSONObject;
import com.dm.quiz.domain.DamingContentInfo;
import com.dm.quiz.dto.QuestionPageDto;
import com.dm.quiz.mapper.DamingQuestionMapper;
import com.dm.quiz.viewmodel.QuestionInfoContentVM;
import com.dm.quiz.dto.QuestionDto;
import com.dm.quiz.mapper.DamingContentInfoMapper;
import com.dm.quiz.viewmodel.QuestionPageVM;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.dm.quiz.domain.DamingQuestion;
import com.dm.quiz.service.IDamingQuestionService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 题目表Controller
 *
 * @author zww
 * @date 2024-10-08
 */
@RestController
@RequestMapping("/quiz/question")
public class DamingQuestionController extends BaseController {
    @Autowired
    private IDamingQuestionService damingQuestionService;
    @Autowired
    private DamingContentInfoMapper damingContentInfoMapper;
    private DamingQuestionMapper damingQuestionMapper;

    /**
     * 查询题目表列表
     */

    @PreAuthorize("@ss.hasPermi('quiz:question:list')")
    @GetMapping("/list")
    public TableDataInfo list(DamingQuestion damingQuestion) {
        startPage();
        List<DamingQuestion> list = damingQuestionService.selectDamingQuestionList(damingQuestion);
        TableDataInfo dataTable = getDataTable(list);
        // 加工一个题干给用户，否则题目干巴巴的内容很少,还缺一个题目详细信息
        List<QuestionPageVM> questionVOS = list.stream().map(i -> {
            QuestionPageVM questionPageVM = new QuestionPageVM();
            BeanUtils.copyProperties(i, questionPageVM);
            DamingContentInfo damingContentInfo = damingContentInfoMapper.selectDamingContentInfoById(i.getQuestionInfoId());
            String content = damingContentInfo.getContent();
            QuestionInfoContentVM infoContent = JSONObject
                    .parseObject
                            (content, QuestionInfoContentVM.class);
            questionPageVM.setQuestionTitle(infoContent.getQuestionTitle());
            return questionPageVM;
        }).collect(Collectors.toList());
        dataTable.setRows(questionVOS);

        return dataTable;
    }

    /**
     * 导出题目表列表
     */
    @PreAuthorize("@ss.hasPermi('quiz:question:export')")
    @Log(title = "题目表", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DamingQuestion damingQuestion) {
        List<DamingQuestion> list = damingQuestionService.selectDamingQuestionList(damingQuestion);
        ExcelUtil<DamingQuestion> util = new ExcelUtil<DamingQuestion>(DamingQuestion.class);
        util.exportExcel(response, list, "题目表数据");
    }

    /**
     * 获取题目表详细信息
     */
    @PreAuthorize("@ss.hasPermi('quiz:question:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return damingQuestionService.selectDamingQuestionById(id);
    }

    /**
     * 新增题目表
     */
    @PreAuthorize("@ss.hasPermi('quiz:question:add')")
    @Log(title = "题目表", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody QuestionDto questionDto) {
        return toAjax(damingQuestionService.insertDamingQuestion(questionDto));
    }

    /**
     * 修改题目表
     */
    @PreAuthorize("@ss.hasPermi('quiz:question:edit')")
    @Log(title = "题目表", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody QuestionDto questionDto) {
        return toAjax(damingQuestionService.updateDamingQuestion(questionDto));
    }

    /**
     * 删除题目表
     */
    @PreAuthorize("@ss.hasPermi('quiz:question:remove')")
    @Log(title = "题目表", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        int i = damingQuestionService.deleteDamingQuestionByIds(ids);
        if (i==2){
            return error("该试卷下有答题记录，请先删除");
        }
        return success("删除成功");
    }
}
