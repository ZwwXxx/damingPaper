import request from '@/utils/request'

// 查询试卷列表
export function listPaper(query) {
  return request({
    url: '/quiz/paper/list',
    method: 'get',
    params: query
  })
}

// 查询试卷详细
export function getPaper(paperId) {
  return request({
    url: '/quiz/paper/' + paperId,
    method: 'get'
  })
}

// 新增试卷
export function addPaper(data) {
  return request({
    url: '/quiz/paper',
    method: 'post',
    data: data
  })
}

// 自动组卷
export function autoComposePaper(data) {
  return request({
    url: '/quiz/paper/auto-compose',
    method: 'post',
    data
  })
}

// 修改试卷
export function updatePaper(data) {
  return request({
    url: '/quiz/paper',
    method: 'put',
    data: data
  })
}

// 删除试卷
export function delPaper(paperId) {
  return request({
    url: '/quiz/paper/' + paperId,
    method: 'delete'
  })
}
