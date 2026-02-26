import request from '@/utils/request'

/**
 * 获取评论列表
 */
export function getComments(params) {
  return request({
    url: '/comment/list',
    method: 'get',
    params
  })
}

/**
 * 创建评论
 */
export function createComment(data) {
  return request({
    url: '/comment/create',
    method: 'post',
    data
  })
}

/**
 * 删除评论
 */
export function deleteComment(commentId) {
  return request({
    url: `/comment/${commentId}`,
    method: 'delete'
  })
}
