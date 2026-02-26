<template>
  <div class="min-h-screen bg-canvas">
    <!-- 加载状态 -->
    <div v-if="loading" class="flex justify-center items-center py-20">
      <div class="w-8 h-8 border-2 border-ink border-t-transparent rounded-full animate-spin"></div>
    </div>

    <!-- 作者详情 -->
    <div v-else-if="author" class="max-w-7xl mx-auto px-8 pt-10 pb-20">
      <!-- 返回按钮 -->
      <button
        @click="$router.back()"
        class="flex items-center gap-2 text-ink-light hover:text-ink transition-colors mb-12"
      >
        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="2"
            d="M15 19l-7-7 7-7"
          />
        </svg>
        <span>{{ i18n.back }}</span>
      </button>

      <!-- 作者信息卡片 -->
      <div class="bg-white rounded-2xl shadow-lg p-12 mb-12">
        <div class="flex flex-col md:flex-row gap-8 items-start">
          <!-- 作者头像 -->
          <div class="flex-shrink-0">
            <div
              class="w-32 h-32 rounded-full bg-structure flex items-center justify-center overflow-hidden"
            >
              <img
                v-if="author.photoUrl"
                :src="author.photoUrl"
                :alt="author.name"
                class="w-full h-full object-cover"
              />
              <svg
                v-else
                class="w-16 h-16 text-ink-light"
                fill="currentColor"
                viewBox="0 0 20 20"
              >
                <path
                  fill-rule="evenodd"
                  d="M10 9a3 3 0 100-6 3 3 0 000 6zm-7 9a7 7 0 1114 0H3z"
                  clip-rule="evenodd"
                />
              </svg>
            </div>
          </div>

          <!-- 作者信息 -->
          <div class="flex-1">
            <h1 class="text-4xl font-bold text-ink mb-2">{{ author.name }}</h1>
            <p v-if="author.originalName" class="text-lg text-ink-light mb-4">
              {{ author.originalName }}
            </p>
            <div class="flex flex-wrap gap-4 mb-6">
              <div v-if="author.country" class="flex items-center gap-2 text-ink-light">
                <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M3.055 11H5a2 2 0 012 2v1a2 2 0 002 2 2 2 0 012 2v2.945M8 3.935V5.5A2.5 2.5 0 0010.5 8h.5a2 2 0 012 2 2 2 0 104 0 2 2 0 012-2h1.064M15 20.488V18a2 2 0 012-2h3.064M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
                  />
                </svg>
                <span>{{ author.country }}</span>
              </div>
              <div class="flex items-center gap-2 text-ink-light">
                <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253"
                  />
                </svg>
                <span>{{ author.worksCount || 0 }} {{ i18n.works }}</span>
              </div>
            </div>
            <p v-if="author.description" class="text-base text-ink-light leading-relaxed">
              {{ author.description }}
            </p>
          </div>
        </div>
      </div>

      <!-- 作品列表 -->
      <div>
        <h2 class="text-2xl font-bold text-ink mb-6">{{ i18n.authorWorks }}</h2>
        <div v-if="author.works && author.works.length > 0">
          <BookMasonry :books="author.works" :column-count="4" :gap="24" @book-click="goToBook" />
        </div>
        <div v-else class="text-center py-12 text-ink-light">
          <svg
            class="w-12 h-12 mx-auto mb-4 opacity-50"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="1.5"
              d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253"
            />
          </svg>
          <p class="text-sm">{{ i18n.noWorks }}</p>
        </div>
      </div>
    </div>

    <!-- 错误状态 -->
    <div v-else class="flex flex-col items-center justify-center py-20 text-ink-light">
      <svg class="w-16 h-16 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path
          stroke-linecap="round"
          stroke-linejoin="round"
          stroke-width="1.5"
          d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
        />
      </svg>
      <p>{{ i18n.notFound }}</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getAuthorDetail } from '@/api/author'
import { Message } from '@arco-design/web-vue'
import { useLocaleStore } from '@/stores/locale'
import BookMasonry from '@/components/book/BookMasonry.vue'

const route = useRoute()
const router = useRouter()
const localeStore = useLocaleStore()

// 国际化文本
const i18n = computed(() => ({
  back: localeStore.currentLang === 'zh' ? '返回' : 'Back',
  works: localeStore.currentLang === 'zh' ? '部作品' : 'Works',
  authorWorks: localeStore.currentLang === 'zh' ? '作品列表' : 'Works',
  noWorks: localeStore.currentLang === 'zh' ? '暂无作品' : 'No works yet',
  notFound:
    localeStore.currentLang === 'zh'
      ? '作者不存在或已被删除'
      : 'Author not found or has been deleted'
}))

// 作者信息
const author = ref(null)
const loading = ref(true)

/**
 * 加载作者详情
 */
const loadAuthorDetail = async () => {
  loading.value = true
  try {
    const res = await getAuthorDetail(route.params.authorId)
    if (res.code === 0 && res.data) {
      author.value = res.data
    }
  } catch (error) {
    console.error('加载作者详情失败:', error)
    Message.error(localeStore.currentLang === 'zh' ? '加载失败' : 'Failed to load')
  } finally {
    loading.value = false
  }
}

/**
 * 跳转到书籍详情页
 */
const goToBook = (bookId) => {
  router.push(`/book/${bookId}`)
}

onMounted(() => {
  loadAuthorDetail()
})
</script>
