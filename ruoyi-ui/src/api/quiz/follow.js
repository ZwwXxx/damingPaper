import request from '@/utils/request'

export function toggleFollow(followingId, follow = true) {
  return request({
    url: `/quiz/student/user/follow/${followingId}`,
    method: 'post',
    params: { follow }
  })
}

export function isFollowing(followingId) {
  return request({
    url: `/quiz/student/user/follow/status/${followingId}`,
    method: 'get'
  })
}

export function countFollowers(userId) {
  return request({
    url: `/quiz/student/user/followers/${userId}/count`,
    method: 'get'
  })
}
