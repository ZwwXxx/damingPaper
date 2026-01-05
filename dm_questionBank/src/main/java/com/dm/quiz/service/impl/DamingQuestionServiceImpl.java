package com.dm.quiz.service.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.dm.quiz.domain.DamingContentInfo;
import com.dm.quiz.domain.DamingQuestionAnswer;
import com.dm.quiz.mapper.DamingQuestionAnswerMapper;
import com.dm.quiz.viewmodel.QuestionInfoContentVM;
import com.dm.quiz.viewmodel.QuestionOptionVM;
import com.dm.quiz.viewmodel.QuestionExportVO;
import com.dm.quiz.dto.QuestionDto;
import com.dm.quiz.enums.QuestionTypeEnum;
import com.dm.quiz.mapper.DamingContentInfoMapper;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import com.dm.quiz.mapper.DamingQuestionMapper;
import com.dm.quiz.domain.DamingQuestion;
import com.dm.quiz.service.IDamingQuestionService;

/**
 * 题目表Service业务层处理
 *
 * @author zww
 * @date 2024-10-08
 */
@Service
public class DamingQuestionServiceImpl implements IDamingQuestionService {
    @Autowired
    private DamingQuestionMapper damingQuestionMapper;
    @Autowired
    private DamingContentInfoMapper damingContentInfoMapper;

    /**
     * 查询题目表
     *
     * @param id 题目表主键
     * @return 题目表
     */
    @Override
    public AjaxResult selectDamingQuestionById(Long id) {
        // DamingPaperMapper.xml.根据id获取题目表，根据信息id查信息表
        DamingQuestion damingQuestion = damingQuestionMapper.selectDamingQuestionById(id);
        // 抽取方法，后续试卷再遍历
        QuestionDto questionDto = getQuestionDto(damingQuestion);
        return AjaxResult.success(questionDto);
    }


    @Override
    public QuestionDto getQuestionDto(DamingQuestion damingQuestion) {
        DamingContentInfo damingContentInfo = damingContentInfoMapper.selectDamingContentInfoById(damingQuestion.getQuestionInfoId());
        // 2.获取题目信息表的内容，二者拼接返回，使用前端传来的dto实体承接
        QuestionDto questionDto = new QuestionDto();
        // 获取题目信息内容
        QuestionInfoContentVM questionInfoContentVM = JSONObject.parseObject(damingContentInfo.getContent(), QuestionInfoContentVM.class);
        questionDto.setQuestionTitle(questionInfoContentVM.getQuestionTitle());
        questionDto.setAnalysis(questionInfoContentVM.getAnalysis());
        questionDto.setItems(questionInfoContentVM.getQuestionOptionList());
        questionDto.setId(damingQuestion.getId());
        questionDto.setScore(damingQuestion.getScore());
        questionDto.setQuestionType(damingQuestion.getQuestionType());
        questionDto.setSubjectId(damingQuestion.getSubjectId());
        Integer questionType = damingQuestion.getQuestionType();
        if (questionType.equals(QuestionTypeEnum.Single.getCode())) {
            questionDto.setCorrect(questionInfoContentVM.getCorrect());
        } else if (questionType.equals(QuestionTypeEnum.Multiple.getCode())) {
            questionDto.setCorrectArray(questionInfoContentVM.getCorrect().split(","));
        } else {
            questionDto.setCorrect(questionInfoContentVM.getCorrect());
        }
        // 设置填空题答案顺序要求（默认false，不要求按顺序）
        questionDto.setRequireOrder(questionInfoContentVM.getRequireOrder() != null ? questionInfoContentVM.getRequireOrder() : false);
        // 设置解析内容格式（默认html，富文本格式）
        questionDto.setAnalysisFormat(questionInfoContentVM.getAnalysisFormat() != null ? questionInfoContentVM.getAnalysisFormat() : "html");
        // 设置题干内容格式（默认html，富文本格式）
        questionDto.setQuestionTitleFormat(questionInfoContentVM.getQuestionTitleFormat() != null ? questionInfoContentVM.getQuestionTitleFormat() : "html");
        // 设置选项内容格式（默认html，富文本格式）
        questionDto.setOptionFormat(questionInfoContentVM.getOptionFormat() != null ? questionInfoContentVM.getOptionFormat() : "html");
        // 设置标准答案内容格式（默认html，富文本格式，仅主观题）
        questionDto.setCorrectFormat(questionInfoContentVM.getCorrectFormat() != null ? questionInfoContentVM.getCorrectFormat() : "html");
        return questionDto;
    }

