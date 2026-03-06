package com.dm.quiz.service;

import java.util.List;

public interface IDamingFollowService {
    boolean setFollow(Long followerId, Long followingId, boolean follow);
    boolean isFollowing(Long followerId, Long followingId);
    List<Long> getFollowers(Long userId);
    List<Long> getFollowing(Long userId);
}
