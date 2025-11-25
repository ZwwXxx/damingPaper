package com.ruoyi.quiz.mapper;

import com.ruoyi.common.core.domain.entity.ForumComment;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 论坛评论Mapper接口
 * 
 * @author ruoyi
 */
public interface ForumCommentMapper {
    
    /**
     * 查询评论列表
     * 
     * @param comment 查询条件
     * @return 评论列表
     */
    List<ForumComment> selectForumCommentList(ForumComment comment);
    
    /**
     * 根据帖子ID查询评论列表
     * 
     * @param postId 帖子ID
     * @return 评论列表
     */
    List<ForumComment> selectCommentsByPostId(Long postId);
    
    /**
     * 根据ID查询评论详情
     * 
     * @param commentId 评论ID
     * @return 评论详情
     */
    ForumComment selectForumCommentById(Long commentId);
    
    /**
     * 新增评论
     * 
     * @param comment 评论信息
     * @return 影响行数
     */
    int insertForumComment(ForumComment comment);
    
    /**
     * 更新评论
     * 
     * @param comment 评论信息
     * @return 影响行数
     */
    int updateForumComment(ForumComment comment);
    
    /**
     * 删除评论
     * 
     * @param commentId 评论ID
     * @return 影响行数
     */
    int deleteForumCommentById(Long commentId);
    
    /**
     * 批量删除评论
     * 
     * @param commentIds 评论ID数组
     * @return 影响行数
     */
    int deleteForumCommentByIds(Long[] commentIds);
    
    /**
     * 增加点赞数
     * 
     * @param commentId 评论ID
     * @return 影响行数
     */
    int incrementLikeCount(Long commentId);
    
    /**
     * 减少点赞数
     * 
     * @param commentId 评论ID
     * @return 影响行数
     */
    int decrementLikeCount(Long commentId);
    
    /**
     * 根据帖子ID删除所有评论
     * 
     * @param postId 帖子ID
     * @return 影响行数
     */
    int deleteCommentsByPostId(Long postId);
}
