<template>
  <div class="register-container">
    <!-- 知识粒子背景 -->
    <canvas ref="knowledgeCanvas" class="knowledge-canvas"></canvas>

    <div class="register-card">
      <div class="register-header">
        <h2>教师注册</h2>
        <p>创建教师账户</p>
      </div>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        class="register-form"
        size="large"
      >
        <!-- 用户名 -->
        <el-form-item prop="username">
          <el-input
            v-model="form.username"
            placeholder="用户名"
            :prefix-icon="User"
          />
        </el-form-item>

        <!-- 密码 -->
        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="密码"
            :prefix-icon="Lock"
            show-password
          />
        </el-form-item>

        <!-- 确认密码 -->
        <el-form-item prop="confirmPassword">
          <el-input
            v-model="form.confirmPassword"
            type="password"
            placeholder="确认密码"
            :prefix-icon="Lock"
            show-password
          />
        </el-form-item>

        <!-- 真实姓名 -->
        <el-form-item prop="realName">
          <el-input
            v-model="form.realName"
            placeholder="真实姓名"
            :prefix-icon="User"
          />
        </el-form-item>

        <!-- 手机号 -->
        <el-form-item prop="phone">
          <el-input
            v-model="form.phone"
            placeholder="手机号"
            :prefix-icon="Phone"
          />
        </el-form-item>

        <!-- 邮箱（可选） -->
        <el-form-item prop="email">
          <el-input
            v-model="form.email"
            placeholder="邮箱（可选）"
            :prefix-icon="Message"
          />
        </el-form-item>

        <!-- 部门 -->
        <el-form-item prop="department">
          <el-input
            v-model="form.department"
            placeholder="部门/院系（如：数学教研组）"
            :prefix-icon="OfficeBuilding"
          />
        </el-form-item>

        <!-- 职称 -->
        <el-form-item prop="title">
          <el-input
            v-model="form.title"
            placeholder="职称（如：高级教师）"
            :prefix-icon="UserFilled"
          />
        </el-form-item>

        <!-- 学校选择 -->
        <el-form-item label="学校" prop="school">
          <el-select
            v-model="form.school"
            placeholder="选择或输入学校"
            style="width: 100%"
            filterable
            allow-create
            default-first-option
            :loading="schoolsLoading"
          >
            <el-option
              v-for="item in schools"
              :key="item.id"
              :label="item.schoolName"
              :value="item.schoolName"
            />
          </el-select>
          <div style="margin-top: 5px; font-size: 12px; color: #999;">
            提示：可直接输入新学校名称
          </div>
        </el-form-item>

        <!-- 注册按钮 -->
        <el-form-item>
          <el-button
            type="primary"
            :loading="loading"
            class="register-button"
            @click="handleRegister"
          >
            注册教师账户
          </el-button>
        </el-form-item>

        <!-- 底部链接 -->
        <div class="register-footer">
          <div style="margin-bottom: 10px;">
            <el-link type="primary" @click="$router.push('/login')">
              已有账户？立即登录
            </el-link>
          </div>
          <div>
            <el-link type="default" @click="$router.push('/register')">
              普通用户注册
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
import { User, Lock, Phone, Message, OfficeBuilding, UserFilled } from '@element-plus/icons-vue'
import { registerTeacher } from '@/api/auth'
import request from '@/utils/request'

const router = useRouter()
const formRef = ref()
const loading = ref(false)
const schoolsLoading = ref(false)
const schools = ref([])
const knowledgeCanvas = ref(null)
let animationId = null

// 书本页面类
class Page {
  constructor(canvas) {
    this.canvas = canvas
    this.reset()
  }

  reset() {
    this.x = Math.random() * this.canvas.width
    this.y = Math.random() * this.canvas.height
    this.vx = (Math.random() - 0.5) * 0.4
    this.vy = (Math.random() - 0.5) * 0.4
    this.width = Math.random() * 15 + 10
    this.height = Math.random() * 20 + 15
    this.rotation = Math.random() * Math.PI * 2
    this.rotationSpeed = (Math.random() - 0.5) * 0.02
    this.opacity = Math.random() * 0.3 + 0.2
  }

  update() {
    this.x += this.vx
    this.y += this.vy
    this.rotation += this.rotationSpeed

    // 边界检查
    if (this.x < -50 || this.x > this.canvas.width + 50 ||
        this.y < -50 || this.y > this.canvas.height + 50) {
      this.reset()
    }
  }

