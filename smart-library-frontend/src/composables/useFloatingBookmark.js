import { ref } from 'vue'

// 全局书签实例引用
const bookmarkInstance = ref(null)

/**
 * 漂流书签 Composable
 * 提供手动触发书签的能力（主要用于测试和调试）
 */
export function useFloatingBookmark() {
  /**
   * 设置书签组件实例
   * @param {Object} instance - FloatingBookmark 组件实例
   */
  function setBookmarkInstance(instance) {
    bookmarkInstance.value = instance
  }

  /**
   * 手动触发书签掉落
   * 主要用于测试和调试
   */
  function triggerBookmark() {
    console.log('📌 triggerBookmark 被调用')
    console.log('📌 bookmarkInstance.value:', bookmarkInstance.value)
    
    if (bookmarkInstance.value && bookmarkInstance.value.triggerBookmark) {
      console.log('✅ 调用书签组件的 triggerBookmark 方法')
      bookmarkInstance.value.triggerBookmark()
    } else {
      console.warn('❌ 书签组件实例未初始化或方法不存在')
      console.warn('bookmarkInstance.value:', bookmarkInstance.value)
    }
  }

  return {
    setBookmarkInstance,
    triggerBookmark
  }
}
