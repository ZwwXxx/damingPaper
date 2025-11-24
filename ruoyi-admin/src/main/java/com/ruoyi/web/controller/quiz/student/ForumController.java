package com.ruoyi.web.controller.quiz.student;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.ForumComment;
import com.ruoyi.common.core.domain.entity.ForumPost;
import com.ruoyi.common.core.domain.entity.LoginUser;
import com.ruoyi.common.core.domain.model.DamingUser;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.quiz.service.IForumService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 论坛控制器
 * 
 * @author ruoyi
 */
@Slf4j
@RestController
@RequestMapping("/quiz/forum")
public class ForumController extends BaseController {
    
    @Autowired
    private IForumService forumService;
    
    @Autowired
    private TokenService tokenService;
    
    /**
     * 获取当前登录用户ID
     */
    private Long getCurrentUserId(HttpServletRequest request) {
        LoginUser loginUser = tokenService.getLoginUser(request);
        if (loginUser != null && loginUser.getDamingUser() != null) {
            return loginUser.getDamingUser().getUserId();
        }
        return null;
    }
    
    /**
     * 获取当前登录用户信息
     */
    private DamingUser getCurrentUser(HttpServletRequest request) {
        LoginUser loginUser = tokenService.getLoginUser(request);
        if (loginUser != null) {
            return loginUser.getDamingUser();
        }
        return null;
    }
    
    // ==================== 帖子相关 ====================
    
    /**
     * 查询帖子列表
     */
    @GetMapping("/posts")
    public AjaxResult getPostList(ForumPost post, HttpServletRequest request) {
        log.info("查询帖子列表");
        Long currentUserId = getCurrentUserId(request);
        List<ForumPost> list = forumService.selectForumPostList(post, currentUserId);
        return AjaxResult.success().put("list", list);
    }
    
    /**
     * 根据ID查询帖子详情
     */
    @GetMapping("/posts/{postId}")
    public AjaxResult getPostDetail(@PathVariable Long postId, HttpServletRequest request) {
        log.info("查询帖子详情 - postId: {}", postId);
        Long currentUserId = getCurrentUserId(request);
        ForumPost post = forumService.selectForumPostById(postId, currentUserId);
        if (post == null) {
            return AjaxResult.error("帖子不存在或已删除");
        }
        return AjaxResult.success().put("post", post);
    }
    
    /**
     * 新增帖子
     */
    @PostMapping("/posts")
    public AjaxResult addPost(@RequestBody ForumPost post, HttpServletRequest request) {
        DamingUser user = getCurrentUser(request);
        if (user == null) {
            return AjaxResult.error(401, "请先登录");
        }
        
        log.info("发布帖子 - 用户: {}, 标题: {}", user.getUserName(), post.getTitle());
        
        // 设置发帖人信息
        post.setUserId(user.getUserId());
        post.setUserName(user.getUserName());
        post.setNickName(user.getNickName());
        post.setAvatar(user.getAvatar());
        
        boolean success = forumService.insertForumPost(post);
        return success ? AjaxResult.success("发帖成功") : AjaxResult.error("发帖失败");
    }
    
    /**
     * 更新帖子
     */
    @PutMapping("/posts/{postId}")
    public AjaxResult updatePost(@PathVariable Long postId, 
                                  @RequestBody ForumPost post, 
                                  HttpServletRequest request) {
        DamingUser user = getCurrentUser(request);
        if (user == null) {
            return AjaxResult.error(401, "请先登录");
        }
        
        // 检查是否是帖子作者
        ForumPost existPost = forumService.selectForumPostById(postId, null);
        if (existPost == null) {
            return AjaxResult.error("帖子不存在");
        }
        if (!existPost.getUserId().equals(user.getUserId())) {
            return AjaxResult.error(403, "无权修改他人的帖子");
        }
        
        log.info("更新帖子 - postId: {}, 用户: {}", postId, user.getUserName());
        
        post.setPostId(postId);
        boolean success = forumService.updateForumPost(post);
        return success ? AjaxResult.success("更新成功") : AjaxResult.error("更新失败");
    }
    
