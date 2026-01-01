<template>
  <div class="profile">
    <el-row :gutter="20">
      <el-col :span="8" :xs="24">
        <el-card class="profile-card">
          <div class="profile-header">
            <el-avatar :size="80" :src="userInfo?.avatar || ''">
              {{ userInfo?.realName?.charAt(0) }}
            </el-avatar>
            <h3>{{ userInfo?.realName }}</h3>
            <p class="role">{{ userRoles.join(', ') }}</p>
          </div>
          <div class="profile-info">
            <div class="info-item">
              <span class="label">用户名:</span>
              <span class="value">{{ userInfo?.username }}</span>
            </div>
            <div class="info-item">
              <span class="label">手机号:</span>
              <span class="value">{{ userInfo?.phone || '未填写' }}</span>
            </div>
            <div class="info-item">
              <span class="label">邮箱:</span>
              <span class="value">{{ userInfo?.email || '未填写' }}</span>
            </div>
            <div class="info-item">
              <span class="label">部门:</span>
              <span class="value">{{ userInfo?.department || '未填写' }}</span>
            </div>
            <div class="info-item">
              <span class="label">职务:</span>
              <span class="value">{{ userInfo?.title || '未填写' }}</span>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="16" :xs="24">
        <el-card class="edit-card">
          <template #header>
            <div class="card-header">
              <span>编辑信息</span>
            </div>
          </template>
          <el-form
            ref="formRef"
            :model="form"
            :rules="rules"
            label-width="100px"
            size="large"
          >
            <el-form-item label="真实姓名" prop="realName">
              <el-input v-model="form.realName" placeholder="请输入真实姓名" />
            </el-form-item>
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入手机号" />
            </el-form-item>
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入邮箱" />
            </el-form-item>
            <el-form-item label="部门" prop="department">
              <el-input v-model="form.department" placeholder="请输入部门" />
            </el-form-item>
            <el-form-item label="职务" prop="title">
              <el-input v-model="form.title" placeholder="请输入职务" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSubmit">保存修改</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <el-card class="password-card" style="margin-top: 20px;">
          <template #header>
            <div class="card-header">
              <span>修改密码</span>
            </div>
          </template>
          <el-form
            ref="passwordFormRef"
            :model="passwordForm"
            :rules="passwordRules"
            label-width="100px"
            size="large"
          >
            <el-form-item label="原密码" prop="oldPassword">
              <el-input
                v-model="passwordForm.oldPassword"
                type="password"
                placeholder="请输入原密码"
                show-password
              />
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword">
              <el-input
                v-model="passwordForm.newPassword"
                type="password"
                placeholder="请输入新密码"
                show-password
              />
            </el-form-item>
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input
                v-model="passwordForm.confirmPassword"
                type="password"
                placeholder="请再次输入新密码"
                show-password
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleChangePassword">修改密码</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/modules/user'
import { getProfile, updateProfile, changePassword } from '@/api/campus'

const userStore = useUserStore()
const userInfo = computed(() => userStore.userInfo)
const userRoles = computed(() => userStore.roles)

const formRef = ref()
const passwordFormRef = ref()

const form = reactive({
  realName: '',
  phone: '',
  email: '',
  department: '',
  title: ''
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const rules = {
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }]
}

const validatePass = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请输入新密码'))
  } else {
    if (passwordForm.confirmPassword !== '') {
      passwordFormRef.value.validateField('confirmPassword')
    }
    callback()
  }
}

const validatePass2 = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入密码不一致!'))
  } else {
    callback()
  }
}

const passwordRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [{ required: true, validator: validatePass, trigger: 'blur' }],
  confirmPassword: [{ required: true, validator: validatePass2, trigger: 'blur' }]
}

