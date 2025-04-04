package com.dm.quiz.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.dm.quiz.domain.DamingContentInfo;
import com.dm.quiz.domain.DamingPaperAnswer;
import com.dm.quiz.domain.DamingQuestionAnswer;
import com.dm.quiz.mapper.DamingQuestionAnswerMapper;
import com.dm.quiz.viewmodel.QuestionInfoContentVM;
import com.dm.quiz.viewmodel.QuestionOptionVM;
import com.dm.quiz.dto.QuestionDto;
import com.dm.quiz.enums.QuestionTypeEnum;
import com.dm.quiz.mapper.DamingContentInfoMapper;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.dm.quiz.mapper.DamingQuestionMapper;
import com.dm.quiz.domain.DamingQuestion;
import com.dm.quiz.service.IDamingQuestionService;
import org.springframework.transaction.annotation.Transactional;

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
        }
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
    @Transactional
    public int insertDamingQuestion(QuestionDto questionDto) {
        // DamingPaperMapper.xml.参数校验
        if (questionDto == null) {
            return 0;
        }
        // 2.组装题目实体,塞到数据库里
        // 将选项数组转为列表，单选没有score信息，多选有。
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
        List<QuestionOptionVM> questionOptionVMList = questionDto.getItems().stream().map(i -> {
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
        return JSON.toJSONString(questionInfoContentVM);
    }

    /**
     * 修改题目表
     *
     * @param questionDto 题目表
     * @return 结果
     */
    @Override
    @Transactional
    public int updateDamingQuestion(QuestionDto questionDto) {
        // DamingPaperMapper.xml.修改题目信息，先找出来，然后修改后在update表即可
        DamingQuestion damingQuestion = damingQuestionMapper.selectDamingQuestionById(questionDto.getId());
        damingQuestion.setQuestionType(questionDto.getQuestionType());
        damingQuestion.setScore(questionDto.getScore());
        damingQuestion.setSubjectId(questionDto.getSubjectId());
        damingQuestionMapper.updateDamingQuestion(damingQuestion);
        // 2.题目信息实体也是这样
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
}
