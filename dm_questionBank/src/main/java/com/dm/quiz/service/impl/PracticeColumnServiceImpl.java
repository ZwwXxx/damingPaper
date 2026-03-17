package com.dm.quiz.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dm.quiz.domain.DamingQuestion;
import com.dm.quiz.domain.DamingPaper;
import com.dm.quiz.domain.PracticeColumn;
import com.dm.quiz.dto.PaperDto;
import com.dm.quiz.dto.PaperQuestionTypeDto;
import com.dm.quiz.dto.QuestionDto;
import com.dm.quiz.mapper.DamingQuestionMapper;
import com.dm.quiz.mapper.PracticeColumnMapper;
import com.dm.quiz.mapper.PracticeColumnQuestionMapper;
import com.dm.quiz.service.IDamingPaperService;
import com.dm.quiz.service.IPracticeColumnService;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;

@Service
public class PracticeColumnServiceImpl implements IPracticeColumnService {

    private static final String DEFAULT_COLUMN_NAME = "其它/零散题";

    @Autowired
    private PracticeColumnMapper practiceColumnMapper;

    @Autowired
    private PracticeColumnQuestionMapper practiceColumnQuestionMapper;

    @Autowired
    private DamingQuestionMapper damingQuestionMapper;

    @Autowired
    private IDamingPaperService damingPaperService;

    @Override
    public PracticeColumn selectById(Long columnId) {
        return practiceColumnMapper.selectPracticeColumnById(columnId);
    }

    @Override
    public List<PracticeColumn> selectList(PracticeColumn query) {
        return practiceColumnMapper.selectPracticeColumnList(query);
    }

    @Override
    public int insert(PracticeColumn column) {
        if (column == null) {
            throw new ServiceException("栏目不能为空");
        }
        if (column.getSubjectId() == null) {
            throw new ServiceException("请选择科目");
        }
        // groupName 前端通常必填，这里仍做一下规整
        if (column.getGroupName() != null) {
            column.setGroupName(column.getGroupName().trim());
        }
        if (StringUtils.isBlank(column.getColumnName())) {
            column.setColumnName(DEFAULT_COLUMN_NAME);
        } else {
            column.setColumnName(column.getColumnName().trim());
        }
        // 新建栏目默认不绑定试卷
        if (column.getPaperId() == null) {
            column.setPaperId(null);
        }
        return practiceColumnMapper.insertPracticeColumn(column);
    }

    @Override
    public int update(PracticeColumn column) {
        if (column == null || column.getColumnId() == null) {
            throw new ServiceException("栏目ID不能为空");
        }
        if (column.getGroupName() != null) {
            column.setGroupName(column.getGroupName().trim());
        }
        // 编辑时：若用户把栏目名清空，也自动回填默认值（避免 column_name NOT NULL 且保持一致体验）
        if (column.getColumnName() != null) {
            if (StringUtils.isBlank(column.getColumnName())) {
                column.setColumnName(DEFAULT_COLUMN_NAME);
            } else {
                column.setColumnName(column.getColumnName().trim());
            }
        }
        return practiceColumnMapper.updatePracticeColumn(column);
    }

    @Override
    public int deleteById(Long columnId) {
        return practiceColumnMapper.deletePracticeColumnById(columnId);
    }

    @Override
    @Transactional
    public Long getOrCreatePracticePaperId(Long columnId) {
        if (columnId == null) {
            throw new ServiceException("栏目ID不能为空");
        }
        PracticeColumn column = practiceColumnMapper.selectPracticeColumnById(columnId);
        if (column == null) {
            throw new ServiceException("栏目不存在");
        }
        if (column.getPaperId() != null) {
            return column.getPaperId();
        }
        if (column.getSubjectId() == null) {
            throw new ServiceException("栏目未设置科目，无法生成试卷");
        }
        List<Long> questionIds = practiceColumnQuestionMapper.selectQuestionIdsByColumnId(columnId);
        if (questionIds == null || questionIds.isEmpty()) {
            throw new ServiceException("该栏目下暂无题目，无法开始专项练习");
        }

        List<DamingQuestion> questions = damingQuestionMapper.selectQuestionListByIds(questionIds);
        if (questions == null || questions.isEmpty()) {
            throw new ServiceException("栏目题目不存在或已被删除");
        }

        // 构建最小可用的 PaperDto（insertDamingPaper 需要 questionType/score/parentId 来计算与编号）
        List<QuestionDto> questionDtos = new ArrayList<>();
        for (Long qid : questionIds) {
            DamingQuestion q = questions.stream().filter(x -> Objects.equals(x.getId(), qid)).findFirst().orElse(null);
            if (q == null) {
                continue;
            }
            QuestionDto dto = new QuestionDto();
            dto.setId(q.getId());
            dto.setQuestionType(q.getQuestionType());
            dto.setScore(q.getScore());
            dto.setParentId(q.getParentId());
            dto.setClozeIndex(q.getClozeIndex());
            questionDtos.add(dto);
        }
        if (questionDtos.isEmpty()) {
            throw new ServiceException("栏目题目为空，无法生成试卷");
        }

        PaperQuestionTypeDto typeDto = new PaperQuestionTypeDto();
        typeDto.setName("专项练习");
        typeDto.setQuestionDtos(questionDtos);

        PaperDto paperDto = new PaperDto();
        paperDto.setPaperName("专项-" + column.getColumnName());
        paperDto.setSubjectId(column.getSubjectId());
        paperDto.setPaperType(3); // 3=专项练习（自定义）
        paperDto.setSuggestTime(60);
        paperDto.setEnableAntiCheat(false);
        paperDto.setNumberMode(2);
        List<PaperQuestionTypeDto> list = new ArrayList<>();
        list.add(typeDto);
        paperDto.setPaperQuestionTypeDto(list);

        DamingPaper paper = damingPaperService.insertDamingPaper(paperDto);
        if (paper == null || paper.getPaperId() == null) {
            throw new ServiceException("生成专项练习试卷失败");
        }
        practiceColumnMapper.updatePaperId(columnId, paper.getPaperId());
        return paper.getPaperId();
    }

