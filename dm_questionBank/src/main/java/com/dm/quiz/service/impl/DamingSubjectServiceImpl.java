package com.dm.quiz.service.impl;

import com.dm.quiz.domain.DamingPaper;
import com.dm.quiz.domain.DamingQuestion;
import com.dm.quiz.domain.DamingSubject;
import com.dm.quiz.mapper.DamingPaperMapper;
import com.dm.quiz.mapper.DamingQuestionMapper;
import com.dm.quiz.mapper.DamingSubjectMapper;
import com.dm.quiz.service.IDamingSubjectService;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 科目管理Service业务层处理
 */
@Service
public class DamingSubjectServiceImpl implements IDamingSubjectService {

    @Autowired
    private DamingSubjectMapper damingSubjectMapper;
    @Autowired
    private DamingQuestionMapper damingQuestionMapper;
    @Autowired
    private DamingPaperMapper damingPaperMapper;

    @Override
    public DamingSubject selectDamingSubjectBySubjectId(Integer subjectId) {
        return damingSubjectMapper.selectDamingSubjectBySubjectId(subjectId);
    }

    @Override
    public List<DamingSubject> selectDamingSubjectList(DamingSubject damingSubject) {
        return damingSubjectMapper.selectDamingSubjectList(damingSubject);
    }

    @Override
    public List<DamingSubject> selectActiveSubjects() {
        return damingSubjectMapper.selectActiveSubjects();
    }

    @Override
    public int insertDamingSubject(DamingSubject damingSubject) {
        validateSubjectName(damingSubject);
        damingSubject.setDelFlag(0);
        damingSubject.setCreateTime(DateUtils.getNowDate());
        damingSubject.setUpdateTime(DateUtils.getNowDate());
        return damingSubjectMapper.insertDamingSubject(damingSubject);
    }

    @Override
    public int updateDamingSubject(DamingSubject damingSubject) {
        if (damingSubject.getSubjectId() == null) {
            throw new ServiceException("科目ID不能为空");
        }
        validateSubjectName(damingSubject);
        damingSubject.setUpdateTime(DateUtils.getNowDate());
        return damingSubjectMapper.updateDamingSubject(damingSubject);
    }

    @Override
    public int deleteDamingSubjectBySubjectIds(Integer[] subjectIds) {
        if (subjectIds == null || subjectIds.length == 0) {
            return 0;
        }
        for (Integer subjectId : subjectIds) {
            ensureSubjectCanBeRemoved(subjectId);
        }
        return damingSubjectMapper.deleteDamingSubjectBySubjectIds(subjectIds);
    }

    @Override
    public int deleteDamingSubjectBySubjectId(Integer subjectId) {
        if (subjectId == null) {
            return 0;
        }
        ensureSubjectCanBeRemoved(subjectId);
        return damingSubjectMapper.deleteDamingSubjectBySubjectId(subjectId);
    }

    private void validateSubjectName(DamingSubject subject) {
        if (StringUtils.isBlank(subject.getSubjectName())) {
            throw new ServiceException("科目名称不能为空");
        }
        DamingSubject exist = damingSubjectMapper.selectDamingSubjectBySubjectName(subject.getSubjectName());
        if (exist != null && (subject.getSubjectId() == null || !exist.getSubjectId().equals(subject.getSubjectId()))) {
            throw new ServiceException("科目名称已存在");
        }
    }

    private void ensureSubjectCanBeRemoved(Integer subjectId) {
        if (subjectId == null) {
            return;
        }
        DamingQuestion question = new DamingQuestion();
        question.setSubjectId(subjectId);
        List<DamingQuestion> questionList = damingQuestionMapper.selectDamingQuestionList(question);
        if (!CollectionUtils.isEmpty(questionList)) {
            throw new ServiceException("该科目下存在题目，无法删除");
        }
        DamingPaper paper = new DamingPaper();
        paper.setSubjectId(subjectId);
        List<DamingPaper> paperList = damingPaperMapper.selectDamingPaperList(paper);
        if (!CollectionUtils.isEmpty(paperList)) {
            throw new ServiceException("该科目下存在试卷，无法删除");
        }
    }
}

