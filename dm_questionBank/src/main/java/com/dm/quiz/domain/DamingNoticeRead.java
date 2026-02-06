package com.dm.quiz.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * 用户公告阅读状态对象 daming_notice_read
 *
 * @author daming
 * @date 2025-01-15
 */
public class DamingNoticeRead extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 公告ID */
    private Long noticeId;

    /** 阅读时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date readTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getNoticeId() {
        return noticeId;
    }

    public void setNoticeId(Long noticeId) {
        this.noticeId = noticeId;
    }

    public Date getReadTime() {
        return readTime;
    }

    public void setReadTime(Date readTime) {
        this.readTime = readTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("userId", getUserId())
                .append("noticeId", getNoticeId())
                .append("readTime", getReadTime())
                .append("createTime", getCreateTime())
                .toString();
    }
}








