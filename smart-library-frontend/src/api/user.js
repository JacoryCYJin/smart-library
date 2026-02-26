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
