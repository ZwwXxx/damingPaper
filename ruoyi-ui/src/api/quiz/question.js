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

// 批量新增完形填空题（父题 + 子题）
export function addClozeQuestion(data) {
  return request({
    url: '/quiz/question/cloze',
    method: 'post',
    data: data
  })
}

// 查询完形填空题详情（父题 + 子题）
export function getClozeQuestion(id) {
  return request({
    url: '/quiz/question/cloze/' + id,
    method: 'get'
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

// 绑定题目与知识点关联
export function bindKnowledgePoints(data) {
  return request({
    url: '/quiz/question/relation/bind',
    method: 'post',
    data: data
  })
}

// 获取题目关联的知识点
export function getQuestionKnowledgePoints(questionId) {
  return request({
    url: '/quiz/question/' + questionId + '/knowledge-points',
    method: 'get'
  })
}

// 获取知识点列表（用于选择器）
export function listKnowledgePoints(query) {
  return request({
    url: '/student/knowledge/points',
    method: 'get',
    params: query
  })
}

// 获取题目绑定的专项栏目ID列表
export function getQuestionPracticeColumns(questionId) {
  return request({
    url: '/quiz/question/' + questionId + '/practice-columns',
    method: 'get'
  })
}

// 保存题目绑定的专项栏目（全量覆盖）
export function bindQuestionPracticeColumns(questionId, columnIds) {
  return request({
    url: '/quiz/question/' + questionId + '/practice-columns',
    method: 'post',
    data: columnIds || []
  })
}