  draw(ctx) {
    ctx.save()
    ctx.translate(this.x, this.y)
    ctx.rotate(this.rotation)

    // 书页
    ctx.beginPath()
    ctx.moveTo(-this.width / 2, -this.height / 2)
    ctx.lineTo(this.width / 2, -this.height / 2)
    ctx.lineTo(this.width / 2, this.height / 2)
    ctx.lineTo(-this.width / 2, this.height / 2)
    ctx.closePath()
    ctx.fillStyle = `rgba(255, 235, 205, ${this.opacity})`
    ctx.fill()
    ctx.strokeStyle = `rgba(210, 180, 140, ${this.opacity * 1.5})`
    ctx.lineWidth = 0.5
    ctx.stroke()

    ctx.restore()
  }
}

// 知识光点类（智慧之光）
class WisdomLight {
  constructor(canvas) {
    this.canvas = canvas
    this.reset()
  }

  reset() {
    this.x = Math.random() * this.canvas.width
    this.y = Math.random() * this.canvas.height
    this.vx = (Math.random() - 0.5) * 0.6
    this.vy = (Math.random() - 0.5) * 0.6
    this.radius = Math.random() * 2.5 + 1
    this.opacity = Math.random() * 0.5 + 0.2
    this.pulseSpeed = Math.random() * 0.03 + 0.01
    this.pulsePhase = Math.random() * Math.PI * 2
    this.colorType = Math.random()
  }

  update() {
    this.x += this.vx
    this.y += this.vy
    this.pulsePhase += this.pulseSpeed

    // 脉动效果
    this.opacity = 0.2 + Math.sin(this.pulsePhase) * 0.3

    // 边界反弹
    if (this.x < 0 || this.x > this.canvas.width) this.vx *= -1
    if (this.y < 0 || this.y > this.canvas.height) this.vy *= -1
  }

  draw(ctx) {
    let color
    if (this.colorType < 0.5) {
      color = `rgba(255, 215, 0` // 金色 - 智慧
    } else if (this.colorType < 0.8) {
      color = `rgba(255, 193, 7` // 琥珀色
    } else {
      color = `rgba(255, 235, 59` // 浅金
    }

    // 核心
    ctx.beginPath()
    ctx.arc(this.x, this.y, this.radius, 0, Math.PI * 2)
    ctx.fillStyle = `${color}, ${this.opacity})`
    ctx.fill()

    // 光晕
    ctx.beginPath()
    ctx.arc(this.x, this.y, this.radius * 4, 0, Math.PI * 2)
    const gradient = ctx.createRadialGradient(this.x, this.y, 0, this.x, this.y, this.radius * 4)
    gradient.addColorStop(0, `${color}, ${this.opacity * 0.3})`)
    gradient.addColorStop(1, 'rgba(255, 215, 0, 0)')
    ctx.fillStyle = gradient
    ctx.fill()
  }
}

// 符号类（数学/知识符号）
class Symbol {
  constructor(canvas) {
    this.canvas = canvas
    this.reset()
  }

  reset() {
    this.x = Math.random() * this.canvas.width
    this.y = Math.random() * this.canvas.height
    this.vy = Math.random() * 0.3 + 0.1 // 向上飘
    this.size = Math.random() * 12 + 8
    this.opacity = Math.random() * 0.3 + 0.1
    this.rotation = Math.random() * Math.PI * 2
    this.rotationSpeed = (Math.random() - 0.5) * 0.01
    this.chars = ['∑', '∫', '∂', '∞', '∆', '∏', '√', '∴']
    this.char = this.chars[Math.floor(Math.random() * this.chars.length)]
  }

  update() {
    this.y -= this.vy
    this.rotation += this.rotationSpeed

    if (this.y < -20) {
      this.y = this.canvas.height + 20
      this.x = Math.random() * this.canvas.width
    }
  }

  draw(ctx) {
    ctx.save()
    ctx.translate(this.x, this.y)
    ctx.rotate(this.rotation)
    ctx.font = `${this.size}px serif`
    ctx.fillStyle = `rgba(255, 255, 255, ${this.opacity})`
    ctx.textAlign = 'center'
    ctx.textBaseline = 'middle'
    ctx.fillText(this.char, 0, 0)
    ctx.restore()
  }
}

// 知识背景系统
class KnowledgeSystem {
  constructor(canvas) {
    this.canvas = canvas
    this.ctx = canvas.getContext('2d')
    this.pages = []
    this.lights = []
    this.symbols = []
    this.resize()
    this.init()
  }

