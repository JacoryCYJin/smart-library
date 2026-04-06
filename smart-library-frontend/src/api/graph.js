import request from '@/utils/request'

/**
 * 获取资源的人物关系图谱
 * @param {string} resourceId - 资源ID
 * @param {boolean} autoGenerate - 是否自动生成（可选，默认 true）
 * @returns {Promise}
 */
export function getGraph(resourceId, autoGenerate = true) {
  return request({
    url: `/graph/${resourceId}`,
    method: 'get',
    params: { autoGenerate }
  })
}

/**
 * 手动触发生成人物关系图谱
 * @param {string} resourceId - 资源ID
 * @returns {Promise}
 */
export function generateGraph(resourceId) {
  return request({
    url: `/graph/generate/${resourceId}`,
    method: 'post',
  })
}
