package com.dm.quiz.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.dm.quiz.domain.PracticeColumn;

public interface PracticeColumnMapper {

    PracticeColumn selectPracticeColumnById(@Param("columnId") Long columnId);

    List<PracticeColumn> selectPracticeColumnList(PracticeColumn query);

    int insertPracticeColumn(PracticeColumn column);

    int updatePracticeColumn(PracticeColumn column);

    int deletePracticeColumnById(@Param("columnId") Long columnId);

    int updatePaperId(@Param("columnId") Long columnId, @Param("paperId") Long paperId);

    List<PracticeColumn> selectGroupOptions(@Param("subjectId") Integer subjectId);
}

