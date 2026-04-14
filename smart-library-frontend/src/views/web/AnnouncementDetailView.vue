<template>
  <div class="min-h-screen bg-canvas">
    <!-- 加载状态 -->
    <div v-if="loading" class="flex justify-center items-center py-20">
      <div class="w-8 h-8 border-2 border-ink border-t-transparent rounded-full animate-spin"></div>
    </div>

    <!-- 公告详情 -->
    <div v-else-if="announcement">
      <!-- 内容容器 -->
      <div class="max-w-4xl mx-auto px-6 pt-16 pb-20">
        <!-- 主卡片 -->
        <article class="bg-white rounded-3xl shadow-[0_8px_30px_rgba(16,42,67,0.08)] overflow-hidden">
          <!-- 头部区域 -->
          <div class="relative px-8 sm:px-12 pt-12 pb-10 border-b border-structure/30">
            <!-- 优先级标签 - 浮动在右上角 -->
            <div class="absolute top-8 right-8 flex gap-2">
              <span :class="priorityStyles.badge">
                {{ priorityLabels[announcement.priority] }}
              </span>
              <span class="px-4 py-1.5 text-sm bg-white/80 backdrop-blur-sm text-ink-light rounded-full font-medium border border-structure/50">
                {{ typeLabels[announcement.type] }}
              </span>
            </div>

            <!-- 标题区域 -->
            <div class="max-w-3xl">
              <!-- 优先级图标 -->
              <div v-if="announcement.priority > 0" :class="priorityStyles.icon" class="mb-4 inline-flex items-center gap-2 px-3 py-1.5 rounded-full bg-white/80 backdrop-blur-sm">
                <svg v-if="announcement.priority === 2" class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
                  <path fill-rule="evenodd" d="M8.257 3.099c.765-1.36 2.722-1.36 3.486 0l5.58 9.92c.75 1.334-.213 2.98-1.742 2.98H4.42c-1.53 0-2.493-1.646-1.743-2.98l5.58-9.92zM11 13a1 1 0 11-2 0 1 1 0 012 0zm-1-8a1 1 0 00-1 1v3a1 1 0 002 0V6a1 1 0 00-1-1z" clip-rule="evenodd" />
                </svg>
                <svg v-else class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
                  <path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z" clip-rule="evenodd" />
                </svg>
                <span class="text-sm font-semibold">{{ priorityLabels[announcement.priority] }}</span>
              </div>

              <!-- 标题 -->
              <h1 class="text-4xl sm:text-5xl font-bold text-ink leading-tight mb-6 tracking-tight">
                {{ announcement.title }}
              </h1>

              <!-- 元信息 -->
              <div class="flex flex-wrap items-center gap-4 text-sm text-ink-light">
                <div class="flex items-center gap-2 px-3 py-1.5 bg-white/60 backdrop-blur-sm rounded-full">
                  <svg class="w-4 h-4 text-pop" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z" />
                  </svg>
                  <span class="font-medium">阅墨官方</span>
                </div>
                
                <div class="flex items-center gap-2">
                  <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                  </svg>
                  <span>{{ formatDate(announcement.publishTime) }}</span>
                </div>
                
                <div class="flex items-center gap-2">
                  <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                  </svg>
                  <span>{{ announcement.viewCount }} 次阅读</span>
                </div>
              </div>
            </div>
          </div>

          <!-- 内容区域 -->
          <div class="px-8 sm:px-12 py-12">
            <div class="max-w-3xl mx-auto">
              <MdPreview 
                :model-value="announcement.content" 
                language="zh-CN"
                :theme="'light'"
                class="announcement-markdown"
              />
            </div>
          </div>

          <!-- 底部装饰 -->
          <div class="px-8 sm:px-12 py-8 border-t border-structure/30">
            <div class="max-w-3xl mx-auto flex items-center justify-center">
              <div class="flex items-center gap-2 text-sm text-ink-light">
                <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
                  <path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z" clip-rule="evenodd" />
                </svg>
                <span>如有疑问，请联系客服</span>
              </div>
            </div>
          </div>
        </article>
      </div>
    </div>

    <!-- 错误状态 -->
    <div v-else class="flex flex-col items-center justify-center py-20 text-ink-light">
      <div class="w-20 h-20 rounded-full bg-structure/50 flex items-center justify-center mb-4">
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
/* Markdown 内容样式 - 现代化设计 */
:deep(.announcement-markdown) {
  font-size: 17px;
  line-height: 1.8;
  color: #102a43;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', sans-serif;
}

/* 标题样式 */
:deep(.announcement-markdown h1) {
  font-size: 2.25em;
  font-weight: 700;
  margin: 2em 0 0.75em;
  padding-bottom: 0.5em;
  border-bottom: 3px solid #d9e2ec;
  color: #102a43;
  letter-spacing: -0.02em;
  position: relative;
}

