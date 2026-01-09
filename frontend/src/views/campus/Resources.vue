<template>
  <div class="resources-container">
    <!-- 搜索和筛选区域 -->
    <el-card class="search-card">
      <div class="search-header">
        <h2>学习资源库</h2>
        <div class="search-controls">
          <el-input
            v-model="searchParams.keyword"
            placeholder="搜索资源标题或描述..."
            size="large"
            style="width: 300px;"
            clearable
            @keyup.enter="handleSearch"
            @clear="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          <el-button type="primary" size="large" @click="handleSearch" :icon="Search">
            搜索
          </el-button>
        </div>
      </div>

      <div class="filter-row">
        <div class="filter-group">
          <span class="filter-label">分类：</span>
          <el-radio-group v-model="searchParams.category" size="small" @change="handleSearch">
            <el-radio-button label="">全部</el-radio-button>
            <el-radio-button label="lesson">课程</el-radio-button>
            <el-radio-button label="video">视频</el-radio-button>
            <el-radio-button label="exam">试卷</el-radio-button>
            <el-radio-button label="other">其他</el-radio-button>
          </el-radio-group>
        </div>

        <div class="filter-group">
          <span class="filter-label">科目：</span>
          <el-select
            v-model="searchParams.subject"
            placeholder="选择科目"
            size="small"
            style="width: 120px;"
            clearable
            @change="handleSearch"
          >
            <el-option label="语文" value="语文"></el-option>
            <el-option label="数学" value="数学"></el-option>
            <el-option label="英语" value="英语"></el-option>
            <el-option label="物理" value="物理"></el-option>
            <el-option label="化学" value="化学"></el-option>
            <el-option label="生物" value="生物"></el-option>
            <el-option label="历史" value="历史"></el-option>
            <el-option label="地理" value="地理"></el-option>
            <el-option label="政治" value="政治"></el-option>
            <el-option label="通用" value="通用"></el-option>
          </el-select>
        </div>

        <div class="filter-group">
          <el-button type="info" size="small" @click="resetFilters" :icon="Refresh">
            重置
          </el-button>
        </div>
      </div>
    </el-card>

    <!-- 资源列表 -->
    <el-card class="resources-card">
      <template #header>
        <div class="card-header">
          <span>资源列表</span>
          <span class="result-count" v-if="total > 0">共 {{ total }} 个资源</span>
        </div>
      </template>

      <div v-loading="loading" class="resources-content">
        <!-- 空状态 -->
        <el-empty v-if="resourcesData.length === 0 && !loading" description="暂无资源，请稍后再试或联系管理员"></el-empty>

        <!-- 资源卡片列表 -->
        <div v-else class="resource-grid">
          <div
            v-for="item in resourcesData"
            :key="item.id"
            class="resource-card"
            @click="openResourceDetail(item)"
          >
            <div class="resource-header">
              <div class="resource-title">
                <el-tag type="primary" size="small" class="category-tag">
                  {{ getCategoryText(item.category) }}
                </el-tag>
                <span class="title-text">{{ item.title }}</span>
              </div>
            </div>

            <div class="resource-description">
              {{ item.description || '暂无描述' }}
            </div>

            <div class="resource-meta">
              <div class="meta-item">
                <el-icon><Collection /></el-icon>
                <span>{{ item.subject || '通用' }}</span>
              </div>
              <div class="meta-item">
                <el-icon><View /></el-icon>
                <span>{{ item.viewCount || 0 }}</span>
              </div>
              <div class="meta-item">
                <el-icon><Download /></el-icon>
                <span>{{ item.downloadCount || 0 }}</span>
              </div>
            </div>

            <div class="resource-footer">
              <span class="upload-time">{{ formatDateTime(item.uploadTime) }}</span>
              <div class="resource-actions">
                <el-button
                  type="primary"
                  size="small"
                  link
                  @click.stop="handleDownload(item)"
                >
                  下载
                </el-button>
                <el-button
                  type="info"
                  size="small"
                  link
                  @click.stop="handleShare(item)"
                >
                  分享
                </el-button>
              </div>
            </div>
          </div>
        </div>

        <!-- 分页 -->
        <div v-if="total > 0" class="pagination-container">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :total="total"
            :page-sizes="[12, 24, 48]"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>
    </el-card>

    <!-- 资源详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="资源详情"
      width="600px"
      destroy-on-close
    >
      <div v-if="currentResource" class="resource-detail">
        <div class="detail-section">
          <h3 class="detail-title">{{ currentResource.title }}</h3>
          <div class="detail-tags">
            <el-tag type="primary">{{ getCategoryText(currentResource.category) }}</el-tag>
            <el-tag type="info">{{ currentResource.subject || '通用' }}</el-tag>
            <el-tag type="warning" v-if="currentResource.fileType">{{ currentResource.fileType }}</el-tag>
          </div>
        </div>

        <div class="detail-section">
          <h4>描述</h4>
          <p class="detail-description">{{ currentResource.description || '暂无描述' }}</p>
        </div>

        <div class="detail-section">
          <h4>资源信息</h4>
          <div class="detail-info">
            <div class="info-item">
              <span class="label">上传时间：</span>
              <span class="value">{{ formatDateTime(currentResource.uploadTime) }}</span>
            </div>
            <div class="info-item">
              <span class="label">浏览次数：</span>
              <span class="value">{{ currentResource.viewCount || 0 }}</span>
            </div>
            <div class="info-item">
              <span class="label">下载次数：</span>
              <span class="value">{{ currentResource.downloadCount || 0 }}</span>
            </div>
            <div class="info-item" v-if="currentResource.fileSize">
              <span class="label">文件大小：</span>
              <span class="value">{{ formatFileSize(currentResource.fileSize) }}</span>
            </div>
            <div class="info-item" v-if="currentResource.fileUrl">
              <span class="label">文件路径：</span>
              <span class="value">{{ currentResource.fileUrl }}</span>
            </div>
          </div>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
          <el-button type="primary" @click="handleDownload(currentResource)">
            下载资源
          </el-button>
          <el-button type="info" @click="handleShare(currentResource)">
            分享链接
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Collection, View, Download } from '@element-plus/icons-vue'
import { getStudentResources, downloadResource, shareResource, getResourceDetail } from '@/api/campus'

