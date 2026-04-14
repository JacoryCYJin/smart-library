<template>
  <div class="min-h-screen bg-canvas">
    <!-- 加载状态 -->
    <div v-if="loading" class="flex justify-center items-center py-20">
      <div class="w-8 h-8 border-2 border-ink border-t-transparent rounded-full animate-spin"></div>
    </div>

    <!-- 公告详情 -->
    <div v-else-if="announcement" class="max-w-6xl mx-auto px-6 py-12 sm:py-20">
      <!-- 装饰性顶部 -->
      <div class="mb-12 text-center">
        <div class="inline-flex items-center gap-3 px-6 py-2 bg-white/60 backdrop-blur-sm rounded-full border border-structure/30 shadow-sm">
          <svg class="w-5 h-5 text-pop" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9" />
          </svg>
          <span class="text-sm font-medium text-ink-light">阅墨官方公告</span>
        </div>
      </div>

      <!-- 主内容区 -->
      <article class="relative">
        <!-- 标题区域 -->
        <header class="mb-12 text-center">
          <!-- 优先级和类型标签 -->
          <div class="flex items-center justify-center gap-3 mb-6">
            <span 
              v-if="announcement.priority > 0"
              :class="priorityStyles.badge"
              class="inline-flex items-center gap-1.5 px-4 py-1.5 rounded-full text-sm font-medium"
            >
              <svg v-if="announcement.priority === 2" class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
                <path fill-rule="evenodd" d="M8.257 3.099c.765-1.36 2.722-1.36 3.486 0l5.58 9.92c.75 1.334-.213 2.98-1.742 2.98H4.42c-1.53 0-2.493-1.646-1.743-2.98l5.58-9.92zM11 13a1 1 0 11-2 0 1 1 0 012 0zm-1-8a1 1 0 00-1 1v3a1 1 0 002 0V6a1 1 0 00-1-1z" clip-rule="evenodd" />
              </svg>
              <svg v-else class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
                <path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z" clip-rule="evenodd" />
              </svg>
              {{ priorityLabels[announcement.priority] }}
            </span>
            <span class="px-4 py-1.5 bg-white/60 backdrop-blur-sm text-ink-light rounded-full text-sm font-medium border border-structure/30">
              {{ typeLabels[announcement.type] }}
            </span>
          </div>

          <!-- 标题 -->
          <h1 class="text-4xl sm:text-6xl font-bold text-ink leading-tight mb-8 tracking-tight px-4">
            {{ announcement.title }}
          </h1>

          <!-- 元信息 -->
          <div class="flex flex-wrap items-center justify-center gap-6 text-sm text-ink-light">
            <div class="flex items-center gap-2">
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              <span>{{ formatDate(announcement.publishTime) }}</span>
            </div>
            
            <span class="text-structure">·</span>
            
            <div class="flex items-center gap-2">
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
              </svg>
              <span>{{ announcement.viewCount }} 次阅读</span>
            </div>
          </div>

          <!-- 装饰性分隔线 -->
          <div class="mt-12 flex items-center justify-center gap-2">
            <div class="w-1 h-1 rounded-full bg-structure"></div>
            <div class="w-12 h-px bg-gradient-to-r from-transparent via-structure to-transparent"></div>
            <div class="w-1 h-1 rounded-full bg-structure"></div>
          </div>
        </header>

        <!-- 内容区域 -->
        <div class="max-w-4xl mx-auto">
          <!-- 白色内容卡片 -->
          <div class="bg-white rounded-2xl shadow-[0_4px_20px_rgba(16,42,67,0.06)] p-8 sm:p-12">
            <MdPreview 
              :model-value="announcement.content" 
              language="zh-CN"
              :theme="'light'"
              class="announcement-markdown"
            />
          </div>

          <!-- 底部装饰 -->
          <div class="mt-12 text-center">
            <div class="inline-flex items-center gap-2 px-6 py-3 bg-white/40 backdrop-blur-sm rounded-full text-sm text-ink-light border border-structure/20">
              <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
                <path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z" clip-rule="evenodd" />
              </svg>
              <span>如有疑问，请联系客服</span>
            </div>
          </div>
        </div>
      </article>
    </div>

    <!-- 错误状态 -->
    <div v-else class="flex flex-col items-center justify-center py-20 text-ink-light">
      <div class="w-20 h-20 rounded-full bg-white/60 backdrop-blur-sm flex items-center justify-center mb-4 border border-structure/30">
        <svg class="w-10 h-10 opacity-50" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
        </svg>
      </div>
      <p class="text-lg font-medium">公告不存在或已被删除</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getAnnouncementDetail } from '@/api/announcement'
