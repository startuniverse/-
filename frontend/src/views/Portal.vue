<template>
  <div class="portal-container">
    <!-- 顶部导航栏 -->
    <header class="portal-header">
      <div class="header-content">
        <div class="logo-section">
          <h1 class="site-title">城市教育局综合信息平台</h1>
        </div>
        <div class="header-actions">
          <el-button type="primary" @click="goToLogin">登录</el-button>
          <el-button @click="goToRegister">注册</el-button>
        </div>
      </div>
    </header>

    <!-- 主要内容区域 -->
    <main class="portal-main">
      <!-- 欢迎横幅 -->
      <div class="hero-banner">
        <div class="hero-content">
          <h2>欢迎访问教育局综合信息平台</h2>
          <p>为您提供最新的教育资讯、通知公告和便捷的在线服务</p>
        </div>
      </div>

      <!-- 滚动播放栏目 -->
      <div class="carousel-section">
        <el-card class="carousel-card">
          <template #header>
            <div class="card-header">
              <span class="section-title">📊 信息展示</span>
            </div>
          </template>
          <div class="carousel-container">
            <!-- 只显示当前图片 -->
            <template v-for="(image, index) in carouselImages" :key="index">
              <div
                v-show="currentIndex === index"
                class="carousel-item"
              >
                <img :src="image.src" :alt="image.alt" />
                <div class="carousel-caption">{{ image.caption }}</div>
              </div>
            </template>
            <!-- 控制按钮 -->
            <div class="carousel-controls">
              <button class="control-btn prev" @click="prevSlide">‹</button>
              <button class="control-btn next" @click="nextSlide">›</button>
            </div>
            <!-- 指示器 -->
            <div class="carousel-indicators">
              <span
                v-for="(_, idx) in carouselImages"
                :key="idx"
                class="indicator"
                :class="{ active: currentIndex === idx }"
                @click="goToSlide(idx)"
              ></span>
            </div>
          </div>
        </el-card>
      </div>

      <div class="content-grid">
        <!-- 左侧：最新公告 -->
        <div class="left-column">
          <el-card class="section-card">
            <template #header>
              <div class="card-header">
                <span class="section-title">📢 最新公告</span>
                <el-button link @click="goToLogin">查看全部</el-button>
              </div>
            </template>
            <div class="announcement-list">
              <div
                v-for="item in announcements"
                :key="item.id"
                class="announcement-item"
                @click="viewAnnouncement(item)"
              >
                <span class="announcement-tag" :class="item.type">{{ getTypeText(item.type) }}</span>
                <span class="announcement-title">{{ item.title }}</span>
                <span class="announcement-date">{{ item.publishTime }}</span>
              </div>
              <div v-if="announcements.length === 0" class="empty-state">
                <el-empty description="暂无公告" />
              </div>
            </div>
          </el-card>

          <!-- 要闻动态 -->
          <el-card class="section-card">
            <template #header>
              <div class="card-header">
                <span class="section-title">📰 要闻动态</span>
              </div>
            </template>
            <div class="news-list">
              <div v-for="news in newsList" :key="news.id" class="news-item">
                <div class="news-content">
                  <h4>{{ news.title }}</h4>
                  <p>{{ news.summary }}</p>
                </div>
                <span class="news-date">{{ news.date }}</span>
              </div>
            </div>
          </el-card>
        </div>

        <!-- 右侧：服务栏目 -->
        <div class="right-column">
          <el-card class="section-card">
            <template #header>
              <div class="card-header">
                <span class="section-title">🎯 服务栏目</span>
              </div>
            </template>
            <div class="service-grid">
              <div
                v-for="service in services"
                :key="service.id"
                class="service-item"
                @click="handleServiceClick(service)"
              >
                <el-icon class="service-icon"><component :is="service.icon" /></el-icon>
                <span class="service-name">{{ service.name }}</span>
              </div>
            </div>
          </el-card>

          <!-- 快速入口 -->
          <el-card class="section-card">
            <template #header>
              <div class="card-header">
                <span class="section-title">⚡ 快速入口</span>
              </div>
            </template>
            <div class="quick-links">
              <el-button
                v-for="link in quickLinks"
                :key="link.path"
                class="quick-link-btn"
                @click="handleQuickLink(link)"
              >
                {{ link.name }}
              </el-button>
            </div>
          </el-card>

          <!-- 联系信息 -->
          <el-card class="section-card">
            <template #header>
              <div class="card-header">
                <span class="section-title">📞 联系我们</span>
              </div>
            </template>
            <div class="contact-info">
              <div class="contact-item">
                <el-icon><Phone /></el-icon>
                <span>咨询电话: 010-12345678</span>
              </div>
              <div class="contact-item">
                <el-icon><Clock /></el-icon>
                <span>工作时间: 周一至周五 9:00-17:00</span>
              </div>
              <div class="contact-item">
                <el-icon><Location /></el-icon>
                <span>地址: 城市教育局办公大楼</span>
              </div>
            </div>
          </el-card>
        </div>
      </div>
    </main>

    <!-- 页脚 -->
    <footer class="portal-footer">
      <div class="footer-content">
        <p>© 2025 城市教育局综合信息平台 - All Rights Reserved</p>
        <p>技术支持: 教育局信息中心</p>
      </div>
    </footer>

    <!-- 公告详情对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="selectedAnnouncement?.title"
      width="600px"
    >
      <div class="announcement-detail">
        <div class="detail-meta">
          <span>发布者: {{ selectedAnnouncement?.publisherName || '教育局' }}</span>
          <span>发布时间: {{ selectedAnnouncement?.publishTime }}</span>
        </div>
        <div class="detail-content">
          {{ selectedAnnouncement?.content }}
        </div>
      </div>
      <template #footer>
        <el-button type="primary" @click="goToLogin">登录查看详情</el-button>
        <el-button @click="dialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  Document,
  Bell,
  School,
  User,
  Calendar,
  Trophy,
  Phone,
  Clock,
  Location
} from '@element-plus/icons-vue'

