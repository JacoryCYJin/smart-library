<script setup>
import { ref, onMounted } from 'vue'
import { Message } from '@arco-design/web-vue'
import { getAnnouncementList, createAnnouncement, deleteAnnouncement } from '@/api/announcement'
import { MdEditor } from 'md-editor-v3'
import 'md-editor-v3/lib/style.css'

const announcements = ref([])
const loading = ref(false)
const showCreateModal = ref(false)

const form = ref({
  title: '',
  content: '',
  type: 1,
  priority: 0,
  status: 1
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

/**
 * 加载公告列表
 */
async function loadAnnouncements() {
  loading.value = true
  try {
    const res = await getAnnouncementList()
    if (res.code === 0) {
      announcements.value = res.data
    }
  } catch (error) {
    console.error('加载公告列表失败:', error)
  } finally {
    loading.value = false
  }
}

/**
 * 打开创建弹窗
 */
function openCreateModal() {
  form.value = {
    title: '',
    content: '',
    type: 1,
    priority: 0,
    status: 1
  }
  showCreateModal.value = true
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
    const res = await createAnnouncement(form.value)
    if (res.code === 0) {
      Message.success('发布成功')
      showCreateModal.value = false
      loadAnnouncements()
    }
  } catch (error) {
    console.error('创建公告失败:', error)
    Message.error('发布失败')
  }
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
          发布公告
        </button>
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
              </div>
              
              <p class="text-sm text-ink-light mb-3 line-clamp-2">
                {{ announcement.content }}
              </p>
              
              <div class="flex items-center gap-4 text-xs text-ink-light">
                <span>发布者: {{ announcement.publisherName }}</span>
                <span>发布时间: {{ new Date(announcement.publishTime).toLocaleString('zh-CN') }}</span>
                <span>查看: {{ announcement.viewCount }}</span>
              </div>
            </div>

            <button
              @click="handleDelete(announcement.announcementId)"
              class="ml-4 px-4 py-2 text-sm text-pop hover:bg-red-50 rounded-lg transition-colors"
            >
              删除
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 创建公告弹窗 -->
    <a-modal
      v-model:visible="showCreateModal"
      title="发布公告"
      @ok="handleCreate"
      @cancel="showCreateModal = false"
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
