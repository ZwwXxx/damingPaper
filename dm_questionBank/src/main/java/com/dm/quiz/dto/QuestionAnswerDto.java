package com.dm.quiz.dto;

/**
 * 用户提交的题目答案实体
 */
public class QuestionAnswerDto {
    // 后续会计算设置该值并且返回进行查看哪道题是否做对了，做错了
    private Boolean isCorrect;
    // 用户回答的内容
    private String content;
    // 如果是多选题，回答内容装到数组里
    private String[] contentArray;
    // 回答该题目的id,用户后台查看，前台用不到这个，原题交给了paperDto上的题型
    private Long questionId;
    // 题目顺序
    private Integer itemOrder;

    public Boolean getCorrect() {
        return isCorrect;
    }

    public void setCorrect(Boolean correct) {
        isCorrect = correct;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String[] getContentArray() {
        return contentArray;
    }

    public void setContentArray(String[] contentArray) {
        this.contentArray = contentArray;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Integer getItemOrder() {
        return itemOrder;
    }

    public void setItemOrder(Integer itemOrder) {
        this.itemOrder = itemOrder;
    }
}
