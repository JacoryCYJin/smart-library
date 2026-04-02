<template>
  <div class="min-h-screen bg-canvas py-12 px-4">
    <div class="max-w-7xl mx-auto">
      <!-- 页面标题 -->
      <div class="flex items-center justify-between mb-8">
        <h1 class="text-3xl font-bold text-ink">我的收藏</h1>
        <div class="text-ink-light">
          共 {{ total }} 本图书
        </div>
      </div>

      <!-- 加载状态 -->
      <div v-if="loading && favorites.length === 0" class="flex justify-center py-20">
        <a-spin :size="32" />
      </div>

      <!-- 空状态 -->
      <div v-else-if="!loading && favorites.length === 0" class="text-center py-20">
        <div class="text-6xl mb-4">📚</div>
        <p class="text-ink-light text-lg mb-4">还没有收藏任何图书</p>
        <router-link
          to="/book"
          class="inline-block px-6 py-3 bg-pop text-white rounded-lg hover:opacity-90 transition-opacity"
        >
          去逛逛
        </router-link>
      </div>

      <!-- 图书列表 -->
      <div v-else>
        <BookMasonry :books="favorites" />

        <!-- 加载更多 -->
        <div v-if="hasMore" class="flex justify-center mt-8">
          <button
            @click="loadMore"
            :disabled="loading"
            class="px-6 py-3 bg-white text-ink rounded-lg shadow-sm hover:shadow-md transition-shadow disabled:opacity-50"
          >
            {{ loading ? '加载中...' : '加载更多' }}
          </button>
        </div>

        <!-- 已加载全部 -->
        <div v-else-if="favorites.length > 0" class="text-center text-ink-light mt-8">
          已加载全部收藏
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getUserFavorites } from '@/api/favorite'
import { Message } from '@arco-design/web-vue'
import BookMasonry from '@/components/book/BookMasonry.vue'

// 收藏列表
const favorites = ref([])
const loading = ref(false)
const total = ref(0)
const hasMore = ref(true)

// 分页参数
const limit = 20
let offset = 0

/**
 * 加载收藏列表
 */
const loadFavorites = async (isLoadMore = false) => {
  if (loading.value) return

  loading.value = true
  try {
    const res = await getUserFavorites({
      limit,
      offset: isLoadMore ? offset : 0
    })

    if (res.code === 0) {
      const newBooks = res.data || []
      
      if (isLoadMore) {
        favorites.value = [...favorites.value, ...newBooks]
      } else {
        favorites.value = newBooks
        offset = 0
      }

      // 更新偏移量
      offset += newBooks.length

      // 判断是否还有更多
      hasMore.value = newBooks.length === limit

      // 更新总数（如果后端返回）
      if (res.total !== undefined) {
        total.value = res.total
      } else {
        total.value = favorites.value.length
      }
    } else {
      Message.error(res.message || '加载失败')
    }
  } catch (error) {
    console.error('加载收藏列表失败:', error)
    Message.error('加载收藏列表失败')
  } finally {
    loading.value = false
  }
}

/**
 * 加载更多
 */
const loadMore = () => {
  loadFavorites(true)
}

onMounted(() => {
  loadFavorites()
})
</script>
