<template>
  <div class="layout-container">
    <el-container class="main-container">
      <!-- 侧边栏 - 教师专属 -->
      <el-aside :width="isCollapse ? '64px' : '200px'" class="sidebar teacher-sidebar">
        <div class="logo">
          <h3 v-if="!isCollapse">教师工作台</h3>
          <span v-else>师</span>
        </div>
        <el-menu
          :default-active="$route.path"
          class="el-menu-vertical"
          :collapse="isCollapse"
          router
          background-color="#1e3a5f"
          text-color="#bfcbd9"
          active-text-color="#409EFF"
        >
          <el-menu-item index="/teacher">
            <el-icon><HomeFilled /></el-icon>
            <template #title>教学仪表盘</template>
          </el-menu-item>

          <el-menu-item index="/teacher/profile">
            <el-icon><User /></el-icon>
            <template #title>个人信息</template>
          </el-menu-item>

          <el-menu-item index="/teacher/my-students">
            <el-icon><User /></el-icon>
            <template #title>我的学生</template>
          </el-menu-item>

          <el-menu-item index="/teacher/grade-management">
            <el-icon><Document /></el-icon>
            <template #title>成绩管理</template>
          </el-menu-item>

          <el-menu-item index="/teacher/assignment">
            <el-icon><Notebook /></el-icon>
            <template #title>作业布置</template>
          </el-menu-item>

          <el-menu-item index="/teacher/timetable">
            <el-icon><Calendar /></el-icon>
            <template #title>我的课表</template>
          </el-menu-item>

          <el-menu-item index="/teacher/announcements">
            <el-icon><Bell /></el-icon>
            <template #title>发布通知</template>
          </el-menu-item>

          <el-menu-item index="/teacher/class-management">
            <el-icon><School /></el-icon>
            <template #title>班级管理</template>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <el-container>
        <!-- 顶部栏 -->
        <el-header class="header">
          <div class="header-left">
            <el-button circle @click="toggleCollapse">
              <el-icon><Menu /></el-icon>
            </el-button>
            <span class="role-badge">教师</span>
          </div>
          <div class="header-right">
            <span class="username">{{ userInfo?.realName }}</span>
            <el-dropdown @command="handleCommand">
              <span class="el-dropdown-link">
                <el-avatar :size="32" :src="userInfo?.avatar || ''">
                  {{ userInfo?.realName?.charAt(0) }}
                </el-avatar>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                  <el-dropdown-item command="changePassword">修改密码</el-dropdown-item>
                  <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-header>

        <!-- 内容区域 -->
        <el-main class="main-content">
          <router-view v-slot="{ Component }">
            <component :is="Component" />
          </router-view>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/store/modules/user'
import { useAppStore } from '@/store/modules/app'
import {
  Menu,
  HomeFilled,
  User,
  Document,
  Calendar,
  Bell,
  School,
  Notebook
} from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const appStore = useAppStore()

const isCollapse = computed(() => appStore.sidebarCollapsed)
const userInfo = computed(() => userStore.userInfo)

const toggleCollapse = () => {
  appStore.toggleSidebar()
}

const handleCommand = (command) => {
  if (command === 'profile') {
    router.push('/teacher/profile')
  } else if (command === 'changePassword') {
    // 在个人信息页面已经有修改密码功能
    router.push('/teacher/profile')
    ElMessage.info('请在个人信息页面修改密码')
  } else if (command === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      userStore.logout()
      ElMessage.success('已退出登录')
      router.push('/login')
    }).catch(() => {})
  }
}

onMounted(() => {
  if (!userStore.isLogin) {
    router.push('/login')
  } else if (!userStore.userInfo) {
    userStore.fetchUserInfo().catch(() => {
      userStore.logout()
      router.push('/login')
    })
  }
})
</script>

<style scoped>
.layout-container {
  height: 100vh;
  background: linear-gradient(135deg, #0f172a 0%, #1e293b 100%);
}

.main-container {
  height: 100%;
}

.sidebar {
  background: linear-gradient(180deg, #1e3a5f 0%, #1a2942 100%);
  overflow: hidden;
  transition: width 0.3s;
  border-right: 1px solid rgba(255, 255, 255, 0.05);
}

.teacher-sidebar .logo {
  background: linear-gradient(135deg, #1e3a5f 0%, #2d5a8f 100%);
  color: #e8f0ff;
  font-weight: 600;
  letter-spacing: 0.5px;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.el-menu-vertical {
  border-right: none;
  height: calc(100% - 60px);
}

.el-menu-vertical .el-menu-item {
  color: #cbd5e1;
  border-left: 3px solid transparent;
  transition: all 0.2s ease;
}

.el-menu-vertical .el-menu-item:hover {
  background-color: #2d5a8f !important;
  color: #ffffff;
}

.el-menu-vertical .el-menu-item.is-active {
  background: linear-gradient(90deg, #2d5a8f 0%, #3a6ba8 100%) !important;
  color: #ffffff;
  border-left-color: #60a5fa;
}

.header {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(0, 0, 0, 0.08);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.role-badge {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  color: white;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.3);
}

.header-right {
  display: flex;
  align-items: center;
  gap: 15px;
}

.username {
  font-size: 14px;
  color: #475569;
  font-weight: 500;
}

.main-content {
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  padding: 20px;
  overflow-y: auto;
}

.main-content::-webkit-scrollbar {
  width: 6px;
}

.main-content::-webkit-scrollbar-track {
  background: transparent;
}

.main-content::-webkit-scrollbar-thumb {
  background: #94a3b8;
  border-radius: 3px;
}

.main-content::-webkit-scrollbar-thumb:hover {
  background: #64748b;
}
</style>
