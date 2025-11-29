package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.KnowledgeFolder;

/**
 * 知识点收藏夹Service接口
 * 
 * @author ruoyi
 * @date 2025-11-29
 */
public interface IKnowledgeFolderService 
{
    /**
     * 查询知识点收藏夹
     * 
     * @param folderId 知识点收藏夹主键
     * @return 知识点收藏夹
     */
    public KnowledgeFolder selectKnowledgeFolderByFolderId(Long folderId);

    /**
     * 查询知识点收藏夹列表
     * 
     * @param knowledgeFolder 知识点收藏夹
     * @return 知识点收藏夹集合
     */
    public List<KnowledgeFolder> selectKnowledgeFolderList(KnowledgeFolder knowledgeFolder);

    /**
     * 查询用户的收藏夹列表
     * 
     * @param userId 用户ID
     * @return 收藏夹集合
     */
    public List<KnowledgeFolder> selectFoldersByUserId(Long userId);

    /**
     * 获取或创建用户的默认收藏夹
     * 
     * @param userId 用户ID
     * @return 默认收藏夹
     */
    public KnowledgeFolder getOrCreateDefaultFolder(Long userId);

    /**
     * 新增知识点收藏夹
     * 
     * @param knowledgeFolder 知识点收藏夹
     * @return 结果
     */
    public int insertKnowledgeFolder(KnowledgeFolder knowledgeFolder);

    /**
     * 修改知识点收藏夹
     * 
     * @param knowledgeFolder 知识点收藏夹
     * @return 结果
     */
    public int updateKnowledgeFolder(KnowledgeFolder knowledgeFolder);

    /**
     * 批量删除知识点收藏夹
     * 
     * @param folderIds 需要删除的知识点收藏夹主键集合
     * @return 结果
     */
    public int deleteKnowledgeFolderByFolderIds(Long[] folderIds);

    /**
     * 删除知识点收藏夹信息
     * 
     * @param folderId 知识点收藏夹主键
     * @return 结果
     */
    public int deleteKnowledgeFolderByFolderId(Long folderId);

    /**
     * 更新收藏夹的收藏数量
     * 
     * @param folderId 收藏夹ID
     * @param count 数量增减值
     * @return 结果
     */
    public int updateFolderCollectCount(Long folderId, int count);
}