:deep(.announcement-markdown h1::before) {
  content: '';
  position: absolute;
  bottom: -3px;
  left: 0;
  width: 60px;
  height: 3px;
  background: linear-gradient(90deg, #d64545, #ff6b6b);
  border-radius: 2px;
}

:deep(.announcement-markdown h2) {
  font-size: 1.75em;
  font-weight: 600;
  margin: 1.75em 0 0.75em;
  color: #102a43;
  letter-spacing: -0.01em;
  padding-left: 12px;
  border-left: 4px solid #d64545;
}

:deep(.announcement-markdown h3) {
  font-size: 1.35em;
  font-weight: 600;
  margin: 1.5em 0 0.5em;
  color: #102a43;
}

/* 段落样式 */
:deep(.announcement-markdown p) {
  margin-bottom: 1.25em;
  color: #627d98;
  text-align: justify;
}

:deep(.announcement-markdown p:first-of-type) {
  font-size: 1.1em;
  color: #102a43;
  font-weight: 500;
}

/* 列表样式 */
:deep(.announcement-markdown ul),
:deep(.announcement-markdown ol) {
  margin: 1.25em 0;
  padding-left: 2em;
  color: #627d98;
}

:deep(.announcement-markdown li) {
  margin-bottom: 0.75em;
  line-height: 1.8;
}

:deep(.announcement-markdown li::marker) {
  color: #d64545;
  font-weight: 600;
}

/* 代码样式 */
:deep(.announcement-markdown code) {
  background: linear-gradient(135deg, #f0f4f8 0%, #e3eaf0 100%);
  padding: 0.25em 0.5em;
  border-radius: 6px;
  font-size: 0.9em;
  color: #d64545;
  font-weight: 500;
  border: 1px solid #d9e2ec;
}

:deep(.announcement-markdown pre) {
  background: linear-gradient(135deg, #102a43 0%, #1a3a52 100%);
  padding: 1.5em;
  border-radius: 12px;
  overflow-x: auto;
  margin: 1.5em 0;
  box-shadow: 0 4px 12px rgba(16, 42, 67, 0.15);
}

:deep(.announcement-markdown pre code) {
  background: transparent;
  padding: 0;
  color: #f0f4f8;
  border: none;
  font-size: 0.95em;
}

/* 引用样式 */
:deep(.announcement-markdown blockquote) {
  border-left: 4px solid #d64545;
  background: linear-gradient(90deg, #f0f4f8 0%, transparent 100%);
  padding: 1em 1.5em;
  margin: 1.5em 0;
  color: #627d98;
  font-style: italic;
  border-radius: 0 8px 8px 0;
  position: relative;
}

:deep(.announcement-markdown blockquote::before) {
  content: '"';
  position: absolute;
  top: -10px;
  left: 10px;
  font-size: 3em;
  color: #d9e2ec;
  font-family: Georgia, serif;
}

/* 链接样式 */
:deep(.announcement-markdown a) {
  color: #d64545;
  text-decoration: none;
  font-weight: 500;
  border-bottom: 2px solid transparent;
  transition: all 0.3s ease;
}

:deep(.announcement-markdown a:hover) {
  border-bottom-color: #d64545;
}

/* 图片样式 */
:deep(.announcement-markdown img) {
  max-width: 100%;
  height: auto;
  border-radius: 12px;
  margin: 2em 0;
  box-shadow: 0 8px 24px rgba(16, 42, 67, 0.12);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

:deep(.announcement-markdown img:hover) {
  transform: translateY(-4px);
  box-shadow: 0 12px 32px rgba(16, 42, 67, 0.18);
}

/* 表格样式 */
:deep(.announcement-markdown table) {
  width: 100%;
  border-collapse: separate;
  border-spacing: 0;
  margin: 1.5em 0;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(16, 42, 67, 0.08);
}

:deep(.announcement-markdown th),
:deep(.announcement-markdown td) {
  border: 1px solid #d9e2ec;
  padding: 1em 1.25em;
  text-align: left;
}

:deep(.announcement-markdown th) {
  background: linear-gradient(135deg, #102a43 0%, #1a3a52 100%);
  color: white;
  font-weight: 600;
  text-transform: uppercase;
  font-size: 0.85em;
  letter-spacing: 0.05em;
}

:deep(.announcement-markdown tbody tr) {
  transition: background-color 0.2s ease;
}

:deep(.announcement-markdown tbody tr:nth-child(even)) {
  background-color: #f0f4f8;
}

:deep(.announcement-markdown tbody tr:hover) {
  background-color: #e3eaf0;
}

/* 分隔线样式 */
:deep(.announcement-markdown hr) {
  border: none;
  height: 2px;
  background: linear-gradient(90deg, transparent, #d9e2ec, transparent);
  margin: 2.5em 0;
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
