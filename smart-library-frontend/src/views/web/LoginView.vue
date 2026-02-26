<template>
  <div class="min-h-screen flex items-center justify-center bg-canvas px-4 py-12">
    <div class="w-full max-w-md">
      <!-- Logo -->
      <div class="text-center mb-12 animate-fade-in-up">
        <h1 class="text-6xl font-serif text-ink mb-3 tracking-wide">阅墨</h1>
        <p class="text-base text-ink-light tracking-widest">SMART LIBRARY</p>
      </div>

      <!-- 登录/注册卡片 -->
      <div class="bg-white rounded-2xl p-10 animate-fade-in-up animation-delay-500" style="box-shadow: var(--shadow-gallery)">
        <!-- Tab 切换 -->
        <div class="flex gap-2 mb-8 p-1 bg-canvas rounded-xl">
          <button
            @click="isLogin = true"
            :class="[
              'flex-1 py-3 text-base font-medium text-center rounded-lg transition-all duration-300',
              isLogin
                ? 'bg-white text-ink shadow-sm'
                : 'text-ink-light hover:text-ink'
            ]"
          >
            登录
          </button>
          <button
            @click="isLogin = false"
            :class="[
              'flex-1 py-3 text-base font-medium text-center rounded-lg transition-all duration-300',
              !isLogin
                ? 'bg-white text-ink shadow-sm'
                : 'text-ink-light hover:text-ink'
            ]"
          >
            注册
          </button>
        </div>

        <!-- 登录表单 -->
        <form v-if="isLogin" @submit.prevent="handleLogin" class="space-y-5">
          <div>
            <label class="block text-sm font-medium text-ink mb-2">手机号/邮箱</label>
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
        </form>

        <!-- 注册表单 -->
        <form v-else @submit.prevent="handleRegister" class="space-y-5">
          <div>
            <label class="block text-sm font-medium text-ink mb-2">手机号/邮箱</label>
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
              placeholder="请输入密码（6-20位）"
              class="w-full px-4 py-3 text-base bg-canvas border-0 rounded-lg focus:outline-none focus:ring-2 focus:ring-ink-light transition-all"
              required
            />
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
import { login, register } from '@/api/user'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

// 状态
const isLogin = ref(true)
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
      
      // 跳转到首页或之前的页面
      const redirect = router.currentRoute.value.query.redirect || '/'
      router.push(redirect)
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
  // 验证密码
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
      isLogin.value = true
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
</script>
