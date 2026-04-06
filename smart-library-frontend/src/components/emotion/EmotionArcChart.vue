<template>
  <div class="emotion-arc-container">
    <!-- 加载状态 -->
    <div v-if="loading" class="flex flex-col items-center justify-center py-20 gap-4">
      <div class="w-12 h-12 border-4 border-ink border-t-transparent rounded-full animate-spin"></div>
      <p class="text-sm text-ink-light">{{ loadingText }}</p>
    </div>

    <!-- 空状态 -->
    <div v-else-if="isEmpty" class="flex flex-col items-center justify-center py-20 gap-4">
      <svg class="w-16 h-16 text-ink-light opacity-50" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
      </svg>
      <p class="text-sm text-ink-light">{{ emptyText }}</p>
    </div>

    <!-- 错误状态 -->
    <div v-else-if="error" class="flex flex-col items-center justify-center py-20 gap-4">
      <svg class="w-16 h-16 text-pop" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
      </svg>
      <p class="text-sm text-ink-light">{{ errorText }}</p>
    </div>

    <!-- 图表展示 -->
    <div v-else-if="arcData" class="relative">
      <!-- 标题 -->
      <div class="flex items-center justify-between mb-6">
        <h2 class="text-2xl font-bold text-ink">{{ title }}</h2>
        <div class="flex items-center gap-2 text-sm text-ink-light">
          <span>{{ arcData.chapters.length }} 个章节</span>
          <span>·</span>
          <span>{{ arcData.overallTrend }}</span>
        </div>
      </div>

      <!-- 外层容器：背景和边框 -->
      <div 
        class="relative w-full rounded-2xl border overflow-hidden"
        style="background: linear-gradient(to bottom right, #f0f4f8, #ffffff); border-color: #d9e2ec;"
        :style="{ height: `${props.height}px` }"
      >
        <!-- 内层 ECharts 容器 -->
        <div ref="chartContainer" class="w-full h-full"></div>
      </div>

      <!-- 提示文字 -->
      <div class="mt-4 text-center text-xs text-ink-light">
        <p>悬停查看章节详情 · 情感强度范围 -1.0 (极度悲伤) 到 +1.0 (极度欢乐)</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted, onBeforeUnmount, nextTick } from 'vue'
import * as echarts from 'echarts'
import { getEmotionArc } from '@/api/emotionArc'
import { useLocaleStore } from '@/stores/locale'
import gsap from 'gsap'

/**
 * 情感走向图表组件 (ECharts 版本)
 * 
 * @author jcy
 * @date 2026/04/06
 */

const props = defineProps({
  resourceId: {
    type: String,
    required: true
  },
  height: {
    type: Number,
    default: 500
  }
})

const emit = defineEmits(['arc-loaded'])

const localeStore = useLocaleStore()

// 国际化文本
const title = computed(() => localeStore.currentLang === 'zh' ? '情感走向图' : 'Emotion Arc')
const loadingText = computed(() => localeStore.currentLang === 'zh' ? '正在生成情感走向图...' : 'Generating emotion arc...')
const emptyText = computed(() => localeStore.currentLang === 'zh' ? '该作品暂无情感走向分析' : 'No emotion arc available')
const errorText = computed(() => localeStore.currentLang === 'zh' ? '生成失败' : 'Generation failed')

// 状态
const loading = ref(false)
const error = ref(false)
const isEmpty = ref(false)
const arcData = ref(null)

const chartContainer = ref(null)
let chartInstance = null

// 情感标签颜色映射（鲜艳配色）
const emotionColors = {
  '平静': '#94A3B8',
  '紧张': '#F97316',
  '悲伤': '#3B82F6',
  '欢乐': '#FBBF24',
  '震撼': '#A855F7',
  '愤怒': '#EF4444',
  '温馨': '#EC4899',
  '恐惧': '#8B5CF6',
  '希望': '#10B981',
  '绝望': '#0F172A'
}

/**
 * 初始化 ECharts 图表
 */
