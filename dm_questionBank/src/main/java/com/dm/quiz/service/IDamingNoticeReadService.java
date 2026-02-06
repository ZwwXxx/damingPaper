package com.dm.quiz.service;

import com.dm.quiz.domain.DamingNoticeRead;

import java.util.List;

/**
 * 用户公告阅读状态Service接口
 *
 * @author daming
 * @date 2025-01-15
 */
public interface IDamingNoticeReadService {

    /**
     * 标记公告为已读
     *
     * @param userId 用户ID
     * @param noticeId 公告ID
     * @return 结果
     */
    int markNoticeAsRead(Long userId, Long noticeId);

    /**
     * 批量标记公告为已读
     *
     * @param userId 用户ID
     * @param noticeIds 公告ID列表
     * @return 结果
     */
    int batchMarkNoticeAsRead(Long userId, List<Long> noticeIds);

    /**
     * 查询用户是否已读指定公告
     *
     * @param userId 用户ID
     * @param noticeId 公告ID
     * @return true-已读，false-未读
     */
    boolean isNoticeRead(Long userId, Long noticeId);

    /**
     * 查询用户已读的公告ID列表
     *
     * @param userId 用户ID
     * @return 公告ID列表
     */
    List<Long> getReadNoticeIds(Long userId);

    /**
     * 查询用户未读的公告ID列表
     *
     * @param userId 用户ID
     * @param allNoticeIds 所有公告ID列表
     * @return 未读公告ID列表
     */
    List<Long> getUnreadNoticeIds(Long userId, List<Long> allNoticeIds);
}