    /**
     * 删除帖子
     */
    @DeleteMapping("/posts/{postId}")
    public AjaxResult deletePost(@PathVariable Long postId, HttpServletRequest request) {
        DamingUser user = getCurrentUser(request);
        if (user == null) {
            return AjaxResult.error(401, "请先登录");
        }
        
        // 检查是否是帖子作者
        ForumPost existPost = forumService.selectForumPostById(postId, null);
        if (existPost == null) {
            return AjaxResult.error("帖子不存在");
        }
        if (!existPost.getUserId().equals(user.getUserId())) {
            return AjaxResult.error(403, "无权删除他人的帖子");
        }
        
        log.info("删除帖子 - postId: {}, 用户: {}", postId, user.getUserName());
        
        boolean success = forumService.deleteForumPostById(postId);
        return success ? AjaxResult.success("删除成功") : AjaxResult.error("删除失败");
    }
    
    // ==================== 评论相关 ====================
    
    /**
     * 查询帖子的评论列表
     */
    @GetMapping("/posts/{postId}/comments")
    public AjaxResult getCommentList(@PathVariable Long postId, HttpServletRequest request) {
        log.info("查询评论列表 - postId: {}", postId);
        Long currentUserId = getCurrentUserId(request);
        List<ForumComment> list = forumService.selectCommentsByPostId(postId, currentUserId);
        return AjaxResult.success().put("list", list);
    }
    
    /**
     * 新增评论
     */
    @PostMapping("/comments")
    public AjaxResult addComment(@RequestBody ForumComment comment, HttpServletRequest request) {
        DamingUser user = getCurrentUser(request);
        if (user == null) {
            return AjaxResult.error(401, "请先登录");
        }
        
        log.info("发表评论 - 用户: {}, 帖子ID: {}", user.getUserName(), comment.getPostId());
        
        // 设置评论人信息
        comment.setUserId(user.getUserId());
        comment.setUserName(user.getUserName());
        comment.setNickName(user.getNickName());
        comment.setAvatar(user.getAvatar());
        
        boolean success = forumService.insertForumComment(comment);
        return success ? AjaxResult.success("评论成功") : AjaxResult.error("评论失败");
    }
    
    /**
     * 删除评论
     */
    @DeleteMapping("/comments/{commentId}")
    public AjaxResult deleteComment(@PathVariable Long commentId, HttpServletRequest request) {
        DamingUser user = getCurrentUser(request);
        if (user == null) {
            return AjaxResult.error(401, "请先登录");
        }
        
        // 检查是否是评论作者
        ForumComment existComment = forumService.selectForumCommentById(commentId);
        if (existComment == null) {
            return AjaxResult.error("评论不存在");
        }
        if (!existComment.getUserId().equals(user.getUserId())) {
            return AjaxResult.error(403, "无权删除他人的评论");
        }
        
        log.info("删除评论 - commentId: {}, 用户: {}", commentId, user.getUserName());
        
        boolean success = forumService.deleteForumCommentById(commentId);
        return success ? AjaxResult.success("删除成功") : AjaxResult.error("删除失败");
    }
    
    // ==================== 点赞相关 ====================
    
    /**
     * 点赞/取消点赞帖子
     */
    @PostMapping("/posts/{postId}/like")
    public AjaxResult togglePostLike(@PathVariable Long postId, HttpServletRequest request) {
        DamingUser user = getCurrentUser(request);
        if (user == null) {
            return AjaxResult.error(401, "请先登录");
        }
        
        boolean liked = forumService.togglePostLike(postId, user.getUserId());
        String msg = liked ? "点赞成功" : "取消点赞";
        log.info("{} - postId: {}, 用户: {}", msg, postId, user.getUserName());
        
        return AjaxResult.success(msg).put("liked", liked);
    }
    
    /**
     * 点赞/取消点赞评论
     */
    @PostMapping("/comments/{commentId}/like")
    public AjaxResult toggleCommentLike(@PathVariable Long commentId, HttpServletRequest request) {
        DamingUser user = getCurrentUser(request);
        if (user == null) {
            return AjaxResult.error(401, "请先登录");
        }
        
        boolean liked = forumService.toggleCommentLike(commentId, user.getUserId());
        String msg = liked ? "点赞成功" : "取消点赞";
        log.info("{} - commentId: {}, 用户: {}", msg, commentId, user.getUserName());
        
        return AjaxResult.success(msg).put("liked", liked);
    }
}
