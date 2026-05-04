<template>
  <div class="dashboard-view">
    <!-- 页面标题 -->
    <div class="mb-8">
      <h1 class="text-3xl font-serif font-bold text-ink">数据概览</h1>
      <p class="text-sm text-ink-light mt-2">系统核心指标与运营数据统计</p>
    </div>

    <!-- 核心指标卡片 - 简约渐变风格 -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
      <div
        v-for="stat in coreStats"
        :key="stat.key"
        class="stat-card-modern group relative overflow-hidden rounded-2xl p-6 cursor-pointer transition-all duration-300 hover:scale-105"
        :style="{
          background: `linear-gradient(135deg, ${stat.gradient[0]} 0%, ${stat.gradient[1]} 100%)`
        }"
      >
        <div class="relative z-10">
          <div class="text-xs uppercase tracking-wider text-white/80 mb-2 font-medium">
            {{ stat.label }}
          </div>
          <div class="text-4xl font-bold text-white mb-4">
            {{ formatNumber(stat.value) }}
          </div>
          <div class="flex items-center gap-2 text-white/90">
            <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
              <path
                fill-rule="evenodd"
                d="M12 7a1 1 0 110-2h5a1 1 0 011 1v5a1 1 0 11-2 0V8.414l-4.293 4.293a1 1 0 01-1.414 0L8 10.414l-4.293 4.293a1 1 0 01-1.414-1.414l5-5a1 1 0 011.414 0L11 10.586 14.586 7H12z"
                clip-rule="evenodd"
              />
            </svg>
            <span class="text-sm font-medium">+{{ formatNumber(stat.today) }} 今日</span>
          </div>
        </div>
        <!-- 装饰性背景图案 -->
        <div
          class="absolute -right-8 -bottom-8 w-32 h-32 rounded-full opacity-20 group-hover:scale-110 transition-transform duration-500"
          :style="{ background: 'white' }"
        ></div>
      </div>
    </div>

    <!-- 增长趋势 - ECharts 专业图表 -->
    <div class="mb-8">
      <div class="bg-white rounded-2xl shadow-sm p-8 border border-structure/30">
        <div class="flex items-center justify-between mb-6">
          <div>
            <h2 class="font-serif text-xl font-semibold text-ink">增长趋势</h2>
            <p class="text-xs text-ink-light mt-1">最近 30 天数据概览</p>
          </div>
        </div>

        <!-- ECharts 图表容器 -->
        <div ref="trendChartRef" class="w-full h-80"></div>
      </div>
    </div>

    <!-- 底部网格 - 重新设计 -->
    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
      <!-- 热门分类 - 极简卡片 -->
      <div class="bg-white rounded-2xl shadow-sm border border-structure/30 flex flex-col" style="height: 520px;">
        <div class="p-6 pb-4">
          <h3 class="font-serif text-lg font-semibold text-ink">热门分类</h3>
          <p class="text-xs text-ink-light mt-1">资源分布概览</p>
        </div>
        <div class="flex-1 overflow-y-auto px-6 pb-6">
          <div class="space-y-4">
            <div
              v-for="(item, index) in dashboardData?.categoryDistribution?.slice(0, 10) || []"
              :key="item.categoryId"
              class="group"
            >
              <div class="flex items-center justify-between mb-2">
                <div class="flex items-center gap-3">
                  <span class="text-xs font-bold text-ink-light w-4">{{ index + 1 }}</span>
                  <span class="text-sm font-medium text-ink">{{ item.categoryName }}</span>
                </div>
                <span class="text-sm font-bold text-ink">{{ formatNumber(item.count) }}</span>
              </div>
              <div class="h-1.5 bg-canvas rounded-full overflow-hidden">
                <div
                  class="h-full rounded-full transition-all duration-500 group-hover:opacity-80"
                  :style="{
                    width: `${(item.count / Math.max(...(dashboardData?.categoryDistribution?.map(c => c.count) || [1]))) * 100}%`,
                    background: `linear-gradient(90deg, ${getCategoryColor(index)[0]}, ${getCategoryColor(index)[1]})`
                  }"
                ></div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 热门资源 - 精致列表 -->
      <div class="bg-white rounded-2xl shadow-sm border border-structure/30 flex flex-col" style="height: 520px;">
        <div class="p-6 pb-4">
          <h3 class="font-serif text-lg font-semibold text-ink">热门资源</h3>
          <p class="text-xs text-ink-light mt-1">浏览与收藏排行</p>
        </div>
        
        <!-- 自定义标签切换 -->
        <div class="px-6 flex gap-2 border-b border-structure/30">
          <button
            @click="activeRankingTab = 'view'"
            class="px-4 py-2 text-sm font-medium transition-all relative"
            :class="activeRankingTab === 'view' ? 'text-ink' : 'text-ink-light hover:text-ink'"
          >
            浏览排行
            <div
              v-if="activeRankingTab === 'view'"
              class="absolute bottom-0 left-0 right-0 h-0.5 bg-ink rounded-full"
            ></div>
          </button>
          <button
            @click="activeRankingTab = 'favorite'"
            class="px-4 py-2 text-sm font-medium transition-all relative"
            :class="activeRankingTab === 'favorite' ? 'text-ink' : 'text-ink-light hover:text-ink'"
          >
            收藏排行
            <div
              v-if="activeRankingTab === 'favorite'"
              class="absolute bottom-0 left-0 right-0 h-0.5 bg-ink rounded-full"
            ></div>
          </button>
        </div>

        <div class="flex-1 overflow-y-auto p-4">
          <!-- 浏览排行 -->
          <div v-if="activeRankingTab === 'view'" class="space-y-3">
            <div
              v-for="item in viewRanking?.slice(0, 10)"
              :key="item.resourceId"
              class="flex items-center gap-3 p-3 rounded-xl hover:bg-canvas/50 transition-all cursor-pointer group"
              @click="goToResource(item.resourceId)"
            >
              <div
                class="w-6 h-6 rounded-lg flex items-center justify-center text-xs font-bold text-white bg-gradient-to-br from-ink to-ink-light"
              >
                {{ item.rank }}
              </div>
              <img
                v-if="item.coverUrl"
                :src="item.coverUrl"
                :alt="item.title"
                class="w-10 h-14 object-cover rounded-lg shadow-sm group-hover:shadow-md transition-shadow"
              />
              <div class="flex-1 min-w-0">
                <div class="text-sm text-ink truncate font-medium group-hover:text-pop transition-colors">
                  {{ item.title }}
                </div>
                <div class="text-xs text-ink-light mt-1">{{ formatNumber(item.viewCount) }} 次浏览</div>
              </div>
            </div>
          </div>

          <!-- 收藏排行 -->
          <div v-if="activeRankingTab === 'favorite'" class="space-y-3">
            <div
              v-for="item in favoriteRanking?.slice(0, 10)"
              :key="item.resourceId"
              class="flex items-center gap-3 p-3 rounded-xl hover:bg-canvas/50 transition-all cursor-pointer group"
              @click="goToResource(item.resourceId)"
            >
              <div
                class="w-6 h-6 rounded-lg flex items-center justify-center text-xs font-bold text-white bg-gradient-to-br from-pop to-pink-400"
              >
                {{ item.rank }}
              </div>
              <img
                v-if="item.coverUrl"
                :src="item.coverUrl"
                :alt="item.title"
                class="w-10 h-14 object-cover rounded-lg shadow-sm group-hover:shadow-md transition-shadow"
              />
              <div class="flex-1 min-w-0">
                <div class="text-sm text-ink truncate font-medium group-hover:text-pop transition-colors">
                  {{ item.title }}
                </div>
                <div class="text-xs text-ink-light mt-1">{{ formatNumber(item.favoriteCount) }} 次收藏</div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 系统状态 - 现代仪表盘 -->
      <div class="bg-white rounded-2xl shadow-sm border border-structure/30 flex flex-col" style="height: 520px;">
        <div class="p-6 pb-4">
          <h3 class="font-serif text-lg font-semibold text-ink">系统状态</h3>
        </div>

        <div class="flex-1 overflow-y-auto px-6 pb-6">
          <!-- AI 图谱 - 圆环进度 -->
          <div class="mb-8">
            <div class="text-xs uppercase tracking-wider text-ink-light mb-4 font-medium">
              AI 图谱生成
            </div>
            <div class="flex items-center justify-between">
              <div class="flex-1">
                <div class="flex items-baseline gap-2 mb-2">
                  <span class="text-3xl font-bold text-ink">{{ graphStats.totalCount || 0 }}</span>
                  <span class="text-xs text-ink-light">总计</span>
                </div>
                <div class="flex items-center gap-2">
                  <div class="flex-1 h-2 bg-canvas rounded-full overflow-hidden">
                    <div
                      class="h-full bg-gradient-to-r from-green-400 to-green-500 rounded-full transition-all duration-500"
                      :style="{ width: `${graphStats.successRate || 0}%` }"
                    ></div>
                  </div>
                  <span class="text-sm font-bold text-green-500">{{ graphStats.successRate || 0 }}%</span>
                </div>
              </div>
            </div>
          </div>

          <!-- 爬虫任务 - 简约状态卡 -->
          <div>
            <div class="text-xs uppercase tracking-wider text-ink-light mb-4 font-medium">
              爬虫任务
            </div>
            <div class="space-y-3">
              <div
                v-for="(stats, key) in crawlerStats"
                :key="key"
                class="p-4 rounded-xl bg-gradient-to-br from-canvas/50 to-canvas/30 border border-structure/20"
              >
                <div class="text-sm font-medium text-ink mb-3">{{ getCrawlerName(key) }}</div>
                <div class="grid grid-cols-3 gap-2 text-center">
                  <div>
                    <div class="text-lg font-bold text-ink-light">{{ stats.pending || 0 }}</div>
                    <div class="text-xs text-ink-light mt-1">待处理</div>
                  </div>
                  <div>
                    <div class="text-lg font-bold text-blue-500">{{ stats.processing || 0 }}</div>
                    <div class="text-xs text-ink-light mt-1">处理中</div>
                  </div>
                  <div>
                    <div class="text-lg font-bold text-green-500">{{ stats.completed || 0 }}</div>
                    <div class="text-xs text-ink-light mt-1">已完成</div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, nextTick, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import {
  getDashboardStats,
  getTrends,
  getViewRanking,
  getFavoriteRanking
} from '@/api/admin'