const router = useRouter()
const dialogVisible = ref(false)
const selectedAnnouncement = ref(null)

// 轮播图数据 - 使用 public 目录下的图片
const carouselImages = ref([
  {
    src: '/images/1.jpg',
    alt: '教育活动1',
    caption: '教育信息化建设成果展示'
  },
  {
    src: '/images/2.png',
    alt: '教育活动2',
    caption: '智慧校园平台升级'
  },
  {
    src: '/images/3.png',
    alt: '教育活动3',
    caption: '教学质量提升工程'
  },
  {
    src: '/images/4.jpg',
    alt: '教育活动4',
    caption: '教师专业发展培训'
  },
  {
    src: '/images/5.jpg',
    alt: '教育活动5',
    caption: '校园安全管理工作'
  }
])

// 当前轮播索引
const currentIndex = ref(0)
const autoPlayInterval = ref(null)

// 上一张
const prevSlide = () => {
  currentIndex.value = currentIndex.value === 0 ? carouselImages.value.length - 1 : currentIndex.value - 1
  resetAutoPlay()
}

// 下一张
const nextSlide = () => {
  currentIndex.value = (currentIndex.value + 1) % carouselImages.value.length
  resetAutoPlay()
}

// 跳转到指定幻灯片
const goToSlide = (index) => {
  currentIndex.value = index
  resetAutoPlay()
}

// 自动播放
const startAutoPlay = () => {
  autoPlayInterval.value = setInterval(() => {
    nextSlide()
  }, 3000) // 3秒切换一次
}

const resetAutoPlay = () => {
  if (autoPlayInterval.value) {
    clearInterval(autoPlayInterval.value)
    startAutoPlay()
  }
}

onMounted(() => {
  startAutoPlay()
})

onUnmounted(() => {
  if (autoPlayInterval.value) {
    clearInterval(autoPlayInterval.value)
  }
})

