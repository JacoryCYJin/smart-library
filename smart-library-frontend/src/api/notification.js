import request from '@/utils/request'

/**
 * 获取通知列表
 */
export function getNotifications(limit = 20) {
  return request({
    url: '/notification/list',
    method: 'get',
    params: { limit }
  })
}

/**
 * 获取未读通知数量
 */
export function getUnreadCount() {
  return request({
    url: '/notification/unread-count',
    method: 'get'
  })
}

/**
 * 标记通知为已读
 */
export function markAsRead(notificationId) {
  return request({
    url: `/notification/read/${notificationId}`,
    method: 'post'
  })
}

/**
 * 标记全部为已读
 */
export function markAllAsRead() {
  return request({
    url: '/notification/read-all',
    method: 'post'
  })
}

/**
 * 删除通知
 */
export function deleteNotification(notificationId) {
  return request({
    url: `/notification/${notificationId}`,
    method: 'delete'
  })
}
