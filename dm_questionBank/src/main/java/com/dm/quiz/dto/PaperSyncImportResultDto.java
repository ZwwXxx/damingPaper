package com.dm.quiz.dto;

/**
 * 试卷同步导入结果
 */
public class PaperSyncImportResultDto {
    private Long newPaperId;
    private int insertedQuestionCount;
    private int skippedQuestionCount;

    public Long getNewPaperId() {
        return newPaperId;
    }

    public void setNewPaperId(Long newPaperId) {
        this.newPaperId = newPaperId;
    }

    public int getInsertedQuestionCount() {
        return insertedQuestionCount;
    }

    public void setInsertedQuestionCount(int insertedQuestionCount) {
        this.insertedQuestionCount = insertedQuestionCount;
    }

    public int getSkippedQuestionCount() {
        return skippedQuestionCount;
    }

    public void setSkippedQuestionCount(int skippedQuestionCount) {
        this.skippedQuestionCount = skippedQuestionCount;
    }
}
