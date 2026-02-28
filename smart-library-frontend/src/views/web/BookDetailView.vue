<template>
  <div class="min-h-screen bg-canvas">
    <!-- 加载状态 -->
    <div v-if="loading" class="flex justify-center items-center py-20">
      <div class="w-8 h-8 border-2 border-ink border-t-transparent rounded-full animate-spin"></div>
    </div>

    <!-- 书籍详情 -->
    <div v-else-if="book" class="max-w-7xl mx-auto px-8 pt-10">
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

      <!-- 布局容器 -->
      <div class="relative">
        <!-- 上半部分：固定高度容器 -->
        <div class="relative h-auto lg:h-[420px] z-10 flex items-center justify-center">
          <!-- 内容容器：紧凑布局 -->
          <div class="flex flex-col lg:flex-row items-center gap-20 px-8 py-8 lg:py-0">
            <!-- 左侧：封面（固定高度，宽度自适应） -->
            <div class="flex-shrink-0">
              <img
                :src="book.coverUrl || 'https://via.placeholder.com/280x420?text=No+Cover'"
                :alt="book.title"
                class="h-[420px] w-auto object-contain rounded-lg"
                style="filter: drop-shadow(-20px 20px 5px rgba(16, 42, 67, 0.5))"
              />
            </div>

            <!-- 右侧：书籍信息 -->
            <div
              class="w-full lg:w-[500px] flex flex-col justify-between h-full lg:h-[420px] py-8 lg:py-0"
            >
              <!-- 上部：书籍信息 -->
              <div class="flex flex-col gap-4">
                <!-- 书名 -->
                <h1 class="text-5xl font-bold text-ink leading-tight">{{ book.title }}</h1>

                <!-- 第一作者（可点击） -->
                <div v-if="book.authorName" class="flex flex-wrap gap-2 mb-2">
                  <button
                    type="button"
                    @click.prevent="goToAuthorBySort(1)"
                    class="text-3xl font-semibold text-ink-light hover:text-pop transition-colors cursor-pointer bg-transparent border-0 p-0 outline-none"
                  >
                    {{ getAuthorNames()[0] }}
                  </button>
                </div>

                <!-- 分类标签 -->
                <div
                  v-if="book.categoryNames && book.categoryNames.length"
                  class="flex flex-wrap gap-2"
                >
                  <span
                    v-for="(category, index) in book.categoryNames"
                    :key="index"
                    class="px-3 py-1.5 text-base bg-structure text-ink rounded-full"
                  >
                    {{ category }}
                  </span>
                </div>

                <!-- Tag 标签 -->
                <div v-if="book.tags && book.tags.length" class="flex flex-wrap gap-2">
                  <span
                    v-for="tag in book.tags"
                    :key="tag.tagId"
                    class="px-3 py-1.5 text-base border border-structure text-ink-light rounded-full"
                  >
                    #{{ tag.name }}
                  </span>
                </div>
              </div>

              <!-- 下部：Action Bar -->
              <div
                class="flex items-center justify-between gap-4 mt-3 pt-8 pb-7 border-b border-structure"
              >
                <!-- 开始阅读按钮 -->
                <button
                  @click="handleRead"
                  :disabled="!hasFiles"
                  class="flex items-center gap-3 px-8 py-4 bg-ink text-white rounded-full font-semibold text-base transition-all duration-300 hover:bg-pop hover:translate-x-1 disabled:opacity-50 disabled:cursor-not-allowed disabled:hover:bg-ink disabled:hover:translate-x-0"
                >
                  <span>{{ i18n.startReading }}</span>
                  <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      stroke-width="2"
                      d="M13 7l5 5m0 0l-5 5m5-5H6"
                    />
                  </svg>
                </button>

                <!-- 图标操作组 -->
                <div class="flex items-center gap-4">
                  <!-- 收藏按钮 -->
                  <button
                    @click="toggleFavorite"
                    :disabled="favoriteLoading"
                    class="flex items-center justify-center w-12 h-12 border rounded-full transition-all duration-300"
                    :class="
                      isFavorited
                        ? 'border-pop bg-pop text-white hover:bg-[#b83838]'
                        : 'border-structure text-ink-light bg-white hover:border-ink hover:text-ink hover:bg-[rgba(16,42,67,0.05)]'
                    "
                    :title="isFavorited ? '取消收藏' : '收藏'"
                  >
                    <svg
                      class="w-6 h-6 transition-transform duration-200"
                      :class="{ 'scale-110': isFavorited }"
                      :fill="isFavorited ? 'currentColor' : 'none'"
                      stroke="currentColor"
                      viewBox="0 0 24 24"
                    >
                      <path
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        stroke-width="2"
                        d="M5 5a2 2 0 012-2h10a2 2 0 012 2v16l-7-3.5L5 21V5z"
                      />
                    </svg>
                  </button>

                  <button
                    class="flex items-center justify-center w-12 h-12 border border-structure rounded-full text-ink-light bg-white transition-all duration-300 hover:border-ink hover:text-ink hover:bg-[rgba(16,42,67,0.05)]"
                    title="分享"
                  >
                    <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        stroke-width="2"
                        d="M8.684 13.342C8.886 12.938 9 12.482 9 12c0-.482-.114-.938-.316-1.342m0 2.684a3 3 0 110-2.684m0 2.684l6.632 3.316m-6.632-6l6.632-3.316m0 0a3 3 0 105.367-2.684 3 3 0 00-5.367 2.684zm0 9.316a3 3 0 105.368 2.684 3 3 0 00-5.368-2.684z"
                      />
                    </svg>
                  </button>

                  <!-- 下载按钮 (支持多文件) -->
                  <a-dropdown v-if="files.length > 1" trigger="click" position="bottom">
                    <button
                      class="flex items-center justify-center w-12 h-12 border border-structure rounded-full text-ink-light bg-white transition-all duration-300 hover:border-ink hover:text-ink hover:bg-[rgba(16,42,67,0.05)]"
                      title="下载"
                    >
                      <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path
                          stroke-linecap="round"
                          stroke-linejoin="round"
                          stroke-width="2"
                          d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4"
                        />
                      </svg>
                    </button>
                    <template #content>
                      <a-doption v-for="file in files" :key="file.id" @click="handleDownload(file)">
                        <template #icon>
                          <span
                            class="text-xs font-bold px-1 py-0.5 rounded bg-gray-100 text-gray-600 border border-gray-200"
                            >{{ file.fileTypeDesc || 'FILE' }}</span
                          >
                        </template>
                        下载 {{ file.fileTypeDesc || '文件' }} ({{ formatFileSize(file.fileSize) }})
                      </a-doption>
                    </template>
                  </a-dropdown>

                  <button
                    v-else
                    @click="handleDownload(files[0])"
                    :disabled="!hasFiles"
                    class="flex items-center justify-center w-12 h-12 border border-structure rounded-full text-ink-light bg-white transition-all duration-300 hover:border-ink hover:text-ink hover:bg-[rgba(16,42,67,0.05)] disabled:opacity-50 disabled:cursor-not-allowed disabled:hover:border-structure disabled:hover:text-ink-light disabled:hover:bg-white"
                    title="下载"
                  >
                    <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        stroke-width="2"
                        d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4"
                      />
                    </svg>
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 下半部分：白色卡片（负 margin 向上覆盖） -->
        <div
          class="relative bg-white rounded-t-3xl shadow-[0_8px_30px_rgba(16,42,67,0.12)] mt-0 lg:-mt-[110px] pt-12 lg:pt-40 px-12 z-[5]"
        >
          <!-- 左右分栏：左侧 Description + 右侧书籍信息 -->
          <div class="grid grid-cols-1 lg:grid-cols-2 gap-8 lg:gap-16 mb-16">
            <!-- 左侧：Description -->
            <div>
              <h2 class="text-2xl font-bold text-ink mb-6">{{ i18n.description }}</h2>
              <p class="text-base text-ink-light leading-7">
                {{ book.summary || i18n.noDescription }}
              </p>
            </div>

            <!-- 右侧：书籍信息区域 -->
            <div class="flex flex-col gap-8">
              <!-- 评分（重点突出） -->
              <div v-if="book.sourceScore" class="flex items-center gap-3">
                <span class="text-6xl font-bold text-pop">{{ book.sourceScore }}</span>
                <svg class="w-12 h-12 text-yellow-500" fill="currentColor" viewBox="0 0 20 20">
                  <path
                    d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z"
                  />
                </svg>
              </div>

              <!-- 其他信息（两列布局） -->
              <div class="grid grid-cols-2 gap-x-8 gap-y-6">
                <!-- 其他作者（如果有多位作者） -->
                <div v-if="getAuthorNames().length > 1" class="col-span-2 flex flex-col gap-2">
                  <h3 class="text-sm font-semibold text-ink">{{ i18n.otherAuthors }}</h3>
                  <div class="flex flex-wrap gap-2">
                    <button
                      v-for="(authorName, index) in getAuthorNames().slice(1)"
                      :key="index"
                      type="button"
                      @click.prevent="goToAuthorBySort(index + 2)"
                      class="text-base text-ink-light hover:text-pop transition-colors cursor-pointer bg-transparent border-0 p-0 outline-none underline"
                    >
                      {{ authorName }}<span v-if="index < getAuthorNames().length - 2">, </span>
                    </button>
                  </div>
                </div>

                <!-- 译者（如果有） -->
                <div v-if="book.translatorName" class="flex flex-col gap-2">
                  <h3 class="text-sm font-semibold text-ink">{{ i18n.translator }}</h3>
                  <p class="text-base text-ink-light">{{ book.translatorName }}</p>
                </div>

                <!-- 出版社 -->
                <div v-if="book.publisher" class="flex flex-col gap-2">
                  <h3 class="text-sm font-semibold text-ink">{{ i18n.publisher }}</h3>
                  <p class="text-base text-ink-light">{{ book.publisher }}</p>
                </div>

                <!-- 出版日期 -->
                <div v-if="book.pubDate" class="flex flex-col gap-2">
                  <h3 class="text-sm font-semibold text-ink">{{ i18n.pubDate }}</h3>
                  <p class="text-base text-ink-light">{{ book.pubDate }}</p>
                </div>

                <!-- ISBN -->
                <div v-if="book.isbn" class="flex flex-col gap-2">
                  <h3 class="text-sm font-semibold text-ink">{{ i18n.isbn }}</h3>
                  <p class="text-base text-ink-light">{{ book.isbn }}</p>
                </div>

                <!-- 页数 -->
                <div v-if="book.pageCount" class="flex flex-col gap-2">
                  <h3 class="text-sm font-semibold text-ink">{{ i18n.pageCount }}</h3>
                  <p class="text-base text-ink-light">{{ book.pageCount }}</p>
                </div>

                <!-- 价格 -->
                <div v-if="book.price" class="flex flex-col gap-2">
                  <h3 class="text-sm font-semibold text-ink">{{ i18n.price }}</h3>
                  <p class="text-base text-ink-light">¥{{ book.price }}</p>
                </div>
              </div>
            </div>
          </div>

          <!-- 读者评论区 -->
          <div class="border-t border-structure pt-12">
            <h2 class="text-2xl font-bold text-ink mb-6">{{ i18n.comments }}</h2>

            <!-- 发表评论表单 -->
            <div class="max-w-3xl mb-12 pb-8 border-b border-structure">
              <h3 class="text-lg font-semibold text-ink mb-4">{{ i18n.postComment }}</h3>
              <form @submit.prevent="submitComment" class="flex flex-col gap-4">
                <!-- 评分 -->
                <div class="flex flex-col gap-2">
                  <label class="text-sm font-medium text-ink-light">{{ i18n.rating }}</label>
                  <div class="flex gap-2">
                    <button
                      v-for="star in 5"
                      :key="star"
                      type="button"
                      @click="commentForm.score = star"
                      class="outline-none transition-transform duration-200 hover:scale-110"
                    >
                      <svg
                        class="w-6 h-6 transition-colors"
                        :class="star <= commentForm.score ? 'text-yellow-500' : 'text-structure'"
                        fill="currentColor"
                        viewBox="0 0 20 20"
                      >
                        <path
                          d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z"
                        />
                      </svg>
                    </button>
                  </div>
                </div>

                <!-- 评论内容 -->
                <div class="flex flex-col gap-2">
                  <label class="text-sm font-medium text-ink-light">{{
                    i18n.commentContent
                  }}</label>
                  <textarea
                    v-model="commentForm.content"
                    rows="3"
                    :placeholder="i18n.commentPlaceholder"
                    class="w-full px-4 py-3 border border-structure rounded-lg text-sm text-ink bg-white transition-all duration-300 focus:outline-none focus:ring-2 focus:ring-ink"
                  ></textarea>
                </div>

                <!-- 提交按钮 -->
                <button
                  type="submit"
                  :disabled="submitting || !commentForm.content.trim()"
                  class="px-6 py-2 bg-ink text-white rounded-full text-sm font-medium transition-all duration-300 self-start hover:bg-pop disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  {{ submitting ? i18n.submitting : i18n.submit }}
                </button>
              </form>
            </div>

            <!-- 评论列表 - 瀑布流布局 -->
            <CommentMasonry :comments="comments" :column-count="3" :gap="16" />

            <!-- 空状态 -->
            <div
              v-if="!loadingComments && comments.length === 0"
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
                  d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z"
                />
              </svg>
              <p class="text-sm">{{ i18n.noComments }}</p>
            </div>

            <!-- 加载中提示 -->
            <div v-if="loadingComments && comments.length > 0" class="text-center py-8">
              <div class="inline-flex items-center gap-2 text-ink-light">
                <div
                  class="w-4 h-4 border-2 border-ink-light border-t-transparent rounded-full animate-spin"
                ></div>
                <span class="text-sm">{{ i18n.loading }}</span>
              </div>
            </div>

            <!-- 全部加载完成提示 -->
            <div v-if="allLoaded && comments.length > 0" class="text-center py-8">
              <div class="inline-flex items-center gap-2 text-ink-light">
                <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
                  <path
                    fill-rule="evenodd"
                    d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z"
                    clip-rule="evenodd"
                  />
                </svg>
                <span class="text-sm">{{ i18n.allLoaded }}</span>
              </div>
            </div>
          </div>
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
import { ref, onMounted, computed, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getBookDetail } from '@/api/book'
import { getComments, createComment } from '@/api/comment'
import { getAuthorId } from '@/api/author'
import { Message } from '@arco-design/web-vue'
import { useLocaleStore } from '@/stores/locale'
import { useAuthStore } from '@/stores/auth'
import { useFavorite } from '@/composables/useFavorite'
import CommentMasonry from '@/components/comment/CommentMasonry.vue'

