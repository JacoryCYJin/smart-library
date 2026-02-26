<template>
  <div ref="masonryContainer" class="masonry-wrapper">
    <BookCard
      v-for="book in books"
      :key="book.resourceId"
      :book="book"
      class="masonry-item"
      @click="$emit('bookClick', $event)"
      @image-load="onImageLoad"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch, nextTick } from 'vue'
import BookCard from './BookCard.vue'

const props = defineProps({
  books: {
    type: Array,
    required: true
  }
})

defineEmits(['bookClick'])

const masonryContainer = ref(null)
const gap = 16

// 计算列数
const calculateColumns = () => {
  if (!masonryContainer.value) return 2
  
  const width = masonryContainer.value.offsetWidth
  
  if (width >= 1536) return 7
  if (width >= 1280) return 6
  if (width >= 1024) return 5
  if (width >= 768) return 4
  if (width >= 640) return 3
  return 2
}

// 初始化瀑布流
const initMasonry = () => {
  if (!masonryContainer.value) return
  
  const columnCount = calculateColumns()
  const containerWidth = masonryContainer.value.offsetWidth
  const itemWidth = (containerWidth - gap * (columnCount - 1)) / columnCount
  
  const columnHeights = new Array(columnCount).fill(0)
  
  const items = masonryContainer.value.querySelectorAll('.masonry-item')
  
  items.forEach((item) => {
    const minHeight = Math.min(...columnHeights)
    const minIndex = columnHeights.indexOf(minHeight)
    
    item.style.position = 'absolute'
    item.style.width = `${itemWidth}px`
    item.style.left = `${minIndex * (itemWidth + gap)}px`
    item.style.top = `${minHeight}px`
    
    columnHeights[minIndex] += item.offsetHeight + gap
  })
  
  const maxHeight = Math.max(...columnHeights)
  masonryContainer.value.style.height = `${maxHeight}px`
}

// 图片加载完成后重新布局
const onImageLoad = () => {
  requestAnimationFrame(() => {
    initMasonry()
  })
}

// 监听窗口大小变化
let resizeTimer = null
const handleResize = () => {
  clearTimeout(resizeTimer)
  resizeTimer = setTimeout(() => {
    initMasonry()
  }, 200)
}

// 监听 books 变化（监听整个数组，而不只是长度）
watch(() => props.books, () => {
  nextTick(() => {
    initMasonry()
  })
}, { deep: true })

onMounted(() => {
  nextTick(() => {
    initMasonry()
  })
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
.masonry-wrapper {
  position: relative;
  width: 100%;
}

.masonry-item {
  opacity: 1;
}
</style>
