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
}

.main-container {
  height: 100%;
}

.sidebar {
  background-color: #1e3a5f;
  overflow: hidden;
  transition: width 0.3s;
}

.teacher-sidebar .logo {
  background: linear-gradient(135deg, #1e3a5f 0%, #2d5a8f 100%);
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 18px;
  border-bottom: 1px solid #2c3e50;
}

.el-menu-vertical {
  border-right: none;
  height: calc(100% - 60px);
}

.el-menu-vertical .el-menu-item {
  border-left: 3px solid transparent;
}

.el-menu-vertical .el-menu-item.is-active {
  background-color: #2d5a8f !important;
  border-left-color: #409EFF;
}

.header {
  background-color: white;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.role-badge {
  background: linear-gradient(135deg, #409EFF 0%, #337ecc 100%);
  color: white;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 15px;
}

.username {
  font-size: 14px;
  color: #606266;
}

.main-content {
  background-color: #f5f7fa;
  padding: 20px;
}
</style>
