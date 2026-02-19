package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.KnowledgeTag;

/**
 * 知识点标签Mapper接口
 *
 * @author ruoyi
 */
public interface KnowledgeTagMapper {

    KnowledgeTag selectKnowledgeTagByTagId(Long tagId);

    List<KnowledgeTag> selectKnowledgeTagList(KnowledgeTag knowledgeTag);

    int insertKnowledgeTag(KnowledgeTag knowledgeTag);

    int updateKnowledgeTag(KnowledgeTag knowledgeTag);

    int deleteKnowledgeTagByTagId(Long tagId);

    int deleteKnowledgeTagByTagIds(Long[] tagIds);

    /**
     * 根据标签名查询（用于重名校验）
     */
    KnowledgeTag selectByTagName(String tagName);
}
