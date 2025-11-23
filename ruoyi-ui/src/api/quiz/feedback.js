import request from '@/utils/request'

// 查询反馈列表
export function listFeedback(query) {
  return request({
    url: '/quiz/feedback/list',
    method: 'get',
    params: query
  })
}

// 查询反馈详细
export function getFeedback(feedbackId) {
  return request({
    url: '/quiz/feedback/' + feedbackId,
    method: 'get'
  })
}

// 新增反馈
export function addFeedback(data) {
  return request({
    url: '/quiz/feedback',
    method: 'post',
    data: data
  })
}

// 修改反馈
export function updateFeedback(data) {
  return request({
    url: '/quiz/feedback',
    method: 'put',
    data: data
  })
}

// 删除反馈
export function delFeedback(feedbackId) {
  return request({
    url: '/quiz/feedback/' + feedbackId,
    method: 'delete'
  })
}

// 处理反馈
export function handleFeedback(data) {
  return request({
    url: '/quiz/feedback/handle',
    method: 'put',
    data: data
  })
}

// 统计反馈数量（按状态）
export function countByStatus() {
  return request({
    url: '/quiz/feedback/stats/status',
    method: 'get'
  })
}

// 统计反馈数量（按类型）
export function countByType() {
  return request({
    url: '/quiz/feedback/stats/type',
    method: 'get'
  })
}
