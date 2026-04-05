<template>
  <div class="space-y-6">
    <!-- 页面标题 -->
    <div>
      <h2 class="text-2xl font-semibold text-ink">用户管理</h2>
      <p class="mt-1 text-sm text-ink-light">管理系统用户账号和权限</p>
    </div>

    <!-- 搜索栏 -->
    <div class="bg-white rounded-xl p-4" style="box-shadow: 0 2px 8px rgba(16, 42, 67, 0.08)">
      <div class="flex gap-4">
        <input
          v-model="searchForm.keyword"
          type="text"
          placeholder="搜索用户名或邮箱"
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

    <!-- 用户列表 -->
    <div class="bg-white rounded-xl overflow-hidden" style="box-shadow: 0 2px 8px rgba(16, 42, 67, 0.08)">
      <a-table
        :columns="columns"
        :data="users"
        :loading="loading"
        :pagination="false"
        :scroll="{ x: 1200 }"
      >
        <template #email="{ record }">
          <span class="text-ink-light">{{ record.email || record.phone || '-' }}</span>
        </template>

        <template #role="{ record }">
          <a-tag :color="record.role === 1 ? 'red' : 'blue'">
            {{ record.role === 1 ? '管理员' : '普通用户' }}
          </a-tag>
        </template>

        <template #status="{ record }">
          <a-tag :color="record.status === 0 ? 'green' : 'red'">
            {{ record.status === 0 ? '正常' : '禁用' }}
          </a-tag>
        </template>

        <template #ctime="{ record }">
          <span class="text-ink-light">{{ formatDateTime(record.ctime) }}</span>
        </template>

        <template #actions="{ record }">
          <a-space>
            <a-button
              type="text"
              :status="record.status === 0 ? 'danger' : 'success'"
              size="small"
              @click="toggleUserStatus(record)"
            >
              {{ record.status === 0 ? '禁用' : '启用' }}
            </a-button>
            <a-button
              v-if="record.role === 0"
              type="text"
              size="small"
              @click="toggleUserRole(record)"
            >
              设为管理员
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
import { Message } from '@arco-design/web-vue'
import { getUserList, updateUserStatus, updateUserRole } from '@/api/admin'
import AdminPagination from '@/components/admin/AdminPagination.vue'

// 状态
const loading = ref(false)
const users = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

// 搜索表单
const searchForm = ref({
  keyword: ''
})

// 表格列定义
const columns = [
  {
    title: 'ID',
    dataIndex: 'userId',
    width: 100,
    ellipsis: true,
    tooltip: true
  },
  {
    title: '用户名',
    dataIndex: 'username',
    width: 150
  },
  {
    title: '邮箱/手机',
    slotName: 'email',
    width: 200
  },
  {
    title: '角色',
    slotName: 'role',
    width: 100
  },
  {
    title: '状态',
    slotName: 'status',
    width: 80
  },
  {
    title: '注册时间',
    slotName: 'ctime',
    width: 180
  },
  {
    title: '操作',
    slotName: 'actions',
    width: 200,
    fixed: 'right'
  }
]

/**
 * 加载用户列表
 */
async function loadUsers() {
  loading.value = true
  try {
    const res = await getUserList({
      keyword: searchForm.value.keyword,
      pageNum: pageNum.value,
      pageSize: pageSize.value
    })
    
    if (res.code === 0) {
      users.value = res.data.list || []
      total.value = res.data.totalCount || 0
    } else {
      Message.error(res.message || '加载用户列表失败')
    }
  } catch (error) {
    console.error(error)
    Message.error('加载用户列表失败')
  } finally {
    loading.value = false
  }
}

/**
 * 搜索
 */
function handleSearch() {
  pageNum.value = 1
  loadUsers()
}

/**
 * 切换用户状态
 */
async function toggleUserStatus(user) {
  const newStatus = user.status === 0 ? 1 : 0
  const action = newStatus === 1 ? '禁用' : '启用'
  
  try {
    const res = await updateUserStatus(user.userId, newStatus)
    if (res.code === 0) {
      Message.success(`${action}成功`)
      user.status = newStatus
    } else {
      Message.error(res.message || `${action}失败`)
    }
  } catch (error) {
    console.error(error)
    Message.error(`${action}失败`)
  }
}

/**
 * 切换用户角色
 */
async function toggleUserRole(user) {
  try {
    const res = await updateUserRole(user.userId, 1)
    if (res.code === 0) {
      Message.success('设置成功')
      user.role = 1
    } else {
      Message.error(res.message || '设置失败')
    }
  } catch (error) {
    console.error(error)
    Message.error('设置失败')
  }
}

/**
 * 翻页
 */
function handlePageChange(page) {
  pageNum.value = page
  loadUsers()
}

/**
 * 改变每页条数
 */
function handlePageSizeChange(size) {
  pageSize.value = size
  pageNum.value = 1
  loadUsers()
}

/**
 * 格式化日期时间
 */
function formatDateTime(dateStr) {
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

onMounted(() => {
  loadUsers()
})
</script>
