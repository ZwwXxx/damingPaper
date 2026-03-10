package com.dm.quiz.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 动画库对象 daming_animation
 *
 * @author zww
 * @date 2026-03-04
 */
public class DamingAnimation extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 动画ID */
    private Long animationId;

    /** 科目ID（用于分类） */
    private Integer subjectId;

    /** 动画名称（通常为原始文件名） */
    @Excel(name = "动画名称")
    private String animationName;

    /** 动画页面URL */
    @Excel(name = "动画URL")
    private String animationUrl;

    /** 对象存储ObjectName/本地相对路径 */
    private String objectName;

    /** 删除标志：0=正常 1=删除 */
    private String delFlag;

    /** 创建人 */
    private String createUser;

    /** 更新人 */
    private String updateUser;

    public Long getAnimationId() {
        return animationId;
    }

    public void setAnimationId(Long animationId) {
        this.animationId = animationId;
    }

    public Integer getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Integer subjectId) {
        this.subjectId = subjectId;
    }

    public String getAnimationName() {
        return animationName;
    }

    public void setAnimationName(String animationName) {
        this.animationName = animationName;
    }

    public String getAnimationUrl() {
        return animationUrl;
    }

    public void setAnimationUrl(String animationUrl) {
        this.animationUrl = animationUrl;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("animationId", getAnimationId())
                .append("subjectId", getSubjectId())
                .append("animationName", getAnimationName())
                .append("animationUrl", getAnimationUrl())
                .append("objectName", getObjectName())
                .append("delFlag", getDelFlag())
                .append("createUser", getCreateUser())
                .append("createTime", getCreateTime())
                .append("updateUser", getUpdateUser())
                .append("updateTime", getUpdateTime())
                .append("remark", getRemark())
                .toString();
    }
}

