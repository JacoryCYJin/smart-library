<template>
  <div class="admin-layout min-h-screen bg-canvas flex flex-col">
    <!-- Top Navbar -->
    <header class="fixed top-0 z-50 w-full h-16 bg-white shadow-sm">
      <div class="h-full px-6 flex items-center justify-between">
        <!-- Logo -->
        <div class="flex items-center gap-3">
          <img :src="logoUrl" alt="阅墨 Logo" class="h-8 w-8" />
          <span class="font-serif text-xl font-bold text-ink">阅墨 · 管理后台</span>
        </div>

        <!-- Right Section -->
        <div class="flex items-center gap-6">
          <!-- 返回前台 -->
          <RouterLink
            to="/"
            class="flex items-center gap-2 text-sm text-ink-light hover:text-ink transition-colors"
          >
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6" />
            </svg>
            <span>返回前台</span>
          </RouterLink>

          <div class="h-4 w-px bg-structure"></div>

          <!-- 用户信息 -->
          <div class="flex items-center gap-3">
            <div v-if="authStore.avatarUrl" class="w-8 h-8 rounded-full overflow-hidden">
              <img :src="authStore.avatarUrl" alt="头像" class="w-full h-full object-cover" />
            </div>
            <div v-else class="w-8 h-8 rounded-full bg-ink text-white flex items-center justify-center text-sm font-medium">
              {{ authStore.username.charAt(0).toUpperCase() }}
            </div>
            <span class="text-sm font-medium text-ink">{{ authStore.username }}</span>
          </div>

          <div class="h-4 w-px bg-structure"></div>

          <!-- 退出登录 -->
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
      </div>
    </header>

    <!-- Main Content Area (below navbar) -->
    <div class="flex flex-1 pt-16">
      <!-- Sidebar -->
      <aside 
        :class="[
          'fixed left-0 top-16 bottom-0 bg-white flex-shrink-0 transition-all duration-300 flex flex-col',
          isCollapsed ? 'w-20' : 'w-64'
        ]" 
        style="box-shadow: 2px 0 8px rgba(16, 42, 67, 0.06)"
      >
        <!-- 导航菜单 -->
        <nav class="flex-1 px-4 py-6 space-y-1 overflow-y-auto">
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
                  : 'text-ink-light hover:text-ink hover:bg-canvas',
                isCollapsed ? 'justify-center' : ''
              ]"
              :title="isCollapsed ? item.label : ''"
            >
              <component :is="item.icon" class="w-5 h-5 flex-shrink-0" />
              <span 
                v-show="!isCollapsed" 
                class="text-sm font-medium transition-opacity duration-300"
              >
                {{ item.label }}
              </span>
            </div>
          </router-link>
        </nav>

        <!-- 折叠按钮（固定在底部） -->
        <div class="px-4 py-4 border-t border-structure">
          <button
            @click="toggleSidebar"
            :class="[
              'w-full flex items-center gap-3 px-4 py-3 rounded-lg cursor-pointer transition-all duration-200',
              'text-ink-light hover:text-ink hover:bg-canvas',
              isCollapsed ? 'justify-center' : ''
            ]"
            :title="isCollapsed ? '展开侧边栏' : '收起侧边栏'"
          >
            <svg 
              class="w-5 h-5 flex-shrink-0 transition-transform duration-300" 
              :class="{ 'rotate-180': isCollapsed }"
              fill="none" 
              stroke="currentColor" 
              viewBox="0 0 24 24"
            >
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 19l-7-7 7-7m8 14l-7-7 7-7" />
            </svg>
            <span 
              v-show="!isCollapsed" 
              class="text-sm font-medium transition-opacity duration-300"
            >
              收起侧边栏
            </span>
          </button>
        </div>
      </aside>

      <!-- Page Content -->
      <main 
        :class="[
          'flex-1 transition-all duration-300',
          isCollapsed ? 'ml-20' : 'ml-64'
        ]"
      >
        <div class="p-8">
          <router-view />
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, h } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { Message } from '@arco-design/web-vue'
import { logout as logoutApi } from '@/api/user'
import logoDark from '@/assets/images/logo-light.png'

const router = useRouter()
const authStore = useAuthStore()

// Sidebar 折叠状态
const isCollapsed = ref(false)

// Logo URL
const logoUrl = logoDark

// 切换侧边栏
const toggleSidebar = () => {
  isCollapsed.value = !isCollapsed.value
}

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
      h('path', { 'stroke-linecap': 'round', 'stroke-linejoin': 'round', 'stroke-width': '2', d: 'M7 12l3-3 3 3 4-4M8 21l4-4 4 4M3 4h18M4 4h16v12a1 1 0 01-1 1H5a1 1 0 01-1-1V4z' })
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
    label: '人物图谱',
    icon: h('svg', { class: 'w-5 h-5', fill: 'none', stroke: 'currentColor', viewBox: '0 0 24 24' }, [
      h('path', { 'stroke-linecap': 'round', 'stroke-linejoin': 'round', 'stroke-width': '2', d: 'M9 20l-5.447-2.724A1 1 0 013 16.382V5.618a1 1 0 011.447-.894L9 7m0 13l6-3m-6 3V7m6 10l4.553 2.276A1 1 0 0021 18.382V7.618a1 1 0 00-.553-.894L15 4m0 13V4m0 0L9 7' })
    ])
  },
  {
    path: '/admin/emotions',
    label: '情感曲线',
    icon: h('svg', { class: 'w-5 h-5', fill: 'none', stroke: 'currentColor', viewBox: '0 0 24 24' }, [
      h('path', { 'stroke-linecap': 'round', 'stroke-linejoin': 'round', 'stroke-width': '2', d: 'M7 12l3-3 3 3 4-4M8 21l4-4 4 4M3 4h18M4 4h16v12a1 1 0 01-1 1H5a1 1 0 01-1-1V4z' })
    ])
  },
  {
    path: '/admin/announcements',
    label: '公告管理',
    icon: h('svg', { class: 'w-5 h-5', fill: 'none', stroke: 'currentColor', viewBox: '0 0 24 24' }, [
      h('path', { 'stroke-linecap': 'round', 'stroke-linejoin': 'round', 'stroke-width': '2', d: 'M11 5.882V19.24a1.76 1.76 0 01-3.417.592l-2.147-6.15M18 13a3 3 0 100-6M5.436 13.683A4.001 4.001 0 017 6h1.832c4.1 0 7.625-1.234 9.168-3v14c-1.543-1.766-5.067-3-9.168-3H7a3.988 3.988 0 01-1.564-.317z' })
    ])
  }
]

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
