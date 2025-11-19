package com.dm.quiz.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 科目管理对象 daming_subject
 *
 * @author
 */
public class DamingSubject extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 科目ID */
    @Excel(name = "科目ID")
    private Integer subjectId;

    /** 科目名称 */
    @Excel(name = "科目名称")
    private String subjectName;

    /** 删除标记（0正常 2删除） */
    private Integer delFlag;

    public Integer getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Integer subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public Integer getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("subjectId", getSubjectId())
                .append("subjectName", getSubjectName())
                .append("delFlag", getDelFlag())
                .append("createTime", getCreateTime())
                .append("updateTime", getUpdateTime())
                .toString();
    }
}

