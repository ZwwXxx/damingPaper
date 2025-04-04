package com.dm.quiz.viewmodel;

/**
 * 试卷上的题目，数据层，包含id和顺序
 */
public class PaperQuestionVM {
    private Long id;
    private Integer itemOrder;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getItemOrder() {
        return itemOrder;
    }

    public void setItemOrder(Integer itemOrder) {
        this.itemOrder = itemOrder;
    }
}
