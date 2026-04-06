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
    {
      path: '/profile',
      name: 'profile',
      component: () => import('@/views/web/ProfileView.vue'),
      meta: { 
        layout: 'web',
        requiresAuth: true
      }
    },
    {
      path: '/favorites',
      name: 'favorites',
      component: () => import('@/views/web/FavoritesView.vue'),
      meta: { 
        layout: 'web',
        requiresAuth: true
      }
    },
    {
      path: '/history',
      name: 'history',
      component: () => import('@/views/web/HistoryView.vue'),
      meta: { 
        layout: 'web',
        requiresAuth: true
      }
    },
    {
      path: '/serendipity',
      name: 'serendipity',
      component: () => import('@/views/web/SerendipityView.vue'),
      meta: { layout: 'web' }
    },
    {
      path: '/bookmark-test',
      name: 'bookmark-test',
      component: () => import('@/views/web/BookmarkTestView.vue'),
      meta: { layout: 'web' }
    },

    // ================= 管理端路由 (Admin) =================
    {
      path: '/admin',
      name: 'admin-dashboard',
      component: () => import('@/views/admin/DashboardView.vue'),
      meta: { 
        layout: 'admin',
        requiresAuth: true,
        requiresAdmin: true
      }
    },
    {
      path: '/admin/users',
      name: 'admin-users',
      component: () => import('@/views/admin/UserManageView.vue'),
      meta: { 
        layout: 'admin',
        requiresAuth: true,
        requiresAdmin: true
      }
    },
    {
      path: '/admin/resources',
      name: 'admin-resources',
      component: () => import('@/views/admin/ResourceManageView.vue'),
      meta: { 
        layout: 'admin',
        requiresAuth: true,
        requiresAdmin: true
      }
    },
    {
      path: '/admin/comments',
      name: 'admin-comments',
      component: () => import('@/views/admin/CommentManageView.vue'),
      meta: { 
        layout: 'admin',
        requiresAuth: true,
        requiresAdmin: true
      }
    },
    {
      path: '/admin/categories',
      name: 'admin-categories',
      component: () => import('@/views/admin/CategoryManageView.vue'),
      meta: { 
        layout: 'admin',
        requiresAuth: true,
        requiresAdmin: true
      }
    },
    {
      path: '/admin/authors',
      name: 'admin-authors',
      component: () => import('@/views/admin/AuthorManageView.vue'),
      meta: { 
        layout: 'admin',
        requiresAuth: true,
        requiresAdmin: true
      }
    },
    {
      path: '/admin/links',
      name: 'admin-links',
      component: () => import('@/views/admin/LinkManageView.vue'),
      meta: { 
        layout: 'admin',
        requiresAuth: true,
        requiresAdmin: true
      }
    },
    {
      path: '/admin/graphs',
      name: 'admin-graphs',
      component: () => import('@/views/admin/GraphManageView.vue'),
      meta: { 
        layout: 'admin',
        requiresAuth: true,
        requiresAdmin: true
      }
    },
    {
      path: '/admin/ranking',
      name: 'admin-ranking',
      component: () => import('@/views/admin/RankingView.vue'),
      meta: { 
        layout: 'admin',
        requiresAuth: true,
        requiresAdmin: true
      }
    },
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
  
  // 需要登录的路由
  if (to.meta.requiresAuth && !authStore.isLoggedIn) {
    // 未登录，跳转到登录页，并记录原始目标路由
    next({
      name: 'login',
      query: { redirect: to.fullPath }
    })
    return
  }
  
  // 需要管理员权限的路由
  if (to.meta.requiresAdmin && !authStore.isAdmin) {
    // 不是管理员，跳转到首页
    next({ name: 'home' })
    return
  }
  
  next()
})

export default router
