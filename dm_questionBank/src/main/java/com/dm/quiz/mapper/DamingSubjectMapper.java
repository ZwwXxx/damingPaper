package com.dm.quiz.mapper;

import com.dm.quiz.domain.DamingSubject;

import java.util.List;

/**
 * 科目管理Mapper接口
 */
public interface DamingSubjectMapper {

    /**
     * 查询科目信息
     *
     * @param subjectId 主键
     * @return 结果
     */
    DamingSubject selectDamingSubjectBySubjectId(Integer subjectId);

    /**
     * 根据科目名称查询
     *
     * @param subjectName 科目名称
     * @return 结果
     */
    DamingSubject selectDamingSubjectBySubjectName(String subjectName);

    /**
     * 查询科目列表
     *
     * @param damingSubject 查询条件
     * @return 集合
     */
    List<DamingSubject> selectDamingSubjectList(DamingSubject damingSubject);

    /**
     * 查询所有有效科目
     *
     * @return 集合
     */
    List<DamingSubject> selectActiveSubjects();

    /**
     * 新增科目
     */
    int insertDamingSubject(DamingSubject damingSubject);

    /**
     * 更新科目
     */
    int updateDamingSubject(DamingSubject damingSubject);

    /**
     * 批量删除科目
     */
    int deleteDamingSubjectBySubjectIds(Integer[] subjectIds);

    /**
     * 删除科目
     */
    int deleteDamingSubjectBySubjectId(Integer subjectId);
}

