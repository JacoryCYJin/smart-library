<template>
  <div class="admin-layout min-h-screen bg-canvas flex">
    <!-- Sidebar -->
    <aside class="w-64 bg-white flex-shrink-0" style="box-shadow: 2px 0 8px rgba(16, 42, 67, 0.06)">
      <div class="h-16 flex items-center px-6 border-b border-structure/30">
        <div class="flex items-center gap-2">
          <img :src="logoUrl" alt="Logo" class="h-8 w-8" />
          <span class="font-serif text-xl font-bold text-ink">阅墨管理</span>
        </div>
      </div>

      <nav class="px-4 py-6 space-y-1">
        <router-link
          v-for="item in menuItems"
          :key="item.path"
          :to="item.path"
          custom
          v-slot="{ navigate, isActive }"
        >
          <div
            @click="navigate"
            :class="[
              'flex items-center gap-3 px-4 py-3 rounded-lg cursor-pointer transition-all duration-200',
              isActive
                ? 'bg-ink text-white shadow-sm'
                : 'text-ink-light hover:text-ink hover:bg-canvas'
            ]"
          >
            <component :is="item.icon" class="w-5 h-5" />
            <span class="text-sm font-medium">{{ item.label }}</span>
          </div>
        </router-link>
      </nav>
    </aside>

    <!-- Main Content -->
    <div class="flex-1 flex flex-col min-w-0">
      <!-- Header -->
      <header class="h-16 bg-white flex items-center justify-end px-8" style="box-shadow: 0 1px 3px rgba(16, 42, 67, 0.06)">
        <div class="flex items-center gap-6">
          <button
            @click="handleBackToHome"
            class="flex items-center gap-2 text-sm text-ink-light hover:text-ink transition-colors"
          >
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6" />
            </svg>
            <span>返回首页</span>
          </button>

          <div class="h-4 w-px bg-structure"></div>

          <button
            @click="handleLogout"
            class="flex items-center gap-2 text-sm text-pop hover:opacity-80 transition-opacity"
          >
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
            </svg>
            <span>退出登录</span>
          </button>
        </div>
      </header>

      <!-- Page Content -->
      <main class="flex-1 overflow-auto p-8">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import { h } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { Message } from '@arco-design/web-vue'
import { logout as logoutApi } from '@/api/user'
import logoUrl from '@/assets/images/logo-dark.png'

const router = useRouter()
const authStore = useAuthStore()

// 菜单项
const menuItems = [
  {
    path: '/admin',
    label: '数据概览',
    icon: h('svg', { class: 'w-5 h-5', fill: 'none', stroke: 'currentColor', viewBox: '0 0 24 24' }, [
      h('path', { 'stroke-linecap': 'round', 'stroke-linejoin': 'round', 'stroke-width': '2', d: 'M4 5a1 1 0 011-1h4a1 1 0 011 1v7a1 1 0 01-1 1H5a1 1 0 01-1-1V5zM14 5a1 1 0 011-1h4a1 1 0 011 1v3a1 1 0 01-1 1h-4a1 1 0 01-1-1V5zM4 16a1 1 0 011-1h4a1 1 0 011 1v3a1 1 0 01-1 1H5a1 1 0 01-1-1v-3zM14 13a1 1 0 011-1h4a1 1 0 011 1v7a1 1 0 01-1 1h-4a1 1 0 01-1-1v-7z' })
    ])
  },
  {
    path: '/admin/users',
    label: '用户管理',
    icon: h('svg', { class: 'w-5 h-5', fill: 'none', stroke: 'currentColor', viewBox: '0 0 24 24' }, [
      h('path', { 'stroke-linecap': 'round', 'stroke-linejoin': 'round', 'stroke-width': '2', d: 'M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z' })
    ])
  },
  {
    path: '/admin/resources',
    label: '资源管理',
    icon: h('svg', { class: 'w-5 h-5', fill: 'none', stroke: 'currentColor', viewBox: '0 0 24 24' }, [
      h('path', { 'stroke-linecap': 'round', 'stroke-linejoin': 'round', 'stroke-width': '2', d: 'M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253' })
    ])
  },
  {
    path: '/admin/comments',
    label: '评论管理',
    icon: h('svg', { class: 'w-5 h-5', fill: 'none', stroke: 'currentColor', viewBox: '0 0 24 24' }, [
      h('path', { 'stroke-linecap': 'round', 'stroke-linejoin': 'round', 'stroke-width': '2', d: 'M7 8h10M7 12h4m1 8l-4-4H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-3l-4 4z' })
    ])
  },
  {
    path: '/admin/categories',
    label: '分类管理',
    icon: h('svg', { class: 'w-5 h-5', fill: 'none', stroke: 'currentColor', viewBox: '0 0 24 24' }, [
      h('path', { 'stroke-linecap': 'round', 'stroke-linejoin': 'round', 'stroke-width': '2', d: 'M7 7h.01M7 3h5c.512 0 1.024.195 1.414.586l7 7a2 2 0 010 2.828l-7 7a2 2 0 01-2.828 0l-7-7A1.994 1.994 0 013 12V7a4 4 0 014-4z' })
    ])
  },
  {
    path: '/admin/authors',
    label: '作者管理',
    icon: h('svg', { class: 'w-5 h-5', fill: 'none', stroke: 'currentColor', viewBox: '0 0 24 24' }, [
      h('path', { 'stroke-linecap': 'round', 'stroke-linejoin': 'round', 'stroke-width': '2', d: 'M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z' })
    ])
  },
  {
    path: '/admin/links',
    label: '链接管理',
    icon: h('svg', { class: 'w-5 h-5', fill: 'none', stroke: 'currentColor', viewBox: '0 0 24 24' }, [
      h('path', { 'stroke-linecap': 'round', 'stroke-linejoin': 'round', 'stroke-width': '2', d: 'M13.828 10.172a4 4 0 00-5.656 0l-4 4a4 4 0 105.656 5.656l1.102-1.101m-.758-4.899a4 4 0 005.656 0l4-4a4 4 0 00-5.656-5.656l-1.1 1.1' })
    ])
  },
  {
    path: '/admin/graphs',
    label: 'AI 图谱管理',
    icon: h('svg', { class: 'w-5 h-5', fill: 'none', stroke: 'currentColor', viewBox: '0 0 24 24' }, [
      h('path', { 'stroke-linecap': 'round', 'stroke-linejoin': 'round', 'stroke-width': '2', d: 'M13 10V3L4 14h7v7l9-11h-7z' })
    ])
  },
  {
    path: '/admin/ranking',
    label: '排行榜',
    icon: h('svg', { class: 'w-5 h-5', fill: 'none', stroke: 'currentColor', viewBox: '0 0 24 24' }, [
      h('path', { 'stroke-linecap': 'round', 'stroke-linejoin': 'round', 'stroke-width': '2', d: 'M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z' })
    ])
  }
]

// 返回首页
const handleBackToHome = () => {
  router.push('/')
}

// 退出登录
const handleLogout = async () => {
  try {
    await logoutApi()
    authStore.logout()
    Message.success('退出登录成功')
    router.push('/login')
  } catch (error) {
    console.error('退出登录失败:', error)
    authStore.logout()
    router.push('/login')
  }
}
</script>
