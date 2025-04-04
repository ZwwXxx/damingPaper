package com.dm.quiz.mapper;

import java.util.List;
import com.dm.quiz.domain.DamingQuestion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 题目表Mapper接口
 *
 * @author zww
 * @date 2024-10-08
 */

@Mapper
public interface DamingQuestionMapper
{
    /**
     * 查询题目表
     *
     * @param id 题目表主键
     * @return 题目表
     */
    public DamingQuestion selectDamingQuestionById(Long id);

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
     * @param damingQuestion 题目表
     * @return 结果
     */
    public int insertDamingQuestion(DamingQuestion damingQuestion);

    /**
     * 修改题目表
     *
     * @param damingQuestion 题目表
     * @return 结果
     */
    public int updateDamingQuestion(DamingQuestion damingQuestion);

    /**
     * 删除题目表
     *
     * @param id 题目表主键
     * @return 结果
     */
    public int deleteDamingQuestionById(Long id);

    /**
     * 批量删除题目表
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDamingQuestionByIds(Long[] ids);

    /**
     * 根据ids查询questionList
     */
    List<DamingQuestion> selectQuestionListByIds(@Param("ids") List<Long> ids);
}
