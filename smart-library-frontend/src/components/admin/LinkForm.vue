<template>
  <a-modal
    :visible="visible"
    :title="isEdit ? '编辑链接' : '添加链接'"
    @cancel="handleCancel"
    @ok="handleSubmit"
    :ok-loading="submitting"
    width="500px"
  >
    <a-form :model="form" ref="formRef" layout="vertical">
      <a-form-item field="resourceId" label="资源ID" :rules="[{ required: true, message: '请输入资源ID' }]">
        <a-input-number v-model="form.resourceId" placeholder="请输入关联的资源ID" />
      </a-form-item>
      
      <a-form-item field="title" label="链接标题" :rules="[{ required: true, message: '请输入标题' }]">
        <a-input v-model="form.title" placeholder="请输入链接标题" />
      </a-form-item>
      
      <a-form-item field="url" label="链接URL" :rules="[{ required: true, message: '请输入URL' }]">
        <a-input v-model="form.url" placeholder="请输入完整的URL地址" />
      </a-form-item>

      <a-form-item field="linkType" label="链接类型" :rules="[{ required: true, message: '请选择链接类型' }]">
        <a-select v-model="form.linkType" placeholder="请选择类型">
          <a-option :value="1">豆瓣</a-option>
          <a-option :value="2">下载</a-option>
          <a-option :value="3">视频解读</a-option>
        </a-select>
      </a-form-item>

      <a-form-item field="platform" label="所属平台" :rules="[{ required: true, message: '请选择平台' }]">
        <a-select v-model="form.platform" placeholder="请选择平台">
          <a-option :value="1">豆瓣</a-option>
          <a-option :value="2">Z-Library</a-option>
          <a-option :value="3">B站</a-option>
          <a-option :value="4">YouTube</a-option>
          <a-option :value="99">其他</a-option>
        </a-select>
      </a-form-item>

      <a-form-item field="status" label="状态">
        <a-switch v-model="form.status" :checked-value="1" :unchecked-value="0" />
        <span class="ml-2 text-ink-light">{{ form.status === 1 ? '启用' : '禁用' }}</span>
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'
import { Message } from '@arco-design/web-vue'
import { createLink, updateLink } from '@/api/admin'

const props = defineProps({
  link: {
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
  resourceId: null,
  title: '',
  url: '',
  linkType: 1,
  platform: 1,
  status: 1
})

watch(() => props.link, (newVal) => {
  if (newVal) {
    isEdit.value = true
    Object.assign(form, {
      resourceId: newVal.resourceId,
      title: newVal.title,
      url: newVal.url,
      linkType: newVal.linkType,
      platform: newVal.platform,
      status: newVal.status
    })
  } else {
    isEdit.value = false
    Object.assign(form, {
      resourceId: null,
      title: '',
      url: '',
      linkType: 1,
      platform: 1,
      status: 1
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
      res = await updateLink(props.link.linkId, form)
    } else {
      res = await createLink(form)
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
