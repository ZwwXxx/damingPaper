package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.system.domain.KnowledgeCollect;
import com.ruoyi.system.domain.KnowledgeLike;
import com.ruoyi.system.domain.KnowledgePoint;
import com.ruoyi.system.domain.KnowledgeFolder;
import com.ruoyi.system.domain.dto.KnowledgePointListDTO;
import com.ruoyi.system.mapper.KnowledgeCollectMapper;
import com.ruoyi.system.mapper.KnowledgeLikeMapper;
import com.ruoyi.system.mapper.KnowledgePointMapper;
import com.ruoyi.system.mapper.KnowledgeFolderMapper;
import com.ruoyi.system.service.IKnowledgeInteractionService;
import com.ruoyi.system.service.IKnowledgeFolderService;

/**
 * 知识点互动Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-11-28
 */
@Service
public class KnowledgeInteractionServiceImpl implements IKnowledgeInteractionService 
{
    @Autowired
    private KnowledgeLikeMapper knowledgeLikeMapper;

    @Autowired
    private KnowledgeCollectMapper knowledgeCollectMapper;

    @Autowired
    private KnowledgePointMapper knowledgePointMapper;
    
    @Autowired
    private KnowledgeFolderMapper knowledgeFolderMapper;
    
    @Autowired
    private IKnowledgeFolderService knowledgeFolderService;

    /**
     * 切换点赞状态
     */
    @Override
    @Transactional
    public boolean toggleLike(Long userId, Long pointId)
    {
        KnowledgeLike like = knowledgeLikeMapper.selectByUserAndPoint(userId, pointId);
        
        if (like != null)
        {
            // 已点赞，则取消点赞
            knowledgeLikeMapper.deleteByUserAndPoint(userId, pointId);
            knowledgePointMapper.decreaseLikeCount(pointId);
            return false;
        }
        else
        {
            // 未点赞，则添加点赞
            KnowledgeLike newLike = new KnowledgeLike();
            newLike.setUserId(userId);
            newLike.setPointId(pointId);
            knowledgeLikeMapper.insertKnowledgeLike(newLike);
            knowledgePointMapper.increaseLikeCount(pointId);
            return true;
        }
    }

    /**
     * 检查是否已点赞
     */
    @Override
    public boolean isLiked(Long userId, Long pointId)
    {
        return knowledgeLikeMapper.selectByUserAndPoint(userId, pointId) != null;
    }

    /**
     * 切换收藏状态（收藏到默认收藏夹）
     */
    @Override
    @Transactional
    public boolean toggleCollect(Long userId, Long pointId)
    {
        // 获取或创建默认收藏夹
        KnowledgeFolder defaultFolder = knowledgeFolderService.getOrCreateDefaultFolder(userId);
        return toggleCollectToFolder(userId, pointId, defaultFolder.getFolderId());
    }

    /**
     * 检查是否已收藏
     */
    @Override
    public boolean isCollected(Long userId, Long pointId)
    {
        return knowledgeCollectMapper.selectByUserAndPoint(userId, pointId) != null;
    }

    /**
     * 查询用户收藏的知识点
     */
    @Override
    public List<KnowledgePoint> getCollectedPoints(Long userId)
    {
        return knowledgeCollectMapper.selectCollectedPoints(userId);
    }

    /**
     * 查询用户在指定收藏夹中的知识点列表
     */
    @Override
    public List<KnowledgePoint> getCollectedPointsByFolder(Long userId, Long folderId)
    {
        return knowledgeCollectMapper.selectCollectedPointsByFolder(userId, folderId);
    }

    /**
     * 查询用户收藏的知识点列表（精简版）
     */
    @Override
    public List<KnowledgePointListDTO> getCollectedPointsLite(Long userId)
    {
        List<KnowledgePointListDTO> list = knowledgeCollectMapper.selectCollectedPointsLite(userId);
        fillUserStatusForDTO(list, userId);
        return list;
    }

    /**
     * 查询用户在指定收藏夹中的知识点列表（精简版）
     */
    @Override
    public List<KnowledgePointListDTO> getCollectedPointsByFolderLite(Long userId, Long folderId)
    {
        List<KnowledgePointListDTO> list = knowledgeCollectMapper.selectCollectedPointsByFolderLite(userId, folderId);
        fillUserStatusForDTO(list, userId);
        return list;
    }

    /**
     * 为精简DTO填充用户交互状态
     */
    private void fillUserStatusForDTO(List<KnowledgePointListDTO> list, Long userId)
    {
        try {
            for (KnowledgePointListDTO dto : list) {
                dto.setIsLiked(isLiked(userId, dto.getPointId()));
                dto.setIsCollected(isCollected(userId, dto.getPointId()));
            }
        } catch (Exception e) {
            // 异常情况下设置默认状态
            for (KnowledgePointListDTO dto : list) {
                dto.setIsLiked(false);
                dto.setIsCollected(false);
            }
        }
    }

    /**
     * 收藏知识点到指定收藏夹
     */
    @Override
    @Transactional
    public boolean toggleCollectToFolder(Long userId, Long pointId, Long folderId)
    {
        KnowledgeCollect existingCollect = knowledgeCollectMapper.selectByUserAndPoint(userId, pointId);
        
        if (existingCollect != null)
        {
            // 已收藏，则取消收藏
            // 更新收藏夹数量（减少）
            knowledgeFolderMapper.updateFolderCollectCount(existingCollect.getFolderId(), -1);
            // 删除收藏记录
            knowledgeCollectMapper.deleteByUserAndPoint(userId, pointId);
            // 更新知识点收藏数
            knowledgePointMapper.decreaseCollectCount(pointId);
            return false;
        }
        else
        {
            // 未收藏，则添加收藏到指定收藏夹
            KnowledgeCollect newCollect = new KnowledgeCollect();
            newCollect.setUserId(userId);
            newCollect.setPointId(pointId);
            newCollect.setFolderId(folderId);
            knowledgeCollectMapper.insertKnowledgeCollect(newCollect);
            // 更新收藏夹数量（增加）
            knowledgeFolderMapper.updateFolderCollectCount(folderId, 1);
            // 更新知识点收藏数
            knowledgePointMapper.increaseCollectCount(pointId);
            return true;
        }
    }
}
