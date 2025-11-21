package com.dm.quiz.service;

import com.dm.quiz.domain.DamingQuestionFavorite;
import com.dm.quiz.dto.QuestionFavoriteRequest;
import com.dm.quiz.viewmodel.FavoriteQuestionVO;

import java.util.List;

/**
 * 题目收藏服务
 */
public interface IDamingQuestionFavoriteService {

    /**
     * 添加收藏
     */
    void addFavorite(QuestionFavoriteRequest request);

    /**
     * 取消收藏
     */
    void removeFavorite(Long questionId, String username);

    /**
     * 获取收藏题目ID列表
     */
    List<Long> queryFavoriteQuestionIds(String username, Long paperAnswerId);

    /**
     * 查询收藏题目详情列表
     */
    List<FavoriteQuestionVO> selectFavoriteQuestionList(DamingQuestionFavorite filter);
}


