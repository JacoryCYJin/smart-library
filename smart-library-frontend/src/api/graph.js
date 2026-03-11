import request from '@/utils/request'

/**
 * 获取资源的人物关系图谱
 * 如果不存在则自动生成
 * @param {string} resourceId - 资源ID
 * @returns {Promise}
 */
export function getGraph(resourceId) {
  return request({
    url: `/graph/${resourceId}`,
    method: 'get',
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
