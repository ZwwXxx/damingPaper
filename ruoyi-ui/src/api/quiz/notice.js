import request from '@/utils/request'

// 查询公告列表
export function listNotice(query) {
  return request({
    url: '/quiz/notice/list',
    method: 'get',
    params: query
  })
}

// 查询公告详细
export function getNotice(noticeId) {
  return request({
    url: '/quiz/notice/' + noticeId,
    method: 'get'
  })
}

// 新增公告
export function addNotice(data) {
  return request({
    url: '/quiz/notice',
    method: 'post',
    data: data
  })
}

// 修改公告
export function updateNotice(data) {
  return request({
    url: '/quiz/notice',
    method: 'put',
    data: data
  })
}

// 删除公告
export function delNotice(noticeId) {
  return request({
    url: '/quiz/notice/' + noticeId,
    method: 'delete'
  })
}

// 发布公告
export function publishNotice(noticeId) {
  return request({
    url: '/quiz/notice/publish/' + noticeId,
    method: 'put'
  })
}

// 取消发布公告
export function unpublishNotice(noticeId) {
  return request({
    url: '/quiz/notice/unpublish/' + noticeId,
    method: 'put'
  })
}
