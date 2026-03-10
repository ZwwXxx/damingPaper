/**
 * 上传工具：图片静默压缩
 */
import { compressAccurately } from 'image-conversion'

/** 超过此大小才触发压缩（500KB） */
const COMPRESS_THRESHOLD = 500 * 1024
/** 压缩目标大小（KB），约 1MB */
const COMPRESS_TARGET_KB = 1024

/**
 * 静默压缩图片：超过阈值的图片自动压缩到约 1MB，用户无感知
 * @param {File} file 图片文件
 * @returns {Promise<File>} 压缩后的文件（或原文件）
 */
export async function compressImageIfNeeded(file) {
  if (!file || !file.type.startsWith('image/')) {
    return file
  }
  if (file.size <= COMPRESS_THRESHOLD) {
    return file
  }
  try {
    const blob = await compressAccurately(file, {
      size: COMPRESS_TARGET_KB,
      accuracy: 0.9,
      type: file.type === 'image/png' ? 'image/png' : 'image/jpeg'
    })
    return new File([blob], file.name, { type: blob.type, lastModified: Date.now() })
  } catch (e) {
    console.warn('图片压缩失败，使用原图', e)
    return file
  }
}
