import request from '@/utils/request'

/**
 * 获取情感走向数据
 * @param {string} resourceId - 资源ID
 */
export function getEmotionArc(resourceId) {
  return request({
    url: `/emotion-arc/${resourceId}`,
    method: 'get'
  })
}

/**
 * 触发情感走向生成
 * @param {string} resourceId - 资源ID
 */
export function generateEmotionArc(resourceId) {
  return request({
    url: `/emotion-arc/generate/${resourceId}`,
    method: 'post'
  })
}
