package com.ruoyi.web.controller.quiz.student;


import com.dm.quiz.domain.DamingPaperAnswer;
import com.dm.quiz.dto.PaperAnswerDto;
import com.dm.quiz.dto.PaperReviewDto;
import com.dm.quiz.service.IDamingPaperAnswerService;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.SecurityUtils;
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
        return AjaxResult.success(damingPaperAnswerService.submitAnswer(paperAnswerDto));
    }

    /**
     * 根据id查看所有考试记录
     */
    @GetMapping("/list")
    public AjaxResult list(DamingPaperAnswer damingPaperAnswer) {
        damingPaperAnswer.setCreateUser(SecurityUtils.getUsername());
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
        // 获取当前用户
        String currentUser = SecurityUtils.getUsername();
        
        // 先验证这条考试记录是否属于当前用户
        DamingPaperAnswer paperAnswer = damingPaperAnswerService.selectDamingPaperAnswerByPaperAnswerId(paperAnswerId);
        if (paperAnswer == null) {
            return AjaxResult.error("考试记录不存在");
        }
        
        // 检查是否是当前用户的考试记录
        if (!currentUser.equals(paperAnswer.getCreateUser())) {
            return AjaxResult.error("无权访问此考试记录");
        }
        
        PaperReviewDto paperReviewDto = damingPaperAnswerService.getPaperReviewDto(paperAnswerId);
        return AjaxResult.success(paperReviewDto);
    }



}
