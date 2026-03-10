import request from '@/utils/request'

// 查询动画库列表
export function listAnimation(query) {
  return request({
    url: '/quiz/animation/list',
    method: 'get',
    params: query
  })
}

// 查询动画库详情
export function getAnimation(animationId) {
  return request({
    url: '/quiz/animation/' + animationId,
    method: 'get'
  })
}

// 删除动画（软删除）
export function delAnimation(animationId) {
  return request({
    url: '/quiz/animation/' + animationId,
    method: 'delete'
  })
}

// 导出动画库列表
export function exportAnimation(query) {
  return request({
    url: '/quiz/animation/export',
    method: 'post',
    params: query
  })
}

