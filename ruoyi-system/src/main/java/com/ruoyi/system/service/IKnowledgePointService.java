package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.KnowledgePoint;
import com.ruoyi.system.domain.dto.KnowledgePointBaseDTO;
import com.ruoyi.system.domain.dto.KnowledgePointContentDTO;

/**
 * 知识点Service接口
 * 
 * @author ruoyi
 * @date 2025-11-28
 */
public interface IKnowledgePointService 
{


    /**
     * 查询知识点内容详情（仅content等大字段）
     * 
     * @param pointId 知识点主键
     * @return 知识点内容DTO
     */
    public KnowledgePointContentDTO selectKnowledgePointContentByPointId(Long pointId);

    /**
     * 查询知识点列表（前台用）
     * 
     * @param knowledgePoint 知识点
     * @return 知识点基础信息集合
     */
    public List<KnowledgePointBaseDTO> selectKnowledgePointList(KnowledgePoint knowledgePoint);
    
    /**
     * 查询知识点列表（管理后台用）
     * 
     * @param knowledgePoint 知识点
     * @return 完整知识点集合
     */
    public List<KnowledgePoint> selectKnowledgePointListForAdmin(KnowledgePoint knowledgePoint);
    
    /**
     * 获取知识点完整信息（管理后台用）
     * 
     * @param pointId 知识点ID
     * @return 完整知识点信息
     */
    public KnowledgePoint selectKnowledgePointByPointId(Long pointId);




    /**
     * 查询推荐知识点列表
     * 
     * @param limit 限制数量
     * @return 知识点列表
     */
    public List<KnowledgePointBaseDTO> selectRecommendKnowledgePoints(Integer limit);


    /**
     * 新增知识点
     * 
     * @param knowledgePoint 知识点
     * @return 结果
     */
    public int insertKnowledgePoint(KnowledgePoint knowledgePoint);

    /**
     * 修改知识点
     * 
     * @param knowledgePoint 知识点
     * @return 结果
     */
    public int updateKnowledgePoint(KnowledgePoint knowledgePoint);

    /**
     * 批量删除知识点
     * 
     * @param pointIds 需要删除的知识点主键集合
     * @return 结果
     */
    public int deleteKnowledgePointByPointIds(Long[] pointIds);

    /**
     * 删除知识点信息
     * 
     * @param pointId 知识点主键
     * @return 结果
     */
    public int deleteKnowledgePointByPointId(Long pointId);

    /**
     * 增加浏览次数
     * 
     * @param pointId 知识点ID
     * @return 结果
     */
    public int increaseViewCount(Long pointId);

    /**
     * 审核知识点
     * 
     * @param knowledgePoint 知识点
     * @return 结果
     */
    public int auditKnowledgePoint(KnowledgePoint knowledgePoint);

    /**
     * 批量审核知识点
     * 
     * @param knowledgePoint 知识点
     * @return 结果
     */
    public int batchAuditKnowledgePoint(KnowledgePoint knowledgePoint);

    /**
     * 更新知识点状态
     * 
     * @param knowledgePoint 知识点
     * @return 结果
     */
    public int updateKnowledgePointStatus(KnowledgePoint knowledgePoint);
}
