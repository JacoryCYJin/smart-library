import { computed } from 'vue'
import { defineStore } from 'pinia'
import { usePreferredLanguages, useStorage } from '@vueuse/core'

export const useLocaleStore = defineStore('locale', () => {
  const preferredLanguages = usePreferredLanguages()

  // null means "auto" (follow browser). When user toggles/sets language, it becomes manual.
  const manualLang = useStorage('app-locale-manual', null)

  const autoLang = computed(() => {
    const first = preferredLanguages.value?.[0] || ''
    return first.toLowerCase().startsWith('zh') ? 'zh' : 'en'
  })

  const currentLang = computed(() => manualLang.value || autoLang.value)

  function setLang(lang) {
    manualLang.value = lang
  }

  function toggleLang() {
    setLang(currentLang.value === 'zh' ? 'en' : 'zh')
  }

  function resetToAuto() {
    manualLang.value = null
  }

  return {
    currentLang,
    autoLang,
    manualLang,
    setLang,
    toggleLang,
    resetToAuto
  }
})
