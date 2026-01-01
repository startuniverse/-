<template>
  <div class="login-container">
    <!-- 粒子动画背景 -->
    <canvas ref="particlesCanvas" class="particles-canvas"></canvas>

    <div class="login-card">
      <div class="login-header">
        <h2>城市教育局综合信息服务平台</h2>
        <p>请登录您的账户</p>
      </div>

      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        class="login-form"
        size="large"
      >
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="用户名"
            :prefix-icon="User"
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="密码"
            :prefix-icon="Lock"
            show-password
          />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            :loading="loading"
            class="login-button"
            @click="handleLogin"
          >
            登录
          </el-button>
        </el-form-item>

        <div class="login-footer">
          <div style="margin-bottom: 10px;">
            <el-link type="primary" @click="$router.push('/register')">
              没有账户？立即注册
            </el-link>
          </div>
          <div>
            <el-link type="success" @click="$router.push('/teacher-register')">
              教师注册
            </el-link>
          </div>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/modules/user'

const router = useRouter()
const userStore = useUserStore()

const loginFormRef = ref()
const loading = ref(false)
const particlesCanvas = ref(null)
let animationId = null

// 鼠标位置追踪
const mouse = reactive({
  x: null,
  y: null,
  radius: 250 // 增大影响范围
})

// 太阳类（太阳系中心）
class Sun {
  constructor(canvas) {
    this.canvas = canvas
    this.x = canvas.width * 0.5
    this.y = canvas.height * 0.5
    this.radius = 8
    this.pulsePhase = 0
  }

  update() {
    this.pulsePhase += 0.02
    // 太阳轻微脉动
    this.radius = 8 + Math.sin(this.pulsePhase) * 1
  }

  draw(ctx) {
    // 太阳核心
    ctx.beginPath()
    ctx.arc(this.x, this.y, this.radius, 0, Math.PI * 2)
    const gradient = ctx.createRadialGradient(this.x, this.y, 0, this.x, this.y, this.radius)
    gradient.addColorStop(0, '#fff')
    gradient.addColorStop(0.5, '#ffd700')
    gradient.addColorStop(1, '#ff8c00')
    ctx.fillStyle = gradient
    ctx.fill()

    // 太阳光晕
    ctx.beginPath()
    ctx.arc(this.x, this.y, this.radius * 3, 0, Math.PI * 2)
    const glow = ctx.createRadialGradient(this.x, this.y, 0, this.x, this.y, this.radius * 3)
    glow.addColorStop(0, 'rgba(255, 215, 0, 0.3)')
    glow.addColorStop(0.5, 'rgba(255, 140, 0, 0.1)')
    glow.addColorStop(1, 'rgba(255, 140, 0, 0)')
    ctx.fillStyle = glow
    ctx.fill()
  }
}

// 行星类（轨道行星）
class Planet {
  constructor(canvas, orbitRadius, size, speed, color) {
    this.canvas = canvas
    this.orbitRadius = orbitRadius
    this.radius = size
    this.speed = speed
    this.color = color
    this.angle = Math.random() * Math.PI * 2
    this.orbitCenterX = canvas.width * 0.5
    this.orbitCenterY = canvas.height * 0.5
  }

  update() {
    this.angle += this.speed
    // 轨道中心跟随太阳位置
    this.orbitCenterX = this.canvas.width * 0.5
    this.orbitCenterY = this.canvas.height * 0.5
  }

  draw(ctx) {
    const x = this.orbitCenterX + Math.cos(this.angle) * this.orbitRadius
    const y = this.orbitCenterY + Math.sin(this.angle) * this.orbitRadius

    // 绘制行星
    ctx.beginPath()
    ctx.arc(x, y, this.radius, 0, Math.PI * 2)
    ctx.fillStyle = this.color
    ctx.fill()

    // 行星光晕
    ctx.beginPath()
    ctx.arc(x, y, this.radius * 2, 0, Math.PI * 2)
    const gradient = ctx.createRadialGradient(x, y, 0, x, y, this.radius * 2)
    gradient.addColorStop(0, this.color.replace(')', ', 0.4)').replace('rgb', 'rgba'))
    gradient.addColorStop(1, 'rgba(0, 0, 0, 0)')
    ctx.fillStyle = gradient
    ctx.fill()
  }

