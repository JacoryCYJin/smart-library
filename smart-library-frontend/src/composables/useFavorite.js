import { ref } from 'vue'
import { addFavorite, removeFavorite, checkFavorite } from '@/api/favorite'
import { Message } from '@arco-design/web-vue'
import { useAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'

/**
 * 收藏功能 Composable
 * @param {string} resourceId - 资源ID
 * @returns {Object} - 收藏状态和操作方法
 */
export function useFavorite(resourceId) {
  const authStore = useAuthStore()
  const router = useRouter()
  
  const isFavorited = ref(false)
  const loading = ref(false)

  /**
   * 检查收藏状态
   */
  async function checkFavoriteStatus() {
    // 直接检查 localStorage 中的 token，避免 Pinia 初始化延迟
    const token = localStorage.getItem('token')
    console.log('🔍 [checkFavoriteStatus] Token存在:', !!token)
    
    if (!token) {
      console.log('❌ [checkFavoriteStatus] 无Token，设置为未收藏')
      isFavorited.value = false
      return
    }

    try {
      console.log('📡 [checkFavoriteStatus] 调用API检查收藏状态，resourceId:', resourceId)
      const res = await checkFavorite(resourceId)
      console.log('✅ [checkFavoriteStatus] API返回:', res)
      console.log('📊 [checkFavoriteStatus] res.data:', res.data, 'type:', typeof res.data)
      
      // 确保返回的是布尔值
      const favoriteStatus = Boolean(res.data)
      console.log('🎯 [checkFavoriteStatus] 最终收藏状态:', favoriteStatus)
      isFavorited.value = favoriteStatus
    } catch (error) {
      console.error('❌ [checkFavoriteStatus] 检查收藏状态失败:', error)
      // 如果是 401 错误，说明 Token 无效，清除登录状态
      if (error.response && error.response.status === 401) {
        console.log('🚪 [checkFavoriteStatus] Token无效，执行登出')
        authStore.logout()
      }
      isFavorited.value = false
    }
  }

  /**
   * 切换收藏状态
   */
  async function toggleFavorite() {
    console.log('🔄 [toggleFavorite] 开始切换收藏状态')
    console.log('📊 [toggleFavorite] 当前状态 isFavorited:', isFavorited.value)
    
    // 检查登录状态
    if (!authStore.isLoggedIn) {
      console.log('❌ [toggleFavorite] 未登录')
      Message.warning('请先登录')
      router.push('/login')
      return
    }

    loading.value = true

    try {
      if (isFavorited.value) {
        // 取消收藏
        console.log('➖ [toggleFavorite] 执行取消收藏')
        await removeFavorite(resourceId)
        isFavorited.value = false
        Message.success('已取消收藏')
      } else {
        // 添加收藏
        console.log('➕ [toggleFavorite] 执行添加收藏')
        await addFavorite(resourceId)
        isFavorited.value = true
        Message.success('收藏成功')
      }
      console.log('✅ [toggleFavorite] 操作成功，新状态:', isFavorited.value)
    } catch (error) {
      console.error('❌ [toggleFavorite] 收藏操作失败:', error)
      console.error('📊 [toggleFavorite] 错误详情:', {
        status: error.response?.status,
        message: error.message,
        data: error.response?.data
      })
      
      // 如果是 401 错误，提示登录
      if (error.response && error.response.status === 401) {
        Message.warning('登录已过期，请重新登录')
        authStore.logout()
        router.push('/login')
      } else if (error.response && error.response.status === 400) {
        // 400 错误可能是状态不一致，重新检查状态
        console.log('⚠️ [toggleFavorite] 状态不一致，重新检查')
        Message.warning('操作失败，正在刷新状态...')
        await checkFavoriteStatus()
      } else {
        Message.error(error.message || '操作失败')
      }
    } finally {
      loading.value = false
    }
  }

  return {
    isFavorited,
    loading,
    checkFavoriteStatus,
    toggleFavorite
  }
}
