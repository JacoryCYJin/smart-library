<template>
  <div class="min-h-screen bg-canvas py-12 px-4">
    <div class="max-w-4xl mx-auto">
      <!-- 页面标题 -->
      <h1 class="text-3xl font-bold text-ink mb-8">个人中心</h1>

      <!-- 用户信息卡片 -->
      <div class="bg-white rounded-2xl shadow-sm p-8 mb-6">
        <div class="flex items-start gap-6">
          <!-- 头像 -->
          <div class="flex-shrink-0">
            <div class="w-24 h-24 rounded-full bg-structure flex items-center justify-center text-3xl text-ink-light">
              {{ user.username?.charAt(0) || '用' }}
            </div>
          </div>

          <!-- 用户信息 -->
          <div class="flex-1">
            <h2 class="text-2xl font-bold text-ink mb-2">{{ user.username }}</h2>
            <p class="text-ink-light mb-4">{{ user.email || user.phone || '未设置联系方式' }}</p>
            
            <div class="flex gap-4 text-sm text-ink-light">
              <div>
                <span class="font-medium">注册时间：</span>
                {{ formatDate(user.ctime) }}
              </div>
            </div>
          </div>

          <!-- 编辑按钮 -->
          <button
            @click="isEditing = true"
            class="px-4 py-2 bg-pop text-white rounded-lg hover:opacity-90 transition-opacity"
          >
            编辑资料
          </button>
        </div>

        <!-- 个人简介 -->
        <div v-if="user.bio" class="mt-6 pt-6 border-t border-structure">
          <h3 class="text-sm font-medium text-ink-light mb-2">个人简介</h3>
          <p class="text-ink">{{ user.bio }}</p>
        </div>
      </div>

      <!-- 统计卡片 -->
      <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div class="bg-white rounded-xl shadow-sm p-6 text-center">
          <div class="text-3xl font-bold text-pop mb-2">{{ stats.favoriteCount }}</div>
          <div class="text-ink-light">我的收藏</div>
        </div>
        <div class="bg-white rounded-xl shadow-sm p-6 text-center">
          <div class="text-3xl font-bold text-pop mb-2">{{ stats.historyCount }}</div>
          <div class="text-ink-light">浏览历史</div>
        </div>
        <div class="bg-white rounded-xl shadow-sm p-6 text-center">
          <div class="text-3xl font-bold text-pop mb-2">{{ stats.commentCount || 0 }}</div>
          <div class="text-ink-light">我的评论</div>
        </div>
      </div>
    </div>

    <!-- 编辑资料弹窗 -->
    <a-modal
      v-model:visible="isEditing"
      title="编辑资料"
      @ok="handleSave"
      @cancel="handleCancel"
      :ok-loading="saving"
    >
      <a-form :model="editForm" layout="vertical">
        <a-form-item label="用户名">
          <a-input v-model="editForm.username" placeholder="请输入用户名" />
        </a-form-item>
        
        <a-form-item label="手机号">
          <a-input v-model="editForm.phone" placeholder="请输入手机号" />
        </a-form-item>
        
        <a-form-item label="邮箱">
          <a-input v-model="editForm.email" placeholder="请输入邮箱" />
        </a-form-item>
        
        <a-form-item label="个人简介">
          <a-textarea
            v-model="editForm.bio"
            placeholder="介绍一下自己吧"
            :max-length="200"
            show-word-limit
            :auto-size="{ minRows: 3, maxRows: 5 }"
          />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { getProfile, updateProfile } from '@/api/user'
import { countUserFavorites } from '@/api/favorite'
import { countBrowseHistory } from '@/api/history'
import { Message } from '@arco-design/web-vue'

const authStore = useAuthStore()

// 用户信息
const user = ref({})

// 统计数据
const stats = reactive({
  favoriteCount: 0,
  historyCount: 0,
  commentCount: 0
})

// 编辑状态
const isEditing = ref(false)
const saving = ref(false)

// 编辑表单
const editForm = reactive({
  username: '',
  phone: '',
  email: '',
  bio: ''
})

/**
 * 格式化日期
 */
const formatDate = (dateStr) => {
  if (!dateStr) return '未知'
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN')
}

/**
 * 加载用户信息
 */
const loadUserProfile = async () => {
  try {
    const res = await getProfile()
    if (res.code === 200) {
      user.value = res.data
      // 初始化编辑表单
      editForm.username = res.data.username
      editForm.phone = res.data.phone || ''
      editForm.email = res.data.email || ''
      editForm.bio = res.data.bio || ''
    }
  } catch (error) {
    console.error('加载用户信息失败:', error)
    Message.error('加载用户信息失败')
  }
}

/**
 * 加载统计数据
 */
const loadStats = async () => {
  try {
    // 加载收藏数量
    const favoriteRes = await countUserFavorites()
    if (favoriteRes.code === 200) {
      stats.favoriteCount = favoriteRes.data
    }

    // 加载浏览历史数量
    const historyRes = await countBrowseHistory()
    if (historyRes.code === 200) {
      stats.historyCount = historyRes.data
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

/**
 * 保存编辑
 */
const handleSave = async () => {
  saving.value = true
  try {
    const res = await updateProfile(editForm)
    if (res.code === 200) {
      Message.success('保存成功')
      isEditing.value = false
      // 重新加载用户信息
      await loadUserProfile()
      // 更新 store 中的用户信息
      authStore.updateUser({
        username: editForm.username,
        phone: editForm.phone,
        email: editForm.email,
        bio: editForm.bio
      })
    } else {
      Message.error(res.message || '保存失败')
    }
  } catch (error) {
    console.error('保存失败:', error)
    Message.error('保存失败')
  } finally {
    saving.value = false
  }
}

/**
 * 取消编辑
 */
const handleCancel = () => {
  // 恢复原始数据
  editForm.username = user.value.username
  editForm.phone = user.value.phone || ''
  editForm.email = user.value.email || ''
  editForm.bio = user.value.bio || ''
  isEditing.value = false
}

onMounted(() => {
  loadUserProfile()
  loadStats()
})
</script>
