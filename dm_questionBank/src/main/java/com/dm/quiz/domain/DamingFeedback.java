package com.dm.quiz.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * 用户反馈对象 daming_feedback
 *
 * @author daming
 * @date 2025-11-23
 */
public class DamingFeedback extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 反馈ID */
    private Long feedbackId;

    /** 用户ID */
    @Excel(name = "用户ID")
    private String userId;

    /** 用户名称 */
    @Excel(name = "用户名称")
    private String userName;

    /** 反馈类型：1=功能建议 2=Bug反馈 3=使用问题 4=其他 */
    @Excel(name = "反馈类型", readConverterExp = "1=功能建议,2=Bug反馈,3=使用问题,4=其他")
    private Integer feedbackType;

    /** 反馈标题 */
    @Excel(name = "反馈标题")
    private String feedbackTitle;

    /** 反馈内容 */
    @Excel(name = "反馈内容")
    private String feedbackContent;

    /** 联系方式 */
    @Excel(name = "联系方式")
    private String contactInfo;

    /** 截图附件（多个用逗号分隔） */
    private String images;

    /** 处理状态：0=待处理 1=处理中 2=已处理 3=已关闭 */
    @Excel(name = "处理状态", readConverterExp = "0=待处理,1=处理中,2=已处理,3=已关闭")
    private Integer status;

    /** 优先级：1=低 2=中 3=高 */
    @Excel(name = "优先级", readConverterExp = "1=低,2=中,3=高")
    private Integer priority;

    /** 处理人 */
    @Excel(name = "处理人")
    private String handler;

    /** 处理时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "处理时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date handleTime;

    /** 回复内容 */
    @Excel(name = "回复内容")
    private String replyContent;

    /** 删除标志：0=正常 1=删除 */
    private String delFlag;

    /** 创建者 */
    private String createUser;

    /** 更新者 */
    private String updateUser;

    public Long getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(Long feedbackId) {
        this.feedbackId = feedbackId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getFeedbackType() {
        return feedbackType;
    }

    public void setFeedbackType(Integer feedbackType) {
        this.feedbackType = feedbackType;
    }

    public String getFeedbackTitle() {
        return feedbackTitle;
    }

    public void setFeedbackTitle(String feedbackTitle) {
        this.feedbackTitle = feedbackTitle;
    }

    public String getFeedbackContent() {
        return feedbackContent;
    }

    public void setFeedbackContent(String feedbackContent) {
        this.feedbackContent = feedbackContent;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }

    public Date getHandleTime() {
        return handleTime;
    }

    public void setHandleTime(Date handleTime) {
        this.handleTime = handleTime;
    }

    public String getReplyContent() {
        return replyContent;
    }

    public void setReplyContent(String replyContent) {
        this.replyContent = replyContent;
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
                .append("feedbackId", getFeedbackId())
                .append("userId", getUserId())
                .append("userName", getUserName())
                .append("feedbackType", getFeedbackType())
                .append("feedbackTitle", getFeedbackTitle())
                .append("feedbackContent", getFeedbackContent())
                .append("contactInfo", getContactInfo())
                .append("images", getImages())
                .append("status", getStatus())
                .append("priority", getPriority())
                .append("handler", getHandler())
                .append("handleTime", getHandleTime())
                .append("replyContent", getReplyContent())
                .append("delFlag", getDelFlag())
                .append("createUser", getCreateUser())
                .append("createTime", getCreateTime())
                .append("updateUser", getUpdateUser())
                .append("updateTime", getUpdateTime())
                .append("remark", getRemark())
                .toString();
    }
}
