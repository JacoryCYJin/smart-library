<script setup>
import { computed, onMounted, onUnmounted, ref, watch, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Lenis from 'lenis'
import gsap from 'gsap'
import { ScrollTrigger } from 'gsap/ScrollTrigger'
import { useLocaleStore } from '@/stores/locale'
import { useAuthStore } from '@/stores/auth'
import { logout as logoutApi } from '@/api/user'
import { getUnreadCount, getNotifications, markAsRead, markAllAsRead } from '@/api/notification'
import { globalSearch } from '@/api/search'
import { Message } from '@arco-design/web-vue'
import AppFooter from '@/components/layout/Footer.vue'
import FloatingBookmark from '@/components/bookmark/FloatingBookmark.vue'
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
const avatarUrl = computed(() => authStore.avatarUrl)

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
const showNotificationMenu = ref(false)
const showSearchExpanded = ref(false)
const searchKeyword = ref('')
const searchResults = ref(null)
const searchLoading = ref(false)
const notifications = ref([])
const loadingNotifications = ref(false)



/**
 * 执行搜索（防抖优化）
 */
let searchTimeout = null
async function performSearch() {
  if (!searchKeyword.value || searchKeyword.value.trim().length === 0) {
    searchResults.value = null
    return
  }

  // 清除之前的定时器
  if (searchTimeout) {
    clearTimeout(searchTimeout)
  }

  // 防抖：300ms 后执行搜索
  searchTimeout = setTimeout(async () => {
    searchLoading.value = true
    try {
      const res = await globalSearch({
        keyword: searchKeyword.value.trim(),
        limit: 5
      })
      
      if (res.code === 0) {
        searchResults.value = res.data
      }
    } catch (error) {
      console.error('搜索失败:', error)
    } finally {
      searchLoading.value = false
    }
  }, 300)
}

/**
 * 按 Enter 键跳转到搜索结果页
 */
function handleSearchEnter() {
  if (searchKeyword.value && searchKeyword.value.trim().length > 0) {
    router.push({
      path: '/search',
      query: { keyword: searchKeyword.value.trim() }
    })
    showSearchExpanded.value = false
    searchKeyword.value = ''
    searchResults.value = null
  }
}

/**
 * 监听搜索关键词变化
 */
watch(searchKeyword, () => {
  performSearch()
})

/**
 * 跳转到图书详情
 */
function goToBook(bookId) {
  router.push(`/book/${bookId}`)
  showSearchExpanded.value = false
  searchKeyword.value = ''
  searchResults.value = null
}

/**
 * 跳转到作者详情
 */
function goToAuthor(authorId) {
  router.push(`/author/${authorId}`)
  showSearchExpanded.value = false
  searchKeyword.value = ''
  searchResults.value = null
}

/**
 * 查看更多图书
 */
function viewMoreBooks() {
  router.push({
    path: '/search',
    query: { keyword: searchKeyword.value }
  })
  showSearchExpanded.value = false
  searchKeyword.value = ''
  searchResults.value = null
}

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
 * 加载未读通知数量
 */
async function loadUnreadCount() {
  if (!isLoggedIn.value) return
  
  try {
    const res = await getUnreadCount()
    if (res.code === 0) {
      notificationCount.value = res.data.count
    }
  } catch (error) {
    console.error('加载未读通知数量失败:', error)
  }
}

/**
 * 加载通知列表
 */
async function loadNotifications() {
  if (loadingNotifications.value) return
  
  loadingNotifications.value = true
  try {
    const res = await getNotifications(10)
    if (res.code === 0) {
      notifications.value = res.data
    }
  } catch (error) {
    console.error('加载通知列表失败:', error)
  } finally {
    loadingNotifications.value = false
  }
}

/**
 * 切换通知菜单
 */
async function toggleNotificationMenu() {
  showNotificationMenu.value = !showNotificationMenu.value
  
  if (showNotificationMenu.value && notifications.value.length === 0) {
    await loadNotifications()
  }
}

/**
 * 标记通知为已读
 */
async function handleMarkAsRead(notificationId) {
  try {
    await markAsRead(notificationId)
    
    // 更新本地状态
    const notification = notifications.value.find(n => n.notificationId === notificationId)
    if (notification) {
      notification.isRead = 1
    }
    
    // 更新未读数量
    await loadUnreadCount()
  } catch (error) {
    console.error('标记已读失败:', error)
  }
}

/**
 * 标记全部为已读
 */
async function handleMarkAllAsRead() {
  try {
    await markAllAsRead()
    
    // 更新本地状态
    notifications.value.forEach(n => {
      n.isRead = 1
    })
    
    notificationCount.value = 0
    Message.success('已全部标记为已读')
  } catch (error) {
    console.error('标记全部已读失败:', error)
  }
}

/**
 * 点击通知
 */
function handleNotificationClick(notification) {
  // 标记为已读
  if (notification.isRead === 0) {
    handleMarkAsRead(notification.notificationId)
  }
  
  // 跳转到链接
  if (notification.linkUrl) {
    router.push(notification.linkUrl)
    showNotificationMenu.value = false
  }
}

/**
 * 点击外部关闭菜单和搜索框
 */
function handleClickOutside(event) {
  const userMenu = document.querySelector('.user-menu-container')
  const notificationMenu = document.querySelector('.notification-menu-container')
  const searchContainer = document.querySelector('.search-container')
  
  if (userMenu && !userMenu.contains(event.target)) {
    showUserMenu.value = false
  }
  
  if (notificationMenu && !notificationMenu.contains(event.target)) {
    showNotificationMenu.value = false
  }
  
  // 点击搜索框外部时收起搜索
  if (searchContainer && !searchContainer.contains(event.target) && showSearchExpanded.value) {
    showSearchExpanded.value = false
    searchKeyword.value = ''
    searchResults.value = null
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
  
  // 6. 加载未读通知数量
  loadUnreadCount()
  
  // 7. 定时刷新未读通知数量（每30秒）
  const intervalId = setInterval(loadUnreadCount, 30000)
  
  // 清理定时器
  onUnmounted(() => {
    clearInterval(intervalId)
  })
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
          <!-- 左侧：Logo -->
          <div class="flex items-center min-w-[140px] flex-shrink-0">
            <a
              href="/"
              class="inline-flex items-center gap-2 font-serif text-2xl font-bold text-ink"
            >
              <img :src="logoUrl" alt="Logo" class="h-7 w-7 flex-shrink-0" />
              <span>{{ labels.siteName }}</span>
            </a>
          </div>

          <!-- 中间：导航 -->
          <div class="hidden md:flex items-center justify-center flex-1">
            <!-- 导航链接 -->
            <nav class="flex items-center gap-13 flex-shrink-0">
              <a
                href="/"
                class="px-3 py-2 rounded-md text-base text-ink font-medium hover:text-[#627D98] hover:bg-slate-50 whitespace-nowrap"
              >
                {{ labels.home }}
              </a>
              <a
                href="/book"
                class="px-3 py-2 rounded-md text-base text-ink font-medium hover:text-[#627D98] hover:bg-slate-50 whitespace-nowrap"
              >
                {{ labels.explore }}
              </a>
              <a
                href="/serendipity"
                class="px-3 py-2 rounded-md text-base text-ink font-medium hover:text-[#627D98] hover:bg-slate-50 whitespace-nowrap"
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
            </nav>
          </div>

          <!-- 右侧：搜索框 + 语言切换 + 用户菜单 -->
          <div class="flex items-center justify-end gap-4 flex-shrink-0">
            <!-- 搜索框占位容器（固定宽度，不影响布局） -->
            <div class="relative w-[220px] search-container">
              <!-- 搜索框（绝对定位，向左扩展） -->
              <div 
                class="absolute right-0 top-1/2 -translate-y-1/2 flex items-center"
                style="transition: all 0.7s cubic-bezier(0.4, 0, 0.2, 1);"
                :style="{ width: showSearchExpanded ? '400px' : '220px' }"
              >
                <div class="relative w-full">
                  <!-- 搜索输入框 -->
                  <div class="flex items-center gap-2 bg-slate-100 rounded-full px-4 py-2">
                    <svg
                      viewBox="0 0 24 24"
                      fill="none"
                      stroke="currentColor"
                      class="h-5 w-5 text-[#627D98] flex-shrink-0"
                      stroke-width="2"
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      aria-hidden="true"
                    >
                      <circle cx="11" cy="11" r="8" />
                      <path d="m21 21-4.3-4.3" />
                    </svg>
                    <input
                      v-model="searchKeyword"
                      @focus="showSearchExpanded = true"
                      @keyup.enter="handleSearchEnter"
                      class="search-input w-full bg-transparent outline-none text-base text-ink placeholder:text-slate-400"
                      type="text"
                      :placeholder="labels.search"
                    />
                    <button
                      v-if="searchKeyword"
                      @click="searchKeyword = ''; searchResults = null"
                      class="p-1 rounded-full hover:bg-slate-200 transition-colors flex-shrink-0"
                    >
                      <svg
                        viewBox="0 0 24 24"
                        fill="none"
                        stroke="currentColor"
                        class="h-4 w-4 text-ink-light"
                        stroke-width="2"
                        stroke-linecap="round"
                        stroke-linejoin="round"
                      >
                        <line x1="18" y1="6" x2="6" y2="18" />
                        <line x1="6" y1="6" x2="18" y2="18" />
                      </svg>
                    </button>
                  </div>

                  <!-- 搜索结果下拉框 -->
                  <transition
                    enter-active-class="transition ease-out duration-200"
                    enter-from-class="opacity-0 translate-y-1"
                    enter-to-class="opacity-100 translate-y-0"
                    leave-active-class="transition ease-in duration-150"
                    leave-from-class="opacity-100 translate-y-0"
                    leave-to-class="opacity-0 translate-y-1"
                  >
                    <div
                      v-if="showSearchExpanded && (searchLoading || searchResults)"
                      class="absolute top-full left-0 right-0 mt-2 bg-white rounded-xl shadow-lg ring-1 ring-black ring-opacity-5 overflow-hidden z-50"
                      style="box-shadow: var(--shadow-gallery)"
                    >
                      <!-- 加载中 -->
                      <div v-if="searchLoading" class="py-8 text-center text-ink-light text-sm">
                        {{ currentLang === 'en' ? 'Searching...' : '搜索中...' }}
                      </div>

                      <!-- 无结果 -->
                      <div
                        v-else-if="searchResults && searchResults.books.length === 0 && searchResults.authors.length === 0"
                        class="py-8 text-center text-ink-light text-sm"
                      >
                        {{ currentLang === 'en' ? 'No results found' : '未找到相关结果' }}
                      </div>

                      <!-- 有结果 -->
                      <div v-else-if="searchResults" class="max-h-[60vh] overflow-y-auto">
                        <!-- 图书结果 -->
                        <div v-if="searchResults.books.length > 0" class="py-2">
                          <div class="px-4 py-2 flex items-center justify-between">
                            <h3 class="text-xs font-semibold text-ink-light">
                              {{ currentLang === 'en' ? 'Books' : '图书' }} ({{ searchResults.bookTotal }})
                            </h3>
                            <button
                              v-if="searchResults.bookTotal > searchResults.books.length"
                              @click="viewMoreBooks"
                              class="text-xs text-pop hover:underline"
                            >
                              {{ currentLang === 'en' ? 'View More' : '查看更多' }}
                            </button>
                          </div>
                          <div class="space-y-1">
                            <button
                              v-for="book in searchResults.books"
                              :key="book.resourceId"
                              @click="goToBook(book.resourceId)"
                              class="w-full px-4 py-2 flex items-center gap-3 hover:bg-canvas transition-colors text-left"
                            >
                              <img
                                v-if="book.coverUrl"
                                :src="book.coverUrl"
                                :alt="book.title"
                                class="w-10 h-14 object-cover rounded flex-shrink-0"
                              />
                              <div class="flex-1 min-w-0">
                                <p class="text-sm font-medium text-ink truncate">{{ book.title }}</p>
                                <p v-if="book.authorName" class="text-xs text-ink-light truncate mt-0.5">
                                  {{ book.authorName }}
                                </p>
                              </div>
                            </button>
                          </div>
                        </div>

                        <!-- 作者结果 -->
                        <div v-if="searchResults.authors.length > 0" class="py-2 border-t border-structure">
                          <div class="px-4 py-2">
                            <h3 class="text-xs font-semibold text-ink-light">
                              {{ currentLang === 'en' ? 'Authors' : '作者' }} ({{ searchResults.authorTotal }})
                            </h3>
                          </div>
                          <div class="space-y-1">
                            <button
                              v-for="author in searchResults.authors"
                              :key="author.authorId"
                              @click="goToAuthor(author.authorId)"
                              class="w-full px-4 py-2 flex items-center gap-3 hover:bg-canvas transition-colors text-left"
                            >
                              <div
                                v-if="author.photoUrl"
                                class="w-10 h-10 rounded-full overflow-hidden flex-shrink-0"
                              >
                                <img
                                  :src="author.photoUrl"
                                  :alt="author.name"
                                  class="w-full h-full object-cover"
                                />
                              </div>
                              <div
                                v-else
                                class="w-10 h-10 rounded-full bg-ink text-white flex items-center justify-center text-sm font-semibold flex-shrink-0"
                              >
                                {{ author.name.charAt(0) }}
                              </div>
                              <div class="flex-1 min-w-0">
                                <p class="text-sm font-medium text-ink truncate">{{ author.name }}</p>
                                <p v-if="author.country" class="text-xs text-ink-light truncate mt-0.5">
                                  {{ author.country }}
                                </p>
                              </div>
                            </button>
                          </div>
                        </div>
                      </div>
                    </div>
                  </transition>
                </div>
              </div>
            </div>

            <!-- 语言切换 -->
            <button
              type="button"
              class="w-10 h-10 flex items-center justify-center flex-shrink-0 rounded-full border border-slate-200 text-base font-medium hover:bg-slate-50"
              @click="localeStore.toggleLang"
              aria-label="Toggle language"
            >
              {{ localeStore.currentLang === 'zh' ? 'CN' : 'EN' }}
            </button>

            <!-- 用户菜单区域 -->
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
                <!-- 通知菜单 -->
                <div class="relative notification-menu-container">
                  <button
                    type="button"
                    @click.stop="toggleNotificationMenu"
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

                  <!-- 通知下拉菜单 -->
                  <transition
                    enter-active-class="transition ease-out duration-200"
                    enter-from-class="opacity-0 translate-y-1"
                    enter-to-class="opacity-100 translate-y-0"
                    leave-active-class="transition ease-in duration-150"
                    leave-from-class="opacity-100 translate-y-0"
                    leave-to-class="opacity-0 translate-y-1"
                  >
                    <div
                      v-if="showNotificationMenu"
                      class="absolute right-0 mt-2 w-96 rounded-xl bg-white shadow-lg ring-1 ring-black ring-opacity-5 overflow-hidden"
                      style="box-shadow: var(--shadow-gallery)"
                    >
                      <!-- 标题栏 -->
                      <div class="px-4 py-3 border-b border-structure flex items-center justify-between">
                        <h3 class="text-sm font-semibold text-ink">通知</h3>
                        <button
                          v-if="hasNotifications"
                          @click="handleMarkAllAsRead"
                          class="text-xs text-ink-light hover:text-ink transition-colors"
                        >
                          全部已读
                        </button>
                      </div>

                      <!-- 通知列表 -->
                      <div class="max-h-96 overflow-y-auto">
                        <div v-if="loadingNotifications" class="py-8 text-center text-ink-light">
                          加载中...
                        </div>
                        
                        <div v-else-if="notifications.length === 0" class="py-8 text-center text-ink-light">
                          暂无通知
                        </div>
                        
                        <div v-else>
                          <button
                            v-for="notification in notifications"
                            :key="notification.notificationId"
                            @click="handleNotificationClick(notification)"
                            class="w-full px-4 py-3 text-left hover:bg-canvas transition-colors border-b border-structure last:border-b-0"
                            :class="{ 'bg-blue-50': notification.isRead === 0 }"
                          >
                            <div class="flex items-start gap-3">
                              <div
                                v-if="notification.isRead === 0"
                                class="mt-1.5 h-2 w-2 rounded-full bg-pop flex-shrink-0"
                              />
                              <div v-else class="mt-1.5 h-2 w-2 flex-shrink-0" />
                              
                              <div class="flex-1 min-w-0">
                                <p class="text-sm font-medium text-ink">{{ notification.title }}</p>
                                <p class="mt-1 text-xs text-ink-light line-clamp-2">
                                  {{ notification.content }}
                                </p>
                                <p class="mt-1 text-xs text-ink-light">
                                  {{ new Date(notification.ctime).toLocaleString('zh-CN') }}
                                </p>
                              </div>
                            </div>
                          </button>
                        </div>
                      </div>
                    </div>
                  </transition>
                </div>

                <!-- 用户菜单 -->
                <div class="relative user-menu-container">
                  <button
                    type="button"
                    @click.stop="toggleUserMenu"
                    class="flex items-center gap-2 p-1 rounded-full hover:bg-slate-50 transition-colors"
                    aria-label="User menu"
                  >
                    <!-- 如果有头像则显示头像，否则显示用户名首字母 -->
                    <div v-if="avatarUrl" class="h-9 w-9 rounded-full overflow-hidden">
                      <img :src="avatarUrl" :alt="username" class="w-full h-full object-cover" />
                    </div>
                    <div v-else class="h-9 w-9 rounded-full bg-ink text-white flex items-center justify-center text-sm font-semibold">
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
                        <RouterLink
                          to="/profile"
                          @click="showUserMenu = false"
                          class="w-full px-4 py-2.5 text-left text-sm text-ink hover:bg-canvas transition-colors flex items-center gap-3"
                        >
                          <svg class="w-5 h-5 text-ink-light" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                          </svg>
                          {{ labels.profile }}
                        </RouterLink>
                        
                        <RouterLink
                          to="/favorites"
                          @click="showUserMenu = false"
                          class="w-full px-4 py-2.5 text-left text-sm text-ink hover:bg-canvas transition-colors flex items-center gap-3"
                        >
                          <svg class="w-5 h-5 text-ink-light" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" />
                          </svg>
                          我的收藏
                        </RouterLink>
                        
                        <RouterLink
                          to="/history"
                          @click="showUserMenu = false"
                          class="w-full px-4 py-2.5 text-left text-sm text-ink hover:bg-canvas transition-colors flex items-center gap-3"
                        >
                          <svg class="w-5 h-5 text-ink-light" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                          </svg>
                          浏览历史
                        </RouterLink>
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
    
    <!-- 漂流书签组件（全局） -->
    <FloatingBookmark />
  </div>
</template>

<style scoped>
/* 如果有特殊样式写这里，但尽量用 Tailwind 类名 */
</style>