// 模拟公告数据 - 门户首页显示的公共公告
const announcements = ref([
  {
    id: 1,
    type: 'announcement',
    title: '关于2025年春季学期开学安排的通知',
    content: '根据市教育局统一安排，2025年春季学期将于2月24日正式开学。请各学校做好开学准备工作，确保教学秩序正常进行。',
    publishTime: '2025-02-20'
  },
  {
    id: 2,
    type: 'notice',
    title: '全市中小学教学研讨会通知',
    content: '定于本周五下午2点在教育局会议中心召开全市中小学教学研讨会，请各校教学负责人准时参加。',
    publishTime: '2025-02-18'
  },
  {
    id: 3,
    type: 'emergency',
    title: '紧急：关于做好校园安全工作的紧急通知',
    content: '近期气温骤降，请各学校加强校园安全管理，做好防寒保暖和安全检查工作。',
    publishTime: '2025-02-15'
  },
  {
    id: 4,
    type: 'announcement',
    title: '2025年教师培训计划公布',
    content: '新学期教师培训计划已公布，包含教学技能提升、教育信息化应用等多个专题，请各校组织教师报名参加。',
    publishTime: '2025-02-10'
  }
])

// 模拟新闻数据 - 要闻动态
const newsList = ref([
  {
    id: 1,
    title: '我市教育信息化建设取得新突破',
    summary: '智慧校园平台全面升级，实现教学管理一体化，提升教育教学效率。',
    date: '2025-02-18'
  },
  {
    id: 2,
    title: '全市教育质量提升工程启动',
    summary: '新学期将实施多项教育质量提升措施，重点关注学生综合素质培养。',
    date: '2025-02-15'
  },
  {
    id: 3,
    title: '教师专业发展支持计划实施',
    summary: '为教师提供更多专业发展机会，提升教育教学水平。',
    date: '2025-02-12'
  }
])

// 服务栏目
const services = ref([
  { id: 1, name: '成绩查询', icon: 'Document', path: '/login', needAuth: true },
  { id: 2, name: '课表查询', icon: 'Calendar', path: '/login', needAuth: true },
  { id: 3, name: '班级信息', icon: 'School', path: '/login', needAuth: true },
  { id: 4, name: '通知公告', icon: 'Bell', path: '/login', needAuth: true },
  { id: 5, name: '个人信息', icon: 'User', path: '/login', needAuth: true },
  { id: 6, name: '荣誉榜', icon: 'Trophy', path: '/login', needAuth: true }
])

// 快速链接
const quickLinks = ref([
  { name: '教育政策', path: '/login' },
  { name: '招生信息', path: '/login' },
  { name: '教育资源', path: '/login' },
  { name: '家长指南', path: '/login' }
])

// 获取公告类型文本
const getTypeText = (type) => {
  const map = {
    notice: '通知',
    announcement: '公告',
    emergency: '紧急'
  }
  return map[type] || type
}

// 跳转到登录
const goToLogin = () => {
  router.push('/login')
}

// 跳转到注册
const goToRegister = () => {
  router.push('/register')
}

// 查看公告详情
const viewAnnouncement = (item) => {
  selectedAnnouncement.value = item
  dialogVisible.value = true
}

// 处理服务点击
const handleServiceClick = (service) => {
  if (service.needAuth) {
    ElMessage.info('请先登录后使用该功能')
    setTimeout(() => {
      router.push('/login')
    }, 800)
  } else {
    router.push(service.path)
  }
}

// 处理快速链接点击
const handleQuickLink = (link) => {
  ElMessage.info('请先登录后使用该功能')
  setTimeout(() => {
    router.push('/login')
  }, 800)
}
</script>

<style scoped>
/* 整体容器 */
.portal-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #f8fafc 0%, #eef2f7 100%);
  display: flex;
  flex-direction: column;
}

