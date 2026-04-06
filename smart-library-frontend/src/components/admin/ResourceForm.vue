<template>
  <a-modal
    v-model:visible="visible"
    :title="modalTitle"
    width="800px"
    @cancel="handleCancel"
  >
    <a-form :model="formData" layout="vertical">
      <!-- 资源类型 -->
      <a-form-item label="资源类型" required>
        <a-radio-group v-model="formData.type" :disabled="!isEditMode">
          <a-radio :value="1">图书</a-radio>
          <a-radio :value="2">文献</a-radio>
        </a-radio-group>
      </a-form-item>

      <!-- 标题 -->
      <a-form-item label="标题" required>
        <a-input v-model="formData.title" placeholder="请输入标题" :disabled="!isEditMode" />
      </a-form-item>

      <!-- 封面URL -->
      <a-form-item label="封面URL">
        <a-input v-model="formData.coverUrl" placeholder="请输入封面图片URL" :disabled="!isEditMode" />
      </a-form-item>

      <!-- 简介 -->
      <a-form-item label="简介">
        <a-textarea
          v-model="formData.summary"
          placeholder="请输入简介"
          :rows="4"
          :disabled="!isEditMode"
        />
      </a-form-item>

      <!-- 出版日期 -->
      <a-form-item label="出版/发表日期">
        <a-date-picker
          v-model="formData.pubDate"
          style="width: 100%"
          format="YYYY-MM-DD"
          :disabled="!isEditMode"
        />
      </a-form-item>

      <!-- 图书特有字段 -->
      <template v-if="formData.type === 1">
        <a-form-item label="ISBN">
          <a-input v-model="formData.isbn" placeholder="请输入ISBN号" :disabled="!isEditMode" />
        </a-form-item>

        <a-form-item label="出版社">
          <a-input v-model="formData.publisher" placeholder="请输入出版社" :disabled="!isEditMode" />
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
                :disabled="!isEditMode"
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
                :disabled="!isEditMode"
              />
            </a-form-item>
          </a-col>
        </a-row>
      </template>

      <!-- 文献特有字段 -->
      <template v-if="formData.type === 2">
        <a-form-item label="DOI">
          <a-input v-model="formData.doi" placeholder="请输入DOI" :disabled="!isEditMode" />
        </a-form-item>

        <a-form-item label="期刊名称">
          <a-input v-model="formData.journal" placeholder="请输入期刊名称" :disabled="!isEditMode" />
        </a-form-item>
      </template>

      <!-- 原站链接 -->
      <a-form-item label="原站链接">
        <a-input v-model="formData.sourceUrl" placeholder="请输入原站链接" :disabled="!isEditMode" />
      </a-form-item>

      <!-- AI 生成操作（仅查看已有资源时显示） -->
      <template v-if="props.resourceId">
        <a-divider />
        
        <!-- 左右并排布局 -->
        <a-row :gutter="16">
          <!-- 左侧：人物关系图谱 -->
          <a-col :span="12">
            <a-form-item label="人物关系图谱">
              <a-space direction="vertical" fill>
                <a-space>
                  <span v-if="graphStatus === 2" class="text-sm text-green-600">
                    ✓ 已生成
                  </span>
                  <span v-else-if="graphStatus === -1" class="text-sm text-orange-600">
                    ○ 空数据（AI 认为不符合）
                  </span>
                  <span v-else class="text-sm text-ink-light">
                    ✗ 未生成
                  </span>
                  
                  <a-button
                    type="outline"
                    size="small"
                    @click="handleGenerateGraph"
                    :loading="graphLoading"
                  >
                    {{ graphStatus === 0 ? '生成' : '更新' }}
                  </a-button>
                </a-space>
                
                <!-- JSON 数据预览 -->
                <div v-if="graphData && graphStatus !== 0" class="mt-2">
                  <a-textarea
                    :model-value="JSON.stringify(graphData, null, 2)"
                    :auto-size="{ minRows: 10, maxRows: 20 }"
                    readonly
                    class="font-mono text-xs"
                    placeholder="暂无数据"
                  />
                </div>
              </a-space>
            </a-form-item>
          </a-col>
          
          <!-- 右侧：情感走向 -->
          <a-col :span="12">
            <a-form-item label="情感走向">
              <a-space direction="vertical" fill>
                <a-space>
                  <span v-if="emotionStatus === 2" class="text-sm text-green-600">
                    ✓ 已生成
                  </span>
                  <span v-else-if="emotionStatus === -1" class="text-sm text-orange-600">
                    ○ 空数据（AI 认为不符合）
                  </span>
                  <span v-else class="text-sm text-ink-light">
                    ✗ 未生成
                  </span>
                  
                  <a-button
                    type="outline"
                    size="small"
                    @click="handleGenerateEmotionArc"
                    :loading="emotionLoading"
                  >
                    {{ emotionStatus === 0 ? '生成' : '更新' }}
                  </a-button>
                </a-space>
                
                <!-- JSON 数据预览 -->
                <div v-if="emotionData && emotionStatus !== 0" class="mt-2">
                  <a-textarea
                    :model-value="JSON.stringify(emotionData, null, 2)"
                    :auto-size="{ minRows: 10, maxRows: 20 }"
                    readonly
                    class="font-mono text-xs"
                    placeholder="暂无数据"
                  />
                </div>
              </a-space>
            </a-form-item>
          </a-col>
        </a-row>
      </template>
    </a-form>

    <!-- 自定义底部按钮 -->
    <template #footer>
      <a-space>
        <a-button @click="handleCancel">取消</a-button>
        <a-button v-if="!isEditMode && props.resourceId" type="primary" @click="enableEditMode">
          编辑
        </a-button>
        <a-button v-if="isEditMode" type="primary" @click="handleSubmit" :loading="loading">
          {{ props.resourceId ? '保存' : '添加' }}
        </a-button>
      </a-space>
    </template>
  </a-modal>