  drawOrbit(ctx) {
    // 绘制轨道线
    ctx.beginPath()
    ctx.arc(this.orbitCenterX, this.orbitCenterY, this.orbitRadius, 0, Math.PI * 2)
    ctx.strokeStyle = 'rgba(100, 150, 255, 0.1)'
    ctx.lineWidth = 0.5
    ctx.stroke()
  }

  getPosition() {
    return {
      x: this.orbitCenterX + Math.cos(this.angle) * this.orbitRadius,
      y: this.orbitCenterY + Math.sin(this.angle) * this.orbitRadius
    }
  }
}

// 星星类（星空背景）
class Star {
  constructor(canvas) {
    this.canvas = canvas
    this.reset()
  }

  reset() {
    this.x = Math.random() * this.canvas.width
    this.y = Math.random() * this.canvas.height
    this.radius = Math.random() * 2 + 0.5 // 增大星星大小
    this.opacity = Math.random() * 0.6 + 0.3 // 增强亮度
    this.twinkleSpeed = Math.random() * 0.03 + 0.01
    this.twinkleDirection = Math.random() > 0.5 ? 1 : -1
  }

  update() {
    // 闪烁效果
    this.opacity += this.twinkleSpeed * this.twinkleDirection

    if (this.opacity >= 0.9 || this.opacity <= 0.2) {
      this.twinkleDirection *= -1
    }

    // 缓慢漂移
    this.y -= 0.15
    if (this.y < 0) {
      this.y = this.canvas.height
      this.x = Math.random() * this.canvas.width
    }
  }

  draw(ctx) {
    // 星星核心（更亮）
    ctx.beginPath()
    ctx.arc(this.x, this.y, this.radius, 0, Math.PI * 2)
    ctx.fillStyle = `rgba(255, 255, 255, ${this.opacity})`
    ctx.fill()

    // 增强的光晕效果
    if (this.radius > 0.8) {
      ctx.beginPath()
      ctx.arc(this.x, this.y, this.radius * 3, 0, Math.PI * 2)
      const gradient = ctx.createRadialGradient(this.x, this.y, 0, this.x, this.y, this.radius * 3)
      gradient.addColorStop(0, `rgba(255, 255, 255, ${this.opacity * 0.5})`)
      gradient.addColorStop(0.5, `rgba(150, 200, 255, ${this.opacity * 0.2})`)
      gradient.addColorStop(1, 'rgba(255, 255, 255, 0)')
      ctx.fillStyle = gradient
      ctx.fill()
    }
  }
}

// 粒子类（交互粒子）
class Particle {
  constructor(canvas) {
    this.canvas = canvas
    this.reset()
  }

  reset() {
    this.x = Math.random() * this.canvas.width
    this.y = Math.random() * this.canvas.height
    this.vx = (Math.random() - 0.5) * 1.2
    this.vy = (Math.random() - 0.5) * 1.2
    this.radius = Math.random() * 2.5 + 1.5 // 稍微增大
    this.opacity = Math.random() * 0.5 + 0.4 // 增强亮度
    this.baseVx = this.vx
    this.baseVy = this.vy
    this.color = Math.random() > 0.7 ? 'rgba(180, 220, 255' : 'rgba(150, 200, 255' // 增加颜色变化
  }

