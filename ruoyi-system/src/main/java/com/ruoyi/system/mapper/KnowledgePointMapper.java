package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.KnowledgePoint;
import com.ruoyi.system.domain.dto.KnowledgePointBaseDTO;
import com.ruoyi.system.domain.dto.KnowledgePointContentDTO;
import org.apache.ibatis.annotations.Param;

/**
 * 知识点Mapper接口
 * 
 * @author ruoyi
 * @date 2025-11-28
 */
public interface KnowledgePointMapper 
{


    /**
     * 查询知识点内容详情（仅content表）
     * 
     * @param pointId 知识点主键
     * @return 知识点内容DTO
     */
    public KnowledgePointContentDTO selectKnowledgePointContentByPointId(Long pointId);

    /**
     * 查询知识点列表
     * 
     * @param knowledgePoint 知识点
     * @return 知识点基础信息集合
     */
    public List<KnowledgePointBaseDTO> selectKnowledgePointList(KnowledgePoint knowledgePoint);




    /**
     * 查询推荐知识点列表
     * 
     * @param limit 限制数量
     * @return 知识点列表
     */
    public List<KnowledgePointBaseDTO> selectRecommendKnowledgePoints(Integer limit);


    /**
     * 新增知识点（插入base表）
     * 
     * @param knowledgePoint 知识点
     * @return 结果
     */
    public int insertKnowledgePoint(KnowledgePoint knowledgePoint);

    /**
     * 新增知识点内容（插入content表）
     * 
     * @param knowledgePoint 知识点
     * @return 结果
     */
    public int insertKnowledgePointContent(KnowledgePoint knowledgePoint);

    /**
     * 修改知识点（更新base表）
     * 
     * @param knowledgePoint 知识点
     * @return 结果
     */
    public int updateKnowledgePoint(KnowledgePoint knowledgePoint);

    /**
     * 修改知识点内容（更新content表）
     * 
     * @param knowledgePoint 知识点
     * @return 结果
     */
    public int updateKnowledgePointContent(KnowledgePoint knowledgePoint);

    /**
     * 删除知识点
     * 
     * @param pointId 知识点主键
     * @return 结果
     */
    public int deleteKnowledgePointByPointId(Long pointId);

    /**
     * 批量删除知识点
     * 
     * @param pointIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteKnowledgePointByPointIds(Long[] pointIds);

    /**
     * 增加浏览次数
     * 
     * @param pointId 知识点ID
     * @return 结果
     */
    public int increaseViewCount(Long pointId);

    /**
     * 增加点赞数
     * 
     * @param pointId 知识点ID
     * @return 结果
     */
    public int increaseLikeCount(Long pointId);

    /**
     * 减少点赞数
     * 
     * @param pointId 知识点ID
     * @return 结果
     */
    public int decreaseLikeCount(Long pointId);

    /**
     * 增加收藏数
     * 
     * @param pointId 知识点ID
     * @return 结果
     */
    public int increaseCollectCount(Long pointId);

    /**
     * 减少收藏数
     * 
     * @param pointId 知识点ID
     * @return 结果
     */
    public int decreaseCollectCount(Long pointId);

    /**
     * 增加评论数
     * 
     * @param pointId 知识点ID
     * @return 结果
     */
    public int increaseCommentCount(Long pointId);

    /**
     * 减少评论数
     * 
     * @param pointId 知识点ID
     * @return 结果
     */
    public int decreaseCommentCount(Long pointId);
}
