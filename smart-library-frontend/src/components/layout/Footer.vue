<template>
  <footer class="bg-ink">
    <div class="max-w-6xl mx-auto px-6 py-12">
      <div class="grid grid-cols-1 gap-10 md:grid-cols-3">
        <div>
          <div class="inline-flex items-center gap-3 font-serif text-3xl font-bold text-white">
            <img :src="logoUrl" alt="Logo" class="h-10 w-10 flex-shrink-0" />
            <span>{{ labels.brand }}</span>
          </div>
          <p class="mt-3 font-serif text-base font-semibold leading-relaxed text-white/70">
            {{ labels.slogan }}
          </p>
        </div>

        <div>
          <h3 class="font-serif text-lg text-white font-bold">{{ labels.exploreTitle }}</h3>
          <ul class="mt-4 space-y-3 font-sans">
            <li>
              <a href="/" class="text-white/70 hover:text-white transition-colors">{{
                labels.home
              }}</a>
            </li>
            <li>
              <a href="#" class="text-white/70 hover:text-white transition-colors">{{
                labels.books
              }}</a>
            </li>
            <li>
              <a href="#" class="text-white/70 hover:text-white transition-colors">{{
                labels.serendipity
              }}</a>
            </li>
            <li>
              <a href="#" class="text-white/70 hover:text-white transition-colors">{{
                labels.research
              }}</a>
            </li>
          </ul>
        </div>

        <div>
          <h3 class="font-serif text-lg text-white font-bold">{{ labels.aboutTitle }}</h3>
          <ul class="mt-4 space-y-3 font-sans">
            <li>
              <a
                href="#"
                class="text-white/70 hover:text-white transition-colors"
                rel="noopener noreferrer"
              >
                {{ labels.github }}
              </a>
            </li>
            <li>
              <a href="#" class="text-white/70 hover:text-white transition-colors">{{
                labels.designSystem
              }}</a>
            </li>
            <li>
              <a href="#" class="text-white/70 hover:text-white transition-colors">{{
                labels.team
              }}</a>
            </li>
          </ul>
        </div>
      </div>

      <div class="mt-10 border-t border-white/10 pt-6">
        <p class="font-sans text-sm text-white/70">{{ labels.copyright }}</p>
      </div>
    </div>
  </footer>
</template>

<script setup>
import { computed } from 'vue'
import { useLocaleStore } from '@/stores/locale'
import logoUrl from '@/assets/images/logo-dark.png'

defineOptions({ name: 'AppFooter' })

const localeStore = useLocaleStore()
const currentLang = computed(() => localeStore.currentLang)

const labels = computed(() => {
  if (currentLang.value === 'en') {
    return {
      brand: 'InkSight',
      slogan: 'Redefining the gravity of reading.',
      exploreTitle: 'Explore',
      aboutTitle: 'Project',
      home: 'Home',
      books: 'Collections',
      serendipity: 'Serendipity',
      research: 'Papers',
      github: 'GitHub',
      designSystem: 'Design System',
      team: 'Team',
      copyright: '© 2025 InkSight Team. All rights reserved.',
    }
  }

  return {
    brand: '阅 墨',
    slogan: '重塑阅读的引力。',
    exploreTitle: '探索',
    aboutTitle: '关于',
    home: '首页',
    books: '藏书',
    serendipity: '偶遇',
    research: '文献',
    github: 'GitHub',
    designSystem: '设计系统',
    team: '团队',
    copyright: '© 2025 InkSight Team. All rights reserved.',
  }
})
</script>
