package com.dm.quiz.service.impl;

import com.dm.quiz.domain.DamingFeedback;
import com.dm.quiz.mapper.DamingFeedbackMapper;
import com.dm.quiz.service.IDamingFeedbackService;
import com.ruoyi.common.utils.oss.OssSignUrlHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 用户反馈Service实现
 *
 * @author daming
 * @date 2025-11-23
 */
@Service
public class DamingFeedbackServiceImpl implements IDamingFeedbackService {

    @Autowired
    private DamingFeedbackMapper damingFeedbackMapper;
    
    @Autowired
    private OssSignUrlHelper ossSignUrlHelper;

    /**
     * 查询反馈信息
     *
     * @param feedbackId 反馈ID
     * @return 结果
     */
    @Override
    public DamingFeedback selectDamingFeedbackByFeedbackId(Long feedbackId) {
        DamingFeedback feedback = damingFeedbackMapper.selectDamingFeedbackByFeedbackId(feedbackId);
        if (feedback != null) {
            // ⭐ 将逗号分隔的ObjectName转换为签名URL
            processImages(feedback);
        }
        return feedback;
    }

    /**
     * 查询反馈列表（后台管理）
     *
     * @param damingFeedback 查询条件
     * @return 集合
     */
    @Override
    public List<DamingFeedback> selectDamingFeedbackList(DamingFeedback damingFeedback) {
        List<DamingFeedback> list = damingFeedbackMapper.selectDamingFeedbackList(damingFeedback);
        // ⭐ 批量处理图片签名URL
        list.forEach(this::processImages);
        return list;
    }

    /**
     * 查询用户的反馈列表（前台）
     *
     * @param userId 用户ID
     * @return 集合
     */
    @Override
    public List<DamingFeedback> selectUserFeedbackList(String userId) {
        List<DamingFeedback> list = damingFeedbackMapper.selectUserFeedbackList(userId);
        // ⭐ 批量处理图片签名URL
        list.forEach(this::processImages);
        return list;
    }

    /**
     * 新增反馈
     *
     * @param damingFeedback 反馈信息
     * @return 结果
     */
    @Override
    public int insertDamingFeedback(DamingFeedback damingFeedback) {
        // 设置默认值
        if (damingFeedback.getStatus() == null) {
            damingFeedback.setStatus(0); // 默认待处理
        }
        if (damingFeedback.getPriority() == null) {
            damingFeedback.setPriority(1); // 默认低优先级
        }
        if (damingFeedback.getDelFlag() == null) {
            damingFeedback.setDelFlag("0");
        }
        return damingFeedbackMapper.insertDamingFeedback(damingFeedback);
    }

    /**
     * 更新反馈
     *
     * @param damingFeedback 反馈信息
     * @return 结果
     */
    @Override
    public int updateDamingFeedback(DamingFeedback damingFeedback) {
        return damingFeedbackMapper.updateDamingFeedback(damingFeedback);
    }

    /**
     * 批量删除反馈
     *
     * @param feedbackIds 反馈ID数组
     * @return 结果
     */
    @Override
    public int deleteDamingFeedbackByFeedbackIds(Long[] feedbackIds) {
        return damingFeedbackMapper.deleteDamingFeedbackByFeedbackIds(feedbackIds);
    }

    /**
     * 删除反馈
     *
     * @param feedbackId 反馈ID
     * @return 结果
     */
    @Override
    public int deleteDamingFeedbackByFeedbackId(Long feedbackId) {
        return damingFeedbackMapper.deleteDamingFeedbackByFeedbackId(feedbackId);
    }

    /**
     * 处理反馈（更新状态和回复）
     *
     * @param feedbackId 反馈ID
     * @param status 处理状态
     * @param replyContent 回复内容
     * @param handler 处理人
     * @return 结果
     */
    @Override
    public int handleFeedback(Long feedbackId, Integer status, String replyContent, String handler) {
        DamingFeedback feedback = new DamingFeedback();
        feedback.setFeedbackId(feedbackId);
        feedback.setStatus(status);
        feedback.setReplyContent(replyContent);
        feedback.setHandler(handler);
        feedback.setHandleTime(new Date()); // 设置处理时间
        return damingFeedbackMapper.updateDamingFeedback(feedback);
    }

    /**
     * 统计反馈数量（按状态）
     *
     * @return 统计结果
     */
    @Override
    public List<Map<String, Object>> countByStatus() {
        return damingFeedbackMapper.countByStatus();
    }

    /**
     * 统计反馈数量（按类型）
     *
     * @return 统计结果
     */
    @Override
    public List<Map<String, Object>> countByType() {
        return damingFeedbackMapper.countByType();
    }
    
    /**
     * 处理反馈中的图片字段，将ObjectName转换为签名URL
     * 
     * @param feedback 反馈实体
     */
    private void processImages(DamingFeedback feedback) {
        if (feedback != null && feedback.getImages() != null && !feedback.getImages().trim().isEmpty()) {
            // ⭐ 将逗号分隔的ObjectName字符串转换为签名URL字符串
            String signedUrls = ossSignUrlHelper.convertCommaSeparatedToSignedUrls(feedback.getImages());
            feedback.setImages(signedUrls);
        }
    }
}
