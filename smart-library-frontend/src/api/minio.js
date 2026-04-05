import request from '@/utils/request'

/**
 * 上传文件到MinIO
 * @param {FormData} formData - 包含文件和bucketName的表单数据
 * @returns {Promise}
 */
export function uploadFile(formData) {
  return request({
    url: '/file/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}