const loadProfile = async () => {
  try {
    const data = await getProfile()
    console.log('=== 加载的个人信息数据:', data)
    Object.assign(form, data)
    console.log('=== 更新后的form:', JSON.parse(JSON.stringify(form)))
  } catch (error) {
    console.error('加载个人信息失败:', error)
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        // 显示完整的form数据
        const formData = {
          realName: form.realName,
          phone: form.phone,
          email: form.email,
          department: form.department,
          title: form.title
        }
        console.log('=== 前端准备发送的完整数据:', formData)
        console.log('=== form对象类型:', typeof form)
        console.log('=== form对象:', form)

        // 调用更新API
        const result = await updateProfile(formData)
        console.log('=== updateProfile返回结果:', result)

        ElMessage.success('个人信息更新成功')

        // 重新加载用户信息，更新store中的数据
        console.log('=== 开始重新加载用户信息...')
        await userStore.fetchUserInfo()
        console.log('=== 更新后store中的userInfo:', userStore.userInfo)

        // 重新加载表单数据
        console.log('=== 开始重新加载表单数据...')
        await loadProfile()

      } catch (error) {
        console.error('=== 更新失败:', error)
        ElMessage.error(error.message || '更新个人信息失败')
      }
    }
  })
}

const handleChangePassword = async () => {
  if (!passwordFormRef.value) return
  await passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        // 调用修改密码API
        const result = await changePassword({
          oldPassword: passwordForm.oldPassword,
          newPassword: passwordForm.newPassword
        })

        ElMessage.success('密码修改成功')

        // 清空表单
        Object.assign(passwordForm, {
          oldPassword: '',
          newPassword: '',
          confirmPassword: ''
        })
      } catch (error) {
        console.error('修改密码失败:', error)
        ElMessage.error(error.message || '修改密码失败')
      }
    }
  })
}

onMounted(() => {
  loadProfile()
})
</script>

<style scoped>
.profile {
  width: 100%;
}

.profile-card,
.edit-card,
.password-card {
  margin-bottom: 20px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 10px;
  border: 1px solid rgba(64, 158, 255, 0.15);
  transition: all 0.3s ease;
}

.profile-card:hover,
.edit-card:hover,
.password-card:hover {
  box-shadow: 0 6px 20px rgba(64, 158, 255, 0.15);
  border-color: rgba(64, 158, 255, 0.3);
}

.profile-header {
  text-align: center;
  padding: 20px 0;
  border-bottom: 1px solid rgba(64, 158, 255, 0.1);
  background: linear-gradient(135deg, #e8f4ff 0%, #f0f7ff 100%);
  margin: -20px -20px 20px -20px;
  border-radius: 10px 10px 0 0;
}

.profile-header h3 {
  margin: 15px 0 5px 0;
  color: #1e88e5;
  font-weight: 700;
}

.role {
  margin: 0;
  color: #5a7a9a;
  font-size: 14px;
  font-weight: 500;
}

.profile-info {
  padding: 20px 0;
}

.info-item {
  display: flex;
  justify-content: space-between;
  padding: 12px 0;
  border-bottom: 1px solid rgba(64, 158, 255, 0.08);
}

.info-item:last-child {
  border-bottom: none;
}

.label {
  color: #7a8a9a;
  font-size: 14px;
}

.value {
  color: #2c3e50;
  font-size: 14px;
  font-weight: 600;
}

.card-header {
  font-weight: 600;
  color: #1e88e5;
}

.edit-card :deep(.el-form-item__label) {
  color: #5a7a9a;
  font-weight: 500;
}

.edit-card :deep(.el-input__wrapper) {
  background-color: #f8fafc;
  border: 1px solid rgba(64, 158, 255, 0.2);
  border-radius: 8px;
}

.edit-card :deep(.el-input__wrapper:hover) {
  border-color: rgba(64, 158, 255, 0.5);
}

.edit-card :deep(.el-input__wrapper.is-focus) {
  border-color: #409EFF;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

.edit-card :deep(.el-button--primary) {
  background: linear-gradient(135deg, #409EFF 0%, #337ecc 100%);
  border: none;
  border-radius: 8px;
  font-weight: 600;
}

.password-card :deep(.el-form-item__label) {
  color: #5a7a9a;
  font-weight: 500;
}

.password-card :deep(.el-input__wrapper) {
  background-color: #f8fafc;
  border: 1px solid rgba(64, 158, 255, 0.2);
  border-radius: 8px;
}

.password-card :deep(.el-input__wrapper:hover) {
  border-color: rgba(64, 158, 255, 0.5);
}

.password-card :deep(.el-input__wrapper.is-focus) {
  border-color: #409EFF;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}
</style>
