package com.dm.quiz.service;

import com.dm.quiz.domain.DamingSubject;

import java.util.List;

/**
 * 科目管理Service接口
 */
public interface IDamingSubjectService {

    /**
     * 查询科目信息
     */
    DamingSubject selectDamingSubjectBySubjectId(Integer subjectId);

    /**
     * 查询科目列表
     */
    List<DamingSubject> selectDamingSubjectList(DamingSubject damingSubject);

    /**
     * 查询所有有效科目
     */
    List<DamingSubject> selectActiveSubjects();

    /**
     * 新增科目
     */
    int insertDamingSubject(DamingSubject damingSubject);

    /**
     * 修改科目
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

