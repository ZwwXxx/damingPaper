import request from '@/utils/request'

// 获取概览统计数据
export function getOverview() {
  return request({
    url: '/dashboard/overview',
    method: 'get'
  })
}

// 获取用户活跃度数据
export function getUserActivity() {
  return request({
    url: '/dashboard/user-activity',
    method: 'get'
  })
}

// 获取考试统计数据
export function getExamStatistics() {
  return request({
    url: '/dashboard/exam-statistics',
    method: 'get'
  })
}

// 获取题目统计数据
export function getQuestionStatistics() {
  return request({
    url: '/dashboard/question-statistics',
    method: 'get'
  })
}

// 获取试卷统计数据
export function getPaperStatistics() {
  return request({
    url: '/dashboard/paper-statistics',
    method: 'get'
  })
}

// 获取时间分布数据
export function getTimeDistribution() {
  return request({
    url: '/dashboard/time-distribution',
    method: 'get'
  })
}
