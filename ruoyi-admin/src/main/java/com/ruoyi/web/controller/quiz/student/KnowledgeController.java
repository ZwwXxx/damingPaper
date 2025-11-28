package com.ruoyi.web.controller.quiz.student;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.KnowledgeChapter;
import com.ruoyi.system.domain.KnowledgePoint;
import com.ruoyi.system.domain.KnowledgeSubject;
import com.ruoyi.system.service.IKnowledgeChapterService;
import com.ruoyi.system.service.IKnowledgeInteractionService;
import com.ruoyi.system.service.IKnowledgePointService;
import com.ruoyi.system.service.IKnowledgeSubjectService;

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
}
