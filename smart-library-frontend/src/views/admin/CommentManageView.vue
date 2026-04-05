<template>
  <div class="space-y-6">
    <!-- 页面标题 -->
    <div>
      <h2 class="text-2xl font-semibold text-ink">评论管理</h2>
      <p class="mt-1 text-sm text-ink-light">审核和管理用户评论内容</p>
    </div>

    <!-- 搜索栏 -->
    <div class="bg-white rounded-xl p-6" style="box-shadow: 0 2px 8px rgba(16, 42, 67, 0.08)">
      <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
        <a-input
          v-model="searchForm.keyword"
          placeholder="搜索评论内容"
          allow-clear
          @press-enter="handleSearch"
        />
        <a-input
          v-model="searchForm.username"
          placeholder="用户名"
          allow-clear
          @press-enter="handleSearch"
        />
        <a-select
          v-model="searchForm.status"
          placeholder="状态"
          allow-clear
        >
          <a-option :value="0">正常</a-option>
          <a-option :value="1">已删除</a-option>
        </a-select>
        <button
          @click="handleSearch"
          class="px-6 py-2 bg-ink text-white rounded-lg hover:opacity-90 transition-opacity"
        >
          搜索
        </button>
      </div>
    </div>

    <!-- 评论列表 -->
    <div class="bg-white rounded-xl overflow-hidden" style="box-shadow: 0 2px 8px rgba(16, 42, 67, 0.08)">
      <a-table
        :columns="columns"
        :data="commentList"
        :loading="loading"
        :pagination="false"
        :scroll="{ x: 1200 }"
      >
        <template #content="{ record }">
          <div class="max-w-md">
            <p class="text-sm text-ink line-clamp-2">{{ record.content }}</p>
          </div>
        </template>

        <template #status="{ record }">
          <a-tag :color="record.deleted === 0 ? 'green' : 'red'">
            {{ record.deleted === 0 ? '正常' : '已删除' }}
          </a-tag>
        </template>

        <template #actions="{ record }">
          <a-space>
            <a-button
              type="text"
              size="small"
              @click="handleViewDetail(record)"
            >
              查看
            </a-button>
            <a-button
              v-if="record.deleted === 0"
              type="text"
              status="danger"
              size="small"
              @click="handleDelete(record.commentId)"
            >
              删除
            </a-button>
            <a-button
              v-else
              type="text"
              status="success"
              size="small"
              @click="handleRestore(record.commentId)"
            >
              恢复
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

    <!-- 评论详情弹窗 -->
    <a-modal
      v-model:visible="detailVisible"
      title="评论详情"
      :footer="false"
      width="600px"
    >
      <div v-if="currentComment" class="space-y-4">
        <div>
          <div class="text-sm text-ink-light mb-1">用户</div>
          <div class="text-ink">{{ currentComment.username }}</div>
        </div>
        <div>
          <div class="text-sm text-ink-light mb-1">资源标题</div>
          <div class="text-ink">{{ currentComment.resourceTitle || '未知' }}</div>
        </div>
        <div>
          <div class="text-sm text-ink-light mb-1">评分</div>
          <div class="text-ink">{{ currentComment.score ? currentComment.score.toFixed(1) + ' 分' : '无评分' }}</div>
        </div>
        <div>
          <div class="text-sm text-ink-light mb-1">评论内容</div>
          <div class="text-ink whitespace-pre-wrap">{{ currentComment.content }}</div>
        </div>
        <div>
          <div class="text-sm text-ink-light mb-1">评论时间</div>
          <div class="text-ink">{{ formatDateTime(currentComment.ctime) }}</div>
        </div>
        <div>
          <div class="text-sm text-ink-light mb-1">状态</div>
          <a-tag :color="currentComment.deleted === 0 ? 'green' : 'red'">
            {{ currentComment.deleted === 0 ? '正常' : '已删除' }}
          </a-tag>
        </div>
      </div>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Message } from '@arco-design/web-vue'
import { getCommentList, deleteComment, restoreComment } from '@/api/admin'
import AdminPagination from '@/components/admin/AdminPagination.vue'

// 搜索表单
const searchForm = reactive({
  keyword: '',
  username: '',
  status: undefined
})

// 评论列表
const commentList = ref([])
const loading = ref(false)

// 分页
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0
})

// 详情弹窗
const detailVisible = ref(false)
const currentComment = ref(null)

// 表格列定义
const columns = [
  {
    title: 'ID',
    dataIndex: 'commentId',
    width: 100,
    ellipsis: true,
    tooltip: true
  },
  {
    title: '用户名',
    dataIndex: 'username',
    width: 120
  },
  {
    title: '资源标题',
    dataIndex: 'resourceTitle',
    width: 200,
    ellipsis: true,
    tooltip: true
  },
  {
    title: '评论内容',
    slotName: 'content',
    width: 300
  },
  {
    title: '评分',
    dataIndex: 'score',
    width: 80,
    render: ({ record }) => {
      return record.score ? record.score.toFixed(1) : '-'
    }
  },
  {
    title: '状态',
    slotName: 'status',
    width: 80
  },
  {
    title: '评论时间',
    dataIndex: 'ctime',
    width: 180,
    render: ({ record }) => {
      return formatDateTime(record.ctime)
    }
  },
  {
    title: '操作',
    slotName: 'actions',
    width: 150,
    fixed: 'right'
  }
]

/**
 * 格式化日期时间
 */
const formatDateTime = (dateStr) => {
  if (!dateStr) return '-'
  
  // 处理 ISO 8601 格式 (2026-04-02T13:57:01)
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

// 加载评论列表
const loadCommentList = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.current,
      pageSize: pagination.pageSize,
      ...searchForm
    }
    const res = await getCommentList(params)
    if (res.code === 0) {
      commentList.value = res.data.list
      pagination.total = res.data.totalCount
    }
  } catch (error) {
    console.error(error)
    Message.error('加载评论列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  loadCommentList()
}

// 查看详情
const handleViewDetail = (comment) => {
  currentComment.value = comment
  detailVisible.value = true
}

// 删除评论
const handleDelete = async (commentId) => {
  try {
    const res = await deleteComment(commentId)
    if (res.code === 0) {
      Message.success('删除成功')
      loadCommentList()
    }
  } catch (error) {
        console.error(error)
        Message.error('删除失败')
      }
}

// 恢复评论
const handleRestore = async (commentId) => {
  try {
    const res = await restoreComment(commentId)
    if (res.code === 0) {
      Message.success('恢复成功')
      loadCommentList()
    }
  } catch (error) {
    console.error(error)
    Message.error('操作失败')
  }
}

// 分页变化
const handlePageChange = (page) => {
  pagination.current = page
  loadCommentList()
}

const handlePageSizeChange = (pageSize) => {
  pagination.pageSize = pageSize
  pagination.current = 1
  loadCommentList()
}

// 初始化
onMounted(() => {
  loadCommentList()
})
</script>
