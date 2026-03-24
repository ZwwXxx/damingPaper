package com.dm.quiz.dto;

/**
 * 试卷同步包（用于跨环境导出/导入）
 */
public class PaperSyncPackageDto {
    /**
     * 包版本，便于后续兼容升级
     */
    private String version;

    /**
     * 源试卷ID（导出环境中的ID）
     */
    private Long sourcePaperId;

    /**
     * 试卷完整数据（含题型与题目）
     */
    private PaperDto paper;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Long getSourcePaperId() {
        return sourcePaperId;
    }

    public void setSourcePaperId(Long sourcePaperId) {
        this.sourcePaperId = sourcePaperId;
    }

    public PaperDto getPaper() {
        return paper;
    }

    public void setPaper(PaperDto paper) {
        this.paper = paper;
    }
}
