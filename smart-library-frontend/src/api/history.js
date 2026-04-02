import request from '@/utils/request'

/**
 * 分页查询用户浏览历史
 * @param {Object} data - 查询参数
 * @param {number} data.pageNum - 页码（默认 1）
 * @param {number} data.pageSize - 每页数量（默认 20）
 * @returns {Promise}
 */
export function getBrowseHistory(data) {
  return request({
    url: '/history/list',
    method: 'post',
    data
  })
}
