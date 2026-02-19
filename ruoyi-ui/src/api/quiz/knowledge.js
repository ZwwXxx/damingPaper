import request from '@/utils/request'

// 查询知识点列表（管理端）
export function listKnowledge(query) {
  return request({
    url: '/quiz/knowledge/list',
    method: 'get',
    params: query
  })
}

// 查询知识点详情（管理端）
export function getKnowledge(pointId) {
  return request({
    url: '/quiz/knowledge/' + pointId,
    method: 'get'
  })
}

// 新增知识点
export function addKnowledge(data) {
  return request({
    url: '/quiz/knowledge',
    method: 'post',
    data: data
  })
}

// 修改知识点
export function updateKnowledge(data) {
  return request({
    url: '/quiz/knowledge',
    method: 'put',
    data: data
  })
}

// 删除知识点
export function delKnowledge(pointIds) {
  return request({
    url: '/quiz/knowledge/' + pointIds,
    method: 'delete'
  })
}

// 审核知识点
export function auditKnowledge(data) {
  return request({
    url: '/quiz/knowledge/audit',
    method: 'put',
    data: data
  })
}

// 批量审核知识点
export function batchAuditKnowledge(data) {
  return request({
    url: '/quiz/knowledge/batchAudit',
    method: 'put',
    data: data
  })
}

// 获取知识点科目列表（下拉选择）
export function listKnowledgeSubjects() {
  return request({
    url: '/quiz/knowledge/subjects',
    method: 'get'
  })
}
