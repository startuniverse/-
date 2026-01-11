<template>
  <div class="teacher-dashboard">
    <!-- 班级未选择提醒 -->
    <el-row :gutter="20" v-if="showClassReminder">
      <el-col :span="24">
        <el-alert
          title="⚠️ 您还没有选择负责的班级，请先选择班级才能使用完整功能"
          type="warning"
          :closable="false"
          show-icon
        >
          <template #default>
            <div style="margin-top: 8px;">
              <el-button type="warning" size="small" @click="$router.push('/teacher/class-management')">
                去选择班级
              </el-button>
              <span style="margin-left: 10px; color: #606266; font-size: 12px;">
                选择班级后，您可以管理学生、布置作业、录入成绩等
              </span>
            </div>
          </template>
        </el-alert>
      </el-col>
    </el-row>

    <!-- 欢迎卡片 -->
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card class="welcome-card teacher-welcome">
          <div class="welcome-content">
            <div>
              <h2>👋 欢迎回来，{{ userInfo?.realName }}老师！</h2>
              <p>今天是 {{ currentDate }}，开始您的教学工作吧</p>
              <div class="teacher-info">
                <span class="info-item">📚 {{ userInfo?.department || '未设置部门' }}</span>
                <span class="info-item">🎓 {{ userInfo?.title || '未设置职称' }}</span>
                <span class="info-item">🏫 {{ userInfo?.schoolName || '未设置学校' }}</span>
              </div>
            </div>
            <div class="quick-actions">
              <el-button
                type="primary"
                icon="Plus"
                @click="$router.push('/teacher/assignment')"
                :disabled="showClassReminder"
                :title="showClassReminder ? '请先选择班级' : '布置作业'"
              >
                布置作业
              </el-button>
              <el-button
                type="success"
                icon="Edit"
                @click="$router.push('/teacher/grade-management')"
                :disabled="showClassReminder"
                :title="showClassReminder ? '请先选择班级' : '录入成绩'"
              >
                录入成绩
              </el-button>
              <el-button
                icon="Bell"
                @click="$router.push('/teacher/announcements')"
                :disabled="showClassReminder"
                :title="showClassReminder ? '请先选择班级' : '发布通知'"
              >
                发布通知
              </el-button>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 核心指标卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6" :xs="24" :sm="12" :md="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #409EFF 0%, #337ecc 100%);">
              <User />
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.studentCount || 0 }}</div>
              <div class="stat-label">我的学生</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6" :xs="24" :sm="12" :md="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #67C23A 0%, #529b2e 100%);">
              <Notebook />
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.activeAssignments || 0 }}</div>
              <div class="stat-label">进行中作业</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6" :xs="24" :sm="12" :md="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #E6A23C 0%, #d8911d 100%);">
              <Document />
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.pendingGrades || 0 }}</div>
              <div class="stat-label">待批改作业</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6" :xs="24" :sm="12" :md="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #F56C6C 0%, #de4444 100%);">
              <Calendar />
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.todayClasses || 0 }}</div>
              <div class="stat-label">今日课程</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 功能快捷入口 -->
    <el-row :gutter="20" class="features-row">
      <el-col :span="12" :xs="24">
        <el-card class="feature-card">
          <template #header>
            <div class="card-header">
              <span>📋 教学管理</span>
            </div>
          </template>
          <div class="feature-grid">
            <div class="feature-item" @click="$router.push('/teacher/my-students')">
              <div class="feature-icon">
                <User />
              </div>
              <div class="feature-info">
                <div class="feature-title">我的学生</div>
                <div class="feature-desc">查看和管理班级学生</div>
              </div>
            </div>

            <div class="feature-item" @click="$router.push('/teacher/grade-management')">
              <div class="feature-icon" style="background: #67C23A;">
                <Document />
              </div>
              <div class="feature-info">
                <div class="feature-title">成绩管理</div>
                <div class="feature-desc">录入和查询学生成绩</div>
              </div>
            </div>

            <div class="feature-item" @click="$router.push('/teacher/assignment')">
              <div class="feature-icon" style="background: #E6A23C;">
                <Notebook />
              </div>
              <div class="feature-info">
                <div class="feature-title">作业布置</div>
                <div class="feature-desc">发布和批改作业</div>
              </div>
            </div>

            <div class="feature-item" @click="$router.push('/teacher/class-management')">
              <div class="feature-icon" style="background: #909399;">
                <School />
              </div>
              <div class="feature-info">
                <div class="feature-title">班级管理</div>
                <div class="feature-desc">管理班级信息和分组</div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="12" :xs="24">
        <el-card class="feature-card">
          <template #header>
            <div class="card-header">
              <span>📢 沟通管理</span>
            </div>
          </template>
          <div class="feature-grid">
            <div class="feature-item" @click="$router.push('/teacher/announcements')">
              <div class="feature-icon" style="background: #409EFF;">
                <Bell />
              </div>
              <div class="feature-info">
                <div class="feature-title">发布通知</div>
                <div class="feature-desc">向学生和家长发送通知</div>
              </div>
            </div>

            <div class="feature-item" @click="$router.push('/teacher/timetable')">
              <div class="feature-icon" style="background: #7232ef;">
                <Calendar />
              </div>
              <div class="feature-info">
                <div class="feature-title">我的课表</div>
                <div class="feature-desc">查看教学安排</div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 最近活动 -->
    <el-row :gutter="20" v-if="!showClassReminder">
      <el-col :span="24">
        <el-card class="activity-card">
          <template #header>
            <div class="card-header">
              <span>🕐 最近活动</span>
              <el-button link @click="viewAllActivities">查看全部</el-button>
            </div>
          </template>
          <div v-if="recentActivities.length > 0" class="activity-list">
            <div v-for="(activity, index) in recentActivities" :key="index" class="activity-item">
              <el-tag :type="activity.type" size="small">{{ activity.category }}</el-tag>
              <span class="activity-title">{{ activity.title }}</span>
              <span class="activity-time">{{ activity.time }}</span>
            </div>
          </div>
          <div v-else class="empty-activity">
            <el-empty description="暂无最近活动" />
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useUserStore } from '@/store/modules/user'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import dayjs from 'dayjs'
import request from '@/utils/request'
import { getMyClasses } from '@/api/teacher'
import {
  User,
  Document,
  Notebook,
  Calendar,
  Bell,
  School
} from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const userInfo = computed(() => userStore.userInfo)

