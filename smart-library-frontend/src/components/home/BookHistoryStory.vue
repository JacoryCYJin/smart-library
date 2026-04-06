<template>
  <section ref="storyRoot" class="relative">
    <!-- 章节 1: 泥板时代 - 左右分栏，视觉在左 -->
    <div class="story-chapter sticky top-0 h-[calc(100vh-4rem)] flex items-center justify-center px-6 py-20 bg-canvas">
      <div class="max-w-7xl mx-auto w-full">
        <div class="grid md:grid-cols-2 gap-12 items-center">
          <div class="chapter-visual opacity-0 order-2 md:order-1">
            <div class="w-full aspect-square rounded-3xl bg-gradient-to-br from-[#627D98] to-[#334E68] flex items-center justify-center shadow-2xl">
              <div class="text-white/20 text-9xl">𒀭</div>
            </div>
          </div>
          <div class="chapter-content opacity-0 order-1 md:order-2">
            <div class="text-sm font-medium tracking-widest text-ink/60 mb-4">公元前 3200 年</div>
            <h2 class="font-serif text-5xl md:text-6xl text-ink mb-6 leading-tight">
              刻在泥板上的文明
            </h2>
            <p class="text-lg md:text-xl text-ink/70 leading-relaxed">
              美索不达米亚的苏美尔人在湿润的泥板上刻下楔形文字，记录着神话、法律与商业往来。这些泥板在阳光下晒干，成为人类最早的"书籍"。
            </p>
          </div>
        </div>
      </div>
    </div>

    <!-- 章节 2: 纸莎草 - 全宽居中，视觉在上 -->
    <div class="story-chapter sticky top-0 h-[calc(100vh-4rem)] flex items-center justify-center px-6 py-20 bg-white">
      <div class="max-w-5xl mx-auto text-center">
        <div class="chapter-content opacity-0 mb-16">
          <div class="text-sm font-medium tracking-widest text-ink/60 mb-4">公元前 3000 - 公元 100 年</div>
          <h2 class="font-serif text-5xl md:text-7xl text-ink mb-8 leading-tight">
            纸莎草的轻盈革命
          </h2>
          <p class="text-lg md:text-xl text-ink/70 leading-relaxed max-w-3xl mx-auto">
            古埃及人从尼罗河畔的纸莎草中提取纤维，制成轻便的书写材料。卷轴取代了沉重的泥板，知识开始流动。羊皮纸的出现让书籍更加耐久，抄写员们在修道院中日夜誊抄经文。
          </p>
        </div>
        <div class="chapter-visual opacity-0">
          <div class="w-full h-80 rounded-3xl bg-gradient-to-br from-[#D9E2EC] to-[#627D98] flex items-center justify-center overflow-hidden shadow-2xl">
            <div class="flex gap-6">
              <div class="w-20 h-56 bg-[#102A43]/10 rounded-lg transform -rotate-3"></div>
              <div class="w-20 h-56 bg-[#102A43]/20 rounded-lg"></div>
              <div class="w-20 h-56 bg-[#102A43]/10 rounded-lg transform rotate-3"></div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 章节 3: 造纸术 - 左右分栏，视觉在右 -->
    <div class="story-chapter sticky top-0 h-[calc(100vh-4rem)] flex items-center justify-center px-6 py-20 bg-canvas">
      <div class="max-w-7xl mx-auto w-full">
        <div class="grid md:grid-cols-2 gap-12 items-center">
          <div class="chapter-content opacity-0">
            <div class="text-sm font-medium tracking-widest text-ink/60 mb-4">公元 105 年</div>
            <h2 class="font-serif text-5xl md:text-6xl text-ink mb-6 leading-tight">
              蔡伦的纸张奇迹
            </h2>
            <p class="text-lg md:text-xl text-ink/70 leading-relaxed">
              东汉宦官蔡伦改进造纸术，用树皮、麻头、破布和渔网制成轻薄而坚韧的纸张。这项发明沿着丝绸之路传向西方，彻底改变了人类记录信息的方式。
            </p>
          </div>
          <div class="chapter-visual opacity-0">
            <div class="w-full aspect-square rounded-3xl bg-gradient-to-br from-white to-[#D9E2EC] flex items-center justify-center relative overflow-hidden shadow-2xl">
              <div class="absolute inset-0 opacity-10">
                <div class="grid grid-cols-8 gap-2 h-full p-4">
                  <div v-for="i in 32" :key="i" class="bg-[#102A43] rounded"></div>
                </div>
              </div>
              <div class="text-[#102A43]/30 text-9xl font-serif">紙</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 章节 4: 古腾堡 - 大标题 + 底部视觉 -->
    <div class="story-chapter sticky top-0 h-[calc(100vh-4rem)] px-6 pt-20 bg-[#102A43] overflow-hidden">
      <div class="max-w-6xl mx-auto w-full">
        <!-- 标题区域 -->
        <div class="chapter-content opacity-0 text-center mb-12">
          <div class="text-sm font-medium tracking-widest text-white/60 mb-4">1440 年</div>
          <h2 class="font-serif text-5xl md:text-7xl text-white mb-6 leading-tight">
            古腾堡的<br/>金属活字
          </h2>
          <p class="text-base md:text-lg text-white/80 leading-relaxed max-w-2xl mx-auto">
            德国金匠约翰内斯·古腾堡发明了活字印刷机，用金属铸造可重复使用的字模。一本《圣经》的印刷时间从数年缩短至数月，知识的复制成本骤降，文艺复兴与宗教改革由此点燃。
          </p>
        </div>
        
        <!-- 活字网格 - 直接溢出视口 -->
        <div class="chapter-visual opacity-0">
          <div class="w-full rounded-3xl bg-gradient-to-br from-[#334E68] to-[#627D98] p-8 md:p-12 shadow-2xl">
            <div class="grid grid-cols-6 md:grid-cols-8 gap-3">
              <div v-for="i in 48" :key="i" 
                   class="aspect-square bg-white/90 rounded flex items-center justify-center text-[#102A43] font-bold text-lg md:text-xl">
                {{ String.fromCharCode(64 + (i % 26) + 1) }}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 章节 5: 工业革命 - 对角线分割布局 -->
    <div class="story-chapter sticky top-0 h-[calc(100vh-4rem)] flex items-center justify-center px-6 py-20 relative overflow-hidden bg-[#F0F4F8]">
      <div class="absolute inset-0 bg-gradient-to-br from-[#D64545]/10 to-transparent"></div>
      <div class="max-w-7xl mx-auto w-full relative z-10">
        <div class="grid md:grid-cols-5 gap-12 items-center">
          <div class="md:col-span-3 chapter-content opacity-0">
            <div class="text-sm font-medium tracking-widest text-ink/60 mb-4">1800 - 1900 年</div>
            <h2 class="font-serif text-5xl md:text-7xl text-ink mb-6 leading-tight">
              蒸汽机驱动的<br/>知识洪流
            </h2>
            <p class="text-lg md:text-xl text-ink/70 leading-relaxed">
              蒸汽印刷机的出现让书籍产量暴增，报纸、杂志、小说涌入千家万户。公共图书馆兴起，识字率攀升，阅读不再是贵族的特权，而成为每个人触手可及的权利。
            </p>
          </div>
          <div class="md:col-span-2 chapter-visual opacity-0">
            <div class="w-full max-w-sm mx-auto aspect-[3/4] rounded-3xl bg-gradient-to-br from-[#D64545] to-[#102A43] p-6 flex items-center justify-center shadow-2xl">
              <div class="grid grid-cols-3 gap-2 w-full">
                <div v-for="i in 12" :key="i" class="aspect-[3/4] bg-white/20 rounded-lg"></div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 章节 6: 数字时代 - 全屏沉浸式 -->
    <div class="story-chapter sticky top-0 h-[calc(100vh-4rem)] flex items-center justify-center px-6 py-20 bg-gradient-to-b from-[#102A43] to-[#334E68] relative overflow-hidden">
      <div class="chapter-visual opacity-0 absolute inset-0">
        <div class="w-full h-full relative">
          <div class="absolute inset-0 grid grid-cols-16 grid-rows-12 gap-1 p-4 opacity-20">
            <div v-for="i in 192" :key="i" 
                 :class="[
                   'rounded-sm transition-all duration-300',
                   Math.random() > 0.5 ? 'bg-white' : 'bg-transparent'
                 ]"></div>
          </div>
        </div>
      </div>
      <div class="max-w-4xl mx-auto text-center relative z-10">
        <div class="chapter-content opacity-0">
          <div class="text-sm font-medium tracking-widest text-white/60 mb-6">1971 年 - 至今</div>
          <h2 class="font-serif text-6xl md:text-8xl text-white mb-8 leading-tight">
            比特与字节的<br/>新纪元
          </h2>
          <p class="text-lg md:text-xl text-white/80 leading-relaxed max-w-2xl mx-auto mb-12">
            古腾堡计划启动，第一本电子书诞生。互联网让全球图书馆互联互通，Kindle 重新定义阅读体验。今天，数十亿本书籍以光速在云端流转，而你正站在这场五千年演变的最前沿。
          </p>
          <div class="text-white text-8xl">∞</div>
        </div>
      </div>
    </div>

    <!-- 尾声 -->
    <div class="story-chapter sticky top-0 h-[calc(100vh-4rem)] flex items-center justify-center px-6 py-20 bg-canvas">
      <div class="max-w-4xl mx-auto text-center">
        <div class="chapter-content opacity-0">
          <h2 class="font-serif text-5xl md:text-7xl text-ink mb-8 leading-tight">
            而故事，仍在继续
          </h2>
          <p class="text-xl md:text-2xl text-ink/70 leading-relaxed max-w-2xl mx-auto mb-12">
            从泥板到云端，书籍的形态不断演变，但人类对知识的渴望从未改变。
          </p>
          <button
            type="button"
            @click="goToLibrary"
            class="cursor-pointer font-serif inline-flex items-center justify-center rounded-full bg-[#D64545] px-9 py-4 text-lg font-medium text-white transition-all duration-300 hover:bg-[#D64545]/90 hover:shadow-xl hover:-translate-y-1 focus:outline-none focus:ring-2 focus:ring-[#D64545]/20"
          >
            探索图书馆
          </button>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { onMounted, onBeforeUnmount, ref } from 'vue'
