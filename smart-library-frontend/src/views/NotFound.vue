<template>
  <div ref="root" class="relative w-screen h-screen overflow-hidden bg-canvas text-ink font-sans selection:bg-pop selection:text-white">
    
    <div ref="waveContainer" class="absolute inset-0 z-0 pointer-events-auto mix-blend-multiply opacity-50"></div>

    <div class="relative z-10 w-full h-full flex flex-col justify-center items-center pointer-events-none">
      
      <div ref="badge" class="absolute top-10 left-10 md:top-18 md:left-20">
        <span class="font-serif italic text-xs tracking-[0.2em] text-ink-light border-b border-structure pb-1">
          InkSight / 404
        </span>
      </div>

      <div class="text-center space-y-9 mix-blend-darken">
        
        <h1 ref="title" class="font-serif italic text-9xl md:text-[9rem] lg:text-[11rem] font-medium text-ink leading-none select-none tracking-widest">
          404
        </h1>
                
        <div ref="copy" class="space-y-3">
          <p class="font-serif text-1xl md:text-3xl text-ink tracking-widest">
            {{ labels.title }}
          </p>
          <p class="font-sans text-xs md:text-xl text-ink-light opacity-60 tracking-wider uppercase">
            {{ labels.subtitle }}
          </p>
        </div>
        
      </div>

      <div ref="backWrap" class="mt-20 pointer-events-auto">
        <router-link 
          to="/" 
          class="group relative inline-block pb-1 cursor-pointer"
        >
          <span class="font-serif italic text-xl tracking-widest text-ink transition-opacity group-hover:opacity-70">
            {{ labels.back }}
          </span>
          <span class="absolute bottom-0 left-0 w-full h-0.25 bg-ink transform origin-left scale-x-100 transition-transform duration-500 ease-out group-hover:scale-x-0"></span>
          <span class="absolute bottom-0 left-0 w-full h-0.25 bg-pop transform origin-right scale-x-0 transition-transform duration-500 ease-out group-hover:scale-x-100"></span>
        </router-link>
      </div>
    </div>

    <div ref="footerText" class="absolute bottom-10 right-10 text-[10px] tracking-[0.3em] text-ink-light opacity-40 font-sans uppercase vertical-rl md:horizontal-tb">
      The Lost Page
    </div>

  </div>
</template>

<script setup>
import { computed, ref, onMounted, onUnmounted } from 'vue'
import gsap from 'gsap'
import { useLocaleStore } from '@/stores/locale'

// --- 状态与配置 ---
const root = ref(null)
const badge = ref(null)
const title = ref(null)
const copy = ref(null)
const backWrap = ref(null)
const footerText = ref(null)

const waveContainer = ref(null)
const localeStore = useLocaleStore()
const currentLang = computed(() => localeStore.currentLang)

// 配色配置 (对应 --color-ink-light #627d98)
const WAVE_COLOR = '#627d98'
const WAVE_OPACITY = 0.3 // 降低透明度，更像水印

// --- 文案逻辑 ---
const labels = computed(() => {
  if (currentLang.value === 'en') {
    return {
      title: 'This page is yet to be written.',
      subtitle: 'Chapter not found',
      back: 'Return Home'
    }
  }
  return {
    title: '此处留白，未有墨迹。',
    subtitle: '您寻找的篇章暂不存在',
    back: '返回首页' // 或者 "回 溯" 更文艺，看你喜好
  }
})

// --- 波浪线动画逻辑 (封装) ---
class WavesLogic {
  constructor(container) {
    this.container = container
    this.svg = document.createElementNS('http://www.w3.org/2000/svg', 'svg')
    this.svg.style.width = '100%'
    this.svg.style.height = '100%'
    this.svg.style.display = 'block'
    this.container.appendChild(this.svg)

    this.mouse = { x: 0, y: 0, lx: 0, ly: 0, sx: 0, sy: 0, v: 0, vs: 0, a: 0 }
    this.lines = []
    this.paths = []
    this.bounding = { width: 0, height: 0, left: 0, top: 0 }
    
    // 绑定上下文
    this._onMouseMove = this.onMouseMove.bind(this)
    this._onTouchMove = this.onTouchMove.bind(this)
    this._tick = this.tick.bind(this)

    this.init()
  }

  init() {
    this.setSize()
    this.setLines()
    this.bindEvents()
    this.start()
  }

  start() {
    gsap.ticker.add(this._tick)
  }

  stop() {
    gsap.ticker.remove(this._tick)
  }

  bindEvents() {
    window.addEventListener('mousemove', this._onMouseMove)
    window.addEventListener('touchmove', this._onTouchMove, { passive: false })
  }

  cleanup() {
    this.stop()
    window.removeEventListener('mousemove', this._onMouseMove)
    window.removeEventListener('touchmove', this._onTouchMove)
    if (this.svg) this.svg.remove()
  }

  onMouseMove(e) {
    this.updateMousePosition(e.pageX, e.pageY)
  }

  onTouchMove(e) {
    if(e.touches.length > 0) {
      this.updateMousePosition(e.touches[0].clientX, e.touches[0].clientY)
    }
  }

  setSize() {
    this.bounding = this.container.getBoundingClientRect()
  }

