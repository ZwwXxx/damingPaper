package com.ruoyi.web.controller.quiz.student;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.KnowledgeChapter;
import com.ruoyi.system.domain.KnowledgeComment;
import com.ruoyi.system.domain.KnowledgePoint;
import com.ruoyi.system.domain.KnowledgeSubject;
import com.ruoyi.common.core.domain.model.DamingUser;
import com.ruoyi.system.service.IKnowledgeChapterService;
import com.ruoyi.system.service.IKnowledgeCommentService;
import com.ruoyi.system.service.IKnowledgeInteractionService;
import com.ruoyi.system.service.IKnowledgePointService;
import com.ruoyi.system.service.IKnowledgeSubjectService;
import com.dm.quiz.service.IDamingUserService;

/**
 * 前台知识点Controller
 * 
 * @author ruoyi
 * @date 2025-11-28
 */
@RestController
@RequestMapping("/student/knowledge")
public class KnowledgeController extends BaseController
{
    @Autowired
    private IKnowledgePointService knowledgePointService;

    @Autowired
    private IKnowledgeSubjectService knowledgeSubjectService;

    @Autowired
    private IKnowledgeChapterService knowledgeChapterService;

    @Autowired
    private IKnowledgeInteractionService knowledgeInteractionService;

    @Autowired
    private IKnowledgeCommentService knowledgeCommentService;

    @Autowired
    private IDamingUserService damingUserService;

    /**
     * 查询科目列表
     */
    @GetMapping("/subjects")
    public AjaxResult listSubjects()
    {
        KnowledgeSubject query = new KnowledgeSubject();
        query.setStatus(1); // 只查询启用的科目
        List<KnowledgeSubject> list = knowledgeSubjectService.selectKnowledgeSubjectList(query);
        return success(list);
    }

    /**
     * 查询章节树（根据科目ID）
     */
    @GetMapping("/chapters/{subjectId}")
    public AjaxResult getChapterTree(@PathVariable Long subjectId)
    {
        List<KnowledgeChapter> tree = knowledgeChapterService.buildChapterTree(subjectId);
        return success(tree);
    }

    /**
     * 查询知识点列表（分页）
     */
    @GetMapping("/points")
    public TableDataInfo listPoints(KnowledgePoint knowledgePoint)
    {
        startPage();
        // 只查询已发布的知识点
        knowledgePoint.setStatus(1);
        List<KnowledgePoint> list = knowledgePointService.selectKnowledgePointList(knowledgePoint);
        fillUserStatus(list);
        return getDataTable(list);
    }

    /**
     * 获取知识点详细信息
     */
    @GetMapping("/point/{pointId}")
    public AjaxResult getPointInfo(@PathVariable Long pointId)
    {
        // 增加浏览次数
        knowledgePointService.increaseViewCount(pointId);
        KnowledgePoint point = knowledgePointService.selectKnowledgePointByPointId(pointId);
        
        // 填充用户的点赞和收藏状态
        try {
            Long userId = SecurityUtils.getUserId();
            point.setIsLiked(knowledgeInteractionService.isLiked(userId, pointId));
            point.setIsCollected(knowledgeInteractionService.isCollected(userId, pointId));
        } catch (Exception e) {
            // 未登录用户，默认为false
            point.setIsLiked(false);
            point.setIsCollected(false);
        }
        
        return success(point);
    }

    /**
     * 获取热门知识点
     */
    @GetMapping("/point/hot")
    public AjaxResult getHotPoints(@RequestParam(defaultValue = "10") Integer limit)
    {
        List<KnowledgePoint> list = knowledgePointService.selectHotKnowledgePoints(limit);
        fillUserStatus(list);
        return success(list);
    }

    /**
     * 获取最新知识点
     */
    @GetMapping("/point/latest")
    public AjaxResult getLatestPoints(@RequestParam(defaultValue = "10") Integer limit)
    {
        List<KnowledgePoint> list = knowledgePointService.selectLatestKnowledgePoints(limit);
        fillUserStatus(list);
        return success(list);
    }

    /**
     * 获取推荐知识点
     */
    @GetMapping("/point/recommend")
    public AjaxResult getRecommendPoints(@RequestParam(defaultValue = "10") Integer limit)
    {
        List<KnowledgePoint> list = knowledgePointService.selectRecommendKnowledgePoints(limit);
        fillUserStatus(list);
        return success(list);
    }

    /**
     * 点赞/取消点赞
     */
    @PostMapping("/like/{pointId}")
    public AjaxResult toggleLike(@PathVariable Long pointId)
    {
        Long userId = SecurityUtils.getUserId();
        boolean isLiked = knowledgeInteractionService.toggleLike(userId, pointId);
        return AjaxResult.success(isLiked ? "点赞成功" : "取消点赞", isLiked);
    }

    /**
     * 收藏/取消收藏
     */
    @PostMapping("/collect/{pointId}")
    public AjaxResult toggleCollect(@PathVariable Long pointId)
    {
        Long userId = SecurityUtils.getUserId();
        boolean isCollected = knowledgeInteractionService.toggleCollect(userId, pointId);
        return AjaxResult.success(isCollected ? "收藏成功" : "取消收藏", isCollected);
    }

    /**
     * 获取我的收藏列表
     */
    @GetMapping("/my/collects")
    public AjaxResult getMyCollects()
    {
        Long userId = SecurityUtils.getUserId();
        List<KnowledgePoint> list = knowledgeInteractionService.getCollectedPoints(userId);
        fillUserStatus(list);
        return success(list);
    }

