import request from '@/utils/request'

/**
 * 获取数据看板统计数据
 */
export function getDashboardStats() {
  return request({
    url: '/admin/dashboard',
    method: 'get'
  })
}

/**
 * 获取趋势数据
 */
export function getTrends(days = 30) {
  return request({
    url: '/admin/trends',
    method: 'get',
    params: { days }
  })
}

/**
 * 获取浏览量排行榜
 */
export function getViewRanking(limit = 10) {
  return request({
    url: '/admin/ranking/views',
    method: 'get',
    params: { limit }
  })
}

/**
 * 获取收藏量排行榜
 */
export function getFavoriteRanking(limit = 10) {
  return request({
    url: '/admin/ranking/favorites',
    method: 'get',
    params: { limit }
  })
}

/**
 * 获取评论量排行榜
 */
export function getCommentRanking(limit = 10) {
  return request({
    url: '/admin/ranking/comments',
    method: 'get',
    params: { limit }
  })
}

/**
 * 获取评分排行榜
 */
export function getRatingRanking(limit = 10) {
  return request({
    url: '/admin/ranking/ratings',
    method: 'get',
    params: { limit }
  })
}

// ==================== 用户管理 ====================

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

// ==================== 资源管理 ====================

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
 * 创建资源
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
 * 获取资源详情
 */
export function getResourceDetail(resourceId) {
  return request({
    url: `/admin/resources/${resourceId}`,
    method: 'get'
  })
}

// ==================== 评论管理 ====================

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

// ==================== 分类管理 ====================

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
    url: '/admin/categories/create',
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

// ==================== 作者管理 ====================

/**
 * 获取作者列表
 */
export function getAuthorList(params) {
  return request({
    url: '/admin/authors/list',
    method: 'post',
    data: params
  })
}

/**
 * 创建作者
 */
export function createAuthor(data) {
  return request({
    url: '/admin/authors/create',
    method: 'post',
    data
  })
}

/**
 * 更新作者
 */
export function updateAuthor(authorId, data) {
  return request({
    url: `/admin/authors/${authorId}`,
    method: 'put',
    data
  })
}

/**
 * 删除作者
 */
export function deleteAuthor(authorId) {
  return request({
    url: `/admin/authors/${authorId}`,
    method: 'delete'
  })
}

/**
 * 获取作者详情
 */
export function getAuthorDetail(authorId) {
  return request({
    url: `/admin/authors/${authorId}`,
    method: 'get'
  })
}

// ==================== 链接管理 ====================

/**
 * 获取链接列表
 */
export function getLinkList(params) {
  return request({
    url: '/admin/links/list',
    method: 'post',
    data: params
  })
}

/**
 * 创建链接
 */
export function createLink(data) {
  return request({
    url: '/admin/links/create',
    method: 'post',
    data
  })
}

/**
 * 更新链接
 */
export function updateLink(linkId, data) {
  return request({
    url: `/admin/links/${linkId}`,
    method: 'put',
    data
  })
}

/**
 * 删除链接
 */
export function deleteLink(linkId) {
  return request({
    url: `/admin/links/${linkId}`,
    method: 'delete'
  })
}

/**
 * 获取链接详情
 */
export function getLinkDetail(linkId) {
  return request({
    url: `/admin/links/${linkId}`,
    method: 'get'
  })
}

// ==================== 人物图谱管理 ====================

/**
 * 获取人物图谱列表
 */
export function getGraphList(params) {
  return request({
    url: '/admin/graphs/list',
    method: 'post',
    data: params
  })
}

/**
 * 删除人物图谱
 */
export function deleteGraph(graphId) {
  return request({
    url: `/admin/graphs/${graphId}`,
    method: 'delete'
  })
}

/**
 * 触发图谱生成
 */
export function triggerGraphGeneration(resourceId, forceGenerate = false) {
  return request({
    url: `/admin/graphs/trigger/${resourceId}`,
    method: 'post',
    params: { forceGenerate }
  })
}

/**
 * 重试图谱生成
 */
export function retryGraphGeneration(graphId) {
  return request({
    url: `/admin/graphs/retry/${graphId}`,
    method: 'post'
  })
}

/**
 * 获取图谱详情
 */
export function getGraphDetail(graphId) {
  return request({
    url: `/admin/graphs/${graphId}`,
    method: 'get'
  })
}

// ==================== 情感曲线管理 ====================

/**
 * 获取情感曲线列表
 */
export function getEmotionArcList(params) {
  return request({
    url: '/admin/emotion-arcs/list',
    method: 'post',
    data: params
  })
}

/**
 * 删除情感曲线
 */
export function deleteEmotionArc(arcId) {
  return request({
    url: `/admin/emotion-arcs/${arcId}`,
    method: 'delete'
  })
}

/**
 * 触发情感曲线生成
 */
export function triggerEmotionArcGeneration(resourceId, forceGenerate = false) {
  return request({
    url: `/admin/emotion-arcs/trigger/${resourceId}`,
    method: 'post',
    params: { forceGenerate }
  })
}

/**
 * 重试情感曲线生成
 */
export function retryEmotionArcGeneration(arcId) {
  return request({
    url: `/admin/emotion-arcs/retry/${arcId}`,
    method: 'post'
  })
}

/**
 * 获取情感曲线详情
 */
export function getEmotionArcDetail(arcId) {
  return request({
    url: `/admin/emotion-arcs/${arcId}`,
    method: 'get'
  })
}

// ==================== 公告管理 ====================

/**
 * 获取公告列表
 */
export function getAnnouncementList(params) {
  return request({
    url: '/admin/announcements',
    method: 'post',
    data: params
  })
}

/**
 * 创建公告
 */
export function createAnnouncement(data) {
  return request({
    url: '/admin/announcements',
    method: 'post',
    data
  })
}

/**
 * 更新公告
 */
export function updateAnnouncement(announcementId, data) {
  return request({
    url: `/admin/announcements/${announcementId}`,
    method: 'put',
    data
  })
}

/**
 * 删除公告
 */
export function deleteAnnouncement(announcementId) {
  return request({
    url: `/admin/announcements/${announcementId}`,
    method: 'delete'
  })
}
