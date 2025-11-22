import request from '@/utils/request'

export function getOssSign(params) {
  return request({
    url: '/common/oss/sign',
    method: 'get',
    params
  })
}

