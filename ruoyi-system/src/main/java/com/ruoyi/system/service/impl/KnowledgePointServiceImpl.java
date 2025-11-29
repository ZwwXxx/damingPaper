package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.KnowledgePoint;
import com.ruoyi.system.domain.dto.KnowledgePointBaseDTO;
import com.ruoyi.system.domain.dto.KnowledgePointContentDTO;
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
     * 合并基础信息和内容信息，返回完整的KnowledgePoint对象
     * 
     * @param pointId 知识点ID
     * @return 完整的知识点对象
     */
    public KnowledgePoint getCompleteKnowledgePoint(Long pointId)
    {
        // 获取基础信息 - 复用列表查询
        KnowledgePoint params = new KnowledgePoint();
        params.setPointId(pointId);
        params.setStatus(1);
        
        List<KnowledgePointBaseDTO> baseList = knowledgePointMapper.selectKnowledgePointList(params);
        if (baseList.isEmpty()) {
            return null;
        }
        
        KnowledgePointBaseDTO base = baseList.get(0);
        
        // 获取内容信息
        KnowledgePointContentDTO content = knowledgePointMapper.selectKnowledgePointContentByPointId(pointId);
        
        // 合并数据
        KnowledgePoint point = new KnowledgePoint();
        // 复制基础信息
        point.setPointId(base.getPointId());
        point.setSubjectId(base.getSubjectId());
        point.setTitle(base.getTitle());
        point.setSummary(base.getSummary());
        point.setDifficulty(base.getDifficulty());
        point.setAuthorId(base.getAuthorId());
        point.setAuthorName(base.getAuthorName());
        point.setViewCount(base.getViewCount());
        point.setLikeCount(base.getLikeCount());
        point.setCollectCount(base.getCollectCount());
        point.setCommentCount(base.getCommentCount());
        point.setIsRecommend(base.getIsRecommend());
        point.setIsTop(base.getIsTop());
        point.setStatus(base.getStatus());
        point.setAuditStatus(base.getAuditStatus());
        point.setCreateTime(base.getCreateTime());
        point.setUpdateTime(base.getUpdateTime());
        point.setPublishTime(base.getPublishTime());
        point.setSubjectName(base.getSubjectName());
        
        // 复制内容信息
        if (content != null) {
            point.setContent(content.getContent());
            point.setContentHtml(content.getContentHtml());
            point.setAuditRemark(content.getAuditRemark());
        }
        
        return point;
    }



    /**
     * 查询知识点内容详情（仅content表）
     * 
     * @param pointId 知识点主键
     * @return 知识点内容DTO
     */
    @Override
    public KnowledgePointContentDTO selectKnowledgePointContentByPointId(Long pointId)
    {
        return knowledgePointMapper.selectKnowledgePointContentByPointId(pointId);
    }

    /**
     * 查询知识点列表（前台用）
     * 
     * @param knowledgePoint 知识点
     * @return 知识点基础信息集合
     */
    @Override
    public List<KnowledgePointBaseDTO> selectKnowledgePointList(KnowledgePoint knowledgePoint)
    {
        return knowledgePointMapper.selectKnowledgePointList(knowledgePoint);
    }
    
    /**
     * 查询知识点列表（管理后台用）- 获取完整信息
     * 
     * @param knowledgePoint 知识点
     * @return 完整知识点集合
     */
    @Override
    public List<KnowledgePoint> selectKnowledgePointListForAdmin(KnowledgePoint knowledgePoint)
    {
        List<KnowledgePointBaseDTO> baseList = knowledgePointMapper.selectKnowledgePointList(knowledgePoint);
        List<KnowledgePoint> completeList = new java.util.ArrayList<>();
        
        for (KnowledgePointBaseDTO base : baseList) {
            KnowledgePoint complete = getCompleteKnowledgePoint(base.getPointId());
            if (complete != null) {
                completeList.add(complete);
            }
        }
        
        return completeList;
    }
    
    /**
     * 获取知识点完整信息（管理后台用）
     * 
     * @param pointId 知识点ID
     * @return 完整知识点信息
     */
    @Override
    public KnowledgePoint selectKnowledgePointByPointId(Long pointId)
    {
        return getCompleteKnowledgePoint(pointId);
    }




    /**
     * 查询推荐知识点列表
     * 
     * @param limit 数量限制
     * @return 知识点列表
     */
    @Override
    public List<KnowledgePointBaseDTO> selectRecommendKnowledgePoints(Integer limit)
    {
        return knowledgePointMapper.selectRecommendKnowledgePoints(limit);
    }


    /**
     * 新增知识点（分表模式：先插入base表，再插入content表）
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
        // 1. 插入base表（获取自增ID）
        int result = knowledgePointMapper.insertKnowledgePoint(knowledgePoint);
        // 2. 插入content表（使用刚获取的pointId）
        if (result > 0 && knowledgePoint.getPointId() != null) {
            knowledgePointMapper.insertKnowledgePointContent(knowledgePoint);
        }
        return result;
    }

    /**
     * 修改知识点（分表模式：同时更新base表和content表）
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
        // 1. 更新base表
        int result = knowledgePointMapper.updateKnowledgePoint(knowledgePoint);
        // 2. 更新content表
        if (knowledgePoint.getContent() != null || knowledgePoint.getContentHtml() != null || knowledgePoint.getAuditRemark() != null) {
            knowledgePointMapper.updateKnowledgePointContent(knowledgePoint);
        }
        return result;
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
