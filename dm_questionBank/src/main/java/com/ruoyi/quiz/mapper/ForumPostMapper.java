package com.ruoyi.quiz.mapper;

import com.ruoyi.common.core.domain.entity.ForumPost;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 论坛帖子Mapper接口
 * 
 * @author ruoyi
 */
public interface ForumPostMapper {
    
    /**
     * 查询帖子列表
     * 
     * @param post 查询条件
     * @return 帖子列表
     */
    List<ForumPost> selectForumPostList(ForumPost post);
    
    /**
     * 根据ID查询帖子详情
     * 
     * @param postId 帖子ID
     * @return 帖子详情
     */
    ForumPost selectForumPostById(Long postId);
    
    /**
     * 新增帖子
     * 
     * @param post 帖子信息
     * @return 影响行数
     */
    int insertForumPost(ForumPost post);
    
    /**
     * 更新帖子
     * 
     * @param post 帖子信息
     * @return 影响行数
     */
    int updateForumPost(ForumPost post);
    
    /**
     * 删除帖子
     * 
     * @param postId 帖子ID
     * @return 影响行数
     */
    int deleteForumPostById(Long postId);
    
    /**
     * 批量删除帖子
     * 
     * @param postIds 帖子ID数组
     * @return 影响行数
     */
    int deleteForumPostByIds(Long[] postIds);
    
    /**
     * 增加浏览次数
     * 
     * @param postId 帖子ID
     * @return 影响行数
     */
    int incrementViewCount(Long postId);
    
    /**
     * 增加点赞数
     * 
     * @param postId 帖子ID
     * @return 影响行数
     */
    int incrementLikeCount(Long postId);
    
    /**
     * 减少点赞数
     * 
     * @param postId 帖子ID
     * @return 影响行数
     */
    int decrementLikeCount(Long postId);
    
    /**
     * 增加评论数
     * 
     * @param postId 帖子ID
     * @return 影响行数
     */
    int incrementCommentCount(Long postId);
    
    /**
     * 减少评论数
     * 
     * @param postId 帖子ID
     * @return 影响行数
     */
    int decrementCommentCount(Long postId);
}
