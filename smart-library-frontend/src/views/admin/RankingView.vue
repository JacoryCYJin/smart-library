<template>
  <div class="space-y-6">
    <!-- 页面标题 -->
    <div>
      <h2 class="text-2xl font-semibold text-ink">排行榜统计</h2>
      <p class="mt-1 text-sm text-ink-light">资源多维度排行榜单 (Top 10)</p>
    </div>

    <!-- 排行榜网格 -->
    <div class="grid grid-cols-1 xl:grid-cols-2 gap-6">
      
      <!-- 浏览量榜单 -->
      <div class="bg-white rounded-xl p-6" style="box-shadow: 0 2px 8px rgba(16, 42, 67, 0.08)">
        <div class="flex items-center justify-between mb-4">
          <h3 class="text-lg font-semibold text-ink flex items-center gap-2">
            <svg class="w-5 h-5 text-blue-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
            </svg>
            浏览量排行榜
          </h3>
        </div>
        <a-table :columns="columns" :data="viewRanking.slice(0, 10)" :loading="loading.views" :pagination="false" size="small">
          <template #index="{ rowIndex }">
            <span :class="getIndexClass(rowIndex)">{{ rowIndex + 1 }}</span>
          </template>
          <template #value="{ record }">
            <span class="text-ink-light">{{ record.viewCount || 0 }} 次</span>
          </template>
        </a-table>
      </div>

      <!-- 收藏量榜单 -->
      <div class="bg-white rounded-xl p-6" style="box-shadow: 0 2px 8px rgba(16, 42, 67, 0.08)">
        <div class="flex items-center justify-between mb-4">
          <h3 class="text-lg font-semibold text-ink flex items-center gap-2">
            <svg class="w-5 h-5 text-pop" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" />
            </svg>
            收藏量排行榜
          </h3>
        </div>
        <a-table :columns="columns" :data="favoriteRanking.slice(0, 10)" :loading="loading.favorites" :pagination="false" size="small">
          <template #index="{ rowIndex }">
            <span :class="getIndexClass(rowIndex)">{{ rowIndex + 1 }}</span>
          </template>
          <template #value="{ record }">
            <span class="text-ink-light">{{ record.favoriteCount || 0 }} 次</span>
          </template>
        </a-table>
      </div>

      <!-- 评论数榜单 -->
      <div class="bg-white rounded-xl p-6" style="box-shadow: 0 2px 8px rgba(16, 42, 67, 0.08)">
        <div class="flex items-center justify-between mb-4">
          <h3 class="text-lg font-semibold text-ink flex items-center gap-2">
            <svg class="w-5 h-5 text-green-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z" />
            </svg>
            评论数排行榜
          </h3>
        </div>
        <a-table :columns="columns" :data="commentRanking.slice(0, 10)" :loading="loading.comments" :pagination="false" size="small">
          <template #index="{ rowIndex }">
            <span :class="getIndexClass(rowIndex)">{{ rowIndex + 1 }}</span>
          </template>
          <template #value="{ record }">
            <span class="text-ink-light">{{ record.commentCount || 0 }} 条</span>
          </template>
        </a-table>
      </div>

      <!-- 评分榜单 -->
      <div class="bg-white rounded-xl p-6" style="box-shadow: 0 2px 8px rgba(16, 42, 67, 0.08)">
        <div class="flex items-center justify-between mb-4">
          <h3 class="text-lg font-semibold text-ink flex items-center gap-2">
            <svg class="w-5 h-5 text-yellow-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11.049 2.927c.3-.921 1.603-.921 1.898 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
            </svg>
            高分推荐榜单
          </h3>
        </div>
        <a-table :columns="columns" :data="ratingRanking.slice(0, 10)" :loading="loading.ratings" :pagination="false" size="small">
          <template #index="{ rowIndex }">
            <span :class="getIndexClass(rowIndex)">{{ rowIndex + 1 }}</span>
          </template>
          <template #value="{ record }">
            <span class="text-ink-light">{{ record.score ? record.score.toFixed(1) : '0.0' }} 分</span>
          </template>
        </a-table>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Message } from '@arco-design/web-vue'
import { getViewRanking, getFavoriteRanking, getCommentRanking, getRatingRanking } from '@/api/admin'

const loading = ref({
  views: false,
  favorites: false,
  comments: false,
  ratings: false
})

const viewRanking = ref([])
const favoriteRanking = ref([])
const commentRanking = ref([])
const ratingRanking = ref([])

const columns = [
  {
    title: '排名',
    slotName: 'index',
    width: 60,
    align: 'center'
  },
  {
    title: '资源名称',
    dataIndex: 'title',
    ellipsis: true,
    tooltip: true
  },
  {
    title: '数值',
    slotName: 'value',
    width: 100,
    align: 'right'
  }
]

function getIndexClass(index) {
  if (index === 0) return 'inline-block w-6 h-6 leading-6 text-center rounded-full bg-yellow-100 text-yellow-600 font-bold'
  if (index === 1) return 'inline-block w-6 h-6 leading-6 text-center rounded-full bg-gray-200 text-gray-600 font-bold'
  if (index === 2) return 'inline-block w-6 h-6 leading-6 text-center rounded-full bg-orange-100 text-orange-600 font-bold'
  return 'inline-block w-6 h-6 leading-6 text-center text-ink-light'
}

async function loadRankings() {
  const fetchRanking = async (apiFunc, listRef, loadingKey) => {
    loading.value[loadingKey] = true
    try {
      const res = await apiFunc(10)
      if (res.code === 0) {
        listRef.value = res.data || []
      }
    } catch (error) {
      console.error(error)
      Message.error('加载排行榜数据失败')
    } finally {
      loading.value[loadingKey] = false
    }
  }

  fetchRanking(getViewRanking, viewRanking, 'views')
  fetchRanking(getFavoriteRanking, favoriteRanking, 'favorites')
  fetchRanking(getCommentRanking, commentRanking, 'comments')
  fetchRanking(getRatingRanking, ratingRanking, 'ratings')
}

onMounted(() => {
  loadRankings()
})
</script>
