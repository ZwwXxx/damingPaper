package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.KnowledgeCollect;
import com.ruoyi.system.domain.KnowledgePoint;
import org.apache.ibatis.annotations.Param;

/**
 * 知识点收藏Mapper接口
 * 
 * @author ruoyi
 * @date 2025-11-28
 */
public interface KnowledgeCollectMapper 
{
    /**
     * 查询用户是否已收藏
     * 
     * @param userId 用户ID
     * @param pointId 知识点ID
     * @return 收藏记录，如果未收藏则返回null
     */
    public KnowledgeCollect selectByUserAndPoint(@Param("userId") Long userId, @Param("pointId") Long pointId);

    /**
     * 查询用户收藏的知识点列表
     * 
     * @param userId 用户ID
     * @return 知识点集合
     */
    public List<KnowledgePoint> selectCollectedPoints(Long userId);

    /**
     * 新增收藏
     * 
     * @param knowledgeCollect 收藏信息
     * @return 结果
     */
    public int insertKnowledgeCollect(KnowledgeCollect knowledgeCollect);

    /**
     * 删除收藏（取消收藏）
     * 
     * @param userId 用户ID
     * @param pointId 知识点ID
     * @return 结果
     */
    public int deleteByUserAndPoint(@Param("userId") Long userId, @Param("pointId") Long pointId);
}