import { Message } from '@arco-design/web-vue'
import { MdPreview } from 'md-editor-v3'
import 'md-editor-v3/lib/preview.css'

const route = useRoute()
const router = useRouter()
const announcementId = route.params.announcementId

const announcement = ref(null)
const loading = ref(true)

const typeLabels = {
  1: '系统更新',
  2: '功能上线',
  3: '维护通知',
  4: '活动公告'
}

const priorityLabels = {
  0: '普通',
  1: '重要',
  2: '紧急'
}

// 优先级样式
const priorityStyles = computed(() => {
  if (!announcement.value) return {}
  
  const priority = announcement.value.priority
  if (priority === 2) {
    return {
      badge: 'px-4 py-1.5 text-sm bg-red-50 text-red-600 rounded-full font-semibold border border-red-200',
      icon: 'text-red-600'
    }
  } else if (priority === 1) {
    return {
      badge: 'px-4 py-1.5 text-sm bg-orange-50 text-orange-600 rounded-full font-semibold border border-orange-200',
      icon: 'text-orange-600'
    }
  }
  return {
    badge: 'px-4 py-1.5 text-sm bg-structure text-ink-light rounded-full font-medium',
    icon: 'text-ink-light'
  }
})

// 格式化日期
function formatDate(dateString) {
  const date = new Date(dateString)
  const now = new Date()
  const diff = now - date
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))
  
  if (days === 0) {
    const hours = Math.floor(diff / (1000 * 60 * 60))
    if (hours === 0) {
      const minutes = Math.floor(diff / (1000 * 60))
      return minutes === 0 ? '刚刚' : `${minutes} 分钟前`
    }
    return `${hours} 小时前`
  } else if (days === 1) {
    return '昨天'
  } else if (days < 7) {
    return `${days} 天前`
  }
  
  return date.toLocaleDateString('zh-CN', { 
    year: 'numeric', 
    month: 'long', 
    day: 'numeric'
  })
}

async function loadAnnouncement() {
  loading.value = true
  try {
    const res = await getAnnouncementDetail(announcementId)
    if (res.code === 0) {
      announcement.value = res.data
    } else {
      Message.error('加载公告失败')
      router.push('/')
    }
  } catch (error) {
    console.error('加载公告失败:', error)
    Message.error('加载公告失败')
    router.push('/')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadAnnouncement()
})
</script>

<style scoped>
/* Markdown 内容样式 - 优雅文艺风格 */
:deep(.announcement-markdown) {
  font-size: 17px;
  line-height: 2;
  color: #102a43;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', 'Source Han Serif SC', serif;
  letter-spacing: 0.02em;
}

/* 标题样式 - 水墨风格 */
:deep(.announcement-markdown h1) {
  font-size: 2em;
  font-weight: 600;
  margin: 2.5em 0 1em;
  color: #102a43;
  letter-spacing: 0.05em;
  position: relative;
  padding-bottom: 0.5em;
}

