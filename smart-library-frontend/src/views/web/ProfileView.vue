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
            <div v-if="user.avatarUrl" class="w-24 h-24 rounded-full overflow-hidden">
              <img :src="user.avatarUrl" :alt="user.username" class="w-full h-full object-cover" />
            </div>
            <div v-else class="w-24 h-24 rounded-full bg-structure flex items-center justify-center text-3xl text-ink-light">
              {{ user.username?.charAt(0) || '用' }}
            </div>
          </div>

          <!-- 用户信息 -->
          <div class="flex-1">
            <h2 class="text-2xl font-bold text-ink mb-2">{{ user.username }}</h2>
            
            <div class="flex gap-4 text-sm text-ink-light mb-4">
              <div>
                <span class="font-medium">注册时间：</span>
                {{ formatDate(user.ctime) }}
              </div>
            </div>

            <!-- 个人简介 -->
            <div v-if="user.bio" class="text-sm text-ink-light">
              {{ user.bio }}
            </div>
          </div>

          <!-- 操作按钮 -->
          <div class="flex gap-2">
            <button
              @click="isEditing = true"
              class="px-4 py-2 bg-pop text-white rounded-lg hover:opacity-90 transition-opacity"
            >
              编辑资料
            </button>
            <button
              @click="isViewingSecurity = true"
              class="px-4 py-2 bg-ink text-white rounded-lg hover:opacity-90 transition-opacity"
            >
              账号安全
            </button>
          </div>
        </div>
      </div>

      <!-- 统计卡片 -->
      <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div 
          @click="$router.push('/favorites')" 
          class="bg-white rounded-xl shadow-sm p-6 text-center cursor-pointer"
        >
          <div class="text-3xl font-bold text-pop mb-2">{{ stats.favoriteCount }}</div>
          <div class="text-ink-light">我的收藏</div>
        </div>
        <div 
          @click="$router.push('/history')" 
          class="bg-white rounded-xl shadow-sm p-6 text-center cursor-pointer"
        >
          <div class="text-3xl font-bold text-pop mb-2">{{ stats.historyCount }}</div>
          <div class="text-ink-light">浏览历史</div>
        </div>
        <div class="bg-white rounded-xl shadow-sm p-6 text-center">
          <div class="text-3xl font-bold text-pop mb-2">{{ stats.commentCount || 0 }}</div>
          <div class="text-ink-light">我的评论</div>
        </div>
      </div>
    </div>

    <!-- 账号安全弹窗 -->
    <a-modal
      v-model:visible="isViewingSecurity"
      title="账号安全"
      :footer="false"
      width="500px"
    >
      <div class="space-y-4">
        <!-- 登录密码 -->
        <div class="flex items-center justify-between py-3 border-b border-structure">
          <div class="flex-1">
            <div class="text-sm font-medium text-ink mb-1">登录密码</div>
            <div class="text-xs text-ink-light">定期更换密码可以提高账号安全性</div>
          </div>
          <button
            @click="isChangingPassword = true; isViewingSecurity = false"
            class="px-4 py-2 text-sm text-pop hover:bg-structure rounded-lg transition-colors"
          >
            修改
          </button>
        </div>

        <!-- 手机号 -->
        <div class="flex items-center justify-between py-3 border-b border-structure">
          <div class="flex-1">
            <div class="text-sm font-medium text-ink mb-1">手机号</div>
            <div class="text-xs text-ink-light">{{ maskPhone(user.phone) }}</div>
          </div>
          <button
            @click="isChangingPhone = true; isViewingSecurity = false"
            class="px-4 py-2 text-sm text-pop hover:bg-structure rounded-lg transition-colors"
          >
            修改
          </button>
        </div>

        <!-- 邮箱 -->
        <div class="flex items-center justify-between py-3">
          <div class="flex-1">
            <div class="text-sm font-medium text-ink mb-1">邮箱地址</div>
            <div class="text-xs text-ink-light">{{ maskEmail(user.email) }}</div>
          </div>
          <button
            @click="isChangingEmail = true; isViewingSecurity = false"
            class="px-4 py-2 text-sm text-pop hover:bg-structure rounded-lg transition-colors"
          >
            修改
          </button>
        </div>
      </div>
    </a-modal>

    <!-- 编辑资料弹窗 -->
    <a-modal
      v-model:visible="isEditing"
      title="编辑资料"
      @ok="handleSave"
      @cancel="handleCancel"
      :ok-loading="saving"
      width="500px"
    >
      <a-form :model="editForm" layout="vertical">
        <a-form-item label="头像">
          <div class="flex items-center gap-4">
            <div v-if="editForm.avatarUrl" class="w-20 h-20 rounded-full overflow-hidden border-2 border-structure">
              <img :src="editForm.avatarUrl" alt="头像预览" class="w-full h-full object-cover" />
            </div>
            <div v-else class="w-20 h-20 rounded-full bg-structure flex items-center justify-center text-2xl text-ink-light border-2 border-structure">
              {{ editForm.username?.charAt(0) || '用' }}
            </div>
            <a-upload
              :custom-request="handleAvatarUpload"
              :show-file-list="false"
              accept="image/*"
              :loading="uploadingAvatar"
            >
              <a-button type="outline" :loading="uploadingAvatar">
                {{ uploadingAvatar ? '上传中...' : '选择头像' }}
              </a-button>
            </a-upload>
          </div>
          <div class="text-xs text-ink-light mt-2">支持 JPG、PNG 格式，文件大小不超过 2MB</div>
        </a-form-item>

        <a-form-item label="用户名">
          <a-input v-model="editForm.username" placeholder="请输入用户名" />
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

    <!-- 修改密码弹窗 -->
    <a-modal
      v-model:visible="isChangingPassword"
      title="修改密码"
      @ok="handleChangePassword"
      @cancel="handleCancelPassword"
      :ok-loading="changingPassword"
      width="450px"
    >
      <a-form :model="passwordForm" layout="vertical">
        <a-form-item label="旧密码">
          <a-input-password v-model="passwordForm.oldPassword" placeholder="请输入旧密码" />
        </a-form-item>
        
        <a-form-item label="新密码">
          <a-input-password v-model="passwordForm.newPassword" placeholder="请输入新密码（6-20位）" />
        </a-form-item>
        
        <a-form-item label="确认新密码">
          <a-input-password v-model="passwordForm.confirmPassword" placeholder="请再次输入新密码" />
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 修改手机号弹窗 -->
    <a-modal
      v-model:visible="isChangingPhone"
      title="修改手机号"
      @ok="handleChangePhone"
      @cancel="handleCancelPhone"
      :ok-loading="changingPhone"
      width="450px"
    >
      <a-form :model="phoneForm" layout="vertical">
        <a-form-item label="当前手机号">
          <a-input v-model="phoneForm.oldPhone" placeholder="请输入当前手机号" />
        </a-form-item>
        
        <a-form-item label="新手机号">
          <a-input v-model="phoneForm.newPhone" placeholder="请输入新手机号" />
        </a-form-item>
        
        <a-form-item label="密码确认">
          <a-input-password v-model="phoneForm.password" placeholder="请输入密码以确认修改" />
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 修改邮箱弹窗 -->
    <a-modal
      v-model:visible="isChangingEmail"
      title="修改邮箱"
      @ok="handleChangeEmail"
      @cancel="handleCancelEmail"
      :ok-loading="changingEmail"
      width="450px"
    >
      <a-form :model="emailForm" layout="vertical">
        <a-form-item label="当前邮箱">
          <a-input v-model="emailForm.oldEmail" placeholder="请输入当前邮箱" />
        </a-form-item>
        
        <a-form-item label="新邮箱">
          <a-input v-model="emailForm.newEmail" placeholder="请输入新邮箱" />
        </a-form-item>
        
        <a-form-item label="密码确认">
          <a-input-password v-model="emailForm.password" placeholder="请输入密码以确认修改" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { getProfile, updateProfile, countUserComments, changePassword, changePhone, changeEmail } from '@/api/user'
