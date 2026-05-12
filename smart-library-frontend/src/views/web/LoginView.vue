<template>
  <div class="min-h-screen flex items-center justify-center bg-canvas px-4 py-12">
    <div class="w-full max-w-md">
      <!-- Logo -->
      <div class="text-center mb-8 animate-fade-in-up">
        <h1 class="text-5xl font-serif text-ink mb-2 tracking-wide">阅墨</h1>
        <p class="text-sm text-ink-light tracking-widest">SMART LIBRARY</p>
      </div>

      <!-- 登录/注册/忘记密码卡片 -->
      <div class="bg-white rounded-2xl p-10 animate-fade-in-up animation-delay-500" style="box-shadow: var(--shadow-gallery)">
        <!-- Tab 切换（忘记密码时隐藏） -->
        <div v-if="currentTab !== 'reset'" class="flex gap-2 mb-8 p-1 bg-canvas rounded-xl">
          <button
            @click="currentTab = 'login'"
            :class="[
              'flex-1 py-3 text-base font-medium text-center rounded-lg transition-all duration-300',
              currentTab === 'login'
                ? 'bg-white text-ink shadow-sm'
                : 'text-ink-light hover:text-ink'
            ]"
          >
            登录
          </button>
          <button
            @click="currentTab = 'register'"
            :class="[
              'flex-1 py-3 text-base font-medium text-center rounded-lg transition-all duration-300',
              currentTab === 'register'
                ? 'bg-white text-ink shadow-sm'
                : 'text-ink-light hover:text-ink'
            ]"
          >
            注册
          </button>
        </div>
        
        <!-- 忘记密码标题 -->
        <div v-else class="mb-8">
          <h2 class="text-2xl font-semibold text-ink text-center">重置密码</h2>
        </div>

        <!-- 登录表单 -->
        <form v-if="currentTab === 'login'" @submit.prevent="handleLogin" class="space-y-5">
          <div>
            <label class="block text-sm font-medium text-ink mb-2">手机号 / 邮箱</label>
            <input
              v-model="loginForm.phoneOrEmail"
              type="text"
              placeholder="请输入手机号或邮箱"
              class="w-full px-4 py-3 text-base bg-canvas border-0 rounded-lg focus:outline-none focus:ring-2 focus:ring-ink-light transition-all"
              required
            />
          </div>
          <div>
            <label class="block text-sm font-medium text-ink mb-2">密码</label>
            <input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              class="w-full px-4 py-3 text-base bg-canvas border-0 rounded-lg focus:outline-none focus:ring-2 focus:ring-ink-light transition-all"
              required
            />
          </div>
          <button
            type="submit"
            :disabled="loading"
            class="w-full py-3.5 text-base font-semibold bg-ink text-white rounded-lg hover:bg-ink-light transition-all duration-300 disabled:opacity-50 disabled:cursor-not-allowed shadow-sm"
          >
            {{ loading ? '登录中...' : '登录' }}
          </button>
          
          <!-- 忘记密码提示 -->
          <div class="text-center mt-4">
            <button
              type="button"
              @click="currentTab = 'reset'"
              class="text-sm text-ink-light hover:text-ink transition-colors"
            >
              忘记密码？
            </button>
          </div>
        </form>

        <!-- 注册表单 -->
        <form v-else-if="currentTab === 'register'" @submit.prevent="handleRegister" class="space-y-5">
          <div>
            <label class="block text-sm font-medium text-ink mb-2">手机号 / 邮箱</label>
            <input
              v-model="registerForm.phoneOrEmail"
              type="text"
              placeholder="请输入手机号或邮箱"
              class="w-full px-4 py-3 text-base bg-canvas border-0 rounded-lg focus:outline-none focus:ring-2 focus:ring-ink-light transition-all"
              required
            />
          </div>
          <div>
            <label class="block text-sm font-medium text-ink mb-2">密码</label>
            <input
              v-model="registerForm.password"
              type="password"
              placeholder="请输入密码（需包含字母和数字）"
              class="w-full px-4 py-3 text-base bg-canvas border-0 rounded-lg focus:outline-none focus:ring-2 focus:ring-ink-light transition-all"
              required
            />
            <p class="mt-1.5 text-xs text-ink-light">密码必须同时包含字母和数字</p>
          </div>
          <div>
            <label class="block text-sm font-medium text-ink mb-2">确认密码</label>
            <input
              v-model="registerForm.confirmPassword"
              type="password"
              placeholder="请再次输入密码"
              class="w-full px-4 py-3 text-base bg-canvas border-0 rounded-lg focus:outline-none focus:ring-2 focus:ring-ink-light transition-all"
              required
            />
          </div>
          <button
            type="submit"
            :disabled="loading"
            class="w-full py-3.5 text-base font-semibold bg-ink text-white rounded-lg hover:bg-ink-light transition-all duration-300 disabled:opacity-50 disabled:cursor-not-allowed shadow-sm"
          >
            {{ loading ? '注册中...' : '注册' }}
          </button>
        </form>

        <!-- 忘记密码表单 -->
        <form v-else-if="currentTab === 'reset'" @submit.prevent="handleResetPassword" class="space-y-5">
          <div>
            <label class="block text-sm font-medium text-ink mb-2">手机号 / 邮箱</label>
            <input
              v-model="resetForm.phoneOrEmail"
              type="text"
              placeholder="请输入注册时使用的手机号或邮箱"
              class="w-full px-4 py-3 text-base bg-canvas border-0 rounded-lg focus:outline-none focus:ring-2 focus:ring-ink-light transition-all"
              required
            />
          </div>
          <div>
            <label class="block text-sm font-medium text-ink mb-2">新密码</label>
            <input
              v-model="resetForm.newPassword"
              type="password"
              placeholder="请输入新密码（需包含字母和数字）"
              class="w-full px-4 py-3 text-base bg-canvas border-0 rounded-lg focus:outline-none focus:ring-2 focus:ring-ink-light transition-all"
              required
            />
            <p class="mt-1.5 text-xs text-ink-light">密码必须同时包含字母和数字</p>
          </div>
          <div>
            <label class="block text-sm font-medium text-ink mb-2">确认新密码</label>
            <input
              v-model="resetForm.confirmPassword"
              type="password"
              placeholder="请再次输入新密码"
              class="w-full px-4 py-3 text-base bg-canvas border-0 rounded-lg focus:outline-none focus:ring-2 focus:ring-ink-light transition-all"
              required
            />
          </div>
          <button
            type="submit"
            :disabled="loading"
            class="w-full py-3.5 text-base font-semibold bg-ink text-white rounded-lg hover:bg-ink-light transition-all duration-300 disabled:opacity-50 disabled:cursor-not-allowed shadow-sm"
          >
            {{ loading ? '重置中...' : '重置密码' }}
          </button>
          
          <!-- 返回登录 -->
          <div class="text-center mt-4">
            <button
              type="button"
              @click="currentTab = 'login'"
              class="text-sm text-ink-light hover:text-ink transition-colors"
            >
              返回登录
            </button>
          </div>
        </form>

        <!-- 返回首页 -->
        <div class="mt-8 text-center">
          <router-link to="/" class="text-sm text-ink-light hover:text-ink transition-colors inline-flex items-center gap-2">
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
            </svg>
            返回首页
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { Message } from '@arco-design/web-vue'
import { login, register, resetPassword } from '@/api/user'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

