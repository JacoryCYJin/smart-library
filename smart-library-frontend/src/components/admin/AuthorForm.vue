<template>
  <a-modal
    :visible="visible"
    :title="isEdit ? '编辑作者' : '添加作者'"
    @cancel="handleCancel"
    @ok="handleSubmit"
    :ok-loading="submitting"
    width="600px"
  >
    <a-form :model="form" ref="formRef" layout="vertical">
      <div class="grid grid-cols-2 gap-4">
        <a-form-item field="name" label="姓名" :rules="[{ required: true, message: '请输入作者姓名' }]">
          <a-input v-model="form.name" placeholder="请输入作者姓名" />
        </a-form-item>

        <a-form-item field="originalName" label="原名">
          <a-input v-model="form.originalName" placeholder="请输入作者原名（可选）" />
        </a-form-item>

        <a-form-item field="country" label="国籍">
          <a-input v-model="form.country" placeholder="请输入国籍（可选）" />
        </a-form-item>

        <a-form-item field="sourceOrigin" label="数据来源">
          <a-select v-model="form.sourceOrigin" placeholder="请选择来源">
            <a-option :value="1">豆瓣</a-option>
            <a-option :value="2">百科</a-option>
            <a-option :value="99">手动录入</a-option>
          </a-select>
        </a-form-item>
      </div>

      <a-form-item field="photoUrl" label="头像 URL">
        <a-input v-model="form.photoUrl" placeholder="请输入头像图片 URL（可选）" />
        <div v-if="form.photoUrl" class="mt-2">
          <img :src="form.photoUrl" alt="头像预览" class="w-16 h-16 object-cover rounded shadow-sm" />
        </div>
      </a-form-item>

      <a-form-item field="description" label="简介">
        <a-textarea
          v-model="form.description"
          placeholder="请输入作者简介（可选）"
          :auto-size="{ minRows: 3, maxRows: 6 }"
        />
      </a-form-item>

      <a-form-item field="sourceUrl" label="来源 URL">
        <a-input v-model="form.sourceUrl" placeholder="请输入来源页面 URL（可选）" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'
import { Message } from '@arco-design/web-vue'
import { createAuthor, updateAuthor } from '@/api/admin'

const props = defineProps({
  author: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['close', 'success'])

const visible = ref(true)
const formRef = ref(null)
const submitting = ref(false)
const isEdit = ref(false)

const form = reactive({
  name: '',
  originalName: '',
  country: '',
  photoUrl: '',
  description: '',
  sourceOrigin: 99,
  sourceUrl: ''
})

watch(() => props.author, (newVal) => {
  if (newVal) {
    isEdit.value = true
    Object.assign(form, {
      name: newVal.name,
      originalName: newVal.originalName || '',
      country: newVal.country || '',
      photoUrl: newVal.photoUrl || '',
      description: newVal.description || '',
      sourceOrigin: newVal.sourceOrigin || 99,
      sourceUrl: newVal.sourceUrl || ''
    })
  } else {
    isEdit.value = false
    Object.assign(form, {
      name: '',
      originalName: '',
      country: '',
      photoUrl: '',
      description: '',
      sourceOrigin: 99,
      sourceUrl: ''
    })
  }
}, { immediate: true })

const handleCancel = () => {
  visible.value = false
  emit('close')
}

const handleSubmit = async () => {
  const errors = await formRef.value.validate()
  if (errors) return

  submitting.value = true
  try {
    let res
    if (isEdit.value) {
      res = await updateAuthor(props.author.authorId, form)
    } else {
      res = await createAuthor(form)
    }

    if (res.code === 0) {
      Message.success(isEdit.value ? '修改成功' : '添加成功')
      emit('success')
    } else {
      Message.error(res.message || (isEdit.value ? '修改失败' : '添加失败'))
    }
  } catch (error) {
    console.error(error)
    Message.error(isEdit.value ? '修改失败' : '添加失败')
  } finally {
    submitting.value = false
  }
}
</script>
