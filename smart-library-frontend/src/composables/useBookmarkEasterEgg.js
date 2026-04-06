import { onMounted, onBeforeUnmount } from 'vue'
import Matter from 'matter-js'

/**
 * 飘落书签彩蛋 Composable
 * 在全局点击时有极小概率触发书签飘落动画
 * 
 * @param {number} probability - 触发概率 (0-1)，默认 0.02 (2%)
 */
export function useBookmarkEasterEgg(probability = 0.02) {
  let engine = null
  let render = null
  let runner = null
  let container = null
  let clickHandler = null
  let resizeHandler = null
  let ground = null
  let leftWall = null
  let rightWall = null
  let cleanupTimeout = null

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

    ground = Matter.Bodies.rectangle(
      width / 2,
      height + thickness / 2,
      width + thickness * 2,
      thickness,
      {
        isStatic: true,
        render: { visible: false },
      }
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
    ground = null
    leftWall = null
    rightWall = null
  }

  const createBooks = (clickX, clickY, width) => {
    const count = randInt(8, 15)

    const books = Array.from({ length: count }).map((_, i) => {
      const w = randInt(40, 60)
      const h = randInt(60, 90)

      // 从点击位置附近生成
      const offsetX = randInt(-100, 100)
      const x = Math.max(w / 2, Math.min(width - w / 2, clickX + offsetX))

      return Matter.Bodies.rectangle(
        x,
        clickY - 100 - i * 20,
        w,
        h,
        {
          restitution: 0.3,
          friction: 0.7,
          frictionAir: 0.02,
          chamfer: { radius: 4 },
          render: {
            fillStyle: pickBookColor(),
            strokeStyle: 'rgba(16,42,67,0.18)',
            lineWidth: 1,
          },
        }
      )
    })

    Matter.Composite.add(engine.world, books)

    // 5秒后清理
    if (cleanupTimeout) clearTimeout(cleanupTimeout)
    cleanupTimeout = setTimeout(() => {
      cleanup()
    }, 5000)
  }

  const initPhysics = () => {
    if (engine) return

    const width = window.innerWidth
    const height = window.innerHeight

    // 创建容器
    container = document.createElement('div')
    container.style.cssText = `
      position: fixed;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      pointer-events: none;
      z-index: 9999;
    `
    document.body.appendChild(container)

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
      }
    })

    runner = Matter.Runner.create()

    createWalls(width, height)

    Matter.Render.run(render)
    Matter.Runner.run(runner, engine)
  }

  const handleResize = () => {
    if (!render || !engine) return

    const width = window.innerWidth
    const height = window.innerHeight

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
  }

  const handleClick = (event) => {
    // 随机判断是否触发
    if (Math.random() > probability) return

    // 初始化物理引擎（如果还没初始化）
    initPhysics()

    // 创建书签
    const clickX = event.clientX
    const clickY = event.clientY
    createBooks(clickX, clickY, window.innerWidth, window.innerHeight)
  }

  const cleanup = () => {
    if (cleanupTimeout) {
      clearTimeout(cleanupTimeout)
      cleanupTimeout = null
    }

    if (render) {
      Matter.Render.stop(render)
    }

    if (runner) {
      Matter.Runner.stop(runner)
    }

    if (engine) {
      removeWalls()
      Matter.World.clear(engine.world, false)
      Matter.Engine.clear(engine)
    }

    if (render?.canvas && render.canvas.parentNode) {
      render.canvas.parentNode.removeChild(render.canvas)
    }

    if (container && container.parentNode) {
      container.parentNode.removeChild(container)
    }

    if (render) {
      render.textures = {}
    }

    engine = null
    render = null
    runner = null
    container = null
  }

  onMounted(() => {
    clickHandler = handleClick
    resizeHandler = handleResize

    // 监听全局点击
    document.addEventListener('click', clickHandler)
    window.addEventListener('resize', resizeHandler)
  })

  onBeforeUnmount(() => {
    if (clickHandler) {
      document.removeEventListener('click', clickHandler)
      clickHandler = null
    }

    if (resizeHandler) {
      window.removeEventListener('resize', resizeHandler)
      resizeHandler = null
    }

    cleanup()
  })

  return {
    cleanup,
  }
}
