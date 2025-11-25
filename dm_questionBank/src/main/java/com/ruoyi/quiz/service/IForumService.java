package com.ruoyi.quiz.service;

import com.ruoyi.common.core.domain.entity.ForumComment;
import com.ruoyi.common.core.domain.entity.ForumPost;

import java.util.List;

/**
 * 论坛服务接口
 * 
 * @author ruoyi
 */
public interface IForumService {
    
    // ==================== 帖子相关 ====================
    
    /**
     * 查询帖子列表
     * 
     * @param post 查询条件
     * @param currentUserId 当前用户ID（用于查询点赞状态）
     * @return 帖子列表
     */
    List<ForumPost> selectForumPostList(ForumPost post, Long currentUserId);
    
    /**
     * 根据ID查询帖子详情
     * 
     * @param postId 帖子ID
     * @param currentUserId 当前用户ID（用于查询点赞状态）
     * @return 帖子详情
     */
    ForumPost selectForumPostById(Long postId, Long currentUserId);
    
    /**
     * 新增帖子
     * 
     * @param post 帖子信息
     * @return 是否成功
     */
    boolean insertForumPost(ForumPost post);
    
    /**
     * 更新帖子
     * 
     * @param post 帖子信息
     * @return 是否成功
     */
    boolean updateForumPost(ForumPost post);
    
    /**
     * 删除帖子
     * 
     * @param postId 帖子ID
     * @return 是否成功
     */
    boolean deleteForumPostById(Long postId);
    
    // ==================== 评论相关 ====================
    
    /**
     * 根据帖子ID查询评论列表（含点赞状态）
     * 
     * @param postId 帖子ID
     * @param currentUserId 当前用户ID
     * @return 评论列表
     */
    List<ForumComment> selectCommentsByPostId(Long postId, Long currentUserId);
    
    /**
     * 根据ID查询评论
     * 
     * @param commentId 评论ID
     * @return 评论信息
     */
    ForumComment selectForumCommentById(Long commentId);
    
    /**
     * 新增评论
     * 
     * @param comment 评论信息
     * @return 是否成功
     */
    boolean insertForumComment(ForumComment comment);
    
    /**
     * 删除评论
     * 
     * @param commentId 评论ID
     * @return 是否成功
     */
    boolean deleteForumCommentById(Long commentId);
    
    // ==================== 点赞相关 ====================
    
    /**
     * 点赞/取消点赞帖子
     * 
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 是否点赞（true=点赞成功，false=取消点赞成功）
     */
    boolean togglePostLike(Long postId, Long userId);
    
    /**
     * 点赞/取消点赞评论
     * 
     * @param commentId 评论ID
     * @param userId 用户ID
     * @return 是否点赞（true=点赞成功，false=取消点赞成功）
     */
    boolean toggleCommentLike(Long commentId, Long userId);
    
    // ==================== 管理功能 ====================
    
    /**
     * 批量删除帖子
     * 
     * @param postIds 帖子ID数组
     * @return 删除数量
     */
    int deleteForumPostByIds(Long[] postIds);
    
    /**
     * 更新帖子置顶状态
     * 
     * @param postId 帖子ID
     * @param isTop 是否置顶（0否 1是）
     * @return 影响行数
     */
    int updatePostTop(Long postId, Integer isTop);
    
    /**
     * 更新帖子热门状态
     * 
     * @param postId 帖子ID
     * @param isHot 是否热门（0否 1是）
     * @return 影响行数
     */
    int updatePostHot(Long postId, Integer isHot);
    
    /**
     * 更新帖子状态
     * 
     * @param postId 帖子ID
     * @param status 状态（0删除 1正常 2审核中）
     * @return 影响行数
     */
    int updatePostStatus(Long postId, Integer status);
    
    /**
     * 查询评论列表（管理用）
     * 
     * @param comment 查询条件
     * @param currentUserId 当前用户ID
     * @return 评论列表
     */
    List<ForumComment> selectForumCommentList(ForumComment comment, Long currentUserId);
    
    /**
     * 根据ID查询评论详情（管理用）
     * 
     * @param commentId 评论ID
     * @param currentUserId 当前用户ID
     * @return 评论详情
     */
    ForumComment selectForumCommentById(Long commentId, Long currentUserId);
    
    /**
     * 批量删除评论
     * 
     * @param commentIds 评论ID数组
     * @return 删除数量
     */
    int deleteForumCommentByIds(Long[] commentIds);
}
