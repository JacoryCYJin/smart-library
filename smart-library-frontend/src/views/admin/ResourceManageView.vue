<template>
  <div class="space-y-6">
    <!-- 页面标题 -->
    <div>
      <h2 class="text-2xl font-semibold text-ink">资源管理</h2>
      <p class="mt-1 text-sm text-ink-light">管理图书、文献等资源内容</p>
    </div>

    <!-- 搜索栏 -->
    <div class="bg-white rounded-xl p-6" style="box-shadow: 0 2px 8px rgba(16, 42, 67, 0.08)">
      <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
        <a-input
          v-model="searchForm.keyword"
          placeholder="搜索标题/ISBN"
          allow-clear
          @press-enter="handleSearch"
        />
        <a-select
          v-model="searchForm.type"
          placeholder="资源类型"
          allow-clear
        >
          <a-option :value="1">图书</a-option>
          <a-option :value="2">文献</a-option>
        </a-select>
        <a-select
          v-model="searchForm.status"
          placeholder="状态"
          allow-clear
        >
          <a-option :value="0">已上架</a-option>
          <a-option :value="1">已下架</a-option>
        </a-select>
        <button
          @click="handleSearch"
          class="px-6 py-2 bg-ink text-white rounded-lg hover:opacity-90 transition-opacity"
        >
          搜索
        </button>
      </div>
    </div>

    <!-- 资源列表 -->
    <div class="bg-white rounded-xl overflow-hidden" style="box-shadow: 0 2px 8px rgba(16, 42, 67, 0.08)">
      <a-table
        :columns="columns"
        :data="resourceList"
        :loading="loading"
        :pagination="false"
        :scroll="{ x: 1200 }"
      >
        <template #cover="{ record }">
          <img
            v-if="record.coverUrl"
            :src="record.coverUrl"
            class="w-12 h-16 object-cover rounded"
            alt="封面"
          />
          <div v-else class="w-12 h-16 bg-structure rounded flex items-center justify-center text-xs text-ink-light">
            无封面
          </div>
        </template>

        <template #type="{ record }">
          <a-tag :color="record.type === 1 ? 'blue' : 'green'">
            {{ record.type === 1 ? '图书' : '文献' }}
          </a-tag>
        </template>

        <template #status="{ record }">
          <a-tag :color="record.deleted === 0 ? 'green' : 'orange'">
            {{ record.deleted === 0 ? '已上架' : '已下架' }}
          </a-tag>
        </template>

        <template #actions="{ record }">
          <a-space>
            <a-button
              v-if="record.deleted === 0"
              type="text"
              status="warning"
              size="small"
              @click="handleOffline(record.resourceId)"
            >
              下架
            </a-button>
            <a-button
              v-else
              type="text"
              status="success"
              size="small"
              @click="handleOnline(record.resourceId)"
            >
              上架
            </a-button>
            <a-button
              type="text"
              status="danger"
              size="small"
              @click="handleDelete(record.resourceId)"
            >
              删除
            </a-button>
          </a-space>
        </template>
      </a-table>

      <!-- 分页 -->
      <AdminPagination
        v-model:current="pagination.current"
        v-model:page-size="pagination.pageSize"
        :total="pagination.total"
        @change="handlePageChange"
        @page-size-change="handlePageSizeChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Message } from '@arco-design/web-vue'
import { getResourceList, deleteResource, restoreResource } from '@/api/admin'
import AdminPagination from '@/components/admin/AdminPagination.vue'

// 搜索表单
const searchForm = reactive({
  keyword: '',
  type: undefined,
  status: undefined
})

// 资源列表
const resourceList = ref([])
const loading = ref(false)

// 分页
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0
})

// 表格列定义
const columns = [
  {
    title: 'ID',
    dataIndex: 'resourceId',
    width: 100,
    ellipsis: true,
    tooltip: true
  },
  {
    title: '封面',
    slotName: 'cover',
    width: 80
  },
  {
    title: '标题',
    dataIndex: 'title',
    ellipsis: true,
    tooltip: true
  },
  {
    title: 'ISBN',
    dataIndex: 'isbn',
    width: 140
  },
  {
    title: '类型',
    slotName: 'type',
    width: 80
  },
  {
    title: '浏览量',
    dataIndex: 'viewCount',
    width: 100
  },
  {
    title: '评分',
    dataIndex: 'finalScore',
    width: 80,
    render: ({ record }) => {
      return record.finalScore ? record.finalScore.toFixed(1) : '-'
    }
  },
  {
    title: '状态',
    slotName: 'status',
    width: 100
  },
  {
    title: '创建时间',
    dataIndex: 'ctime',
    width: 180,
    render: ({ record }) => {
      return formatDateTime(record.ctime)
    }
  },
  {
    title: '操作',
    slotName: 'actions',
    width: 200,
    fixed: 'right'
  }
]

// 加载资源列表
const loadResourceList = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.current,
      pageSize: pagination.pageSize,
      ...searchForm
    }
    const res = await getResourceList(params)
    if (res.code === 0) {
      resourceList.value = res.data.list
      pagination.total = res.data.totalCount
    }
  } catch (error) {
    Message.error('加载资源列表失败')
  } finally {
    loading.value = false
  }
}

/**
 * 格式化日期时间
 */
const formatDateTime = (dateStr) => {
  if (!dateStr) return '-'
  
  // 处理 ISO 8601 格式
  const date = new Date(dateStr)
  
  // 检查日期是否有效
  if (isNaN(date.getTime())) return '-'
  
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  
  return `${year}-${month}-${day} ${hours}:${minutes}`
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  loadResourceList()
}

// 下架资源（软删除）
const handleOffline = async (resourceId) => {
  try {
    const res = await deleteResource(resourceId)
    if (res.code === 0) {
      Message.success('下架成功')
      loadResourceList()
    }
  } catch (error) {
    Message.error('下架失败')
  }
}

// 上架资源（恢复）
const handleOnline = async (resourceId) => {
  try {
    const res = await restoreResource(resourceId)
    if (res.code === 0) {
      Message.success('上架成功')
      loadResourceList()
    }
  } catch (error) {
    Message.error('上架失败')
  }
}

// 删除资源（暂时不实现物理删除，保留接口）
const handleDelete = async (resourceId) => {
  Message.warning('永久删除功能暂未开放')
}

// 分页变化
const handlePageChange = (page) => {
  pagination.current = page
  loadResourceList()
}

const handlePageSizeChange = (pageSize) => {
  pagination.pageSize = pageSize
  pagination.current = 1
  loadResourceList()
}

// 初始化
onMounted(() => {
  loadResourceList()
})
</script>
