<template>
  <div class="space-y-6">
    <!-- 页面标题 -->
    <div class="flex justify-between items-center">
      <div>
        <h2 class="text-2xl font-semibold text-ink">作者管理</h2>
        <p class="mt-1 text-sm text-ink-light">管理系统中的作者信息</p>
      </div>
      <button
        @click="handleAdd"
        class="px-4 py-2 bg-pop text-white rounded-lg hover:opacity-90 transition-opacity"
      >
        添加作者
      </button>
    </div>

    <!-- 搜索栏 -->
    <div class="bg-white rounded-xl p-4" style="box-shadow: 0 2px 8px rgba(16, 42, 67, 0.08)">
      <div class="flex gap-4">
        <input
          v-model="searchKeyword"
          type="text"
          placeholder="搜索作者姓名..."
          class="flex-1 px-4 py-2 bg-canvas border-0 rounded-lg focus:outline-none focus:ring-2 focus:ring-ink-light"
          @keyup.enter="handleSearch"
        />
        <button
          @click="handleSearch"
          class="px-6 py-2 bg-ink text-white rounded-lg hover:opacity-90 transition-opacity"
        >
          搜索
        </button>
      </div>
    </div>

    <!-- 作者列表 -->
    <div class="bg-white rounded-xl overflow-hidden" style="box-shadow: 0 2px 8px rgba(16, 42, 67, 0.08)">
      <a-table
        :columns="columns"
        :data="authorList"
        :loading="loading"
        :pagination="false"
        :scroll="{ x: 1000 }"
      >
        <template #originalName="{ record }">
          <span class="text-ink-light">{{ record.originalName || '-' }}</span>
        </template>

        <template #country="{ record }">
          <span class="text-ink-light">{{ record.country || '-' }}</span>
        </template>
        
        <template #resourceCount="{ record }">
          <span class="text-ink-light">{{ record.resourceCount || 0 }}</span>
        </template>

        <template #sourceOrigin="{ record }">
          <a-tag :color="getSourceColor(record.sourceOrigin)">
            {{ getSourceText(record.sourceOrigin) }}
          </a-tag>
        </template>

        <template #ctime="{ record }">
          <span class="text-ink-light">{{ formatDate(record.ctime) }}</span>
        </template>

        <template #actions="{ record }">
          <a-space>
            <a-button type="text" size="small" @click="handleEdit(record)">
              编辑
            </a-button>
            <a-button type="text" status="danger" size="small" @click="handleDelete(record)">
              删除
            </a-button>
          </a-space>
        </template>
      </a-table>

      <!-- 分页 -->
      <AdminPagination
        v-model:current="currentPage"
        v-model:page-size="pageSize"
        :total="totalCount"
        @change="handlePageChange"
        @page-size-change="handlePageSizeChange"
      />
    </div>

    <!-- 作者表单弹窗 -->
    <AuthorForm
      v-if="showForm"
      :author="currentAuthor"
      @close="showForm = false"
      @success="handleFormSuccess"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Message, Modal } from '@arco-design/web-vue'
import { getAuthorList, deleteAuthor } from '@/api/admin'
import AdminPagination from '@/components/admin/AdminPagination.vue'
import AuthorForm from '@/components/admin/AuthorForm.vue'

const loading = ref(false)
const authorList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const totalCount = ref(0)
const searchKeyword = ref('')
const showForm = ref(false)
const currentAuthor = ref(null)

const columns = [
  { title: 'ID', dataIndex: 'authorId', width: 80 },
  { title: '姓名', dataIndex: 'name', width: 150 },
  { title: '原名', slotName: 'originalName', width: 150 },
  { title: '国籍', slotName: 'country', width: 100 },
  { title: '作品数量', slotName: 'resourceCount', width: 100 },
  { title: '数据来源', slotName: 'sourceOrigin', width: 120 },
  { title: '创建时间', slotName: 'ctime', width: 180 },
  { title: '操作', slotName: 'actions', width: 150, fixed: 'right' }
]

// 加载作者列表
const loadAuthors = async () => {
  loading.value = true
  try {
    const res = await getAuthorList({
      keyword: searchKeyword.value,
      pageNum: currentPage.value,
      pageSize: pageSize.value
    })
    
    if (res.code === 0) {
      authorList.value = res.data.list || []
      totalCount.value = res.data.totalCount || 0
    } else {
      Message.error(res.message || '加载作者列表失败')
    }
  } catch (error) {
    console.error(error)
    Message.error('加载作者列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  loadAuthors()
}

const handlePageChange = (page) => {
  currentPage.value = page
  loadAuthors()
}

const handlePageSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  loadAuthors()
}

const handleAdd = () => {
  currentAuthor.value = null
  showForm.value = true
}

const handleEdit = (author) => {
  currentAuthor.value = author
  showForm.value = true
}

const handleDelete = (author) => {
  Modal.warning({
    title: '确认删除',
    content: `确定要删除作者"${author.name}"吗？`,
    onOk: async () => {
      try {
        const res = await deleteAuthor(author.authorId)
        if (res.code === 0) {
          Message.success('删除成功')
          loadAuthors()
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

const handleFormSuccess = () => {
  showForm.value = false
  loadAuthors()
}

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  if (isNaN(date.getTime())) return '-'
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

const getSourceColor = (origin) => {
  const colors = { 1: 'blue', 2: 'green', 99: 'gray' }
  return colors[origin] || 'gray'
}

const getSourceText = (origin) => {
  const texts = { 1: '豆瓣', 2: '百科', 99: '手动录入' }
  return texts[origin] || '未知'
}

onMounted(() => {
  loadAuthors()
})
</script>
