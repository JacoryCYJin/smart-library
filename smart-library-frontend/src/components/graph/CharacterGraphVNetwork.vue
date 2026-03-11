<template>
  <div class="character-graph-container">
    <!-- 加载状态 -->
    <div v-if="loading" class="flex flex-col items-center justify-center py-20 gap-4">
      <div class="w-12 h-12 border-4 border-ink border-t-transparent rounded-full animate-spin"></div>
      <p class="text-sm text-ink-light">{{ loadingText }}</p>
    </div>

    <!-- 空状态 -->
    <div v-else-if="isEmpty" class="flex flex-col items-center justify-center py-20 gap-4">
      <svg class="w-16 h-16 text-ink-light opacity-50" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
      </svg>
      <p class="text-sm text-ink-light">{{ emptyText }}</p>
    </div>

    <!-- 错误状态 -->
    <div v-else-if="error" class="flex flex-col items-center justify-center py-20 gap-4">
      <svg class="w-16 h-16 text-pop" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
      </svg>
      <p class="text-sm text-ink-light">{{ errorText }}</p>
      <button
        @click="retryGenerate"
        class="px-6 py-2 bg-ink text-white rounded-full text-sm font-medium transition-all duration-300 hover:bg-pop"
      >
        {{ retryText }}
      </button>
    </div>

    <!-- 图谱展示 -->
    <div v-else-if="graphData" class="relative">
      <!-- 标题 -->
      <div class="flex items-center justify-between mb-6">
        <h2 class="text-2xl font-bold text-ink">{{ title }}</h2>
        <div class="flex items-center gap-2 text-sm text-ink-light">
          <span>{{ Object.keys(nodes).length }} 个人物</span>
          <span>·</span>
          <span>{{ Object.keys(edges).length }} 个关系</span>
        </div>
      </div>

      <!-- v-network-graph 画布 -->
      <div 
        class="relative w-full rounded-2xl border border-structure overflow-hidden"
        style="background: linear-gradient(to bottom right, #f0f4f8, #ffffff);"
        :style="{ height: `${height}px` }"
      >
        <v-network-graph
          v-model:selected-nodes="selectedNodes"
          v-model:selected-edges="selectedEdges"
          :nodes="nodes"
          :edges="edges"
          :layouts="layouts"
          :configs="configs"
          class="graph"
        >
          <!-- 自定义边标签 -->
          <template #edge-label="{ edge, ...slotProps }">
            <v-edge-label
              :text="edge.label"
              align="center"
              vertical-align="center"
              v-bind="slotProps"
              :style="{ fill: '#102a43' }"
            />
          </template>
        </v-network-graph>

        <!-- 图例 -->
        <div class="absolute top-4 right-4 bg-white/90 backdrop-blur-sm rounded-xl p-4 shadow-lg border border-structure z-10">
          <div class="text-xs font-semibold text-ink mb-2">阵营</div>
          <div class="flex flex-col gap-2">
            <div
              v-for="(category, index) in categories"
              :key="index"
              class="flex items-center gap-2"
            >
              <div
                class="w-4 h-4 rounded-full"
                :style="{ backgroundColor: category.color }"
              ></div>
              <span class="text-xs text-ink-light">{{ category.name }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 提示文字 -->
      <div class="mt-4 text-center text-xs text-ink-light">
        <p>节点大小代表人物重要程度 · 箭头表示有向关系 · 拖动节点调整位置</p>
        <p class="mt-1">滚轮缩放 · 拖动画布移动视图 · 双击节点聚焦</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, nextTick } from 'vue'
import { VNetworkGraph, VEdgeLabel, defineConfigs } from 'v-network-graph'
import * as vNG from 'v-network-graph/lib/force-layout'
import { getGraph, generateGraph } from '@/api/graph'
import { useLocaleStore } from '@/stores/locale'
import gsap from 'gsap'
import 'v-network-graph/lib/style.css'

const props = defineProps({
  resourceId: {
    type: String,
    required: true,
  },
  width: {
    type: Number,
    default: 1000,
  },
  height: {
    type: Number,
    default: 600,
  },
})