const currentDate = ref(dayjs().format('YYYY年MM月DD日'))
const showClassReminder = ref(false)

const stats = ref({
  studentCount: 0,
  activeAssignments: 0,
  pendingGrades: 0,
  todayClasses: 0
})

const recentActivities = ref([])

// 检查教师是否有班级
const checkTeacherClasses = async () => {
  try {
    const classes = await getMyClasses()
    if (classes.length === 0) {
      showClassReminder.value = true
      // 只显示一次欢迎提示
      const hasSeenWelcome = localStorage.getItem('hasSeenClassWelcome')
      if (!hasSeenWelcome) {
        setTimeout(() => {
          ElMessageBox.confirm(
            '欢迎来到教学平台！您还没有选择负责的班级，是否现在去选择？',
            '👋 欢迎加入',
            {
              confirmButtonText: '去选择班级',
              cancelButtonText: '稍后再说',
              type: 'info'
            }
          ).then(() => {
            localStorage.setItem('hasSeenClassWelcome', 'true')
            router.push('/teacher/class-management')
          }).catch(() => {
            localStorage.setItem('hasSeenClassWelcome', 'true')
          })
        }, 500)
      }
    }
  } catch (error) {
    console.error('检查班级失败:', error)
  }
}

// 从后端加载数据
const loadDashboardData = async () => {
  // 如果没有班级，不加载统计数据
  if (showClassReminder.value) {
    stats.value = {
      studentCount: 0,
      activeAssignments: 0,
      pendingGrades: 0,
      todayClasses: 0
    }
    recentActivities.value = []
    return
  }

  try {
    // 拦截器已经返回了data部分，所以直接使用
    const data = await request({
      url: '/teacher/dashboard',
      method: 'get'
    })

    stats.value = {
      studentCount: data.studentCount || 0,
      activeAssignments: data.activeAssignments || 0,
      pendingGrades: data.pendingGrades || 0,
      todayClasses: data.todayClasses || 0
    }

    // 生成最近活动
    recentActivities.value = [
      {
        category: '作业',
        title: `已发布 ${data.totalAssignments || 0} 个作业`,
        time: '最近',
        type: 'success'
      },
      {
        category: '教学',
        title: `进行中作业: ${data.activeAssignments || 0} 个`,
        time: '最近',
        type: 'warning'
      },
      {
        category: '学生',
        title: `管理 ${data.studentCount || 0} 名学生`,
        time: '最近',
        type: 'info'
      }
    ]
  } catch (error) {
    console.error('加载仪表盘数据失败:', error)
    // 使用默认数据
    stats.value = {
      studentCount: 0,
      activeAssignments: 0,
      pendingGrades: 0,
      todayClasses: 0
    }
    recentActivities.value = []
  }
}

// 加载数据
onMounted(async () => {
  // 先检查班级状态
  await checkTeacherClasses()
  // 再根据班级状态加载数据
  await loadDashboardData()
})

