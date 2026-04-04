<template>
  <div class="space-y-6">
    <!-- 页面标题 -->
    <div>
      <h2 class="text-2xl font-semibold text-ink">数据统计</h2>
      <p class="mt-1 text-sm text-ink-light">系统核心指标概览</p>
    </div>

    <!-- 核心指标卡片 -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
      <div
        v-for="stat in stats"
        :key="stat.label"
        class="bg-white rounded-xl p-6 transition-all duration-300 hover:shadow-lg"
        style="box-shadow: 0 2px 8px rgba(16, 42, 67, 0.08)"
      >
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm text-ink-light">{{ stat.label }}</p>
            <p class="mt-2 text-3xl font-semibold text-ink">
              {{ loading ? '-' : stat.value }}
            </p>
          </div>
          <div
            class="w-12 h-12 rounded-lg flex items-center justify-center"
            :class="[stat.bgClass, stat.colorClass]"
          >
            <component :is="stat.icon" class="w-6 h-6" />
          </div>
        </div>
      </div>
    </div>

    <!-- 占位：后续添加图表 -->
    <div class="bg-white rounded-xl p-6" style="box-shadow: 0 2px 8px rgba(16, 42, 67, 0.08)">
      <h3 class="text-lg font-semibold text-ink mb-4">趋势分析</h3>
      <div class="h-64 flex items-center justify-center text-ink-light">
        图表功能开发中...
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, h } from 'vue'
import { Message } from '@arco-design/web-vue'
import { getStats } from '@/api/admin'

// 状态
const loading = ref(true)
const stats = ref([
  {
    label: '资源总数',
    value: 0,
    colorClass: 'text-ink', // 使用 Tailwind 类名
    bgClass: 'bg-ink/10', // 背景色
    icon: () => h('svg', { class: 'w-6 h-6', fill: 'none', stroke: 'currentColor', viewBox: '0 0 24 24' }, [
      h('path', { 'stroke-linecap': 'round', 'stroke-linejoin': 'round', 'stroke-width': '2', d: 'M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253' })
    ])
  },
  {
    label: '用户总数',
    value: 0,
    colorClass: 'text-ink-light',
    bgClass: 'bg-ink-light/10',
    icon: () => h('svg', { class: 'w-6 h-6', fill: 'none', stroke: 'currentColor', viewBox: '0 0 24 24' }, [
      h('path', { 'stroke-linecap': 'round', 'stroke-linejoin': 'round', 'stroke-width': '2', d: 'M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z' })
    ])
  },
  {
    label: '评论总数',
    value: 0,
    colorClass: 'text-pop',
    bgClass: 'bg-pop/10',
    icon: () => h('svg', { class: 'w-6 h-6', fill: 'none', stroke: 'currentColor', viewBox: '0 0 24 24' }, [
      h('path', { 'stroke-linecap': 'round', 'stroke-linejoin': 'round', 'stroke-width': '2', d: 'M7 8h10M7 12h4m1 8l-4-4H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-3l-4 4z' })
    ])
  },
  {
    label: '收藏总数',
    value: 0,
    colorClass: 'text-ink',
    bgClass: 'bg-ink/10',
    icon: () => h('svg', { class: 'w-6 h-6', fill: 'none', stroke: 'currentColor', viewBox: '0 0 24 24' }, [
      h('path', { 'stroke-linecap': 'round', 'stroke-linejoin': 'round', 'stroke-width': '2', d: 'M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z' })
    ])
  }
])

/**
 * 加载统计数据
 */
async function loadStats() {
  loading.value = true
  try {
    const res = await getStats()
    if (res.code === 0) {
      stats.value[0].value = res.data.resourceCount || 0
      stats.value[1].value = res.data.userCount || 0
      stats.value[2].value = res.data.commentCount || 0
      stats.value[3].value = res.data.favoriteCount || 0
    } else {
      Message.error(res.message || '加载统计数据失败')
    }
  } catch (err) {
    Message.error('加载统计数据失败')
    console.error('加载统计数据失败:', err)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadStats()
})
</script>
