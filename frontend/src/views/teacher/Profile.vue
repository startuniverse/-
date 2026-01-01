<template>
  <div class="teacher-profile">
    <el-row :gutter="20">
      <!-- 左侧：个人信息卡片 -->
      <el-col :span="16">
        <el-card class="profile-card">
          <template #header>
            <div class="card-header">
              <span>👤 教师个人信息</span>
              <el-button type="primary" @click="editMode = !editMode">
                {{ editMode ? '取消编辑' : '编辑信息' }}
              </el-button>
            </div>
          </template>

          <el-form
            ref="formRef"
            :model="profileForm"
            :rules="rules"
            label-width="120px"
            :disabled="!editMode"
            class="profile-form"
          >
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="用户名" prop="username">
                  <el-input v-model="profileForm.username" disabled placeholder="用户名" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="真实姓名" prop="realName">
                  <el-input v-model="profileForm.realName" placeholder="请输入真实姓名" />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="手机号" prop="phone">
                  <el-input v-model="profileForm.phone" placeholder="请输入手机号" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="邮箱" prop="email">
                  <el-input v-model="profileForm.email" placeholder="请输入邮箱" />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="所属学院" prop="department">
                  <el-input v-model="profileForm.department" placeholder="请输入所属学院/部门" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="职称" prop="title">
                  <el-select v-model="profileForm.title" placeholder="请选择职称" style="width: 100%;">
                    <el-option label="助教" value="助教" />
                    <el-option label="讲师" value="讲师" />
                    <el-option label="副教授" value="副教授" />
                    <el-option label="教授" value="教授" />
                    <el-option label="高级教师" value="高级教师" />
                    <el-option label="特级教师" value="特级教师" />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="20">
              <el-col :span="24">
                <el-form-item label="所属学校" prop="schoolName">
                  <el-select
                    v-model="profileForm.schoolName"
                    placeholder="请选择或输入学校"
                    filterable
                    allow-create
                    style="width: 100%;"
                  >
                    <el-option label="云南大学" value="云南大学" />
                    <el-option label="第一中学" value="第一中学" />
                    <el-option label="实验小学" value="实验小学" />
                    <el-option label="第二中学" value="第二中学" />
                    <el-option label="职业技术学校" value="职业技术学校" />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="20">
              <el-col :span="24">
                <el-form-item label="个人简介" prop="bio">
                  <el-input
                    v-model="profileForm.bio"
                    type="textarea"
                    :rows="4"
                    placeholder="请输入个人简介（可选）"
                  />
                </el-form-item>
              </el-col>
            </el-row>

            <el-form-item v-if="editMode">
              <el-button type="primary" @click="saveProfile" :loading="saving">
                保存修改
              </el-button>
              <el-button @click="resetForm">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>

      <!-- 右侧：账号安全和统计 -->
      <el-col :span="8">
        <!-- 账号安全 -->
        <el-card class="security-card">
          <template #header>
            <div class="card-header">
              <span>🔒 账号安全</span>
            </div>
          </template>
          <div class="security-actions">
            <div class="action-item">
              <div class="action-info">
                <div class="action-title">修改密码</div>
                <div class="action-desc">定期更新密码更安全</div>
              </div>
              <el-button link type="primary" @click="showChangePassword">修改</el-button>
            </div>
            <div class="action-item">
              <div class="action-info">
                <div class="action-title">绑定手机</div>
                <div class="action-desc">{{ profileForm.phone ? '已绑定' : '未绑定' }}</div>
              </div>
              <el-button link type="primary" @click="bindPhone">绑定</el-button>
            </div>
            <div class="action-item">
              <div class="action-info">
                <div class="action-title">绑定邮箱</div>
                <div class="action-desc">{{ profileForm.email ? '已绑定' : '未绑定' }}</div>
              </div>
              <el-button link type="primary" @click="bindEmail">绑定</el-button>
            </div>
          </div>
        </el-card>

        <!-- 教学统计 -->
        <el-card class="stats-card" style="margin-top: 20px;">
          <template #header>
            <div class="card-header">
              <span>📊 教学统计</span>
            </div>
          </template>
          <div class="stats-list">
            <div class="stat-item">
              <span class="stat-label">教龄</span>
              <span class="stat-value">{{ stats.teachingYears }}年</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">累计授课</span>
              <span class="stat-value">{{ stats.totalClasses }}课时</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">学生总数</span>
              <span class="stat-value">{{ stats.totalStudents }}人</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">作业发布</span>
              <span class="stat-value">{{ stats.totalAssignments }}次</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 修改密码对话框 -->
    <el-dialog v-model="passwordDialogVisible" title="修改密码" width="450px">
      <el-form :model="passwordForm" :rules="passwordRules" label-width="100px" ref="passwordFormRef">
        <el-form-item label="原密码" prop="oldPassword">
          <el-input v-model="passwordForm.oldPassword" type="password" show-password placeholder="请输入原密码" />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password" show-password placeholder="请输入新密码" />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="passwordForm.confirmPassword" type="password" show-password placeholder="请再次输入新密码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="passwordDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="changePassword" :loading="changingPassword">确认修改</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/store/modules/user'