const route = useRoute()
const router = useRouter()
const localeStore = useLocaleStore()
const authStore = useAuthStore()

// 收藏功能
const {
  isFavorited,
  loading: favoriteLoading,
  checkFavoriteStatus,
  toggleFavorite,
} = useFavorite(route.params.bookId)

// 国际化文本
const i18n = computed(() => ({
  description: localeStore.currentLang === 'zh' ? '简介' : 'Description',
  author: localeStore.currentLang === 'zh' ? '作者' : 'Author',
  authors: localeStore.currentLang === 'zh' ? '作者' : 'Authors',
  translator: localeStore.currentLang === 'zh' ? '译者' : 'Translator',
  otherAuthors: localeStore.currentLang === 'zh' ? '其他作者' : 'Other Authors',
  publisher: localeStore.currentLang === 'zh' ? '出版社' : 'Publisher',
  pubDate: localeStore.currentLang === 'zh' ? '出版日期' : 'Publication Date',
  isbn: localeStore.currentLang === 'zh' ? 'ISBN' : 'ISBN',
  pageCount: localeStore.currentLang === 'zh' ? '页数' : 'Pages',
  price: localeStore.currentLang === 'zh' ? '价格' : 'Price',
  sourceScore: localeStore.currentLang === 'zh' ? '评分' : 'Rating',
  comments: localeStore.currentLang === 'zh' ? '读者评论' : 'Reader Comments',
  postComment: localeStore.currentLang === 'zh' ? '发表你的评论' : 'Post Your Comment',
  rating: localeStore.currentLang === 'zh' ? '评分' : 'Rating',
  commentContent: localeStore.currentLang === 'zh' ? '评论内容' : 'Comment',
  commentPlaceholder:
    localeStore.currentLang === 'zh' ? '分享你的阅读感受...' : 'Share your reading experience...',
  submit: localeStore.currentLang === 'zh' ? '发表评论' : 'Submit',
  submitting: localeStore.currentLang === 'zh' ? '提交中...' : 'Submitting...',
  loadMore: localeStore.currentLang === 'zh' ? '查看更多评论' : 'Load More Comments',
  loading: localeStore.currentLang === 'zh' ? '加载中...' : 'Loading...',
  allLoaded: localeStore.currentLang === 'zh' ? '已加载全部评论' : 'All comments loaded',
  noComments:
    localeStore.currentLang === 'zh'
      ? '暂无评论，快来发表第一条评论吧！'
      : 'No comments yet. Be the first to comment!',
  noDescription: localeStore.currentLang === 'zh' ? '暂无详细描述' : 'No description available',
  unknown: localeStore.currentLang === 'zh' ? '未知' : 'Unknown',
  startReading: localeStore.currentLang === 'zh' ? '开始阅读' : 'Start Reading',
  back: localeStore.currentLang === 'zh' ? '返回' : 'Back',
  notFound:
    localeStore.currentLang === 'zh'
      ? '书籍不存在或已被删除'
      : 'Book not found or has been deleted',
}))

