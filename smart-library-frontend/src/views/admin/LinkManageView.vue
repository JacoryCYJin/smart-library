<template>
  <div class="space-y-6">
    <!-- 页面标题 -->
    <div class="flex justify-between items-center">
      <div>
        <h2 class="text-2xl font-semibold text-ink">资源链接管理</h2>
        <p class="mt-1 text-sm text-ink-light">管理外部资源的聚合链接</p>
      </div>
      <button
        @click="handleAdd"
        class="px-4 py-2 bg-pop text-white rounded-lg hover:opacity-90 transition-opacity"
      >
        添加链接
      </button>
    </div>

    <!-- 搜索栏 -->
    <div class="bg-white rounded-xl p-4" style="box-shadow: 0 2px 8px rgba(16, 42, 67, 0.08)">
      <div class="flex gap-4">
        <input
          v-model="resourceId"
          type="text"
          placeholder="按资源ID筛选..."
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

    <!-- 链接列表 -->
    <div class="bg-white rounded-xl overflow-hidden" style="box-shadow: 0 2px 8px rgba(16, 42, 67, 0.08)">
      <a-table
        :columns="columns"
        :data="groupedLinks"
        row-key="key"
        v-model:expandedKeys="expandedKeys"
        :loading="loading"
        :pagination="false"
        :scroll="{ x: 1000 }"
      >
        <template #title="{ record }">
          <template v-if="record.isResourceGroup">
            <span class="font-bold text-ink">{{ record.title }}</span>
          </template>
          <template v-else>
            <a :href="record.url" target="_blank" class="text-pop hover:underline">
              {{ record.title }}
            </a>
          </template>
        </template>

        <template #linkType="{ record }">
          <a-tag v-if="!record.isResourceGroup" :color="getLinkTypeColor(record.linkType)">
            {{ getLinkTypeText(record.linkType) }}
          </a-tag>
        </template>

        <template #platform="{ record }">
          <span v-if="!record.isResourceGroup" class="text-ink-light">{{ getPlatformText(record.platform) }}</span>
        </template>

        <template #clickCount="{ record }">
          <span v-if="!record.isResourceGroup" class="text-ink-light">{{ record.clickCount || 0 }}</span>
        </template>

        <template #status="{ record }">
          <a-tag v-if="!record.isResourceGroup" :color="record.status === 1 ? 'green' : 'red'">
            {{ record.status === 1 ? '启用' : '禁用' }}
          </a-tag>
        </template>

        <template #ctime="{ record }">
          <span v-if="!record.isResourceGroup" class="text-ink-light">{{ formatDate(record.ctime) }}</span>
        </template>

        <template #actions="{ record }">
          <a-space v-if="!record.isResourceGroup">
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

    <!-- 链接表单弹窗 -->
    <LinkForm
      v-if="showForm"
      :link="currentLink"
      @close="showForm = false"
      @success="handleFormSuccess"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { Message, Modal } from '@arco-design/web-vue'
import { getLinkList, deleteLink } from '@/api/admin'
import AdminPagination from '@/components/admin/AdminPagination.vue'
import LinkForm from '@/components/admin/LinkForm.vue'

const loading = ref(false)
const linkList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const totalCount = ref(0)
const resourceId = ref('')
const showForm = ref(false)
const currentLink = ref(null)
const expandedKeys = ref([])

const groupedLinks = computed(() => {
  const map = new Map()
  linkList.value.forEach(link => {
    if (!map.has(link.resourceId)) {
      map.set(link.resourceId, {
        isResourceGroup: true,
        key: `res-${link.resourceId}`,
        title: link.resourceTitle || '未知资源',
        resourceId: link.resourceId,
        children: []
      })
    }
    map.get(link.resourceId).children.push({
      ...link,
      key: `link-${link.linkId}`
    })
  })
  return Array.from(map.values())
})

const columns = [
  { title: '资源名称 / 链接标题', slotName: 'title', width: 300, ellipsis: true },
  { title: 'ID', dataIndex: 'linkId', width: 80 },
  { title: '资源ID', dataIndex: 'resourceId', width: 100 },
  { title: '类型', slotName: 'linkType', width: 100 },
  { title: '平台', slotName: 'platform', width: 120 },
  { title: '点击量', slotName: 'clickCount', width: 100 },
  { title: '状态', slotName: 'status', width: 100 },
  { title: '创建时间', slotName: 'ctime', width: 150 },
  { title: '操作', slotName: 'actions', width: 150, fixed: 'right' }
]

const loadLinks = async () => {
  loading.value = true
  try {
    const res = await getLinkList({
      resourceId: resourceId.value || undefined,
      pageNum: currentPage.value,
      pageSize: pageSize.value
    })
    
    if (res.code === 0) {
      linkList.value = res.data.list || []
      totalCount.value = res.data.totalCount || 0
      
      // 默认展开所有组
      expandedKeys.value = groupedLinks.value.map(g => g.key)
    } else {
      Message.error(res.message || '加载链接列表失败')
    }
  } catch (error) {
    console.error(error)
    Message.error('加载链接列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  loadLinks()
}

const handlePageChange = (page) => {
  currentPage.value = page
  loadLinks()
}

const handlePageSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  loadLinks()
}

const handleAdd = () => {
  currentLink.value = null
  showForm.value = true
}

const handleEdit = (link) => {
  currentLink.value = link
  showForm.value = true
}

const handleDelete = (link) => {
  Modal.warning({
    title: '确认删除',
    content: `确定要删除链接"${link.title}"吗？`,
    onOk: async () => {
      try {
        const res = await deleteLink(link.linkId)
        if (res.code === 0) {
          Message.success('删除成功')
          loadLinks()
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
  loadLinks()
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

const getLinkTypeColor = (type) => {
  const colors = { 1: 'blue', 2: 'green', 3: 'purple' }
  return colors[type] || 'gray'
}

const getLinkTypeText = (type) => {
  const texts = { 1: '豆瓣', 2: '下载', 3: '视频解读' }
  return texts[type] || '未知'
}

const getPlatformText = (platform) => {
  const texts = { 1: '豆瓣', 2: 'Z-Library', 3: 'B站', 4: 'YouTube', 99: '其他' }
  return texts[platform] || '未知'
}

onMounted(() => {
  loadLinks()
})
</script>
