package com.dm.quiz.dto;

/**
 * 试卷同步包（用于跨环境导出/导入）
 */
public class PaperSyncPackageDto {
    /**
     * 包版本，便于后续兼容升级
     */
    private String version; // 同步包版本号

    /**
     * 源试卷ID（导出环境中的ID）
     */
    private Long sourcePaperId; // 源环境试卷ID

    /**
     * 试卷完整数据（含题型与题目）
     */
    private PaperDto paper; // 试卷完整数据

    public String getVersion() {
        return version; // 返回版本号
    }

    public void setVersion(String version) {
        this.version = version; // 设置版本号
    }

    public Long getSourcePaperId() {
        return sourcePaperId; // 返回源试卷ID
    }

    public void setSourcePaperId(Long sourcePaperId) {
        this.sourcePaperId = sourcePaperId; // 设置源试卷ID
    }

    public PaperDto getPaper() {
        return paper; // 返回试卷主体
    }

    public void setPaper(PaperDto paper) {
        this.paper = paper; // 设置试卷主体
    }
}
