package com.ruoyi.common.core.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 论坛评论实体
 * 
 * @author ruoyi
 */
@Data
public class ForumComment {
    
    /** 评论ID */
    private Long commentId;
    
    /** 帖子ID */
    @Excel(name = "帖子ID")
    private Long postId;
    
    /** 评论人ID */
    @Excel(name = "评论人ID")
    private Long userId;
    
    /** 评论人用户名 */
    @Excel(name = "评论人用户名")
    private String userName;
    
    /** 评论人昵称 */
    @Excel(name = "评论人昵称")
    private String nickName;
    
    /** 评论人头像 */
    private String avatar;
    
    /** 评论内容 */
    @Excel(name = "评论内容")
    private String content;
    
    /** 父评论ID（回复评论时使用） */
    private Long parentId;
    
    /** 被回复的用户ID */
    private Long replyToUserId;
    
    /** 被回复的用户名 */
    private String replyToUserName;
    
    /** 点赞数 */
    @Excel(name = "点赞数")
    private Integer likeCount;
    
    /** 状态（0删除 1正常） */
    @Excel(name = "状态", readConverterExp = "0=删除,1=正常")
    private Integer status;
    
    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    
    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    
    /** 当前用户是否已点赞（前端使用，不存数据库） */
    private Boolean hasLiked;
    
    /** 子评论列表（用于嵌套评论展示） */
    private List<ForumComment> children;
}
