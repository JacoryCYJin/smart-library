import request from '@/utils/request'

/**
 * 获取情感走向数据
 * @param {string} resourceId - 资源ID
 * @param {boolean} autoGenerate - 是否自动生成（可选，默认 true）
 */
export function getEmotionArc(resourceId, autoGenerate = true) {
  return request({
    url: `/emotion-arc/${resourceId}`,
    method: 'get',
    params: { autoGenerate }
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