    @Override
    @Transactional
    public void saveColumnQuestions(Long columnId, List<Long> questionIds) {
        if (columnId == null) {
            throw new ServiceException("栏目ID不能为空");
        }
        PracticeColumn column = practiceColumnMapper.selectPracticeColumnById(columnId);
        if (column == null) {
            throw new ServiceException("栏目不存在");
        }
        // 全量覆盖
        practiceColumnQuestionMapper.deleteByColumnId(columnId);
        if (questionIds != null && !questionIds.isEmpty()) {
            List<com.dm.quiz.domain.PracticeColumnQuestion> list = new ArrayList<>();
            int i = 0;
            for (Long qid : questionIds) {
                if (qid == null) continue;
                com.dm.quiz.domain.PracticeColumnQuestion rel = new com.dm.quiz.domain.PracticeColumnQuestion();
                rel.setColumnId(columnId);
                rel.setQuestionId(qid);
                rel.setSortOrder(i++);
                list.add(rel);
            }
            if (!list.isEmpty()) {
                practiceColumnQuestionMapper.batchInsert(list);
            }
        }
        // 使已生成的试卷失效，避免栏目题目变化后仍复用旧试卷
        practiceColumnMapper.updatePaperId(columnId, null);
    }

    @Override
    public List<Long> selectQuestionIdsByColumnId(Long columnId) {
        if (columnId == null) {
            throw new ServiceException("栏目ID不能为空");
        }
        return practiceColumnQuestionMapper.selectQuestionIdsByColumnId(columnId);
    }

    @Override
    public List<Long> selectBoundColumnIdsByQuestionId(Long questionId) {
        if (questionId == null) {
            throw new ServiceException("题目ID不能为空");
        }
        return practiceColumnQuestionMapper.selectColumnIdsByQuestionId(questionId);
    }

    @Override
    @Transactional
    public void saveQuestionColumns(Long questionId, List<Long> columnIds) {
        if (questionId == null) {
            throw new ServiceException("题目ID不能为空");
        }
        // 旧绑定
        List<Long> old = practiceColumnQuestionMapper.selectColumnIdsByQuestionId(questionId);
        Set<Long> affected = new HashSet<>();
        if (old != null) affected.addAll(old);
        if (columnIds != null) {
            for (Long cid : columnIds) {
                if (cid != null) affected.add(cid);
            }
        }

        // 全量覆盖
        practiceColumnQuestionMapper.deleteByQuestionId(questionId);
        if (columnIds != null && !columnIds.isEmpty()) {
            List<com.dm.quiz.domain.PracticeColumnQuestion> list = new ArrayList<>();
            int i = 0;
            for (Long cid : columnIds) {
                if (cid == null) continue;
                com.dm.quiz.domain.PracticeColumnQuestion rel = new com.dm.quiz.domain.PracticeColumnQuestion();
                rel.setColumnId(cid);
                rel.setQuestionId(questionId);
                rel.setSortOrder(i++);
                list.add(rel);
            }
            if (!list.isEmpty()) {
                practiceColumnQuestionMapper.batchInsert(list);
            }
        }

        // 相关栏目的试卷失效（题目集合变了）
        for (Long cid : affected) {
            if (cid != null) {
                practiceColumnMapper.updatePaperId(cid, null);
            }
        }
    }

    @Override
    public List<PracticeColumn> selectGroupOptions(Integer subjectId) {
        return practiceColumnMapper.selectGroupOptions(subjectId);
    }
}

