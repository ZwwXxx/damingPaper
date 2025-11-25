package com.dm.quiz.service.impl;

import com.dm.quiz.domain.DamingNotice;
import com.dm.quiz.mapper.DamingNoticeMapper;
import com.dm.quiz.service.IDamingNoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 公告管理Service实现
 *
 * @author daming
 * @date 2025-11-23
 */
@Service
public class DamingNoticeServiceImpl implements IDamingNoticeService {

    @Autowired
    private DamingNoticeMapper damingNoticeMapper;

    /**
     * 查询公告信息
     *
     * @param noticeId 公告ID
     * @return 结果
     */
    @Override
    public DamingNotice selectDamingNoticeByNoticeId(Long noticeId) {
        return damingNoticeMapper.selectDamingNoticeByNoticeId(noticeId);
    }

    /**
     * 查询公告列表
     *
     * @param damingNotice 查询条件
     * @return 集合
     */
    @Override
    public List<DamingNotice> selectDamingNoticeList(DamingNotice damingNotice) {
        return damingNoticeMapper.selectDamingNoticeList(damingNotice);
    }

    /**
     * 查询前台展示的公告列表（已发布）
     *
     * @param damingNotice 查询条件
     * @return 集合
     */
    @Override
    public List<DamingNotice> selectPublishedNoticeList(DamingNotice damingNotice) {
        return damingNoticeMapper.selectPublishedNoticeList(damingNotice);
    }

    /**
     * 新增公告
     *
     * @param damingNotice 公告信息
     * @return 结果
     */
    @Override
    public int insertDamingNotice(DamingNotice damingNotice) {
        // 设置默认值
        if (damingNotice.getIsTop() == null) {
            damingNotice.setIsTop(0);
        }
        if (damingNotice.getStatus() == null) {
            damingNotice.setStatus(0); // 默认为草稿
        }
        if (damingNotice.getSortOrder() == null) {
            damingNotice.setSortOrder(0);
        }
        if (damingNotice.getViewCount() == null) {
            damingNotice.setViewCount(0);
        }
        if (damingNotice.getDelFlag() == null) {
            damingNotice.setDelFlag("0");
        }
        return damingNoticeMapper.insertDamingNotice(damingNotice);
    }

    /**
     * 更新公告
     *
     * @param damingNotice 公告信息
     * @return 结果
     */
    @Override
    public int updateDamingNotice(DamingNotice damingNotice) {
        return damingNoticeMapper.updateDamingNotice(damingNotice);
    }

    /**
     * 批量删除公告
     *
     * @param noticeIds 公告ID数组
     * @return 结果
     */
    @Override
    public int deleteDamingNoticeByNoticeIds(Long[] noticeIds) {
        return damingNoticeMapper.deleteDamingNoticeByNoticeIds(noticeIds);
    }

    /**
     * 删除公告
     *
     * @param noticeId 公告ID
     * @return 结果
     */
    @Override
    public int deleteDamingNoticeByNoticeId(Long noticeId) {
        return damingNoticeMapper.deleteDamingNoticeByNoticeId(noticeId);
    }

    /**
     * 增加浏览次数
     *
     * @param noticeId 公告ID
     * @return 结果
     */
    @Override
    public int incrementViewCount(Long noticeId) {
        return damingNoticeMapper.incrementViewCount(noticeId);
    }

    /**
     * 发布公告
     *
     * @param noticeId 公告ID
     * @return 结果
     */
    @Override
    public int publishNotice(Long noticeId) {
        DamingNotice notice = new DamingNotice();
        notice.setNoticeId(noticeId);
        notice.setStatus(1); // 设置为已发布
        notice.setPublishTime(new Date()); // 设置发布时间
        return damingNoticeMapper.updateDamingNotice(notice);
    }

    /**
     * 取消发布公告
     *
     * @param noticeId 公告ID
     * @return 结果
     */
    @Override
    public int unpublishNotice(Long noticeId) {
        DamingNotice notice = new DamingNotice();
        notice.setNoticeId(noticeId);
        notice.setStatus(0); // 设置为草稿
        return damingNoticeMapper.updateDamingNotice(notice);
    }
}