/* 顶部导航栏 */
.portal-header {
  background: linear-gradient(135deg, #2c3e50 0%, #34495e 100%);
  color: white;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.15);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 15px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 15px;
}

.logo-section {
  display: flex;
  align-items: center;
  gap: 15px;
}

.site-title {
  font-size: 22px;
  font-weight: 700;
  margin: 0;
  letter-spacing: 0.5px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.header-actions {
  display: flex;
  gap: 10px;
}

.header-actions :deep(.el-button) {
  font-weight: 600;
  border-radius: 6px;
}

.header-actions :deep(.el-button--primary) {
  background: linear-gradient(135deg, #4a7dff 0%, #6a5af9 100%);
  border: none;
  box-shadow: 0 4px 12px rgba(74, 125, 255, 0.4);
}

/* 主要内容区域 */
.portal-main {
  flex: 1;
  max-width: 1200px;
  width: 100%;
  margin: 0 auto;
  padding: 20px;
}

/* 欢迎横幅 */
.hero-banner {
  background: linear-gradient(135deg, #4a7dff 0%, #6a5af9 100%);
  border-radius: 16px;
  padding: 40px;
  margin-bottom: 30px;
  color: white;
  box-shadow: 0 8px 24px rgba(74, 125, 255, 0.3);
}

/* 滚动播放栏目 */
.carousel-section {
  margin-bottom: 30px;
}

.carousel-card {
  border-radius: 12px;
  overflow: hidden;
}

.carousel-container {
  position: relative;
  width: 100%;
  height: 320px;
  overflow: hidden;
  background: #f5f7fa;
  border-radius: 8px;
}

.carousel-item {
  width: 100%;
  height: 100%;
  animation: fadeIn 0.5s ease-in-out;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.carousel-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

/* 图片加载失败时的样式 */
.carousel-item img[src=""] {
  background: #ddd;
  content: '图片加载失败';
}

.carousel-caption {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: linear-gradient(to top, rgba(0, 0, 0, 0.7), transparent);
  color: white;
  padding: 20px 15px 15px;
  font-size: 16px;
  font-weight: 600;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.5);
}

/* 控制按钮 */
.carousel-controls {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  width: 100%;
  display: flex;
  justify-content: space-between;
  padding: 0 10px;
  pointer-events: none;
}

.control-btn {
  pointer-events: all;
  background: rgba(255, 255, 255, 0.8);
  border: none;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  font-size: 24px;
  color: #2c3e50;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.control-btn:hover {
  background: white;
  transform: scale(1.1);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

/* 指示器 */
.carousel-indicators {
  position: absolute;
  bottom: 15px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 8px;
  z-index: 10;
}

.indicator {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.5);
  cursor: pointer;
  transition: all 0.3s ease;
  border: 2px solid transparent;
}

.indicator:hover {
  background: rgba(255, 255, 255, 0.8);
  transform: scale(1.2);
}

.indicator.active {
  background: white;
  width: 24px;
  border-radius: 5px;
}

.hero-content h2 {
  font-size: 32px;
  margin: 0 0 10px 0;
  font-weight: 700;
}

.hero-content p {
  font-size: 16px;
  margin: 0;
  opacity: 0.95;
}

/* 内容网格布局 */
.content-grid {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 20px;
}

.left-column,
.right-column {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* 卡片样式 */
.section-card {
  border-radius: 12px;
  border: 1px solid rgba(0, 0, 0, 0.05);
  background: white;
  transition: all 0.3s ease;
}

.section-card:hover {
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.08);
  transform: translateY(-2px);
}

.section-card :deep(.el-card__header) {
  background: linear-gradient(135deg, #f8fafc 0%, #eef2f7 100%);
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
  padding: 12px 16px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.section-title {
  font-weight: 700;
  color: #2c3e50;
  font-size: 16px;
}

/* 公告列表 */
.announcement-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.announcement-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
  border: 1px solid #ebeef5;
  background: #fafafa;
}

.announcement-item:hover {
  background: #f0f7ff;
  border-color: #409EFF;
  transform: translateX(4px);
}

.announcement-tag {
  padding: 3px 8px;
  border-radius: 4px;
  font-size: 11px;
  font-weight: 600;
  white-space: nowrap;
}

.announcement-tag.announcement {
  background: #67C23A;
  color: white;
}

.announcement-tag.notice {
  background: #909399;
  color: white;
}

.announcement-tag.emergency {
  background: #F56C6C;
  color: white;
}

.announcement-title {
  flex: 1;
  font-size: 14px;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.announcement-date {
  font-size: 12px;
  color: #909399;
  white-space: nowrap;
}

.empty-state {
  padding: 20px 0;
}

/* 新闻列表 */
.news-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.news-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  border-bottom: 1px solid #ebeef5;
  gap: 15px;
}

.news-item:last-child {
  border-bottom: none;
}

.news-content h4 {
  margin: 0 0 5px 0;
  font-size: 14px;
  color: #303133;
  font-weight: 600;
}

.news-content p {
  margin: 0;
  font-size: 12px;
  color: #606266;
  line-height: 1.4;
}

.news-date {
  font-size: 11px;
  color: #909399;
  white-space: nowrap;
}

/* 服务栏目 */
.service-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.service-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 16px;
  border-radius: 8px;
  background: linear-gradient(135deg, #f8fafc 0%, #eef2f7 100%);
  border: 1px solid #ebeef5;
  cursor: pointer;
  transition: all 0.3s ease;
  gap: 8px;
}

.service-item:hover {
  background: linear-gradient(135deg, #e8f4ff 0%, #f0f7ff 100%);
  border-color: #409EFF;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.2);
}

.service-icon {
  font-size: 24px;
  color: #409EFF;
}

.service-name {
  font-size: 13px;
  font-weight: 600;
  color: #303133;
}

/* 快速链接 */
.quick-links {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.quick-link-btn {
  width: 100%;
  justify-content: flex-start;
  border-radius: 8px;
  font-weight: 500;
  background: #f5f7fa;
  border: 1px solid #ebeef5;
  color: #303133;
  transition: all 0.2s ease;
}

.quick-link-btn:hover {
  background: linear-gradient(135deg, #e8f4ff 0%, #f0f7ff 100%);
  border-color: #409EFF;
  color: #409EFF;
  transform: translateX(4px);
}

/* 联系信息 */
.contact-info {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.contact-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px;
  background: #f8f9fa;
  border-radius: 6px;
  font-size: 13px;
  color: #495057;
}

.contact-item .el-icon {
  color: #409EFF;
  font-size: 16px;
}

/* 页脚 */
.portal-footer {
  background: linear-gradient(135deg, #2c3e50 0%, #34495e 100%);
  color: white;
  padding: 20px;
  margin-top: auto;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  text-align: center;
  font-size: 13px;
  opacity: 0.9;
}

.footer-content p {
  margin: 5px 0;
}

/* 公告详情 */
.announcement-detail {
  padding: 10px;
}

.detail-meta {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #ebeef5;
  color: #909399;
  font-size: 13px;
}

.detail-content {
  line-height: 1.8;
  color: #303133;
  white-space: pre-line;
  font-size: 14px;
}

/* 响应式设计 */
@media (max-width: 968px) {
  .content-grid {
    grid-template-columns: 1fr;
  }

  .hero-banner {
    padding: 25px;
  }

  .hero-content h2 {
    font-size: 24px;
  }

  .service-grid {
    grid-template-columns: repeat(3, 1fr);
  }

  .header-content {
    flex-direction: column;
    align-items: flex-start;
  }
}

@media (max-width: 640px) {
  .service-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .portal-main {
    padding: 10px;
  }

  .hero-banner {
    padding: 20px;
  }

  .hero-content h2 {
    font-size: 20px;
  }

  .announcement-item {
    flex-wrap: wrap;
  }

  .news-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
}
</style>
