package com.ruoyi.system.service.impl;

import java.util.List;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.system.mapper.KnowledgeCommentMapper;
import com.ruoyi.system.mapper.KnowledgeCommentLikeMapper;
import com.ruoyi.system.domain.KnowledgeComment;
import com.ruoyi.system.domain.KnowledgeCommentLike;
import com.ruoyi.system.service.IKnowledgeCommentService;

/**
 * 知识点评论Service业务层处理
 * 
 * @author daming
 * @date 2025-11-28
 */
@Service
public class KnowledgeCommentServiceImpl implements IKnowledgeCommentService 
{
    @Autowired
    private KnowledgeCommentMapper knowledgeCommentMapper;

    @Autowired
    private KnowledgeCommentLikeMapper knowledgeCommentLikeMapper;

    /**
     * 查询知识点评论
     * 
     * @param commentId 知识点评论主键
     * @return 知识点评论
     */
    @Override
    public KnowledgeComment selectKnowledgeCommentByCommentId(Long commentId)
    {
        return knowledgeCommentMapper.selectKnowledgeCommentByCommentId(commentId);
    }

    /**
     * 查询知识点评论列表
     * 
     * @param knowledgeComment 知识点评论
     * @return 知识点评论
     */
    @Override
    public List<KnowledgeComment> selectKnowledgeCommentList(KnowledgeComment knowledgeComment)
    {
        return knowledgeCommentMapper.selectKnowledgeCommentList(knowledgeComment);
    }

    /**
     * 查询知识点评论树形结构（包含子评论）
     * 
     * @param pointId 知识点ID
     * @param currentUserId 当前用户ID（用于判断点赞状态）
     * @return 评论树形结构
     */
    @Override
    public List<KnowledgeComment> selectCommentTreeByPointId(Long pointId, Long currentUserId)
    {
        // 查询一级评论
        List<KnowledgeComment> rootComments = knowledgeCommentMapper.selectRootCommentsByPointId(pointId);
        
        // 为每个一级评论查询子评论
        for (KnowledgeComment rootComment : rootComments) {
            // 设置用户点赞状态
            if (currentUserId != null) {
                KnowledgeCommentLike like = knowledgeCommentLikeMapper.selectByUserIdAndCommentId(currentUserId, rootComment.getCommentId());
                rootComment.setHasLiked(like != null);
            } else {
                rootComment.setHasLiked(false);
            }
            
            // 查询子评论
            List<KnowledgeComment> children = knowledgeCommentMapper.selectChildCommentsByParentId(rootComment.getCommentId());
            for (KnowledgeComment child : children) {
                // 设置子评论的点赞状态
                if (currentUserId != null) {
                    KnowledgeCommentLike like = knowledgeCommentLikeMapper.selectByUserIdAndCommentId(currentUserId, child.getCommentId());
                    child.setHasLiked(like != null);
                } else {
                    child.setHasLiked(false);
                }
            }
            rootComment.setChildren(children);
        }
        
        return rootComments;
    }

    /**
     * 新增知识点评论
     * 
     * @param knowledgeComment 知识点评论
     * @return 结果
     */
    @Override
    public int insertKnowledgeComment(KnowledgeComment knowledgeComment)
    {
        // 设置默认值
        if (knowledgeComment.getLikeCount() == null) {
            knowledgeComment.setLikeCount(0L);
        }
        if (knowledgeComment.getStatus() == null) {
            knowledgeComment.setStatus(1);
        }
        if (knowledgeComment.getParentId() == null) {
            knowledgeComment.setParentId(0L);
        }
        
        return knowledgeCommentMapper.insertKnowledgeComment(knowledgeComment);
    }

    /**
     * 修改知识点评论
     * 
     * @param knowledgeComment 知识点评论
     * @return 结果
     */
    @Override
    public int updateKnowledgeComment(KnowledgeComment knowledgeComment)
    {
        return knowledgeCommentMapper.updateKnowledgeComment(knowledgeComment);
    }

    /**
     * 批量删除知识点评论
     * 
     * @param commentIds 需要删除的知识点评论主键
     * @return 结果
     */
    @Override
    public int deleteKnowledgeCommentByCommentIds(Long[] commentIds)
    {
        return knowledgeCommentMapper.deleteKnowledgeCommentByCommentIds(commentIds);
    }

    /**
     * 删除知识点评论信息
     * 
     * @param commentId 知识点评论主键
     * @return 结果
     */
    @Override
    public int deleteKnowledgeCommentByCommentId(Long commentId)
    {
        return knowledgeCommentMapper.deleteKnowledgeCommentByCommentId(commentId);
    }

    /**
     * 点赞/取消点赞评论
     * 
     * @param userId 用户ID
     * @param commentId 评论ID
     * @return 是否点赞（true已点赞，false已取消）
     */
    @Override
    @Transactional
    public boolean toggleCommentLike(Long userId, Long commentId)
    {
        // 检查是否已点赞
        KnowledgeCommentLike existingLike = knowledgeCommentLikeMapper.selectByUserIdAndCommentId(userId, commentId);
        
        if (existingLike != null) {
            // 已点赞，取消点赞
            knowledgeCommentLikeMapper.deleteByUserIdAndCommentId(userId, commentId);
            knowledgeCommentMapper.decreaseLikeCount(commentId);
            return false;
        } else {
            // 未点赞，添加点赞
            KnowledgeCommentLike like = new KnowledgeCommentLike();
            like.setUserId(userId);
            like.setCommentId(commentId);
            knowledgeCommentLikeMapper.insertKnowledgeCommentLike(like);
            knowledgeCommentMapper.increaseLikeCount(commentId);
            return true;
        }
    }

    /**
     * 统计知识点评论总数
     * 
     * @param pointId 知识点ID
     * @return 评论总数
     */
    @Override
    public int countCommentsByPointId(Long pointId)
    {
        return knowledgeCommentMapper.countCommentsByPointId(pointId);
    }
}
