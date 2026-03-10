package com.dm.quiz.service;

import com.dm.quiz.domain.DamingAnimation;
import java.util.List;

/**
 * 动画库Service接口
 *
 * @author zww
 * @date 2026-03-04
 */
public interface IDamingAnimationService {

    DamingAnimation selectDamingAnimationByAnimationId(Long animationId);

    List<DamingAnimation> selectDamingAnimationList(DamingAnimation damingAnimation);

    int insertDamingAnimation(DamingAnimation damingAnimation);

    int deleteDamingAnimationByAnimationIds(Long[] animationIds);
}