const loading = ref(true)
const dashboardData = ref(null)
const trendsData = ref(null)
const viewRanking = ref([])
const favoriteRanking = ref([])
const activeRankingTab = ref('view')
const trendChartRef = ref(null)
let trendChart = null

// 核心统计数据 - 使用设计系统配色
const coreStats = computed(() => {
  if (!dashboardData.value) return []

  return [
    {
      key: 'resource',
      label: '资源总数',
      value: dashboardData.value.resourceCount || 0,
      today: dashboardData.value.todayResourceCount || 0,
      gradient: ['#102a43', '#243b53'] // ink 深色系
    },
    {
      key: 'user',
      label: '用户总数',
      value: dashboardData.value.userCount || 0,
      today: dashboardData.value.todayUserCount || 0,
      gradient: ['#486581', '#627d98'] // ink-light 中灰蓝
    },
    {
      key: 'comment',
      label: '评论总数',
      value: dashboardData.value.commentCount || 0,
      today: dashboardData.value.todayCommentCount || 0,
      gradient: ['#829ab1', '#9fb3c8'] // 淡灰蓝
    },
    {
      key: 'favorite',
      label: '收藏总数',
      value: dashboardData.value.favoriteCount || 0,
      today: dashboardData.value.todayFavoriteCount || 0,
      gradient: ['#d64545', '#e66a6a'] // pop 砖红色
    }
  ]
})

