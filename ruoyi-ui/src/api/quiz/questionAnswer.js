import request from '@/utils/request'

// 查询题目回答情况表列表
export function listQuestionAnswer(query) {
    return request({
        url: '/quiz/questionAnswer/list',
        method: 'get',
        params: query
    })
}

// 查询题目回答情况表详细
export function getQuestionAnswer(answerId) {
    return request({
        url: '/quiz/questionAnswer/' + answerId,
        method: 'get'
    })
}

// 新增题目回答情况表
export function addQuestionAnswer(data) {
    return request({
        url: '/quiz/questionAnswer',
        method: 'post',
        data: data
    })
}

// 修改题目回答情况表
export function updateQuestionAnswer(data) {
    return request({
        url: '/quiz/questionAnswer',
        method: 'put',
        data: data
    })
}

// 删除题目回答情况表
export function delQuestionAnswer(answerId) {
    return request({
        url: '/quiz/questionAnswer/' + answerId,
        method: 'delete'
    })
}