  update() {
    // 基础持续运动（即使鼠标不动）
    this.x += this.vx
    this.y += this.vy

    // 鼠标交互 - 强力吸附效果
    if (mouse.x !== null && mouse.y !== null) {
      const dx = mouse.x - this.x
      const dy = mouse.y - this.y
      const distance = Math.sqrt(dx * dx + dy * dy)

      if (distance < mouse.radius) {
        // 粒子被强力吸引
        const forceDirectionX = dx / distance
        const forceDirectionY = dy / distance
        const force = (mouse.radius - distance) / mouse.radius
        const attractionStrength = 0.15 // 大幅增强吸引力

        this.vx += forceDirectionX * force * attractionStrength
        this.vy += forceDirectionY * force * attractionStrength

        // 增加透明度当靠近鼠标
        this.opacity = Math.min(1.0, this.opacity + 0.05)

        // 轨道环绕效果 - 粒子会绕着鼠标旋转
        const orbitForce = 0.02
        this.vx += -forceDirectionY * force * orbitForce
        this.vy += forceDirectionX * force * orbitForce
      } else {
        // 逐渐恢复基础速度和透明度
        this.opacity = Math.max(0.4, this.opacity - 0.02)
        this.vx += (this.baseVx - this.vx) * 0.03
        this.vy += (this.baseVy - this.vy) * 0.03
      }
    } else {
      // 没有鼠标交互时，缓慢恢复基础速度
      this.vx += (this.baseVx - this.vx) * 0.03
      this.vy += (this.baseVy - this.vy) * 0.03
    }

    // 限制最大速度
    const maxSpeed = 5
    const speed = Math.sqrt(this.vx * this.vx + this.vy * this.vy)
    if (speed > maxSpeed) {
      this.vx = (this.vx / speed) * maxSpeed
      this.vy = (this.vy / speed) * maxSpeed
    }

    // 边界检查（反弹而不是重置）
    if (this.x < 0 || this.x > this.canvas.width) {
      this.vx *= -1
      this.x = Math.max(0, Math.min(this.x, this.canvas.width))
    }
    if (this.y < 0 || this.y > this.canvas.height) {
      this.vy *= -1
      this.y = Math.max(0, Math.min(this.y, this.canvas.height))
    }
  }

  draw(ctx) {
    // 粒子核心（更亮）
    ctx.beginPath()
    ctx.arc(this.x, this.y, this.radius, 0, Math.PI * 2)
    ctx.fillStyle = `${this.color}, ${this.opacity})`
    ctx.fill()

    // 增强的光晕
    ctx.beginPath()
    ctx.arc(this.x, this.y, this.radius * 2.5, 0, Math.PI * 2)
    const gradient = ctx.createRadialGradient(this.x, this.y, 0, this.x, this.y, this.radius * 2.5)
    gradient.addColorStop(0, `${this.color}, ${this.opacity * 0.6})`)
    gradient.addColorStop(1, 'rgba(150, 200, 255, 0)')
    ctx.fillStyle = gradient
    ctx.fill()
  }
}

// 鼠标指针光效
class MousePointer {
  constructor() {
    this.particles = []
  }

  update(mouse) {
    if (mouse.x !== null && mouse.y !== null) {
      // 生成尾迹粒子
      if (Math.random() > 0.7) {
        this.particles.push({
          x: mouse.x,
          y: mouse.y,
          vx: (Math.random() - 0.5) * 2,
          vy: (Math.random() - 0.5) * 2,
          life: 1.0,
          radius: Math.random() * 2 + 1
        })
      }
    }

    // 更新尾迹
    this.particles = this.particles.filter(p => {
      p.x += p.vx
      p.y += p.vy
      p.life -= 0.03
      return p.life > 0
    })
  }