    /**
     * 获取知识点评论列表
     */
    @GetMapping("/comments/{pointId}")
    public AjaxResult getComments(@PathVariable Long pointId)
    {
        try {
            Long userId = SecurityUtils.getUserId();
            List<KnowledgeComment> comments = knowledgeCommentService.selectCommentTreeByPointId(pointId, userId);
            return AjaxResult.success("查询成功", comments);
        } catch (Exception e) {
            // 未登录用户
            List<KnowledgeComment> comments = knowledgeCommentService.selectCommentTreeByPointId(pointId, null);
            return AjaxResult.success("查询成功", comments);
        }
    }

    /**
     * 添加知识点评论
     */
    @PostMapping("/comment")
    public AjaxResult addComment(@RequestBody KnowledgeComment comment)
    {
        try {
            Long userId = SecurityUtils.getUserId();
            if (userId == null) {
                return AjaxResult.error("请先登录");
            }
            
            DamingUser user = damingUserService.selectDamingUserByUserId(userId);
            if (user == null) {
                return AjaxResult.error("用户信息不存在，请重新登录");
            }
            
            // 设置评论信息
            comment.setUserId(userId);
            comment.setUserName(user.getUserName());
            comment.setNickName(user.getNickName());
            comment.setAvatar(user.getAvatar());
            comment.setCreateBy(user.getUserName());
            
            int result = knowledgeCommentService.insertKnowledgeComment(comment);
            if (result > 0) {
                return AjaxResult.success("评论成功");
            } else {
                return AjaxResult.error("评论失败");
            }
        } catch (RuntimeException e) {
            // 认证相关异常
            logger.error("用户认证失败: {}", e.getMessage());
            return AjaxResult.error("请先登录");
        } catch (Exception e) {
            // 其他异常
            logger.error("添加评论失败: {}", e.getMessage(), e);
            return AjaxResult.error("系统异常，请稍后重试");
        }
    }

    /**
     * 删除知识点评论
     */
    @DeleteMapping("/comment/{commentId}")
    public AjaxResult deleteComment(@PathVariable Long commentId)
    {
        try {
            Long userId = SecurityUtils.getUserId();
            if (userId == null) {
                return AjaxResult.error("请先登录");
            }
            
            KnowledgeComment comment = knowledgeCommentService.selectKnowledgeCommentByCommentId(commentId);
            if (comment == null) {
                return AjaxResult.error("评论不存在");
            }
            
            // 只能删除自己的评论
            if (!comment.getUserId().equals(userId)) {
                return AjaxResult.error("无权删除此评论");
            }
            
            int result = knowledgeCommentService.deleteKnowledgeCommentByCommentId(commentId);
            if (result > 0) {
                return AjaxResult.success("删除成功");
            } else {
                return AjaxResult.error("删除失败");
            }
        } catch (RuntimeException e) {
            logger.error("用户认证失败: {}", e.getMessage());
            return AjaxResult.error("请先登录");
        } catch (Exception e) {
            logger.error("删除评论失败: {}", e.getMessage(), e);
            return AjaxResult.error("系统异常，请稍后重试");
        }
    }

    /**
     * 点赞/取消点赞评论
     */
    @PostMapping("/comment/like/{commentId}")
    public AjaxResult toggleCommentLike(@PathVariable Long commentId)
    {
        try {
            Long userId = SecurityUtils.getUserId();
            if (userId == null) {
                return AjaxResult.error("请先登录");
            }
            
            boolean isLiked = knowledgeCommentService.toggleCommentLike(userId, commentId);
            return AjaxResult.success(isLiked ? "点赞成功" : "取消点赞", isLiked);
        } catch (RuntimeException e) {
            logger.error("用户认证失败: {}", e.getMessage());
            return AjaxResult.error("请先登录");
        } catch (Exception e) {
            logger.error("点赞评论失败: {}", e.getMessage(), e);
            return AjaxResult.error("系统异常，请稍后重试");
        }
    }

    /**
     * 填充用户点赞和收藏状态（私有方法）
     */
    private void fillUserStatus(List<KnowledgePoint> list)
    {
        try {
            Long userId = SecurityUtils.getUserId();
            for (KnowledgePoint point : list) {
                point.setIsLiked(knowledgeInteractionService.isLiked(userId, point.getPointId()));
                point.setIsCollected(knowledgeInteractionService.isCollected(userId, point.getPointId()));
            }
        } catch (Exception e) {
            // 未登录用户，默认为false
            for (KnowledgePoint point : list) {
                point.setIsLiked(false);
                point.setIsCollected(false);
            }
        }
    }

    /**
     * 用户发布知识点
     */
    @PostMapping("/publish")
    public AjaxResult publishKnowledge(@RequestBody KnowledgePoint knowledgePoint)
    {
        try {
            // 检查用户是否登录
            Long userId = SecurityUtils.getUserId();
            if (userId == null) {
                return error("请先登录");
            }

            // 获取用户信息
            DamingUser user = damingUserService.selectDamingUserByUserId(userId);
            if (user == null) {
                return error("用户信息不存在");
            }

            // 设置发布者信息
            knowledgePoint.setCreateBy(user.getUserName());
            knowledgePoint.setUpdateBy(user.getUserName());
            
            // 设置审核状态为待审核
            knowledgePoint.setAuditStatus(0); // 0-待审核
            knowledgePoint.setStatus(1); // 1-正常
            
            // 初始化统计数据
            knowledgePoint.setViewCount(0);
            knowledgePoint.setLikeCount(0);
            knowledgePoint.setCollectCount(0);

            // 保存知识点
            int result = knowledgePointService.insertKnowledgePoint(knowledgePoint);
            
            if (result > 0) {
                return success("知识点发布成功，请等待管理员审核");
            } else {
                return error("发布失败");
            }
            
        } catch (Exception e) {
            logger.error("发布知识点失败", e);
            return error("发布失败：" + e.getMessage());
        }
    }
}
