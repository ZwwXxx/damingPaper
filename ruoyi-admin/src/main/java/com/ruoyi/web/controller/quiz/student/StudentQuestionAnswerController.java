package com.ruoyi.web.controller.quiz.student;

import com.dm.quiz.domain.DamingQuestionAnswer;
import com.dm.quiz.service.IDamingQuestionAnswerService;
import com.ruoyi.common.core.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 学生错题记录
 *
 * 供前端错题本调用，按照当前用户、科目、时间等条件返回错题明细。
 */
@RestController
@RequestMapping("/quiz/student/question/answer")
public class StudentQuestionAnswerController {

    @Autowired
    private IDamingQuestionAnswerService damingQuestionAnswerService;

    /**
     * 查询错题列表（只返回答错的题目）
     */
    @GetMapping("/wrongList")
    public AjaxResult wrongList(DamingQuestionAnswer damingQuestionAnswer) {
        return AjaxResult.success(damingQuestionAnswerService.selectWrongQuestionList(damingQuestionAnswer));
    }
}

