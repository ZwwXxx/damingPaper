package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.KnowledgePoint;
import com.ruoyi.system.domain.dto.KnowledgePointBaseDTO;

/**
 * 知识点互动Service接口（点赞、收藏）
 * 
 * @author ruoyi
 * @date 2025-11-28
 */
public interface IKnowledgeInteractionService 
{
    /**
     * 切换点赞状态（点赞/取消点赞）
     * 
     * @param userId 用户ID
     * @param pointId 知识点ID
     * @return 点赞后的状态（true-已点赞 false-已取消）
     */
    public boolean toggleLike(Long userId, Long pointId);

    /**
     * 检查用户是否已点赞
     * 
     * @param userId 用户ID
     * @param pointId 知识点ID
     * @return true-已点赞 false-未点赞
     */
    public boolean isLiked(Long userId, Long pointId);

    /**
     * 切换收藏状态（收藏/取消收藏）
     * 
     * @param userId 用户ID
     * @param pointId 知识点ID
     * @return 收藏后的状态（true-已收藏 false-已取消）
     */
    public boolean toggleCollect(Long userId, Long pointId);

    /**
     * 检查用户是否已收藏
     * 
     * @param userId 用户ID
     * @param pointId 知识点ID
     * @return true-已收藏 false-未收藏
     */
    public boolean isCollected(Long userId, Long pointId);

    /**
     * 查询用户收藏的知识点列表
     * 
     * @param userId 用户ID
     * @return 知识点集合
     */
    public List<KnowledgePoint> getCollectedPoints(Long userId);

    /**
     * 查询用户在指定收藏夹中的知识点列表
     * 
     * @param userId 用户ID
     * @param folderId 收藏夹ID
     * @return 知识点集合
     */
    public List<KnowledgePoint> getCollectedPointsByFolder(Long userId, Long folderId);

    /**
     * 查询用户收藏的知识点列表（精简版）- 用于列表展示，提升性能
     * 
     * @param userId 用户ID
     * @return 知识点精简信息集合
     */
    public List<KnowledgePointBaseDTO> getCollectedPointsLite(Long userId);

    /**
     * 查询用户在指定收藏夹中的知识点列表（精简版）- 用于列表展示，提升性能
     * 
     * @param userId 用户ID
     * @param folderId 收藏夹ID
     * @return 知识点精简信息集合
     */
    public List<KnowledgePointBaseDTO> getCollectedPointsByFolderLite(Long userId, Long folderId);

    /**
     * 收藏知识点到指定收藏夹（收藏/取消收藏）
     * 
     * @param userId 用户ID
     * @param pointId 知识点ID
     * @param folderId 收藏夹ID
     * @return 收藏后的状态（true-已收藏 false-已取消）
     */
    public boolean toggleCollectToFolder(Long userId, Long pointId, Long folderId);
}
