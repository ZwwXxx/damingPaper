package com.ruoyi.web.controller.quiz.student;


import com.dm.quiz.domain.DamingPaperAnswer;
import com.dm.quiz.dto.PaperAnswerDto;
import com.dm.quiz.dto.PaperReviewDto;
import com.dm.quiz.service.IDamingPaperAnswerService;
import com.ruoyi.common.core.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/quiz/student/paper/answer")
public class StudentPaperAnswerController {

    @Autowired
    private IDamingPaperAnswerService damingPaperAnswerService;

    /**
     * 用户提交答案
     */
    @PostMapping("/submitAnswer")
    public AjaxResult submitAnswer(@RequestBody PaperAnswerDto paperAnswerDto) {
        int score = damingPaperAnswerService.submitAnswer(paperAnswerDto);
        return AjaxResult.success(score);
    }

    /**
     * 根据id查看所有考试记录
     */
    @GetMapping("/list")
    public AjaxResult list(DamingPaperAnswer damingPaperAnswer) {
        List<DamingPaperAnswer> damingPaperAnswers = damingPaperAnswerService.selectDamingPaperAnswerList(damingPaperAnswer);
        return AjaxResult.success(damingPaperAnswers);
    }

    /**
     * 查询单条考试记录
     * @param paperAnswerId
     * @return
     */
    @GetMapping("/getPaperReview/{paperAnswerId}")
    public AjaxResult getPaperAnswer(@PathVariable("paperAnswerId") Long paperAnswerId) {
        PaperReviewDto paperReviewDto = damingPaperAnswerService.getPaperReviewDto(paperAnswerId);
        return AjaxResult.success(paperReviewDto);
    }



}
