<template>
  <div class="space-y-6">
    <!-- 页面标题 -->
    <div class="flex items-center justify-between">
      <div>
        <h2 class="text-2xl font-semibold text-ink">分类管理</h2>
        <p class="mt-1 text-sm text-ink-light">管理图书分类体系</p>
      </div>
      <button
        @click="handleAdd"
        class="px-6 py-2 bg-ink text-white rounded-lg hover:opacity-90 transition-opacity flex items-center gap-2"
      >
        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
        </svg>
        <span>添加分类</span>
      </button>
    </div>

    <!-- 分类树 -->
    <div class="bg-white rounded-xl overflow-hidden" style="box-shadow: 0 2px 8px rgba(16, 42, 67, 0.08)">
      <a-table
        :columns="columns"
        :data="categoryTree"
        :loading="loading"
        :pagination="false"
        :scroll="{ x: 900 }"
        row-key="categoryId"
        v-model:expandedKeys="expandedKeys"
        :indent-size="20"
      >
        <template #categoryName="{ record }">
          <div class="flex items-center gap-2">
            <span :class="[
              'font-medium',
              record.level === 1 ? 'text-ink text-base' : 
              record.level === 2 ? 'text-ink-light text-sm' : 
              'text-ink-light/70 text-sm'
            ]">
              {{ record.categoryName }}
            </span>
          </div>
        </template>

        <template #level="{ record }">
          <span v-if="record.level === 1" class="text-xs px-2 py-0.5 bg-ink/10 text-ink rounded font-medium">
            一级
          </span>
          <span v-else-if="record.level === 2" class="text-xs px-2 py-0.5 bg-ink-light/10 text-ink-light rounded">
            二级
          </span>
          <span v-else-if="record.level === 3" class="text-xs px-2 py-0.5 bg-structure text-ink-light rounded">
            三级
          </span>
        </template>

        <template #resourceCount="{ record }">
          <a-tag :color="record.level === 1 ? 'blue' : 'arcoblue'">
            {{ record.resourceCount || 0 }}
          </a-tag>
        </template>

        <template #actions="{ record }">
          <a-space>
            <a-button
              type="text"
              size="small"
              @click="handleEdit(record)"
            >
              编辑
            </a-button>
            <a-popconfirm
              :content="getDeleteConfirmText(record)"
              @ok="handleDelete(record)"
            >
              <a-button
                type="text"
                status="danger"
                size="small"
              >
                删除
              </a-button>
            </a-popconfirm>
          </a-space>
        </template>
      </a-table>
    </div>

    <!-- 分类表单弹窗 -->
    <CategoryForm
      v-model="showForm"
      :is-edit="isEdit"
      :category="currentCategory"
      :category-tree="categoryTree"
      @success="handleFormSuccess"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Message } from '@arco-design/web-vue'
import { getCategoryTree, createCategory, updateCategory, deleteCategory } from '@/api/admin'
import CategoryForm from '@/components/admin/CategoryForm.vue'

// 状态
const loading = ref(false)
const categoryTree = ref([])
const showForm = ref(false)
const isEdit = ref(false)
const currentCategory = ref(null)
const expandedKeys = ref([])

// 表格列定义
const columns = [
  {
    title: 'ID',
    dataIndex: 'categoryId',
    width: 280,
    ellipsis: true,
    tooltip: true
  },
  {
    title: '分类名称',
    dataIndex: 'categoryName',
    width: 200
  },
  {
    title: '层级',
    slotName: 'level',
    width: 80
  },
  {
    title: '资源数量',
    slotName: 'resourceCount',
    width: 120
  },
  {
    title: '操作',
    slotName: 'actions',
    width: 150,
    fixed: 'right'
  }
]

/**
 * 递归获取所有分类 ID
 */
function getAllCategoryIds(nodes) {
  let ids = []
  nodes.forEach(node => {
    ids.push(node.categoryId)
    if (node.children && node.children.length > 0) {
      ids = ids.concat(getAllCategoryIds(node.children))
    }
  })
  return ids
}

/**
 * 加载分类树
 */
async function loadCategoryTree() {
  loading.value = true
  try {
    const res = await getCategoryTree()
    
    if (res.code === 0) {
      categoryTree.value = res.data || []
      expandedKeys.value = getAllCategoryIds(categoryTree.value)
    } else {
      Message.error(res.message || '加载分类树失败')
    }
  } catch {
    Message.error('加载分类树失败')
  } finally {
    loading.value = false
  }
}

/**
 * 获取删除确认文本
 */
function getDeleteConfirmText(category) {
  const hasChildren = category.children && category.children.length > 0
  const resourceCount = category.resourceCount || 0
  
  if (hasChildren) {
    const childCount = category.children.length
    return `此分类下有 ${childCount} 个子分类，删除后子分类将变为孤立分类（无父级）。确定要删除吗？`
  }
  
  if (resourceCount > 0) {
    return `此分类下有 ${resourceCount} 个资源，无法删除。请先移除关联资源。`
  }
  
  return '确定要删除这个分类吗？'
}

/**
 * 添加分类
 */
function handleAdd() {
  isEdit.value = false
  currentCategory.value = null
  showForm.value = true
}

/**
 * 编辑分类
 */
function handleEdit(category) {
  isEdit.value = true
  currentCategory.value = category
  showForm.value = true
}

/**
 * 删除分类
 */
async function handleDelete(category) {
  try {
    const res = await deleteCategory(category.categoryId)
    if (res.code === 0) {
      Message.success('删除成功')
      loadCategoryTree()
    } else {
      Message.error(res.message || '删除失败')
    }
  } catch {
    Message.error('删除失败')
  }
}

/**
 * 表单提交成功
 */
async function handleFormSuccess(formData) {
  try {
    let res
    if (isEdit.value) {
      // 编辑
      res = await updateCategory(currentCategory.value.categoryId, formData)
    } else {
      // 添加
      res = await createCategory(formData)
    }
    
    if (res.code === 0) {
      Message.success(isEdit.value ? '编辑成功' : '添加成功')
      showForm.value = false
      loadCategoryTree()
    } else {
      Message.error(res.message || (isEdit.value ? '编辑失败' : '添加失败'))
    }
  } catch {
    Message.error(isEdit.value ? '编辑失败' : '添加失败')
  }
}

onMounted(() => {
  loadCategoryTree()
})
</script>
