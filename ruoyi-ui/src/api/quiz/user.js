import request from '@/utils/request'

// 查询刷题用户列表
export function listUser(query) {
  return request({
    url: '/quiz/user/list',
    method: 'get',
    params: query
  })
}

// 查询刷题用户详细
export function getUser(userId) {
  return request({
    url: '/quiz/user/' + userId,
    method: 'get'
  })
}

// 新增刷题用户
export function addUser(data) {
  return request({
    url: '/quiz/user',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'multipart/form-data' // 可选，axios 通常会自动处理
    }
  })
}

export function resetPwd(data) {
  return request({
    url: '/quiz/student/user/resetPwd' ,
    method: 'post',
    data
  })
}


// 修改刷题用户
export function updateUser(data) {
  return request({
    url: '/quiz/user',
    method: 'put',
    data: data,
    headers: {
      'Content-Type': 'multipart/form-data' // 可选，axios 通常会自动处理
    }
  })
}

// 删除刷题用户
export function delUser(userId) {
  return request({
    url: '/quiz/user/' + userId,
    method: 'delete'
  })
}
