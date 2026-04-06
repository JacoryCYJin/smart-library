<template>
  <section ref="storyRoot" class="relative">
    <!-- 章节 1: 泥板时代 - 左右分栏，视觉在左 -->
    <div class="story-chapter sticky top-0 h-[calc(100vh-4rem)] flex items-center justify-center px-6 py-20 bg-canvas">
      <div class="max-w-7xl mx-auto w-full">
        <div class="grid md:grid-cols-2 gap-12 items-center">
          <div class="chapter-visual opacity-0 order-2 md:order-1">
            <div class="w-full aspect-square rounded-3xl bg-gradient-to-br from-[#627D98] to-[#334E68] flex items-center justify-center shadow-2xl relative overflow-hidden">
              <!-- 泥板纹理背景 -->
              <div class="absolute inset-0 opacity-20">
                <div class="grid grid-cols-12 grid-rows-12 h-full w-full gap-1 p-4">
                  <div v-for="i in 144" :key="i" class="bg-white/10 rounded-sm"></div>
                </div>
              </div>
              <!-- 楔形文字组合 -->
              <div class="relative z-10 flex flex-col items-center gap-8">
                <div class="flex gap-6">
                  <div class="text-white/80 text-6xl">𒀭</div>
                  <div class="text-white/60 text-6xl">𒁹</div>
                  <div class="text-white/80 text-6xl">𒌋</div>
                </div>
                <div class="flex gap-6">
                  <div class="text-white/60 text-6xl">𒐕</div>
                  <div class="text-white/90 text-7xl">𒀸</div>
                  <div class="text-white/60 text-6xl">𒈗</div>
                </div>
                <div class="flex gap-6">
                  <div class="text-white/80 text-6xl">𒁀</div>
                  <div class="text-white/60 text-6xl">𒋗</div>
                  <div class="text-white/80 text-6xl">𒆳</div>
                </div>
              </div>
            </div>
          </div>
          <div class="chapter-content opacity-0 order-1 md:order-2">
            <div class="text-sm font-medium tracking-widest text-ink/60 mb-4">公元前 3200 年</div>
            <h2 class="font-serif text-5xl md:text-6xl text-ink mb-6 leading-tight">
              当文明<br/>第一次被镌刻
            </h2>
            <p class="text-lg md:text-xl text-ink/70 leading-relaxed">
              两河流域的苏美尔人用芦苇杆在湿润的泥板上刻下楔形文字。那些关于众神、法典与商贸的记录，在烈日下凝固成永恒。人类终于找到了对抗遗忘的武器——这是知识传承的黎明。
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
            知识开始<br/>在风中流转
          </h2>
          <p class="text-lg md:text-xl text-ink/70 leading-relaxed max-w-3xl mx-auto">
            尼罗河畔的纸莎草被编织成卷轴，亚历山大图书馆的藏书第一次突破了重量的束缚。羊皮纸在欧洲诞生，修道院的烛光下，抄写员用一生誊写经文。知识不再是石头的囚徒，它开始在大陆间迁徙，在时光中穿行。
          </p>
        </div>
        <div class="chapter-visual opacity-0">
          <div class="w-full h-80 rounded-3xl bg-gradient-to-br from-[#D9E2EC] to-[#627D98] flex items-center justify-center overflow-hidden shadow-2xl relative">
            <!-- 纸莎草纹理 -->
            <div class="absolute inset-0 opacity-10">
              <div class="h-full w-full flex">
                <div v-for="i in 20" :key="i" class="flex-1 border-r border-[#102A43]/20"></div>
              </div>
            </div>
            <!-- 卷轴展开效果 -->
            <div class="relative flex gap-4 items-center">
              <!-- 左侧卷轴 -->
              <div class="flex flex-col gap-2">
                <div class="w-16 h-48 bg-gradient-to-r from-[#102A43]/30 to-[#102A43]/10 rounded-full shadow-lg"></div>
              </div>
              <!-- 展开的纸莎草 -->
              <div class="w-64 h-56 bg-gradient-to-br from-[#F0F4F8]/90 to-[#D9E2EC]/80 rounded-lg shadow-xl p-4 relative">
                <!-- 古埃及象形文字 -->
                <div class="space-y-3">
                  <div class="flex gap-2">
                    <div class="w-6 h-6 border-2 border-[#102A43]/40 rounded"></div>
                    <div class="w-6 h-6 border-2 border-[#102A43]/40 rounded-full"></div>
                    <div class="w-6 h-6 border-2 border-[#102A43]/40"></div>
                  </div>
                  <div class="h-1 bg-[#102A43]/20 rounded"></div>
                  <div class="h-1 bg-[#102A43]/20 rounded w-3/4"></div>
                  <div class="h-1 bg-[#102A43]/20 rounded"></div>
                  <div class="flex gap-2 mt-4">
                    <div class="w-8 h-8 border-2 border-[#102A43]/40 rounded-full"></div>
                    <div class="w-8 h-8 border-2 border-[#102A43]/40"></div>
                  </div>
                  <div class="h-1 bg-[#102A43]/20 rounded w-2/3"></div>
                  <div class="h-1 bg-[#102A43]/20 rounded"></div>
                </div>
              </div>
              <!-- 右侧卷轴 -->
              <div class="flex flex-col gap-2">
                <div class="w-16 h-48 bg-gradient-to-l from-[#102A43]/30 to-[#102A43]/10 rounded-full shadow-lg"></div>
              </div>
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
              东方的<br/>造纸术密码
            </h2>
            <p class="text-lg md:text-xl text-ink/70 leading-relaxed">
              蔡伦在洛阳的作坊里，用树皮、麻头、破布和渔网调制出轻薄如蝉翼的纸张。这个秘密沿着丝绸之路向西传播了千年，最终在撒马尔罕被阿拉伯人破解。纸张让书籍的成本骤降百倍，知识的民主化从此拉开序幕。
            </p>
          </div>
          <div class="chapter-visual opacity-0">
            <div class="w-full aspect-square rounded-3xl bg-gradient-to-br from-white to-[#D9E2EC] flex items-center justify-center relative overflow-hidden shadow-2xl">
              <!-- 纸张纤维纹理 -->
              <div class="absolute inset-0 opacity-5">
                <div class="h-full w-full">
                  <div v-for="i in 50" :key="i" 
                       :style="{
                         position: 'absolute',
                         left: Math.random() * 100 + '%',
                         top: Math.random() * 100 + '%',
                         width: Math.random() * 60 + 20 + 'px',
                         height: '2px',
                         transform: `rotate(${Math.random() * 360}deg)`,
                         background: '#102A43'
                       }"></div>
                </div>
              </div>
              <!-- 造纸过程示意 -->
              <div class="relative z-10 flex flex-col items-center gap-8">
                <!-- 原材料 -->
                <div class="flex gap-4">
                  <div class="w-16 h-16 rounded-lg bg-[#627D98]/20 flex items-center justify-center">
                    <div class="text-2xl">🌾</div>
                  </div>
                  <div class="w-16 h-16 rounded-lg bg-[#627D98]/20 flex items-center justify-center">
                    <div class="text-2xl">🪵</div>
                  </div>
                  <div class="w-16 h-16 rounded-lg bg-[#627D98]/20 flex items-center justify-center">
                    <div class="text-2xl">🧵</div>
                  </div>
                </div>
                <!-- 箭头 -->
                <div class="text-[#102A43]/40 text-4xl">↓</div>
                <!-- 纸张成品 -->
                <div class="relative">
                  <div class="w-48 h-56 bg-white rounded-lg shadow-2xl border-2 border-[#D9E2EC] relative overflow-hidden">
                    <!-- 纸张上的文字 -->
                    <div class="absolute inset-0 p-6 flex flex-col justify-center items-center">
                      <div class="text-[#102A43]/60 text-6xl font-serif mb-4">紙</div>
                      <div class="space-y-2 w-full">
                        <div class="h-1 bg-[#102A43]/10 rounded"></div>
                        <div class="h-1 bg-[#102A43]/10 rounded w-3/4"></div>
                        <div class="h-1 bg-[#102A43]/10 rounded w-5/6"></div>
                      </div>
                    </div>
                  </div>
                  <!-- 纸张阴影效果 -->
                  <div class="absolute -bottom-2 -right-2 w-48 h-56 bg-[#627D98]/10 rounded-lg -z-10"></div>
                </div>
              </div>
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
            当金属活字<br/>引爆文艺复兴
          </h2>
          <p class="text-base md:text-lg text-white/80 leading-relaxed max-w-2xl mx-auto">
            美因茨的金匠古腾堡在地下室里铸造出可重复使用的金属活字。1455年，第一本印刷版《圣经》诞生，印刷时间从三年骤降至三个月。知识的洪流冲破教会的垄断，马丁·路德的《九十五条论纲》在两周内传遍欧洲。这是人类历史的分水岭——印刷术点燃了文艺复兴，催生了宗教改革，启蒙了整个时代。
          </p>
        </div>
        
        <!-- 活字网格 - 直接溢出视口 -->
        <div class="chapter-visual opacity-0">
          <div class="w-full rounded-3xl bg-gradient-to-br from-[#334E68] to-[#627D98] p-8 md:p-12 shadow-2xl relative overflow-hidden">
            <!-- 印刷机齿轮背景 -->
            <div class="absolute top-4 right-4 w-24 h-24 border-8 border-white/10 rounded-full"></div>
            <div class="absolute bottom-4 left-4 w-32 h-32 border-8 border-white/10 rounded-full"></div>
            
            <!-- 活字排版盘 -->
            <div class="relative">
              <!-- 标题：印刷机 -->
              <div class="text-center mb-6">
                <div class="text-white/60 text-sm tracking-widest mb-2">GUTENBERG PRESS</div>
                <div class="h-0.5 bg-white/20 w-32 mx-auto"></div>
              </div>
              
              <!-- 活字网格 -->
              <div class="grid grid-cols-6 md:grid-cols-8 gap-2 md:gap-3">
                <div v-for="i in 48" :key="i" 
                     class="aspect-square bg-white/95 rounded shadow-lg flex items-center justify-center text-[#102A43] font-bold text-base md:text-xl relative group hover:scale-110 transition-transform cursor-pointer">
                  {{ String.fromCharCode(64 + (i % 26) + 1) }}
                  <!-- 活字的金属质感 -->
                  <div class="absolute inset-0 bg-gradient-to-br from-white/40 to-transparent rounded pointer-events-none"></div>
                </div>
              </div>
              
              <!-- 底部装饰 -->
              <div class="mt-6 flex justify-center gap-2">
                <div class="w-2 h-2 bg-white/40 rounded-full"></div>
                <div class="w-2 h-2 bg-white/40 rounded-full"></div>
                <div class="w-2 h-2 bg-white/40 rounded-full"></div>
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
              工业革命<br/>掀起知识海啸
            </h2>
            <p class="text-lg md:text-xl text-ink/70 leading-relaxed">
              蒸汽印刷机的轰鸣声响彻伦敦、巴黎和纽约。一台机器每小时可印刷一万页，书籍的价格暴跌至工人一天的工资。《泰晤士报》、《雾都孤儿》、《共产党宣言》涌入街头巷尾。公共图书馆如雨后春笋般涌现，识字率从5%飙升至80%。知识不再是贵族的专属，它属于每一个渴望改变命运的人。
            </p>
          </div>
          <div class="md:col-span-2 chapter-visual opacity-0">
            <div class="w-full max-w-sm mx-auto aspect-[3/4] rounded-3xl bg-gradient-to-br from-[#D64545] to-[#102A43] p-6 flex flex-col items-center justify-center shadow-2xl relative overflow-hidden">
              <!-- 蒸汽齿轮装饰 -->
              <div class="absolute top-0 right-0 w-20 h-20 opacity-10">
                <div class="w-full h-full border-4 border-white rounded-full relative">
                  <div class="absolute inset-2 border-4 border-white rounded-full"></div>
                </div>
              </div>
              
              <!-- 印刷书籍堆叠 -->
              <div class="relative w-full">
                <!-- 书籍堆叠效果 -->
                <div class="grid grid-cols-3 gap-3 w-full mb-6">
                  <div v-for="i in 9" :key="i" 
                       class="aspect-[3/4] bg-white/90 rounded-lg shadow-xl relative overflow-hidden group hover:scale-105 transition-transform">
                    <!-- 书脊线条 -->
                    <div class="absolute left-2 top-0 bottom-0 w-0.5 bg-[#102A43]/20"></div>
                    <!-- 书页纹理 -->
                    <div class="absolute inset-0 flex flex-col justify-center items-center p-2">
                      <div class="w-full space-y-1">
                        <div class="h-0.5 bg-[#102A43]/30 rounded"></div>
                        <div class="h-0.5 bg-[#102A43]/20 rounded w-3/4"></div>
                        <div class="h-0.5 bg-[#102A43]/30 rounded"></div>
                      </div>
                    </div>
                  </div>
                </div>
                
                <!-- 蒸汽效果 -->
                <div class="flex justify-center gap-2">
                  <div class="w-1 h-1 bg-white/40 rounded-full animate-pulse"></div>
                  <div class="w-1 h-1 bg-white/40 rounded-full animate-pulse" style="animation-delay: 0.2s"></div>
                  <div class="w-1 h-1 bg-white/40 rounded-full animate-pulse" style="animation-delay: 0.4s"></div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 章节 6: 数字时代 - 全屏沉浸式 -->
    <div class="story-chapter sticky top-0 h-[calc(100vh-4rem)] flex items-center justify-center px-6 py-20 bg-gradient-to-b from-[#102A43] to-[#334E68] relative overflow-hidden">
      <div class="chapter-visual opacity-0 absolute inset-0">
        <div class="w-full h-full relative overflow-hidden">
          <!-- 数字雨效果 -->
          <div class="absolute inset-0">
            <div v-for="i in 20" :key="i" 
                 class="absolute top-0 animate-fall"
                 :style="{
                   left: (i * 5) + '%',
                   animationDelay: (Math.random() * 2) + 's',
                   animationDuration: (3 + Math.random() * 2) + 's'
                 }">
              <div class="flex flex-col gap-4 text-white/20 text-xs font-mono">
                <div>01001000</div>
                <div>01100101</div>
                <div>01101100</div>
                <div>01101100</div>
                <div>01101111</div>
              </div>
            </div>
          </div>
          
          <!-- 电路板纹理 -->
          <div class="absolute inset-0 opacity-10">
            <svg class="w-full h-full" xmlns="http://www.w3.org/2000/svg">
              <defs>
                <pattern id="circuit" x="0" y="0" width="100" height="100" patternUnits="userSpaceOnUse">
                  <circle cx="50" cy="50" r="2" fill="white"/>
                  <line x1="50" y1="50" x2="100" y2="50" stroke="white" stroke-width="1"/>
                  <line x1="50" y1="50" x2="50" y2="100" stroke="white" stroke-width="1"/>
                  <circle cx="100" cy="50" r="2" fill="white"/>
                  <circle cx="50" cy="100" r="2" fill="white"/>
                </pattern>
              </defs>
              <rect width="100%" height="100%" fill="url(#circuit)"/>
            </svg>
          </div>
        </div>
      </div>
      <div class="max-w-4xl mx-auto text-center relative z-10">
        <div class="chapter-content opacity-0">
          <div class="text-sm font-medium tracking-widest text-white/60 mb-6">1971 年 - 至今</div>
          <h2 class="font-serif text-6xl md:text-8xl text-white mb-8 leading-tight">
            当书籍化为<br/>光与数据
          </h2>
          <p class="text-lg md:text-xl text-white/80 leading-relaxed max-w-2xl mx-auto mb-12">
            1971年，迈克尔·哈特在伊利诺伊大学的主机上敲下《独立宣言》，古腾堡计划诞生。互联网让全球图书馆的藏书瞬间互联，Google Books 扫描了四千万本书籍，Kindle 让一台设备装下整座图书馆。今天，知识以光速在全球流转，AI 开始阅读、理解并创造新的文本。从泥板到云端，五千年的演化在此刻汇聚——而你，正站在这场永恒革命的最前沿。
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
            而这场革命<br/>永不落幕
          </h2>
          <p class="text-xl md:text-2xl text-ink/70 leading-relaxed max-w-2xl mx-auto mb-12">
            五千年来，载体在变，技术在变，但人类对知识的渴望从未改变。每一次革命都让知识更自由，让智慧更普及。下一个篇章，由你书写。
          </p>
          <button
            type="button"
            @click="goToLibrary"
            class="cursor-pointer font-serif inline-flex items-center justify-center rounded-full bg-[#D64545] px-9 py-4 text-lg font-medium text-white transition-all duration-300 hover:bg-[#D64545]/90 hover:shadow-xl hover:-translate-y-1 focus:outline-none focus:ring-2 focus:ring-[#D64545]/20"
          >
            开启你的阅读之旅
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

@keyframes fall {
  0% {
    transform: translateY(-100%);
    opacity: 0;
  }
  10% {
    opacity: 1;
  }
  90% {
    opacity: 1;
  }
  100% {
    transform: translateY(100vh);
    opacity: 0;
  }
}

.animate-fall {
  animation: fall linear infinite;
}
</style>
