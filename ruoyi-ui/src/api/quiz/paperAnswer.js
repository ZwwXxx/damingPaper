import request from '@/utils/request'

// 查询试卷作答情况列表
export function listPaperAnswer(query) {
  return request({
    url: '/quiz/paperAnswer/list',
    method: 'get',
    params: query
  })
}

// 查询试卷作答情况详细
export function getPaperAnswer(paperAnswerId) {
  return request({
    url: '/quiz/paperAnswer/' + paperAnswerId,
    method: 'get'
  })
}

// 新增试卷作答情况
export function addPaperAnswer(data) {
  return request({
    url: '/quiz/paperAnswer',
    method: 'post',
    data: data
  })
}

// 修改试卷作答情况
export function updatePaperAnswer(data) {
  return request({
    url: '/quiz/paperAnswer',
    method: 'put',
    data: data
  })
}

// 删除试卷作答情况
export function delPaperAnswer(paperAnswerId) {
  return request({
    url: '/quiz/paperAnswer/' + paperAnswerId,
    method: 'delete'
  })
}