const initChart = () => {
  if (!chartContainer.value || !arcData.value) return

  // 销毁旧实例
  if (chartInstance) {
    chartInstance.dispose()
  }

  const { chapters } = arcData.value

  // 准备数据
  const xAxisData = chapters.map(ch => ch.chapterTitle || `第${ch.chapterIndex}章`)
  const emotionScores = chapters.map(ch => ch.emotionScore)
  const emotionLabels = chapters.map(ch => ch.emotionLabel)

  // 初始化 ECharts 实例（透明背景）
  chartInstance = echarts.init(chartContainer.value, null, {
    renderer: 'canvas',
    useDirtyRect: false
  })

  // 配置选项
  const option = {
    backgroundColor: 'rgba(0,0,0,0)',
    grid: {
      left: '8%',
      right: '5%',
      top: '12%',
      bottom: '18%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: xAxisData,
      axisLabel: {
        rotate: 45,
        color: '#0F172A',
        fontSize: 13,
        fontWeight: 'normal',
        margin: 12
      },
      axisLine: {
        lineStyle: {
          color: '#d9e2ec',
          width: 1
        }
      },
      axisTick: {
        show: false
      }
    },
    yAxis: {
      type: 'value',
      min: -1,
      max: 1,
      interval: 0.5,
      axisLabel: {
        formatter: (value) => {
          if (value === 1) return '极度欢乐'
          if (value === 0.5) return '欢乐'
          if (value === 0) return '平静'
          if (value === -0.5) return '悲伤'
          if (value === -1) return '极度悲伤'
          return value.toFixed(1)
        },
        color: '#0F172A',
        fontSize: 14,
        fontWeight: 'normal'
      },
      axisLine: {
        show: false
      },
      axisTick: {
        show: false
      },
      splitLine: {
        lineStyle: {
          color: '#d9e2ec',
          width: 1,
          type: 'solid'
        }
      }
    },
    series: [
      {
        type: 'line',
        data: emotionScores,
        smooth: true,
        smoothMonotone: 'x',
        lineStyle: {
          width: 6,
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 1,
            y2: 0,
            colorStops: [
              { offset: 0, color: '#10B981' },
              { offset: 0.25, color: '#3B82F6' },
              { offset: 0.5, color: '#A855F7' },
              { offset: 0.75, color: '#FB923C' },
              { offset: 1, color: '#EF4444' }
            ]
          }
        },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              { offset: 0, color: 'rgba(16, 185, 129, 0.8)' },
              { offset: 0.25, color: 'rgba(59, 130, 246, 0.8)' },
              { offset: 0.5, color: 'rgba(168, 85, 247, 0.8)' },
              { offset: 0.75, color: 'rgba(251, 146, 60, 0.8)' },
              { offset: 1, color: 'rgba(239, 68, 68, 0.8)' }
            ]
          }
        },
        symbol: 'circle',
        symbolSize: 12,
        itemStyle: {
          color: (params) => {
            const label = emotionLabels[params.dataIndex]
            return emotionColors[label] || '#475569'
          },
          borderColor: '#ffffff',
          borderWidth: 3
        },
        emphasis: {
          scale: true,
          scaleSize: 16,
          itemStyle: {
            borderWidth: 4
          }
        }
      }
    ],
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(15, 23, 42, 0.95)',
      borderColor: '#475569',
      borderWidth: 2,
      textStyle: {
        color: '#E2E8F0',
        fontSize: 13
      },
      padding: 18,
      formatter: (params) => {
        const index = params[0].dataIndex
        const chapter = chapters[index]
        const lines = [
          `<div style="font-size: 15px; font-weight: bold; color: #FFFFFF; margin-bottom: 8px;">${chapter.chapterTitle}</div>`,
          `<div style="margin-bottom: 4px;">情感强度: <span style="font-weight: bold;">${chapter.emotionScore.toFixed(2)}</span></div>`,
          `<div style="margin-bottom: 8px;">情感类型: <span style="font-weight: bold;">${chapter.emotionLabel}</span></div>`,
          `<div style="margin-bottom: 4px; font-weight: bold;">关键事件:</div>`
        ]
        chapter.keyEvents.forEach(event => {
          lines.push(`<div style="margin-left: 8px;">• ${event}</div>`)
        })
        return lines.join('')
      }
    }
  }

  chartInstance.setOption(option)

  // 入场动画
  nextTick(() => {
    gsap.from(chartContainer.value, {
      opacity: 0,
      scale: 0.95,
      duration: 0.6,
      ease: 'back.out(1.2)',
    })
  })
}

/**
 * 加载情感走向数据
 * 如果返回 null，说明正在生成中，3 秒后重新查询
 */
const loadEmotionArc = async () => {
  loading.value = true
  error.value = false
  isEmpty.value = false

  try {
    const res = await getEmotionArc(props.resourceId)
    
    if (res.code === 0 && res.data) {
      // 后端返回 Map<String, Object>，包含 chapterCount 和 data
      const { chapterCount, data } = res.data
      
      if (data) {
        arcData.value = typeof data === 'string' ? JSON.parse(data) : data
        
        // 检查是否为空数组（AI 判断不适合生成）
        if (chapterCount === 0 || !arcData.value.chapters || arcData.value.chapters.length === 0) {
          isEmpty.value = true
          emit('arc-loaded', false)
        } else {
          nextTick(() => {
            initChart()
          })
          emit('arc-loaded', true)
        }
      } else {
        isEmpty.value = true
        emit('arc-loaded', false)
      }
    } else if (res.code === 0 && res.data === null) {
      // 返回 null 表示正在生成中，3 秒后重新查询
      setTimeout(() => {
        loadEmotionArc()
      }, 3000)
      return // 保持 loading 状态
    } else {
      isEmpty.value = true
      emit('arc-loaded', false)
    }
  } catch (err) {
    console.error('加载情感走向失败:', err)
    error.value = true
    emit('arc-loaded', true)
  } finally {
    // 只有在非生成中状态才关闭 loading
    if (arcData.value !== null || isEmpty.value || error.value) {
      loading.value = false
    }
  }
}

const handleResize = () => {
  if (chartInstance) {
    chartInstance.resize()
  }
}

onMounted(() => {
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  if (chartInstance) {
    chartInstance.dispose()
  }
})

watch(
  () => props.resourceId,
  (newId) => {
    if (newId) loadEmotionArc()
  },
  { immediate: true }
)

watch(() => arcData.value, () => {
  if (arcData.value) {
    nextTick(() => {
      initChart()
    })
  }
}, { deep: true })
</script>

<style scoped>
.emotion-arc-container {
  width: 100%;
}
</style>
