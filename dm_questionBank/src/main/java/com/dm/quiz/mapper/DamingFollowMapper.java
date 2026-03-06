package com.dm.quiz.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DamingFollowMapper {
    int insertFollow(@Param("followerId") Long followerId, @Param("followingId") Long followingId);
    int deleteFollow(@Param("followerId") Long followerId, @Param("followingId") Long followingId);
    int countFollow(@Param("followerId") Long followerId, @Param("followingId") Long followingId);
    List<Long> selectFollowers(@Param("userId") Long userId);
    List<Long> selectFollowing(@Param("userId") Long userId);
}
