import request from '@/utils/request'

/**
 * 随机获取一个书签
 * @returns {Promise}
 */
export function getRandomBookmark() {
  return request({
    url: '/bookmark/random',
    method: 'get'
  })
}

/**
 * 记录书签点击
 * @param {string} bookmarkId - 书签ID
 * @returns {Promise}
 */
export function recordBookmarkClick(bookmarkId) {
  return request({
    url: `/bookmark/click/${bookmarkId}`,
    method: 'post'
  })
}
