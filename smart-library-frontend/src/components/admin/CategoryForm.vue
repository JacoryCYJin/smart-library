<template>
  <a-modal
    v-model:visible="visible"
    :title="isEdit ? '编辑分类' : '添加分类'"
    :width="600"
    @cancel="handleCancel"
    @before-ok="handleSubmit"
  >
    <a-form :model="formData" layout="vertical">
      <a-form-item label="分类名称" required>
        <a-input
          v-model="formData.categoryName"
          placeholder="请输入分类名称"
          :max-length="50"
        />
      </a-form-item>

      <a-form-item label="父级分类">
        <a-tree-select
          v-model="formData.parentId"
          :data="categoryTreeOptions"
          placeholder="请选择父级分类（不选则为顶级分类）"
          allow-clear
          :field-names="{ key: 'categoryId', title: 'categoryName', children: 'children' }"
        />
      </a-form-item>

      <a-form-item label="排序">
        <a-input-number
          v-model="formData.sortOrder"
          placeholder="数字越小越靠前"
          :min="0"
          :max="9999"
        />
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script setup>
import { ref, watch } from 'vue'
import { Message } from '@arco-design/web-vue'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  isEdit: {
    type: Boolean,
    default: false
  },
  category: {
    type: Object,
    default: null
  },
  categoryTree: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits(['update:modelValue', 'success'])

const visible = ref(props.modelValue)
const formData = ref({
  categoryName: '',
  parentId: null,
  sortOrder: 0
})

// 分类树选项（过滤掉当前编辑的分类及其子分类）
const categoryTreeOptions = ref([])

watch(() => props.modelValue, (val) => {
  visible.value = val
  if (val) {
    if (props.isEdit && props.category) {
      // 编辑模式：填充表单
      formData.value = {
        categoryName: props.category.categoryName,
        parentId: props.category.parentId || null,
        sortOrder: props.category.sortOrder || 0
      }
      // 过滤掉当前分类及其子分类
      categoryTreeOptions.value = filterCurrentCategory(props.categoryTree, props.category.categoryId)
    } else {
      // 添加模式：重置表单
      formData.value = {
        categoryName: '',
        parentId: null,
        sortOrder: 0
      }
      categoryTreeOptions.value = props.categoryTree
    }
  }
})

watch(visible, (val) => {
  emit('update:modelValue', val)
})

/**
 * 过滤掉当前分类及其子分类（避免循环引用）
 */
function filterCurrentCategory(tree, currentId) {
  return tree
    .filter(node => node.categoryId !== currentId)
    .map(node => ({
      ...node,
      children: node.children ? filterCurrentCategory(node.children, currentId) : []
    }))
}

/**
 * 提交表单
 */
async function handleSubmit(done) {
  // 验证
  if (!formData.value.categoryName?.trim()) {
    Message.error('请输入分类名称')
    done(false)
    return
  }

  // 触发成功事件
  emit('success', formData.value)
  done()
}

/**
 * 取消
 */
function handleCancel() {
  visible.value = false
}
</script>