  init() {
    // 创建书本页面
    this.pages = []
    for (let i = 0; i < 40; i++) {
      this.pages.push(new Page(this.canvas))
    }

    // 创建智慧光点
    this.lights = []
    for (let i = 0; i < 50; i++) {
      this.lights.push(new WisdomLight(this.canvas))
    }

    // 创建知识符号
    this.symbols = []
    for (let i = 0; i < 20; i++) {
      this.symbols.push(new Symbol(this.canvas))
    }
  }

  resize() {
    this.canvas.width = window.innerWidth
    this.canvas.height = window.innerHeight
  }

  drawGradient() {
    // 深蓝到紫色的渐变 - 知识与深度
    const gradient = this.ctx.createLinearGradient(0, 0, 0, this.canvas.height)
    gradient.addColorStop(0, '#0f1b2e')
    gradient.addColorStop(0.5, '#1a2744')
    gradient.addColorStop(1, '#2a1b3d')
    this.ctx.fillStyle = gradient
    this.ctx.fillRect(0, 0, this.canvas.width, this.canvas.height)
  }

  drawBookShadows() {
    // 绘制书本轮廓的阴影
    const centerX = this.canvas.width * 0.5
    const centerY = this.canvas.height * 0.5

    this.ctx.save()
    this.ctx.globalAlpha = 0.05
    this.ctx.fillStyle = '#ffd700'

    // 书本形状
    for (let i = 0; i < 3; i++) {
      this.ctx.beginPath()
      this.ctx.rect(centerX - 80 + i * 15, centerY - 100, 60, 200)
      this.ctx.fill()
    }
    this.ctx.restore()
  }

  animate() {
    this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height)

    // 绘制背景
    this.drawGradient()

    // 绘制书本阴影
    this.drawBookShadows()

    // 更新和绘制书页
    this.pages.forEach(page => {
      page.update()
      page.draw(this.ctx)
    })

    // 更新和绘制智慧光点
    this.lights.forEach(light => {
      light.update()
      light.draw(this.ctx)
    })

    // 更新和绘制符号
    this.symbols.forEach(symbol => {
      symbol.update()
      symbol.draw(this.ctx)
    })

    animationId = requestAnimationFrame(() => this.animate())
  }

  destroy() {
    if (animationId) {
      cancelAnimationFrame(animationId)
    }
  }
}

let knowledgeSystem = null

onMounted(() => {
  if (knowledgeCanvas.value) {
    knowledgeSystem = new KnowledgeSystem(knowledgeCanvas.value)
    knowledgeSystem.animate()

    window.addEventListener('resize', () => {
      knowledgeSystem.resize()
      knowledgeSystem.init()
    })
  }
})

onUnmounted(() => {
  if (knowledgeSystem) {
    knowledgeSystem.destroy()
  }
})

const form = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  realName: '',
  phone: '',
  email: '',
  department: '',
  title: '',
  school: ''  // 可以是选择的学校ID对应的名称，也可以是手动输入的新学校名称
})

const validatePass = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请输入密码'))
  } else {
    if (form.confirmPassword !== '') {
      formRef.value.validateField('confirmPassword')
    }
    callback()
  }
}

const validatePass2 = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== form.password) {
    callback(new Error('两次输入密码不一致!'))
  } else {
    callback()
  }
}

const rules = {
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
  department: [
    { required: true, message: '请输入部门/院系', trigger: 'blur' }
  ],
  title: [
    { required: true, message: '请输入职称', trigger: 'blur' }
  ],
  school: [
    { required: true, message: '请选择或输入学校名称', trigger: 'change' }
  ]
}

// 加载学校列表
const loadSchools = async () => {
  try {
    schoolsLoading.value = true
    const response = await request({
      url: '/backend/school/list',
      method: 'get'
    })
    if (response.code === 200) {
      schools.value = response.data
    }
  } catch (error) {
    console.error('加载学校列表失败:', error)
  } finally {
    schoolsLoading.value = false
  }
}

// 处理注册
const handleRegister = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        loading.value = true

        // 构建请求数据
        const data = {
          username: form.username,
          password: form.password,
          realName: form.realName,
          phone: form.phone,
          email: form.email,
          department: form.department,
          title: form.title
        }

        // 判断输入的是现有学校还是新学校
        const selectedSchool = schools.value.find(s => s.schoolName === form.school)
        if (selectedSchool) {
          // 选择的是现有学校
          data.schoolId = selectedSchool.id
        } else {
          // 输入的是新学校名称
          data.customSchoolName = form.school
        }

        console.log('提交数据:', JSON.stringify(data, null, 2))

        await registerTeacher(data)
        ElMessage.success('教师注册成功！')
        setTimeout(() => {
          router.push('/login')
        }, 1000)
      } catch (error) {
        console.error('注册失败:', error)
        ElMessage.error(error.message || '注册失败')
      } finally {
        loading.value = false
      }
    }
  })
}