import gsap from 'gsap'
import { ScrollTrigger } from 'gsap/ScrollTrigger'
import { useRouter } from 'vue-router'

gsap.registerPlugin(ScrollTrigger)

const router = useRouter()
const storyRoot = ref(null)

const goToLibrary = () => {
  router.push('/book')
}

onMounted(() => {
  if (!storyRoot.value) return

  const chapters = storyRoot.value.querySelectorAll('.story-chapter')

  chapters.forEach((chapter, index) => {
    const content = chapter.querySelector('.chapter-content')
    const visual = chapter.querySelector('.chapter-visual')

    // 内容淡入 + 上移动画
    gsap.fromTo(
      content,
      {
        opacity: 0,
        y: 80,
      },
      {
        opacity: 1,
        y: 0,
        duration: 1.2,
        ease: 'power3.out',
        scrollTrigger: {
          trigger: chapter,
          start: 'top 70%',
          end: 'top 30%',
          toggleActions: 'play none none reverse',
        },
      }
    )

    // 视觉元素延迟淡入 + 缩放动画
    if (visual) {
      gsap.fromTo(
        visual,
        {
          opacity: 0,
          scale: 0.9,
        },
        {
          opacity: 1,
          scale: 1,
          duration: 1,
          ease: 'back.out(1.2)',
          scrollTrigger: {
            trigger: chapter,
            start: 'top 60%',
            end: 'top 20%',
            toggleActions: 'play none none reverse',
          },
        }
      )
    }

    // 为特定章节添加特殊动画
    if (index === 3) {
      // 古腾堡章节：活字飞入动画
      const letters = visual.querySelectorAll('.grid > div')
      gsap.fromTo(
        letters,
        {
          opacity: 0,
          y: -50,
          rotation: -180,
        },
        {
          opacity: 1,
          y: 0,
          rotation: 0,
          duration: 0.6,
          stagger: 0.03,
          ease: 'back.out(1.5)',
          scrollTrigger: {
            trigger: chapter,
            start: 'top 50%',
            toggleActions: 'play none none reverse',
          },
        }
      )
    }

    if (index === 5) {
      // 数字时代：像素闪烁动画
      const pixels = visual.querySelectorAll('.grid > div')
      gsap.to(pixels, {
        opacity: 0.8,
        duration: 0.3,
        stagger: {
          amount: 2,
          from: 'random',
          repeat: -1,
          yoyo: true,
        },
        scrollTrigger: {
          trigger: chapter,
          start: 'top 50%',
          end: 'bottom top',
        },
      })
    }
  })
})

onBeforeUnmount(() => {
  ScrollTrigger.getAll().forEach(trigger => trigger.kill())
})
</script>

<style scoped>
.story-chapter {
  position: sticky;
  top: 0;
}
</style>
