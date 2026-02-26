<script setup>
import { onMounted } from 'vue'
import { useFavorite } from '@/composables/useFavorite'

/**
 * 收藏按钮组件
 * 使用示例：<FavoriteButton :resource-id="bookId" />
 */
const props = defineProps({
  resourceId: {
    type: String,
    required: true
  },
  // 按钮样式：icon（仅图标）、text（带文字）
  variant: {
    type: String,
    default: 'icon',
    validator: (value) => ['icon', 'text'].includes(value)
  }
})

const { isFavorited, loading, checkFavoriteStatus, toggleFavorite } = useFavorite(props.resourceId)

onMounted(() => {
  checkFavoriteStatus()
})
</script>

<template>
  <button
    type="button"
    @click="toggleFavorite"
    :disabled="loading"
    class="favorite-button"
    :class="{ 'is-favorited': isFavorited, 'is-loading': loading }"
    :aria-label="isFavorited ? '取消收藏' : '收藏'"
  >
    <!-- 收藏图标 -->
    <svg
      viewBox="0 0 24 24"
      :fill="isFavorited ? 'currentColor' : 'none'"
      stroke="currentColor"
      class="favorite-icon"
      stroke-width="2"
      stroke-linecap="round"
      stroke-linejoin="round"
    >
      <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z" />
    </svg>

    <!-- 文字（可选） -->
    <span v-if="variant === 'text'" class="favorite-text">
      {{ isFavorited ? '已收藏' : '收藏' }}
    </span>
  </button>
</template>

<style scoped>
.favorite-button {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem;
  border: none;
  background: transparent;
  color: var(--color-ink-light);
  cursor: pointer;
  transition: all 0.2s ease;
  border-radius: 0.5rem;
}

.favorite-button:hover:not(:disabled) {
  background-color: rgba(0, 0, 0, 0.05);
  color: var(--color-pop);
}

.favorite-button.is-favorited {
  color: var(--color-pop);
}

.favorite-button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.favorite-icon {
  width: 1.5rem;
  height: 1.5rem;
  transition: transform 0.2s ease;
}

.favorite-button:hover:not(:disabled) .favorite-icon {
  transform: scale(1.1);
}

.favorite-button.is-favorited .favorite-icon {
  animation: heartbeat 0.3s ease;
}

@keyframes heartbeat {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.2);
  }
}

.favorite-text {
  font-size: 0.875rem;
  font-weight: 500;
}
</style>
