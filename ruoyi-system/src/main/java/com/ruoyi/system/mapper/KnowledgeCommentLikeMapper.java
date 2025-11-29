package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.KnowledgeCommentLike;

/**
 * 知识点评论点赞Mapper接口
 * 
 * @author daming
 * @date 2025-11-28
 */
public interface KnowledgeCommentLikeMapper 
{
    /**
     * 查询知识点评论点赞
     * 
     * @param likeId 知识点评论点赞主键
     * @return 知识点评论点赞
     */
    public KnowledgeCommentLike selectKnowledgeCommentLikeByLikeId(Long likeId);

    /**
     * 查询知识点评论点赞列表
     * 
     * @param knowledgeCommentLike 知识点评论点赞
     * @return 知识点评论点赞集合
     */
    public List<KnowledgeCommentLike> selectKnowledgeCommentLikeList(KnowledgeCommentLike knowledgeCommentLike);

    /**
     * 查询用户是否已点赞评论
     * 
     * @param userId 用户ID
     * @param commentId 评论ID
     * @return 点赞记录
     */
    public KnowledgeCommentLike selectByUserIdAndCommentId(@Param("userId") Long userId, @Param("commentId") Long commentId);

    /**
     * 新增知识点评论点赞
     * 
     * @param knowledgeCommentLike 知识点评论点赞
     * @return 结果
     */
    public int insertKnowledgeCommentLike(KnowledgeCommentLike knowledgeCommentLike);

    /**
     * 删除知识点评论点赞
     * 
     * @param likeId 知识点评论点赞主键
     * @return 结果
     */
    public int deleteKnowledgeCommentLikeByLikeId(Long likeId);

    /**
     * 根据用户ID和评论ID删除点赞记录
     * 
     * @param userId 用户ID
     * @param commentId 评论ID
     * @return 结果
     */
    public int deleteByUserIdAndCommentId(@Param("userId") Long userId, @Param("commentId") Long commentId);

    /**
     * 批量删除知识点评论点赞
     * 
     * @param likeIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteKnowledgeCommentLikeByLikeIds(Long[] likeIds);
}
