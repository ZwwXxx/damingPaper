package com.dm.quiz.dto;

import java.util.List;

/**
 * 批量设置题目的考试年份、上/下半年批次。
 * <p>为 null 的字段表示不修改该列。</p>
 */
public class BatchQuestionExamMetaRequest {

    private List<Long> ids;
    /** 为空则不更新年份 */
    private Integer examYear;
    /** 为空则不更新批次 */
    private Integer examHalf;

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }

    public Integer getExamYear() {
        return examYear;
    }

    public void setExamYear(Integer examYear) {
        this.examYear = examYear;
    }

    public Integer getExamHalf() {
        return examHalf;
    }

    public void setExamHalf(Integer examHalf) {
        this.examHalf = examHalf;
    }
}
