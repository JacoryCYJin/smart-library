import request from '@/utils/request'

/**
 * 用户登录
 * @param {Object} data - 登录数据
 * @param {string} data.phoneOrEmail - 手机号或邮箱
 * @param {string} data.password - 密码
 * @returns {Promise}
 */
export function login(data) {
  return request({
    url: '/user/login',
    method: 'post',
    data
  })
}

/**
 * 用户注册
 * @param {Object} data - 注册数据
 * @param {string} data.phoneOrEmail - 手机号或邮箱
 * @param {string} data.password - 密码
 * @param {string} data.confirmPassword - 确认密码
 * @returns {Promise}
 */
export function register(data) {
  return request({
    url: '/user/register',
    method: 'post',
    data
  })
}

/**
 * 搜索用户
 * @param {Object} data - 搜索条件
 * @returns {Promise}
 */
export function searchUser(data) {
  return request({
    url: '/user/search',
    method: 'post',
    data
  })
}

/**
 * 退出登录
 * @returns {Promise}
 */
export function logout() {
  return request({
    url: '/user/logout',
    method: 'post'
  })
}

/**
 * 获取当前用户信息
 * @returns {Promise}
 */
export function getProfile() {
  return request({
    url: '/user/profile',
    method: 'post'
  })
}

/**
 * 更新用户信息
 * @param {Object} data - 用户信息
 * @returns {Promise}
 */
export function updateProfile(data) {
  return request({
    url: '/user/update',
    method: 'post',
    data
  })
}

/**
 * 统计用户的评论数量
 * @returns {Promise}
 */
export function countUserComments() {
  return request({
    url: '/user/count-comments',
    method: 'post'
  })
}

/**
 * 修改密码
 * @param {Object} data - 包含旧密码和新密码
 * @param {string} data.oldPassword - 旧密码
 * @param {string} data.newPassword - 新密码
 * @returns {Promise}
 */
export function changePassword(data) {
  return request({
    url: '/user/change-password',
    method: 'post',
    data
  })
}

/**
 * 修改手机号
 * @param {Object} data - 包含旧手机号、新手机号和密码
 * @param {string} data.oldPhone - 旧手机号
 * @param {string} data.newPhone - 新手机号
 * @param {string} data.password - 密码确认
 * @returns {Promise}
 */
export function changePhone(data) {
  return request({
    url: '/user/change-phone',
    method: 'post',
    data
  })
}

/**
 * 修改邮箱
 * @param {Object} data - 包含旧邮箱、新邮箱和密码
 * @param {string} data.oldEmail - 旧邮箱
 * @param {string} data.newEmail - 新邮箱
 * @param {string} data.password - 密码确认
 * @returns {Promise}
 */
export function changeEmail(data) {
  return request({
    url: '/user/change-email',
    method: 'post',
    data
  })
}
