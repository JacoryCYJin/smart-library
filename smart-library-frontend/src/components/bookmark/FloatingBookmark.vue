<template>
  <Teleport to="body">
    <!-- 遮罩层 -->
    <Transition name="fade">
      <div
        v-if="isExpanded"
        class="fixed inset-0 z-[9998] bg-black/30 backdrop-blur-sm"
        @click="handleDismiss"
      />
    </Transition>

    <!-- 书签卡片 -->
    <div
      v-if="isVisible"
      ref="bookmarkRef"
      class="floating-bookmark fixed z-[9999] select-none transition-all duration-300"
      :class="{ 'cursor-pointer': !isExpanded }"
      :style="bookmarkStyle"
      @click="handleBookmarkClick"
    >
      <!-- 书签卡片 -->
      <div
        class="bookmark-card relative rounded-lg bg-white/95 shadow-2xl backdrop-blur-sm transition-all duration-500"
        :class="[
          isExpanded
            ? 'w-96 scale-100 p-6'
            : 'w-64 scale-90 p-4 hover:scale-95'
        ]"
      >
        <!-- 跳过按钮（展开时显示） -->
        <button
          v-if="isExpanded"
          class="absolute right-3 top-3 flex h-8 w-8 items-center justify-center rounded-full bg-gray-100 text-gray-600 transition-colors hover:bg-gray-200 hover:text-gray-800 dark:bg-slate-700 dark:text-gray-300 dark:hover:bg-slate-600"
          @click.stop="handleDismiss"
        >
          ✕
        </button>

        <!-- 封面缩略图 -->
        <div
          v-if="bookmark.resourceCoverUrl"
          class="mb-4 overflow-hidden rounded-md transition-all duration-500"
          :class="isExpanded ? 'h-48' : 'h-32'"
        >
          <img
            :src="bookmark.resourceCoverUrl"
            :alt="bookmark.bookTitle"
            class="h-full w-full object-cover"
          />
        </div>

        <!-- 金句内容 -->
        <div
          class="mb-3 leading-relaxed text-ink transition-all duration-500 dark:text-white"
          :class="isExpanded ? 'text-base' : 'text-sm'"
        >
          "{{ bookmark.content }}"
        </div>

        <!-- 作者和书名 -->
        <div
          v-if="bookmark.authorName || bookmark.bookTitle"
          class="mb-3 space-y-2"
        >
          <div
            v-if="bookmark.bookTitle"
            class="font-medium text-pop transition-all duration-500 dark:text-blue-400"
            :class="isExpanded ? 'text-base truncate' : 'text-xs truncate'"
          >
            📖 {{ bookmark.bookTitle }}
          </div>
          <div
            v-if="bookmark.authorName"
            class="text-right text-ink-light transition-all duration-500 dark:text-gray-400"
            :class="isExpanded ? 'text-sm' : 'text-xs'"
          >
            —— {{ bookmark.authorName }}
          </div>
        </div>

        <!-- 操作提示 -->
        <div
          class="text-center text-ink-light transition-all duration-500 dark:text-gray-500"
          :class="isExpanded ? 'text-sm' : 'text-xs'"
        >
          {{ isExpanded ? '点击查看详情 · 点击外部关闭' : '飘落中...' }}
        </div>
      </div>
    </div>
  </Teleport>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { getRandomBookmark, recordBookmarkClick } from '@/api/bookmark'
import { useFloatingBookmark } from '@/composables/useFloatingBookmark'

const router = useRouter()
const { setBookmarkInstance } = useFloatingBookmark()

// 书签数据
const bookmark = ref(null)
const isVisible = ref(false)
const isExpanded = ref(false) // 是否展开

// 物理引擎状态
const position = ref({ x: 0, y: -100 })
const velocity = ref({ x: 0, y: 0 })
const rotation = ref(0)
const rotationVelocity = ref(0)

// 动画状态
const animationState = ref('falling') // falling | expanded | dismissed
let animationFrameId = null

// 书签样式
const bookmarkStyle = computed(() => ({
  left: `${position.value.x}px`,
  top: `${position.value.y}px`,
  transform: `rotate(${rotation.value}deg)`,
  transition: animationState.value === 'expanded' ? 'all 0.5s ease-out' : 'none'
}))

// 物理参数（降低速度）
const GRAVITY = 0.15 // 重力加速度（降低）
const AIR_RESISTANCE = 0.99 // 空气阻力（增加阻力）
const ROTATION_DAMPING = 0.97 // 旋转阻尼（增加阻尼）
const WIND_FORCE = 0.05 // 风力扰动（降低）
const EXPAND_THRESHOLD = 0.4 // 展开位置（屏幕高度的40%）

