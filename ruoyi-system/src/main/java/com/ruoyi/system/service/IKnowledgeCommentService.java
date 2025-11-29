package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.KnowledgeComment;

/**
 * 知识点评论Service接口
 * 
 * @author daming
 * @date 2025-11-28
 */
public interface IKnowledgeCommentService 
{
    /**
     * 查询知识点评论
     * 
     * @param commentId 知识点评论主键
     * @return 知识点评论
     */
    public KnowledgeComment selectKnowledgeCommentByCommentId(Long commentId);

    /**
     * 查询知识点评论列表
     * 
     * @param knowledgeComment 知识点评论
     * @return 知识点评论集合
     */
    public List<KnowledgeComment> selectKnowledgeCommentList(KnowledgeComment knowledgeComment);

    /**
     * 查询知识点评论树形结构（包含子评论）
     * 
     * @param pointId 知识点ID
     * @param currentUserId 当前用户ID（用于判断点赞状态）
     * @return 评论树形结构
     */
    public List<KnowledgeComment> selectCommentTreeByPointId(Long pointId, Long currentUserId);

    /**
     * 新增知识点评论
     * 
     * @param knowledgeComment 知识点评论
     * @return 结果
     */
    public int insertKnowledgeComment(KnowledgeComment knowledgeComment);

    /**
     * 修改知识点评论
     * 
     * @param knowledgeComment 知识点评论
     * @return 结果
     */
    public int updateKnowledgeComment(KnowledgeComment knowledgeComment);

    /**
     * 批量删除知识点评论
     * 
     * @param commentIds 需要删除的知识点评论主键集合
     * @return 结果
     */
    public int deleteKnowledgeCommentByCommentIds(Long[] commentIds);

    /**
     * 删除知识点评论信息
     * 
     * @param commentId 知识点评论主键
     * @return 结果
     */
    public int deleteKnowledgeCommentByCommentId(Long commentId);

    /**
     * 点赞/取消点赞评论
     * 
     * @param userId 用户ID
     * @param commentId 评论ID
     * @return 是否点赞（true已点赞，false已取消）
     */
    public boolean toggleCommentLike(Long userId, Long commentId);

    /**
     * 统计知识点评论总数
     * 
     * @param pointId 知识点ID
     * @return 评论总数
     */
    public int countCommentsByPointId(Long pointId);
}
