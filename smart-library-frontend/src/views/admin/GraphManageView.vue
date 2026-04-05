<template>
  <div class="space-y-6">
    <!-- 页面标题 -->
    <div>
      <h2 class="text-2xl font-semibold text-ink">AI 图谱管理</h2>
      <p class="mt-1 text-sm text-ink-light">管理人物关系图谱生成状态与数据</p>
    </div>

    <!-- 搜索栏 -->
    <div class="bg-white rounded-xl p-4" style="box-shadow: 0 2px 8px rgba(16, 42, 67, 0.08)">
      <div class="flex gap-4">
        <input
          v-model="searchForm.keyword"
          type="text"
          placeholder="搜索图谱名称或资源ID"
          class="flex-1 px-4 py-2 bg-canvas border-0 rounded-lg focus:outline-none focus:ring-2 focus:ring-ink-light"
          @keyup.enter="handleSearch"
        />
        <a-select
          v-model="searchForm.status"
          placeholder="生成状态"
          class="w-48 bg-canvas border-0 rounded-lg"
          allow-clear
        >
          <a-option :value="0">待生成</a-option>
          <a-option :value="1">生成中</a-option>
          <a-option :value="2">已完成</a-option>
          <a-option :value="3">生成失败</a-option>
        </a-select>
        <button
          @click="handleSearch"
          class="px-6 py-2 bg-ink text-white rounded-lg hover:opacity-90 transition-opacity"
        >
          搜索
        </button>
      </div>
    </div>

    <!-- 列表操作栏 -->
    <div class="flex justify-between items-center">
      <div class="text-sm text-ink-light">
        共找到 <span class="font-semibold text-ink">{{ total }}</span> 条记录
      </div>
    </div>

    <!-- 图谱列表 -->
    <div class="bg-white rounded-xl overflow-hidden" style="box-shadow: 0 2px 8px rgba(16, 42, 67, 0.08)">
      <a-table
        :columns="columns"
        :data="graphs"
        :loading="loading"
        :pagination="false"
        :scroll="{ x: 1200 }"
      >
        <template #status="{ record }">
          <a-tag :color="getStatusColor(record.generateStatus)">
            {{ getStatusText(record.generateStatus) }}
          </a-tag>
        </template>

        <template #relationship="{ record }">
          <span class="text-ink-light">
            {{ formatRelationship(record.nodeCount, record.edgeCount) }}
          </span>
        </template>

        <template #ctime="{ record }">
          <span class="text-ink-light">{{ formatDateTime(record.ctime) }}</span>
        </template>

        <template #actions="{ record }">
          <a-space>
            <a-button
              v-if="record.generateStatus === 3"
              type="text"
              status="warning"
              size="small"
              @click="handleRetry(record)"
            >
              重试
            </a-button>
            <a-button
              type="text"
              status="danger"
              size="small"
              @click="handleDelete(record)"
            >
              删除
            </a-button>
          </a-space>
        </template>
      </a-table>

      <!-- 分页 -->
      <AdminPagination
        v-model:current="pageNum"
        v-model:page-size="pageSize"
        :total="total"
        @change="handlePageChange"
        @page-size-change="handlePageSizeChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Message, Modal } from '@arco-design/web-vue'
import { getGraphList, deleteGraph, retryGraphGeneration } from '@/api/admin'
import AdminPagination from '@/components/admin/AdminPagination.vue'

// 状态
const loading = ref(false)
const graphs = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

// 搜索表单
const searchForm = ref({
  keyword: '',
  status: null
})

// 表格列定义
const columns = [
  {
    title: 'ID',
    dataIndex: 'graphId',
    width: 80,
  },
  {
    title: '资源名称',
    dataIndex: 'resourceTitle',
    width: 150,
    ellipsis: true,
    tooltip: true
  },
  {
    title: '资源ID',
    dataIndex: 'resourceId',
    width: 100
  },
  {
    title: '版本',
    dataIndex: 'version',
    width: 80
  },
  {
    title: '状态',
    slotName: 'status',
    width: 100
  },
  {
    title: '人物关系',
    slotName: 'relationship',
    width: 120
  },
  {
    title: '创建时间',
    slotName: 'ctime',
    width: 180
  },
  {
    title: '操作',
    slotName: 'actions',
    width: 150,
    fixed: 'right'
  }
]

/**
 * 加载图谱列表
 */
async function loadGraphs() {
  loading.value = true
  try {
    const params = {
      keyword: searchForm.value.keyword,
      pageNum: pageNum.value,
      pageSize: pageSize.value
    }
    if (searchForm.value.status !== null) {
      params.status = searchForm.value.status
    }
    const res = await getGraphList(params)
    
    if (res.code === 0) {
      graphs.value = res.data.list || []
      total.value = res.data.totalCount || 0
    } else {
      Message.error(res.message || '加载图谱列表失败')
    }
  } catch (error) {
    console.error(error)
    Message.error('加载图谱列表失败')
  } finally {
    loading.value = false
  }
}

/**
 * 搜索
 */
function handleSearch() {
  pageNum.value = 1
  loadGraphs()
}

/**
 * 状态显示文字
 */
function getStatusText(status) {
  const map = {
    0: '待生成',
    1: '生成中',
    2: '已完成',
    3: '生成失败'
  }
  return map[status] || '未知'
}

/**
 * 状态显示颜色
 */
function getStatusColor(status) {
  const map = {
    0: 'gray',
    1: 'blue',
    2: 'green',
    3: 'red'
  }
  return map[status] || 'gray'
}

/**
 * 重试生成
 */
async function handleRetry(record) {
  try {
    const res = await retryGraphGeneration(record.graphId)
    if (res.code === 0) {
      Message.success('已触发重新生成')
      loadGraphs()
    } else {
      Message.error(res.message || '重试失败')
    }
  } catch (error) {
    console.error(error)
    Message.error('重试失败')
  }
}

/**
 * 删除图谱
 */
function handleDelete(record) {
  Modal.warning({
    title: '确认删除',
    content: `确定要删除该图谱记录吗？此操作不可恢复。`,
    onOk: async () => {
      try {
        const res = await deleteGraph(record.graphId)
        if (res.code === 0) {
          Message.success('删除成功')
          loadGraphs()
        } else {
          Message.error(res.message || '删除失败')
        }
      } catch (error) {
        console.error(error)
        Message.error('删除失败')
      }
    }
  })
}

/**
 * 翻页
 */
function handlePageChange(page) {
  pageNum.value = page
  loadGraphs()
}

/**
 * 改变每页条数
 */
function handlePageSizeChange(size) {
  pageSize.value = size
  pageNum.value = 1
  loadGraphs()
}

/**
 * 格式化人物关系
 */
function formatRelationship(nodeCount, edgeCount) {
  const nodes = nodeCount || 0
  const edges = edgeCount || 0
  
  // 0节点0边显示为"无"
  if (nodes === 0 && edges === 0) {
    return '无'
  }
  
  return `${nodes}人 / ${edges}关系`
}

/**
 * 格式化日期时间
 */
function formatDateTime(dateStr) {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  if (isNaN(date.getTime())) return '-'
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}`
}

onMounted(() => {
  loadGraphs()
})
</script>