    /**
     * 查询题目表列表
     *
     * @param damingQuestion 题目表
     * @return 题目表
     */
    @Override
    public List<DamingQuestion> selectDamingQuestionList(DamingQuestion damingQuestion) {

        // 进行条件筛选
        return damingQuestionMapper.selectDamingQuestionList(damingQuestion);
    }

    /**
     * 新增题目表
     *
     * @param questionDto 题目表
     * @return 结果
     */
    @Override
    public int insertDamingQuestion(QuestionDto questionDto) {
        // DamingPaperMapper.xml.参数校验
        if (questionDto == null) {
            return 0;
        }
        standardizeQuestionOptions(questionDto);
        String dtoToString = QuestionDtoToString(questionDto);
        DamingContentInfo questionInfo = new DamingContentInfo();
        questionInfo.setContent(dtoToString);
        damingContentInfoMapper.insertContentInfo(questionInfo);

        // 3.组装问题实体
        DamingQuestion damingQuestion = new DamingQuestion();
        damingQuestion.setQuestionType(questionDto.getQuestionType());
        damingQuestion.setSubjectId(questionDto.getSubjectId());
        damingQuestion.setQuestionInfoId(questionInfo.getId());
        // 如果是多选题，将答案列表拆为字符串
        damingQuestion.setCorrect(
                questionDto.getQuestionType() == 2
                        ? String.join(",", questionDto.getCorrectArray())
                        : questionDto.getCorrect()
        );
        damingQuestion.setScore(questionDto.getScore());


        return damingQuestionMapper.insertDamingQuestion(damingQuestion);
    }

    /**
     * 加工info的content内容
     *
     * @param questionDto
     */
    private static String QuestionDtoToString(QuestionDto questionDto) {
        List<QuestionOptionVM> items = questionDto.getItems();
        List<QuestionOptionVM> questionOptionVMList = CollectionUtils.isEmpty(items)
                ? Collections.emptyList()
                : items.stream().map(i -> {
                    QuestionOptionVM questionOptionVM = new QuestionOptionVM();
                    questionOptionVM.setContent(i.getContent());
                    questionOptionVM.setPrefix(i.getPrefix());
                    questionOptionVM.setScore(i.getScore() == null ? null : i.getScore());
                    return questionOptionVM;
                }).collect(Collectors.toList());
        QuestionInfoContentVM questionInfoContentVM = new QuestionInfoContentVM();
        questionInfoContentVM.setQuestionTitle(questionDto.getQuestionTitle());
        questionInfoContentVM.setAnalysis(questionDto.getAnalysis());
        questionInfoContentVM.setCorrect((questionDto.getQuestionType() == 2)
                ? String.join(",", questionDto.getCorrectArray())
                : questionDto.getCorrect()
        );
        questionInfoContentVM.setQuestionOptionList(questionOptionVMList);
        // 保存填空题答案顺序要求（仅填空题有效）
        if (questionDto.getQuestionType() == QuestionTypeEnum.FillBlank.getCode()) {
            questionInfoContentVM.setRequireOrder(questionDto.getRequireOrder() != null ? questionDto.getRequireOrder() : false);
        }
        // 保存解析内容格式（默认html）
        questionInfoContentVM.setAnalysisFormat(questionDto.getAnalysisFormat() != null ? questionDto.getAnalysisFormat() : "html");
        // 保存题干内容格式（默认html）
        questionInfoContentVM.setQuestionTitleFormat(questionDto.getQuestionTitleFormat() != null ? questionDto.getQuestionTitleFormat() : "html");
        // 保存选项内容格式（默认html）
        questionInfoContentVM.setOptionFormat(questionDto.getOptionFormat() != null ? questionDto.getOptionFormat() : "html");
        // 保存标准答案内容格式（默认html，仅主观题）
        if (questionDto.getQuestionType() == QuestionTypeEnum.Subjective.getCode()) {
            questionInfoContentVM.setCorrectFormat(questionDto.getCorrectFormat() != null ? questionDto.getCorrectFormat() : "html");
        }
        return JSON.toJSONString(questionInfoContentVM);
    }