// 状态
const currentTab = ref('login') // 'login' | 'register' | 'reset'
const loading = ref(false)

// 登录表单
const loginForm = ref({
  phoneOrEmail: '',
  password: ''
})

// 注册表单
const registerForm = ref({
  phoneOrEmail: '',
  password: '',
  confirmPassword: ''
})

// 重置密码表单
const resetForm = ref({
  phoneOrEmail: '',
  newPassword: '',
  confirmPassword: ''
})

/**
 * 处理登录
 */
async function handleLogin() {
  loading.value = true
  try {
    const res = await login(loginForm.value)
    if (res.code === 0) {
      // 保存用户信息和 token 到 store
      authStore.login(res.data)
      Message.success('登录成功')
      
      // 根据用户角色跳转
      if (res.data.role === 1) {
        // 管理员跳转到管理后台
        router.push('/admin')
      } else {
        // 普通用户跳转到首页或之前的页面
        const redirect = router.currentRoute.value.query.redirect || '/'
        router.push(redirect)
      }
    } else {
      Message.error(res.message || '登录失败')
    }
  } catch (error) {
    Message.error(error.message || '登录失败')
  } finally {
    loading.value = false
  }
}

/**
 * 处理注册
 */
async function handleRegister() {
  // 验证密码格式
  const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]+$/
  if (!passwordRegex.test(registerForm.value.password)) {
    Message.error('密码必须同时包含字母和数字')
    return
  }
  
  // 验证密码一致性
  if (registerForm.value.password !== registerForm.value.confirmPassword) {
    Message.error('两次输入的密码不一致')
    return
  }
  
  loading.value = true
  try {
    const res = await register(registerForm.value)
    if (res.code === 0) {
      Message.success('注册成功，请登录')
      // 切换到登录表单
      currentTab.value = 'login'
      // 清空注册表单
      registerForm.value = {
        phoneOrEmail: '',
        password: '',
        confirmPassword: ''
      }
    } else {
      Message.error(res.message || '注册失败')
    }
  } catch (error) {
    Message.error(error.message || '注册失败')
  } finally {
    loading.value = false
  }
}

/**
 * 处理重置密码
 */
async function handleResetPassword() {
  // 验证密码格式
  const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]+$/
  if (!passwordRegex.test(resetForm.value.newPassword)) {
    Message.error('密码必须同时包含字母和数字')
    return
  }
  
  // 验证密码一致性
  if (resetForm.value.newPassword !== resetForm.value.confirmPassword) {
    Message.error('两次输入的密码不一致')
    return
  }
  
  loading.value = true
  try {
    const res = await resetPassword(resetForm.value)
    if (res.code === 0) {
      Message.success('密码重置成功，请使用新密码登录')
      // 切换到登录表单
      currentTab.value = 'login'
      // 清空重置表单
      resetForm.value = {
        phoneOrEmail: '',
        newPassword: '',
        confirmPassword: ''
      }
    } else {
      Message.error(res.message || '重置密码失败')
    }
  } catch (error) {
    Message.error(error.message || '重置密码失败')
  } finally {
    loading.value = false
  }
}
</script>
