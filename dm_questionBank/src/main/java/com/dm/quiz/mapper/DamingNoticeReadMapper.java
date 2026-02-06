package com.dm.quiz.mapper;

import com.dm.quiz.domain.DamingNoticeRead;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户公告阅读状态Mapper接口
 *
 * @author daming
 * @date 2025-01-15
 */
public interface DamingNoticeReadMapper {

    /**
     * 查询用户公告阅读状态
     *
     * @param id 主键ID
     * @return 结果
     */
    DamingNoticeRead selectDamingNoticeReadById(Long id);

    /**
     * 查询用户是否已读指定公告
     *
     * @param userId 用户ID
     * @param noticeId 公告ID
     * @return 结果
     */
    DamingNoticeRead selectDamingNoticeReadByUserAndNotice(@Param("userId") Long userId, @Param("noticeId") Long noticeId);

    /**
     * 查询用户已读的公告ID列表
     *
     * @param userId 用户ID
     * @return 公告ID列表
     */
    List<Long> selectReadNoticeIdsByUserId(Long userId);

    /**
     * 新增用户公告阅读状态
     *
     * @param damingNoticeRead 用户公告阅读状态信息
     * @return 结果
     */
    int insertDamingNoticeRead(DamingNoticeRead damingNoticeRead);

    /**
     * 批量新增用户公告阅读状态
     *
     * @param list 阅读状态列表
     * @return 结果
     */
    int batchInsertDamingNoticeRead(List<DamingNoticeRead> list);

    /**
     * 删除用户公告阅读状态
     *
     * @param id 主键ID
     * @return 结果
     */
    int deleteDamingNoticeReadById(Long id);

    /**
     * 删除用户的所有阅读记录（用于用户注销等场景）
     *
     * @param userId 用户ID
     * @return 结果
     */
    int deleteDamingNoticeReadByUserId(Long userId);
}








