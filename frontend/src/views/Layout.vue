<template>
  <div class="layout-container">
    <el-container class="main-container">
      <!-- 侧边栏 -->
      <el-aside :width="isCollapse ? '64px' : '200px'" class="sidebar">
        <div class="logo">
          <h3 v-if="!isCollapse">教育局平台</h3>
          <span v-else>教</span>
        </div>
        <el-menu
          :default-active="$route.path"
          class="el-menu-vertical"
          :collapse="isCollapse"
          router
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409EFF"
        >
          <el-menu-item index="/campus">
            <el-icon><HomeFilled /></el-icon>
            <template #title>仪表盘</template>
          </el-menu-item>
          <el-menu-item index="/campus/profile">
            <el-icon><User /></el-icon>
            <template #title>个人信息</template>
          </el-menu-item>
          <el-menu-item index="/campus/class">
            <el-icon><School /></el-icon>
            <template #title>班级信息</template>
          </el-menu-item>
          <el-menu-item index="/campus/timetable">
            <el-icon><Calendar /></el-icon>
            <template #title>课程表</template>
          </el-menu-item>
          <el-menu-item index="/campus/grades">
            <el-icon><Document /></el-icon>
            <template #title>成绩查询</template>
          </el-menu-item>
          <el-menu-item index="/campus/announcements">
            <el-icon><Bell /></el-icon>
            <template #title>通知公告</template>
          </el-menu-item>
          <el-menu-item index="/campus/status-change">
            <el-icon><Document /></el-icon>
            <template #title>学籍异动</template>
          </el-menu-item>
          <el-menu-item index="/campus/resources">
            <el-icon><Collection /></el-icon>
            <template #title>学习资源</template>
          </el-menu-item>
          <el-menu-item index="/campus/training">
            <el-icon><Notebook /></el-icon>
            <template #title>培训课程</template>
          </el-menu-item>
          <el-menu-item index="/campus/my-training">
            <el-icon><Finished /></el-icon>
            <template #title>我的培训</template>
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
  School,
  Calendar,
  Document,
  Bell,
  Collection,
  Notebook,
  Finished
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
    router.push('/campus/profile')
  } else if (command === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      userStore.logout()
      ElMessage.success('已退出登录')
      router.push('/')  // 退出后返回门户首页
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
  // 注意：管理员和教师也可以访问前台页面，不需要角色检查
})
</script>

<style scoped>
.layout-container {
  height: 100vh;
  background: linear-gradient(135deg, #f8fafc 0%, #eef2f7 100%);
}

.main-container {
  height: 100%;
}

.sidebar {
  background: linear-gradient(180deg, #2c3e50 0%, #34495e 100%);
  overflow: hidden;
  transition: width 0.3s;
  border-right: 1px solid rgba(255, 255, 255, 0.05);
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ffffff;
  font-size: 18px;
  font-weight: 600;
  background: linear-gradient(135deg, #2c3e50 0%, #34495e 100%);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  letter-spacing: 0.5px;
}

.el-menu-vertical {
  border-right: none;
  height: calc(100% - 60px);
}

.el-menu-vertical .el-menu-item {
  color: #d1dbe5;
  border-left: 3px solid transparent;
  transition: all 0.2s ease;
}

.el-menu-vertical .el-menu-item:hover {
  background-color: #3d5266 !important;
  color: #ffffff;
}

.el-menu-vertical .el-menu-item.is-active {
  background: linear-gradient(90deg, #3d5266 0%, #4a5f75 100%) !important;
  color: #ffffff;
  border-left-color: #409EFF;
}

.header {
  background: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.header-left {
  display: flex;
  align-items: center;
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
  background: linear-gradient(135deg, #ffffff 0%, #f8fafc 100%);
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
