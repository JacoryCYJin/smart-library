<template>
  <div class="min-h-screen bg-canvas">
    <!-- 页面标题和筛选 -->
    <div class="border-b border-structure">
      <div class="max-w-[1600px] mx-auto px-8 py-8">
        <h1 class="text-3xl font-bold mb-8 text-center text-ink">藏书</h1>
        
        <!-- 筛选区 -->
        <div class="max-w-4xl mx-auto space-y-6">
          <!-- 分类筛选 -->
          <div class="flex items-center justify-center gap-6">
            <span class="text-sm font-medium flex-shrink-0 text-ink-light">分类</span>
            <div class="flex flex-wrap gap-2 justify-center">
              <button
                :class="[
                  'px-4 py-2 text-sm rounded-full transition-all',
                  selectedCategory === ''
                    ? 'bg-ink text-white'
                    : 'bg-structure/50 text-ink hover:bg-structure'
                ]"
                @click="selectCategory('')"
              >
                全部
              </button>
              <button
                v-for="category in categories"
                :key="category.categoryId"
                :class="[
                  'px-4 py-2 text-sm rounded-full transition-all',
                  selectedCategory === category.categoryId
                    ? 'bg-ink text-white'
                    : 'bg-structure/50 text-ink hover:bg-structure'
                ]"
                @click="selectCategory(category.categoryId)"
              >
                {{ category.name }}
              </button>
            </div>
          </div>

          <!-- 排序 -->
          <div class="flex items-center justify-center gap-4">
            <span class="text-sm font-medium text-ink-light">排序</span>
            <div class="flex gap-2">
              <button
                v-for="sort in sortOptions"
                :key="sort.value"
                :class="[
                  'px-4 py-2 text-sm rounded-full transition-all',
                  sortBy === sort.value
                    ? 'bg-ink text-white'
                    : 'bg-structure/50 text-ink hover:bg-structure'
                ]"
                @click="changeSortBy(sort.value)"
              >
                {{ sort.label }}
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 图书内容区 -->
    <div class="max-w-[1600px] mx-auto px-8 py-12">
      <!-- 首次加载状态 -->
      <div v-if="loading && bookList.length === 0" class="flex justify-center py-20">
        <div class="w-8 h-8 border-2 border-ink border-t-transparent rounded-full animate-spin"></div>
      </div>

      <!-- 图书列表（始终显示，不受 loading 影响） -->
      <BookMasonry
        v-if="bookList.length > 0"
        :books="bookList"
        @book-click="handleBookClick"
      />

      <!-- 空状态 -->
      <div v-if="!loading && bookList.length === 0" class="flex flex-col items-center justify-center py-20 text-ink-light">
        <svg class="w-16 h-16 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
        </svg>
        <p class="text-sm">暂无图书</p>
      </div>

      <!-- 加载更多状态（无限滚动） -->
      <div v-if="bookList.length > 0" class="flex justify-center mt-12 mb-8">
        <div v-if="loading" class="flex items-center gap-2 text-sm text-ink-light">
          <div class="w-4 h-4 border-2 border-ink-light border-t-transparent rounded-full animate-spin"></div>
          <span>加载中...</span>
        </div>
        <div v-else-if="!hasMore" class="text-sm text-ink-light">
          已加载全部图书
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, onActivated } from 'vue'
import { useRouter, onBeforeRouteLeave } from 'vue-router'
import { useInfiniteScroll } from '@vueuse/core'
import { searchBooks, getCategories } from '@/api/book'
import BookMasonry from '@/components/book/BookMasonry.vue'

// 定义组件名称，用于 keep-alive
defineOptions({
  name: 'BookView'
})

const router = useRouter()

// 筛选条件
const categories = ref([])
const selectedCategory = ref('') // 改为单选
const sortBy = ref('')

// 排序选项
const sortOptions = [
  { label: '默认', value: '' },
  { label: '最热门', value: 'view_count' },
  { label: '最新', value: 'pub_date' },
  { label: '评分', value: 'star_count' }
]

// 图书列表
const bookList = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(30)
const total = ref(0)

// 保存滚动位置
const savedScrollPosition = ref(0)

// 计算是否还有更多
const hasMore = computed(() => bookList.value.length < total.value)

/**
 * 加载分类列表
 */
const loadCategories = async () => {
  try {
    const res = await getCategories()
    if (res.code === 0 && res.data) {
      // 只显示一级分类
      categories.value = res.data.filter(c => c.level === 1)
    }
  } catch (error) {
    console.error('加载分类失败:', error)
  }
}

/**
 * 加载图书列表
 */
const loadBooks = async (append = false) => {
  loading.value = true
  try {
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value
    }

    // 单选分类筛选
    if (selectedCategory.value) {
      params.categoryIds = [selectedCategory.value]
    }

    if (sortBy.value) {
      params.sortBy = sortBy.value
    }

    const res = await searchBooks(params)
    
    if (res.code === 0 && res.data) {
      if (append) {
        bookList.value = [...bookList.value, ...(res.data.list || [])]
      } else {
        bookList.value = res.data.list || []
      }
      total.value = res.data.totalCount || 0
    }
  } catch (error) {
    console.error('加载图书失败:', error)
  } finally {
    loading.value = false
  }
}

/**
 * 选择分类（单选）
 */
const selectCategory = (categoryId) => {
  selectedCategory.value = categoryId
  currentPage.value = 1
  loadBooks()
}

/**
 * 改变排序
 */
const changeSortBy = (value) => {
  sortBy.value = value
  currentPage.value = 1
  loadBooks()
}

/**
 * 加载更多（无限滚动触发）
 */
const loadMore = () => {
  if (!loading.value && hasMore.value) {
    currentPage.value++
    loadBooks(true)
  }
}

/**
 * 点击图书卡片
 */
const handleBookClick = (bookId) => {
  router.push(`/book/${bookId}`)
}

// 路由离开前保存滚动位置（最准确的时机）
onBeforeRouteLeave((to) => {
  // 只在进入详情页时保存位置
  if (to.path.startsWith('/book/')) {
    // 获取当前滚动位置（多种方式兼容）
    const scrollY = window.scrollY || window.pageYOffset || document.documentElement.scrollTop || document.body.scrollTop || 0
    savedScrollPosition.value = scrollY
  }
})

// 无限滚动
useInfiniteScroll(
  window,
  () => {
    loadMore()
  },
  {
    distance: 300, // 距离底部 300px 时触发
    interval: 500  // 节流间隔 500ms
  }
)

onMounted(() => {
  loadCategories()
  loadBooks()
})

// 组件被激活时（从详情页返回）恢复滚动位置
onActivated(() => {
  if (savedScrollPosition.value > 0) {
    // 使用多次尝试确保滚动成功（Safari 兼容性）
    const restoreScroll = () => {
      window.scrollTo({
        top: savedScrollPosition.value,
        behavior: 'instant'
      })
    }
    
    // 立即尝试
    requestAnimationFrame(() => {
      restoreScroll()
      // 再次确认（Safari 有时需要多次尝试）
      setTimeout(restoreScroll, 0)
      setTimeout(restoreScroll, 10)
      setTimeout(restoreScroll, 50)
    })
  }
})
</script>
