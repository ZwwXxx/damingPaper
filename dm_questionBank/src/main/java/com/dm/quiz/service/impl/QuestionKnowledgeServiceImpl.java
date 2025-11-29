package com.dm.quiz.service.impl;

import com.dm.quiz.domain.QuestionKnowledgeRelation;
import com.dm.quiz.dto.QuestionKnowledgeBindDto;
import com.dm.quiz.mapper.QuestionKnowledgeRelationMapper;
import com.dm.quiz.service.IQuestionKnowledgeService;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.KnowledgePoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class QuestionKnowledgeServiceImpl implements IQuestionKnowledgeService {

    @Autowired
    private QuestionKnowledgeRelationMapper relationMapper;

    @Override
    @Transactional
    public void bindKnowledgePoints(QuestionKnowledgeBindDto dto) {
        relationMapper.deleteByQuestionId(dto.getQuestionId());
        
        if (dto.getKnowledgePointIds() != null && !dto.getKnowledgePointIds().isEmpty()) {
            List<QuestionKnowledgeRelation> relations = new ArrayList<>();
            String username = SecurityUtils.getUsername();
            
            for (Long knowledgePointId : dto.getKnowledgePointIds()) {
                QuestionKnowledgeRelation relation = new QuestionKnowledgeRelation();
                relation.setQuestionId(dto.getQuestionId());
                relation.setKnowledgePointId(knowledgePointId);
                relation.setRelationType(dto.getRelationType() != null ? dto.getRelationType() : 1);
                relation.setReason(dto.getReason());
                relation.setCreateBy(username);
                relations.add(relation);
            }
            
            relationMapper.batchInsert(relations);
        }
    }

    @Override
    public List<KnowledgePoint> getKnowledgePointsByQuestionId(Long questionId) {
        return relationMapper.selectKnowledgePointsByQuestionId(questionId);
    }

    @Override
    public Map<Long, List<KnowledgePoint>> getKnowledgePointsByQuestionIds(List<Long> questionIds) {
        Map<Long, List<KnowledgePoint>> result = new HashMap<>();
        for (Long questionId : questionIds) {
            result.put(questionId, getKnowledgePointsByQuestionId(questionId));
        }
        return result;
    }

    @Override
    public List<KnowledgePoint> getWeakKnowledgePoints(List<Long> wrongQuestionIds) {
        if (wrongQuestionIds == null || wrongQuestionIds.isEmpty()) {
            return new ArrayList<>();
        }
        return relationMapper.selectWeakKnowledgePoints(wrongQuestionIds);
    }

    @Override
    public List<Long> getQuestionIdsByKnowledgePointId(Long knowledgePointId) {
        return relationMapper.selectQuestionIdsByKnowledgePointId(knowledgePointId);
    }
}
