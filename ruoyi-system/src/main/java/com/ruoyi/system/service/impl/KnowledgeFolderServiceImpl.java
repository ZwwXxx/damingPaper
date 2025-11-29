package com.ruoyi.system.service.impl;

import java.util.List;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.KnowledgeFolderMapper;
import com.ruoyi.system.domain.KnowledgeFolder;
import com.ruoyi.system.service.IKnowledgeFolderService;

/**
 * 知识点收藏夹Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-11-29
 */
@Service
public class KnowledgeFolderServiceImpl implements IKnowledgeFolderService 
{
    @Autowired
    private KnowledgeFolderMapper knowledgeFolderMapper;

    /**
     * 查询知识点收藏夹
     * 
     * @param folderId 知识点收藏夹主键
     * @return 知识点收藏夹
     */
    @Override
    public KnowledgeFolder selectKnowledgeFolderByFolderId(Long folderId)
    {
        return knowledgeFolderMapper.selectKnowledgeFolderByFolderId(folderId);
    }

    /**
     * 查询知识点收藏夹列表
     * 
     * @param knowledgeFolder 知识点收藏夹
     * @return 知识点收藏夹
     */
    @Override
    public List<KnowledgeFolder> selectKnowledgeFolderList(KnowledgeFolder knowledgeFolder)
    {
        return knowledgeFolderMapper.selectKnowledgeFolderList(knowledgeFolder);
    }

    /**
     * 查询用户的收藏夹列表
     * 
     * @param userId 用户ID
     * @return 收藏夹集合
     */
    @Override
    public List<KnowledgeFolder> selectFoldersByUserId(Long userId)
    {
        return knowledgeFolderMapper.selectFoldersByUserId(userId);
    }

    /**
     * 获取或创建用户的默认收藏夹
     * 
     * @param userId 用户ID
     * @return 默认收藏夹
     */
    @Override
    public KnowledgeFolder getOrCreateDefaultFolder(Long userId)
    {
        // 先查找是否已有默认收藏夹
        KnowledgeFolder defaultFolder = knowledgeFolderMapper.selectDefaultFolderByUserId(userId);
        
        if (defaultFolder == null) {
            // 创建默认收藏夹
            defaultFolder = new KnowledgeFolder();
            defaultFolder.setFolderName("默认收藏夹");
            defaultFolder.setDescription("系统自动创建的默认收藏夹");
            defaultFolder.setUserId(userId);
            defaultFolder.setIsDefault(1);
            defaultFolder.setIsPublic(0);
            defaultFolder.setCollectCount(0);
            defaultFolder.setCreateTime(new Date());
            defaultFolder.setUpdateTime(new Date());
            
            knowledgeFolderMapper.insertKnowledgeFolder(defaultFolder);
        }
        
        return defaultFolder;
    }

    /**
     * 新增知识点收藏夹
     * 
     * @param knowledgeFolder 知识点收藏夹
     * @return 结果
     */
    @Override
    public int insertKnowledgeFolder(KnowledgeFolder knowledgeFolder)
    {
        knowledgeFolder.setCreateTime(new Date());
        knowledgeFolder.setUpdateTime(new Date());
        return knowledgeFolderMapper.insertKnowledgeFolder(knowledgeFolder);
    }

    /**
     * 修改知识点收藏夹
     * 
     * @param knowledgeFolder 知识点收藏夹
     * @return 结果
     */
    @Override
    public int updateKnowledgeFolder(KnowledgeFolder knowledgeFolder)
    {
        knowledgeFolder.setUpdateTime(new Date());
        return knowledgeFolderMapper.updateKnowledgeFolder(knowledgeFolder);
    }

    /**
     * 批量删除知识点收藏夹
     * 
     * @param folderIds 需要删除的知识点收藏夹主键
     * @return 结果
     */
    @Override
    public int deleteKnowledgeFolderByFolderIds(Long[] folderIds)
    {
        return knowledgeFolderMapper.deleteKnowledgeFolderByFolderIds(folderIds);
    }

    /**
     * 删除知识点收藏夹信息
     * 
     * @param folderId 知识点收藏夹主键
     * @return 结果
     */
    @Override
    public int deleteKnowledgeFolderByFolderId(Long folderId)
    {
        return knowledgeFolderMapper.deleteKnowledgeFolderByFolderId(folderId);
    }

    /**
     * 更新收藏夹的收藏数量
     * 
     * @param folderId 收藏夹ID
     * @param count 数量增减值
     * @return 结果
     */
    @Override
    public int updateFolderCollectCount(Long folderId, int count)
    {
        return knowledgeFolderMapper.updateFolderCollectCount(folderId, count);
    }
}
