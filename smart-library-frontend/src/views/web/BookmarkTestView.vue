<template>
  <div class="min-h-screen bg-canvas p-8">
    <div class="mx-auto max-w-4xl">
      <h1 class="mb-8 text-3xl font-bold text-ink">漂流书签测试页面</h1>

      <!-- 测试按钮 -->
      <div class="mb-8 rounded-lg bg-white p-6 shadow-md dark:bg-slate-800">
        <h2 class="mb-4 text-xl font-semibold text-ink dark:text-white">
          手动触发测试
        </h2>
        <button
          @click="handleTrigger"
          class="rounded-lg bg-pop px-6 py-3 text-white transition-colors hover:bg-red-600"
        >
          触发书签掉落
        </button>
        <p class="mt-4 text-sm text-ink-light dark:text-gray-400">
          点击按钮可以立即触发一次书签掉落，用于测试动画效果。
        </p>
      </div>

      <!-- 功能说明 -->
      <div class="mb-8 rounded-lg bg-white p-6 shadow-md dark:bg-slate-800">
        <h2 class="mb-4 text-xl font-semibold text-ink dark:text-white">
          功能说明
        </h2>
        <ul class="space-y-2 text-ink-light dark:text-gray-300">
          <li>
            <strong>触发机制</strong>: 用户点击页面任意位置时，系统以极低概率（0.5%）触发书签掉落
          </li>
          <li>
            <strong>物理动画</strong>: 书签从屏幕顶部掉落，具有重力、空气阻力和随机风力效果
          </li>
          <li>
            <strong>交互响应</strong>: 点击书签可跳转到对应资源详情页，忽略则自然掉出屏幕
          </li>
          <li>
            <strong>游客可见</strong>: 未登录用户也可以看到书签掉落（不过滤已收藏/评论资源）
          </li>
          <li>
            <strong>智能过滤</strong>: 已登录用户不会看到已收藏或已评论过的资源书签
          </li>
          <li>
            <strong>数据统计</strong>: 系统会记录每个书签的点击次数，用于分析用户兴趣
          </li>
        </ul>
      </div>

      <!-- 技术细节 -->
      <div class="mb-8 rounded-lg bg-white p-6 shadow-md dark:bg-slate-800">
        <h2 class="mb-4 text-xl font-semibold text-ink dark:text-white">
          技术实现
        </h2>
        <div class="space-y-4 text-sm text-ink-light dark:text-gray-300">
          <div>
            <h3 class="mb-2 font-semibold text-ink dark:text-white">
              后端 API
            </h3>
            <ul class="ml-4 list-disc space-y-1">
              <li>GET /bookmark/random - 随机获取书签</li>
              <li>POST /bookmark/click/:id - 记录点击统计</li>
            </ul>
          </div>
          <div>
            <h3 class="mb-2 font-semibold text-ink dark:text-white">
              前端组件
            </h3>
            <ul class="ml-4 list-disc space-y-1">
              <li>FloatingBookmark.vue - 书签组件（全局挂载）</li>
              <li>使用 requestAnimationFrame 实现流畅物理动画</li>
              <li>Teleport 到 body，确保在最顶层渲染</li>
            </ul>
          </div>
          <div>
            <h3 class="mb-2 font-semibold text-ink dark:text-white">
              物理参数
            </h3>
            <ul class="ml-4 list-disc space-y-1">
              <li>重力加速度: 0.15</li>
              <li>空气阻力: 0.99</li>
              <li>风力扰动: 0.05</li>
              <li>旋转阻尼: 0.97</li>
            </ul>
          </div>
          <div>
            <h3 class="mb-2 font-semibold text-ink dark:text-white">
              触发机制
            </h3>
            <ul class="ml-4 list-disc space-y-1">
              <li>触发方式: 全局点击事件</li>
              <li>触发概率: 0.5%（低概率彩蛋）</li>
              <li>防重复: 已有书签显示时不再触发</li>
            </ul>
          </div>
        </div>
      </div>

      <!-- 点击测试区域 -->
      <div class="rounded-lg bg-white p-6 shadow-md dark:bg-slate-800">
        <h2 class="mb-4 text-xl font-semibold text-ink dark:text-white">
          点击测试区域
        </h2>
        <p class="mb-4 text-ink-light dark:text-gray-400">
          在此区域内随意点击，有低概率触发书签掉落（概率较低，可能需要多次点击）
        </p>
        <div class="grid grid-cols-4 gap-4">
          <div
            v-for="i in 40"
            :key="i"
            class="flex h-24 cursor-pointer items-center justify-center rounded-lg bg-gradient-to-br from-blue-50 to-purple-50 transition-all hover:scale-105 hover:shadow-lg dark:from-slate-700 dark:to-slate-900"
          >
            <span class="text-lg font-semibold text-ink dark:text-white">
              {{ i }}
            </span>
          </div>
        </div>
        <p class="mt-4 text-sm text-ink-light dark:text-gray-400">
          💡 提示：点击任意方块都有可能触发书签彩蛋！
        </p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useFloatingBookmark } from '@/composables/useFloatingBookmark'

const { triggerBookmark } = useFloatingBookmark()

function handleTrigger() {
  console.log('🎯 按钮触发书签')
  triggerBookmark()
}
</script>