  draw(ctx, mouse) {
    if (mouse.x === null || mouse.y === null) return

    // 绘制鼠标影响范围（微弱提示）
    ctx.beginPath()
    ctx.arc(mouse.x, mouse.y, mouse.radius, 0, Math.PI * 2)
    ctx.strokeStyle = 'rgba(100, 150, 255, 0.05)'
    ctx.lineWidth = 1
    ctx.stroke()

    // 绘制中心光点
    ctx.beginPath()
    ctx.arc(mouse.x, mouse.y, 4, 0, Math.PI * 2)
    const gradient = ctx.createRadialGradient(mouse.x, mouse.y, 0, mouse.x, mouse.y, 4)
    gradient.addColorStop(0, 'rgba(255, 255, 255, 0.8)')
    gradient.addColorStop(1, 'rgba(100, 150, 255, 0.3)')
    ctx.fillStyle = gradient
    ctx.fill()

    // 绘制尾迹
    this.particles.forEach(p => {
      ctx.beginPath()
      ctx.arc(p.x, p.y, p.radius, 0, Math.PI * 2)
      ctx.fillStyle = `rgba(150, 200, 255, ${p.life * 0.6})`
      ctx.fill()
    })
  }
}

// 粒子系统
class ParticleSystem {
  constructor(canvas) {
    this.canvas = canvas
    this.ctx = canvas.getContext('2d')
    this.stars = []
    this.particles = []
    this.sun = null
    this.planets = []
    this.mousePointer = new MousePointer()
    this.resize()
    this.init()
  }

  init() {
    // 创建星空背景
    this.stars = []
    for (let i = 0; i < 200; i++) {
      this.stars.push(new Star(this.canvas))
    }

    // 创建太阳
    this.sun = new Sun(this.canvas)

    // 创建行星（太阳系）
    this.planets = [
      new Planet(this.canvas, 60, 2, 0.008, 'rgb(180, 180, 180)'), // 水星
      new Planet(this.canvas, 90, 3, 0.006, 'rgb(255, 165, 0)'),   // 金星
      new Planet(this.canvas, 120, 3.5, 0.005, 'rgb(100, 150, 255)'), // 地球
      new Planet(this.canvas, 150, 2.5, 0.004, 'rgb(200, 80, 50)'),   // 火星
      new Planet(this.canvas, 200, 5, 0.003, 'rgb(220, 180, 140)'),   // 木星
    ]

    // 创建交互粒子
    this.particles = []
    for (let i = 0; i < 80; i++) {
      this.particles.push(new Particle(this.canvas))
    }
  }

  resize() {
    this.canvas.width = window.innerWidth
    this.canvas.height = window.innerHeight
  }

  // 绘制星云背景
  drawNebula() {
    const gradient = this.ctx.createRadialGradient(
      this.canvas.width * 0.3, this.canvas.height * 0.3, 0,
      this.canvas.width * 0.5, this.canvas.height * 0.5, this.canvas.width * 0.9
    )
    gradient.addColorStop(0, 'rgba(40, 25, 80, 0.4)')
    gradient.addColorStop(0.4, 'rgba(25, 40, 100, 0.25)')
    gradient.addColorStop(0.7, 'rgba(15, 25, 60, 0.15)')
    gradient.addColorStop(1, 'rgba(10, 15, 40, 0)')

    this.ctx.fillStyle = gradient
    this.ctx.fillRect(0, 0, this.canvas.width, this.canvas.height)
  }

  // 连接交互粒子
  connect() {
    const maxDistance = 180
    for (let i = 0; i < this.particles.length; i++) {
      for (let j = i + 1; j < this.particles.length; j++) {
        const dx = this.particles[i].x - this.particles[j].x
        const dy = this.particles[i].y - this.particles[j].y
        const distance = Math.sqrt(dx * dx + dy * dy)

        if (distance < maxDistance) {
          this.ctx.beginPath()
          this.ctx.strokeStyle = `rgba(180, 220, 255, ${(1 - distance / maxDistance) * 0.25})`
          this.ctx.lineWidth = 0.8
          this.ctx.moveTo(this.particles[i].x, this.particles[i].y)
          this.ctx.lineTo(this.particles[j].x, this.particles[j].y)
          this.ctx.stroke()
        }
      }
    }
  }

