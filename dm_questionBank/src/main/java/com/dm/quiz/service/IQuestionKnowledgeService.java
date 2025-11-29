package com.dm.quiz.service;

import com.dm.quiz.dto.QuestionKnowledgeBindDto;
import com.ruoyi.system.domain.KnowledgePoint;

import java.util.List;
import java.util.Map;

public interface IQuestionKnowledgeService {
    
    void bindKnowledgePoints(QuestionKnowledgeBindDto dto);
    
    List<KnowledgePoint> getKnowledgePointsByQuestionId(Long questionId);
    
    Map<Long, List<KnowledgePoint>> getKnowledgePointsByQuestionIds(List<Long> questionIds);
    
    List<KnowledgePoint> getWeakKnowledgePoints(List<Long> wrongQuestionIds);
    
    List<Long> getQuestionIdsByKnowledgePointId(Long knowledgePointId);
}
