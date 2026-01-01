<template>
  <div class="register-container">
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
import { ref, reactive, onMounted } from 'vue'
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
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.register-card {
  width: 500px;
  background: white;
  border-radius: 10px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
  padding: 40px;
}

.register-header {
  text-align: center;
  margin-bottom: 30px;
}

.register-header h2 {
  color: #333;
  font-size: 24px;
  margin-bottom: 10px;
}

.register-header p {
  color: #666;
  font-size: 14px;
}

.register-form {
  margin-top: 20px;
}

.register-button {
  width: 100%;
  margin-top: 10px;
}

.register-footer {
  text-align: center;
  margin-top: 20px;
}
</style>
