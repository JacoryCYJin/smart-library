<template>
  <div ref="cardRef" class="bg-white border border-structure rounded-xl p-5 hover:shadow-lg transition-all duration-300 h-full flex flex-col">
    <!-- 用户信息 -->
    <div class="flex items-start gap-3 mb-4">
      <img
        :src="comment.avatarUrl || 'https://api.dicebear.com/7.x/avataaars/svg?seed=' + comment.username"
        :alt="comment.username"
        class="w-12 h-12 rounded-full border-2 border-structure flex-shrink-0"
      />
      <div class="flex-1 min-w-0">
        <div class="flex items-center gap-2 mb-1">
          <span class="text-base font-bold text-ink truncate">{{ comment.username }}</span>
          <!-- 评分星星 -->
          <div v-if="comment.score" class="flex items-center gap-0.5 flex-shrink-0">
            <svg
              v-for="star in 5"
              :key="star"
              class="w-4 h-4"
              :class="star <= comment.score ? 'text-yellow-500' : 'text-structure'"
              fill="currentColor"
              viewBox="0 0 20 20"
            >
              <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
            </svg>
          </div>
        </div>
        <span class="text-xs text-ink-light">{{ formatDate(comment.ctime) }}</span>
      </div>
    </div>

    <!-- 评论内容 -->
    <p class="text-sm text-ink-light leading-relaxed flex-1">{{ comment.content }}</p>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useLocaleStore } from '@/stores/locale'
import gsap from 'gsap'

defineProps({
  comment: {
    type: Object,
    required: true
  }
})

const localeStore = useLocaleStore()
const cardRef = ref(null)
let observer = null

/**
 * 格式化日期
 */
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  
  const date = new Date(dateStr)
  const now = new Date()
  const diff = now - date
  
  if (diff < 60000) {
    return localeStore.currentLang === 'zh' ? '刚刚' : 'Just now'
  }
  
  if (diff < 3600000) {
    const minutes = Math.floor(diff / 60000)
    return localeStore.currentLang === 'zh' ? `${minutes}分钟前` : `${minutes}m ago`
  }
  
  if (diff < 86400000) {
    const hours = Math.floor(diff / 3600000)
    return localeStore.currentLang === 'zh' ? `${hours}小时前` : `${hours}h ago`
  }
  
  if (diff < 604800000) {
    const days = Math.floor(diff / 86400000)
    return localeStore.currentLang === 'zh' ? `${days}天前` : `${days}d ago`
  }
  
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  
  if (year === now.getFullYear()) {
    return `${month}-${day}`
  }
  
  return `${year}-${month}-${day}`
}

onMounted(() => {
  // 获取父元素（CommentMasonry 中的 absolute 定位容器）
  const parentElement = cardRef.value?.parentElement
  
  if (!parentElement) return

  // 立即隐藏卡片，避免闪烁
  parentElement.style.opacity = '0'
  parentElement.style.transform = 'translateY(35px)'

  // 创建 Intersection Observer
  observer = new IntersectionObserver(
    (entries) => {
      entries.forEach((entry) => {
        if (entry.isIntersecting) {
          // 当卡片进入视口时，播放动画
          gsap.to(entry.target, {
            y: 0,
            opacity: 1,
            duration: 0.65,
            ease: 'power2.out',
            delay: Math.random() * 0.15 // 随机延迟，制造错落感
          })
          
          // 动画播放后，停止观察该元素
          observer.unobserve(entry.target)
        }
      })
    },
    {
      threshold: 0.1, // 当 10% 的卡片进入视口时触发
      rootMargin: '50px' // 提前 50px 触发，让动画更自然
    }
  )

  // 开始观察父元素
  observer.observe(parentElement)
})

onUnmounted(() => {
  // 清理 observer
  const parentElement = cardRef.value?.parentElement
  if (observer && parentElement) {
    observer.unobserve(parentElement)
  }
})
</script>
