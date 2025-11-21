package com.dm.quiz.mapper;

import com.dm.quiz.domain.DamingQuestionFavorite;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 题目收藏 Mapper
 */
public interface DamingQuestionFavoriteMapper {

    /**
     * 新增收藏记录
     */
    int insertDamingQuestionFavorite(DamingQuestionFavorite favorite);

    /**
     * 删除收藏
     */
    int deleteByUserAndQuestion(@Param("questionId") Long questionId, @Param("createUser") String createUser);

    /**
     * 查询收藏列表
     */
    List<DamingQuestionFavorite> selectDamingQuestionFavoriteList(DamingQuestionFavorite filter);

    /**
     * 查询当前用户收藏的题目ID
     */
    List<Long> selectFavoriteQuestionIds(@Param("createUser") String createUser,
                                         @Param("paperAnswerId") Long paperAnswerId);

    /**
     * 查询是否存在
     */
    DamingQuestionFavorite selectOne(@Param("questionId") Long questionId, @Param("createUser") String createUser);
}


