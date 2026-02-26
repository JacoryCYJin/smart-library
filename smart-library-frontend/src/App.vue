<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import WebLayout from '@/layouts/WebLayout.vue'
import AdminLayout from '@/layouts/AdminLayout.vue'

const route = useRoute()

// 动态计算当前应该使用的布局组件
const layout = computed(() => {
  if (route.meta.layout === 'none') {
    return null
  }
  // 如果路由配置里写了 meta: { layout: 'admin' }，就用后台布局
  if (route.meta.layout === 'admin') {
    return AdminLayout
  }
  // 否则默认都用前台布局
  return WebLayout
})

// 需要缓存的页面列表
const keepAlivePages = computed(() => {
  // 缓存 BookView，保持滚动位置和数据状态
  return ['BookView']
})
</script>

<template>
  <component v-if="layout" :is="layout">
    <router-view v-slot="{ Component }">
      <keep-alive :include="keepAlivePages">
        <component :is="Component" />
      </keep-alive>
    </router-view>
  </component>
  <router-view v-else />
</template>