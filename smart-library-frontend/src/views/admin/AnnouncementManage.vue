<script setup>
import { ref, onMounted } from 'vue'
import { Message, Modal } from '@arco-design/web-vue'
import { searchAnnouncements, createAnnouncement, updateAnnouncement, deleteAnnouncement } from '@/api/announcement'
import { MdEditor } from 'md-editor-v3'
import AdminPagination from '@/components/admin/AdminPagination.vue'
import 'md-editor-v3/lib/style.css'

const announcements = ref([])
const loading = ref(false)
const showCreateModal = ref(false)
const showEditModal = ref(false)
const editingId = ref(null)

const form = ref({
  title: '',
  content: '',
  type: 1,
  priority: 0
})

const searchForm = ref({
  keyword: '',
  type: null,
  priority: null,
  status: null,
  pageNum: 1,
  pageSize: 10
})

const pagination = ref({
  total: 0,
  current: 1,
  pageSize: 10
})

const typeOptions = [
  { label: '系统更新', value: 1 },
  { label: '功能上线', value: 2 },
  { label: '维护通知', value: 3 },
  { label: '活动公告', value: 4 }
]

const priorityOptions = [
  { label: '普通', value: 0 },
  { label: '重要', value: 1 },
  { label: '紧急', value: 2 }
]

const statusOptions = [
  { label: '草稿', value: 0 },
  { label: '已发布', value: 1 }
]

/**
 * 加载公告列表
 */
async function loadAnnouncements() {
  loading.value = true
  try {
    const res = await searchAnnouncements(searchForm.value)
    if (res.code === 0) {
      announcements.value = res.data.list
      pagination.value.total = res.data.totalCount
      pagination.value.current = res.data.pageNum
    }
  } catch (error) {
    console.error('加载公告列表失败:', error)
  } finally {
    loading.value = false
  }
}

/**
 * 搜索
 */
function handleSearch() {
  searchForm.value.pageNum = 1
  pagination.value.current = 1
  loadAnnouncements()
}

/**
 * 重置搜索
 */
function handleReset() {
  searchForm.value = {
    keyword: '',
    type: null,
    priority: null,
    status: null,
    pageNum: 1,
    pageSize: 10
  }
  pagination.value.current = 1
  loadAnnouncements()
}

/**
 * 分页变化
 */
function handlePageChange(page) {
  searchForm.value.pageNum = page
  loadAnnouncements()
}

/**
 * 每页条数变化
 */
function handlePageSizeChange(pageSize) {
  searchForm.value.pageSize = pageSize
  searchForm.value.pageNum = 1
  pagination.value.pageSize = pageSize
  loadAnnouncements()
}

/**
 * 打开创建弹窗
 */
function openCreateModal() {
  form.value = {
    title: '',
    content: '',
    type: 1,
    priority: 0
  }
  editingId.value = null
  showCreateModal.value = true
}

/**
 * 打开编辑弹窗
 */
function openEditModal(announcement) {
  form.value = {
    title: announcement.title,
    content: announcement.content,
    type: announcement.type,
    priority: announcement.priority,
    status: announcement.status
  }
  editingId.value = announcement.announcementId
  showEditModal.value = true
}

/**
 * 创建公告
 */
async function handleCreate() {
  if (!form.value.title || !form.value.content) {
    Message.warning('请填写标题和内容')
    return
  }
  
  try {
    // 创建时默认保存为草稿
    const createData = {
      ...form.value,
      status: 0  // 强制设置为草稿状态
    }
    const res = await createAnnouncement(createData)
    if (res.code === 0) {
      Message.success('保存成功，公告已保存为草稿')
      showCreateModal.value = false
      loadAnnouncements()
    }
  } catch (error) {
    console.error('创建公告失败:', error)
    Message.error('保存失败')
  }
}

/**
 * 更新公告
 */
async function handleUpdate() {
  if (!form.value.title || !form.value.content) {
    Message.warning('请填写标题和内容')
    return
  }
  
  try {
    const res = await updateAnnouncement(editingId.value, form.value)
    if (res.code === 0) {
      Message.success('更新成功')
      showEditModal.value = false
      loadAnnouncements()
    }
  } catch (error) {
    console.error('更新公告失败:', error)
    Message.error('更新失败')
  }
}

/**
 * 发布公告（从草稿变为已发布）
 */
async function handlePublish(announcement) {
  // 二次确认
  Modal.confirm({
    title: '确认发布',
    content: `确定要发布公告《${announcement.title}》吗？发布后将通知所有用户。`,
    okText: '确认发布',
    cancelText: '取消',
    onOk: async () => {
      try {
        const res = await updateAnnouncement(announcement.announcementId, {
          ...announcement,
          status: 1  // 设置为已发布
        })
        if (res.code === 0) {
          Message.success('发布成功，已通知所有用户')
          loadAnnouncements()
        }
      } catch (error) {
        console.error('发布公告失败:', error)
        Message.error('发布失败')
      }
    }
  })
}

/**
 * 删除公告
 */
