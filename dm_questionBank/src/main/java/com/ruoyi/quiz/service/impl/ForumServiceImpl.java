package com.ruoyi.quiz.service.impl;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.core.domain.entity.ForumComment;
import com.ruoyi.common.core.domain.entity.ForumLike;
import com.ruoyi.common.core.domain.entity.ForumPost;
import com.ruoyi.quiz.mapper.ForumCommentMapper;
import com.ruoyi.quiz.mapper.ForumLikeMapper;
import com.ruoyi.quiz.mapper.ForumPostMapper;
import com.ruoyi.quiz.service.IForumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 论坛服务实现
 * 
 * @author ruoyi
 */
@Service
public class ForumServiceImpl implements IForumService {
    
    @Autowired
    private ForumPostMapper forumPostMapper;
    
    @Autowired
    private ForumCommentMapper forumCommentMapper;
    
    @Autowired
    private ForumLikeMapper forumLikeMapper;
    
    // ==================== 帖子相关 ====================
    
    @Override
    public List<ForumPost> selectForumPostList(ForumPost post, Long currentUserId) {
        List<ForumPost> posts = forumPostMapper.selectForumPostList(post);
        
        // 处理图片JSON字符串转List
        for (ForumPost p : posts) {
            if (p.getImagesJson() != null && !p.getImagesJson().isEmpty()) {
                p.setImages(JSON.parseArray(p.getImagesJson(), String.class));
            }
        }
        
        // 批量查询点赞状态
        if (currentUserId != null && !posts.isEmpty()) {
            List<Long> postIds = posts.stream().map(ForumPost::getPostId).collect(Collectors.toList());
            List<Long> likedPostIds = forumLikeMapper.batchCheckLikeExists(currentUserId, postIds, 1);
            
            for (ForumPost p : posts) {
                p.setHasLiked(likedPostIds.contains(p.getPostId()));
            }
        }
        
        return posts;
    }
    
    @Override
    public ForumPost selectForumPostById(Long postId, Long currentUserId) {
        ForumPost post = forumPostMapper.selectForumPostById(postId);
        if (post == null) {
            return null;
        }
        
        // 处理图片JSON
        if (post.getImagesJson() != null && !post.getImagesJson().isEmpty()) {
            post.setImages(JSON.parseArray(post.getImagesJson(), String.class));
        }
        
        // 查询点赞状态
        if (currentUserId != null) {
            int count = forumLikeMapper.checkLikeExists(currentUserId, postId, 1);
            post.setHasLiked(count > 0);
        }
        
        // 增加浏览次数
        forumPostMapper.incrementViewCount(postId);
        
        return post;
    }
    
    @Override
    @Transactional
    public boolean insertForumPost(ForumPost post) {
        // 处理图片List转JSON
        if (post.getImages() != null && !post.getImages().isEmpty()) {
            post.setImagesJson(JSON.toJSONString(post.getImages()));
        }
        
        // 初始化默认值
        if (post.getViewCount() == null) post.setViewCount(0);
        if (post.getLikeCount() == null) post.setLikeCount(0);
        if (post.getCommentCount() == null) post.setCommentCount(0);
        if (post.getIsTop() == null) post.setIsTop(0);
        if (post.getIsHot() == null) post.setIsHot(0);
        if (post.getStatus() == null) post.setStatus(1);
        
        return forumPostMapper.insertForumPost(post) > 0;
    }
    
    @Override
    @Transactional
    public boolean updateForumPost(ForumPost post) {
        // 处理图片List转JSON
        if (post.getImages() != null) {
            post.setImagesJson(JSON.toJSONString(post.getImages()));
        }
        
        return forumPostMapper.updateForumPost(post) > 0;
    }
    
    @Override
    @Transactional
    public boolean deleteForumPostById(Long postId) {
        // 删除帖子的所有评论
        forumCommentMapper.deleteCommentsByPostId(postId);
        
        // 删除帖子的所有点赞
        forumLikeMapper.deleteLikesByTarget(postId, 1);
        
        // 删除帖子
        return forumPostMapper.deleteForumPostById(postId) > 0;
    }
    
