package com.dm.quiz.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.dm.quiz.domain.PracticeColumnQuestion;

public interface PracticeColumnQuestionMapper {

    List<Long> selectQuestionIdsByColumnId(@Param("columnId") Long columnId);

    List<Long> selectColumnIdsByQuestionId(@Param("questionId") Long questionId);

    int deleteByColumnId(@Param("columnId") Long columnId);

    int deleteByQuestionId(@Param("questionId") Long questionId);

    int batchInsert(@Param("list") List<PracticeColumnQuestion> list);
}