</template>

<script setup>
import { ref, reactive, watch, computed } from 'vue'
import { Message } from '@arco-design/web-vue'
import { createResource, updateResource, getResourceDetail, triggerGraphGeneration, triggerEmotionArcGeneration } from '@/api/admin'
import { getGraph } from '@/api/graph'
import { getEmotionArc } from '@/api/emotionArc'

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
const isEditMode = ref(false) // 是否处于编辑模式
const loading = ref(false)
const graphLoading = ref(false)
const emotionLoading = ref(false)
const graphStatus = ref(0) // 0=未生成, -1=空数据(AI认为不符合), 2=有数据
const emotionStatus = ref(0) // 0=未生成, -1=空数据(AI认为不符合), 2=有数据
const graphData = ref(null) // 人物关系图谱数据
const emotionData = ref(null) // 情感走向数据

// 计算模态框标题
const modalTitle = computed(() => {
  if (!props.resourceId) return '添加资源'
  return isEditMode.value ? '编辑资源' : '查看资源'
})

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
      isEditMode.value = false // 默认查看模式
      loadResourceDetail()
      loadAIStatus() // 加载 AI 生成状态
    } else {
      isEditMode.value = true // 添加模式直接可编辑
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
    console.error(error)
    Message.error('加载资源详情失败')
  } finally {
    loading.value = false
  }
}

// 加载 AI 生成状态
const loadAIStatus = async () => {
  try {
    // 加载人物关系图谱状态（管理员后台不自动生成）
    const graphRes = await getGraph(props.resourceId, false)
    if (graphRes.code === 0 && graphRes.data) {
      // 判断是否为空数据（AI 认为不符合）
      const hasNodes = graphRes.data.nodes && graphRes.data.nodes.length > 0
      if (hasNodes) {
        graphStatus.value = 2 // 有数据
        graphData.value = graphRes.data // 保存图谱数据用于预览
      } else {
        graphStatus.value = -1 // 空数据（AI 认为不符合）
        graphData.value = null
      }
    } else {
      graphStatus.value = 0 // 未生成
      graphData.value = null
    }
  } catch (error) {
    // 未生成时会返回 null
    graphStatus.value = 0
    graphData.value = null
  }

  try {
    // 加载情感走向状态（管理员后台不自动生成）
    const emotionRes = await getEmotionArc(props.resourceId, false)
    if (emotionRes.code === 0 && emotionRes.data) {
      const { chapterCount, data } = emotionRes.data
      // 判断是否为空数据（AI 认为不符合）
      if (chapterCount > 0) {
        emotionStatus.value = 2 // 有数据
        emotionData.value = data // 保存情感走向数据用于预览
      } else {
        emotionStatus.value = -1 // 空数据（AI 认为不符合）
        emotionData.value = null
      }
    } else {
      emotionStatus.value = 0 // 未生成
      emotionData.value = null
    }
  } catch (error) {
    // 未生成时会返回 null
    emotionStatus.value = 0
    emotionData.value = null
  }
}

// 启用编辑模式
const enableEditMode = () => {
  isEditMode.value = true
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
  isEditMode.value = false
}

// 提交
const handleSubmit = async () => {
  // 验证必填字段
  if (!formData.title) {
    Message.error('请输入标题')
    return
  }

  loading.value = true
  try {
    const data = { ...formData }
    
    // 转换日期格式
    if (data.pubDate) {
      data.pubDate = data.pubDate.split('T')[0]
    }

    let res
    if (props.resourceId) {
      res = await updateResource(props.resourceId, data)
    } else {
      res = await createResource(data)
    }

    if (res.code === 0) {
      Message.success(props.resourceId ? '更新成功' : '添加成功')
      visible.value = false
      isEditMode.value = false
      emit('success')
    } else {
      Message.error(res.message || '操作失败')
    }
  } catch (error) {
    console.error(error)
    Message.error(props.resourceId ? '修改失败' : '添加失败')
  } finally {
    loading.value = false
  }
}

// 生成人物关系图谱
const handleGenerateGraph = async () => {
  graphLoading.value = true
  try {
    // 使用强制生成模式（forceMode=true），跳过 AI 判断，直接生成图谱
    const res = await triggerGraphGeneration(props.resourceId, true)
    if (res.code === 0) {
      Message.success(graphStatus.value === 0 ? '已触发生成人物关系图谱' : '已触发更新人物关系图谱')
      // 3 秒后重新加载状态
      setTimeout(() => {
        loadAIStatus()
      }, 3000)
    } else {
      Message.error(res.message || '触发失败')
    }
  } catch (error) {
    console.error(error)
    Message.error('触发失败')
  } finally {
    graphLoading.value = false
  }
}

// 生成情感走向
const handleGenerateEmotionArc = async () => {
  emotionLoading.value = true
  try {
    // 使用强制生成模式（forceMode=true），跳过 AI 判断，直接生成情感走向
    const res = await triggerEmotionArcGeneration(props.resourceId, true)
    if (res.code === 0) {
      Message.success(emotionStatus.value === 0 ? '已触发生成情感走向' : '已触发更新情感走向')
      // 3 秒后重新加载状态
      setTimeout(() => {
        loadAIStatus()
      }, 3000)
    } else {
      Message.error(res.message || '触发失败')
    }
  } catch (error) {
    console.error(error)
    Message.error('触发失败')
  } finally {
    emotionLoading.value = false
  }
}
</script>
