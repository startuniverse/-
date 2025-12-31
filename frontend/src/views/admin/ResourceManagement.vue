<template>
  <div class="resource-management">
    <el-row :gutter="20">
      <el-col :span="16" :xs="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>资源列表</span>
              <div class="controls">
                <el-select v-model="category" placeholder="分类" size="small" style="width: 120px;" clearable>
                  <el-option label="课程" value="lesson"></el-option>
                  <el-option label="视频" value="video"></el-option>
                  <el-option label="试卷" value="exam"></el-option>
                </el-select>
                <el-button type="primary" size="small" @click="loadResources">查询</el-button>
              </div>
            </div>
          </template>

          <el-table v-loading="loading" :data="resourcesData" border style="width: 100%">
            <el-table-column type="index" label="序号" width="60" align="center" />
            <el-table-column prop="title" label="标题" min-width="150" />
            <el-table-column prop="category" label="分类" width="100" align="center">
              <template #default="scope">
                <el-tag type="info">{{ getCategoryText(scope.row.category) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="subject" label="科目" width="100" align="center" />
            <el-table-column prop="viewCount" label="浏览" width="80" align="center" />
            <el-table-column prop="downloadCount" label="下载" width="80" align="center" />
            <el-table-column prop="status" label="状态" width="100" align="center">
              <template #default="scope">
                <el-tag :type="getStatusTag(scope.row.status)">
                  {{ getStatusText(scope.row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150" align="center" fixed="right">
              <template #default="scope">
                <el-button
                  v-if="scope.row.status === 0"
                  type="success"
                  link
                  size="small"
                  @click="handleReview(scope.row, 1)"
                >
                  通过
                </el-button>
                <el-button
                  v-if="scope.row.status === 0"
                  type="danger"
                  link
                  size="small"
                  @click="handleReview(scope.row, 2)"
                >
                  拒绝
                </el-button>
                <el-button
                  type="danger"
                  link
                  size="small"
                  @click="handleDelete(scope.row)"
                >
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>

          <div class="pagination">
            <el-pagination
              v-model:current-page="currentPage"
              v-model:page-size="pageSize"
              :total="total"
              :page-sizes="[10, 20, 50]"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
            />
          </div>
        </el-card>
      </el-col>

      <el-col :span="8" :xs="24">
        <el-card class="stats-card">
          <template #header>
            <div class="card-header">
              <span>资源统计</span>
            </div>
          </template>
          <div v-if="stats" class="stats-content">
            <div class="stat-item">
              <span class="label">总资源数:</span>
              <span class="value">{{ stats.totalResources }}</span>
            </div>
            <div class="stat-item">
              <span class="label">已发布:</span>
              <span class="value">{{ stats.publishedResources }}</span>
            </div>
            <div class="stat-item">
              <span class="label">总浏览:</span>
              <span class="value">{{ stats.totalViews }}</span>
            </div>
            <div class="stat-item">
              <span class="label">总下载:</span>
              <span class="value">{{ stats.totalDownloads }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getResources, getResourceStatistics, deleteResource } from '@/api/admin'

const loading = ref(false)
const category = ref('')
const resourcesData = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const stats = ref(null)

const getCategoryText = (category) => {
  const map = {
    lesson: '课程',
    video: '视频',
    exam: '试卷',
    other: '其他'
  }
  return map[category] || category
}

const getStatusText = (status) => {
  const map = {
    0: '待审核',
    1: '已发布',
    2: '已拒绝'
  }
  return map[status] || '未知'
}

const getStatusTag = (status) => {
  const map = {
    0: 'warning',
    1: 'success',
    2: 'danger'
  }
  return map[status] || 'info'
}

const loadResources = async () => {
  try {
    loading.value = true
    const params = {
      current: currentPage.value,
      size: pageSize.value
    }
    if (category.value) params.category = category.value

    const data = await getResources(params)
    resourcesData.value = data.records
    total.value = data.total
  } catch (error) {
    ElMessage.error('加载资源列表失败')
  } finally {
    loading.value = false
  }
}

const loadStats = async () => {
  try {
    const data = await getResourceStatistics()
    stats.value = data
  } catch (error) {
    console.error('加载统计失败:', error)
  }
}

const handleReview = async (row, status) => {
  const action = status === 1 ? '通过' : '拒绝'
  ElMessageBox.confirm(`确定要${action}该资源吗？`, '资源审核', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: status === 1 ? 'success' : 'warning'
  }).then(async () => {
    try {
      // 这里调用审核API
      ElMessage.success(`${action}成功`)
      loadResources()
      loadStats()
    } catch (error) {
      ElMessage.error(`${action}失败`)
    }
  }).catch(() => {})
}

const handleDelete = async (row) => {
  ElMessageBox.confirm(`确定要删除资源 "${row.title}" 吗？`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteResource(row.id)
      ElMessage.success('删除成功')
      loadResources()
      loadStats()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

const handleSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1
  loadResources()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  loadResources()
}

onMounted(() => {
  loadResources()
  loadStats()
})
</script>

<style scoped>
.resource-management {
  width: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.controls {
  display: flex;
  gap: 10px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.stats-card {
  margin-bottom: 20px;
}

.stats-content {
  padding: 10px 0;
}

.stat-item {
  display: flex;
  justify-content: space-between;
  padding: 12px 0;
  border-bottom: 1px solid #f5f5f5;
}

.stat-item:last-child {
  border-bottom: none;
}

.label {
  color: #909399;
  font-size: 14px;
}

.value {
  color: #303133;
  font-size: 16px;
  font-weight: 600;
}
</style>