  setLines() {
    const { width, height } = this.bounding
    this.lines = []
    
    this.paths.forEach(p => p.remove())
    this.paths = []

    // 增加间距，使波浪更稀疏、更优雅
    const xGap = 15 
    const yGap = 32

    const oWidth = width + 200
    const oHeight = height + 30

    const totalLines = Math.ceil(oWidth / xGap)
    const totalPoints = Math.ceil(oHeight / yGap)

    const xStart = (width - xGap * totalLines) / 2
    const yStart = (height - yGap * totalPoints) / 2

    for (let i = 0; i <= totalLines; i++) {
      const points = []
      for (let j = 0; j <= totalPoints; j++) {
        points.push({
          x: xStart + xGap * i,
          y: yStart + yGap * j,
          cursor: { x: 0, y: 0, vx: 0, vy: 0 },
        })
      }
      this.lines.push(points)

      const path = document.createElementNS('http://www.w3.org/2000/svg', 'path')
      path.setAttribute('fill', 'none')
      path.setAttribute('stroke', WAVE_COLOR)
      path.setAttribute('stroke-width', '1')
      path.setAttribute('stroke-opacity', WAVE_OPACITY) 
      this.svg.appendChild(path)
      this.paths.push(path)
    }
  }

  updateMousePosition(x, y) {
    this.mouse.x = x - this.bounding.left
    this.mouse.y = y - this.bounding.top + window.scrollY
  }

  movePoints() {
    const { lines, mouse } = this
    lines.forEach((points) => {
      points.forEach((p) => {
        const dx = p.x - mouse.sx
        const dy = p.y - mouse.sy
        const d = Math.hypot(dx, dy)
        const l = Math.max(175, mouse.vs)

        if (d < l) {
          const f = 1 - d / l
          p.cursor.vx += Math.cos(mouse.a) * f * mouse.vs * 0.08
          p.cursor.vy += Math.sin(mouse.a) * f * mouse.vs * 0.08
        }

        p.cursor.vx += (0 - p.cursor.x) * 0.005
        p.cursor.vy += (0 - p.cursor.y) * 0.005
        p.cursor.vx *= 0.925
        p.cursor.vy *= 0.925
        
        p.cursor.x += p.cursor.vx * 2
        p.cursor.y += p.cursor.vy * 2

        p.cursor.x = Math.min(100, Math.max(-100, p.cursor.x))
        p.cursor.y = Math.min(100, Math.max(-100, p.cursor.y))
      })
    })
  }

  moved(point, withCursorForce = true) {
    const coords = {
      x: point.x + (withCursorForce ? point.cursor.x : 0),
      y: point.y + (withCursorForce ? point.cursor.y : 0),
    }
    coords.x = Math.round(coords.x * 10) / 10
    coords.y = Math.round(coords.y * 10) / 10
    return coords
  }

  drawLines() {
    const { lines, paths } = this
    lines.forEach((points, lIndex) => {
      let p1 = this.moved(points[0], false)
      let d = `M ${p1.x} ${p1.y}`
      points.forEach((p, pIndex) => {
        const isLast = pIndex === points.length - 1
        p = this.moved(p, !isLast)
        d += `L ${p.x} ${p.y}`
      })
      paths[lIndex].setAttribute('d', d)
    })
  }

  tick() {
    const { mouse } = this
    mouse.sx += (mouse.x - mouse.sx) * 0.1
    mouse.sy += (mouse.y - mouse.sy) * 0.1

    const dx = mouse.x - mouse.lx
    const dy = mouse.y - mouse.ly
    const d = Math.hypot(dx, dy)

    mouse.v = d
    mouse.vs += (d - mouse.vs) * 0.1
    mouse.vs = Math.min(100, mouse.vs)

    mouse.lx = mouse.x
    mouse.ly = mouse.y
    mouse.a = Math.atan2(dy, dx)

    this.movePoints()
    this.drawLines()
  }
}

// --- 生命周期管理 ---
let wavesInstance = null
let resizeObserver = null
let gsapCtx = null

onMounted(() => {
  if (waveContainer.value) {
    wavesInstance = new WavesLogic(waveContainer.value)

    resizeObserver = new ResizeObserver(() => {
      wavesInstance.setSize()
      wavesInstance.setLines()
    })
    resizeObserver.observe(waveContainer.value)
  }

  gsapCtx = gsap.context(() => {
    const tl = gsap.timeline({ defaults: { ease: 'power2.out', duration: 0.85 } })
    tl.from(badge.value, { autoAlpha: 0, y: 14 }, 0)
      .from(title.value, { autoAlpha: 0, y: 18 }, 0.15)
      .from(copy.value, { autoAlpha: 0, y: 12 }, 0.35)
      .from(backWrap.value, { autoAlpha: 0, y: 12 }, 0.5)
      .from(footerText.value, { autoAlpha: 0 }, 0.75)
  }, root)
})

onUnmounted(() => {
  if (gsapCtx) gsapCtx.revert()
  if (resizeObserver) resizeObserver.disconnect()
  if (wavesInstance) wavesInstance.cleanup()
})
</script>

<style scoped>
/* 如果 Tailwind 尚未完全加载，这里提供一些辅助样式 
  vertical-rl: 用于右下角的竖排文字效果
*/
.vertical-rl {
  writing-mode: vertical-rl;
}
</style>