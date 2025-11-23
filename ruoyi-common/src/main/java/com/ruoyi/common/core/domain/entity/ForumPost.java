package com.ruoyi.common.core.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 论坛帖子实体
 * 
 * @author ruoyi
 */
@Data
public class ForumPost {
    
    /** 帖子ID */
    private Long postId;
    
    /** 发帖人ID */
    @Excel(name = "发帖人ID")
    private Long userId;
    
    /** 发帖人用户名 */
    @Excel(name = "发帖人用户名")
    private String userName;
    
    /** 发帖人昵称 */
    @Excel(name = "发帖人昵称")
    private String nickName;
    
    /** 发帖人头像 */
    private String avatar;
    
    /** 帖子标题 */
    @Excel(name = "帖子标题")
    private String title;
    
    /** 帖子内容 */
    @Excel(name = "帖子内容")
    private String content;
    
    /** 图片列表（JSON数组转换为List） */
    private List<String> images;
    
    /** 图片JSON字符串（用于数据库存储） */
    private String imagesJson;
    
    /** 浏览次数 */
    @Excel(name = "浏览次数")
    private Integer viewCount;
    
    /** 点赞数 */
    @Excel(name = "点赞数")
    private Integer likeCount;
    
    /** 评论数 */
    @Excel(name = "评论数")
    private Integer commentCount;
    
    /** 是否置顶（0否 1是） */
    @Excel(name = "是否置顶", readConverterExp = "0=否,1=是")
    private Integer isTop;
    
    /** 是否热门（0否 1是） */
    @Excel(name = "是否热门", readConverterExp = "0=否,1=是")
    private Integer isHot;
    
    /** 状态（0删除 1正常 2审核中） */
    @Excel(name = "状态", readConverterExp = "0=删除,1=正常,2=审核中")
    private Integer status;
    
    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    
    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    
    /** 当前用户是否已点赞（前端使用，不存数据库） */
    private Boolean hasLiked;
}
