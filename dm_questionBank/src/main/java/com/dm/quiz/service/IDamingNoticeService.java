package com.dm.quiz.service;

import com.dm.quiz.domain.DamingNotice;

import java.util.List;

/**
 * 公告管理Service接口
 *
 * @author daming
 * @date 2025-11-23
 */
public interface IDamingNoticeService {

    /**
     * 查询公告信息
     *
     * @param noticeId 公告ID
     * @return 结果
     */
    DamingNotice selectDamingNoticeByNoticeId(Long noticeId);

    /**
     * 查询公告列表
     *
     * @param damingNotice 查询条件
     * @return 集合
     */
    List<DamingNotice> selectDamingNoticeList(DamingNotice damingNotice);

    /**
     * 查询前台展示的公告列表（已发布）
     *
     * @param damingNotice 查询条件
     * @return 集合
     */
    List<DamingNotice> selectPublishedNoticeList(DamingNotice damingNotice);

    /**
     * 新增公告
     *
     * @param damingNotice 公告信息
     * @return 结果
     */
    int insertDamingNotice(DamingNotice damingNotice);

    /**
     * 更新公告
     *
     * @param damingNotice 公告信息
     * @return 结果
     */
    int updateDamingNotice(DamingNotice damingNotice);

    /**
     * 批量删除公告
     *
     * @param noticeIds 公告ID数组
     * @return 结果
     */
    int deleteDamingNoticeByNoticeIds(Long[] noticeIds);

    /**
     * 删除公告
     *
     * @param noticeId 公告ID
     * @return 结果
     */
    int deleteDamingNoticeByNoticeId(Long noticeId);

    /**
     * 增加浏览次数
     *
     * @param noticeId 公告ID
     * @return 结果
     */
    int incrementViewCount(Long noticeId);

    /**
     * 发布公告
     *
     * @param noticeId 公告ID
     * @return 结果
     */
    int publishNotice(Long noticeId);

    /**
     * 取消发布公告
     *
     * @param noticeId 公告ID
     * @return 结果
     */
    int unpublishNotice(Long noticeId);
}
