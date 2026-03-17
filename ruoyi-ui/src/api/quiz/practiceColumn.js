import request from '@/utils/request'

const module = '/quiz/practice/column'

// 一级分组选项（章节/大类）
export function listPracticeGroupOptions(params) {
  return request({
    url: `${module}/groupOptions`,
    method: 'get',
    params
  })
}

// 二级栏目选项（可按 subjectId/groupName 过滤）
export function listPracticeColumnOptions(params) {
  return request({
    url: `${module}/options`,
    method: 'get',
    params
  })
}

// 查询栏目已绑定的题目ID列表（按 sort_order）
export function getPracticeColumnQuestionIds(columnId) {
  return request({
    url: `${module}/${columnId}/questions`,
    method: 'get'
  })
}

// 保存栏目绑定题目（全量覆盖）
export function savePracticeColumnQuestions(columnId, questionIds) {
  return request({
    url: `${module}/${columnId}/questions`,
    method: 'post',
    data: questionIds || []
  })
}

