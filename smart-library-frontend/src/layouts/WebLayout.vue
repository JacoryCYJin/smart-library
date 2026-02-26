<script setup>
import { computed, onMounted, onUnmounted, ref, watch, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Lenis from 'lenis'
import gsap from 'gsap'
import { ScrollTrigger } from 'gsap/ScrollTrigger'
import { useLocaleStore } from '@/stores/locale'
import { useAuthStore } from '@/stores/auth'
import { logout as logoutApi } from '@/api/user'
import { Message } from '@arco-design/web-vue'
import AppFooter from '@/components/layout/Footer.vue'
import logoUrl from '@/assets/images/logo-light.png'

// 注册 GSAP 插件
gsap.registerPlugin(ScrollTrigger)

const route = useRoute()
const router = useRouter()
let lenis

const localeStore = useLocaleStore()
const authStore = useAuthStore()
const currentLang = computed(() => localeStore.currentLang)
const isLoggedIn = computed(() => authStore.isLoggedIn)
const username = computed(() => authStore.username)

const labels = computed(() => {
  if (currentLang.value === 'en') {
    return {
      siteName: 'InkSight',
      home: 'Home',
      explore: 'Explore Books',
      serendipity: 'Serendipity',
      research: 'Research',
      search: 'Search...',
      login: 'Login',
      register: 'Sign up',
      logout: 'Logout',
      profile: 'Profile',
    }
  }

  return {
    siteName: '阅墨',
    home: '首页',
    explore: '藏书',
    serendipity: '偶遇',
    research: '文献',
    search: '搜索...',
    login: '登录',
    register: '注册',
    logout: '退出',
    profile: '个人中心',
  }
})

const notificationCount = ref(0)
const hasNotifications = computed(() => notificationCount.value > 0)
const showUserMenu = ref(false)

/**
 * 退出登录
 */
async function handleLogout() {
  try {
    // 调用后端 API 将 token 加入黑名单
    await logoutApi()
    
    // 清除前端状态
    authStore.logout()
    showUserMenu.value = false
    
    Message.success('退出登录成功')
    router.push('/')
  } catch (error) {
    console.error('退出登录失败:', error)
    // 即使后端失败,也清除前端状态
    authStore.logout()
    showUserMenu.value = false
    router.push('/')
  }
}

/**
 * 切换用户菜单
 */
function toggleUserMenu() {
  showUserMenu.value = !showUserMenu.value
}

/**
 * 点击外部关闭菜单
 */
function handleClickOutside(event) {
  const userMenu = document.querySelector('.user-menu-container')
  if (userMenu && !userMenu.contains(event.target)) {
    showUserMenu.value = false
  }
}

onMounted(() => {
  // 1. 初始化 Lenis 平滑滚动
  lenis = new Lenis({
    duration: 1.2, // 滚动速度 (越大越慢/平滑)
    easing: (t) => Math.min(1, 1.001 - Math.pow(2, -10 * t)), // 惯性效果算法
    smoothWheel: true,
  })

  // 2. 将 Lenis 的滚动事件同步给 GSAP
  // (必须这样做，否则 GSAP 的 ScrollTrigger 会因为监测不到滚动而失效)
  lenis.on('scroll', ScrollTrigger.update)

  // 3. 将 Lenis 的刷新挂载到 GSAP 的 Ticker 上，保证动画帧率同步
  gsap.ticker.add((time) => {
    lenis.raf(time * 1000)
  })

  // 4. 关闭 GSAP 自带的平滑处理，防止冲突
  gsap.ticker.lagSmoothing(0)
  
  // 5. 添加点击外部关闭菜单的监听
  document.addEventListener('click', handleClickOutside)
})

// 监听路由变化，滚动到顶部
watch(() => route.path, (newPath, oldPath) => {
  if (lenis) {
    // 如果是从详情页返回列表页，不做任何滚动操作（保持步骤1的位置）
    const isBackToList = oldPath?.startsWith('/book/') && newPath === '/book'
    
    if (!isBackToList) {
      // 其他情况（包括进入详情页）滚动到顶部
      // 使用 nextTick 确保 DOM 更新后再滚动
      nextTick(() => {
        lenis.scrollTo(0, { immediate: true })
      })
    }
  }
})

onUnmounted(() => {
  // 页面销毁时清理，防止内存泄漏
  if (lenis) {
    lenis.destroy()
    gsap.ticker.remove(lenis.raf)
  }
  
  // 清理事件监听
  document.removeEventListener('click', handleClickOutside)
})
</script>

<template>
  <div class="web-layout min-h-screen bg-canvas flex flex-col font-sans text-ink">
    <header class="fixed top-0 z-50 w-full h-16 bg-white shadow-sm">
      <div class="mx-auto h-full max-w-7xl px-2 sm:px-3 lg:px-4">
        <div class="h-full flex items-center justify-between gap-4">
          <div class="flex items-center min-w-[140px]">
            <a
              href="/"
              class="inline-flex items-center gap-2 font-serif text-2xl font-bold text-ink"
            >
              <img :src="logoUrl" alt="Logo" class="h-8 w-8 flex-shrink-0" />
              <span>{{ labels.siteName }}</span>
            </a>
          </div>

          <nav class="hidden md:flex items-center justify-center flex-1">
            <div class="flex items-center gap-8">
              <a
                href="/"
                class="px-3 py-2 rounded-md text-base text-ink font-medium hover:text-[#627D98] hover:bg-slate-50"
              >
                {{ labels.home }}
              </a>
              <a
                href="/book"
                class="px-3 py-2 rounded-md text-base text-ink font-medium hover:text-[#627D98] hover:bg-slate-50"
              >
                {{ labels.explore }}
              </a>
              <a
                href="#"
                class="px-3 py-2 rounded-md text-base text-ink font-medium hover:text-[#627D98] hover:bg-slate-50"
              >
                {{ labels.research }}
              </a>
              <a
                href="#"
                class="px-3 py-2 rounded-md text-base text-ink font-medium hover:text-[#627D98] hover:bg-slate-50"
              >
                <span class="inline-flex items-center gap-1">
                  <svg
                    viewBox="0 0 24 24"
                    fill="none"
                    stroke="currentColor"
                    class="h-4 w-4 text-[#627D98]"
                    stroke-width="2"
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    aria-hidden="true"
                  >
                    <path d="M12 2l1.6 4.8L18 8.4l-4.4 1.6L12 15l-1.6-5L6 8.4l4.4-1.6L12 2z" />
                    <path d="M19 12l.8 2.4L22 15.2l-2.2.8L19 18l-.8-2.4L16 14.8l2.2-.8L19 12z" />
                  </svg>
                  <span>{{ labels.serendipity }}</span>
                </span>
              </a>
            </div>
          </nav>

          <div class="flex items-center justify-end gap-4 min-w-0">
            <div
              class="hidden sm:flex flex-1 min-w-0 max-w-[220px] items-center gap-2 bg-slate-100 rounded-full px-4 py-2"
            >
              <svg
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                class="h-5 w-5 text-[#627D98]"
                stroke-width="2"
                stroke-linecap="round"
                stroke-linejoin="round"
                aria-hidden="true"
              >
                <circle cx="11" cy="11" r="8" />
                <path d="m21 21-4.3-4.3" />
              </svg>
              <input
                class="w-full min-w-0 bg-transparent outline-none text-base text-ink placeholder:text-slate-400"
                type="text"
                :placeholder="labels.search"
              />
            </div>

            <button
              type="button"
              class="w-10 h-10 flex items-center justify-center flex-shrink-0 rounded-full border border-slate-200 text-base font-medium hover:bg-slate-50"
              @click="localeStore.toggleLang"
              aria-label="Toggle language"
            >
              {{ localeStore.currentLang === 'zh' ? 'CN' : 'EN' }}
            </button>

            <div class="flex items-center gap-4 flex-shrink-0 whitespace-nowrap">
              <template v-if="!isLoggedIn">
                <RouterLink
                  to="/login"
                  class="text-base font-bold text-ink hover:text-[#627D98] transition-colors"
                >
                  {{ labels.register }}
                </RouterLink>

                <RouterLink
                  to="/login"
                  style="background-color: #102a43"
                  class="inline-flex items-center justify-center px-6 py-2 rounded-full text-base text-white font-bold shadow-sm hover:opacity-90 transition-opacity"
                >
                  {{ labels.login }}
                </RouterLink>
              </template>

              <template v-else>
                <button
                  type="button"
                  class="relative p-2 rounded-full hover:bg-slate-50 text-ink transition-colors"
                  aria-label="Notifications"
                >
                  <svg
                    viewBox="0 0 24 24"
                    fill="none"
                    stroke="currentColor"
                    class="h-6 w-6"
                    stroke-width="2"
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    aria-hidden="true"
                  >
                    <path d="M18 8a6 6 0 0 0-12 0c0 7-3 7-3 7h18s-3 0-3-7" />
                    <path d="M13.73 21a2 2 0 0 1-3.46 0" />
                  </svg>
                  <span
                    v-if="hasNotifications"
                    class="absolute top-1 right-1 h-2.5 w-2.5 rounded-full bg-pop"
                  />
                </button>

                <!-- 用户菜单 -->
                <div class="relative user-menu-container">
                  <button
                    type="button"
                    @click.stop="toggleUserMenu"
                    class="flex items-center gap-2 p-1 rounded-full hover:bg-slate-50 transition-colors"
                    aria-label="User menu"
                  >
                    <div class="h-9 w-9 rounded-full bg-ink text-white flex items-center justify-center text-sm font-semibold">
                      {{ username.charAt(0).toUpperCase() }}
                    </div>
                  </button>

                  <!-- 下拉菜单 -->
                  <transition
                    enter-active-class="transition ease-out duration-200"
                    enter-from-class="opacity-0 translate-y-1"
                    enter-to-class="opacity-100 translate-y-0"
                    leave-active-class="transition ease-in duration-150"
                    leave-from-class="opacity-100 translate-y-0"
                    leave-to-class="opacity-0 translate-y-1"
                  >
                    <div
                      v-if="showUserMenu"
                      class="absolute right-0 mt-2 w-56 rounded-xl bg-white shadow-lg ring-1 ring-black ring-opacity-5 overflow-hidden"
                      style="box-shadow: var(--shadow-gallery)"
                    >
                      <!-- 用户信息 -->
                      <div class="px-4 py-3 border-b border-structure">
                        <p class="text-sm font-medium text-ink">{{ username }}</p>
                      </div>

                      <!-- 菜单项 -->
                      <div class="py-1">
                        <button
                          type="button"
                          class="w-full px-4 py-2.5 text-left text-sm text-ink hover:bg-canvas transition-colors flex items-center gap-3"
                        >
                          <svg class="w-5 h-5 text-ink-light" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                          </svg>
                          {{ labels.profile }}
                        </button>
                        
                        <button
                          type="button"
                          class="w-full px-4 py-2.5 text-left text-sm text-ink hover:bg-canvas transition-colors flex items-center gap-3"
                        >
                          <svg class="w-5 h-5 text-ink-light" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" />
                          </svg>
                          我的收藏
                        </button>
                        
                        <button
                          type="button"
                          class="w-full px-4 py-2.5 text-left text-sm text-ink hover:bg-canvas transition-colors flex items-center gap-3"
                        >
                          <svg class="w-5 h-5 text-ink-light" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                          </svg>
                          浏览历史
                        </button>
                      </div>

                      <!-- 退出登录 -->
                      <div class="border-t border-structure">
                        <button
                          type="button"
                          @click="handleLogout"
                          class="w-full px-4 py-2.5 text-left text-sm text-pop hover:bg-canvas transition-colors flex items-center gap-3"
                        >
                          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
                          </svg>
                          {{ labels.logout }}
                        </button>
                      </div>
                    </div>
                  </transition>
                </div>
              </template>
            </div>
          </div>
        </div>
      </div>
    </header>

    <main class="flex-grow pt-16">
      <slot />
    </main>

    <AppFooter />
  </div>
</template>

<style scoped>
/* 如果有特殊样式写这里，但尽量用 Tailwind 类名 */
</style>
