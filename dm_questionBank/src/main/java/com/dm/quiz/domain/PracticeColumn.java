package com.dm.quiz.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 专项刷题栏目对象 daming_practice_column
 */
public class PracticeColumn extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 栏目ID */
    private Long columnId;

    /** 栏目名称 */
    @Excel(name = "栏目名称")
    private String columnName;

    /** 科目ID */
    @Excel(name = "科目ID")
    private Integer subjectId;

    /** 一级分组名称（章节/大类） */
    private String groupName;

    /** 一级分组排序（越大越靠前） */
    private Integer groupSort;

    /** 栏目描述 */
    private String description;

    /** 排序（越大越靠前） */
    private Integer sortOrder;

    /** 是否参与专项刷题筛选（1是0否） */
    private Integer enablePractice;

    /** 关联试卷ID（首次生成后复用） */
    private Long paperId;

    public Long getColumnId() {
        return columnId;
    }

    public void setColumnId(Long columnId) {
        this.columnId = columnId;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public Integer getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Integer subjectId) {
        this.subjectId = subjectId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Integer getGroupSort() {
        return groupSort;
    }

    public void setGroupSort(Integer groupSort) {
        this.groupSort = groupSort;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Integer getEnablePractice() {
        return enablePractice;
    }

    public void setEnablePractice(Integer enablePractice) {
        this.enablePractice = enablePractice;
    }

    public Long getPaperId() {
        return paperId;
    }

    public void setPaperId(Long paperId) {
        this.paperId = paperId;
    }
}

