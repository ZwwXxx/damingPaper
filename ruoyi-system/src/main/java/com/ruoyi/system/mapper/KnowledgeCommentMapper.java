package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.KnowledgeComment;

/**
 * 知识点评论Mapper接口
 * 
 * @author daming
 * @date 2025-11-28
 */
public interface KnowledgeCommentMapper 
{
    /**
     * 查询知识点评论
     * 
     * @param commentId 知识点评论主键
     * @return 知识点评论
     */
    public KnowledgeComment selectKnowledgeCommentByCommentId(Long commentId);

    /**
     * 查询知识点评论列表
     * 
     * @param knowledgeComment 知识点评论
     * @return 知识点评论集合
     */
    public List<KnowledgeComment> selectKnowledgeCommentList(KnowledgeComment knowledgeComment);

    /**
     * 查询知识点的一级评论列表（按时间倒序）
     * 
     * @param pointId 知识点ID
     * @return 评论集合
     */
    public List<KnowledgeComment> selectRootCommentsByPointId(@Param("pointId") Long pointId);

    /**
     * 查询指定父评论的子评论列表（按时间正序）
     * 
     * @param parentId 父评论ID
     * @return 子评论集合
     */
    public List<KnowledgeComment> selectChildCommentsByParentId(@Param("parentId") Long parentId);

    /**
     * 新增知识点评论
     * 
     * @param knowledgeComment 知识点评论
     * @return 结果
     */
    public int insertKnowledgeComment(KnowledgeComment knowledgeComment);

    /**
     * 修改知识点评论
     * 
     * @param knowledgeComment 知识点评论
     * @return 结果
     */
    public int updateKnowledgeComment(KnowledgeComment knowledgeComment);

    /**
     * 删除知识点评论
     * 
     * @param commentId 知识点评论主键
     * @return 结果
     */
    public int deleteKnowledgeCommentByCommentId(Long commentId);

    /**
     * 批量删除知识点评论
     * 
     * @param commentIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteKnowledgeCommentByCommentIds(Long[] commentIds);

    /**
     * 增加评论点赞数
     * 
     * @param commentId 评论ID
     * @return 结果
     */
    public int increaseLikeCount(@Param("commentId") Long commentId);

    /**
     * 减少评论点赞数
     * 
     * @param commentId 评论ID
     * @return 结果
     */
    public int decreaseLikeCount(@Param("commentId") Long commentId);

    /**
     * 统计知识点的评论总数
     * 
     * @param pointId 知识点ID
     * @return 评论总数
     */
    public int countCommentsByPointId(@Param("pointId") Long pointId);
}