    /**
     * 修改题目表
     *
     * @param questionDto 题目表
     * @return 结果
     */
    @Override
    public int updateDamingQuestion(QuestionDto questionDto) {
        // DamingPaperMapper.xml.修改题目信息，先找出来，然后修改后在update表即可
        DamingQuestion damingQuestion = damingQuestionMapper.selectDamingQuestionById(questionDto.getId());
        damingQuestion.setQuestionType(questionDto.getQuestionType());
        damingQuestion.setScore(questionDto.getScore());
        damingQuestion.setSubjectId(questionDto.getSubjectId());
        damingQuestionMapper.updateDamingQuestion(damingQuestion);
        standardizeQuestionOptions(questionDto);
        DamingContentInfo damingContentInfo = damingContentInfoMapper.selectDamingContentInfoById(damingQuestion.getQuestionInfoId());
        String dtoToString = QuestionDtoToString(questionDto);
        damingContentInfo.setContent(dtoToString);
        return damingContentInfoMapper.updateDamingQuestionInfo(damingContentInfo);
    }

    @Autowired
    private DamingQuestionAnswerMapper damingQuestionAnswerMapper;

    /**
     * 批量删除题目表
     *
     * @param ids 需要删除的题目表主键
     * @return 结果
     */
    @Override
    public int deleteDamingQuestionByIds(Long[] ids) {
        // 如果该试卷下有其他作答记录，提示说已经绑定无法删除
        for (Long id : ids) {
            List<DamingQuestionAnswer> damingQuestionAnswers = damingQuestionAnswerMapper.selectDamingQuestionAnswerByQuestionId(id);
            if (damingQuestionAnswers.size()>0){
                return 2;
            }
        }
        return damingQuestionMapper.deleteDamingQuestionByIds(ids);
    }

    /**
     * 删除题目表信息
     *
     * @param id 题目表主键
     * @return 结果
     */
    @Override
    public int deleteDamingQuestionById(Long id) {
        return damingQuestionMapper.deleteDamingQuestionById(id);
    }

    private void standardizeQuestionOptions(QuestionDto questionDto) {
        if (questionDto == null || questionDto.getQuestionType() == null) {
            return;
        }
        if (Objects.equals(questionDto.getQuestionType(), QuestionTypeEnum.Judge.getCode())) {
            QuestionOptionVM trueOption = new QuestionOptionVM();
            trueOption.setPrefix("A");
            trueOption.setContent("正确");
            QuestionOptionVM falseOption = new QuestionOptionVM();
            falseOption.setPrefix("B");
            falseOption.setContent("错误");
            questionDto.setItems(Arrays.asList(trueOption, falseOption));
        }
    }

    @Override
    public List<QuestionExportVO> selectQuestionExportList(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        List<DamingQuestion> damingQuestions = damingQuestionMapper.selectQuestionListByIds(ids);
        return damingQuestions.stream()
                .map(this::buildQuestionExportVO)
                .collect(Collectors.toList());
    }

    private QuestionExportVO buildQuestionExportVO(DamingQuestion damingQuestion) {
        QuestionDto questionDto = getQuestionDto(damingQuestion);
        QuestionExportVO exportVO = new QuestionExportVO();
        exportVO.setId(questionDto.getId());
        exportVO.setSubjectId(questionDto.getSubjectId());
        exportVO.setQuestionType(questionDto.getQuestionType());
        exportVO.setQuestionTitle(questionDto.getQuestionTitle());
        exportVO.setAnalysis(questionDto.getAnalysis());
        exportVO.setScore(questionDto.getScore());
        
        // 处理选项：单选、多选、判断题需要选项；主观题和填空题选项为空
        Integer questionType = questionDto.getQuestionType();
        boolean isSubjective = Objects.equals(questionType, QuestionTypeEnum.Subjective.getCode());
        boolean isFillBlank = Objects.equals(questionType, QuestionTypeEnum.FillBlank.getCode());
        if (isSubjective || isFillBlank) {
            // 主观题和填空题不需要选项，导出为空数组
            exportVO.setOptionsJson("[]");
        } else {
            // 单选、多选、判断题需要选项
            exportVO.setOptionsJson(JSON.toJSONString(questionDto.getItems()));
        }
        
        // 处理答案：多选题答案用逗号分隔，其他题型直接使用correct字段
        if (Objects.equals(questionDto.getQuestionType(), QuestionTypeEnum.Multiple.getCode())) {
            exportVO.setCorrect(questionDto.getCorrectArray() == null ? null : String.join(",", questionDto.getCorrectArray()));
        } else {
            exportVO.setCorrect(questionDto.getCorrect());
        }
        return exportVO;
    }

