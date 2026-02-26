# Composables 使用说明

## useFavorite

用户收藏功能的可复用逻辑。

### 使用方法

```vue
<script setup>
import { onMounted } from 'vue'
import { useFavorite } from '@/composables/useFavorite'

const bookId = 'book_001'
const { isFavorited, loading, checkFavoriteStatus, toggleFavorite } = useFavorite(bookId)

onMounted(() => {
  // 组件挂载时检查收藏状态
  checkFavoriteStatus()
})
</script>

<template>
  <button @click="toggleFavorite" :disabled="loading">
    {{ isFavorited ? '已收藏' : '收藏' }}
  </button>
</template>
```

### 返回值

- `isFavorited` (Ref<boolean>): 是否已收藏
- `loading` (Ref<boolean>): 操作加载状态
- `checkFavoriteStatus` (Function): 检查收藏状态
- `toggleFavorite` (Function): 切换收藏状态（添加/取消）

### 特性

- 自动检查登录状态，未登录时跳转登录页
- 操作成功后显示提示消息
- 自动处理错误情况
- 防止重复点击（loading 状态）

## FavoriteButton 组件

封装好的收藏按钮组件，开箱即用。

### 使用方法

```vue
<template>
  <!-- 仅图标样式 -->
  <FavoriteButton :resource-id="bookId" />

  <!-- 带文字样式 -->
  <FavoriteButton :resource-id="bookId" variant="text" />
</template>

<script setup>
import FavoriteButton from '@/components/book/FavoriteButton.vue'

const bookId = 'book_001'
</script>
```

### Props

- `resourceId` (String, required): 资源ID
- `variant` (String, default: 'icon'): 按钮样式
  - `'icon'`: 仅显示图标
  - `'text'`: 显示图标 + 文字

### 样式

- 未收藏：空心爱心图标，灰色
- 已收藏：实心爱心图标，红色
- Hover：放大动画
- 点击：心跳动画