// 定义 emit
const emit = defineEmits(['graph-loaded'])

const localeStore = useLocaleStore()

// 国际化文本
const title = computed(() => localeStore.currentLang === 'zh' ? '人物关系图谱' : 'Character Relationship Graph')
const loadingText = computed(() => localeStore.currentLang === 'zh' ? '正在生成人物关系图谱...' : 'Generating character graph...')
const emptyText = computed(() => localeStore.currentLang === 'zh' ? '该作品暂无人物关系图谱' : 'No character graph available')
const errorText = computed(() => localeStore.currentLang === 'zh' ? '生成失败，请重试' : 'Generation failed, please retry')
const retryText = computed(() => localeStore.currentLang === 'zh' ? '重新生成' : 'Retry')

// 状态
const loading = ref(false)
const error = ref(false)
const isEmpty = ref(false)
const graphData = ref(null)

// v-network-graph 数据
const nodes = ref({})
const edges = ref({})
const layouts = ref({ nodes: {} })
const selectedNodes = ref([])
const selectedEdges = ref([])

// 分类颜色映射
const categoryColors = ref({})
const categories = computed(() => {
  return Object.entries(categoryColors.value).map(([name, color]) => ({
    name,
    color,
  }))
})

/**
 * 生成分类颜色（仅使用主题色）
 */
const generateCategoryColor = (_category, index) => {
  // 仅使用主题色系
  const colors = [
    '#102a43', // ink - 深海军蓝（主色）
    '#d64545', // pop - 砖红色（强调色）
    '#627d98', // ink-light - 中灰蓝（次要色）
    '#d9e2ec', // structure - 淡灰蓝（结构色，需要加深边框）
  ]
  return colors[index % colors.length]
}

/**
 * v-network-graph 配置
 */
const configs = defineConfigs({
  view: {
    scalingObjects: true,
    minZoomLevel: 0.3,
    maxZoomLevel: 3,
    panEnabled: true,
    zoomEnabled: true,
    layoutHandler: new vNG.ForceLayout({
      positionFixedByDrag: false, // 拖动后不固定位置，继续参与力导向计算
      positionFixedByClickWithAltKey: true, // Alt+点击可以固定节点
      createSimulation: (d3, nodes, edges) => {
        // 自定义 d3-force 参数
        const forceLink = d3.forceLink(edges).id((d) => d.id)
        return d3
          .forceSimulation(nodes)
          .force('edge', forceLink.distance(150).strength(0.5)) // 边的理想长度和强度
          .force('charge', d3.forceManyBody().strength(-800)) // 节点间排斥力
          .force('center', d3.forceCenter().strength(0.05)) // 向中心聚拢的力
          .force('collide', d3.forceCollide().radius(50).strength(0.7)) // 防止节点重叠
          .alphaMin(0.001) // 最小能量阈值
      },
    }),
  },
  node: {
    selectable: true,
    draggable: true,
    normal: {
      type: 'circle',
      radius: (node) => node.radius || 30,
      color: (node) => node.color || '#102a43',
      strokeWidth: 0, // 去掉边框
      strokeColor: 'transparent',
    },
    hover: {
      type: 'circle',
      radius: (node) => ((node.radius || 20) * 1.15), // hover 时放大 15%
      color: (node) => node.color, // 保持原色
      strokeWidth: 0,
      strokeColor: 'transparent',
      strokeDasharray: 0,
    },
    selected: {
      type: 'circle',
      radius: (node) => node.radius || 20,
      color: (node) => node.color || '#102a43', // 保持原色
      strokeWidth: 3,
      strokeColor: '#d64545', // 选中时显示红色边框
    },
    label: {
      visible: true,
      text: 'name',
      fontSize: 15,
      color: '#ffffff',
      fontFamily: undefined,
      direction: 'center',
      margin: 0,
    },
    focusring: {
      visible: false,
    },
  },
  edge: {
    selectable: true,
    normal: {
      width: 2,
      color: '#627d98', // 使用主题色 ink-light
    },
    hover: {
      width: 2, // 保持原宽度
      color: '#627d98', // 保持原色，不变色
    },
    selected: {
      width: 3,
      color: '#d64545',
    },
    marker: {
      target: {
        type: (edge) => (edge.isDirected ? 'arrow' : 'none'),
        width: 8,
        height: 8,
        color: null, // null 表示使用边的颜色
      },
    },
    label: {
      visible: true,
      text: 'label', // 使用边数据中的 label 字段作为标签
      fontSize: 11,
      color: '#102a43', // 使用主题色 ink
      background: {
        visible: true,
        color: '#f6f9fc', // 更接近渐变的浅色
        padding: {
          vertical: 2,
          horizontal: 6,
        },
        borderRadius: 4,
      },
    },
  },
})

