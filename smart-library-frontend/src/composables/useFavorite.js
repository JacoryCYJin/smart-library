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
    if (!authStore.isLoggedIn) {
      isFavorited.value = false
      return
    }

    try {
      const res = await checkFavorite(resourceId)
      isFavorited.value = res.data || false
    } catch (error) {
      console.error('检查收藏状态失败:', error)
      isFavorited.value = false
    }
  }

  /**
   * 切换收藏状态
   */
  async function toggleFavorite() {
    // 检查登录状态
    if (!authStore.isLoggedIn) {
      Message.warning('请先登录')
      router.push('/login')
      return
    }

    loading.value = true

    try {
      if (isFavorited.value) {
        // 取消收藏
        await removeFavorite(resourceId)
        isFavorited.value = false
        Message.success('已取消收藏')
      } else {
        // 添加收藏
        await addFavorite(resourceId)
        isFavorited.value = true
        Message.success('收藏成功')
      }
    } catch (error) {
      console.error('收藏操作失败:', error)
      
      // 如果是 401 错误，提示登录
      if (error.response && error.response.status === 401) {
        Message.warning('登录已过期，请重新登录')
        authStore.logout()
        router.push('/login')
      } else {
        Message.error(error.response?.data?.message || '操作失败')
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
