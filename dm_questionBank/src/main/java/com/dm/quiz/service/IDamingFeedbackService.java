package com.dm.quiz.service;

import com.dm.quiz.domain.DamingFeedback;

import java.util.List;
import java.util.Map;

/**
 * 用户反馈Service接口
 *
 * @author daming
 * @date 2025-11-23
 */
public interface IDamingFeedbackService {

    /**
     * 查询反馈信息
     *
     * @param feedbackId 反馈ID
     * @return 结果
     */
    DamingFeedback selectDamingFeedbackByFeedbackId(Long feedbackId);

    /**
     * 查询反馈列表（后台管理）
     *
     * @param damingFeedback 查询条件
     * @return 集合
     */
    List<DamingFeedback> selectDamingFeedbackList(DamingFeedback damingFeedback);

    /**
     * 查询用户的反馈列表（前台）
     *
     * @param userId 用户ID
     * @return 集合
     */
    List<DamingFeedback> selectUserFeedbackList(String userId);

    /**
     * 新增反馈
     *
     * @param damingFeedback 反馈信息
     * @return 结果
     */
    int insertDamingFeedback(DamingFeedback damingFeedback);

    /**
     * 更新反馈
     *
     * @param damingFeedback 反馈信息
     * @return 结果
     */
    int updateDamingFeedback(DamingFeedback damingFeedback);

    /**
     * 批量删除反馈
     *
     * @param feedbackIds 反馈ID数组
     * @return 结果
     */
    int deleteDamingFeedbackByFeedbackIds(Long[] feedbackIds);

    /**
     * 删除反馈
     *
     * @param feedbackId 反馈ID
     * @return 结果
     */
    int deleteDamingFeedbackByFeedbackId(Long feedbackId);

    /**
     * 处理反馈（更新状态和回复）
     *
     * @param feedbackId 反馈ID
     * @param status 处理状态
     * @param replyContent 回复内容
     * @param handler 处理人
     * @return 结果
     */
    int handleFeedback(Long feedbackId, Integer status, String replyContent, String handler);

    /**
     * 统计反馈数量（按状态）
     *
     * @return 统计结果
     */
    List<Map<String, Object>> countByStatus();

    /**
     * 统计反馈数量（按类型）
     *
     * @return 统计结果
     */
    List<Map<String, Object>> countByType();
}