onMounted(() => {
  loadSchools()
})
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  position: relative;
  background: #0f1b2e;
  overflow: hidden;
  padding: 20px;
}

.knowledge-canvas {
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
  width: 520px;
  background: rgba(15, 27, 46, 0.88);
  border-radius: 18px;
  box-shadow: 0 25px 70px rgba(0, 0, 0, 0.5), 0 0 50px rgba(255, 215, 0, 0.15);
  padding: 45px;
  backdrop-filter: blur(15px);
  border: 1px solid rgba(255, 215, 0, 0.25);
}

.register-header {
  text-align: center;
  margin-bottom: 35px;
}

.register-header h2 {
  color: #ffd700;
  font-size: 24px;
  font-weight: 700;
  margin-bottom: 10px;
  text-shadow: 0 0 20px rgba(255, 215, 0, 0.5);
  letter-spacing: 1px;
}

.register-header p {
  color: #ffc107;
  font-size: 14px;
  text-shadow: 0 0 10px rgba(255, 193, 7, 0.3);
}

.register-form {
  margin-top: 20px;
}

.register-form :deep(.el-input__wrapper) {
  background-color: rgba(20, 30, 50, 0.6);
  border: 1px solid rgba(255, 215, 0, 0.35);
  border-radius: 10px;
  box-shadow: inset 0 2px 8px rgba(0, 0, 0, 0.3);
  transition: all 0.3s ease;
}

.register-form :deep(.el-input__wrapper:hover) {
  background-color: rgba(30, 45, 70, 0.7);
  border-color: rgba(255, 215, 0, 0.6);
}

.register-form :deep(.el-input__wrapper.is-focus) {
  background-color: rgba(40, 55, 85, 0.85);
  border-color: rgba(255, 215, 0, 0.95);
  box-shadow: 0 0 0 2px rgba(255, 215, 0, 0.3);
}

.register-form :deep(.el-input__inner) {
  color: #fff8e1;
  font-weight: 500;
}

.register-form :deep(.el-input__inner::placeholder) {
  color: #d4c59a;
}

.register-form :deep(.el-select) {
  width: 100%;
}

.register-form :deep(.el-select .el-input__wrapper) {
  background-color: rgba(20, 30, 50, 0.6);
  border: 1px solid rgba(255, 215, 0, 0.35);
}

.register-form :deep(.el-select .el-input__wrapper:hover) {
  border-color: rgba(255, 215, 0, 0.6);
}

.register-form :deep(.el-select .el-input__wrapper.is-focus) {
  border-color: rgba(255, 215, 0, 0.95);
  box-shadow: 0 0 0 2px rgba(255, 215, 0, 0.3);
}

.register-form :deep(.el-select-dropdown__item) {
  background-color: #1a2744;
  color: #fff8e1;
}

.register-form :deep(.el-select-dropdown__item:hover) {
  background-color: #2a3754;
}

.register-form :deep(.el-select-dropdown__item.selected) {
  color: #ffd700;
}

.register-button {
  width: 100%;
  margin-top: 15px;
  background: linear-gradient(135deg, #c9a000 0%, #ffd700 50%, #ffb700 100%);
  border: none;
  font-weight: 700;
  letter-spacing: 1px;
  font-size: 15px;
  box-shadow: 0 4px 20px rgba(255, 215, 0, 0.4);
  transition: all 0.3s ease;
  text-transform: uppercase;
}

.register-button:hover {
  background: linear-gradient(135deg, #d4b000 0%, #ffe066 50%, #ffc107 100%);
  box-shadow: 0 6px 25px rgba(255, 215, 0, 0.6);
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
  color: #ffd700;
  text-shadow: 0 0 10px rgba(255, 215, 0, 0.3);
}

.register-footer :deep(.el-link.el-link--primary:hover) {
  color: #ffe066;
  text-shadow: 0 0 15px rgba(255, 224, 102, 0.5);
}

.register-footer :deep(.el-link.el-link--default) {
  color: #b8c5e8;
}

.register-footer :deep(.el-link.el-link--default:hover) {
  color: #d4e0ff;
}
</style>
