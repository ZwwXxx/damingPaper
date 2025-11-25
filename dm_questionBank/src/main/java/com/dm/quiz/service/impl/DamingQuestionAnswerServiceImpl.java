package com.dm.quiz.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.dm.quiz.domain.DamingPaper;
import com.dm.quiz.domain.DamingQuestion;
import com.dm.quiz.dto.QuestionDto;
import com.dm.quiz.mapper.DamingPaperMapper;
import com.dm.quiz.mapper.DamingQuestionMapper;
import com.dm.quiz.viewmodel.WrongQuestionVO;
import com.dm.quiz.service.IDamingQuestionService;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.dm.quiz.mapper.DamingQuestionAnswerMapper;
import com.dm.quiz.domain.DamingQuestionAnswer;
import com.dm.quiz.service.IDamingQuestionAnswerService;

/**
 * 题目回答情况表Service业务层处理
 *
 * @author zww
 * @date 2024-10-13
 */
@Service
public class DamingQuestionAnswerServiceImpl implements IDamingQuestionAnswerService
{
    @Autowired
    private DamingQuestionAnswerMapper damingQuestionAnswerMapper;
    @Autowired
    private DamingQuestionMapper damingQuestionMapper;
    @Autowired
    private DamingPaperMapper damingPaperMapper;
    @Autowired
    private IDamingQuestionService damingQuestionService;

    /**
     * 查询题目回答情况表
     *
     * @param answerId 题目回答情况表主键
     * @return 题目回答情况表
     */
    @Override
    public DamingQuestionAnswer selectDamingQuestionAnswerByAnswerId(Long answerId)
    {
        return damingQuestionAnswerMapper.selectDamingQuestionAnswerByAnswerId(answerId);
    }

    /**
     * 查询题目回答情况表列表
     *
     * @param damingQuestionAnswer 题目回答情况表
     * @return 题目回答情况表
     */
    @Override
    public List<DamingQuestionAnswer> selectDamingQuestionAnswerList(DamingQuestionAnswer damingQuestionAnswer)

    {
        if (!SecurityUtils.getUsername().equals("admin")) {
            damingQuestionAnswer.setCreateBy(SecurityUtils.getUsername());
        }
        return damingQuestionAnswerMapper.selectDamingQuestionAnswerList(damingQuestionAnswer);
    }

    /**
     * 新增题目回答情况表
     *
     * @param damingQuestionAnswer 题目回答情况表
     * @return 结果
     */
    @Override
    public int insertDamingQuestionAnswer(DamingQuestionAnswer damingQuestionAnswer)
    {
        damingQuestionAnswer.setCreateTime(DateUtils.getNowDate());
        return damingQuestionAnswerMapper.insertDamingQuestionAnswer(damingQuestionAnswer);
    }

    /**
     * 修改题目回答情况表
     *
     * @param damingQuestionAnswer 题目回答情况表
     * @return 结果
     */
    @Override
    public int updateDamingQuestionAnswer(DamingQuestionAnswer damingQuestionAnswer)
    {
        return damingQuestionAnswerMapper.updateDamingQuestionAnswer(damingQuestionAnswer);
    }

    /**
     * 批量删除题目回答情况表
     *
     * @param answerIds 需要删除的题目回答情况表主键
     * @return 结果
     */
    @Override
    public int deleteDamingQuestionAnswerByAnswerIds(Long[] answerIds)
    {
        return damingQuestionAnswerMapper.deleteDamingQuestionAnswerByAnswerIds(answerIds);
    }

    /**
     * 删除题目回答情况表信息
     *
     * @param answerId 题目回答情况表主键
     * @return 结果
     */
    @Override
    public int deleteDamingQuestionAnswerByAnswerId(Long answerId)
    {
        return damingQuestionAnswerMapper.deleteDamingQuestionAnswerByAnswerId(answerId);
    }

