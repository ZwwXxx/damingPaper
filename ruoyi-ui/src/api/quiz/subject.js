import request from '@/utils/request'

// 查询科目列表
export function listSubject(query) {
  return request({
    url: '/quiz/subject/list',
    method: 'get',
    params: query
  })
}

// 查询科目详情
export function getSubject(subjectId) {
  return request({
    url: '/quiz/subject/' + subjectId,
    method: 'get'
  })
}

// 新增科目
export function addSubject(data) {
  return request({
    url: '/quiz/subject',
    method: 'post',
    data: data
  })
}

// 修改科目
export function updateSubject(data) {
  return request({
    url: '/quiz/subject',
    method: 'put',
    data: data
  })
}

// 删除科目
export function delSubject(subjectId) {
  return request({
    url: '/quiz/subject/' + subjectId,
    method: 'delete'
  })
}

// 导出科目
export function exportSubject(query) {
  return request({
    url: '/quiz/subject/export',
    method: 'post',
    data: query,
    responseType: 'blob'
  })
}

// 科目下拉
export function optionSubject() {
  return request({
    url: '/quiz/subject/options',
    method: 'get'
  })
}

