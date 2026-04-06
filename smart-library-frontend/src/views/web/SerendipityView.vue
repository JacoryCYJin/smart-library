<template>
  <div class="min-h-screen bg-canvas py-12 px-4">
    <div class="max-w-7xl mx-auto">
      <!-- 页面标题 -->
      <div class="text-center mb-12">
        <h1 class="text-4xl font-bold text-ink mb-4">
          <span class="inline-flex items-center gap-2">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" class="h-10 w-10 text-pop" stroke-width="2">
              <path d="M12 2l1.6 4.8L18 8.4l-4.4 1.6L12 15l-1.6-5L6 8.4l4.4-1.6L12 2z" />
              <path d="M19 12l.8 2.4L22 15.2l-2.2.8L19 18l-.8-2.4L16 14.8l2.2-.8L19 12z" />
            </svg>
            偶遇好书
          </span>
        </h1>
        <p class="text-ink-light text-lg">随机发现你可能喜欢的书籍</p>
      </div>

      <!-- 加载状态 -->
      <div v-if="loading" class="flex justify-center items-center py-20">
        <a-spin size="large" />
      </div>

      <!-- 书籍网格 -->
      <div v-else-if="books.length > 0" class="space-y-8">
        <div class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-6 gap-6">
          <div
            v-for="book in books"
            :key="book.resourceId"
            @click="goToDetail(book.resourceId)"
            class="cursor-pointer group"
          >
            <!-- 封面 -->
            <div class="relative aspect-[3/4] rounded-lg overflow-hidden bg-structure mb-3 shadow-sm group-hover:shadow-md transition-shadow">
              <img
                v-if="book.coverUrl"
                :src="book.coverUrl"
                :alt="book.title"
                class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
              />
              <div v-else class="w-full h-full flex items-center justify-center text-ink-light">
                <svg class="w-12 h-12" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
                </svg>
              </div>
            </div>

            <!-- 书籍信息 -->
            <div class="space-y-1">
              <h3 class="text-sm font-medium text-ink line-clamp-2 group-hover:text-pop transition-colors">
                {{ book.title }}
              </h3>
              <p v-if="book.authors && book.authors.length > 0" class="text-xs text-ink-light line-clamp-1">
                {{ book.authors.map(a => a.name).join(' / ') }}
              </p>
              <div v-if="book.doubanScore" class="flex items-center gap-1">
                <span class="text-xs font-medium text-pop">{{ book.doubanScore }}</span>
                <svg class="w-3 h-3 text-pop" fill="currentColor" viewBox="0 0 20 20">
                  <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
                </svg>
              </div>
            </div>
          </div>
        </div>

        <!-- 换一批按钮 -->
        <div class="flex justify-center pt-8">
          <button
            @click="loadRecommendations"
            :disabled="loading"
            class="px-8 py-3 bg-ink text-white rounded-full font-medium hover:opacity-90 transition-opacity disabled:opacity-50 flex items-center gap-2"
          >
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
            </svg>
            换一批推荐
          </button>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-else class="text-center py-20">
        <p class="text-ink-light text-lg">暂无推荐内容</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getRecommendations } from '@/api/serendipity'
import { Message } from '@arco-design/web-vue'

const router = useRouter()

const books = ref([])
const loading = ref(false)

/**
 * 加载推荐书籍
 */
const loadRecommendations = async () => {
  loading.value = true
  try {
    const res = await getRecommendations(12)
    if (res.code === 0) {
      books.value = res.data
    } else {
      Message.error(res.message || '加载推荐失败')
    }
  } catch (error) {
    console.error('加载推荐失败:', error)
    Message.error('加载推荐失败')
  } finally {
    loading.value = false
  }
}

/**
 * 跳转到书籍详情
 */
const goToDetail = (resourceId) => {
  router.push(`/book/${resourceId}`)
}

onMounted(() => {
  loadRecommendations()
})
</script>
