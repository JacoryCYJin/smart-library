import request from '@/utils/request'

/**
 * 获取统计数据
 */
export function getStats() {
  return request({
    url: '/admin/stats',
    method: 'get'
  })
}

/**
 * 获取用户列表
 */
export function getUserList(params) {
  return request({
    url: '/admin/users',
    method: 'post',
    data: params
  })
}

/**
 * 更新用户状态
 */
export function updateUserStatus(userId, status) {
  return request({
    url: `/admin/users/${userId}/status`,
    method: 'put',
    params: { status }
  })
}

/**
 * 更新用户角色
 */
export function updateUserRole(userId, role) {
  return request({
    url: `/admin/users/${userId}/role`,
    method: 'put',
    params: { role }
  })
}

/**
 * 删除用户
 */
export function deleteUser(userId) {
  return request({
    url: `/admin/users/${userId}`,
    method: 'delete'
  })
}

/**
 * 获取资源列表
 */
export function getResourceList(params) {
  return request({
    url: '/admin/resources',
    method: 'post',
    data: params
  })
}

/**
 * 删除资源
 */
export function deleteResource(resourceId) {
  return request({
    url: `/admin/resources/${resourceId}`,
    method: 'delete'
  })
}

/**
 * 恢复资源
 */
export function restoreResource(resourceId) {
  return request({
    url: `/admin/resources/${resourceId}/restore`,
    method: 'put'
  })
}

/**
 * 获取评论列表
 */
export function getCommentList(params) {
  return request({
    url: '/admin/comments',
    method: 'post',
    data: params
  })
}

/**
 * 审核评论
 */
export function auditComment(commentId, auditStatus, rejectionReason) {
  return request({
    url: `/admin/comments/${commentId}/audit`,
    method: 'put',
    params: { auditStatus, rejectionReason }
  })
}

/**
 * 删除评论
 */
export function deleteComment(commentId) {
  return request({
    url: `/admin/comments/${commentId}`,
    method: 'delete'
  })
}

/**
 * 恢复评论
 */
export function restoreComment(commentId) {
  return request({
    url: `/admin/comments/${commentId}/restore`,
    method: 'put'
  })
}
