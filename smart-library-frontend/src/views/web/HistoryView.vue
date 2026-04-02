<template>
  <div class="min-h-screen bg-canvas py-12 px-4">
    <div class="max-w-7xl mx-auto">
      <!-- 页面标题 -->
      <div class="flex items-center justify-between mb-8">
        <h1 class="text-3xl font-bold text-ink">浏览历史</h1>
        <div class="text-ink-light">
          共 {{ totalCount }} 本图书
        </div>
      </div>

      <!-- 加载状态 -->
      <div v-if="loading && groupedHistory.length === 0" class="flex justify-center py-20">
        <a-spin :size="32" />
      </div>

      <!-- 空状态 -->
      <div v-else-if="!loading && groupedHistory.length === 0" class="text-center py-20">
        <div class="text-6xl mb-4">🕐</div>
        <p class="text-ink-light text-lg mb-4">还没有浏览记录</p>
        <router-link
          to="/book"
          class="inline-block px-6 py-3 bg-pop text-white rounded-lg hover:opacity-90 transition-opacity"
        >
          去逛逛
        </router-link>
      </div>

      <!-- 按日期分组的图书列表 -->
      <div v-else>
        <div v-for="group in groupedHistory" :key="group.date" class="mb-12">
          <!-- 日期标题 -->
          <div class="flex items-center mb-6">
            <h2 class="text-xl font-semibold text-ink">{{ group.label }}</h2>
            <div class="ml-3 text-sm text-ink-light">{{ group.books.length }} 本</div>
          </div>

          <!-- 该日期的图书列表 -->
          <BookMasonry :books="group.books" />
        </div>

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
        <div v-else-if="groupedHistory.length > 0" class="text-center text-ink-light mt-8">
          已加载全部历史
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getBrowseHistory } from '@/api/history'
import { Message } from '@arco-design/web-vue'
import BookMasonry from '@/components/book/BookMasonry.vue'

// 浏览历史列表（原始数据）
const history = ref([])
const loading = ref(false)
const totalCount = ref(0)
const pageCount = ref(0)
const hasMore = ref(true)

// 分页参数
const pageSize = 20
let currentPage = 1

/**
 * 获取日期分组标签
 */
const getDateLabel = (dateStr) => {
  const date = new Date(dateStr)
  const today = new Date()
  const yesterday = new Date(today)
  yesterday.setDate(yesterday.getDate() - 1)
  
  // 重置时间为 00:00:00 以便比较日期
  const resetTime = (d) => {
    d.setHours(0, 0, 0, 0)
    return d
  }
  
  const dateOnly = resetTime(new Date(date))
  const todayOnly = resetTime(new Date(today))
  const yesterdayOnly = resetTime(new Date(yesterday))
  
  // 今天
  if (dateOnly.getTime() === todayOnly.getTime()) {
    return '今天'
  }
  
  // 昨天
  if (dateOnly.getTime() === yesterdayOnly.getTime()) {
    return '昨天'
  }
  
  // 本周（最近7天）
  const weekAgo = new Date(today)
  weekAgo.setDate(weekAgo.getDate() - 7)
  if (dateOnly >= resetTime(weekAgo)) {
    return '本周'
  }
  
  // 本月
  if (dateOnly.getMonth() === todayOnly.getMonth() && 
      dateOnly.getFullYear() === todayOnly.getFullYear()) {
    return '本月'
  }
  
  // 更早（显示年月）
  const year = dateOnly.getFullYear()
  const month = dateOnly.getMonth() + 1
  return `${year}年${month}月`
}

/**
 * 按日期分组历史记录
 */
const groupedHistory = computed(() => {
  if (history.value.length === 0) return []
  
  const groups = {}
  
  history.value.forEach(book => {
    // 假设后端返回的数据中有 mtime 字段
    const dateLabel = getDateLabel(book.mtime || book.ctime)
    
    if (!groups[dateLabel]) {
      groups[dateLabel] = {
        date: dateLabel,
        label: dateLabel,
        books: []
      }
    }
    
    groups[dateLabel].books.push(book)
  })
  
  // 按日期顺序排序（今天、昨天、本周、本月、更早）
  const order = ['今天', '昨天', '本周', '本月']
  const sortedGroups = Object.values(groups).sort((a, b) => {
    const aIndex = order.indexOf(a.date)
    const bIndex = order.indexOf(b.date)
    
    // 如果都在预定义顺序中
    if (aIndex !== -1 && bIndex !== -1) {
      return aIndex - bIndex
    }
    
    // 如果 a 在预定义顺序中，b 不在
    if (aIndex !== -1) return -1
    
    // 如果 b 在预定义顺序中，a 不在
    if (bIndex !== -1) return 1
    
    // 都不在预定义顺序中，按年月降序排序
    return b.date.localeCompare(a.date)
  })
  
  return sortedGroups
})

/**
 * 加载浏览历史
 */
const loadHistory = async (isLoadMore = false) => {
  if (loading.value) return

  loading.value = true
  try {
    const res = await getBrowseHistory({
      pageNum: isLoadMore ? currentPage : 1,
      pageSize
    })

    if (res.code === 0) {
      const pageData = res.data
      const newBooks = pageData.list || []
      
      if (isLoadMore) {
        history.value = [...history.value, ...newBooks]
      } else {
        history.value = newBooks
        currentPage = 1
      }

      // 更新分页信息
      totalCount.value = pageData.totalCount || 0
      pageCount.value = pageData.pageCount || 0

      // 判断是否还有更多
      hasMore.value = currentPage < pageCount.value
    } else {
      Message.error(res.message || '加载失败')
    }
  } catch (error) {
    console.error('加载浏览历史失败:', error)
    Message.error('加载浏览历史失败')
  } finally {
    loading.value = false
  }
}

/**
 * 加载更多
 */
const loadMore = () => {
  currentPage++
  loadHistory(true)
}

onMounted(() => {
  loadHistory()
})
</script>
