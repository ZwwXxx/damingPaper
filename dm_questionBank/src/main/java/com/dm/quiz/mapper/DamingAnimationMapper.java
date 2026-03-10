package com.dm.quiz.mapper;

import com.dm.quiz.domain.DamingAnimation;
import java.util.List;

/**
 * 动画库Mapper接口
 *
 * @author zww
 * @date 2026-03-04
 */
public interface DamingAnimationMapper {

    /**
     * 查询动画详情
     */
    DamingAnimation selectDamingAnimationByAnimationId(Long animationId);

    /**
     * 查询动画列表
     */
    List<DamingAnimation> selectDamingAnimationList(DamingAnimation damingAnimation);

    /**
     * 新增动画
     */
    int insertDamingAnimation(DamingAnimation damingAnimation);

    /**
     * 软删除动画
     */
    int deleteDamingAnimationByAnimationIds(Long[] animationIds);
}

