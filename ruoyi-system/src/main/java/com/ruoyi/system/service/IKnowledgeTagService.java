package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.KnowledgeTag;

/**
 * 知识点标签Service接口
 *
 * @author ruoyi
 */
public interface IKnowledgeTagService {

    KnowledgeTag selectKnowledgeTagByTagId(Long tagId);

    List<KnowledgeTag> selectKnowledgeTagList(KnowledgeTag knowledgeTag);

    int insertKnowledgeTag(KnowledgeTag knowledgeTag);

    int updateKnowledgeTag(KnowledgeTag knowledgeTag);

    int deleteKnowledgeTagByTagIds(Long[] tagIds);

    int deleteKnowledgeTagByTagId(Long tagId);

    /**
     * 根据标签名查询（重名校验）
     */
    KnowledgeTag selectByTagName(String tagName);
}