  animate() {
    // 清除画布
    this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height)

    // 绘制深空背景
    this.ctx.fillStyle = '#050810'
    this.ctx.fillRect(0, 0, this.canvas.width, this.canvas.height)

    // 绘制星云
    this.drawNebula()

    // 更新和绘制星星（背景）
    this.stars.forEach(star => {
      star.update()
      star.draw(this.ctx)
    })

    // 绘制太阳系轨道
    this.planets.forEach(planet => {
      planet.drawOrbit(this.ctx)
    })

    // 更新和绘制太阳
    this.sun.update()
    this.sun.draw(this.ctx)

    // 更新和绘制行星
    this.planets.forEach(planet => {
      planet.update()
      planet.draw(this.ctx)
    })

    // 更新和绘制交互粒子
    this.particles.forEach(particle => {
      particle.update()
      particle.draw(this.ctx)
    })

    // 连接粒子
    this.connect()

    // 更新和绘制鼠标指针光效
    this.mousePointer.update(mouse)
    this.mousePointer.draw(this.ctx, mouse)

    animationId = requestAnimationFrame(() => this.animate())
  }

  destroy() {
    if (animationId) {
      cancelAnimationFrame(animationId)
    }
  }
}

let particleSystem = null

// 鼠标事件处理
const handleMouseMove = (e) => {
  mouse.x = e.clientX
  mouse.y = e.clientY
}

const handleMouseLeave = () => {
  mouse.x = null
  mouse.y = null
}

onMounted(() => {
  if (particlesCanvas.value) {
    particleSystem = new ParticleSystem(particlesCanvas.value)
    particleSystem.animate()

    // 鼠标事件监听
    particlesCanvas.value.addEventListener('mousemove', handleMouseMove)
    particlesCanvas.value.addEventListener('mouseleave', handleMouseLeave)

    // 窗口大小改变时重新调整
    window.addEventListener('resize', () => {
      particleSystem.resize()
      particleSystem.init()
    })
  }
})

onUnmounted(() => {
  if (particleSystem) {
    particleSystem.destroy()
  }

  // 移除事件监听
  if (particlesCanvas.value) {
    particlesCanvas.value.removeEventListener('mousemove', handleMouseMove)
    particlesCanvas.value.removeEventListener('mouseleave', handleMouseLeave)
  }
})

const loginForm = reactive({
  username: '',
  password: ''
})

const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return

  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        loading.value = true
        const res = await userStore.loginAction(loginForm.username, loginForm.password)
        console.log('登录响应:', res)
        console.log('用户角色:', userStore.roles)
        console.log('hasRole(TEACHER):', userStore.hasRole('TEACHER'))
        ElMessage.success('登录成功')

        // 根据角色跳转到不同页面
        if (userStore.hasRole('ADMIN')) {
          router.push('/admin/dashboard')
        } else if (userStore.hasRole('TEACHER')) {
          router.push('/teacher')
        } else {
          router.push('/')
        }
      } catch (error) {
        ElMessage.error(error.message || '登录失败')
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  position: relative;
  background: #050810;
  overflow: hidden;
}

.particles-canvas {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
}

.login-card {
  position: relative;
  z-index: 2;
  width: 440px;
  background: rgba(10, 15, 30, 0.88);
  border-radius: 25px;
  box-shadow:
    0 25px 80px rgba(0, 0, 0, 0.6),
    0 0 60px rgba(100, 150, 255, 0.15),
    inset 0 0 20px rgba(255, 255, 255, 0.02);
  padding: 50px;
  backdrop-filter: blur(20px);
  border: 1px solid rgba(150, 200, 255, 0.25);
  animation: floatCard 6s ease-in-out infinite;
}

@keyframes floatCard {
  0%, 100% { transform: translateY(0px); }
  50% { transform: translateY(-5px); }
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
  width: 100%;
}

