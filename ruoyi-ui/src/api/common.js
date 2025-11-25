import request from '@/utils/request'

export function getOssSign(params) {
  return request({
    url: '/common/oss/sign',
    method: 'get',
    params
  })
}

/**
 * 上传文件到OSS（通用上传接口）
 * @param formData FormData对象，包含file字段
 * @returns {url, fileName, newFileName, originalFilename}
 */
export function uploadFile(formData) {
  return request({
    url: '/common/upload',
    method: 'post',
    data: formData,
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}