// AI 图谱统计
const graphStats = computed(() => dashboardData.value?.graphStats || {})

// 爬虫统计
const crawlerStats = computed(() => dashboardData.value?.crawlerStats || {})

// 分类渐变色 - 基于设计系统
const categoryColors = [
  ['#102a43', '#243b53'], // ink
  ['#d64545', '#e66a6a'], // pop
  ['#486581', '#627d98'], // ink-light
  ['#829ab1', '#9fb3c8'], // 淡灰蓝
  ['#bcccdc', '#d9e2ec'], // structure
  ['#102a43', '#486581'], // ink 渐变
  ['#627d98', '#829ab1'], // 中灰蓝渐变
  ['#d64545', '#bc3a3a']  // pop 深色
]

const getCategoryColor = (index) => {
  return categoryColors[index % categoryColors.length]
}

// 格式化数字
const formatNumber = (num) => {
  if (!num) return 0
  return num.toLocaleString()
}

// 获取爬虫名称
const getCrawlerName = (key) => {
  const names = {
    douban: '豆瓣图书',
    author: '作者信息',
    link: '资源链接'
  }
  return names[key] || key
}

// 跳转到资源详情
const goToResource = (resourceId) => {
  window.open(`/book/${resourceId}`, '_blank')
}

// 初始化 ECharts 图表
const initTrendChart = () => {
  if (!trendChartRef.value) return
  
  trendChart = echarts.init(trendChartRef.value)
  
  const option = {
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(255, 255, 255, 0.95)',
      borderColor: '#d9e2ec',
      borderWidth: 1,
      textStyle: {
        color: '#102a43',
        fontSize: 13
      },
      padding: [12, 16],
      axisPointer: {
        type: 'cross',
        crossStyle: {
          color: '#627d98'
        },
        lineStyle: {
          color: '#d9e2ec',
          type: 'dashed'
        }
      }
    },
    legend: {
      data: ['用户增长', '资源增长'],
      top: 0,
      right: 0,
      textStyle: {
        color: '#486581',
        fontSize: 13
      },
      itemWidth: 20,
      itemHeight: 12,
      itemGap: 20
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      top: '15%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: trendsData.value?.dates || [],
      axisLine: {
        lineStyle: {
          color: '#d9e2ec'
        }
      },
      axisLabel: {
        color: '#627d98',
        fontSize: 12
      },
      axisTick: {
        show: false
      }
    },
    yAxis: {
      type: 'value',
      axisLine: {
        show: false
      },
      axisLabel: {
        color: '#627d98',
        fontSize: 12
      },
      splitLine: {
        lineStyle: {
          color: '#d9e2ec',
          type: 'dashed'
        }
      }
    },
    series: [
      {
        name: '用户增长',
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: 8,
        data: trendsData.value?.userCounts || [],
        lineStyle: {
          width: 3,
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 1,
            y2: 0,
            colorStops: [
              { offset: 0, color: '#4ade80' },
              { offset: 1, color: '#22c55e' }
            ]
          }
        },
        itemStyle: {
          color: '#22c55e',
          borderColor: '#fff',
          borderWidth: 2
        },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              { offset: 0, color: 'rgba(74, 222, 128, 0.3)' },
              { offset: 1, color: 'rgba(74, 222, 128, 0.05)' }
            ]
          }
        },
        emphasis: {
          focus: 'series',
          itemStyle: {
            shadowBlur: 10,
            shadowColor: 'rgba(34, 197, 94, 0.5)'
          }
        }
      },
      {
        name: '资源增长',
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: 8,
        data: trendsData.value?.resourceCounts || [],
        lineStyle: {
          width: 3,
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 1,
            y2: 0,
            colorStops: [
              { offset: 0, color: '#60a5fa' },
              { offset: 1, color: '#3b82f6' }
            ]
          }
        },
        itemStyle: {
          color: '#3b82f6',
          borderColor: '#fff',
          borderWidth: 2
        },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              { offset: 0, color: 'rgba(96, 165, 250, 0.3)' },
              { offset: 1, color: 'rgba(96, 165, 250, 0.05)' }
            ]
          }
        },
        emphasis: {
          focus: 'series',
          itemStyle: {
            shadowBlur: 10,
            shadowColor: 'rgba(59, 130, 246, 0.5)'
          }
        }
      }
    ]
  }
  
  trendChart.setOption(option)
}

