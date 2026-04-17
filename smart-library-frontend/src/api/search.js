import request from '@/utils/request'

/**
 * 全局搜索（同时搜索图书和作者）
 * @param {Object} params - 搜索参数
 * @param {string} params.keyword - 搜索关键词
 * @param {number} params.limit - 每个类型返回的最大结果数
 * @returns {Promise}
 */
export function globalSearch(params) {
  return request({
    url: '/search/global',
    method: 'post',
    data: params
  })
}
