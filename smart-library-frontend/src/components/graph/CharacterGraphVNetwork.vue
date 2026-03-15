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
  const colors = [
    '#102a43', // ink - 深海军蓝
    '#d64545', // pop - 砖红色
    '#627d98', // ink-light - 中灰蓝
    '#486581', // 深灰蓝
    '#9fb3c8', // 浅灰蓝
    '#bcccdc', // 极浅灰蓝
    '#c94040', // 深砖红
    '#e07575', // 浅砖红
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
      positionFixedByDrag: false,
      positionFixedByClickWithAltKey: true,
      createSimulation: (d3, nodes, edges) => {
        const forceLink = d3.forceLink(edges).id((d) => d.id)
        return d3
          .forceSimulation(nodes)
          .force('edge', forceLink.distance(150).strength(0.5))
          .force('charge', d3.forceManyBody().strength(-800))
          .force('center', d3.forceCenter().strength(0.05))
          .force('collide', d3.forceCollide().radius(50).strength(0.7))
          .alphaMin(0.001)
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
      strokeWidth: 0,
      strokeColor: 'transparent',
    },
    hover: {
      type: 'circle',
      radius: (node) => ((node.radius || 20) * 1.15),
      color: (node) => node.color,
      strokeWidth: 0,
      strokeColor: 'transparent',
      strokeDasharray: 0,
    },
    selected: {
      type: 'circle',
      radius: (node) => node.radius || 20,
      color: (node) => node.color || '#102a43',
      strokeWidth: 3,
      strokeColor: '#d64545',
    },
    label: {
      visible: true,
      text: 'name',
      fontSize: (node) => {
        const nameLength = node.name?.length || 0
        // 根据字数动态调整字体大小
        if (nameLength <= 2) return 15      // 2字以内: 15px
        if (nameLength <= 4) return 13      // 3-4字: 13px
        if (nameLength <= 6) return 11      // 5-6字: 11px
        return 9                             // 7字以上: 9px
      },
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
      color: '#627d98',
    },
    hover: {
      width: 2,
      color: '#627d98',
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
        color: null,
      },
    },
    label: {
      visible: true,
      text: 'label',
      fontSize: 11,
      color: '#102a43',
      background: {
        visible: true,
        color: '#f6f9fc',
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
  if (!graphData.value || !graphData.value.nodes.length) return

  const { nodes: rawNodes, edges: rawEdges } = graphData.value

  // 转换节点
  const nodesObj = {}
  const layoutsObj = {}

  rawNodes.forEach((node) => {
    const nodeRadius = 20 + (node.weight / 100) * 15

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

    layoutsObj[node.id] = {}
  })

  nodes.value = nodesObj
  layouts.value = { nodes: layoutsObj }

  // 转换边
  const edgesObj = {}
  rawEdges.forEach((edge, index) => {
    edgesObj[`edge${index}`] = {
      source: edge.source,
      target: edge.target,
      label: edge.label || '',
      isDirected: edge.isDirected !== false,
    }
  })

  edges.value = edgesObj

  // 入场动画
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
      
      if (!res.data.nodes || res.data.nodes.length === 0) {
        isEmpty.value = true
        emit('graph-loaded', false)
      } else {
        convertToVNetworkGraphData()
        emit('graph-loaded', true)
      }
    } else {
      isEmpty.value = true
      emit('graph-loaded', false)
    }
  } catch (err) {
    console.error('加载人物关系图谱失败:', err)
    error.value = true
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
watch(
  () => props.resourceId,
  (newId) => {
    if (newId) loadGraph()
  },
  { immediate: true }
)
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
