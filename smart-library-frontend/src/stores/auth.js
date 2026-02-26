import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

/**
 * 用户认证 Store
 */
export const useAuthStore = defineStore('auth', () => {
  // 状态
  const token = ref(localStorage.getItem('token') || '')
  const user = ref(JSON.parse(localStorage.getItem('user') || 'null'))

  // 计算属性
  const isLoggedIn = computed(() => !!token.value)
  const userId = computed(() => user.value?.userId || null)
  const username = computed(() => user.value?.username || '未登录')
  const avatarUrl = computed(() => user.value?.avatarUrl || '')

  /**
   * 登录
   * @param {Object} userData - 用户数据（包含 token）
   */
  function login(userData) {
    token.value = userData.token
    user.value = userData
    
    // 持久化到 localStorage
    localStorage.setItem('token', userData.token)
    localStorage.setItem('user', JSON.stringify(userData))
  }

  /**
   * 登出
   */
  function logout() {
    token.value = ''
    user.value = null
    
    // 清除 localStorage
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }

  /**
   * 更新用户信息
   * @param {Object} userData - 用户数据
   */
  function updateUser(userData) {
    user.value = { ...user.value, ...userData }
    localStorage.setItem('user', JSON.stringify(user.value))
  }

  return {
    // 状态
    token,
    user,
    // 计算属性
    isLoggedIn,
    userId,
    username,
    avatarUrl,
    // 方法
    login,
    logout,
    updateUser
  }
})
