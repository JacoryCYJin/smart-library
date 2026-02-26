<template>
  <section
    ref="heroRootRef"
    class="relative h-[calc(100vh-4rem)] overflow-hidden bg-gradient-to-b from-[#F0F4F8] to-white"
  >
    <div ref="matterContainerRef" class="absolute inset-0 z-0"></div>

    <div
      class="relative z-10 flex flex-col items-center h-full px-6 pt-40 pb-20 text-center pointer-events-none"
    >
      <div class="mx-auto w-full max-w-full">
        <div
          ref="titleRef"
          class="font-serif font-normal text-ink text-7xl leading-[0.85] tracking-wider"
        >
          重塑阅读的引力
        </div>

        <div
          ref="ctaRef"
          class="mt-12 flex items-center justify-center text-2xl"
        >
          <button
            type="button"
            class="pointer-events-auto cursor-pointer font-serif inline-flex items-center justify-center rounded-full bg-[#102A43] px-9 py-4 text-lg font-medium text-white transition-all duration-300 hover:bg-[#102A43]/90 hover:shadow-xl hover:-translate-y-1 focus:outline-none focus:ring-2 focus:ring-[#102A43]/20"
          >
            开始探索
          </button>
        </div>
      </div>

    </div>
  </section>
</template>

<script setup>
import { onBeforeUnmount, onMounted, ref } from 'vue'
import Matter from 'matter-js'
import gsap from 'gsap'
import { SplitText } from 'gsap/SplitText'

gsap.registerPlugin(SplitText)

const heroRootRef = ref(null)
const matterContainerRef = ref(null)
const ctaRef = ref(null)
const titleRef = ref(null)

let engine
let render
let runner
let mouse
let mouseConstraint

let introPlayed = false
let collisionStartHandler
let fallbackIntroTimeout
let gsapCtx
let splitTextRevert
let titleCharTargets

let canvasPointerDownHandler
let canvasPointerUpHandler
let canvasPointerLeaveHandler

let ground
let leftWall
let rightWall

const palette = ['#102A43', '#334E68', '#627D98', '#D9E2EC']
const popColor = '#D64545'

const randInt = (min, max) => Math.floor(Math.random() * (max - min + 1)) + min

const pickBookColor = () => {
  const roll = Math.random()
  if (roll < 0.1) return popColor
  return palette[Math.floor(Math.random() * palette.length)]
}

const createWalls = (width, height) => {
  const thickness = 200

  // 地面放在视口最底部
  ground = Matter.Bodies.rectangle(
    width / 2,
    height + thickness / 2,
    width + thickness * 2,
    thickness,
    {
      isStatic: true,
      render: { visible: false },
    },
  )

  leftWall = Matter.Bodies.rectangle(-thickness / 2, height / 2, thickness, height * 2, {
    isStatic: true,
    render: { visible: false },
  })

  rightWall = Matter.Bodies.rectangle(width + thickness / 2, height / 2, thickness, height * 2, {
    isStatic: true,
    render: { visible: false },
  })

  Matter.Composite.add(engine.world, [ground, leftWall, rightWall])
}

const removeWalls = () => {
  if (!engine) return
  if (ground) Matter.Composite.remove(engine.world, ground)
  if (leftWall) Matter.Composite.remove(engine.world, leftWall)
  if (rightWall) Matter.Composite.remove(engine.world, rightWall)
  ground = undefined
  leftWall = undefined
  rightWall = undefined
}

const createBooks = (width) => {
  const count = randInt(40, 60) // 增加数量以适应大屏

  const books = Array.from({ length: count }).map((_, i) => {
    const w = randInt(60,80)
    const h = randInt(90, 130)

    // 让书本在整个宽度范围内生成
    const minX = Math.max(w / 2, 1)
    const maxX = Math.max(minX + 1, width - w / 2)

    return Matter.Bodies.rectangle(
      randInt(Math.floor(minX), Math.floor(maxX)),
      -140 - i * 28,
      w,
      h,
      {
        restitution: 0.2,
        friction: 0.7,
        frictionAir: 0.02,
        chamfer: { radius: 6 },
        render: {
          fillStyle: pickBookColor(),
          strokeStyle: 'rgba(16,42,67,0.18)',
          lineWidth: 1,
        },
      },
    )
  })

  Matter.Composite.add(engine.world, books)
}

const resize = () => {
  if (!render || !engine || !matterContainerRef.value) return

  const container = matterContainerRef.value
  const width = container.clientWidth
  const height = container.clientHeight

  if (!width || !height) return

  render.options.width = width
  render.options.height = height

  render.canvas.style.width = `${width}px`
  render.canvas.style.height = `${height}px`

  if (typeof Matter.Render.setSize === 'function') {
    Matter.Render.setSize(render, width, height)
  }

  removeWalls()
  createWalls(width, height)

  Matter.Render.lookAt(render, {
    min: { x: 0, y: 0 },
    max: { x: width, y: height },
  })

  if (mouseConstraint?.mouse) {
    const ratio = render.options.pixelRatio || 1
    Matter.Mouse.setScale(mouseConstraint.mouse, { x: 1 / ratio, y: 1 / ratio })
    Matter.Mouse.setOffset(mouseConstraint.mouse, {
      x: render.bounds.min.x,
      y: render.bounds.min.y,
    })
  }
}