// 响应式调整图表大小
const handleResize = () => {
  trendChart?.resize()
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const [dashboardRes, trendsRes, viewRes, favoriteRes] = await Promise.all([
      getDashboardStats(),
      getTrends(30),
      getViewRanking(10),
      getFavoriteRanking(10)
    ])

    if (dashboardRes.code === 0) {
      dashboardData.value = dashboardRes.data
    }

    if (trendsRes.code === 0) {
      trendsData.value = trendsRes.data
      // 数据加载完成后初始化图表
      await nextTick()
      initTrendChart()
    }

    if (viewRes.code === 0) {
      viewRanking.value = viewRes.data
    }

    if (favoriteRes.code === 0) {
      favoriteRanking.value = favoriteRes.data
    }
  } catch (error) {
    console.error('加载数据失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadData()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  if (trendChart) {
    trendChart.dispose()
    trendChart = null
  }
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
.stat-card-modern {
  position: relative;
  backdrop-filter: blur(10px);
}

/* 自定义滚动条 */
.overflow-y-auto::-webkit-scrollbar {
  width: 4px;
}

.overflow-y-auto::-webkit-scrollbar-track {
  background: transparent;
}

.overflow-y-auto::-webkit-scrollbar-thumb {
  background: var(--color-structure);
  border-radius: 2px;
}

.overflow-y-auto::-webkit-scrollbar-thumb:hover {
  background: var(--color-ink-light);
}
</style>
