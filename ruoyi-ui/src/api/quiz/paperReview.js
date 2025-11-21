import request from '@/utils/request'

export function listPaperReview(query) {
  return request({
    url: '/quiz/paperReview/list',
    method: 'get',
    params: query
  })
}

export function getPaperReview(paperAnswerId) {
  return request({
    url: `/quiz/paperReview/${paperAnswerId}`,
    method: 'get'
  })
}

export function submitPaperReview(data) {
  return request({
    url: '/quiz/paperReview/score',
    method: 'post',
    data
  })
}