:deep(.announcement-markdown h1::after) {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 40px;
  height: 2px;
  background: linear-gradient(90deg, #102a43, transparent);
}

:deep(.announcement-markdown h2) {
  font-size: 1.5em;
  font-weight: 600;
  margin: 2em 0 0.8em;
  color: #102a43;
  letter-spacing: 0.03em;
}

:deep(.announcement-markdown h3) {
  font-size: 1.25em;
  font-weight: 600;
  margin: 1.5em 0 0.6em;
  color: #102a43;
  letter-spacing: 0.02em;
}

/* 段落样式 - 增加行高和字间距 */
:deep(.announcement-markdown p) {
  margin-bottom: 1.5em;
  color: #627d98;
  text-align: justify;
  text-justify: inter-ideograph;
}

:deep(.announcement-markdown p:first-of-type) {
  font-size: 1.15em;
  color: #102a43;
  line-height: 2.2;
  margin-bottom: 2em;
}

/* 列表样式 - 简约风格 */
:deep(.announcement-markdown ul),
:deep(.announcement-markdown ol) {
  margin: 1.5em 0;
  padding-left: 2em;
  color: #627d98;
}

:deep(.announcement-markdown li) {
  margin-bottom: 0.8em;
  line-height: 2;
}

:deep(.announcement-markdown li::marker) {
  color: #102a43;
}

/* 代码样式 - 淡雅配色 */
:deep(.announcement-markdown code) {
  background: #f0f4f8;
  padding: 0.2em 0.5em;
  border-radius: 4px;
  font-size: 0.9em;
  color: #d64545;
  font-weight: 500;
  border: 1px solid #e3eaf0;
  font-family: 'SF Mono', Monaco, 'Cascadia Code', 'Roboto Mono', Consolas, monospace;
}

:deep(.announcement-markdown pre) {
  background: #102a43;
  padding: 1.5em;
  border-radius: 8px;
  overflow-x: auto;
  margin: 2em 0;
  box-shadow: 0 2px 8px rgba(16, 42, 67, 0.1);
}

:deep(.announcement-markdown pre code) {
  background: transparent;
  padding: 0;
  color: #f0f4f8;
  border: none;
  font-size: 0.9em;
}

/* 引用样式 - 水墨风格 */
:deep(.announcement-markdown blockquote) {
  border-left: 3px solid #102a43;
  background: transparent;
  padding: 1em 1.5em;
  margin: 2em 0;
  color: #627d98;
  font-style: normal;
  position: relative;
  quotes: """ """ "'" "'";
}

:deep(.announcement-markdown blockquote::before) {
  content: open-quote;
  position: absolute;
  top: -0.2em;
  left: 0.3em;
  font-size: 3em;
  color: #d9e2ec;
  font-family: Georgia, serif;
  line-height: 1;
}

:deep(.announcement-markdown blockquote p) {
  margin: 0;
  padding-left: 1.5em;
}

/* 链接样式 - 简约下划线 */
:deep(.announcement-markdown a) {
  color: #102a43;
  text-decoration: none;
  border-bottom: 1px solid #d9e2ec;
  transition: all 0.3s ease;
  font-weight: 500;
}

:deep(.announcement-markdown a:hover) {
  border-bottom-color: #102a43;
}

/* 图片样式 - 优雅圆角 */
:deep(.announcement-markdown img) {
  max-width: 100%;
  height: auto;
  border-radius: 8px;
  margin: 2.5em 0;
  box-shadow: 0 4px 16px rgba(16, 42, 67, 0.08);
}

/* 表格样式 - 简约线条 */
:deep(.announcement-markdown table) {
  width: 100%;
  border-collapse: collapse;
  margin: 2em 0;
  font-size: 0.95em;
}

:deep(.announcement-markdown th),
:deep(.announcement-markdown td) {
  border: 1px solid #d9e2ec;
  padding: 0.8em 1em;
  text-align: left;
}

:deep(.announcement-markdown th) {
  background: #f0f4f8;
  color: #102a43;
  font-weight: 600;
}

:deep(.announcement-markdown tbody tr:hover) {
  background-color: #f0f4f8;
}

/* 分隔线样式 - 水墨渐变 */
:deep(.announcement-markdown hr) {
  border: none;
  height: 1px;
  background: linear-gradient(90deg, transparent, #d9e2ec, transparent);
  margin: 3em 0;
}

/* 强调样式 */
:deep(.announcement-markdown strong) {
  color: #102a43;
  font-weight: 600;
}

:deep(.announcement-markdown em) {
  color: #627d98;
  font-style: italic;
}
</style>
