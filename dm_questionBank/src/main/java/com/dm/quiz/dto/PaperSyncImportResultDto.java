package com.dm.quiz.dto;

/**
 * 试卷同步导入结果
 */
public class PaperSyncImportResultDto {
    private Long newPaperId; // 新创建试卷ID
    private int insertedQuestionCount; // 新增题目数量
    private int skippedQuestionCount; // 跳过题目数量（命中去重）
    private int filteredQuestionCount; // 过滤题目数量（如空题干）
    private int parentRemapMissCount; // 子题父ID重映射缺失数量
    private int reusedQuestionCount; // 复用题目数量（命中去重且入卷）
    private int parentMissReusedCount; // 父映射缺失但忽略父ID后复用成功
    private int parentMissDroppedCount; // 父映射缺失且无法复用，最终未入卷
    private int notInPaperQuestionCount; // 最终未入卷数量（过滤+父映射缺失未复用）

    public Long getNewPaperId() {
        return newPaperId; // 返回新试卷ID
    }

    public void setNewPaperId(Long newPaperId) {
        this.newPaperId = newPaperId; // 设置新试卷ID
    }

    public int getInsertedQuestionCount() {
        return insertedQuestionCount; // 返回新增题目数量
    }

    public void setInsertedQuestionCount(int insertedQuestionCount) {
        this.insertedQuestionCount = insertedQuestionCount; // 设置新增题目数量
    }

    public int getSkippedQuestionCount() {
        return skippedQuestionCount; // 返回跳过题目数量
    }

    public void setSkippedQuestionCount(int skippedQuestionCount) {
        this.skippedQuestionCount = skippedQuestionCount; // 设置跳过题目数量
    }

    public int getFilteredQuestionCount() {
        return filteredQuestionCount; // 返回过滤题目数量
    }

    public void setFilteredQuestionCount(int filteredQuestionCount) {
        this.filteredQuestionCount = filteredQuestionCount; // 设置过滤题目数量
    }

    public int getParentRemapMissCount() {
        return parentRemapMissCount; // 返回父ID重映射缺失数量
    }

    public void setParentRemapMissCount(int parentRemapMissCount) {
        this.parentRemapMissCount = parentRemapMissCount; // 设置父ID重映射缺失数量
    }

    public int getReusedQuestionCount() {
        return reusedQuestionCount; // 返回复用题目数量
    }

    public void setReusedQuestionCount(int reusedQuestionCount) {
        this.reusedQuestionCount = reusedQuestionCount; // 设置复用题目数量
    }

    public int getParentMissReusedCount() {
        return parentMissReusedCount; // 返回父映射缺失但复用成功数量
    }

    public void setParentMissReusedCount(int parentMissReusedCount) {
        this.parentMissReusedCount = parentMissReusedCount; // 设置父映射缺失但复用成功数量
    }

    public int getParentMissDroppedCount() {
        return parentMissDroppedCount; // 返回父映射缺失且未入卷数量
    }

    public void setParentMissDroppedCount(int parentMissDroppedCount) {
        this.parentMissDroppedCount = parentMissDroppedCount; // 设置父映射缺失且未入卷数量
    }

    public int getNotInPaperQuestionCount() {
        return notInPaperQuestionCount; // 返回最终未入卷数量
    }

    public void setNotInPaperQuestionCount(int notInPaperQuestionCount) {
        this.notInPaperQuestionCount = notInPaperQuestionCount; // 设置最终未入卷数量
    }
}
