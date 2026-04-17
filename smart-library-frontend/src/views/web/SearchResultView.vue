<template>
  <div class="min-h-screen bg-canvas">
    <!-- 搜索标题 -->
    <div class="border-b border-structure">
      <div class="max-w-[1600px] mx-auto px-8 py-8">
        <h1 class="text-3xl font-bold text-center text-ink">
          {{ keyword }}
        </h1>
      </div>
    </div>

    <!-- 搜索结果内容 -->
    <div class="max-w-[1600px] mx-auto px-8 py-12">
      <!-- 加载状态 -->
      <div v-if="loading && bookList.length === 0 && authorList.length === 0" class="flex justify-center py-20">
        <div class="w-8 h-8 border-2 border-ink border-t-transparent rounded-full animate-spin"></div>
      </div>

      <!-- 图书结果 -->
      <div v-if="bookList.length > 0" class="mb-16">
        <div class="flex items-center justify-between mb-6">
          <h2 class="text-2xl font-bold text-ink">相关图书</h2>
        </div>
        <BookMasonry :books="bookList" @book-click="handleBookClick" />
        
        <!-- 加载更多 -->
        <div v-if="hasMoreBooks" class="flex justify-center mt-8">
          <button
            @click="loadMoreBooks"
            :disabled="loadingBooks"
            class="px-6 py-3 text-sm rounded-full bg-ink text-white hover:opacity-90 transition-opacity disabled:opacity-50 disabled:cursor-not-allowed"
          >
            {{ loadingBooks ? '加载中...' : '加载更多图书' }}
          </button>
        </div>
        <div v-else-if="bookList.length > 0 && !hasMoreBooks" class="text-center mt-8 text-sm text-ink-light">
          已显示全部图书结果
        </div>
      </div>

      <!-- 作者结果 -->
      <div v-if="authorList.length > 0">
        <div class="flex items-center justify-between mb-6">
          <h2 class="text-2xl font-bold text-ink">相关作者</h2>
        </div>
        
        <!-- 作者网格 -->
        <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
          <button
            v-for="author in authorList"
            :key="author.authorId"
            @click="handleAuthorClick(author.authorId)"
            class="flex items-center gap-4 p-4 bg-white rounded-xl hover:shadow-lg transition-shadow text-left"
          >
            <!-- 作者头像 -->
            <div
              v-if="author.photoUrl"
              class="w-16 h-16 rounded-full overflow-hidden flex-shrink-0"
            >
              <img
                :src="author.photoUrl"
                :alt="author.name"
                class="w-full h-full object-cover"
              />
            </div>
            <div
              v-else
              class="w-16 h-16 rounded-full bg-ink text-white flex items-center justify-center text-xl font-semibold flex-shrink-0"
            >
              {{ author.name.charAt(0) }}
            </div>
            
            <!-- 作者信息 -->
            <div class="flex-1 min-w-0">
              <p class="text-base font-semibold text-ink truncate">{{ author.name }}</p>
              <p v-if="author.originalName" class="text-sm text-ink-light truncate mt-1">
                {{ author.originalName }}
              </p>
              <p v-if="author.country" class="text-sm text-ink-light truncate mt-1">
                {{ author.country }}
              </p>
            </div>
          </button>
        </div>
        
        <!-- 加载更多作者 -->
        <div v-if="hasMoreAuthors" class="flex justify-center mt-8">
          <button
            @click="loadMoreAuthors"
            :disabled="loadingAuthors"
            class="px-6 py-3 text-sm rounded-full bg-ink text-white hover:opacity-90 transition-opacity disabled:opacity-50 disabled:cursor-not-allowed"
          >
            {{ loadingAuthors ? '加载中...' : '加载更多作者' }}
          </button>
        </div>
        <div v-else-if="authorList.length > 0 && !hasMoreAuthors" class="text-center mt-8 text-sm text-ink-light">
          已显示全部作者结果
        </div>
      </div>

      <!-- 空状态 -->
      <div v-if="!loading && bookList.length === 0 && authorList.length === 0" class="flex flex-col items-center justify-center py-20 text-ink-light">
        <svg class="w-16 h-16 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
        </svg>
        <p class="text-lg font-medium">未找到"{{ keyword }}"的相关结果</p>
        <p class="text-sm mt-2">试试其他关键词吧</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { globalSearch } from '@/api/search'
