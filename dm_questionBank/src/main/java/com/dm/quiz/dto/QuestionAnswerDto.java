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
    // 答题记录ID
    private Long answerId;
    // 已得分
    private Integer finalScore;
    // 题目总分
    private Integer questionScore;
    // 批改状态
    private Integer reviewStatus;
    // 批改备注
    private String reviewComment;

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

    public Long getAnswerId() {
        return answerId;
    }

    public void setAnswerId(Long answerId) {
        this.answerId = answerId;
    }

    public Integer getFinalScore() {
        return finalScore;
    }

    public void setFinalScore(Integer finalScore) {
        this.finalScore = finalScore;
    }

    public Integer getQuestionScore() {
        return questionScore;
    }

    public void setQuestionScore(Integer questionScore) {
        this.questionScore = questionScore;
    }

    public Integer getReviewStatus() {
        return reviewStatus;
    }

    public void setReviewStatus(Integer reviewStatus) {
        this.reviewStatus = reviewStatus;
    }

    public String getReviewComment() {
        return reviewComment;
    }

    public void setReviewComment(String reviewComment) {
        this.reviewComment = reviewComment;
    }
}
