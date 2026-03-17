package com.dm.quiz.service;

import java.util.List;
import com.dm.quiz.domain.PracticeColumn;

public interface IPracticeColumnService {

    PracticeColumn selectById(Long columnId);

    List<PracticeColumn> selectList(PracticeColumn query);

    int insert(PracticeColumn column);

    int update(PracticeColumn column);

    int deleteById(Long columnId);

    /**
     * 获取或创建该栏目对应的“专项练习试卷”，返回 paperId
     */
    Long getOrCreatePracticePaperId(Long columnId);

    /**
     * 保存栏目下的题目关联（全量覆盖），并使已生成的 paperId 失效（下次自动重新生成）。
     */
    void saveColumnQuestions(Long columnId, List<Long> questionIds);

    /**
     * 查询栏目下已绑定的题目ID列表（按 sort_order）
     */
    List<Long> selectQuestionIdsByColumnId(Long columnId);

    /**
     * 查询题目已绑定的专项栏目ID列表
     */
    List<Long> selectBoundColumnIdsByQuestionId(Long questionId);

    /**
     * 保存题目绑定的专项栏目（全量覆盖），并使相关栏目 paperId 失效（下次自动重新生成）。
     */
    void saveQuestionColumns(Long questionId, List<Long> columnIds);

    /**
     * 下拉选项：一级分组（章节/大类）列表
     */
    List<PracticeColumn> selectGroupOptions(Integer subjectId);
}

