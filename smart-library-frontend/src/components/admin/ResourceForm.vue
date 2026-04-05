<template>
  <a-modal
    v-model:visible="visible"
    :title="isEdit ? '编辑资源' : '添加资源'"
    width="800px"
    @cancel="handleCancel"
    @before-ok="handleSubmit"
  >
    <a-form :model="formData" layout="vertical">
      <!-- 资源类型 -->
      <a-form-item label="资源类型" required>
        <a-radio-group v-model="formData.type">
          <a-radio :value="1">图书</a-radio>
          <a-radio :value="2">文献</a-radio>
        </a-radio-group>
      </a-form-item>

      <!-- 标题 -->
      <a-form-item label="标题" required>
        <a-input v-model="formData.title" placeholder="请输入标题" />
      </a-form-item>

      <!-- 封面URL -->
      <a-form-item label="封面URL">
        <a-input v-model="formData.coverUrl" placeholder="请输入封面图片URL" />
      </a-form-item>

      <!-- 简介 -->
      <a-form-item label="简介">
        <a-textarea
          v-model="formData.summary"
          placeholder="请输入简介"
          :rows="4"
        />
      </a-form-item>

      <!-- 出版日期 -->
      <a-form-item label="出版/发表日期">
        <a-date-picker
          v-model="formData.pubDate"
          style="width: 100%"
          format="YYYY-MM-DD"
        />
      </a-form-item>

      <!-- 图书特有字段 -->
      <template v-if="formData.type === 1">
        <a-form-item label="ISBN">
          <a-input v-model="formData.isbn" placeholder="请输入ISBN号" />
        </a-form-item>

        <a-form-item label="出版社">
          <a-input v-model="formData.publisher" placeholder="请输入出版社" />
        </a-form-item>

        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="价格">
              <a-input-number
                v-model="formData.price"
                placeholder="请输入价格"
                :min="0"
                :precision="2"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="页数">
              <a-input-number
                v-model="formData.pageCount"
                placeholder="请输入页数"
                :min="0"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
        </a-row>
      </template>

      <!-- 文献特有字段 -->
      <template v-if="formData.type === 2">
        <a-form-item label="DOI">
          <a-input v-model="formData.doi" placeholder="请输入DOI" />
        </a-form-item>

        <a-form-item label="期刊名称">
          <a-input v-model="formData.journal" placeholder="请输入期刊名称" />
        </a-form-item>
      </template>

      <!-- 原站链接 -->
      <a-form-item label="原站链接">
        <a-input v-model="formData.sourceUrl" placeholder="请输入原站链接" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'
import { Message } from '@arco-design/web-vue'
import { createResource, updateResource, getResourceDetail } from '@/api/admin'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  resourceId: {
    type: String,
    default: null
  }
})

const emit = defineEmits(['update:modelValue', 'success'])

const visible = ref(props.modelValue)
const isEdit = ref(false)
const loading = ref(false)

// 表单数据
const formData = reactive({
  type: 1,
  title: '',
  coverUrl: '',
  summary: '',
  pubDate: null,
  isbn: '',
  publisher: '',
  price: null,
  pageCount: null,
  doi: '',
  journal: '',
  sourceUrl: '',
  sourceOrigin: 99 // 手动录入
})

// 监听弹窗显示状态
watch(() => props.modelValue, (val) => {
  visible.value = val
  if (val) {
    if (props.resourceId) {
      isEdit.value = true
      loadResourceDetail()
    } else {
      isEdit.value = false
      resetForm()
    }
  }
})

// 监听弹窗关闭
watch(visible, (val) => {
  emit('update:modelValue', val)
})

// 加载资源详情
const loadResourceDetail = async () => {
  loading.value = true
  try {
    const res = await getResourceDetail(props.resourceId)
    if (res.code === 0) {
      const resource = res.data.resource
      Object.assign(formData, {
        type: resource.type,
        title: resource.title,
        coverUrl: resource.coverUrl,
        summary: resource.summary,
        pubDate: resource.pubDate,
        isbn: resource.isbn,
        publisher: resource.publisher,
        price: resource.price,
        pageCount: resource.pageCount,
        doi: resource.doi,
        journal: resource.journal,
        sourceUrl: resource.sourceUrl
      })
    }
  } catch (error) {
    Message.error('加载资源详情失败')
  } finally {
    loading.value = false
  }
}

// 重置表单
const resetForm = () => {
  Object.assign(formData, {
    type: 1,
    title: '',
    coverUrl: '',
    summary: '',
    pubDate: null,
    isbn: '',
    publisher: '',
    price: null,
    pageCount: null,
    doi: '',
    journal: '',
    sourceUrl: '',
    sourceOrigin: 99
  })
}

// 取消
const handleCancel = () => {
  visible.value = false
}

// 提交
const handleSubmit = async () => {
  // 验证必填字段
  if (!formData.title) {
    Message.error('请输入标题')
    return false
  }

  loading.value = true
  try {
    const data = { ...formData }
    
    // 转换日期格式
    if (data.pubDate) {
      data.pubDate = data.pubDate.split('T')[0]
    }

    let res
    if (isEdit.value) {
      res = await updateResource(props.resourceId, data)
    } else {
      res = await createResource(data)
    }

    if (res.code === 0) {
      Message.success(isEdit.value ? '更新成功' : '添加成功')
      visible.value = false
      emit('success')
      return true
    } else {
      Message.error(res.message || '操作失败')
      return false
    }
  } catch (error) {
    Message.error('操作失败')
    return false
  } finally {
    loading.value = false
  }
}
</script>
