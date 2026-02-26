import request from '@/utils/request'

/**
 * 获取作者详情
 * @param {string} authorId - 作者业务ID
 * @returns {Promise}
 */
export function getAuthorDetail(authorId) {
  return request({
    url: `/author/${authorId}`,
    method: 'get'
  })
}

/**
 * 根据资源ID和排序查询作者ID
 * @param {string} resourceId - 资源ID
 * @param {number} sort - 排序（1=第一作者，2=第二作者...）
 * @returns {Promise}
 */
export function getAuthorId(resourceId, sort) {
  return request({
    url: '/author/id',
    method: 'get',
    params: { resourceId, sort }
  })
}
