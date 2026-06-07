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

// 更新完形填空题（父题 + 子题）
export function updateClozeQuestion(data) {
  return request({
    url: '/quiz/question/cloze',
    method: 'put',
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

// 仅更新题目的原卷题号
export function updateQuestionOriginOrder(data) {
  return request({
    url: '/quiz/question/origin-order',
    method: 'put',
    data: data
  })
}

// 仅更新题目的考试批次（上/下半年）
export function updateQuestionExamHalf(data) {
  return request({
    url: '/quiz/question/exam-half',
    method: 'put',
    data: data
  })
}

// 批量设置年份与考试批次（未传字段不改）
export function batchUpdateQuestionExamMeta(data) {
  return request({
    url: '/quiz/question/batch-exam-meta',
    method: 'put',
    data: data
  })
}

// 删除题目表（id 可为单个或数组；Snowflake 须用字符串传递，避免 Number 精度问题）
export function delQuestion(id) {
  const path = Array.isArray(id) ? id.join(',') : id
  return request({
    url: '/quiz/question/' + path,
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

// 图片智能录题：创建任务（代理到 daming-ai）
export function createQuestionAiImageTask(formData) {
  return request({
    url: '/quiz/question/ai-image/task',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 图片智能录题：查询任务
export function queryQuestionAiImageTask(taskId) {
  return request({
    url: '/quiz/question/ai-image/task/' + taskId,
    method: 'get'
  })
}
