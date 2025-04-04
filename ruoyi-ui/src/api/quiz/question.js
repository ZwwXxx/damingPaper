import request from '@/utils/request'

// 查询题目表列表
export function listQuestion(query) {
  return request({
    url: '/quiz/question/list',
    method: 'get',
    params: query
  })
}

// 查询题目表详细
export function getQuestion(id) {
  return request({
    url: '/quiz/question/' + id,
    method: 'get'
  })
}

// 新增题目表
export function addQuestion(data) {
  return request({
    url: '/quiz/question',
    method: 'post',
    data: data
  })
}

// 修改题目表
export function updateQuestion(data) {
  return request({
    url: '/quiz/question',
    method: 'put',
    data: data
  })
}

// 删除题目表
export function delQuestion(id) {
  return request({
    url: '/quiz/question/' + id,
    method: 'delete'
  })
}