/**
 * 转换数据为 v-network-graph 格式
 */
const convertToVNetworkGraphData = () => {
  if (!graphData.value || !graphData.value.nodes.length) {
    console.log('❌ 没有图谱数据')
    return
  }

  const { nodes: rawNodes, edges: rawEdges } = graphData.value

  // 转换节点
  const nodesObj = {}
  const layoutsObj = {}

  rawNodes.forEach((node) => {
    // 根据 weight 计算节点大小（20-35）
    const nodeRadius = 20 + (node.weight / 100) * 15

    // 生成分类颜色
    if (!categoryColors.value[node.category]) {
      const colorIndex = Object.keys(categoryColors.value).length
      categoryColors.value[node.category] = generateCategoryColor(node.category, colorIndex)
    }

    nodesObj[node.id] = {
      name: node.label,
      category: node.category,
      weight: node.weight,
      color: categoryColors.value[node.category],
      radius: nodeRadius,
    }

    // 初始位置由 ForceLayout 自动计算，这里只需要提供空对象
    layoutsObj[node.id] = {}
  })

  nodes.value = nodesObj
  layouts.value = { nodes: layoutsObj }

  // 转换边（确保 label 字段存在）
  const edgesObj = {}
  rawEdges.forEach((edge, index) => {
    edgesObj[`edge${index}`] = {
      source: edge.source,
      target: edge.target,
      label: edge.label || '', // 确保 label 字段存在
      isDirected: edge.isDirected !== false, // 默认为有向
    }
  })

  edges.value = edgesObj

  // GSAP 动画入场
  nextTick(() => {
    gsap.from('.graph', {
      opacity: 0,
      scale: 0.9,
      duration: 0.6,
      ease: 'back.out(1.7)',
    })
  })
}

/**
 * 加载图谱数据
 */
const loadGraph = async () => {
  loading.value = true
  error.value = false
  isEmpty.value = false

  try {
    const res = await getGraph(props.resourceId)
    
    if (res.code === 0 && res.data) {
      graphData.value = res.data
      
      // 检查是否为空
      if (!res.data.nodes || res.data.nodes.length === 0) {
        isEmpty.value = true
        // 通知父组件：数据库中确定没有图谱
        emit('graph-loaded', false)
      } else {
        // 转换数据为 v-network-graph 格式
        convertToVNetworkGraphData()
        // 通知父组件：有图谱数据
        emit('graph-loaded', true)
      }
    } else {
      isEmpty.value = true
      // 通知父组件：没有图谱
      emit('graph-loaded', false)
    }
  } catch (err) {
    console.error('加载人物关系图谱失败:', err)
    error.value = true
    // 发生错误时，保持显示（可能是网络问题）
    emit('graph-loaded', true)
  } finally {
    loading.value = false
  }
}

/**
 * 重新生成图谱
 */
const retryGenerate = async () => {
  loading.value = true
  error.value = false

  try {
    await generateGraph(props.resourceId)
    // 重新加载
    await loadGraph()
  } catch (err) {
    console.error('生成人物关系图谱失败:', err)
    error.value = true
    loading.value = false
  }
}

// 监听 resourceId 变化
watch(() => props.resourceId, () => {
  if (props.resourceId) {
    loadGraph()
  }
}, { immediate: true })
</script>

<style scoped>
.character-graph-container {
  width: 100%;
}

.graph {
  width: 100%;
  height: 100%;
}
</style>
