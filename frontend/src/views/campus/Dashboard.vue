<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <!-- 欢迎卡片 -->
      <el-col :span="24">
        <el-card class="welcome-card">
          <div class="welcome-content">
            <div>
              <h2>欢迎回来，{{ userInfo?.realName }}！</h2>
              <p>今天是 {{ currentDate }}，祝您有美好的一天</p>
            </div>
            <div class="quick-actions">
              <el-button type="primary" @click="$router.push('/profile')">个人信息</el-button>
              <el-button @click="$router.push('/timetable')">课程表</el-button>
              <el-button @click="$router.push('/grades')">成绩查询</el-button>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 统计卡片 -->
      <el-col :span="6" :xs="24" :sm="12" :md="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background: #409EFF;">
              <Bell />
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ dashboard.unreadCount || 0 }}</div>
              <div class="stat-label">未读通知</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6" :xs="24" :sm="12" :md="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background: #67C23A;">
              <Document />
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ dashboard.homeworkCompleted || 0 }}</div>
              <div class="stat-label">作业完成</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6" :xs="24" :sm="12" :md="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background: #E6A23C;">
              <Trophy />
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ dashboard.rewardsEarned || 0 }}</div>
              <div class="stat-label">获得奖励</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6" :xs="24" :sm="12" :md="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background: #F56C6C;">
              <Clock />
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ dashboard.pendingTodos || 0 }}</div>
              <div class="stat-label">待办事项</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 快捷链接 -->
      <el-col :span="12" :xs="24">
        <el-card class="quick-links-card">
          <template #header>
            <div class="card-header">
              <span>快捷链接</span>
            </div>
          </template>
          <div class="quick-links-grid">
            <div
              v-for="link in dashboard.quickLinks"
              :key="link.path"
              class="quick-link-item"
              @click="$router.push(link.path)"
            >
              <el-icon><Link /></el-icon>
              <span>{{ link.name }}</span>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 最近通知 -->
      <el-col :span="12" :xs="24">
        <el-card class="notice-card">
          <template #header>
            <div class="card-header">
              <span>最近通知</span>
              <el-button link @click="$router.push('/announcements')">查看全部</el-button>
            </div>
          </template>
          <div v-if="dashboard.recentNotifications && dashboard.recentNotifications.length > 0" class="notice-list">
            <div v-for="(notification, index) in dashboard.recentNotifications" :key="index" class="notice-item">
              <el-tag type="info" size="small">{{ notification.type }}</el-tag>
              <span class="notice-title">{{ notification.title }}</span>
              <span class="notice-time">{{ notification.time }}</span>
            </div>
          </div>
          <div v-else class="empty-notice">
            <el-empty description="暂无新通知" />
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useUserStore } from '@/store/modules/user'
import { getDashboard } from '@/api/campus'
import dayjs from 'dayjs'
import {
  Bell,
  Document,
  Trophy,
  Clock,
  Link
} from '@element-plus/icons-vue'

const userStore = useUserStore()
const userInfo = computed(() => userStore.userInfo)

const currentDate = ref(dayjs().format('YYYY年MM月DD日'))
const dashboard = ref({
  unreadCount: 0,
  homeworkCompleted: 0,
  rewardsEarned: 0,
  pendingTodos: 0,
  quickLinks: [],
  recentNotifications: []
})

onMounted(async () => {
  try {
    const data = await getDashboard()
    dashboard.value = data

    // 如果后端返回了realName，更新用户store中的信息
    if (data.realName && userStore.userInfo) {
      userStore.userInfo.realName = data.realName
    }
  } catch (error) {
    console.error('获取仪表盘数据失败:', error)
  }
})
</script>

<style scoped>
.dashboard {
  width: 100%;
}

.welcome-card {
  margin-bottom: 20px;
  background: linear-gradient(135deg, #e8f4ff 0%, #f0f7ff 100%);
  border: 1px solid rgba(64, 158, 255, 0.2);
  border-radius: 12px;
}

.welcome-card :deep(.el-card__body) {
  background: transparent;
}

.welcome-content h2 {
  color: #1e88e5;
  font-weight: 700;
}

.welcome-content p {
  color: #5a7a9a;
}

.quick-actions :deep(.el-button--primary) {
  background: linear-gradient(135deg, #409EFF 0%, #337ecc 100%);
  border: none;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
}

.quick-actions :deep(.el-button) {
  border-radius: 8px;
  font-weight: 500;
}

.stat-card {
  margin-bottom: 20px;
  min-height: 120px;
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid rgba(64, 158, 255, 0.15);
  border-radius: 10px;
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(64, 158, 255, 0.15);
  border-color: rgba(64, 158, 255, 0.3);
}

.stat-value {
  color: #1e88e5;
  font-weight: 700;
}

.quick-links-card,
.notice-card {
  margin-bottom: 20px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 10px;
  border: 1px solid rgba(0, 0, 0, 0.05);
}

.quick-link-item {
  border: 1px solid rgba(64, 158, 255, 0.2);
  background: rgba(255, 255, 255, 0.8);
  border-radius: 8px;
}

.quick-link-item:hover {
  background: linear-gradient(135deg, #e8f4ff 0%, #f0f7ff 100%);
  border-color: #409EFF;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.2);
}

.quick-link-item .el-icon {
  color: #409EFF;
}

.quick-link-item span {
  color: #5a7a9a;
  font-weight: 500;
}

.welcome-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 20px;
}

.welcome-content h2 {
  margin: 0 0 5px 0;
  color: #303133;
  font-size: 24px;
}

.welcome-content p {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.quick-actions {
  display: flex;
  gap: 10px;
}

.stat-card {
  margin-bottom: 20px;
  min-height: 120px;
}

.stat-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
}

.stat-info {
  text-align: right;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  line-height: 1;
}

.stat-label {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}

.quick-links-card,
.notice-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
}

.quick-links-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  gap: 15px;
}

.quick-link-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 15px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  gap: 8px;
}

.quick-link-item:hover {
  background: #f5f7fa;
  border-color: #409EFF;
  transform: translateY(-2px);
}

.quick-link-item .el-icon {
  font-size: 20px;
  color: #409EFF;
}

.quick-link-item span {
  font-size: 13px;
  color: #606266;
}

.notice-item {
  display: flex;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #ebeef5;
  gap: 10px;
}

.notice-item:last-child {
  border-bottom: none;
}

.notice-title {
  flex: 1;
  font-size: 14px;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.notice-time {
  font-size: 12px;
  color: #909399;
}

.empty-notice {
  padding: 20px 0;
}
</style>