const viewAllActivities = () => {
  // 跳转到活动日志页面
  ElMessage.info('活动日志功能开发中...')
}
</script>

<style scoped>
.teacher-dashboard {
  width: 100%;
}

.welcome-card {
  margin-bottom: 20px;
}

.teacher-welcome {
  background: linear-gradient(135deg, #1e3a5f 0%, #2d5a8f 100%);
  color: white;
  border: 1px solid rgba(96, 165, 250, 0.3);
  border-radius: 12px;
}

.teacher-welcome h2 {
  color: white;
  font-weight: 700;
}

.teacher-welcome p {
  color: #c0d4f0;
}

.welcome-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 20px;
}

.welcome-content h2 {
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 700;
}

.welcome-content p {
  margin: 0 0 10px 0;
  font-size: 14px;
}

.teacher-info {
  display: flex;
  gap: 15px;
  flex-wrap: wrap;
}

.info-item {
  background: rgba(255, 255, 255, 0.15);
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.quick-actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.quick-actions :deep(.el-button--primary) {
  background: linear-gradient(135deg, #60a5fa 0%, #3b82f6 100%);
  border: none;
  border-radius: 8px;
  font-weight: 600;
}

.quick-actions :deep(.el-button--success) {
  background: linear-gradient(135deg, #34d399 0%, #10b981 100%);
  border: none;
  border-radius: 8px;
  font-weight: 600;
}

.quick-actions :deep(.el-button) {
  border-radius: 8px;
  font-weight: 500;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  margin-bottom: 20px;
  min-height: 120px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.95);
  border: 1px solid rgba(30, 58, 95, 0.15);
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(30, 58, 95, 0.2);
  border-color: rgba(30, 58, 95, 0.3);
}

.stat-value {
  color: #1e3a5f;
  font-weight: 700;
}

.stat-label {
  color: #64748b;
}

.features-row {
  margin-bottom: 20px;
}

.feature-card {
  margin-bottom: 20px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 10px;
  border: 1px solid rgba(30, 58, 95, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
  font-size: 16px;
  color: #1e3a5f;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border: 1px solid rgba(30, 58, 95, 0.1);
  background: rgba(255, 255, 255, 0.7);
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.feature-item:hover {
  background: linear-gradient(135deg, #e8f4ff 0%, #f0f7ff 100%);
  border-color: #1e3a5f;
  transform: translateX(4px);
  box-shadow: 0 4px 12px rgba(30, 58, 95, 0.15);
}

.feature-title {
  color: #1e3a5f;
  font-weight: 600;
}

.feature-desc {
  color: #64748b;
}

.activity-card {
  margin-bottom: 20px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 10px;
  border: 1px solid rgba(30, 58, 95, 0.1);
}

.activity-item {
  border-bottom: 1px solid rgba(30, 58, 95, 0.08);
}

.activity-title {
  color: #1e3a5f;
}

.activity-time {
  color: #64748b;
}

.welcome-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 20px;
}

.welcome-content h2 {
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 600;
}

.welcome-content p {
  margin: 0 0 10px 0;
  font-size: 14px;
}

.teacher-info {
  display: flex;
  gap: 15px;
  flex-wrap: wrap;
}

.info-item {
  background: rgba(255, 255, 255, 0.15);
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  backdrop-filter: blur(10px);
}

.quick-actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  margin-bottom: 20px;
  min-height: 120px;
  border-radius: 12px;
  border: none;
}

.stat-card:hover {
  transform: translateY(-2px);
  transition: all 0.3s;
}

.stat-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.stat-info {
  text-align: right;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #303133;
  line-height: 1;
}

.stat-label {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}

.features-row {
  margin-bottom: 20px;
}

.feature-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
  font-size: 16px;
}

.feature-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.feature-item:hover {
  background: #f5f7fa;
  border-color: #409EFF;
  transform: translateX(4px);
}

.feature-icon {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  background: #409EFF;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 18px;
  flex-shrink: 0;
}

.feature-info {
  flex: 1;
}

.feature-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 2px;
}

.feature-desc {
  font-size: 12px;
  color: #909399;
}

.activity-card {
  margin-bottom: 20px;
}

.activity-list {
  padding: 10px 0;
}

.activity-item {
  display: flex;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #ebeef5;
  gap: 10px;
}

.activity-item:last-child {
  border-bottom: none;
}

.activity-title {
  flex: 1;
  font-size: 14px;
  color: #303133;
}

.activity-time {
  font-size: 12px;
  color: #909399;
}

.empty-activity {
  padding: 20px 0;
}
</style>