.login-header h2 {
  color: #ffffff;
  font-size: 22px;
  font-weight: 700;
  margin-bottom: 10px;
  text-shadow: 0 0 20px rgba(100, 150, 255, 0.5), 0 2px 15px rgba(100, 150, 255, 0.3);
  letter-spacing: 1px;
  line-height: 1.3;
  word-wrap: break-word;
  white-space: normal;
}

.login-header p {
  color: #b8c5e8;
  font-size: 15px;
  font-weight: 400;
  text-shadow: 0 0 10px rgba(150, 200, 255, 0.2);
}

.login-form {
  margin-top: 25px;
}

.login-form :deep(.el-input__wrapper) {
  background-color: rgba(20, 30, 50, 0.6);
  border: 1px solid rgba(100, 150, 255, 0.4);
  border-radius: 12px;
  box-shadow: inset 0 2px 8px rgba(0, 0, 0, 0.3), 0 0 10px rgba(100, 150, 255, 0.08);
  transition: all 0.3s ease;
}

.login-form :deep(.el-input__wrapper:hover) {
  background-color: rgba(30, 45, 70, 0.7);
  border-color: rgba(120, 180, 255, 0.7);
  box-shadow: inset 0 2px 12px rgba(0, 0, 0, 0.4), 0 0 20px rgba(100, 150, 255, 0.15);
}

.login-form :deep(.el-input__wrapper.is-focus) {
  background-color: rgba(40, 60, 90, 0.8);
  border-color: rgba(150, 200, 255, 0.95);
  box-shadow: 0 0 0 3px rgba(100, 150, 255, 0.3), inset 0 2px 15px rgba(0, 0, 0, 0.5);
}

.login-form :deep(.el-input__inner) {
  color: #e8f0ff;
  font-weight: 600;
  text-shadow: 0 0 3px rgba(200, 220, 255, 0.2);
}

.login-form :deep(.el-input__inner::placeholder) {
  color: #7a90b8;
  font-weight: 400;
}

.login-button {
  width: 100%;
  margin-top: 15px;
  background: linear-gradient(135deg, #4a7dff 0%, #6a5af9 50%, #8a4af9 100%);
  border: none;
  font-weight: 700;
  letter-spacing: 1.5px;
  font-size: 16px;
  box-shadow: 0 6px 25px rgba(74, 125, 255, 0.5), 0 0 30px rgba(100, 150, 255, 0.2);
  transition: all 0.3s ease;
  text-transform: uppercase;
}

.login-button:hover {
  background: linear-gradient(135deg, #5a8dff 0%, #7a6af9 50%, #9a5af9 100%);
  box-shadow: 0 8px 35px rgba(74, 125, 255, 0.7), 0 0 40px rgba(100, 150, 255, 0.3);
  transform: translateY(-2px);
}

.login-button:active {
  transform: translateY(0);
}

.login-button:disabled {
  background: linear-gradient(135deg, #2a3d6f 0%, #3a2a6f 100%);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.3);
}

.login-footer {
  text-align: center;
  margin-top: 30px;
}

.login-footer :deep(.el-link) {
  font-weight: 600;
  transition: all 0.3s ease;
}

.login-footer :deep(.el-link.el-link--primary) {
  color: #7aa6ff;
  text-shadow: 0 0 10px rgba(100, 150, 255, 0.3);
}

.login-footer :deep(.el-link.el-link--primary:hover) {
  color: #aaccff;
  text-shadow: 0 0 15px rgba(150, 200, 255, 0.5);
}

.login-footer :deep(.el-link.el-link--success) {
  color: #6af9d9;
  text-shadow: 0 0 10px rgba(100, 255, 200, 0.3);
}

.login-footer :deep(.el-link.el-link--success:hover) {
  color: #9af9e6;
  text-shadow: 0 0 15px rgba(150, 255, 220, 0.5);
}
</style>
