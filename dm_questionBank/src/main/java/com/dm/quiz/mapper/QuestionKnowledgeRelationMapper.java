package com.dm.quiz.mapper;

import com.dm.quiz.domain.QuestionKnowledgeRelation;
import com.ruoyi.system.domain.KnowledgePoint;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface QuestionKnowledgeRelationMapper {
    
    int batchInsert(List<QuestionKnowledgeRelation> relations);
    
    int deleteByQuestionId(Long questionId);
    
    List<KnowledgePoint> selectKnowledgePointsByQuestionId(Long questionId);
    
    List<Long> selectQuestionIdsByKnowledgePointId(Long knowledgePointId);
    
    List<KnowledgePoint> selectWeakKnowledgePoints(@Param("questionIds") List<Long> questionIds);
}
