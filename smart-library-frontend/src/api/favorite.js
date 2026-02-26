import request from '@/utils/request'

/**
 * 添加收藏
 * @param {string} resourceId - 资源ID
 * @returns {Promise}
 */
export function addFavorite(resourceId) {
  return request({
    url: `/favorite/add/${resourceId}`,
    method: 'post'
  })
}

/**
 * 取消收藏
 * @param {string} resourceId - 资源ID
 * @returns {Promise}
 */
export function removeFavorite(resourceId) {
  return request({
    url: `/favorite/remove/${resourceId}`,
    method: 'delete'
  })
}

/**
 * 检查是否已收藏
 * @param {string} resourceId - 资源ID
 * @returns {Promise}
 */
export function checkFavorite(resourceId) {
  return request({
    url: `/favorite/check/${resourceId}`,
    method: 'get'
  })
}

/**
 * 获取用户收藏列表
 * @param {Object} params - 查询参数
 * @param {number} params.limit - 查询数量
 * @param {number} params.offset - 偏移量
 * @returns {Promise}
 */
export function getUserFavorites(params) {
  return request({
    url: '/favorite/list',
    method: 'get',
    params
  })
}

/**
 * 统计用户收藏数量
 * @returns {Promise}
 */
export function countUserFavorites() {
  return request({
    url: '/favorite/count',
    method: 'get'
  })
}
