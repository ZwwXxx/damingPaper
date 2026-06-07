package com.ruoyi.web.controller.quiz.admin;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson2.JSONObject;
import com.dm.quiz.domain.DamingContentInfo;
import com.dm.quiz.viewmodel.QuestionInfoContentVM;
import com.dm.quiz.dto.BatchQuestionExamMetaRequest;
import com.dm.quiz.dto.ClozeQuestionCreateRequest;
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
import com.dm.quiz.service.IQuestionKnowledgeService;
import com.dm.quiz.dto.QuestionKnowledgeBindDto;
import com.dm.quiz.service.IPracticeColumnService;

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
    @Autowired
    private IQuestionKnowledgeService questionKnowledgeService;
    @Autowired
    private IPracticeColumnService practiceColumnService;

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
        Long id = damingQuestionService.insertDamingQuestion(questionDto);
        if (id == null) {
            return AjaxResult.error("新增题目失败");
        }
        // 返回新生成的题目主键ID，前端可用于绑定知识点、专项栏目等
        return AjaxResult.success(id);
    }

    /**
     * 批量创建完形填空题（父题 + 多个子题）
     */
    @PreAuthorize("@ss.hasPermi('quiz:question:add')")
    @Log(title = "题目表-完形填空", businessType = BusinessType.INSERT)
    @PostMapping("/cloze")
    public AjaxResult addCloze(@RequestBody ClozeQuestionCreateRequest request) {
        damingQuestionService.createClozeQuestion(request);
        return success();
    }

    /**
     * 获取完形填空题（父题 + 子题）详情
     */
    @PreAuthorize("@ss.hasPermi('quiz:question:query')")
    @GetMapping("/cloze/{id}")
    public AjaxResult getCloze(@PathVariable("id") Long id) {
        return success(damingQuestionService.getClozeQuestion(id));
    }

    /**
     * 更新完形填空题（父题 + 子题）
     */
    @PreAuthorize("@ss.hasPermi('quiz:question:edit')")
    @Log(title = "题目表-完形填空", businessType = BusinessType.UPDATE)
    @PutMapping("/cloze")
    public AjaxResult updateCloze(@RequestBody ClozeQuestionCreateRequest request) {
        damingQuestionService.updateClozeQuestion(request);
        return success();
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
     * 仅更新题目的原卷题号
     */
    @PreAuthorize("@ss.hasPermi('quiz:question:edit')")
    @Log(title = "题目表-原卷题号", businessType = BusinessType.UPDATE)
    @PutMapping("/origin-order")
    public AjaxResult updateOriginOrder(@RequestBody QuestionDto questionDto) {
        if (questionDto.getId() == null) {
            return AjaxResult.error("题目ID不能为空");
        }
        damingQuestionService.updateOriginOrder(questionDto.getId(), questionDto.getOriginOrder());
        return success();
    }

    /**
     * 仅更新题目的考试批次（上/下半年）
     */
    @PreAuthorize("@ss.hasPermi('quiz:question:edit')")
    @Log(title = "题目表-考试批次", businessType = BusinessType.UPDATE)
    @PutMapping("/exam-half")
    public AjaxResult updateExamHalf(@RequestBody QuestionDto questionDto) {
        if (questionDto.getId() == null) {
            return AjaxResult.error("题目ID不能为空");
        }
        damingQuestionService.updateExamHalf(questionDto.getId(), questionDto.getExamHalf());
        return success();
    }

    /**
     * 批量设置题目的考试年份与上/下半年批次（未传的字段不改）
     */
    @PreAuthorize("@ss.hasPermi('quiz:question:edit')")
    @Log(title = "题目表-批量年份批次", businessType = BusinessType.UPDATE)
    @PutMapping("/batch-exam-meta")
    public AjaxResult batchUpdateExamMeta(@RequestBody BatchQuestionExamMetaRequest body) {
        if (body == null || body.getIds() == null || body.getIds().isEmpty()) {
            return AjaxResult.error("请选择要更新的题目");
        }
        damingQuestionService.batchUpdateExamMeta(body.getIds(), body.getExamYear(), body.getExamHalf());
        return success();
    }

    /**
     * 删除题目表
     */
    @PreAuthorize("@ss.hasPermi('quiz:question:remove')")
    @Log(title = "题目表", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        // 具体的“是否存在答题记录”校验逻辑放在 Service 层，
        // 这里直接调用即可，若有答题记录会抛 ServiceException 由全局异常处理。
        damingQuestionService.deleteDamingQuestionByIds(ids);
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
        List<QuestionExportVO> samples = Arrays.asList(
                // 1. 单选题示例
                createSampleQuestion(1, 1, "示例：这是一道单选题题干？",
                        "[{\"prefix\":\"A\",\"content\":\"选项A内容\"},{\"prefix\":\"B\",\"content\":\"选项B内容\"},{\"prefix\":\"C\",\"content\":\"选项C内容\"},{\"prefix\":\"D\",\"content\":\"选项D内容\"}]",
                        "A", 5, "解析：单选题只有一个正确答案，这里选择A"),
                
                // 2. 多选题示例
                createSampleQuestion(1, 2, "示例：这是一道多选题题干？（多选）",
                        "[{\"prefix\":\"A\",\"content\":\"选项A内容\"},{\"prefix\":\"B\",\"content\":\"选项B内容\"},{\"prefix\":\"C\",\"content\":\"选项C内容\"},{\"prefix\":\"D\",\"content\":\"选项D内容\"}]",
                        "A,B,C", 10, "解析：多选题有多个正确答案，答案用英文逗号分隔，这里选择A、B、C"),
                
                // 3. 主观题示例
                createSampleQuestion(1, 3, "示例：这是一道主观题题干，请简述xxx的特点？",
                        "[]", "参考答案：主观题可以提供参考答案，也可以不提供", 20, "解析：主观题需要人工批阅，可以设置参考答案供批阅参考"),
                
                // 4. 判断题示例
                createSampleQuestion(1, 4, "示例：这是一道判断题题干，xxx说法是否正确？",
                        "[{\"prefix\":\"A\",\"content\":\"正确\"},{\"prefix\":\"B\",\"content\":\"错误\"}]",
                        "A", 5, "解析：判断题选项会自动生成为正确/错误，答案填A表示正确，填B表示错误"),
                
                // 5. 填空题示例
                createSampleQuestion(1, 5, "示例：这是一道填空题题干，请填写____的值",
                        "[]", "答案内容", 8, "解析：填空题答案为标准答案，支持多个空格的情况")
        );
        util.exportExcel(response, samples, "题目导入模板");
    }

    /**
     * 创建示例题目
     */
    private QuestionExportVO createSampleQuestion(Integer subjectId, Integer questionType, 
                                                   String title, String optionsJson, 
                                                   String correct, Integer score, String analysis) {
        QuestionExportVO sample = new QuestionExportVO();
        sample.setSubjectId(subjectId);
        sample.setQuestionType(questionType);
        sample.setQuestionTitle(title);
        sample.setOptionsJson(optionsJson);
        sample.setCorrect(correct);
        sample.setScore(score);
        sample.setAnalysis(analysis);
        return sample;
    }

    @PreAuthorize("@ss.hasPermi('quiz:question:edit')")
    @PostMapping("/relation/bind")
    public AjaxResult bindKnowledgePoints(@RequestBody QuestionKnowledgeBindDto dto) {
        questionKnowledgeService.bindKnowledgePoints(dto);
        return success("关联成功");
    }

    @GetMapping("/{questionId}/knowledge-points")
    public AjaxResult getKnowledgePoints(@PathVariable Long questionId) {
        return success(questionKnowledgeService.getKnowledgePointsByQuestionId(questionId));
    }

    /**
     * 获取题目绑定的专项栏目ID列表
     */
    @PreAuthorize("@ss.hasPermi('quiz:question:query')")
    @GetMapping("/{questionId}/practice-columns")
    public AjaxResult getPracticeColumns(@PathVariable Long questionId) {
        return success(practiceColumnService.selectBoundColumnIdsByQuestionId(questionId));
    }

    /**
     * 保存题目绑定的专项栏目（全量覆盖）
     * body: [columnId1, columnId2, ...]
     */
    @PreAuthorize("@ss.hasPermi('quiz:question:edit')")
    @PostMapping("/{questionId}/practice-columns")
    public AjaxResult savePracticeColumns(@PathVariable Long questionId, @RequestBody List<Long> columnIds) {
        practiceColumnService.saveQuestionColumns(questionId, columnIds);
        return success("保存成功");
    }
}

