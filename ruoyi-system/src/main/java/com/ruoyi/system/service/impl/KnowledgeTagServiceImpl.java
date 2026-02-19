package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.KnowledgeTag;
import com.ruoyi.system.mapper.KnowledgeTagMapper;
import com.ruoyi.system.service.IKnowledgeTagService;

/**
 * 知识点标签Service业务层处理
 *
 * @author ruoyi
 */
@Service
public class KnowledgeTagServiceImpl implements IKnowledgeTagService {

    @Autowired
    private KnowledgeTagMapper knowledgeTagMapper;

    @Override
    public KnowledgeTag selectKnowledgeTagByTagId(Long tagId) {
        return knowledgeTagMapper.selectKnowledgeTagByTagId(tagId);
    }

    @Override
    public List<KnowledgeTag> selectKnowledgeTagList(KnowledgeTag knowledgeTag) {
        return knowledgeTagMapper.selectKnowledgeTagList(knowledgeTag);
    }

    @Override
    public int insertKnowledgeTag(KnowledgeTag knowledgeTag) {
        if (knowledgeTag.getUseCount() == null) {
            knowledgeTag.setUseCount(0);
        }
        return knowledgeTagMapper.insertKnowledgeTag(knowledgeTag);
    }

    @Override
    public int updateKnowledgeTag(KnowledgeTag knowledgeTag) {
        return knowledgeTagMapper.updateKnowledgeTag(knowledgeTag);
    }

    @Override
    public int deleteKnowledgeTagByTagIds(Long[] tagIds) {
        return knowledgeTagMapper.deleteKnowledgeTagByTagIds(tagIds);
    }

    @Override
    public int deleteKnowledgeTagByTagId(Long tagId) {
        return knowledgeTagMapper.deleteKnowledgeTagByTagId(tagId);
    }

    @Override
    public KnowledgeTag selectByTagName(String tagName) {
        return knowledgeTagMapper.selectByTagName(tagName);
    }
}
