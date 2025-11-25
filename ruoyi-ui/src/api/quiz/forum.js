import request from '@/utils/request'

// ==================== 帖子管理 ====================

/**
 * 查询帖子列表
 */
export function listPost(query) {
  return request({
    url: '/quiz/admin/forum/post/list',
    method: 'get',
    params: query
  })
}

/**
 * 查询帖子详细
 */
export function getPost(postId) {
  return request({
    url: '/quiz/admin/forum/post/' + postId,
    method: 'get'
  })
}

/**
 * 删除帖子
 */
export function delPost(postId) {
  return request({
    url: '/quiz/admin/forum/post/' + postId,
    method: 'delete'
  })
}

/**
 * 批量删除帖子
 */
export function delPosts(postIds) {
  return request({
    url: '/quiz/admin/forum/post/' + postIds,
    method: 'delete'
  })
}

/**
 * 置顶/取消置顶帖子
 */
export function toggleTop(postId, isTop) {
  return request({
    url: '/quiz/admin/forum/post/top',
    method: 'put',
    data: {
      postId,
      isTop
    }
  })
}

/**
 * 设置/取消热门帖子
 */
export function toggleHot(postId, isHot) {
  return request({
    url: '/quiz/admin/forum/post/hot',
    method: 'put',
    data: {
      postId,
      isHot
    }
  })
}

/**
 * 审核帖子
 */
export function auditPost(postId, status) {
  return request({
    url: '/quiz/admin/forum/post/audit',
    method: 'put',
    data: {
      postId,
      status
    }
  })
}

// ==================== 评论管理 ====================

/**
 * 查询评论列表
 */
export function listComment(query) {
  return request({
    url: '/quiz/admin/forum/comment/list',
    method: 'get',
    params: query
  })
}

/**
 * 查询评论详细
 */
export function getComment(commentId) {
  return request({
    url: '/quiz/admin/forum/comment/' + commentId,
    method: 'get'
  })
}

/**
 * 删除评论
 */
export function delComment(commentId) {
  return request({
    url: '/quiz/admin/forum/comment/' + commentId,
    method: 'delete'
  })
}

/**
 * 批量删除评论
 */
export function delComments(commentIds) {
  return request({
    url: '/quiz/admin/forum/comment/' + commentIds,
    method: 'delete'
  })
}
