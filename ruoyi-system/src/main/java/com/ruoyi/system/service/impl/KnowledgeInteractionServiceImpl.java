package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.system.domain.KnowledgeCollect;
import com.ruoyi.system.domain.KnowledgeLike;
import com.ruoyi.system.domain.KnowledgePoint;
import com.ruoyi.system.mapper.KnowledgeCollectMapper;
import com.ruoyi.system.mapper.KnowledgeLikeMapper;
import com.ruoyi.system.mapper.KnowledgePointMapper;
import com.ruoyi.system.service.IKnowledgeInteractionService;

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
     * 切换收藏状态
     */
    @Override
    @Transactional
    public boolean toggleCollect(Long userId, Long pointId)
    {
        KnowledgeCollect collect = knowledgeCollectMapper.selectByUserAndPoint(userId, pointId);
        
        if (collect != null)
        {
            // 已收藏，则取消收藏
            knowledgeCollectMapper.deleteByUserAndPoint(userId, pointId);
            knowledgePointMapper.decreaseCollectCount(pointId);
            return false;
        }
        else
        {
            // 未收藏，则添加收藏
            KnowledgeCollect newCollect = new KnowledgeCollect();
            newCollect.setUserId(userId);
            newCollect.setPointId(pointId);
            newCollect.setFolderId(0L); // 默认收藏夹
            knowledgeCollectMapper.insertKnowledgeCollect(newCollect);
            knowledgePointMapper.increaseCollectCount(pointId);
            return true;
        }
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
}