import request from '@/utils/request'

const userStore = useUserStore()
const formRef = ref()
const passwordFormRef = ref()

const editMode = ref(false)
const saving = ref(false)
const passwordDialogVisible = ref(false)
const changingPassword = ref(false)

// 个人信息表单
const profileForm = reactive({
  username: '',
  realName: '',
  phone: '',
  email: '',
  department: '',
  title: '',
  schoolName: '',
  bio: ''
})

// 密码表单
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 验证规则
const rules = {
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  department: [{ required: true, message: '请输入所属学院', trigger: 'blur' }],
  title: [{ required: true, message: '请选择职称', trigger: 'change' }],
  schoolName: [{ required: true, message: '请选择或输入学校', trigger: 'blur' }]
}

const passwordRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 教学统计（模拟数据）
const stats = reactive({
  teachingYears: 8,
  totalClasses: 1256,
  totalStudents: 280,
  totalAssignments: 342
})

// 从用户store加载个人信息
const loadProfile = () => {
  const userInfo = userStore.userInfo
  if (userInfo) {
    profileForm.username = userInfo.username || ''
    profileForm.realName = userInfo.realName || ''
    profileForm.phone = userInfo.phone || ''
    profileForm.email = userInfo.email || ''
    profileForm.department = userInfo.department || ''
    profileForm.title = userInfo.title || ''
    profileForm.schoolName = userInfo.schoolName || ''
    profileForm.bio = userInfo.bio || ''
  }
}

// 保存个人信息
const saveProfile = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      saving.value = true
      try {
        // 调用后端API更新个人信息
        // 拦截器已经处理了响应，成功时返回data，失败时会抛异常
        await request({
          url: '/campus/profile/update',
          method: 'post',
          data: {
            realName: profileForm.realName,
            phone: profileForm.phone,
            email: profileForm.email,
            department: profileForm.department,
            title: profileForm.title,
            schoolName: profileForm.schoolName,
            bio: profileForm.bio
          }
        })

        // 更新store中的用户信息
        userStore.userInfo = { ...userStore.userInfo, ...profileForm }

        // 同步到localStorage
        localStorage.setItem('userInfo', JSON.stringify(userStore.userInfo))

        ElMessage.success('个人信息保存成功')
        editMode.value = false
      } catch (error) {
        console.error('保存失败:', error)
        ElMessage.error('保存失败，请重试')
      } finally {
        saving.value = false
      }
    }
  })
}

// 重置表单
const resetForm = () => {
  loadProfile()
  formRef.value?.clearValidate()
}

// 显示修改密码对话框
const showChangePassword = () => {
  passwordForm.oldPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
  passwordDialogVisible.value = true
}

// 修改密码
const changePassword = async () => {
  if (!passwordFormRef.value) return

  await passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      changingPassword.value = true
      try {
        // 模拟API调用
        await new Promise(resolve => setTimeout(resolve, 800))

        ElMessage.success('密码修改成功')
        passwordDialogVisible.value = false
      } catch (error) {
        ElMessage.error('修改失败，请重试')
      } finally {
        changingPassword.value = false
      }
    }
  })
}

// 绑定手机
const bindPhone = () => {
  if (profileForm.phone) {
    ElMessage.info('手机号已绑定：' + profileForm.phone)
  } else {
    ElMessage.info('请在个人信息中填写手机号并保存')
  }
}

// 绑定邮箱
const bindEmail = () => {
  if (profileForm.email) {
    ElMessage.info('邮箱已绑定：' + profileForm.email)
  } else {
    ElMessage.info('请在个人信息中填写邮箱并保存')
  }
}

onMounted(() => {
  loadProfile()
})
</script>

<style scoped>
.teacher-profile {
  width: 100%;
}

.profile-card,
.security-card,
.stats-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
}

.profile-form {
  margin-top: 10px;
}

.security-actions {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.action-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  background: #f5f7fa;
  border-radius: 6px;
}

.action-info {
  flex: 1;
}

.action-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}

.action-desc {
  font-size: 12px;
  color: #909399;
}

.stats-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.stat-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #ebeef5;
}

.stat-item:last-child {
  border-bottom: none;
}

.stat-label {
  font-size: 14px;
  color: #606266;
}

.stat-value {
  font-size: 16px;
  font-weight: bold;
  color: #409EFF;
}
</style>