// 书籍信息
const book = ref(null)
const loading = ref(true)

// 文件处理
const files = computed(() => book.value?.files || [])
const hasFiles = computed(() => files.value.length > 0)

// 格式化文件大小
const formatFileSize = (bytes) => {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB', 'TB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

// 处理阅读
const handleRead = () => {
  if (!hasFiles.value) return

  // 优先寻找 PDF 或 EPUB
  const readableFile =
    files.value.find(
      (f) =>
        f.fileType === 1 || // PDF
        f.fileType === 2 || // EPUB
        f.fileTypeDesc === 'PDF' ||
        f.fileTypeDesc === 'EPUB',
    ) || files.value[0]

  if (readableFile && readableFile.fileUrl) {
    window.open(readableFile.fileUrl, '_blank')
  } else {
    Message.warning(
      localeStore.currentLang === 'zh'
        ? '暂无在线阅读资源'
        : 'No online reading resource available',
    )
  }
}

// 处理下载
const handleDownload = (file) => {
  if (!file || !file.fileUrl) return
  // 创建临时链接下载
  const link = document.createElement('a')
  link.href = file.fileUrl
  link.setAttribute('download', '') // 尝试触发下载
  link.target = '_blank'
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
}

// 评论列表
const comments = ref([])
const loadingComments = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const hasMore = ref(false)
const allLoaded = ref(false) // 是否已加载全部评论

// 评论表单
const commentForm = ref({
  score: 5,
  content: '',
})
const submitting = ref(false)

/**
 * 加载书籍详情
 */
const loadBookDetail = async () => {
  loading.value = true
  try {
    const res = await getBookDetail(route.params.bookId)
    if (res.code === 0 && res.data) {
      book.value = res.data
    }
  } catch (error) {
    console.error('加载书籍详情失败:', error)
    Message.error('加载失败')
  } finally {
    loading.value = false
  }
}

/**
 * 加载评论列表
 */
const loadComments = async (append = false) => {
  if (!book.value || loadingComments.value || allLoaded.value) return

  loadingComments.value = true
  try {
    const res = await getComments({
      resourceId: book.value.resourceId,
      pageNum: currentPage.value,
      pageSize: pageSize.value,
    })

    if (res.code === 0 && res.data) {
      if (append) {
        comments.value = [...comments.value, ...(res.data.list || [])]
      } else {
        comments.value = res.data.list || []
      }
      total.value = res.data.totalCount || 0
      hasMore.value = comments.value.length < total.value
      allLoaded.value = !hasMore.value
    }
  } catch (error) {
    console.error('加载评论失败:', error)
  } finally {
    loadingComments.value = false
  }
}

/**
 * 无限滚动处理
 */
const handleScroll = () => {
  if (loadingComments.value || allLoaded.value) return

  const scrollTop = window.pageYOffset || document.documentElement.scrollTop
  const windowHeight = window.innerHeight
  const documentHeight = document.documentElement.scrollHeight

  // 距离底部 300px 时触发加载
  if (scrollTop + windowHeight >= documentHeight - 300) {
    currentPage.value++
    loadComments(true)
  }
}

/**
 * 提交评论
 */
const submitComment = async () => {
  if (!commentForm.value.content.trim()) {
    Message.warning(
      localeStore.currentLang === 'zh' ? '请输入评论内容' : 'Please enter comment content',
    )
    return
  }

  submitting.value = true
  try {
    // 检查是否登录
    if (!authStore.isLoggedIn) {
      Message.warning(localeStore.currentLang === 'zh' ? '请先登录' : 'Please login first')
      router.push({
        name: 'login',
        query: { redirect: route.fullPath },
      })
      return
    }

    const res = await createComment({
      resourceId: book.value.resourceId,
      content: commentForm.value.content,
      score: commentForm.value.score,
    })

    if (res.code === 0) {
      Message.success(
        localeStore.currentLang === 'zh' ? '评论发表成功' : 'Comment posted successfully',
      )
      // 重置表单
      commentForm.value = {
        score: 5,
        content: '',
      }
      // 重新加载评论
      currentPage.value = 1
      await loadComments()
      // 更新评论数
      if (book.value) {
        book.value.commentCount = (book.value.commentCount || 0) + 1
      }
    }
  } catch (error) {
    console.error('发表评论失败:', error)

    // 如果是 401 错误，提示登录
    if (error.response && error.response.status === 401) {
      Message.warning(
        localeStore.currentLang === 'zh'
          ? '登录已过期，请重新登录'
          : 'Login expired, please login again',
      )
      router.push({
        name: 'login',
        query: { redirect: route.fullPath },
      })
    } else {
      Message.error(
        localeStore.currentLang === 'zh' ? '发表失败，请稍后重试' : 'Failed to post comment',
      )
    }
  } finally {
    submitting.value = false
  }
}

/**
 * 分割作者名称
 */
const getAuthorNames = () => {
  if (!book.value?.authorName) return []
  return book.value.authorName.split(',').map((name) => name.trim())
}

/**
 * 根据排序跳转到作者详情页
 */
const goToAuthorBySort = async (sort) => {
  if (!book.value?.resourceId) {
    return
  }
  try {
    const res = await getAuthorId(book.value.resourceId, sort)
    if (res.data) {
      router.push(`/author/${res.data}`)
    }
  } catch (error) {
    console.error('查询作者ID失败:', error)
    Message.error('无法跳转到作者详情页')
  }
}

onMounted(async () => {
  await loadBookDetail()
  if (book.value) {
    await loadComments()
    // 检查收藏状态
    await checkFavoriteStatus()
  }

  // 添加滚动监听
  window.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
  // 移除滚动监听
  window.removeEventListener('scroll', handleScroll)
})
</script>
