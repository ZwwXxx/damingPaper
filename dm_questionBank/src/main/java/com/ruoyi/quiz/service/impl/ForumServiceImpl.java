package com.ruoyi.quiz.service.impl;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.core.domain.entity.ForumComment;
import com.ruoyi.common.core.domain.entity.ForumLike;
import com.ruoyi.common.core.domain.entity.ForumPost;
import com.ruoyi.common.utils.oss.OssSignUrlHelper;
import com.ruoyi.quiz.mapper.ForumCommentMapper;
import com.ruoyi.quiz.mapper.ForumLikeMapper;
import com.ruoyi.quiz.mapper.ForumPostMapper;
import com.ruoyi.quiz.service.IForumService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(ForumServiceImpl.class);

    @Autowired
    private ForumPostMapper forumPostMapper;

    @Autowired
    private ForumCommentMapper forumCommentMapper;

    @Autowired
    private ForumLikeMapper forumLikeMapper;

    @Autowired
    private OssSignUrlHelper ossSignUrlHelper;

    // ==================== 帖子相关 ====================

    @Override
    public List<ForumPost> selectForumPostList(ForumPost post, Long currentUserId) {
        List<ForumPost> posts = forumPostMapper.selectForumPostList(post);

        // 处理图片JSON字符串转List，并生成签名URL
        for (ForumPost p : posts) {
            if (p.getImagesJson() != null && !p.getImagesJson().isEmpty()) {
                List<String> objectNames = JSON.parseArray(p.getImagesJson(), String.class);
                // ⭐ 将ObjectName转换为签名URL
                p.setImages(ossSignUrlHelper.convertToSignedUrls(objectNames));
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

        // 处理图片JSON，并生成签名URL
        if (post.getImagesJson() != null && !post.getImagesJson().isEmpty()) {
            List<String> objectNames = JSON.parseArray(post.getImagesJson(), String.class);
            // ⭐ 将ObjectName转换为签名URL
            post.setImages(ossSignUrlHelper.convertToSignedUrls(objectNames));
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
    public ForumComment selectForumCommentById(Long commentId) {
        return forumCommentMapper.selectForumCommentById(commentId);
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

        int deletedCount = 1; // 当前评论

        // 如果是一级评论（parentId为null），需要同时删除所有子评论
        if (comment.getParentId() == null) {
            // 查询所有子评论
            ForumComment queryParam = new ForumComment();
            queryParam.setParentId(commentId);
            List<ForumComment> children = forumCommentMapper.selectForumCommentList(queryParam);

            // 删除所有子评论的点赞
            for (ForumComment child : children) {
                forumLikeMapper.deleteLikesByTarget(child.getCommentId(), 2);
            }

            // 删除所有子评论
            if (!children.isEmpty()) {
                Long[] childIds = children.stream()
                    .map(ForumComment::getCommentId)
                    .toArray(Long[]::new);
                deletedCount += forumCommentMapper.deleteForumCommentByIds(childIds);
            }
        }

        // 删除当前评论的所有点赞
        forumLikeMapper.deleteLikesByTarget(commentId, 2);

        // 删除当前评论
        boolean success = forumCommentMapper.deleteForumCommentById(commentId) > 0;

        if (success) {
            // 减少帖子评论数（包括当前评论和所有子评论）
            for (int i = 0; i < deletedCount; i++) {
                forumPostMapper.decrementCommentCount(comment.getPostId());
            }
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
    
    // ==================== 管理功能 ====================
    
    @Override
    @Transactional
    public int deleteForumPostByIds(Long[] postIds) {
        int count = 0;
        for (Long postId : postIds) {
            if (deleteForumPostById(postId)) {
                count++;
            }
        }
        return count;
    }
    
    @Override
    public int updatePostTop(Long postId, Integer isTop) {
        return forumPostMapper.updatePostTop(postId, isTop);
    }
    
    @Override
    public int updatePostHot(Long postId, Integer isHot) {
        return forumPostMapper.updatePostHot(postId, isHot);
    }
    
    @Override
    public int updatePostStatus(Long postId, Integer status) {
        return forumPostMapper.updatePostStatus(postId, status);
    }
    
    @Override
    public List<ForumComment> selectForumCommentList(ForumComment comment, Long currentUserId) {
        List<ForumComment> comments = forumCommentMapper.selectForumCommentList(comment);
        
        // 如果有当前用户ID，批量查询点赞状态
        if (currentUserId != null && !comments.isEmpty()) {
            List<Long> commentIds = comments.stream()
                    .map(ForumComment::getCommentId)
                    .collect(Collectors.toList());
            List<Long> likedCommentIds = forumLikeMapper.batchCheckLikeExists(currentUserId, commentIds, 2);
            
            for (ForumComment c : comments) {
                c.setHasLiked(likedCommentIds.contains(c.getCommentId()));
            }
        }
        
        return comments;
    }
    
    @Override
    public ForumComment selectForumCommentById(Long commentId, Long currentUserId) {
        ForumComment comment = forumCommentMapper.selectForumCommentById(commentId);
        
        // 查询点赞状态
        if (comment != null && currentUserId != null) {
            int exists = forumLikeMapper.checkLikeExists(currentUserId, commentId, 2);
            comment.setHasLiked(exists > 0);
        }
        
        return comment;
    }
    
    @Override
    @Transactional
    public int deleteForumCommentByIds(Long[] commentIds) {
        int count = 0;
        for (Long commentId : commentIds) {
            if (deleteForumCommentById(commentId)) {
                count++;
            }
        }
        return count;
    }

}