    // ==================== 评论相关 ====================
    
    @Override
    public List<ForumComment> selectCommentsByPostId(Long postId, Long currentUserId) {
        List<ForumComment> comments = forumCommentMapper.selectCommentsByPostId(postId);
        
        // 批量查询点赞状态
        if (currentUserId != null && !comments.isEmpty()) {
            List<Long> commentIds = comments.stream().map(ForumComment::getCommentId).collect(Collectors.toList());
            List<Long> likedCommentIds = forumLikeMapper.batchCheckLikeExists(currentUserId, commentIds, 2);
            
            for (ForumComment c : comments) {
                c.setHasLiked(likedCommentIds.contains(c.getCommentId()));
            }
        }
        
        // 构建评论树结构
        return buildCommentTree(comments);
    }
    
    /**
     * 构建评论树结构
     */
    private List<ForumComment> buildCommentTree(List<ForumComment> comments) {
        List<ForumComment> rootComments = new ArrayList<>();
        
        for (ForumComment comment : comments) {
            if (comment.getParentId() == null) {
                // 根评论
                comment.setChildren(new ArrayList<>());
                rootComments.add(comment);
            }
        }
        
        // 添加子评论
        for (ForumComment comment : comments) {
            if (comment.getParentId() != null) {
                for (ForumComment root : rootComments) {
                    if (root.getCommentId().equals(comment.getParentId())) {
                        root.getChildren().add(comment);
                        break;
                    }
                }
            }
        }
        
        return rootComments;
    }
    
    @Override
    @Transactional
    public boolean insertForumComment(ForumComment comment) {
        // 初始化默认值
        if (comment.getLikeCount() == null) comment.setLikeCount(0);
        if (comment.getStatus() == null) comment.setStatus(1);
        
        boolean success = forumCommentMapper.insertForumComment(comment) > 0;
        
        if (success) {
            // 增加帖子评论数
            forumPostMapper.incrementCommentCount(comment.getPostId());
        }
        
        return success;
    }
    
    @Override
    @Transactional
    public boolean deleteForumCommentById(Long commentId) {
        ForumComment comment = forumCommentMapper.selectForumCommentById(commentId);
        if (comment == null) {
            return false;
        }
        
        // 删除评论的所有点赞
        forumLikeMapper.deleteLikesByTarget(commentId, 2);
        
        // 删除评论
        boolean success = forumCommentMapper.deleteForumCommentById(commentId) > 0;
        
        if (success) {
            // 减少帖子评论数
            forumPostMapper.decrementCommentCount(comment.getPostId());
        }
        
        return success;
    }
    
    // ==================== 点赞相关 ====================
    
    @Override
    @Transactional
    public boolean togglePostLike(Long postId, Long userId) {
        int exists = forumLikeMapper.checkLikeExists(userId, postId, 1);
        
        if (exists > 0) {
            // 已点赞，取消点赞
            forumLikeMapper.deleteForumLike(userId, postId, 1);
            forumPostMapper.decrementLikeCount(postId);
            return false;
        } else {
            // 未点赞，添加点赞
            ForumLike like = new ForumLike();
            like.setUserId(userId);
            like.setTargetId(postId);
            like.setTargetType(1);
            forumLikeMapper.insertForumLike(like);
            forumPostMapper.incrementLikeCount(postId);
            return true;
        }
    }
    
    @Override
    @Transactional
    public boolean toggleCommentLike(Long commentId, Long userId) {
        int exists = forumLikeMapper.checkLikeExists(userId, commentId, 2);
        
        if (exists > 0) {
            // 已点赞，取消点赞
            forumLikeMapper.deleteForumLike(userId, commentId, 2);
            forumCommentMapper.decrementLikeCount(commentId);
            return false;
        } else {
            // 未点赞，添加点赞
            ForumLike like = new ForumLike();
            like.setUserId(userId);
            like.setTargetId(commentId);
            like.setTargetType(2);
            forumLikeMapper.insertForumLike(like);
            forumCommentMapper.incrementLikeCount(commentId);
            return true;
        }
    }
}
