package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.KnowledgePoint;
import com.ruoyi.system.mapper.KnowledgePointMapper;
import com.ruoyi.system.service.IKnowledgePointService;

/**
 * 知识点Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-11-28
 */
@Service
public class KnowledgePointServiceImpl implements IKnowledgePointService 
{
    @Autowired
    private KnowledgePointMapper knowledgePointMapper;

    /**
     * 查询知识点
     * 
     * @param pointId 知识点主键
     * @return 知识点
     */
    @Override
    public KnowledgePoint selectKnowledgePointByPointId(Long pointId)
    {
        return knowledgePointMapper.selectKnowledgePointByPointId(pointId);
    }

    /**
     * 查询知识点列表
     * 
     * @param knowledgePoint 知识点
     * @return 知识点
     */
    @Override
    public List<KnowledgePoint> selectKnowledgePointList(KnowledgePoint knowledgePoint)
    {
        return knowledgePointMapper.selectKnowledgePointList(knowledgePoint);
    }

    /**
     * 查询热门知识点
     * 
     * @param limit 数量限制
     * @return 知识点集合
     */
    @Override
    public List<KnowledgePoint> selectHotKnowledgePoints(Integer limit)
    {
        return knowledgePointMapper.selectHotKnowledgePoints(limit);
    }

    /**
     * 查询最新知识点
     * 
     * @param limit 数量限制
     * @return 知识点集合
     */
    @Override
    public List<KnowledgePoint> selectLatestKnowledgePoints(Integer limit)
    {
        return knowledgePointMapper.selectLatestKnowledgePoints(limit);
    }

    /**
     * 查询推荐知识点
     * 
     * @param limit 数量限制
     * @return 知识点集合
     */
    @Override
    public List<KnowledgePoint> selectRecommendKnowledgePoints(Integer limit)
    {
        return knowledgePointMapper.selectRecommendKnowledgePoints(limit);
    }

    /**
     * 新增知识点
     * 
     * @param knowledgePoint 知识点
     * @return 结果
     */
    @Override
    public int insertKnowledgePoint(KnowledgePoint knowledgePoint)
    {
        // 如果摘要为空，自动从内容中提取
        if (isEmptyOrNull(knowledgePoint.getSummary()) && !isEmptyOrNull(knowledgePoint.getContent())) {
            knowledgePoint.setSummary(generateSummaryFromContent(knowledgePoint.getContent()));
        }
        return knowledgePointMapper.insertKnowledgePoint(knowledgePoint);
    }

    /**
     * 修改知识点
     * 
     * @param knowledgePoint 知识点
     * @return 结果
     */
    @Override
    public int updateKnowledgePoint(KnowledgePoint knowledgePoint)
    {
        // 如果摘要为空且内容不为空，自动从内容中提取
        if (isEmptyOrNull(knowledgePoint.getSummary()) && !isEmptyOrNull(knowledgePoint.getContent())) {
            knowledgePoint.setSummary(generateSummaryFromContent(knowledgePoint.getContent()));
        }
        return knowledgePointMapper.updateKnowledgePoint(knowledgePoint);
    }

    /**
     * 批量删除知识点
     * 
     * @param pointIds 需要删除的知识点主键
     * @return 结果
     */
    @Override
    public int deleteKnowledgePointByPointIds(Long[] pointIds)
    {
        return knowledgePointMapper.deleteKnowledgePointByPointIds(pointIds);
    }

    /**
     * 删除知识点信息
     * 
     * @param pointId 知识点主键
     * @return 结果
     */
    @Override
    public int deleteKnowledgePointByPointId(Long pointId)
    {
        return knowledgePointMapper.deleteKnowledgePointByPointId(pointId);
    }

    /**
     * 增加浏览次数
     * 
     * @param pointId 知识点ID
     * @return 结果
     */
    @Override
    public int increaseViewCount(Long pointId)
    {
        return knowledgePointMapper.increaseViewCount(pointId);
    }

    /**
     * 审核知识点
     * 
     * @param knowledgePoint 知识点
     * @return 结果
     */
    @Override
    public int auditKnowledgePoint(KnowledgePoint knowledgePoint)
    {
        return knowledgePointMapper.updateKnowledgePoint(knowledgePoint);
    }

    /**
     * 批量审核知识点
     * 
     * @param knowledgePoint 知识点
     * @return 结果
     */
    @Override
    public int batchAuditKnowledgePoint(KnowledgePoint knowledgePoint)
    {
        // 这里可以实现批量审核逻辑
        // 暂时使用单个审核的方式
        return knowledgePointMapper.updateKnowledgePoint(knowledgePoint);
    }

    /**
     * 更新知识点状态
     * 
     * @param knowledgePoint 知识点
     * @return 结果
     */
    @Override
    public int updateKnowledgePointStatus(KnowledgePoint knowledgePoint)
    {
        return knowledgePointMapper.updateKnowledgePoint(knowledgePoint);
    }
    
    /**
     * 判断字符串是否为空或null
     */
    private boolean isEmptyOrNull(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    /**
     * 从内容中生成摘要
     * @param content 原始内容
     * @return 生成的摘要
     */
    private String generateSummaryFromContent(String content) {
        if (isEmptyOrNull(content)) {
            return "";
        }
        
        // 清除 Markdown 标记和 HTML 标签
        String cleanContent = content
            .replaceAll("#+\\s*", "")  // 移除标题标记 #
            .replaceAll("\\*\\*([^*]+)\\*\\*", "$1")  // 移除粗体 **text**
            .replaceAll("\\*([^*]+)\\*", "$1")  // 移除斜体 *text*
            .replaceAll("`([^`]+)`", "$1")  // 移除行内代码 `code`
            .replaceAll("\\[([^\\]]+)\\]\\([^)]+\\)", "$1")  // 移除链接 [text](url)
            .replaceAll("!\\[([^\\]]*)\\]\\([^)]+\\)", "")  // 移除图片 ![alt](url)
            .replaceAll("```[\\s\\S]*?```", "")  // 移除代码块
            .replaceAll("<[^>]+>", "")  // 移除HTML标签
            .replaceAll("\\n+", " ")  // 将换行符替换为空格
            .trim();
        
        // 提取前150个字符作为摘要
        int maxLength = 150;
        if (cleanContent.length() <= maxLength) {
            return cleanContent;
        }
        
        // 尽量在句号、问号、感叹号处截断
        String truncated = cleanContent.substring(0, maxLength);
        int lastPunctuation = Math.max(
            Math.max(truncated.lastIndexOf('。'), truncated.lastIndexOf('？')),
            truncated.lastIndexOf('！')
        );
        
        if (lastPunctuation > 50) { // 确保不会太短
            return truncated.substring(0, lastPunctuation + 1);
        } else {
            // 如果没有合适的标点，在最后一个空格处截断
            int lastSpace = truncated.lastIndexOf(' ');
            if (lastSpace > 50) {
                return truncated.substring(0, lastSpace) + "...";
            } else {
                return truncated + "...";
            }
        }
    }
}
