<template>
  <div class="register-container">
    <!-- 森林粒子背景 -->
    <canvas ref="forestCanvas" class="forest-canvas"></canvas>

    <div class="register-card">
      <div class="register-header">
        <h2>用户注册</h2>
        <p>创建您的账户</p>
      </div>

      <el-form
        ref="registerFormRef"
        :model="registerForm"
        :rules="registerRules"
        class="register-form"
        size="large"
      >
        <el-form-item prop="username">
          <el-input
            v-model="registerForm.username"
            placeholder="用户名"
            :prefix-icon="User"
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="registerForm.password"
            type="password"
            placeholder="密码"
            :prefix-icon="Lock"
            show-password
          />
        </el-form-item>

        <el-form-item prop="confirmPassword">
          <el-input
            v-model="registerForm.confirmPassword"
            type="password"
            placeholder="确认密码"
            :prefix-icon="Lock"
            show-password
          />
        </el-form-item>

        <el-form-item prop="realName">
          <el-input
            v-model="registerForm.realName"
            placeholder="真实姓名"
            :prefix-icon="User"
          />
        </el-form-item>

        <el-form-item prop="phone">
          <el-input
            v-model="registerForm.phone"
            placeholder="手机号"
            :prefix-icon="Phone"
          />
        </el-form-item>

        <el-form-item prop="schoolName">
          <el-input
            v-model="registerForm.schoolName"
            placeholder="请输入学校名称"
            :prefix-icon="School"
          />
        </el-form-item>

        <el-form-item prop="className">
          <el-input
            v-model="registerForm.className"
            placeholder="请输入班级名称（如：高三1班）"
          />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            :loading="loading"
            class="register-button"
            @click="handleRegister"
          >
            注册
          </el-button>
        </el-form-item>

        <div class="register-footer">
          <div style="display: flex; justify-content: center; gap: 15px;">
            <el-link type="primary" @click="$router.push('/')">
              返回首页
            </el-link>
            <el-link type="primary" @click="$router.push('/login')">
              已有账户？立即登录
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
import { User, Lock, Phone, School } from '@element-plus/icons-vue'
import { register } from '@/api/auth'

const router = useRouter()

const registerFormRef = ref()
const loading = ref(false)
const forestCanvas = ref(null)
let animationId = null

// 学校和班级数据（不再需要从后端获取）
const schoolList = ref([])
const classList = ref([])

// 森林粒子类（绿色叶子）
class Leaf {
  constructor(canvas) {
    this.canvas = canvas
    this.reset()
  }

  reset() {
    this.x = Math.random() * this.canvas.width
    this.y = Math.random() * this.canvas.height
    this.vx = (Math.random() - 0.5) * 0.3
    this.vy = Math.random() * 0.5 + 0.2 // 向下飘落
    this.size = Math.random() * 4 + 2
    this.rotation = Math.random() * Math.PI * 2
    this.rotationSpeed = (Math.random() - 0.5) * 0.05
    this.opacity = Math.random() * 0.4 + 0.3
    this.color = Math.random() > 0.5 ? 'rgba(76, 175, 80' : 'rgba(102, 187, 106' // 绿色系
  }

  update() {
    this.x += this.vx
    this.y += this.vy
    this.rotation += this.rotationSpeed

    // 边界检查
    if (this.y > this.canvas.height) {
      this.y = -10
      this.x = Math.random() * this.canvas.width
    }
    if (this.x < 0) this.x = this.canvas.width
    if (this.x > this.canvas.width) this.x = 0
  }

  draw(ctx) {
    ctx.save()
    ctx.translate(this.x, this.y)
    ctx.rotate(this.rotation)
    ctx.beginPath()
    ctx.ellipse(0, 0, this.size, this.size * 0.6, 0, 0, Math.PI * 2)
    ctx.fillStyle = `${this.color}, ${this.opacity})`
    ctx.fill()
    ctx.restore()
  }
}

