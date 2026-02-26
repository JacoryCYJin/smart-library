<script setup>
import { ref } from 'vue';
import { IconHome, IconBook, IconUser } from '@arco-design/web-vue/es/icon';

const collapsed = ref(false);
const onCollapse = (val) => {
  collapsed.value = val;
};
</script>

<template>
  <div class="admin-layout h-screen flex bg-canvas text-ink">
    <a-layout-sider
      breakpoint="lg"
      :width="220"
      collapsible
      :collapsed="collapsed"
      @collapse="onCollapse"
      class="h-full shadow-lg z-10"
    >
      <div class="logo h-16 flex items-center justify-center border-b border-structure">
        <span v-if="!collapsed" class="text-xl font-bold text-ink">管理后台</span>
        <span v-else class="text-xl font-bold text-ink">S</span>
      </div>
      
      <a-menu
        :default-open-keys="['1']"
        :default-selected-keys="['1_1']"
        class="w-full"
      >
        <a-menu-item key="0_1" @click="$router.push('/admin')">
          <template #icon><icon-home /></template>
          仪表盘
        </a-menu-item>
        
        <a-sub-menu key="1">
          <template #icon><icon-book /></template>
          <template #title>图书管理</template>
          <a-menu-item key="1_1" @click="$router.push('/admin/books')">图书列表</a-menu-item>
          <a-menu-item key="1_2">分类管理</a-menu-item>
        </a-sub-menu>
        
        <a-sub-menu key="2">
          <template #icon><icon-user /></template>
          <template #title>用户管理</template>
          <a-menu-item key="2_1">用户列表</a-menu-item>
        </a-sub-menu>
      </a-menu>
    </a-layout-sider>

    <a-layout class="flex-1 flex flex-col min-w-0 overflow-hidden">
      <a-layout-header class="h-16 bg-white shadow-sm flex items-center justify-between px-6 border-b border-structure">
        <h2 class="text-lg font-medium text-ink">欢迎回来，管理员</h2>
        <a-space>
          <a-button type="text">通知</a-button>
          <a-avatar :style="{ backgroundColor: '#3370ff' }">Admin</a-avatar>
        </a-space>
      </a-layout-header>

      <a-layout-content class="flex-1 p-6 overflow-auto bg-canvas">
        <slot />
      </a-layout-content>
    </a-layout>
  </div>
</template>

<style scoped>
/* Arco 的一些样式覆盖可以写在这里 */
.admin-layout :deep(.arco-layout-sider) {
  background-color: #fff;
}
</style>