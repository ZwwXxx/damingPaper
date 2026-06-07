package com.dm.quiz.dto;

import com.dm.quiz.viewmodel.QuestionOptionVM;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.List;

public class QuestionDto {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;
    // 父题ID（完形/阅读等复合题）
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long parentId;
    // 动画解析ID（可选）
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long animationId;
    // 动画解析URL（可选，便于回显/预览）
    private String animationUrl;
    // 选项组
    private List<QuestionOptionVM> items;
    // 解析
    private String analysis;
    // 标准答案
    private String correct;
    // 多选题答案列表
    private String[] correctArray;
    // 该题总分（多选就几个选项加一起）
    private Integer score;
    // 难度：1=简单，2=中等，3=困难
    private Integer difficulty;
    // 题目
    private String questionTitle;
    //所属科目
    private Integer subjectId;
    // 题目类型，1单选，2多选，3主观
    private Integer questionType;
    // 完形第几空索引（从1开始），普通题为空
    private Integer clozeIndex;
    // 题目序号表示第几题
    private Integer itemOrder;
    // 原卷题号（复合题可填首子题题号）
    private Integer originOrder;
    // 题目年份（用于按年份筛题/组卷）
    private Integer examYear;
    // 考试批次：1=上半年，2=下半年；为空表示该年份仅考一次
    private Integer examHalf;
    // 填空题答案是否要求按顺序（true=要求按顺序，false=不要求按顺序）
    private Boolean requireOrder;
    // 解析内容格式（html=富文本，markdown=Markdown格式，默认html）
    private String analysisFormat;
    // 题干内容格式（html=富文本，markdown=Markdown格式，默认html）
    private String questionTitleFormat;
    // 选项内容格式（html=富文本，markdown=Markdown格式，默认html）
    private String optionFormat;
    // 标准答案内容格式（html=富文本，markdown=Markdown格式，默认html，仅主观题）
    private String correctFormat;

    public Integer getItemOrder() {
        return itemOrder;
    }

    public void setItemOrder(Integer itemOrder) {
        this.itemOrder = itemOrder;
    }

    public Integer getOriginOrder() {
        return originOrder;
    }

    public void setOriginOrder(Integer originOrder) {
        this.originOrder = originOrder;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Long getAnimationId() {
        return animationId;
    }

    public void setAnimationId(Long animationId) {
        this.animationId = animationId;
    }

    public String getAnimationUrl() {
        return animationUrl;
    }

    public void setAnimationUrl(String animationUrl) {
        this.animationUrl = animationUrl;
    }

    public List<QuestionOptionVM> getItems() {
        return items;
    }

    public void setItems(List<QuestionOptionVM> items) {
        this.items = items;
    }

    public String getAnalysis() {
        return analysis;
    }

    public void setAnalysis(String analysis) {
        this.analysis = analysis;
    }

    public String getCorrect() {
        return correct;
    }

    public void setCorrect(String correct) {
        this.correct = correct;
    }

    public String[] getCorrectArray() {
        return correctArray;
    }

    public void setCorrectArray(String[] correctArray) {
        this.correctArray = correctArray;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }
    public Integer getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Integer difficulty) {
        this.difficulty = difficulty;
    }

    public String getQuestionTitle() {
        return questionTitle;
    }

    public void setQuestionTitle(String questionTitle) {
        this.questionTitle = questionTitle;
    }

    public Integer getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Integer subjectId) {
        this.subjectId = subjectId;
    }

    public Integer getQuestionType() {
        return questionType;
    }

    public void setQuestionType(Integer questionType) {
        this.questionType = questionType;
    }

    public Integer getClozeIndex() {
        return clozeIndex;
    }

    public void setClozeIndex(Integer clozeIndex) {
        this.clozeIndex = clozeIndex;
    }

    public Boolean getRequireOrder() {
        return requireOrder;
    }

    public void setRequireOrder(Boolean requireOrder) {
        this.requireOrder = requireOrder;
    }

    public String getAnalysisFormat() {
        return analysisFormat;
    }

    public void setAnalysisFormat(String analysisFormat) {
        this.analysisFormat = analysisFormat;
    }

    public String getQuestionTitleFormat() {
        return questionTitleFormat;
    }

    public void setQuestionTitleFormat(String questionTitleFormat) {
        this.questionTitleFormat = questionTitleFormat;
    }

    public String getOptionFormat() {
        return optionFormat;
    }

    public void setOptionFormat(String optionFormat) {
        this.optionFormat = optionFormat;
    }

    public String getCorrectFormat() {
        return correctFormat;
    }

    public void setCorrectFormat(String correctFormat) {
        this.correctFormat = correctFormat;
    }
}
