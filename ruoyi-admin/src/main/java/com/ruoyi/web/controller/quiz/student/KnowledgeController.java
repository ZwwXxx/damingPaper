package com.ruoyi.web.controller.quiz.student;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.KnowledgeComment;
import com.ruoyi.system.domain.KnowledgePoint;
import com.ruoyi.system.domain.KnowledgeSubject;
import com.ruoyi.system.domain.KnowledgeFolder;
import com.ruoyi.system.domain.dto.KnowledgePointBaseDTO;
import com.ruoyi.system.domain.dto.KnowledgePointContentDTO;
import com.ruoyi.common.core.domain.model.DamingUser;
import com.ruoyi.system.service.IKnowledgeCommentService;
import com.ruoyi.system.service.IKnowledgeInteractionService;
import com.ruoyi.system.service.IKnowledgePointService;
import com.ruoyi.system.service.impl.KnowledgePointServiceImpl;
import com.ruoyi.system.service.IKnowledgeSubjectService;
import com.ruoyi.system.service.IKnowledgeFolderService;
import com.dm.quiz.service.IDamingUserService;
import com.dm.quiz.service.IQuestionKnowledgeService;
import com.dm.quiz.service.IDamingQuestionService;
import com.dm.quiz.domain.DamingQuestion;

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
    private IKnowledgeInteractionService knowledgeInteractionService;

    @Autowired
    private IKnowledgeCommentService knowledgeCommentService;
    
    @Autowired
    private IQuestionKnowledgeService questionKnowledgeService;
    
    @Autowired
    private IDamingQuestionService damingQuestionService;

    @Autowired
    private IDamingUserService userService;

    @Autowired
    private IKnowledgeFolderService knowledgeFolderService;

    @GetMapping("/subjects")
    public AjaxResult listSubjects()
    {
        KnowledgeSubject query = new KnowledgeSubject();
        query.setStatus(1); // 只查询启用的科目
        List<KnowledgeSubject> list = knowledgeSubjectService.selectKnowledgeSubjectList(query);
        return success(list);
    }

    /**
     * 创建新科目
     */
    @PostMapping("/subject")
    public AjaxResult createSubject(@RequestBody KnowledgeSubject subject)
    {
        try {
            Long userId = SecurityUtils.getUserId();
            if (userId == null) {
                return error("请先登录");
            }

            // 获取用户信息
            DamingUser user = userService.selectDamingUserByUserId(userId);
            if (user == null) {
                return error("用户信息不存在");
            }

            // 设置创建者信息
            subject.setCreateBy(user.getUserName());
            subject.setUpdateBy(user.getUserName());
            subject.setStatus(1); // 设置为启用状态
            
            // 检查科目名称是否已存在
            KnowledgeSubject existingSubject = new KnowledgeSubject();
            existingSubject.setSubjectName(subject.getSubjectName());
            List<KnowledgeSubject> existingList = knowledgeSubjectService.selectKnowledgeSubjectList(existingSubject);
            if (existingList != null && existingList.size() > 0) {
                return error("科目名称已存在");
            }

            // 保存科目
            int result = knowledgeSubjectService.insertKnowledgeSubject(subject);
            if (result > 0) {
                return AjaxResult.success("创建科目成功", subject);
            } else {
                return error("创建科目失败");
            }
            
        } catch (Exception e) {
            logger.error("创建科目失败", e);
            return error("创建科目失败：" + e.getMessage());
        }
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
        // 使用BaseDTO查询，完全不包含content等大字段
        List<KnowledgePointBaseDTO> list = knowledgePointService.selectKnowledgePointList(knowledgePoint);
        fillUserStatusBaseDTO(list);
        return getDataTable(list);
    }


    /**
     * 获取推荐知识点（保留复杂推荐算法）
     * 注意：此接口必须放在 /point/{pointId} 之前，避免路径冲突
     */
    @GetMapping("/point/recommend")
    public AjaxResult getRecommendPoints(@RequestParam(defaultValue = "10") Integer limit)
    {
        List<KnowledgePointBaseDTO> list = knowledgePointService.selectRecommendKnowledgePoints(limit);
        fillUserStatusBaseDTO(list);
        return success(list);
    }

    /**
     * 获取知识点内容详情（详情页第二步）- 仅content等大字段
     */
    @GetMapping("/point/{pointId}/content")
    public AjaxResult getPointContent(@PathVariable Long pointId)
    {
        // 获取内容详情
        KnowledgePointContentDTO content = knowledgePointService.selectKnowledgePointContentByPointId(pointId);
        if (content == null) {
            return error("知识点内容不存在");
        }
        
        return success(content);
    }

    /**
     * 获取知识点编辑数据
     */
    @GetMapping("/point/{pointId}/edit")
    public AjaxResult getPointForEdit(@PathVariable Long pointId)
    {
        try {
            Long userId = SecurityUtils.getUserId();
            KnowledgePoint point = ((KnowledgePointServiceImpl)knowledgePointService).getCompleteKnowledgePoint(pointId);
            
            if (point == null) {
                return AjaxResult.error("知识点不存在");
            }
            
            // 检查权限 - 只有作者可以编辑
            if (!point.getAuthorId().equals(userId)) {
                return AjaxResult.error("无权编辑此知识点");
            }
            
            return success(point);
        } catch (RuntimeException e) {
            logger.error("用户认证失败: {}", e.getMessage());
            return AjaxResult.error("请先登录");
        } catch (Exception e) {
            logger.error("获取知识点编辑数据失败: {}", e.getMessage(), e);
            return AjaxResult.error("系统异常，请稍后重试");
        }
    }

    /**
     * 更新知识点
     */
    @PutMapping("/point/{pointId}")
    public AjaxResult updatePoint(@PathVariable Long pointId, @RequestBody KnowledgePoint knowledgePoint)
    {
        try {
            Long userId = SecurityUtils.getUserId();
            String username = SecurityUtils.getUsername();
            
            KnowledgePoint existingPoint = ((KnowledgePointServiceImpl)knowledgePointService).getCompleteKnowledgePoint(pointId);
            if (existingPoint == null) {
                return AjaxResult.error("知识点不存在");
            }
            
            // 检查权限 - 只有作者可以编辑
            if (!existingPoint.getAuthorId().equals(userId)) {
                return AjaxResult.error("无权编辑此知识点");
            }
            
            // 设置更新信息
            knowledgePoint.setPointId(pointId);
            knowledgePoint.setUpdateBy(username);
            knowledgePoint.setUpdateTime(new Date());
            
            // 更新后需要重新审核
            knowledgePoint.setAuditStatus(0);
            
            int result = knowledgePointService.updateKnowledgePoint(knowledgePoint);
            if (result > 0) {
                return AjaxResult.success("更新成功，请等待管理员审核");
            } else {
                return AjaxResult.error("更新失败");
            }
        } catch (RuntimeException e) {
            logger.error("用户认证失败: {}", e.getMessage());
            return AjaxResult.error("请先登录");
        } catch (Exception e) {
            logger.error("更新知识点失败: {}", e.getMessage(), e);
            return AjaxResult.error("系统异常，请稍后重试");
        }
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
     * 获取我的收藏列表（精简版 - 性能优化）
     */
    @GetMapping("/my/collects")
    public AjaxResult getMyCollects(@RequestParam(required = false) Long folderId)
    {
        Long userId = SecurityUtils.getUserId();
        List<KnowledgePointBaseDTO> list;
        
        if (folderId != null) {
            // 获取指定收藏夹的收藏（精简版）
            list = knowledgeInteractionService.getCollectedPointsByFolderLite(userId, folderId);
            logger.info("用户 {} 查询收藏夹 {} 的收藏，返回 {} 条精简数据", userId, folderId, list.size());
        } else {
            // 获取所有收藏（精简版）
            list = knowledgeInteractionService.getCollectedPointsLite(userId);
            logger.info("用户 {} 查询所有收藏，返回 {} 条精简数据", userId, list.size());
        }
        
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
            
            DamingUser user = userService.selectDamingUserByUserId(userId);
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
     * 填充用户点赞和收藏状态（BaseDTO版本）
     */
    private void fillUserStatusBaseDTO(List<KnowledgePointBaseDTO> list)
    {
        try {
            Long userId = SecurityUtils.getUserId();
            for (KnowledgePointBaseDTO point : list) {
                point.setIsLiked(knowledgeInteractionService.isLiked(userId, point.getPointId()));
                point.setIsCollected(knowledgeInteractionService.isCollected(userId, point.getPointId()));
            }
        } catch (Exception e) {
            // 未登录用户，默认为false
            for (KnowledgePointBaseDTO point : list) {
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
            DamingUser user = userService.selectDamingUserByUserId(userId);
            if (user == null) {
                return error("用户信息不存在");
            }

            // 设置发布者信息
            knowledgePoint.setCreateBy(user.getUserName());
            knowledgePoint.setUpdateBy(user.getUserName());
            knowledgePoint.setAuthorId(userId);  // 设置作者ID
            knowledgePoint.setAuthorName(user.getNickName() != null ? user.getNickName() : user.getUserName());  // 设置作者名称
            
            // 设置审核状态为待审核
            knowledgePoint.setAuditStatus(0); // 0-待审核
            knowledgePoint.setStatus(1); // 1-正常
            
            // 初始化统计数据
            knowledgePoint.setViewCount(0);
            knowledgePoint.setLikeCount(0);
            knowledgePoint.setCollectCount(0);
            knowledgePoint.setCommentCount(0);  // 初始化评论数

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

    // ==================== 收藏夹管理接口 ====================

    /**
     * 获取用户的收藏夹列表
     */
    @GetMapping("/folders")
    public AjaxResult getUserFolders()
    {
        try {
            Long userId = SecurityUtils.getUserId();
            List<KnowledgeFolder> folders = knowledgeFolderService.selectFoldersByUserId(userId);
            return success(folders);
        } catch (Exception e) {
            logger.error("获取收藏夹列表失败", e);
            return error("获取收藏夹列表失败");
        }
    }

    /**
     * 创建新收藏夹
     */
    @PostMapping("/folder")
    public AjaxResult createFolder(@RequestBody KnowledgeFolder folder)
    {
        try {
            Long userId = SecurityUtils.getUserId();
            
            folder.setUserId(userId);
            folder.setIsDefault(0); // 非默认收藏夹
            folder.setCollectCount(0);
            
            int result = knowledgeFolderService.insertKnowledgeFolder(folder);
            if (result > 0) {
                return success("创建收藏夹成功");
            } else {
                return error("创建收藏夹失败");
            }
        } catch (Exception e) {
            logger.error("创建收藏夹失败", e);
            return error("创建收藏夹失败：" + e.getMessage());
        }
    }

    /**
     * 修改收藏夹
     */
    @PostMapping("/folder/update")
    public AjaxResult updateFolder(@RequestBody KnowledgeFolder folder)
    {
        try {
            Long userId = SecurityUtils.getUserId();
            
            // 验证收藏夹是否属于当前用户
            KnowledgeFolder existFolder = knowledgeFolderService.selectKnowledgeFolderByFolderId(folder.getFolderId());
            if (existFolder == null || !existFolder.getUserId().equals(userId)) {
                return error("无权修改此收藏夹");
            }
            
            int result = knowledgeFolderService.updateKnowledgeFolder(folder);
            if (result > 0) {
                return success("修改收藏夹成功");
            } else {
                return error("修改收藏夹失败");
            }
        } catch (Exception e) {
            logger.error("修改收藏夹失败", e);
            return error("修改收藏夹失败：" + e.getMessage());
        }
    }

    /**
     * 删除收藏夹
     */
    @DeleteMapping("/folder/{folderId}")
    public AjaxResult deleteFolder(@PathVariable Long folderId)
    {
        try {
            Long userId = SecurityUtils.getUserId();
            
            // 验证收藏夹是否属于当前用户
            KnowledgeFolder folder = knowledgeFolderService.selectKnowledgeFolderByFolderId(folderId);
            if (folder == null || !folder.getUserId().equals(userId)) {
                return error("无权删除此收藏夹");
            }
            
            // 不能删除默认收藏夹
            if (folder.getIsDefault() == 1) {
                return error("不能删除默认收藏夹");
            }
            
            int result = knowledgeFolderService.deleteKnowledgeFolderByFolderId(folderId);
            if (result > 0) {
                return success("删除收藏夹成功");
            } else {
                return error("删除收藏夹失败");
            }
        } catch (Exception e) {
            logger.error("删除收藏夹失败", e);
            return error("删除收藏夹失败：" + e.getMessage());
        }
    }

    /**
     * 收藏知识点到指定收藏夹
     */
    @PostMapping("/collect/{pointId}/folder/{folderId}")
    public AjaxResult collectToFolder(@PathVariable Long pointId, @PathVariable Long folderId)
    {
        try {
            Long userId = SecurityUtils.getUserId();
            
            // 验证收藏夹是否属于当前用户
            KnowledgeFolder folder = knowledgeFolderService.selectKnowledgeFolderByFolderId(folderId);
            if (folder == null || !folder.getUserId().equals(userId)) {
                return error("无权访问此收藏夹");
            }
            
            boolean isCollected = knowledgeInteractionService.toggleCollectToFolder(userId, pointId, folderId);
            return AjaxResult.success(isCollected ? "收藏成功" : "取消收藏", isCollected);
        } catch (Exception e) {
            logger.error("收藏到指定收藏夹失败", e);
            return error("操作失败：" + e.getMessage());
        }
    }

    /**
     * 删除知识点
     */
    @DeleteMapping("/point/{pointId}")
    public AjaxResult deleteKnowledgePoint(@PathVariable Long pointId)
    {
        try {
            Long userId = SecurityUtils.getUserId();
            
            // 验证知识点是否存在
            KnowledgePoint point = ((KnowledgePointServiceImpl)knowledgePointService).getCompleteKnowledgePoint(pointId);
            if (point == null) {
                return error("知识点不存在");
            }
            
            // 验证是否为作者本人
            if (!point.getAuthorId().equals(userId)) {
                return error("只能删除自己发布的知识点");
            }
            
            int result = knowledgePointService.deleteKnowledgePointByPointId(pointId);
            if (result > 0) {
                return success("删除知识点成功");
            } else {
                return error("删除知识点失败");
            }
        } catch (Exception e) {
            logger.error("删除知识点失败", e);
            return error("删除知识点失败：" + e.getMessage());
        }
    }

    @GetMapping("/{pointId}/related-questions")
    public TableDataInfo getRelatedQuestions(@PathVariable Long pointId, 
                                            @RequestParam(required = false) Integer questionType,
                                            @RequestParam(required = false) Integer difficulty) {
        List<Long> questionIds = questionKnowledgeService.getQuestionIdsByKnowledgePointId(pointId);
        
        if (questionIds == null || questionIds.isEmpty()) {
            return getDataTable(new java.util.ArrayList<>());
        }
        
        DamingQuestion queryParam = new DamingQuestion();
        startPage();
        List<DamingQuestion> questions = damingQuestionService.selectDamingQuestionList(queryParam);
        questions = questions.stream()
            .filter(q -> questionIds.contains(q.getId()))
            .filter(q -> questionType == null || q.getQuestionType().equals(questionType))
            .collect(java.util.stream.Collectors.toList());
        
        return getDataTable(questions);
    }
}