async function handleDelete(announcementId) {
  try {
    const res = await deleteAnnouncement(announcementId)
    if (res.code === 0) {
      Message.success('删除成功')
      loadAnnouncements()
    }
  } catch (error) {
    console.error('删除公告失败:', error)
    Message.error('删除失败')
  }
}

onMounted(() => {
  loadAnnouncements()
})
</script>

<template>
  <div class="p-6 bg-canvas min-h-screen">
    <div class="max-w-6xl mx-auto">
      <!-- 标题栏 -->
      <div class="flex items-center justify-between mb-6">
        <h1 class="text-2xl font-bold text-ink">公告管理</h1>
        <button
          @click="openCreateModal"
          class="px-6 py-2 bg-ink text-white rounded-full hover:opacity-90 transition-opacity"
        >
          创建公告
        </button>
      </div>

      <!-- 搜索栏 -->
      <div class="bg-white rounded-xl p-6 shadow-sm mb-6">
        <div class="flex items-center gap-3">
          <!-- 搜索框 - 占据更多空间 -->
          <input
            v-model="searchForm.keyword"
            type="text"
            placeholder="搜索标题或内容"
            class="flex-1 px-4 py-2 border border-structure rounded-lg focus:outline-none focus:ring-2 focus:ring-ink"
            @keyup.enter="handleSearch"
          />
          
          <!-- 类型筛选 -->
          <select
            v-model="searchForm.type"
            class="w-32 px-3 py-2 border border-structure rounded-lg focus:outline-none focus:ring-2 focus:ring-ink"
          >
            <option :value="null">全部类型</option>
            <option v-for="option in typeOptions" :key="option.value" :value="option.value">
              {{ option.label }}
            </option>
          </select>

          <!-- 优先级筛选 -->
          <select
            v-model="searchForm.priority"
            class="w-32 px-3 py-2 border border-structure rounded-lg focus:outline-none focus:ring-2 focus:ring-ink"
          >
            <option :value="null">全部优先级</option>
            <option v-for="option in priorityOptions" :key="option.value" :value="option.value">
              {{ option.label }}
            </option>
          </select>

          <!-- 状态筛选 -->
          <select
            v-model="searchForm.status"
            class="w-28 px-3 py-2 border border-structure rounded-lg focus:outline-none focus:ring-2 focus:ring-ink"
          >
            <option :value="null">全部状态</option>
            <option v-for="option in statusOptions" :key="option.value" :value="option.value">
              {{ option.label }}
            </option>
          </select>

          <!-- 搜索按钮 -->
          <button
            @click="handleSearch"
            class="px-6 py-2 bg-ink text-white rounded-lg hover:opacity-90 transition-opacity whitespace-nowrap"
          >
            搜索
          </button>
          
          <!-- 清空按钮 -->
          <button
            @click="handleReset"
            class="px-6 py-2 bg-gray-200 text-ink rounded-lg hover:bg-gray-300 transition-colors whitespace-nowrap"
          >
            清空
          </button>
        </div>
      </div>

      <!-- 公告列表 -->
      <div v-if="loading" class="text-center py-12 text-ink-light">
        加载中...
      </div>

      <div v-else-if="announcements.length === 0" class="text-center py-12 text-ink-light">
        暂无公告
      </div>

      <div v-else class="space-y-4">
        <div
          v-for="announcement in announcements"
          :key="announcement.announcementId"
          class="bg-white rounded-xl p-6 shadow-sm"
        >
          <div class="flex items-start justify-between">
            <div class="flex-1">
              <div class="flex items-center gap-2 mb-2">
                <h3 class="text-lg font-semibold text-ink">{{ announcement.title }}</h3>
                <span
                  v-if="announcement.priority === 2"
                  class="px-2 py-0.5 text-xs bg-red-100 text-red-600 rounded"
                >
                  紧急
                </span>
                <span
                  v-else-if="announcement.priority === 1"
                  class="px-2 py-0.5 text-xs bg-orange-100 text-orange-600 rounded"
                >
                  重要
                </span>
                <span
                  v-if="announcement.status === 0"
                  class="px-2 py-0.5 text-xs bg-gray-100 text-gray-600 rounded"
                >
                  草稿
                </span>
              </div>
              
              <p class="text-sm text-ink-light mb-3 line-clamp-2">
                {{ announcement.content }}
              </p>
              
              <div class="flex items-center gap-4 text-xs text-ink-light">
                <span>发布者: {{ announcement.publisherName }}</span>
                <span v-if="announcement.publishTime">
                  发布时间: {{ new Date(announcement.publishTime).toLocaleString('zh-CN') }}
                </span>
                <span>查看: {{ announcement.viewCount }}</span>
              </div>
            </div>

            <div class="ml-4 flex gap-2">
              <!-- 草稿状态显示发布按钮 -->
              <button
                v-if="announcement.status === 0"
                @click="handlePublish(announcement)"
                class="px-4 py-2 text-sm text-white bg-ink hover:opacity-90 rounded-lg transition-opacity"
              >
                发布
              </button>
              
              <button
                @click="openEditModal(announcement)"
                class="px-4 py-2 text-sm text-ink hover:bg-gray-50 rounded-lg transition-colors"
              >
                编辑
              </button>
              <button
                @click="handleDelete(announcement.announcementId)"
                class="px-4 py-2 text-sm text-pop hover:bg-red-50 rounded-lg transition-colors"
              >
                删除
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <AdminPagination
        v-if="pagination.total > 0"
        v-model:current="pagination.current"
        v-model:page-size="pagination.pageSize"
        :total="pagination.total"
        @change="handlePageChange"
        @page-size-change="handlePageSizeChange"
      />
    </div>

    <!-- 创建公告弹窗 -->
    <a-modal
      v-model:visible="showCreateModal"
      title="创建公告"
      @ok="handleCreate"
      @cancel="showCreateModal = false"
      width="900px"
      ok-text="保存为草稿"
      cancel-text="取消"
      :body-style="{ maxHeight: '70vh', overflowY: 'auto' }"
    >
      <div class="space-y-4">
        <div>
          <label class="block text-sm font-medium text-ink mb-2">标题</label>
          <input
            v-model="form.title"
            type="text"
            class="w-full px-4 py-2 border border-structure rounded-lg focus:outline-none focus:ring-2 focus:ring-ink"
            placeholder="请输入公告标题"
          />
        </div>

        <div class="grid grid-cols-2 gap-4">
          <div>
            <label class="block text-sm font-medium text-ink mb-2">类型</label>
            <select
              v-model="form.type"
              class="w-full px-4 py-2 border border-structure rounded-lg focus:outline-none focus:ring-2 focus:ring-ink"
            >
              <option v-for="option in typeOptions" :key="option.value" :value="option.value">
                {{ option.label }}
              </option>
            </select>
          </div>

          <div>
            <label class="block text-sm font-medium text-ink mb-2">优先级</label>
            <select
              v-model="form.priority"
              class="w-full px-4 py-2 border border-structure rounded-lg focus:outline-none focus:ring-2 focus:ring-ink"
            >
              <option v-for="option in priorityOptions" :key="option.value" :value="option.value">
                {{ option.label }}
              </option>
            </select>
          </div>
        </div>

        <div>
          <label class="block text-sm font-medium text-ink mb-2">内容（支持 Markdown）</label>
          <MdEditor 
            v-model="form.content" 
            language="zh-CN"
            :preview="true"
            :toolbars="[
              'bold',
              'underline',
              'italic',
              'strikeThrough',
              '-',
              'title',
              'sub',
              'sup',
              'quote',
              'unorderedList',
              'orderedList',
              'task',
              '-',
              'codeRow',
              'code',
              'link',
              'image',
              'table',
              '-',
              'revoke',
              'next',
              '=',
              'pageFullscreen',
              'fullscreen',
              'preview',
              'catalog'
            ]"
            style="height: 400px;"
          />
        </div>
      </div>
    </a-modal>

    <!-- 编辑公告弹窗 -->
    <a-modal
      v-model:visible="showEditModal"
      title="编辑公告"
      @ok="handleUpdate"
      @cancel="showEditModal = false"
      width="900px"
      :body-style="{ maxHeight: '70vh', overflowY: 'auto' }"
    >
      <div class="space-y-4">
        <div>
          <label class="block text-sm font-medium text-ink mb-2">标题</label>
          <input
            v-model="form.title"
            type="text"
            class="w-full px-4 py-2 border border-structure rounded-lg focus:outline-none focus:ring-2 focus:ring-ink"
            placeholder="请输入公告标题"
          />
        </div>

        <div class="grid grid-cols-2 gap-4">
          <div>
            <label class="block text-sm font-medium text-ink mb-2">类型</label>
            <select
              v-model="form.type"
              class="w-full px-4 py-2 border border-structure rounded-lg focus:outline-none focus:ring-2 focus:ring-ink"
            >
              <option v-for="option in typeOptions" :key="option.value" :value="option.value">
                {{ option.label }}
              </option>
            </select>
          </div>

          <div>
            <label class="block text-sm font-medium text-ink mb-2">优先级</label>
            <select
              v-model="form.priority"
              class="w-full px-4 py-2 border border-structure rounded-lg focus:outline-none focus:ring-2 focus:ring-ink"
            >
              <option v-for="option in priorityOptions" :key="option.value" :value="option.value">
                {{ option.label }}
              </option>
            </select>
          </div>
        </div>

        <div>
          <label class="block text-sm font-medium text-ink mb-2">内容（支持 Markdown）</label>
          <MdEditor 
            v-model="form.content" 
            language="zh-CN"
            :preview="true"
            :toolbars="[
              'bold',
              'underline',
              'italic',
              'strikeThrough',
              '-',
              'title',
              'sub',
              'sup',
              'quote',
              'unorderedList',
              'orderedList',
              'task',
              '-',
              'codeRow',
              'code',
              'link',
              'image',
              'table',
              '-',
              'revoke',
              'next',
              '=',
              'pageFullscreen',
              'fullscreen',
              'preview',
              'catalog'
            ]"
            style="height: 400px;"
          />
        </div>
      </div>
    </a-modal>
  </div>
</template>
