package com.ruoyi.web.controller.quiz.student;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dm.quiz.domain.PracticeColumn;
import com.dm.quiz.service.IPracticeColumnService;
import com.ruoyi.common.core.domain.AjaxResult;

/**
 * 学生端：专项刷题栏目
 */
@RestController
@RequestMapping("/quiz/student/practice")
public class StudentPracticeColumnController {

    @Autowired
    private IPracticeColumnService practiceColumnService;

    /**
     * 获取参与专项刷题的栏目列表
     * 可选：按 subjectId 过滤
     */
    @GetMapping("/columns")
    public AjaxResult listColumns(@RequestParam(value = "subjectId", required = false) Integer subjectId) {
        PracticeColumn query = new PracticeColumn();
        query.setEnablePractice(1);
        if (subjectId != null) {
            query.setSubjectId(subjectId);
        }
        List<PracticeColumn> list = practiceColumnService.selectList(query);
        return AjaxResult.success(list);
    }

    /**
     * 开始某栏目专项练习：返回可直接进入考试页的 paperId
     */
    @GetMapping("/columns/{columnId}/paperId")
    public AjaxResult getPracticePaperId(@PathVariable("columnId") Long columnId) {
        Long paperId = practiceColumnService.getOrCreatePracticePaperId(columnId);
        Map<String, Object> data = new HashMap<>();
        data.put("paperId", paperId);
        return AjaxResult.success(data);
    }
}

