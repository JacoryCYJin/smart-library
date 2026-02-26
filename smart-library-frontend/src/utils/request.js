import axios from 'axios'
import { useAuthStore } from '@/stores/auth'
import router from '@/router'

// 创建 axios 实例
const service = axios.create({
  baseURL: '/api', // 使用代理，所有请求都通过 /api 前缀
  timeout: 10000 // 请求超时时间 10秒
})

// 1. 请求拦截器 (Request Interceptor)
service.interceptors.request.use(
  (config) => {
    // 从 localStorage 获取 token（也可以从 Pinia store 获取）
    const token = localStorage.getItem('token')
    if (token) {
      // 按照标准，在 Header 里携带 Authorization
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  },
)

// 2. 响应拦截器 (Response Interceptor)
service.interceptors.response.use(
  (response) => {
    // 检查响应头中是否有新的 Token (自动续期)
    const newToken = response.headers['x-new-token']
    if (newToken) {
      const authStore = useAuthStore()
      // 更新 Token
      authStore.token = newToken
      localStorage.setItem('token', newToken)
      console.log('Token 已自动续期')
    }
    
    const res = response.data

    // 后端返回 code: 0 表示成功（不是 200）
    if (res.code !== undefined && res.code !== 0 && res.code !== 200) {
      console.error('接口报错:', res.message)
      
      // token 过期或无效，跳转登录页
      if (res.code === 401 || res.code === 403) {
        const authStore = useAuthStore()
        authStore.logout()
        router.push('/login')
      }
      
      return Promise.reject(new Error(res.message || 'Error'))
    } else {
      return res
    }
  },
  (error) => {
    console.error('网络请求失败:', error)
    
    // HTTP 401/403 错误，跳转登录页
    if (error.response && (error.response.status === 401 || error.response.status === 403)) {
      const authStore = useAuthStore()
      authStore.logout()
      router.push('/login')
    }
    
    return Promise.reject(error)
  },
)

export default service