onMounted(() => {
  if (!matterContainerRef.value) return

  const container = matterContainerRef.value
  const width = container.clientWidth
  const height = container.clientHeight

  engine = Matter.Engine.create()

  render = Matter.Render.create({
    element: container,
    engine,
    options: {
      width,
      height,
      wireframes: false,
      background: 'transparent',
      pixelRatio: window.devicePixelRatio || 1,
    },
  })

  runner = Matter.Runner.create()

  createWalls(width, height)
  createBooks(width)

  const titleEl = titleRef.value
  if (titleEl) {
    const split = new SplitText(titleEl, { type: 'chars' })
    titleCharTargets = split.chars
    splitTextRevert = () => split.revert()
  } else {
    titleCharTargets = []
    splitTextRevert = undefined
  }

  gsapCtx = gsap.context(() => {
    gsap.set(titleCharTargets, {
      opacity: 0,
      y: 28,
      filter: 'blur(10px)',
    })

    if (ctaRef.value) {
      gsap.set(ctaRef.value, { opacity: 0, y: 16 })
    }
  }, heroRootRef.value)

  const playIntro = () => {
    if (introPlayed) return
    introPlayed = true

    gsap.timeline({ defaults: { ease: 'power3.out' } })
      .to(titleCharTargets, {
        opacity: 1,
        y: 0,
        filter: 'blur(0px)',
        duration: 0.9,
        stagger: 0.06,
      })
      .to(
        ctaRef.value,
        {
          opacity: 1,
          y: 0,
          duration: 0.6,
        },
        0.55,
      )
  }

  mouse = Matter.Mouse.create(render.canvas)

  const ratio = render.options.pixelRatio || 1
  Matter.Mouse.setScale(mouse, { x: 1 / ratio, y: 1 / ratio })
  Matter.Mouse.setOffset(mouse, { x: 0, y: 0 })

  mouseConstraint = Matter.MouseConstraint.create(engine, {
    mouse,
    constraint: {
      stiffness: 0.45,
      render: { visible: false },
    },
  })

  // 移除鼠标滚轮事件监听，允许页面滚动
  mouse.element.removeEventListener('mousewheel', mouse.mousewheel)
  mouse.element.removeEventListener('DOMMouseScroll', mouse.mousewheel)

  Matter.Composite.add(engine.world, mouseConstraint)
  render.mouse = mouse

  render.canvas.style.cursor = 'grab'
  canvasPointerDownHandler = () => {
    if (render?.canvas) render.canvas.style.cursor = 'grabbing'
  }
  canvasPointerUpHandler = () => {
    if (render?.canvas) render.canvas.style.cursor = 'grab'
  }
  canvasPointerLeaveHandler = () => {
    if (render?.canvas) render.canvas.style.cursor = 'grab'
  }
  render.canvas.addEventListener('pointerdown', canvasPointerDownHandler)
  window.addEventListener('pointerup', canvasPointerUpHandler)
  render.canvas.addEventListener('pointerleave', canvasPointerLeaveHandler)

  Matter.Render.run(render)
  Matter.Runner.run(runner, engine)

  collisionStartHandler = (event) => {
    if (introPlayed || !ground) return

    for (const pair of event.pairs) {
      if (pair.bodyA === ground || pair.bodyB === ground) {
        requestAnimationFrame(playIntro)
        break
      }
    }
  }

  Matter.Events.on(engine, 'collisionStart', collisionStartHandler)

  fallbackIntroTimeout = window.setTimeout(() => {
    playIntro()
  }, 1200)

  window.addEventListener('resize', resize)
  resize()
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', resize)

  if (gsapCtx) {
    gsapCtx.revert()
    gsapCtx = undefined
  }

  if (splitTextRevert) {
    splitTextRevert()
    splitTextRevert = undefined
  }

  if (render?.canvas && canvasPointerDownHandler) {
    render.canvas.removeEventListener('pointerdown', canvasPointerDownHandler)
    canvasPointerDownHandler = undefined
  }

  if (render?.canvas && canvasPointerLeaveHandler) {
    render.canvas.removeEventListener('pointerleave', canvasPointerLeaveHandler)
    canvasPointerLeaveHandler = undefined
  }

  if (canvasPointerUpHandler) {
    window.removeEventListener('pointerup', canvasPointerUpHandler)
    canvasPointerUpHandler = undefined
  }

  if (fallbackIntroTimeout) {
    window.clearTimeout(fallbackIntroTimeout)
    fallbackIntroTimeout = undefined
  }

  if (engine && collisionStartHandler) {
    Matter.Events.off(engine, 'collisionStart', collisionStartHandler)
    collisionStartHandler = undefined
  }

  if (render) {
    Matter.Render.stop(render)
  }

  if (runner) {
    Matter.Runner.stop(runner)
  }

  if (engine) {
    if (mouseConstraint) {
      Matter.Composite.remove(engine.world, mouseConstraint)
      mouseConstraint = undefined
    }

    removeWalls()
    Matter.World.clear(engine.world, false)
    Matter.Engine.clear(engine)
  }

  if (render?.canvas && render.canvas.parentNode) {
    render.canvas.parentNode.removeChild(render.canvas)
  }

  if (render) {
    render.textures = {}
  }

  engine = undefined
  render = undefined
  runner = undefined
})
</script>
