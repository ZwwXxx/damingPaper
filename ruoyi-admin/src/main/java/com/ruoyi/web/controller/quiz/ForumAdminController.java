package com.ruoyi.web.controller.quiz;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.ForumComment;
import com.ruoyi.common.core.domain.entity.ForumPost;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.quiz.service.IForumService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

/**
 * 论坛管理控制器
 * 
 * @author ruoyi
 */
@Slf4j
@RestController
@RequestMapping("/quiz/admin/forum")
public class ForumAdminController extends BaseController {
    
    @Autowired
    private IForumService forumService;
    
    // ==================== 帖子管理 ====================
    
    /**
     * 查询帖子列表
     */
    @PreAuthorize("@ss.hasPermi('quiz:forum:list')")
    @GetMapping("/post/list")
    public TableDataInfo listPost(ForumPost post) {
        startPage();
        List<ForumPost> list = forumService.selectForumPostList(post, null);
        return getDataTable(list);
    }
    
    /**
     * 导出帖子列表
     */
    @PreAuthorize("@ss.hasPermi('quiz:forum:export')")
    @Log(title = "论坛帖子", businessType = BusinessType.EXPORT)
    @PostMapping("/post/export")
    public void exportPost(HttpServletResponse response, ForumPost post) {
        List<ForumPost> list = forumService.selectForumPostList(post, null);
        ExcelUtil<ForumPost> util = new ExcelUtil<ForumPost>(ForumPost.class);
        util.exportExcel(response, list, "论坛帖子数据");
    }
    
    /**
     * 获取帖子详细信息
     */
    @PreAuthorize("@ss.hasPermi('quiz:forum:query')")
    @GetMapping("/post/{postId}")
    public AjaxResult getPost(@PathVariable("postId") Long postId) {
        return success(forumService.selectForumPostById(postId, null));
    }
    
    /**
     * 删除帖子
     */
    @PreAuthorize("@ss.hasPermi('quiz:forum:remove')")
    @Log(title = "论坛帖子", businessType = BusinessType.DELETE)
    @DeleteMapping("/post/{postIds}")
    public AjaxResult removePost(@PathVariable Long[] postIds) {
        int rows = forumService.deleteForumPostByIds(postIds);
        return rows > 0 ? success() : error("删除失败");
    }
    
    /**
     * 置顶/取消置顶帖子
     */
    @PreAuthorize("@ss.hasPermi('quiz:forum:edit')")
    @Log(title = "置顶帖子", businessType = BusinessType.UPDATE)
    @PutMapping("/post/top")
    public AjaxResult toggleTop(@RequestBody ForumPost post) {
        int rows = forumService.updatePostTop(post.getPostId(), post.getIsTop());
        return rows > 0 ? success() : error("操作失败");
    }
    
    /**
     * 设置/取消热门帖子
     */
    @PreAuthorize("@ss.hasPermi('quiz:forum:edit')")
    @Log(title = "热门帖子", businessType = BusinessType.UPDATE)
    @PutMapping("/post/hot")
    public AjaxResult toggleHot(@RequestBody ForumPost post) {
        int rows = forumService.updatePostHot(post.getPostId(), post.getIsHot());
        return rows > 0 ? success() : error("操作失败");
    }
    
    /**
     * 审核帖子
     */
    @PreAuthorize("@ss.hasPermi('quiz:forum:edit')")
    @Log(title = "审核帖子", businessType = BusinessType.UPDATE)
    @PutMapping("/post/audit")
    public AjaxResult auditPost(@RequestBody ForumPost post) {
        int rows = forumService.updatePostStatus(post.getPostId(), post.getStatus());
        return rows > 0 ? success() : error("审核失败");
    }
    
    // ==================== 评论管理 ====================
    
    /**
     * 查询评论列表
     */
    @PreAuthorize("@ss.hasPermi('quiz:forum:list')")
    @GetMapping("/comment/list")
    public TableDataInfo listComment(ForumComment comment) {
        startPage();
        List<ForumComment> list = forumService.selectForumCommentList(comment, null);
        return getDataTable(list);
    }
    
    /**
     * 导出评论列表
     */
    @PreAuthorize("@ss.hasPermi('quiz:forum:export')")
    @Log(title = "论坛评论", businessType = BusinessType.EXPORT)
    @PostMapping("/comment/export")
    public void exportComment(HttpServletResponse response, ForumComment comment) {
        List<ForumComment> list = forumService.selectForumCommentList(comment, null);
        ExcelUtil<ForumComment> util = new ExcelUtil<ForumComment>(ForumComment.class);
        util.exportExcel(response, list, "论坛评论数据");
    }
    
    /**
     * 获取评论详细信息
     */
    @PreAuthorize("@ss.hasPermi('quiz:forum:query')")
    @GetMapping("/comment/{commentId}")
    public AjaxResult getComment(@PathVariable("commentId") Long commentId) {
        return success(forumService.selectForumCommentById(commentId, null));
    }
    
    /**
     * 删除评论
     */
    @PreAuthorize("@ss.hasPermi('quiz:forum:remove')")
    @Log(title = "论坛评论", businessType = BusinessType.DELETE)
    @DeleteMapping("/comment/{commentIds}")
    public AjaxResult removeComment(@PathVariable Long[] commentIds) {
        int rows = forumService.deleteForumCommentByIds(commentIds);
        return rows > 0 ? success() : error("删除失败");
    }
}
