package com.ruoyi.web.controller.quiz.admin;

import com.dm.quiz.domain.DamingPaperAnswer;
import com.dm.quiz.dto.PaperReviewDto;
import com.dm.quiz.dto.PaperReviewSubmitRequest;
import com.dm.quiz.service.IDamingPaperAnswerService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/quiz/paperReview")
public class PaperReviewController extends BaseController {

    @Autowired
    private IDamingPaperAnswerService damingPaperAnswerService;

    /**
     * 待批改列表
     */
    @PreAuthorize("@ss.hasPermi('quiz:paperReview:list')")
    @GetMapping("/list")
    public TableDataInfo list(DamingPaperAnswer query) {
        startPage();
        List<DamingPaperAnswer> list = damingPaperAnswerService.selectDamingPaperAnswerList(query);
        return getDataTable(list);
    }

    /**
     * 批改详情
     */
    @PreAuthorize("@ss.hasPermi('quiz:paperReview:query')")
    @GetMapping("/{paperAnswerId}")
    public AjaxResult detail(@PathVariable Long paperAnswerId) {
        PaperReviewDto dto = damingPaperAnswerService.getPaperReviewDto(paperAnswerId);
        return AjaxResult.success(dto);
    }

    /**
     * 提交评分
     */
    @PreAuthorize("@ss.hasPermi('quiz:paperReview:score')")
    @PostMapping("/score")
    public AjaxResult score(@Validated @RequestBody PaperReviewSubmitRequest request) {
        damingPaperAnswerService.reviewPaper(request);
        return AjaxResult.success();
    }
}

