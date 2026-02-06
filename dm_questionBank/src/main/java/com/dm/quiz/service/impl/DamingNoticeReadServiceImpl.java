package com.dm.quiz.service.impl;

import com.dm.quiz.domain.DamingNoticeRead;
import com.dm.quiz.mapper.DamingNoticeReadMapper;
import com.dm.quiz.service.IDamingNoticeReadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 用户公告阅读状态Service实现
 *
 * @author daming
 * @date 2025-01-15
 */
@Service
public class DamingNoticeReadServiceImpl implements IDamingNoticeReadService {

    @Autowired
    private DamingNoticeReadMapper damingNoticeReadMapper;

    @Override
    public int markNoticeAsRead(Long userId, Long noticeId) {
        // 先查询是否已存在
        DamingNoticeRead exist = damingNoticeReadMapper.selectDamingNoticeReadByUserAndNotice(userId, noticeId);
        if (exist != null) {
            // 已存在，更新阅读时间
            exist.setReadTime(new Date());
            return 1; // 已存在，不需要重复插入
        }

        // 不存在，新增记录
        DamingNoticeRead noticeRead = new DamingNoticeRead();
        noticeRead.setUserId(userId);
        noticeRead.setNoticeId(noticeId);
        noticeRead.setReadTime(new Date());
        return damingNoticeReadMapper.insertDamingNoticeRead(noticeRead);
    }

    @Override
    public int batchMarkNoticeAsRead(Long userId, List<Long> noticeIds) {
        if (noticeIds == null || noticeIds.isEmpty()) {
            return 0;
        }

        List<DamingNoticeRead> list = new ArrayList<>();
        Date now = new Date();
        for (Long noticeId : noticeIds) {
            // 先查询是否已存在
            DamingNoticeRead exist = damingNoticeReadMapper.selectDamingNoticeReadByUserAndNotice(userId, noticeId);
            if (exist == null) {
                // 不存在，添加到批量插入列表
                DamingNoticeRead noticeRead = new DamingNoticeRead();
                noticeRead.setUserId(userId);
                noticeRead.setNoticeId(noticeId);
                noticeRead.setReadTime(now);
                list.add(noticeRead);
            }
        }

        if (list.isEmpty()) {
            return 0;
        }

        return damingNoticeReadMapper.batchInsertDamingNoticeRead(list);
    }

    @Override
    public boolean isNoticeRead(Long userId, Long noticeId) {
        DamingNoticeRead noticeRead = damingNoticeReadMapper.selectDamingNoticeReadByUserAndNotice(userId, noticeId);
        return noticeRead != null;
    }

    @Override
    public List<Long> getReadNoticeIds(Long userId) {
        return damingNoticeReadMapper.selectReadNoticeIdsByUserId(userId);
    }

    @Override
    public List<Long> getUnreadNoticeIds(Long userId, List<Long> allNoticeIds) {
        if (allNoticeIds == null || allNoticeIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 获取已读的公告ID列表
        List<Long> readNoticeIds = getReadNoticeIds(userId);
        if (readNoticeIds == null || readNoticeIds.isEmpty()) {
            return new ArrayList<>(allNoticeIds);
        }

        // 计算未读的公告ID列表
        List<Long> unreadNoticeIds = new ArrayList<>();
        for (Long noticeId : allNoticeIds) {
            if (!readNoticeIds.contains(noticeId)) {
                unreadNoticeIds.add(noticeId);
            }
        }

        return unreadNoticeIds;
    }
}