import { countUserFavorites } from '@/api/favorite'
import { countBrowseHistory } from '@/api/history'
import { uploadFile } from '@/api/minio'
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
const uploadingAvatar = ref(false)

// 账号安全弹窗状态
const isViewingSecurity = ref(false)

// 修改密码状态
const isChangingPassword = ref(false)
const changingPassword = ref(false)

// 修改手机号状态
const isChangingPhone = ref(false)
const changingPhone = ref(false)

// 修改邮箱状态
const isChangingEmail = ref(false)
const changingEmail = ref(false)

// 编辑表单（移除手机号和邮箱）
const editForm = reactive({
  username: '',
  bio: '',
  avatarUrl: ''
})

// 修改密码表单
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 修改手机号表单
const phoneForm = reactive({
  oldPhone: '',
  newPhone: '',
  password: ''
})

// 修改邮箱表单
const emailForm = reactive({
  oldEmail: '',
  newEmail: '',
  password: ''
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
 * 脱敏手机号（保留前3位和后4位）
 */
const maskPhone = (phone) => {
  if (!phone) return '未设置'
  if (phone.length === 11) {
    return phone.substring(0, 3) + '****' + phone.substring(7)
  }
  return phone
}

/**
 * 脱敏邮箱（保留前2位和@后的域名）
 */
const maskEmail = (email) => {
  if (!email) return '未设置'
  const atIndex = email.indexOf('@')
  if (atIndex > 2) {
    const prefix = email.substring(0, 2)
    const domain = email.substring(atIndex)
    return prefix + '***' + domain
  }
  return email
}

/**
 * 加载用户信息
 */
const loadUserProfile = async () => {
  try {
    const res = await getProfile()
    
    if (res.code === 0) {
      user.value = res.data
      
      // 初始化编辑表单（只包含用户名、简介、头像）
      editForm.username = res.data.username
      editForm.bio = res.data.bio || ''
      editForm.avatarUrl = res.data.avatarUrl || ''
    }
  } catch (error) {
    console.error('加载用户信息失败:', error)
    Message.error('加载用户信息失败')
  }
}

/**
 * 处理头像上传
 */
const handleAvatarUpload = async (options) => {
  // Arco Design 的 custom-request 中，fileItem.file 才是真正的 File 对象
  const file = options.fileItem.file
  
  if (!file) {
    Message.error('无法获取文件')
    return
  }
  
  // 验证文件类型
  if (!file.type.startsWith('image/')) {
    Message.error('请上传图片文件')
    return
  }
  
  // 验证文件大小（限制2MB）
  if (file.size > 2 * 1024 * 1024) {
    Message.error('图片大小不能超过2MB')
    return
  }
  
  uploadingAvatar.value = true
  
  try {
    // 创建本地预览 URL
    const localUrl = URL.createObjectURL(file)
    editForm.avatarUrl = localUrl
    
    // 上传到服务器
    const formData = new FormData()
    formData.append('file', file)
    formData.append('bucketName', 'avatar')
    
    const res = await uploadFile(formData)
    
    // 释放本地 URL
    URL.revokeObjectURL(localUrl)
    
    if (res.code === 0) {
      // 上传成功后使用服务器返回的 URL
      editForm.avatarUrl = res.data
      Message.success('头像上传成功')
    } else {
      Message.error(res.message || '头像上传失败')
      // 恢复原头像
      editForm.avatarUrl = user.value.avatarUrl || ''
    }
  } catch (error) {
    console.error('头像上传失败:', error)
    Message.error('头像上传失败')
    // 恢复原头像
    editForm.avatarUrl = user.value.avatarUrl || ''
  } finally {
    uploadingAvatar.value = false
  }
}

/**
 * 加载统计数据
 */
const loadStats = async () => {
  try {
    // 加载收藏数量
    const favoriteRes = await countUserFavorites()
    if (favoriteRes.code === 0) {
      stats.favoriteCount = favoriteRes.data
    }

    // 加载浏览历史数量
    const historyRes = await countBrowseHistory()
    if (historyRes.code === 0) {
      stats.historyCount = historyRes.data
    }

    // 加载评论数量
    const commentRes = await countUserComments()
    if (commentRes.code === 0) {
      stats.commentCount = commentRes.data
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
    
    if (res.code === 0) {
      Message.success('保存成功')
      isEditing.value = false
      
      // 重新加载用户信息
      await loadUserProfile()
      
      // 更新 store 中的用户信息
      authStore.updateUser({
        username: editForm.username,
        bio: editForm.bio,
        avatarUrl: editForm.avatarUrl
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
  editForm.bio = user.value.bio || ''
  editForm.avatarUrl = user.value.avatarUrl || ''
  isEditing.value = false
}

/**
 * 修改密码
 */
const handleChangePassword = async () => {
  // 验证表单
  if (!passwordForm.oldPassword) {
    Message.error('请输入旧密码')
    return
  }
  if (!passwordForm.newPassword) {
    Message.error('请输入新密码')
    return
  }
  if (passwordForm.newPassword.length < 6 || passwordForm.newPassword.length > 20) {
    Message.error('新密码长度应为6-20位')
    return
  }
  if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    Message.error('两次输入的密码不一致')
    return
  }
  if (passwordForm.oldPassword === passwordForm.newPassword) {
    Message.error('新密码不能与旧密码相同')
    return
  }

  changingPassword.value = true
  try {
    const res = await changePassword({
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    })
    if (res.code === 0) {
      Message.success('密码修改成功，请重新登录')
      isChangingPassword.value = false
      // 清空表单
      passwordForm.oldPassword = ''
      passwordForm.newPassword = ''
      passwordForm.confirmPassword = ''
      // 退出登录
      setTimeout(() => {
        authStore.logout()
      }, 1500)
    } else {
      Message.error(res.message || '密码修改失败')
    }
  } catch (error) {
    console.error('密码修改失败:', error)
    Message.error('密码修改失败')
  } finally {
    changingPassword.value = false
  }
}

/**
 * 取消修改密码
 */
const handleCancelPassword = () => {
  passwordForm.oldPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
  isChangingPassword.value = false
}

/**
 * 修改手机号
 */
const handleChangePhone = async () => {
  // 验证表单
  if (!phoneForm.oldPhone) {
    Message.error('请输入当前手机号')
    return
  }
  if (!phoneForm.newPhone) {
    Message.error('请输入新手机号')
    return
  }
  if (!/^1[3-9]\d{9}$/.test(phoneForm.newPhone)) {
    Message.error('请输入正确的手机号格式')
    return
  }
  if (!phoneForm.password) {
    Message.error('请输入密码以确认修改')
    return
  }
  if (phoneForm.oldPhone === phoneForm.newPhone) {
    Message.error('新手机号不能与当前手机号相同')
    return
  }

  changingPhone.value = true
  try {
    const res = await changePhone({
      oldPhone: phoneForm.oldPhone,
      newPhone: phoneForm.newPhone,
      password: phoneForm.password
    })
    if (res.code === 0) {
      Message.success('手机号修改成功')
      isChangingPhone.value = false
      // 清空表单
      phoneForm.oldPhone = ''
      phoneForm.newPhone = ''
      phoneForm.password = ''
      // 重新加载用户信息
      await loadUserProfile()
    } else {
      Message.error(res.message || '手机号修改失败')
    }
  } catch (error) {
    console.error('手机号修改失败:', error)
    Message.error('手机号修改失败')
  } finally {
    changingPhone.value = false
  }
}

/**
 * 取消修改手机号
 */
const handleCancelPhone = () => {
  phoneForm.oldPhone = ''
  phoneForm.newPhone = ''
  phoneForm.password = ''
  isChangingPhone.value = false
}

/**
 * 修改邮箱
 */
const handleChangeEmail = async () => {
  // 验证表单
  if (!emailForm.oldEmail) {
    Message.error('请输入当前邮箱')
    return
  }
  if (!emailForm.newEmail) {
    Message.error('请输入新邮箱')
    return
  }
  if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(emailForm.newEmail)) {
    Message.error('请输入正确的邮箱格式')
    return
  }
  if (!emailForm.password) {
    Message.error('请输入密码以确认修改')
    return
  }
  if (emailForm.oldEmail === emailForm.newEmail) {
    Message.error('新邮箱不能与当前邮箱相同')
    return
  }

  changingEmail.value = true
  try {
    const res = await changeEmail({
      oldEmail: emailForm.oldEmail,
      newEmail: emailForm.newEmail,
      password: emailForm.password
    })
    if (res.code === 0) {
      Message.success('邮箱修改成功')
      isChangingEmail.value = false
      // 清空表单
      emailForm.oldEmail = ''
      emailForm.newEmail = ''
      emailForm.password = ''
      // 重新加载用户信息
      await loadUserProfile()
    } else {
      Message.error(res.message || '邮箱修改失败')
    }
  } catch (error) {
    console.error('邮箱修改失败:', error)
    Message.error('邮箱修改失败')
  } finally {
    changingEmail.value = false
  }
}

/**
 * 取消修改邮箱
 */
const handleCancelEmail = () => {
  emailForm.oldEmail = ''
  emailForm.newEmail = ''
  emailForm.password = ''
  isChangingEmail.value = false
}

onMounted(() => {
  loadUserProfile()
  loadStats()
})
</script>
