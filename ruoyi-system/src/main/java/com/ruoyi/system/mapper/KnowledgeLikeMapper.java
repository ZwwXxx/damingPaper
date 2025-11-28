package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.KnowledgeLike;
import org.apache.ibatis.annotations.Param;

/**
 * 知识点点赞Mapper接口
 * 
 * @author ruoyi
 * @date 2025-11-28
 */
public interface KnowledgeLikeMapper 
{
    /**
     * 查询用户是否已点赞
     * 
     * @param userId 用户ID
     * @param pointId 知识点ID
     * @return 点赞记录，如果未点赞则返回null
     */
    public KnowledgeLike selectByUserAndPoint(@Param("userId") Long userId, @Param("pointId") Long pointId);

    /**
     * 新增点赞
     * 
     * @param knowledgeLike 点赞信息
     * @return 结果
     */
    public int insertKnowledgeLike(KnowledgeLike knowledgeLike);

    /**
     * 删除点赞（取消点赞）
     * 
     * @param userId 用户ID
     * @param pointId 知识点ID
     * @return 结果
     */
    public int deleteByUserAndPoint(@Param("userId") Long userId, @Param("pointId") Long pointId);
}
