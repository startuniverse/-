<template>
  <div class="admin-dashboard">
    <el-row :gutter="20">
      <!-- 系统统计 -->
      <el-col :span="24">
        <el-card class="welcome-card">
          <div class="welcome-content">
            <div>
              <h2>管理仪表盘</h2>
              <p>系统运行状态概览</p>
            </div>
            <div class="stats">
              <div class="stat-item">
                <div class="stat-number">{{ stats.schoolCount || 0 }}</div>
                <div class="stat-label">学校总数</div>
              </div>
              <div class="stat-item">
                <div class="stat-number">{{ stats.userCount || 0 }}</div>
                <div class="stat-label">用户总数</div>
              </div>
              <div class="stat-item">
                <div class="stat-number">{{ stats.version }}</div>
                <div class="stat-label">系统版本</div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 快捷操作 -->
      <el-col :span="12" :xs="24">
        <el-card class="action-card">
          <template #header>
            <div class="card-header">
              <span>快捷操作</span>
            </div>
          </template>
          <div class="action-grid">
            <div class="action-item" @click="$router.push('/admin/students')">
              <el-icon><User /></el-icon>
              <span>学生管理</span>
            </div>
            <div class="action-item" @click="$router.push('/admin/documents')">
              <el-icon><Document /></el-icon>
              <span>文档管理</span>
            </div>
            <div class="action-item" @click="$router.push('/admin/resources')">
              <el-icon><Folder /></el-icon>
              <span>资源管理</span>
            </div>
            <div class="action-item" @click="$router.push('/admin/analytics')">
              <el-icon><DataAnalysis /></el-icon>
              <span>数据分析</span>
            </div>
            <div class="action-item" @click="$router.push('/admin/teacher-info')">
              <el-icon><UserFilled /></el-icon>
              <span>教师信息</span>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 系统信息 -->
      <el-col :span="12" :xs="24">
        <el-card class="info-card">
          <template #header>
            <div class="card-header">
              <span>系统信息</span>
            </div>
          </template>
          <div class="info-list">
            <div class="info-item">
              <span class="label">系统名称:</span>
              <span class="value">{{ stats.systemName }}</span>
            </div>
            <div class="info-item">
              <span class="label">运行时间:</span>
              <span class="value">{{ uptime }}</span>
            </div>
            <div class="info-item">
              <span class="label">最后备份:</span>
              <span class="value">{{ stats.lastBackup }}</span>
            </div>
            <div class="info-item">
              <span class="label">数据库:</span>
              <span class="value">MySQL 8.0</span>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 最近活动 -->
      <el-col :span="24">
        <el-card class="activity-card">
          <template #header>
            <div class="card-header">
              <span>最近活动</span>
            </div>
          </template>
          <el-timeline>
            <el-timeline-item
              timestamp="2025-01-01 10:00"
              type="primary"
            >
              系统初始化完成
            </el-timeline-item>
            <el-timeline-item
              timestamp="2025-01-01 09:30"
              type="success"
            >
              数据库连接成功
            </el-timeline-item>
            <el-timeline-item
              timestamp="2025-01-01 09:00"
              type="info"
            >
              服务启动
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getAdminDashboard } from '@/api/admin'
import { User, Document, Folder, DataAnalysis, UserFilled } from '@element-plus/icons-vue'

const stats = ref({
  systemName: '城市教育局综合信息服务平台',
  version: '1.0.0',
  schoolCount: 0,
  userCount: 0,
  lastBackup: ''
})

const uptime = ref('')

const loadDashboard = async () => {
  try {
    const data = await getAdminDashboard()
    stats.value = data
    updateUptime()
  } catch (error) {
    console.error('加载仪表盘数据失败:', error)
  }
}

const updateUptime = () => {
  const startTime = new Date('2025-01-01T09:00:00')
  const now = new Date()
  const diff = now - startTime
  const hours = Math.floor(diff / (1000 * 60 * 60))
  const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60))
  uptime.value = `${hours}小时${minutes}分钟`
}

onMounted(() => {
  loadDashboard()
})
</script>

<style scoped>
.admin-dashboard {
  width: 100%;
}

.welcome-card {
  margin-bottom: 20px;
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

.stats {
  display: flex;
  gap: 30px;
}

.stat-item {
  text-align: center;
}

.stat-number {
  font-size: 28px;
  font-weight: bold;
  color: #409EFF;
  line-height: 1;
}

.stat-label {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}

.action-card,
.info-card,
.activity-card {
  margin-bottom: 20px;
}

.card-header {
  font-weight: 600;
}

.action-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  gap: 8px;
}

.action-item:hover {
  background: #f5f7fa;
  border-color: #409EFF;
  transform: translateY(-2px);
}

.action-item .el-icon {
  font-size: 24px;
  color: #409EFF;
}

.action-item span {
  font-size: 14px;
  color: #303133;
  font-weight: 500;
}

.info-list {
  padding: 10px 0;
}

.info-item {
  display: flex;
  justify-content: space-between;
  padding: 12px 0;
  border-bottom: 1px solid #f5f5f5;
}

.info-item:last-child {
  border-bottom: none;
}

.label {
  color: #909399;
  font-size: 14px;
}

.value {
  color: #303133;
  font-size: 14px;
  font-weight: 500;
}
</style>
