<template>
  <div class="admin-pagination py-6 flex items-center justify-center gap-8">
    <!-- 总数信息 -->
    <div class="text-sm text-ink-light">
      共 <span class="text-ink font-medium">{{ total }}</span> 条
    </div>

    <!-- 分页按钮 -->
    <div class="flex items-center gap-2">
      <!-- 上一页 -->
      <button
        @click="handlePrev"
        :disabled="current === 1"
        :class="[
          'w-8 h-8 flex items-center justify-center rounded-lg border border-structure text-ink-light transition-all duration-200',
          current === 1
            ? 'opacity-40 cursor-not-allowed'
            : 'hover:text-ink hover:border-ink-light'
        ]"
      >
        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
        </svg>
      </button>

      <!-- 页码 -->
      <div class="flex items-center gap-1">
        <button
          v-for="page in visiblePages"
          :key="page"
          @click="handlePageClick(page)"
          :disabled="page === '...'"
          :class="[
            'min-w-[32px] h-8 px-2 flex items-center justify-center rounded-lg text-sm transition-all duration-200',
            page === current
              ? 'bg-ink text-white'
              : page === '...'
              ? 'text-ink-light cursor-default'
              : 'text-ink-light hover:text-ink hover:bg-canvas cursor-pointer'
          ]"
        >
          {{ page === '...' ? '...' : page }}
        </button>
      </div>

      <!-- 下一页 -->
      <button
        @click="handleNext"
        :disabled="current === totalPages"
        :class="[
          'w-8 h-8 flex items-center justify-center rounded-lg border border-structure text-ink-light transition-all duration-200',
          current === totalPages
            ? 'opacity-40 cursor-not-allowed'
            : 'hover:text-ink hover:border-ink-light'
        ]"
      >
        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
        </svg>
      </button>
    </div>

    <!-- 每页条数 -->
    <div class="flex items-center gap-2 text-sm text-ink-light">
      <span>每页</span>
      <select
        :value="pageSize"
        @change="handlePageSizeChange"
        class="px-3 py-1.5 bg-white border border-structure rounded-lg text-ink focus:outline-none focus:ring-2 focus:ring-ink-light/30 transition-all cursor-pointer"
      >
        <option :value="10">10</option>
        <option :value="20">20</option>
        <option :value="50">50</option>
        <option :value="100">100</option>
      </select>
      <span>条</span>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  current: {
    type: Number,
    required: true
  },
  pageSize: {
    type: Number,
    required: true
  },
  total: {
    type: Number,
    required: true
  }
})

const emit = defineEmits(['update:current', 'update:pageSize', 'change', 'pageSizeChange'])

// 计算总页数
const totalPages = computed(() => {
  return Math.ceil(props.total / props.pageSize)
})

// 计算可见页码
const visiblePages = computed(() => {
  const pages = []
  const total = totalPages.value
  const current = props.current

  if (total <= 7) {
    // 总页数小于等于7，全部显示
    for (let i = 1; i <= total; i++) {
      pages.push(i)
    }
  } else {
    // 总页数大于7，显示省略号
    if (current <= 4) {
      // 当前页在前面
      for (let i = 1; i <= 5; i++) {
        pages.push(i)
      }
      pages.push('...')
      pages.push(total)
    } else if (current >= total - 3) {
      // 当前页在后面
      pages.push(1)
      pages.push('...')
      for (let i = total - 4; i <= total; i++) {
        pages.push(i)
      }
    } else {
      // 当前页在中间
      pages.push(1)
      pages.push('...')
      for (let i = current - 1; i <= current + 1; i++) {
        pages.push(i)
      }
      pages.push('...')
      pages.push(total)
    }
  }

  return pages
})

// 上一页
const handlePrev = () => {
  if (props.current > 1) {
    const newPage = props.current - 1
    emit('update:current', newPage)
    emit('change', newPage)
  }
}

// 下一页
const handleNext = () => {
  if (props.current < totalPages.value) {
    const newPage = props.current + 1
    emit('update:current', newPage)
    emit('change', newPage)
  }
}

// 点击页码
const handlePageClick = (page) => {
  if (page !== '...' && page !== props.current) {
    emit('update:current', page)
    emit('change', page)
  }
}

// 改变每页条数
const handlePageSizeChange = (e) => {
  const newPageSize = Number(e.target.value)
  emit('update:pageSize', newPageSize)
  emit('update:current', 1)
  emit('pageSizeChange', newPageSize)
}
</script>