import BookMasonry from '@/components/book/BookMasonry.vue'

const router = useRouter()
const route = useRoute()

// 搜索关键词
const keyword = ref('')

// 图书数据
const bookList = ref([])
const totalBooks = ref(0)
const bookPage = ref(1)
const bookPageSize = ref(20)
const loadingBooks = ref(false)

// 作者数据
const authorList = ref([])
const totalAuthors = ref(0)
const authorPage = ref(1)
const authorPageSize = ref(20)
const loadingAuthors = ref(false)

// 全局加载状态
const loading = ref(false)

// 计算是否还有更多
const hasMoreBooks = computed(() => bookList.value.length < totalBooks.value)
const hasMoreAuthors = computed(() => authorList.value.length < totalAuthors.value)

/**
 * 重置搜索状态
 */
const resetSearch = () => {
  bookList.value = []
  authorList.value = []
  totalBooks.value = 0
  totalAuthors.value = 0
  bookPage.value = 1
  authorPage.value = 1
}

/**
 * 执行搜索
 */
const performSearch = async () => {
  if (!keyword.value) return
  
  // 重置状态
  resetSearch()
  
  loading.value = true
  try {
    const res = await globalSearch({
      keyword: keyword.value,
      bookPageNum: 1,
      bookPageSize: bookPageSize.value,
      authorPageNum: 1,
      authorPageSize: authorPageSize.value
    })
    
    if (res.code === 0 && res.data) {
      bookList.value = res.data.books || []
      authorList.value = res.data.authors || []
      totalBooks.value = res.data.bookTotal || 0
      totalAuthors.value = res.data.authorTotal || 0
    }
  } catch (error) {
    console.error('搜索失败:', error)
  } finally {
    loading.value = false
  }
}

/**
 * 加载更多图书（后端分页）
 */
const loadMoreBooks = async () => {
  if (loadingBooks.value || !hasMoreBooks.value) return
  
  loadingBooks.value = true
  bookPage.value++
  
  try {
    const res = await globalSearch({
      keyword: keyword.value,
      bookPageNum: bookPage.value,
      bookPageSize: bookPageSize.value,
      authorPageNum: 1,
      authorPageSize: 0 // 不需要作者数据
    })
    
    if (res.code === 0 && res.data) {
      // 追加新数据
      bookList.value = [...bookList.value, ...(res.data.books || [])]
    }
  } catch (error) {
    console.error('加载更多图书失败:', error)
    bookPage.value-- // 失败时回退页码
  } finally {
    loadingBooks.value = false
  }
}

/**
 * 加载更多作者（后端分页）
 */
const loadMoreAuthors = async () => {
  if (loadingAuthors.value || !hasMoreAuthors.value) return
  
  loadingAuthors.value = true
  authorPage.value++
  
  try {
    const res = await globalSearch({
      keyword: keyword.value,
      bookPageNum: 1,
      bookPageSize: 0, // 不需要图书数据
      authorPageNum: authorPage.value,
      authorPageSize: authorPageSize.value
    })
    
    if (res.code === 0 && res.data) {
      // 追加新数据
      authorList.value = [...authorList.value, ...(res.data.authors || [])]
    }
  } catch (error) {
    console.error('加载更多作者失败:', error)
    authorPage.value-- // 失败时回退页码
  } finally {
    loadingAuthors.value = false
  }
}

/**
 * 点击图书
 */
const handleBookClick = (bookId) => {
  router.push(`/book/${bookId}`)
}

/**
 * 点击作者
 */
const handleAuthorClick = (authorId) => {
  router.push(`/author/${authorId}`)
}

onMounted(() => {
  keyword.value = route.query.keyword || ''
  if (keyword.value) {
    performSearch()
  }
})

// 监听路由变化，当搜索关键词改变时重新搜索
watch(() => route.query.keyword, (newKeyword) => {
  if (newKeyword && newKeyword !== keyword.value) {
    keyword.value = newKeyword
    performSearch()
  }
})
</script>
