package com.dm.quiz.service;

import java.util.List;
import com.dm.quiz.domain.DamingQuestion;
import com.dm.quiz.dto.ClozeQuestionCreateRequest;
import com.dm.quiz.dto.QuestionDto;
import com.dm.quiz.viewmodel.QuestionExportVO;
import com.ruoyi.common.core.domain.AjaxResult;

/**
 * 题目表Service接口
 *
 * @author zww
 * @date 2024-10-08
 */
public interface IDamingQuestionService
{
    /**
     * 查询题目表
     *
     * @param id 题目表主键
     * @return 题目表
     */
    public AjaxResult selectDamingQuestionById(Long id);

    /**
     * 查询题目表列表
     *
     * @param damingQuestion 题目表
     * @return 题目表集合
     */
    public List<DamingQuestion> selectDamingQuestionList(DamingQuestion damingQuestion);

    /**
     * 新增题目表
     *
     * @param questionDto 题目表
     * @return 结果
     */
    public Long insertDamingQuestion(com.dm.quiz.dto.QuestionDto questionDto);

    /**
     * 修改题目表
     *
     * @param questionDto 题目表
     * @return 结果
     */
    public int updateDamingQuestion(com.dm.quiz.dto.QuestionDto questionDto);

    /**
     * 批量删除题目表
     *
     * @param ids 需要删除的题目表主键集合
     * @return 结果
     */
    public int deleteDamingQuestionByIds(Long[] ids);

    /**
     * 删除题目表信息
     *
     * @param id 题目表主键
     * @return 结果
     */
    public int deleteDamingQuestionById(Long id);

    /**
     * 将question转化为questionDto
     */
    QuestionDto getQuestionDto ( DamingQuestion damingQuestion);

    /**
     * 根据题目id集合获取可导出的题目内容
     */
    List<QuestionExportVO> selectQuestionExportList(List<Long> ids);

    /**
     * 批量导入题目
     * @param questionList excel数据
     * @param operName 操作人
     * @return 导入结果
     */
    String importQuestions(List<QuestionExportVO> questionList, String operName);

    /**
     * 批量创建完形填空题（父题 + 多个子题）
     *
     * @param request 完形填空创建请求
     */
    void createClozeQuestion(ClozeQuestionCreateRequest request);

    /**
     * 查询完形填空题（父题 + 子题）
     *
     * @param parentId 父题ID
     */
    ClozeQuestionCreateRequest getClozeQuestion(Long parentId);

    /**
     * 更新完形填空题（父题 + 子题）
     *
     * <p>规则：</p>
     * <ul>
     *   <li>父题必须携带 id；questionType 会被强制为 6</li>
     *   <li>子题携带 id 则更新；不带 id 则新增</li>
     *   <li>服务端会按 children 顺序重算 clozeIndex（从1开始）</li>
     *   <li>若原有子题在本次提交中被移除，会被删除（若存在答题记录则禁止删除）</li>
     * </ul>
     */
    void updateClozeQuestion(ClozeQuestionCreateRequest request);

    /**
     * 仅更新题目的原卷题号
     */
    int updateOriginOrder(Long id, Integer originOrder);

    /**
     * 仅更新题目的考试批次（上/下半年）
     */
    int updateExamHalf(Long id, Integer examHalf);

    /**
     * 批量更新题目的考试年份与批次（为 null 的字段不修改）
     */
    int batchUpdateExamMeta(List<Long> ids, Integer examYear, Integer examHalf);
}
