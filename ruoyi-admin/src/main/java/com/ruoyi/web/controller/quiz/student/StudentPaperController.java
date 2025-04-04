package com.ruoyi.web.controller.quiz.student;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.dm.quiz.domain.DamingPaper;
import com.dm.quiz.service.IDamingPaperService;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 试卷Controller
 *
 * @author zww
 * @date 2024-10-10
 */
@RestController
@RequestMapping("/quiz/student/paper")
public class StudentPaperController extends BaseController
{
    @Autowired
    private IDamingPaperService damingPaperService;

    /**
     * 查询试卷列表
     */
    @PreAuthorize("@ss.hasPermi('quiz:paper:list')")
    @GetMapping("/list")
    public TableDataInfo list(  DamingPaper damingPaper)
    {
        startPage();
        List<DamingPaper> list = damingPaperService.selectDamingPaperList(damingPaper);
        return getDataTable(list);
    }

    /**
     * 获取试卷详细信息
     */
    @PreAuthorize("@ss.hasPermi('quiz:paper:query')")
    @GetMapping(value = "/{paperId}")
    public AjaxResult getInfo(@PathVariable("paperId") Long paperId)
    {
        return success(damingPaperService.paperIdtoDto(paperId));
    }




}
