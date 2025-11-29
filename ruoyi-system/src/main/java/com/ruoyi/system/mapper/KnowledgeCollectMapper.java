package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.KnowledgeCollect;
import com.ruoyi.system.domain.KnowledgePoint;
import com.ruoyi.system.domain.dto.KnowledgePointBaseDTO;
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
     * 查询用户在指定收藏夹中的知识点列表
     * 
     * @param userId 用户ID
     * @param folderId 收藏夹ID
     * @return 知识点集合
     */
    public List<KnowledgePoint> selectCollectedPointsByFolder(@Param("userId") Long userId, @Param("folderId") Long folderId);

    /**
     * 查询用户收藏的知识点列表（精简版 - 用于列表展示）
     * 
     * @param userId 用户ID
     * @return 知识点精简信息集合
     */
    public List<KnowledgePointBaseDTO> selectCollectedPointsLite(Long userId);

    /**
     * 查询用户在指定收藏夹中的知识点列表（精简版 - 用于列表展示）
     * 
     * @param userId 用户ID
     * @param folderId 收藏夹ID
     * @return 知识点精简信息集合
     */
    public List<KnowledgePointBaseDTO> selectCollectedPointsByFolderLite(@Param("userId") Long userId, @Param("folderId") Long folderId);

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
