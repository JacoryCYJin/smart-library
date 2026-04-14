import request from '@/utils/request'

/**
 * 获取公告列表
 */
export function getAnnouncementList(type) {
  return request({
    url: '/announcement/list',
    method: 'get',
    params: { type }
  })
}

/**
 * 获取公告详情
 */
export function getAnnouncementDetail(announcementId) {
  return request({
    url: `/announcement/${announcementId}`,
    method: 'get'
  })
}

/**
 * 创建公告（管理员）
 */
export function createAnnouncement(data) {
  return request({
    url: '/announcement/create',
    method: 'post',
    data
  })
}

/**
 * 更新公告（管理员）
 */
export function updateAnnouncement(announcementId, data) {
  return request({
    url: `/announcement/${announcementId}`,
    method: 'put',
    data
  })
}

/**
 * 删除公告（管理员）
 */
export function deleteAnnouncement(announcementId) {
  return request({
    url: `/announcement/${announcementId}`,
    method: 'delete'
  })
}
