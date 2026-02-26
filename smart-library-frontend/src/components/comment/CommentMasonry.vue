<template>
  <div ref="containerRef" class="relative w-full">
    <!-- 评论卡片 -->
    <div
      v-for="(comment, index) in comments"
      :key="comment.userId + comment.ctime"
      :ref="el => cardRefs[index] = el"
      :style="cardStyles[index]"
      class="absolute transition-all duration-300 ease-out"
    >
      <CommentCard :comment="comment" />
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted, onUnmounted, nextTick } from 'vue'
import CommentCard from './CommentCard.vue'

const props = defineProps({
  comments: {
    type: Array,
    required: true
  },
  columnCount: {
    type: Number,
    default: 3
  },
  gap: {
    type: Number,
    default: 20
  }
})

const containerRef = ref(null)
const cardRefs = ref([])
const cardStyles = ref([])
const columnHeights = ref([])

/**
 * 根据评论长度计算应该占据的列数
 * @param {string} content - 评论内容
 * @returns {number} - 占据的列数 (1, 2, 或 3)
 */
const getColumnSpan = (content) => {
  const length = content.length
  
  // 短评论（< 150字）：占1列
  if (length < 150) {
    return 1
  }
  // 中等评论（150-400字）：占2列
  else if (length < 400) {
    return 2
  }
  // 长评论（>= 400字）：占3列
  else {
    return 3
  }
}

/**
 * 找到可以放置指定列宽卡片的最佳位置
 * @param {number} span - 卡片占据的列数
 * @returns {object} - { columnIndex: 起始列索引, minHeight: 该位置的高度 }
 */
const findBestPosition = (span) => {
  // 如果卡片宽度超过总列数，限制为总列数
  const actualSpan = Math.min(span, props.columnCount)
  
  let bestColumnIndex = 0
  let minMaxHeight = Infinity
  
  // 遍历所有可能的起始位置
  for (let i = 0; i <= props.columnCount - actualSpan; i++) {
    // 计算这个位置跨越的所有列中的最大高度
    let maxHeight = 0
    for (let j = i; j < i + actualSpan; j++) {
      maxHeight = Math.max(maxHeight, columnHeights.value[j])
    }
    
    // 找到最小的最大高度（即最矮的位置）
    if (maxHeight < minMaxHeight) {
      minMaxHeight = maxHeight
      bestColumnIndex = i
    }
  }
  
  return { columnIndex: bestColumnIndex, minHeight: minMaxHeight }
}

/**
 * 计算瀑布流布局
 */
const calculateLayout = async () => {
  if (!containerRef.value || props.comments.length === 0) return

  await nextTick()

  // 使用 requestAnimationFrame 确保 DOM 完全渲染
  requestAnimationFrame(() => {
    const containerWidth = containerRef.value.offsetWidth
    const columnWidth = (containerWidth - props.gap * (props.columnCount - 1)) / props.columnCount

    // 初始化列高度
    columnHeights.value = new Array(props.columnCount).fill(0)
    cardStyles.value = []

    // 为每个评论卡片计算位置
    props.comments.forEach((comment, index) => {
      // 计算该评论应该占据的列数
      const span = getColumnSpan(comment.content)
      
      // 找到最佳放置位置
      const { columnIndex, minHeight } = findBestPosition(span)
      
      // 计算卡片宽度（列宽 * 列数 + 间距 * (列数 - 1)）
      const cardWidth = columnWidth * span + props.gap * (span - 1)
      
      // 计算卡片位置
      const left = columnIndex * (columnWidth + props.gap)
      const top = minHeight

      // 获取卡片的真实高度
      const cardElement = cardRefs.value[index]
      const cardHeight = cardElement ? cardElement.offsetHeight : 200

      cardStyles.value.push({
        width: `${cardWidth}px`,
        left: `${left}px`,
        top: `${top}px`
      })

      // 更新所有被占据的列的高度
      for (let i = columnIndex; i < columnIndex + span; i++) {
        columnHeights.value[i] = minHeight + cardHeight + props.gap
      }
    })

    // 设置容器高度
    const maxHeight = Math.max(...columnHeights.value) - props.gap
    containerRef.value.style.height = `${maxHeight}px`
  })
}

// 监听评论变化
watch(() => props.comments, async () => {
  await nextTick()
  calculateLayout()
}, { deep: true })

// 监听窗口大小变化
let resizeObserver = null

onMounted(() => {
  // 立即计算布局
  calculateLayout()

  // 使用 ResizeObserver 监听容器大小变化
  if (containerRef.value) {
    resizeObserver = new ResizeObserver(() => {
      calculateLayout()
    })
    resizeObserver.observe(containerRef.value)
  }
})

onUnmounted(() => {
  if (resizeObserver && containerRef.value) {
    resizeObserver.unobserve(containerRef.value)
  }
})
</script>