const loading = ref(false)
const resourcesData = ref([])
const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0)

// 搜索参数
const searchParams = ref({
  keyword: '',
  category: '',
  subject: ''
})

// 详情对话框
const detailDialogVisible = ref(false)
const currentResource = ref(null)

// 工具函数
const getCategoryText = (category) => {
  const map = {
    lesson: '课程',
    video: '视频',
    exam: '试卷',
    other: '其他'
  }
  return map[category] || category
}

const formatDateTime = (datetime) => {
  if (!datetime) return '-'
  const date = new Date(datetime)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}`
}

const formatFileSize = (bytes) => {
  if (!bytes) return '-'
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB'
  if (bytes < 1024 * 1024 * 1024) return (bytes / (1024 * 1024)).toFixed(1) + ' MB'
  return (bytes / (1024 * 1024 * 1024)).toFixed(1) + ' GB'
}

// 数据加载
const loadResources = async () => {
  try {
    loading.value = true
    const params = {
      current: currentPage.value,
      size: pageSize.value,
      ...searchParams.value
    }

    // 移除空参数
    Object.keys(params).forEach(key => {
      if (params[key] === '' || params[key] === null) {
        delete params[key]
      }
    })

    const data = await getStudentResources(params)
    resourcesData.value = data.records || []
    total.value = data.total || 0
  } catch (error) {
    ElMessage.error('加载资源列表失败')
    resourcesData.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 搜索处理
const handleSearch = () => {
  currentPage.value = 1
  loadResources()
}

// 重置筛选
const resetFilters = () => {
  searchParams.value = {
    keyword: '',
    category: '',
    subject: ''
  }
  currentPage.value = 1
  loadResources()
}

// 分页处理
const handleSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1
  loadResources()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  loadResources()
}

// 资源操作
const openResourceDetail = async (item) => {
  try {
    // 调用详情接口，增加浏览次数
    const detail = await getResourceDetail(item.id)
    currentResource.value = detail
    // 更新列表中的数据
    const index = resourcesData.value.findIndex(r => r.id === item.id)
    if (index !== -1) {
      resourcesData.value[index].viewCount = detail.viewCount
    }
    detailDialogVisible.value = true
  } catch (error) {
    ElMessage.error('加载资源详情失败')
  }
}

const handleDownload = async (item) => {
  try {
    loading.value = true
    const result = await downloadResource(item.id)

    // 更新下载次数
    item.downloadCount = (item.downloadCount || 0) + 1

    ElMessage.success('准备开始下载...')

    // 模拟下载（实际项目中这里会返回真实文件URL）
    if (result && result.url) {
      // 创建下载链接
      const link = document.createElement('a')
      link.href = result.url
      link.download = result.filename || item.title
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
    }
  } catch (error) {
    ElMessage.error('下载失败: ' + (error.message || '请稍后重试'))
  } finally {
    loading.value = false
  }
}

const handleShare = async (item) => {
  try {
    const result = await shareResource(item.id)

    if (result && result.shareCode) {
      // 生成分享链接
      const shareUrl = `${window.location.origin}/resource/share/${result.shareCode}`

      ElMessageBox.confirm(
        `分享链接已生成：${shareUrl}\n\n是否复制到剪贴板？`,
        '资源分享',
        {
          confirmButtonText: '复制链接',
          cancelButtonText: '关闭',
          type: 'info'
        }
      ).then(() => {
        // 复制到剪贴板
        navigator.clipboard.writeText(shareUrl).then(() => {
          ElMessage.success('分享链接已复制到剪贴板')
        }).catch(() => {
          ElMessage.warning('复制失败，请手动复制')
        })
      }).catch(() => {})
    } else {
      ElMessage.warning('分享链接生成失败')
    }
  } catch (error) {
    ElMessage.error('分享失败: ' + (error.message || '请稍后重试'))
  }
}

// 页面挂载时加载数据
onMounted(() => {
  loadResources()
})
</script>

<style scoped>
.resources-container {
  width: 100%;
}

.search-card {
  margin-bottom: 20px;
}

.search-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 15px;
}

.search-header h2 {
  margin: 0;
  color: #303133;
  font-size: 24px;
}

.search-controls {
  display: flex;
  gap: 10px;
  align-items: center;
}

.filter-row {
  display: flex;
  gap: 20px;
  align-items: center;
  flex-wrap: wrap;
  padding-top: 15px;
  border-top: 1px solid #ebeef5;
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 8px;
}

.filter-label {
  font-size: 14px;
  color: #606266;
  font-weight: 500;
}

.resources-card {
  min-height: 400px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.result-count {
  font-size: 14px;
  color: #909399;
}

.resources-content {
  min-height: 300px;
}

.resource-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 16px;
}

.resource-card {
  background: #fff;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  padding: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.resource-card:hover {
  border-color: #409eff;
  box-shadow: 0 2px 12px rgba(64, 158, 255, 0.15);
  transform: translateY(-2px);
}

.resource-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.resource-title {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
}

.category-tag {
  flex-shrink: 0;
}

.title-text {
  font-weight: 600;
  color: #303133;
  font-size: 16px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.resource-description {
  color: #606266;
  font-size: 13px;
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  min-height: 40px;
}

.resource-meta {
  display: flex;
  gap: 16px;
  padding-top: 8px;
  border-top: 1px solid #f5f5f5;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #909399;
  font-size: 12px;
}

.resource-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 8px;
  border-top: 1px solid #f5f5f5;
}

.upload-time {
  font-size: 12px;
  color: #909399;
}

.resource-actions {
  display: flex;
  gap: 8px;
}

.pagination-container {
  margin-top: 24px;
  display: flex;
  justify-content: center;
}

/* 详情对话框样式 */
.resource-detail {
  padding: 10px 0;
}

.detail-section {
  margin-bottom: 20px;
}

.detail-title {
  margin: 0 0 12px 0;
  color: #303133;
  font-size: 20px;
}

.detail-tags {
  display: flex;
  gap: 8px;
}

.detail-section h4 {
  margin: 0 0 12px 0;
  color: #606266;
  font-size: 14px;
}

.detail-description {
  margin: 0;
  color: #606266;
  line-height: 1.8;
  font-size: 14px;
}

.detail-info {
  background: #f5f7fa;
  padding: 16px;
  border-radius: 6px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  border-bottom: 1px solid #ebeef5;
}

.info-item:last-child {
  border-bottom: none;
}

.info-item .label {
  color: #909399;
  font-size: 14px;
}

.info-item .value {
  color: #303133;
  font-size: 14px;
  font-weight: 500;
  text-align: right;
  max-width: 60%;
  word-break: break-all;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .search-header {
    flex-direction: column;
    align-items: stretch;
  }

  .search-controls {
    flex-direction: column;
  }

  .search-controls .el-input {
    width: 100% !important;
  }

  .resource-grid {
    grid-template-columns: 1fr;
  }

  .filter-row {
    flex-direction: column;
    align-items: stretch;
  }

  .filter-group {
    justify-content: space-between;
  }
}
</style>
