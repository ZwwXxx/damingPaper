package com.ruoyi.web.controller.quiz.student;

import com.dm.quiz.domain.DamingQuestionFavorite;
import com.dm.quiz.dto.QuestionFavoriteRequest;
import com.dm.quiz.service.IDamingQuestionFavoriteService;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 学生题目收藏接口
 */
@RestController
@RequestMapping("/quiz/student/question/favorite")
public class StudentQuestionFavoriteController {

    @Autowired
    private IDamingQuestionFavoriteService questionFavoriteService;

    /**
     * 获取收藏题目 ID
     */
    @GetMapping("/list")
    public AjaxResult list(@RequestParam("paperAnswerId") Long paperAnswerId) {
        return AjaxResult.success(
                questionFavoriteService.queryFavoriteQuestionIds(SecurityUtils.getUsername(), paperAnswerId)
        );
    }

    /**
     * 收藏题目详情列表（个人中心使用）
     */
    @GetMapping("/detail")
    public AjaxResult detailList(DamingQuestionFavorite query) {
        return AjaxResult.success(questionFavoriteService.selectFavoriteQuestionList(query));
    }

    /**
     * 收藏题目
     */
    @PostMapping
    public AjaxResult add(@Validated @RequestBody QuestionFavoriteRequest request) {
        questionFavoriteService.addFavorite(request);
        return AjaxResult.success();
    }

    /**
     * 取消收藏
     */
    @DeleteMapping("/{questionId}")
    public AjaxResult remove(@PathVariable("questionId") Long questionId) {
        questionFavoriteService.removeFavorite(questionId, SecurityUtils.getUsername());
        return AjaxResult.success();
    }
}


