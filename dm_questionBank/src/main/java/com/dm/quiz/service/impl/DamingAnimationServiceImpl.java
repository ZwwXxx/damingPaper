package com.dm.quiz.service.impl;

import com.dm.quiz.domain.DamingAnimation;
import com.dm.quiz.mapper.DamingAnimationMapper;
import com.dm.quiz.service.IDamingAnimationService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 动画库Service业务层处理
 *
 * @author zww
 * @date 2026-03-04
 */
@Service
public class DamingAnimationServiceImpl implements IDamingAnimationService {

    @Autowired
    private DamingAnimationMapper damingAnimationMapper;

    @Override
    public DamingAnimation selectDamingAnimationByAnimationId(Long animationId) {
        return damingAnimationMapper.selectDamingAnimationByAnimationId(animationId);
    }

    @Override
    public List<DamingAnimation> selectDamingAnimationList(DamingAnimation damingAnimation) {
        return damingAnimationMapper.selectDamingAnimationList(damingAnimation);
    }

    @Override
    public int insertDamingAnimation(DamingAnimation damingAnimation) {
        return damingAnimationMapper.insertDamingAnimation(damingAnimation);
    }

    @Override
    public int deleteDamingAnimationByAnimationIds(Long[] animationIds) {
        return damingAnimationMapper.deleteDamingAnimationByAnimationIds(animationIds);
    }
}