// 森林光点类（萤火虫）
class Firefly {
  constructor(canvas) {
    this.canvas = canvas
    this.reset()
  }

  reset() {
    this.x = Math.random() * this.canvas.width
    this.y = Math.random() * this.canvas.height
    this.vx = (Math.random() - 0.5) * 0.8
    this.vy = (Math.random() - 0.5) * 0.8
    this.radius = Math.random() * 2 + 1
    this.opacity = Math.random() * 0.6 + 0.2
    this.pulseSpeed = Math.random() * 0.02 + 0.01
    this.pulsePhase = Math.random() * Math.PI * 2
  }

  update() {
    this.x += this.vx
    this.y += this.vy
    this.pulsePhase += this.pulseSpeed

    // 脉动效果
    this.opacity = 0.3 + Math.sin(this.pulsePhase) * 0.3

    // 边界反弹
    if (this.x < 0 || this.x > this.canvas.width) this.vx *= -1
    if (this.y < 0 || this.y > this.canvas.height) this.vy *= -1
  }

  draw(ctx) {
    // 核心
    ctx.beginPath()
    ctx.arc(this.x, this.y, this.radius, 0, Math.PI * 2)
    ctx.fillStyle = `rgba(150, 255, 150, ${this.opacity})`
    ctx.fill()

    // 光晕
    ctx.beginPath()
    ctx.arc(this.x, this.y, this.radius * 3, 0, Math.PI * 2)
    const gradient = ctx.createRadialGradient(this.x, this.y, 0, this.x, this.y, this.radius * 3)
    gradient.addColorStop(0, `rgba(150, 255, 150, ${this.opacity * 0.4})`)
    gradient.addColorStop(1, 'rgba(150, 255, 150, 0)')
    ctx.fillStyle = gradient
    ctx.fill()
  }
}

// 森林背景系统
class ForestSystem {
  constructor(canvas) {
    this.canvas = canvas
    this.ctx = canvas.getContext('2d')
    this.leaves = []
    this.fireflies = []
    this.resize()
    this.init()
  }

  init() {
    // 创建叶子
    this.leaves = []
    for (let i = 0; i < 60; i++) {
      this.leaves.push(new Leaf(this.canvas))
    }

    // 创建萤火虫
    this.fireflies = []
    for (let i = 0; i < 30; i++) {
      this.fireflies.push(new Firefly(this.canvas))
    }
  }

  resize() {
    this.canvas.width = window.innerWidth
    this.canvas.height = window.innerHeight
  }

  drawGradient() {
    // 森林渐变背景
    const gradient = this.ctx.createLinearGradient(0, 0, 0, this.canvas.height)
    gradient.addColorStop(0, '#0a2e1a')
    gradient.addColorStop(0.5, '#134f2c')
    gradient.addColorStop(1, '#1a5c3a')
    this.ctx.fillStyle = gradient
    this.ctx.fillRect(0, 0, this.canvas.width, this.canvas.height)
  }

  animate() {
    this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height)

    // 绘制背景
    this.drawGradient()

    // 更新和绘制叶子
    this.leaves.forEach(leaf => {
      leaf.update()
      leaf.draw(this.ctx)
    })

    // 更新和绘制萤火虫
    this.fireflies.forEach(firefly => {
      firefly.update()
      firefly.draw(this.ctx)
    })

    animationId = requestAnimationFrame(() => this.animate())
  }

  destroy() {
    if (animationId) {
      cancelAnimationFrame(animationId)
    }
  }
}

let forestSystem = null

onMounted(() => {
  if (forestCanvas.value) {
    forestSystem = new ForestSystem(forestCanvas.value)
    forestSystem.animate()

    window.addEventListener('resize', () => {
      forestSystem.resize()
      forestSystem.init()
    })
  }
})

onUnmounted(() => {
  if (forestSystem) {
    forestSystem.destroy()
  }
})

const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  realName: '',
  phone: '',
  schoolName: '',
  className: ''
})

