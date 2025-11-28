package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.KnowledgePoint;

/**
 * 知识点Service接口
 * 
 * @author ruoyi
 * @date 2025-11-28
 */
public interface IKnowledgePointService 
{
    /**
     * 查询知识点
     * 
     * @param pointId 知识点主键
     * @return 知识点
     */
    public KnowledgePoint selectKnowledgePointByPointId(Long pointId);

    /**
     * 查询知识点列表
     * 
     * @param knowledgePoint 知识点
     * @return 知识点集合
     */
    public List<KnowledgePoint> selectKnowledgePointList(KnowledgePoint knowledgePoint);

    /**
     * 查询热门知识点
     * 
     * @param limit 数量限制
     * @return 知识点集合
     */
    public List<KnowledgePoint> selectHotKnowledgePoints(Integer limit);

    /**
     * 查询最新知识点
     * 
     * @param limit 数量限制
     * @return 知识点集合
     */
    public List<KnowledgePoint> selectLatestKnowledgePoints(Integer limit);

    /**
     * 查询推荐知识点
     * 
     * @param limit 数量限制
     * @return 知识点集合
     */
    public List<KnowledgePoint> selectRecommendKnowledgePoints(Integer limit);

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
}
