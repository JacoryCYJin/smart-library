import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import HomeView from '@/views/web/HomeView.vue'
import BookView from '@/views/web/BookView.vue'
import NotFound from '@/views/NotFound.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  scrollBehavior(to, from) {
    // 返回 false 表示不处理滚动，让 Lenis 和组件自己处理
    // 只在从详情页返回列表页时返回 false
    if (from.name === 'book-detail' && to.name === 'book') {
      return false
    }
    // 其他情况返回顶部（会被 WebLayout 的 watch 处理）
    return { top: 0 }
  },
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
      meta: { layout: 'web' },
    },
    {
      path: '/book',
      name: 'book',
      component: BookView,
      meta: {
        layout: 'web',
        keepAlive: true,
      },
    },
    {
      path: '/book/:bookId',
      name: 'book-detail',
      component: () => import('@/views/web/BookDetailView.vue'),
      meta: { layout: 'web' },
    },
    {
      path: '/author/:authorId',
      name: 'author-detail',
      component: () => import('@/views/web/AuthorDetailView.vue'),
      meta: { layout: 'web' },
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/web/LoginView.vue'),
      meta: { layout: 'none' } // 登录页不使用布局
    },

    // ================= 管理端路由 (Admin) =================
    // {
    //   path: '/admin',
    //   name: 'admin-dashboard',
    //   component: () => import('@/views/admin/Dashboard.vue'),
    //   meta: { layout: 'admin' } // ⚠️ 关键标记：告诉 App.vue 用 AdminLayout
    // },
    // {
    //   path: '/admin/books',
    //   name: 'admin-books',
    //   component: () => import('@/views/admin/BookManage.vue'),
    //   meta: { layout: 'admin' }
    // },
    {
      path: '/:pathMatch(.*)*',
      name: 'not-found',
      component: NotFound,
      meta: { layout: 'none' },
    },
  ],
})

// 路由守卫：保护需要登录的页面
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  
  // 需要登录的路由（可以在 meta 中标记）
  if (to.meta.requiresAuth && !authStore.isLoggedIn) {
    // 未登录，跳转到登录页，并记录原始目标路由
    next({
      name: 'login',
      query: { redirect: to.fullPath }
    })
  } else {
    next()
  }
})

export default router
