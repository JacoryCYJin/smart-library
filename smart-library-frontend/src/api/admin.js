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

/**
 * 添加资源
 */
export function createResource(data) {
  return request({
    url: '/admin/resources/create',
    method: 'post',
    data
  })
}

/**
 * 更新资源
 */
export function updateResource(resourceId, data) {
  return request({
    url: `/admin/resources/${resourceId}`,
    method: 'put',
    data
  })
}

/**
 * 获取资源详情（用于编辑）
 */
export function getResourceDetail(resourceId) {
  return request({
    url: `/admin/resources/${resourceId}`,
    method: 'get'
  })
}

/**
 * 获取所有分类列表
 */
export function getAllCategories() {
  return request({
    url: '/admin/categories',
    method: 'get'
  })
}

/**
 * 获取所有标签列表
 */
export function getAllTags() {
  return request({
    url: '/admin/tags',
    method: 'get'
  })
}

/**
 * 搜索作者
 */
export function searchAuthors(keyword) {
  return request({
    url: '/admin/authors/search',
    method: 'get',
    params: { keyword }
  })
}

/**
 * 获取分类树
 */
export function getCategoryTree() {
  return request({
    url: '/admin/categories/tree',
    method: 'get'
  })
}

/**
 * 创建分类
 */
export function createCategory(data) {
  return request({
    url: '/admin/categories',
    method: 'post',
    data
  })
}

/**
 * 更新分类
 */
export function updateCategory(categoryId, data) {
  return request({
    url: `/admin/categories/${categoryId}`,
    method: 'put',
    data
  })
}

/**
 * 删除分类
 */
export function deleteCategory(categoryId) {
  return request({
    url: `/admin/categories/${categoryId}`,
    method: 'delete'
  })
}