    @Override
    @Transactional
    public String importQuestions(List<QuestionExportVO> questionList, String operName) {
        if (CollectionUtils.isEmpty(questionList)) {
            throw new ServiceException("导入题目数据不能为空");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        for (QuestionExportVO questionExportVO : questionList) {
            try {
                QuestionDto questionDto = convertExportVOToDto(questionExportVO);
                insertDamingQuestion(questionDto);
                successNum++;
                successMsg.append("<br/>").append(successNum).append("、题目：" ).append(questionDto.getQuestionTitle()).append(" 导入成功");
            } catch (Exception e) {
                failureNum++;
                String msg = "<br/>" + failureNum + "、题目：" + questionExportVO.getQuestionTitle() + " 导入失败，原因：" + e.getMessage();
                failureMsg.append(msg);
            }
        }
        if (failureNum > 0) {
            failureMsg.insert(0, "很抱歉，部分数据导入失败！共 " + failureNum + " 条数据");
            throw new ServiceException(failureMsg.toString());
        }
        successMsg.insert(0, "恭喜您，数据已全部导入成功！共 " + successNum + " 条数据，操作人：" + operName);
        return successMsg.toString();
    }

    private QuestionDto convertExportVOToDto(QuestionExportVO exportVO) {
        if (exportVO.getSubjectId() == null) {
            throw new ServiceException("科目ID不能为空");
        }
        if (exportVO.getQuestionType() == null) {
            throw new ServiceException("题型不能为空");
        }
        if (StringUtils.isBlank(exportVO.getQuestionTitle())) {
            throw new ServiceException("题目标题不能为空");
        }
        if (exportVO.getScore() == null) {
            throw new ServiceException("分数不能为空");
        }

        Integer questionType = exportVO.getQuestionType();
        boolean isSingle = Objects.equals(questionType, QuestionTypeEnum.Single.getCode());
        boolean isMultiple = Objects.equals(questionType, QuestionTypeEnum.Multiple.getCode());
        boolean isJudge = Objects.equals(questionType, QuestionTypeEnum.Judge.getCode());
        boolean isSubjective = Objects.equals(questionType, QuestionTypeEnum.Subjective.getCode());
        boolean isFillBlank = Objects.equals(questionType, QuestionTypeEnum.FillBlank.getCode());
        if (!isSingle && !isMultiple && !isJudge && !isSubjective && !isFillBlank) {
            throw new ServiceException("不支持的题目类型：" + questionType);
        }

        List<QuestionOptionVM> optionList = Collections.emptyList();
        if (StringUtils.isNotBlank(exportVO.getOptionsJson())) {
            optionList = JSON.parseArray(exportVO.getOptionsJson(), QuestionOptionVM.class);
        }
        if (isSingle || isMultiple) {
            if (CollectionUtils.isEmpty(optionList)) {
                throw new ServiceException("单选/多选题的选项内容不能为空");
            }
        } else if (isJudge) {
            // 判断题：如果选项为空，自动生成正确/错误选项；如果有选项，直接使用
            if (CollectionUtils.isEmpty(optionList)) {
                QuestionOptionVM optionTrue = new QuestionOptionVM();
                optionTrue.setPrefix("A");
                optionTrue.setContent("正确");
                QuestionOptionVM optionFalse = new QuestionOptionVM();
                optionFalse.setPrefix("B");
                optionFalse.setContent("错误");
                optionList = Arrays.asList(optionTrue, optionFalse);
            }
        }

        QuestionDto questionDto = new QuestionDto();
        questionDto.setSubjectId(exportVO.getSubjectId());
        questionDto.setQuestionType(exportVO.getQuestionType());
        questionDto.setQuestionTitle(exportVO.getQuestionTitle());
        questionDto.setAnalysis(StringUtils.isBlank(exportVO.getAnalysis()) ? "" : exportVO.getAnalysis());
        questionDto.setScore(exportVO.getScore());
        questionDto.setItems(optionList);
        if (isMultiple) {
            if (StringUtils.isBlank(exportVO.getCorrect())) {
                throw new ServiceException("多选题正确答案不能为空");
            }
            String[] answers = Arrays.stream(exportVO.getCorrect().split(","))
                    .map(String::trim)
                    .filter(StringUtils::isNotEmpty)
                    .toArray(String[]::new);
            if (answers.length == 0) {
                throw new ServiceException("多选题正确答案不能为空");
            }
            questionDto.setCorrectArray(answers);
        } else {
            // 主观题可以没有标准答案，其他题型必须有答案
            if (StringUtils.isBlank(exportVO.getCorrect()) && !isSubjective && !isFillBlank) {
                throw new ServiceException("正确答案不能为空");
            }
            questionDto.setCorrect(StringUtils.defaultString(exportVO.getCorrect()).trim());
        }
        return questionDto;
    }
}