    @Override
    public List<WrongQuestionVO> selectWrongQuestionList(DamingQuestionAnswer filter) {
        if (filter == null) {
            filter = new DamingQuestionAnswer();
        }
        // 仅筛选错误题
        filter.setIsCorrect(false);
        // 小权限保护：非管理员默认限制为当前用户
        if (!SecurityUtils.isAdmin(SecurityUtils.getUserId())) {
            filter.setCreateUser(SecurityUtils.getUsername());
        }
        List<DamingQuestionAnswer> answers = damingQuestionAnswerMapper.selectDamingQuestionAnswerList(filter);
        Map<String, List<DamingQuestionAnswer>> grouped = answers.stream()
                .collect(Collectors.groupingBy(answer -> buildGroupKey(answer)));
        final String paperNameKeyword = filter.getPaperName();
        List<DamingQuestionAnswer> latestAnswers = grouped.values().stream()
                .map(list -> list.stream()
                        .max((a1, a2) -> compareDate(a1.getCreateTime(), a2.getCreateTime()))
                        .orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        Map<Long, DamingPaper> paperMap = buildPaperMap(latestAnswers);
        Map<Long, QuestionDto> questionDtoMap = buildQuestionDtoMap(latestAnswers);
        return latestAnswers.stream()
                .map(answer -> buildWrongQuestionVO(answer,
                        paperMap.get(answer.getPaperId()),
                        questionDtoMap.get(answer.getQuestionId()),
                        grouped.getOrDefault(buildGroupKey(answer), Collections.emptyList()).size()))
                .filter(Objects::nonNull)
                .filter(vo -> StringUtils.isBlank(paperNameKeyword) || (vo.getPaperName() != null && vo.getPaperName().contains(paperNameKeyword)))
                .collect(Collectors.toList());
    }

    /**
     * 将题目回答记录转为错题视图对象，补充试卷与题干信息
     */
    private WrongQuestionVO buildWrongQuestionVO(DamingQuestionAnswer answer,
                                                DamingPaper paper,
                                                QuestionDto questionDto,
                                                int wrongCount) {
        WrongQuestionVO vo = new WrongQuestionVO();
        vo.setAnswerId(answer.getAnswerId());
        vo.setLatestAnswerId(answer.getAnswerId());
        vo.setPaperId(answer.getPaperId());
        vo.setPaperAnswerId(answer.getPaperAnswerId());
        vo.setQuestionId(answer.getQuestionId());
        vo.setSubjectId(answer.getSubjectId());
        vo.setQuestionType(answer.getQuestionType());
        vo.setUserAnswer(answer.getUserAnswer());
        vo.setQuestionScore(answer.getQuestionScore());
        vo.setFinalScore(answer.getFinalScore());
        if (answer.getCreateTime() != null) {
            vo.setCreateTime(DateUtils.parseDateToStr("yyyy-MM-dd HH:mm:ss", answer.getCreateTime()));
        }
        vo.setWrongCount(Math.max(wrongCount, 1));
        if (paper != null) {
            vo.setPaperName(paper.getPaperName());
        }
        if (questionDto != null) {
            vo.setQuestionTitle(questionDto.getQuestionTitle());
            vo.setOptions(questionDto.getItems());
            vo.setCorrect(questionDto.getCorrect());
            vo.setCorrectArray(questionDto.getCorrectArray());
        }
        return vo;
    }

    private String buildGroupKey(DamingQuestionAnswer answer) {
        return (answer.getCreateUser() == null ? "" : answer.getCreateUser()) + "_"
                + (answer.getPaperId() == null ? "" : answer.getPaperId()) + "_"
                + (answer.getQuestionId() == null ? "" : answer.getQuestionId());
    }

    private Map<Long, DamingPaper> buildPaperMap(List<DamingQuestionAnswer> answers) {
        Set<Long> paperIds = answers.stream()
                .map(DamingQuestionAnswer::getPaperId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (paperIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return damingPaperMapper.selectDamingPaperListByIds(paperIds.stream().collect(Collectors.toList()))
                .stream()
                .collect(Collectors.toMap(DamingPaper::getPaperId, paper -> paper));
    }

    private Map<Long, QuestionDto> buildQuestionDtoMap(List<DamingQuestionAnswer> answers) {
        Set<Long> questionIds = answers.stream()
                .map(DamingQuestionAnswer::getQuestionId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (questionIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return damingQuestionMapper.selectQuestionListByIds(questionIds.stream().collect(Collectors.toList()))
                .stream()
                .collect(Collectors.toMap(
                        DamingQuestion::getId,
                        question -> damingQuestionService.getQuestionDto(question)));
    }

    private int compareDate(java.util.Date d1, java.util.Date d2) {
        if (d1 == null && d2 == null) {
            return 0;
        }
        if (d1 == null) {
            return -1;
        }
        if (d2 == null) {
            return 1;
        }
        return d1.compareTo(d2);
    }
}
