<template>
  <div class="min-h-screen bg-canvas">
    <!-- 加载状态 -->
    <div v-if="loading" class="flex justify-center items-center py-20">
      <div class="w-8 h-8 border-2 border-ink border-t-transparent rounded-full animate-spin"></div>
    </div>

    <!-- 作者详情 -->
    <div v-else-if="author" class="pb-20">
      <!-- 双栏布局容器 -->
      <div class="flex flex-col lg:flex-row gap-12 lg:gap-0">
        <!-- 左侧栏：视觉与元数据区 (35%) - 桌面端完全贴边 -->
        <aside class="w-full lg:w-[35%] flex-shrink-0 px-8 lg:pl-0 lg:pr-0">
          <!-- 作者大图 -->
          <div class="relative overflow-hidden rounded-3xl lg:rounded-none lg:rounded-br-[3rem] bg-structure mb-6">
            <img
              v-if="author.photoUrl"
              :src="author.photoUrl"
              :alt="author.name"
              class="w-full h-auto object-cover aspect-[3/4]"
            />
            <div v-else class="w-full aspect-[3/4] flex items-center justify-center text-ink-light">
              <svg class="w-20 h-20" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="1.5"
                  d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"
                />
              </svg>
            </div>
          </div>


        </aside>

        <!-- 右侧栏：核心内容区 (65%) - 自带最大宽度和间距 -->
        <main class="flex-1 min-w-0 px-8 lg:pl-16 lg:pr-16">
          <div class="max-w-5xl">
            <!-- 顶部：作者名称区 -->
            <div class="mb-8">
              <div class="flex flex-wrap items-baseline gap-4 mb-3">
                <h1 class="text-5xl lg:text-6xl font-bold text-ink leading-tight">
                  {{ author.name }}
                </h1>
                <!-- 国籍标签 -->
                <span v-if="author.country" class="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-structure text-ink text-sm">
                  <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      stroke-width="2"
                      d="M3.055 11H5a2 2 0 012 2v1a2 2 0 002 2 2 2 0 012 2v2.945M8 3.935V5.5A2.5 2.5 0 0010.5 8h.5a2 2 0 012 2 2 2 0 104 0 2 2 0 012-2h1.064M15 20.488V18a2 2 0 012-2h3.064M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
                    />
                  </svg>
                  {{ author.country }}
                </span>
              </div>
              <p v-if="author.originalName" class="text-2xl text-ink-light">
                {{ author.originalName }}
              </p>
            </div>

            <!-- 作者简介 -->
            <div class="mb-12">
              <h2 class="text-xl font-bold text-ink mb-4 uppercase tracking-wide">
                {{ i18n.biography }}
              </h2>
              <p class="text-base text-ink-light leading-7 whitespace-pre-wrap">
                {{ author.description || i18n.noBiography }}
              </p>
            </div>

            <!-- 作品列表 -->
            <div class="border-t border-structure pt-8">
              <div class="flex items-baseline gap-3 mb-6">
                <h2 class="text-xl font-bold text-ink uppercase tracking-wide">
                  {{ i18n.worksList }}
                </h2>
                <!-- 作品数量 -->
                <span class="text-sm text-ink-light">
                  {{ author.worksCount || 0 }} {{ i18n.works }}
                </span>
              </div>

              <!-- 作品网格 -->
              <div
                v-if="author.works && author.works.length > 0"
                class="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-6"
              >
                <AuthorBookCard
                  v-for="book in author.works"
                  :key="book.resourceId"
                  :book="book"
                  @click="goToBookDetail"
                />
              </div>

              <!-- 空状态 -->
              <div
                v-else
                class="text-center py-12 text-ink-light"
              >
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
        </main>
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
import AuthorBookCard from '@/components/book/AuthorBookCard.vue'

const route = useRoute()
const router = useRouter()
const localeStore = useLocaleStore()

// 国际化文本
const i18n = computed(() => ({
  back: localeStore.currentLang === 'zh' ? '返回' : 'Back',
  biography: localeStore.currentLang === 'zh' ? '作者简介' : 'Biography',
  country: localeStore.currentLang === 'zh' ? '国籍' : 'Country',
  works: localeStore.currentLang === 'zh' ? '作品' : 'Works',
  worksList: localeStore.currentLang === 'zh' ? '作品列表' : 'Works List',
  noBiography: localeStore.currentLang === 'zh' ? '暂无作者简介' : 'No biography available',
  noWorks: localeStore.currentLang === 'zh' ? '暂无作品' : 'No works available',
  notFound: localeStore.currentLang === 'zh' ? '作者不存在或已被删除' : 'Author not found or has been deleted',
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
    Message.error('加载失败')
  } finally {
    loading.value = false
  }
}

/**
 * 跳转到书籍详情页
 */
const goToBookDetail = (bookId) => {
  router.push(`/book/${bookId}`)
}

onMounted(() => {
  loadAuthorDetail()
})
</script>
