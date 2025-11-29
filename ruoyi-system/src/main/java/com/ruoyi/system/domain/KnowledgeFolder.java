package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 知识点收藏夹对象 knowledge_folder
 * 
 * @author ruoyi
 * @date 2025-11-29
 */
public class KnowledgeFolder extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 收藏夹ID */
    private Long folderId;

    /** 收藏夹名称 */
    @Excel(name = "收藏夹名称")
    private String folderName;

    /** 收藏夹描述 */
    @Excel(name = "收藏夹描述")
    private String description;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    /** 是否默认收藏夹(1是0否) */
    @Excel(name = "是否默认收藏夹", readConverterExp = "1=是,0=否")
    private Integer isDefault;

    /** 是否公开(1是0否) */
    @Excel(name = "是否公开", readConverterExp = "1=是,0=否")
    private Integer isPublic;

    /** 收藏数量 */
    @Excel(name = "收藏数量")
    private Integer collectCount;

    public void setFolderId(Long folderId) 
    {
        this.folderId = folderId;
    }

    public Long getFolderId() 
    {
        return folderId;
    }

    public void setFolderName(String folderName) 
    {
        this.folderName = folderName;
    }

    public String getFolderName() 
    {
        return folderName;
    }

    public void setDescription(String description) 
    {
        this.description = description;
    }

    public String getDescription() 
    {
        return description;
    }

    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }

    public void setIsDefault(Integer isDefault) 
    {
        this.isDefault = isDefault;
    }

    public Integer getIsDefault() 
    {
        return isDefault;
    }

    public void setIsPublic(Integer isPublic) 
    {
        this.isPublic = isPublic;
    }

    public Integer getIsPublic() 
    {
        return isPublic;
    }

    public void setCollectCount(Integer collectCount) 
    {
        this.collectCount = collectCount;
    }

    public Integer getCollectCount() 
    {
        return collectCount;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("folderId", getFolderId())
            .append("folderName", getFolderName())
            .append("description", getDescription())
            .append("userId", getUserId())
            .append("isDefault", getIsDefault())
            .append("isPublic", getIsPublic())
            .append("collectCount", getCollectCount())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
