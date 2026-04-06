import request from '@/utils/request'

/**
 * 智能随机推荐
 * @param {number} limit - 推荐数量
 * @returns {Promise}
 */
export function getRecommendations(limit = 12) {
  return request({
    url: '/serendipity/recommend',
    method: 'get',
    params: { limit }
  })
}