// 物理引擎更新
function updatePhysics() {
  if (!isVisible.value || animationState.value !== 'falling') return

  // 应用重力
  velocity.value.y += GRAVITY

  // 应用随机风力（模拟微风扰动）
  velocity.value.x += (Math.random() - 0.5) * WIND_FORCE

  // 应用空气阻力
  velocity.value.x *= AIR_RESISTANCE
  velocity.value.y *= AIR_RESISTANCE

  // 更新位置
  position.value.x += velocity.value.x
  position.value.y += velocity.value.y

  // 更新旋转
  rotationVelocity.value += (Math.random() - 0.5) * 0.3
  rotationVelocity.value *= ROTATION_DAMPING
  rotation.value += rotationVelocity.value

  // 检查是否到达展开位置
  const windowHeight = window.innerHeight
  const expandPosition = windowHeight * EXPAND_THRESHOLD

  if (position.value.y >= expandPosition) {
    // 到达展开位置，停止动画并展开
    expandBookmark()
    return
  }

  // 继续动画
  animationFrameId = requestAnimationFrame(updatePhysics)
}

// 展开书签
function expandBookmark() {
  animationState.value = 'expanded'
  isExpanded.value = true

  // 停止物理动画
  stopAnimation()

  // 将书签移动到屏幕中央
  const windowWidth = window.innerWidth
  const windowHeight = window.innerHeight
  
  position.value.x = windowWidth / 2 - 192 // 192 = 展开后宽度的一半 (384/2)
  position.value.y = windowHeight * 0.35 // 屏幕35%位置

  // 重置旋转
  rotation.value = 0
}

// 开始动画
function startAnimation() {
  if (animationFrameId) {
    cancelAnimationFrame(animationFrameId)
  }
  animationState.value = 'falling'
  animationFrameId = requestAnimationFrame(updatePhysics)
}

// 停止动画
function stopAnimation() {
  if (animationFrameId) {
    cancelAnimationFrame(animationFrameId)
    animationFrameId = null
  }
}

// 触发书签掉落
async function triggerBookmark() {
  try {
    const res = await getRandomBookmark()
    if (res.code === 0 && res.data) {
      bookmark.value = res.data

      // 随机初始水平位置（屏幕宽度的 20%-80%）
      const windowWidth = window.innerWidth
      position.value.x = windowWidth * (0.2 + Math.random() * 0.6) - 128 // 128 = 初始卡片宽度的一半

      // 从屏幕顶部上方开始
      position.value.y = -200

      // 初始速度（降低）
      velocity.value.x = (Math.random() - 0.5) * 1
      velocity.value.y = Math.random() * 1

      // 初始旋转
      rotation.value = (Math.random() - 0.5) * 20
      rotationVelocity.value = (Math.random() - 0.5) * 1

      // 重置状态
      isExpanded.value = false
      animationState.value = 'falling'

      // 显示并开始动画
      isVisible.value = true
      startAnimation()
    }
  } catch (error) {
    console.error('获取书签失败:', error)
  }
}

// 处理书签点击
async function handleBookmarkClick() {
  if (!bookmark.value || !isExpanded.value) return

  // 记录点击
  try {
    await recordBookmarkClick(bookmark.value.bookmarkId)
  } catch (error) {
    console.error('记录书签点击失败:', error)
  }

  // 关闭书签
  dismissBookmark()

  // 跳转到资源详情页
  router.push(`/book/${bookmark.value.resourceId}`)
}

// 关闭书签
function handleDismiss() {
  dismissBookmark()
}

// 关闭书签动画
function dismissBookmark() {
  animationState.value = 'dismissed'
  isExpanded.value = false
  
  // 延迟隐藏，等待动画完成
  setTimeout(() => {
    isVisible.value = false
    stopAnimation()
  }, 300)
}

// 概率触发检测
let scrollCount = 0
const TRIGGER_PROBABILITY = 0.001 // 0.1% 触发概率
const SCROLL_THRESHOLD = 10 // 每滚动10次检测一次

function checkTrigger() {
  scrollCount++

  if (scrollCount >= SCROLL_THRESHOLD) {
    scrollCount = 0

    // 如果已经有书签在显示，不再触发
    if (isVisible.value) return

    // 概率判定
    if (Math.random() < TRIGGER_PROBABILITY) {
      triggerBookmark()
    }
  }
}

// 监听滚动事件
let scrollTimeout = null
function handleScroll() {
  if (scrollTimeout) return

  scrollTimeout = setTimeout(() => {
    checkTrigger()
    scrollTimeout = null
  }, 100) // 节流100ms
}

// 生命周期
onMounted(() => {
  window.addEventListener('scroll', handleScroll, { passive: true })
  
  // 注册组件实例到全局（用于手动触发测试）
  setBookmarkInstance({
    triggerBookmark
  })
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
  stopAnimation()
  setBookmarkInstance(null)
})

// 暴露方法供外部调用（测试用）
defineExpose({
  triggerBookmark
})
</script>

<style scoped>
.floating-bookmark {
  pointer-events: auto;
}

.bookmark-card {
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
}

/* 淡入淡出动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
