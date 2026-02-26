import request from '@/utils/request'

/**
 * 搜索图书（支持分页和多条件查询）
 * @param {Object} params - 搜索参数
 * @param {number} params.pageNum - 页码
 * @param {number} params.pageSize - 每页数量
 * @param {string} params.keyword - 搜索关键词
 * @param {Array} params.categoryIds - 分类ID数组
 * @param {Array} params.tagIds - 标签ID数组
 * @param {string} params.sortBy - 排序字段
 * @returns {Promise}
 */
export function searchBooks(params) {
  return request({
    url: '/book/search',
    method: 'post',
    data: params
  })
}

/**
 * 获取图书详情
 * @param {string} bookId - 图书业务ID
 * @returns {Promise}
 */
export function getBookDetail(bookId) {
  return request({
    url: `/book/${bookId}`,
    method: 'get'
  })
}

/**
 * 获取所有分类
 * @returns {Promise}
 */
export function getCategories() {
  return request({
    url: '/category/list',
    method: 'get'
  })
}

/**
 * 获取所有标签
 * @returns {Promise}
 */
export function getTags() {
  return request({
    url: '/tag/list',
    method: 'get'
  })
}