const validatePass = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请输入密码'))
  } else {
    if (registerForm.confirmPassword !== '') {
      registerFormRef.value.validateField('confirmPassword')
    }
    callback()
  }
}

const validatePass2 = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== registerForm.password) {
    callback(new Error('两次输入密码不一致!'))
  } else {
    callback()
  }
}

const registerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, message: '用户名长度不能少于3位', trigger: 'blur' }
  ],
  password: [
    { required: true, validator: validatePass, trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validatePass2, trigger: 'blur' }
  ],
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  schoolName: [
    { required: true, message: '请输入学校名称', trigger: 'blur' }
  ],
  className: [
    { required: true, message: '请输入班级名称', trigger: 'blur' }
  ]
}

// 不再需要加载学校和班级列表

const handleRegister = async () => {
  if (!registerFormRef.value) return

  await registerFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        loading.value = true
        const data = {
          username: registerForm.username,
          password: registerForm.password,
          realName: registerForm.realName,
          phone: registerForm.phone,
          schoolName: registerForm.schoolName,
          className: registerForm.className
        }
        await register(data)
        ElMessage.success('注册成功')
        router.push('/login')
      } catch (error) {
        ElMessage.error(error.message || '注册失败')
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  position: relative;
  background: #0a2e1a;
  overflow: hidden;
}

.forest-canvas {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
}

.register-card {
  position: relative;
  z-index: 2;
  width: 460px;
  background: rgba(20, 40, 30, 0.85);
  border-radius: 18px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.4), 0 0 40px rgba(76, 175, 80, 0.15);
  padding: 45px;
  backdrop-filter: blur(15px);
  border: 1px solid rgba(76, 175, 80, 0.25);
}

.register-header {
  text-align: center;
  margin-bottom: 35px;
}

.register-header h2 {
  color: #a8f5a8;
  font-size: 24px;
  font-weight: 700;
  margin-bottom: 10px;
  text-shadow: 0 0 15px rgba(76, 175, 80, 0.5);
}

.register-header p {
  color: #7fcf8f;
  font-size: 14px;
}

.register-form {
  margin-top: 20px;
}

.register-form :deep(.el-input__wrapper) {
  background-color: rgba(15, 30, 20, 0.6);
  border: 1px solid rgba(76, 175, 80, 0.4);
  border-radius: 10px;
  box-shadow: inset 0 2px 8px rgba(0, 0, 0, 0.3);
  transition: all 0.3s ease;
}

.register-form :deep(.el-input__wrapper:hover) {
  background-color: rgba(20, 40, 25, 0.7);
  border-color: rgba(102, 187, 106, 0.6);
}

.register-form :deep(.el-input__wrapper.is-focus) {
  background-color: rgba(25, 50, 30, 0.8);
  border-color: rgba(129, 199, 132, 0.9);
  box-shadow: 0 0 0 2px rgba(76, 175, 80, 0.3);
}

.register-form :deep(.el-input__inner) {
  color: #e8ffe8;
  font-weight: 500;
}

.register-form :deep(.el-input__inner::placeholder) {
  color: #8fb89f;
}

.register-button {
  width: 100%;
  margin-top: 15px;
  background: linear-gradient(135deg, #2e7d32 0%, #4caf50 100%);
  border: none;
  font-weight: 600;
  letter-spacing: 1px;
  box-shadow: 0 4px 15px rgba(76, 175, 80, 0.4);
  transition: all 0.3s ease;
}

.register-button:hover {
  background: linear-gradient(135deg, #388e3c 0%, #66bb6a 100%);
  box-shadow: 0 6px 20px rgba(76, 175, 80, 0.6);
  transform: translateY(-1px);
}

.register-footer {
  text-align: center;
  margin-top: 25px;
}

.register-footer :deep(.el-link) {
  font-weight: 500;
}

.register-footer :deep(.el-link.el-link--primary) {
  color: #81c784;
}

.register-footer :deep(.el-link.el-link--primary:hover) {
  color: #a5d6a7;
}
</style>
