package com.ruoyi.common.core.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 论坛点赞实体
 * 
 * @author ruoyi
 */
@Data
public class ForumLike {
    
    /** 点赞ID */
    private Long likeId;
    
    /** 用户ID */
    private Long userId;
    
    /** 目标ID（帖子ID或评论ID） */
    private Long targetId;
    
    /** 目标类型（1帖子 2评论） */
    private Integer targetType;
    
    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}
