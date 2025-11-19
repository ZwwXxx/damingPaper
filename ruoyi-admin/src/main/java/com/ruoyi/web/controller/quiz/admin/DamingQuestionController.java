package com.ruoyi.web.controller.quiz.admin;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson2.JSONObject;
import com.dm.quiz.domain.DamingContentInfo;
import com.dm.quiz.viewmodel.QuestionInfoContentVM;
import com.dm.quiz.dto.QuestionDto;
import com.dm.quiz.mapper.DamingContentInfoMapper;
import com.dm.quiz.viewmodel.QuestionExportVO;
import com.dm.quiz.viewmodel.QuestionPageVM;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
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

    /**
     * 查询题目表列表
     */
    @PreAuthorize("@ss.hasPermi('quiz:question:list')")
    @GetMapping("/list")
    public TableDataInfo list(DamingQuestion damingQuestion) {
        startPage();
        List<DamingQuestion> list = damingQuestionService.selectDamingQuestionList(damingQuestion);
        TableDataInfo dataTable = getDataTable(list);
        // 加工一个题干给用户，否则题目干巴巴的内容很枯燥，还缺一个题目详细信息
        List<QuestionPageVM> questionVOS = list.stream().filter(Objects::nonNull).map(i -> {
            QuestionPageVM questionPageVM = new QuestionPageVM();
            Objects.requireNonNull(i, "题目信息不能为空");
            BeanUtils.copyProperties(i, questionPageVM);
            DamingContentInfo damingContentInfo = damingContentInfoMapper.selectDamingContentInfoById(i.getQuestionInfoId());
            String content = damingContentInfo.getContent();
            QuestionInfoContentVM infoContent = JSONObject.parseObject(content, QuestionInfoContentVM.class);
            questionPageVM.setQuestionTitle(infoContent.getQuestionTitle());
            return questionPageVM;
        }).collect(Collectors.toList());
        dataTable.setRows(questionVOS);

        return dataTable;
    }

    /**
     * 导出题目表列表（全部导出）
     */
    @PreAuthorize("@ss.hasPermi('quiz:question:export')")
    @Log(title = "题目表", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DamingQuestion damingQuestion) {
        // 先按查询条件查出符合条件的题目ID集合
        List<DamingQuestion> list = damingQuestionService.selectDamingQuestionList(damingQuestion);
        List<Long> exportIds = list.stream()
                .filter(Objects::nonNull)
                .map(DamingQuestion::getId)
                .collect(Collectors.toList());
        // 复用按ID导出时的视图对象，保持与“导出选中”一致的导出格式
        List<QuestionExportVO> exportList = damingQuestionService.selectQuestionExportList(exportIds);
        ExcelUtil<QuestionExportVO> util = new ExcelUtil<>(QuestionExportVO.class);
        util.exportExcel(response, exportList, "题目明细数据");
    }

    /**
     * 选中导出题目明细
     */
    @PreAuthorize("@ss.hasPermi('quiz:question:export')")
    @Log(title = "题目表", businessType = BusinessType.EXPORT)
    @PostMapping("/exportByIds")
    public void exportByIds(HttpServletResponse response, String ids) {
        List<Long> exportIds = Arrays.stream(StringUtils.defaultString(ids, "").split(","))
                .filter(StringUtils::isNotEmpty)
                .map(Long::valueOf)
                .collect(Collectors.toList());
        List<QuestionExportVO> exportList = damingQuestionService.selectQuestionExportList(exportIds);
        ExcelUtil<QuestionExportVO> util = new ExcelUtil<>(QuestionExportVO.class);
        util.exportExcel(response, exportList, "题目明细数据");
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
    public AjaxResult remove(@PathVariable Long[] ids) {
        int i = damingQuestionService.deleteDamingQuestionByIds(ids);
        if (i == 2) {
            return error("该试卷下有答题记录，请先删除");
        }
        return success("删除成功");
    }

    /**
     * 导入题目
     */
    @Log(title = "题目表", businessType = BusinessType.IMPORT)
    @PreAuthorize("@ss.hasPermi('quiz:question:import')")
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file) throws Exception {
        ExcelUtil<QuestionExportVO> util = new ExcelUtil<>(QuestionExportVO.class);
        List<QuestionExportVO> questionList = util.importExcel(file.getInputStream());
        String message = damingQuestionService.importQuestions(questionList, getUsername());
        return success(message);
    }

    /**
     * 下载题目导入模板
     */
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) {
        ExcelUtil<QuestionExportVO> util = new ExcelUtil<>(QuestionExportVO.class);
        QuestionExportVO sample = new QuestionExportVO();
        sample.setSubjectId(1);
        sample.setQuestionType(1);
        sample.setQuestionTitle("示例：单选题题干");
        sample.setOptionsJson("[{\"prefix\":\"A\",\"content\":\"示例选项1\"},{\"prefix\":\"B\",\"content\":\"示例选项2\"}]");
        sample.setCorrect("A");
        sample.setScore(5);
        sample.setAnalysis("解析示例：这里说明答案为何正确");
        util.exportExcel(response, Arrays.asList(sample), "题目导入模板");
    }
}

