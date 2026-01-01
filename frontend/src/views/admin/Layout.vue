<template>
  <div class="admin-layout">
    <el-container class="main-container">
      <!-- 侧边栏 -->
      <el-aside :width="isCollapse ? '64px' : '220px'" class="sidebar">
        <div class="logo">
          <h3 v-if="!isCollapse">管理后台</h3>
          <span v-else>管</span>
        </div>
        <el-menu
          :default-active="$route.path"
          class="el-menu-vertical"
          :collapse="isCollapse"
          router
          background-color="#2c3e50"
          text-color="#bfcbd9"
          active-text-color="#409EFF"
        >
          <el-menu-item index="/admin/dashboard">
            <el-icon><Monitor /></el-icon>
            <template #title>管理仪表盘</template>
          </el-menu-item>
          <el-menu-item index="/admin/students">
            <el-icon><User /></el-icon>
            <template #title>学生管理</template>
          </el-menu-item>
          <el-menu-item index="/admin/documents">
            <el-icon><Document /></el-icon>
            <template #title>文档管理</template>
          </el-menu-item>
          <el-menu-item index="/admin/resources">
            <el-icon><Folder /></el-icon>
            <template #title>资源管理</template>
          </el-menu-item>
          <el-menu-item index="/admin/analytics">
            <el-icon><DataAnalysis /></el-icon>
            <template #title>数据分析</template>
          </el-menu-item>
          <el-menu-item index="/admin/assets">
            <el-icon><Box /></el-icon>
            <template #title>资产管理</template>
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
            <span class="title">教育局管理后台</span>
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
                  <el-dropdown-item command="home">返回前台</el-dropdown-item>
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
  Monitor,
  User,
  Document,
  Folder,
  DataAnalysis,
  Box
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
  if (command === 'home') {
    router.push('/')
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
  } else if (!userStore.hasRole('ADMIN')) {
    ElMessage.error('您没有访问此页面的权限')
    router.push('/')
  } else if (!userStore.userInfo) {
    userStore.fetchUserInfo().catch(() => {
      userStore.logout()
      router.push('/login')
    })
  }
})
</script>

<style scoped>
.admin-layout {
  height: 100vh;
  background: linear-gradient(135deg, #1a1d29 0%, #12151f 100%);
}

.main-container {
  height: 100%;
}

.sidebar {
  background: linear-gradient(180deg, #1e2330 0%, #1a1d29 100%);
  overflow: hidden;
  transition: width 0.3s;
  border-right: 1px solid rgba(255, 255, 255, 0.05);
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #e4e7ed;
  font-size: 18px;
  font-weight: 600;
  background: linear-gradient(135deg, #1e2330 0%, #252a3a 100%);
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
  letter-spacing: 0.5px;
}

.el-menu-vertical {
  border-right: none;
  height: calc(100% - 60px);
}

.el-menu-vertical .el-menu-item {
  color: #b8c5d8;
  border-left: 3px solid transparent;
  transition: all 0.2s ease;
}

.el-menu-vertical .el-menu-item:hover {
  background-color: #2a2f40 !important;
  color: #ffffff;
}

.el-menu-vertical .el-menu-item.is-active {
  background: linear-gradient(90deg, #2a2f40 0%, #343a50 100%) !important;
  color: #ffffff;
  border-left-color: #409EFF;
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
  gap: 15px;
}

.title {
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
  letter-spacing: 0.3px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 15px;
}

.username {
  font-size: 14px;
  color: #606266;
  font-weight: 500;
}

.main-content {
  background: linear-gradient(135deg, #f8f9fc 0%, #f5f7fa 100%);
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
  background: #c0c4cc;
  border-radius: 3px;
}

.main-content::-webkit-scrollbar-thumb:hover {
  background: #909399;
}
</style>
