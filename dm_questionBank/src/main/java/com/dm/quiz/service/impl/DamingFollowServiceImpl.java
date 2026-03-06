package com.dm.quiz.service.impl;

import com.dm.quiz.mapper.DamingFollowMapper;
import com.dm.quiz.service.IDamingFollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DamingFollowServiceImpl implements IDamingFollowService {

    @Autowired
    private DamingFollowMapper followMapper;

    @Override
    @Transactional
    public boolean setFollow(Long followerId, Long followingId, boolean follow) {
        if (followerId == null || followingId == null) {
            return false;
        }
        if (followerId.equals(followingId)) {
            return false;
        }
        if (follow) {
            return followMapper.insertFollow(followerId, followingId) > 0;
        } else {
            return followMapper.deleteFollow(followerId, followingId) > 0;
        }
    }

    @Override
    public boolean isFollowing(Long followerId, Long followingId) {
        return followMapper.countFollow(followerId, followingId) > 0;
    }

    @Override
    public List<Long> getFollowers(Long userId) {
        return followMapper.selectFollowers(userId);
    }

    @Override
    public List<Long> getFollowing(Long userId) {
        return followMapper.selectFollowing(userId);
    }
}
