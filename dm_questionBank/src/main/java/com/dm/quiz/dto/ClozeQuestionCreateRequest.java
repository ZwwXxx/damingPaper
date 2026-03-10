package com.dm.quiz.dto;

import java.util.List;

/**
 * 完形填空批量创建请求
 * 包含一条父题（文章）和多条子题（每空一道客观题）
 */
public class ClozeQuestionCreateRequest {

    /**
     * 完形填空父题（questionType = 6）
     */
    private QuestionDto parent;

    /**
     * 完形填空子题列表（每条为一道普通题：单选/多选/判断）
     */
    private List<QuestionDto> children;

    public QuestionDto getParent() {
        return parent;
    }

    public void setParent(QuestionDto parent) {
        this.parent = parent;
    }

    public List<QuestionDto> getChildren() {
        return children;
    }

    public void setChildren(List<QuestionDto> children) {
        this.children = children;
    }
}

