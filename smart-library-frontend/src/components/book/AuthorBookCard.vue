<template>
  <div 
    ref="cardRef" 
    class="author-book-card group cursor-pointer"
    @click="$emit('click', book.resourceId)"
  >
    <!-- 封面 - 固定宽高比 -->
    <div class="relative overflow-hidden rounded-lg bg-structure aspect-[3/4]">
      <img
        v-if="book.coverUrl"
        :src="book.coverUrl"
        :alt="book.title"
        class="w-full h-full object-cover transition-transform duration-500 group-hover:scale-105"
        loading="lazy"
        @load="$emit('imageLoad')"
      />
      <div v-else class="w-full h-full flex items-center justify-center text-ink-light">
        <svg class="w-12 h-12" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path 
            stroke-linecap="round" 
            stroke-linejoin="round" 
            stroke-width="1.5" 
            d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" 
          />
        </svg>
      </div>
      
      <div class="absolute inset-0 bg-black/0 group-hover:bg-black/5 transition-colors duration-300"></div>
    </div>

    <!-- 信息 -->
    <div class="space-y-1.5 mt-3">
      <!-- 标题 -->
      <h3 class="text-sm font-medium line-clamp-2 leading-snug group-hover:text-pop transition-colors text-ink min-h-[2.5rem]">
        {{ book.title }}
      </h3>
      
      <!-- 评分和分类 -->
      <div class="flex items-center gap-2 text-xs">
        <!-- 评分 -->
        <div v-if="book.sourceScore || book.finalScore" class="flex items-center gap-1 text-ink-light">
          <svg class="w-3.5 h-3.5 fill-current text-pop" viewBox="0 0 20 20">
            <path d="M10 15l-5.878 3.09 1.123-6.545L.489 6.91l6.572-.955L10 0l2.939 5.955 6.572.955-4.756 4.635 1.123 6.545z"/>
          </svg>
          <span class="font-medium">{{ book.finalScore || book.sourceScore }}</span>
        </div>
        
        <!-- 分隔符 -->
        <span v-if="(book.sourceScore || book.finalScore) && book.categoryNames && book.categoryNames.length > 0" class="text-structure">·</span>
        
        <!-- 分类标签 -->
        <span v-if="book.categoryNames && book.categoryNames.length > 0" class="text-ink-light truncate">
          {{ book.categoryNames[0] }}
        </span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import gsap from 'gsap'

defineProps({
  book: {
    type: Object,
    required: true
  }
})

defineEmits(['click', 'imageLoad'])

const cardRef = ref(null)
let observer = null

onMounted(() => {
  // 初始状态：隐藏卡片
  gsap.set(cardRef.value, {
    y: 35,
    opacity: 0
  })

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
            delay: Math.random() * 0.15
          })
          
          observer.unobserve(entry.target)
        }
      })
    },
    {
      threshold: 0.1,
      rootMargin: '50px'
    }
  )

  if (cardRef.value) {
    observer.observe(cardRef.value)
  }
})

onUnmounted(() => {
  if (observer && cardRef.value) {
    observer.unobserve(cardRef.value)
  }
})
</script>

<style scoped>
.author-book-card {
  width: 100%;
}
</style>
