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
}
