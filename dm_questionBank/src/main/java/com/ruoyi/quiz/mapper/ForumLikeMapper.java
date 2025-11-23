package com.ruoyi.quiz.mapper;

import com.ruoyi.common.core.domain.entity.ForumLike;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 论坛点赞Mapper接口
 * 
 * @author ruoyi
 */
public interface ForumLikeMapper {
    
    /**
     * 新增点赞
     * 
     * @param like 点赞信息
     * @return 影响行数
     */
    int insertForumLike(ForumLike like);
    
    /**
     * 删除点赞
     * 
     * @param userId 用户ID
     * @param targetId 目标ID
     * @param targetType 目标类型
     * @return 影响行数
     */
    int deleteForumLike(@Param("userId") Long userId, 
                        @Param("targetId") Long targetId, 
                        @Param("targetType") Integer targetType);
    
    /**
     * 检查是否已点赞
     * 
     * @param userId 用户ID
     * @param targetId 目标ID
     * @param targetType 目标类型
     * @return 点赞记录数量
     */
    int checkLikeExists(@Param("userId") Long userId, 
                        @Param("targetId") Long targetId, 
                        @Param("targetType") Integer targetType);
    
    /**
     * 批量检查点赞状态
     * 
     * @param userId 用户ID
     * @param targetIds 目标ID列表
     * @param targetType 目标类型
     * @return 已点赞的目标ID列表
     */
    List<Long> batchCheckLikeExists(@Param("userId") Long userId, 
                                     @Param("targetIds") List<Long> targetIds, 
                                     @Param("targetType") Integer targetType);
    
    /**
     * 根据目标删除所有点赞
     * 
     * @param targetId 目标ID
     * @param targetType 目标类型
     * @return 影响行数
     */
    int deleteLikesByTarget(@Param("targetId") Long targetId, 
                            @Param("targetType") Integer targetType);
}
