<template>
  <div class="resource-management">
    <el-row :gutter="20">
      <el-col :span="16" :xs="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>资源列表</span>
              <div class="controls">
                <el-input
                  v-model="keyword"
                  placeholder="搜索标题/描述"
                  size="small"
                  style="width: 150px;"
                  clearable
                  @keyup.enter="loadResources"
                ></el-input>
                <el-select v-model="category" placeholder="分类" size="small" style="width: 100px;" clearable>
                  <el-option label="课程" value="lesson"></el-option>
                  <el-option label="视频" value="video"></el-option>
                  <el-option label="试卷" value="exam"></el-option>
                  <el-option label="其他" value="other"></el-option>
                </el-select>
                <el-select v-model="subject" placeholder="科目" size="small" style="width: 100px;" clearable>
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
                <el-select v-model="status" placeholder="状态" size="small" style="width: 100px;" clearable>
                  <el-option label="待审核" :value="0"></el-option>
                  <el-option label="已发布" :value="1"></el-option>
                  <el-option label="已拒绝" :value="2"></el-option>
                </el-select>
                <el-button type="primary" size="small" @click="loadResources">查询</el-button>
                <el-button type="success" size="small" @click="openAddDialog">添加资源</el-button>
              </div>
            </div>
          </template>

          <el-table v-loading="loading" :data="resourcesData" border style="width: 100%" max-height="500">
            <el-table-column type="index" label="序号" width="60" align="center" fixed />
            <el-table-column prop="title" label="标题" min-width="180" show-overflow-tooltip />
            <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
            <el-table-column prop="category" label="分类" width="90" align="center">
              <template #default="scope">
                <el-tag type="info" size="small">{{ getCategoryText(scope.row.category) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="subject" label="科目" width="90" align="center" />
            <el-table-column prop="fileType" label="文件类型" width="90" align="center">
              <template #default="scope">
                <el-tag type="warning" size="small" v-if="scope.row.fileType">{{ scope.row.fileType }}</el-tag>
                <span v-else>-</span>
              </template>
            </el-table-column>
            <el-table-column prop="viewCount" label="浏览" width="70" align="center" />
            <el-table-column prop="downloadCount" label="下载" width="70" align="center" />
            <el-table-column prop="uploadTime" label="上传时间" width="150" align="center">
              <template #default="scope">
                {{ formatDateTime(scope.row.uploadTime) }}
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="90" align="center">
              <template #default="scope">
                <el-tag :type="getStatusTag(scope.row.status)" size="small">
                  {{ getStatusText(scope.row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="200" align="center" fixed="right">
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
                  v-if="scope.row.status === 1"
                  type="primary"
                  link
                  size="small"
                  @click="openEditDialog(scope.row)"
                >
                  编辑
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
              <span class="label">待审核:</span>
              <span class="value">{{ stats.pendingReview }}</span>
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

    <!-- 添加/编辑资源对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '添加资源' : '编辑资源'"
      width="600px"
      destroy-on-close
    >
      <el-form
        ref="formRef"
        :model="resourceForm"
        :rules="formRules"
        label-width="100px"
        label-position="right"
      >
        <el-form-item label="标题" prop="title">
          <el-input v-model="resourceForm.title" placeholder="请输入资源标题" clearable></el-input>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="resourceForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入资源描述"
          ></el-input>
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-select v-model="resourceForm.category" placeholder="请选择分类" style="width: 100%;" clearable>
            <el-option label="课程" value="lesson"></el-option>
            <el-option label="视频" value="video"></el-option>
            <el-option label="试卷" value="exam"></el-option>
            <el-option label="其他" value="other"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="科目" prop="subject">
          <el-select v-model="resourceForm.subject" placeholder="请选择科目" style="width: 100%;" clearable>
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
        </el-form-item>
        <el-form-item label="文件URL" prop="fileUrl">
          <el-input v-model="resourceForm.fileUrl" placeholder="请输入文件URL或路径" clearable></el-input>
        </el-form-item>
        <el-form-item label="文件类型" prop="fileType">
          <el-input v-model="resourceForm.fileType" placeholder="如: PDF, MP4, DOCX" clearable></el-input>
        </el-form-item>
        <el-form-item label="文件大小" prop="fileSize">
          <el-input v-model.number="resourceForm.fileSize" placeholder="文件大小(字节)" type="number" clearable>
            <template #append>字节</template>
          </el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">
            {{ dialogType === 'add' ? '添加' : '保存' }}
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getAdminResources,
  getResourceStatistics,
  deleteResource,
  adminAddResource,
  adminEditResource,
  reviewResource
} from '@/api/admin'

const loading = ref(false)
const submitting = ref(false)
const category = ref('')
const subject = ref('')
const status = ref('')
const keyword = ref('')
const resourcesData = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const stats = ref(null)

// 对话框相关
const dialogVisible = ref(false)
const dialogType = ref('add') // 'add' 或 'edit'
const formRef = ref(null)
const resourceForm = reactive({
  id: null,
  title: '',
  description: '',
  category: '',
  subject: '',
  fileUrl: '',
  fileType: '',
  fileSize: null
})

const formRules = {
  title: [
    { required: true, message: '请输入资源标题', trigger: 'blur' },
    { min: 2, max: 100, message: '标题长度在 2 到 100 个字符', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入资源描述', trigger: 'blur' },
    { min: 5, max: 500, message: '描述长度在 5 到 500 个字符', trigger: 'blur' }
  ],
  category: [
    { required: true, message: '请选择分类', trigger: 'change' }
  ],
  subject: [
    { required: true, message: '请选择科目', trigger: 'change' }
  ]
}

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

// 数据加载
const loadResources = async () => {
  try {
    loading.value = true
    const params = {
      current: currentPage.value,
      size: pageSize.value
    }
    if (category.value) params.category = category.value
    if (subject.value) params.subject = subject.value
    if (status.value !== '' && status.value !== null) params.status = status.value
    if (keyword.value) params.keyword = keyword.value

    const data = await getAdminResources(params)
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
    // 计算待审核数量
    const pendingCount = resourcesData.value.filter(r => r.status === 0).length
    stats.value = {
      ...data,
      pendingReview: pendingCount
    }
  } catch (error) {
    console.error('加载统计失败:', error)
  }
}

// 操作处理
const handleReview = async (row, status) => {
  const action = status === 1 ? '通过' : '拒绝'
  ElMessageBox.confirm(`确定要${action}该资源吗？`, '资源审核', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: status === 1 ? 'success' : 'warning'
  }).then(async () => {
    try {
      await reviewResource({ id: row.id, status })
      ElMessage.success(`${action}成功`)
      loadResources()
      setTimeout(() => loadStats(), 300)
    } catch (error) {
      ElMessage.error(`${action}失败: ${error.message || ''}`)
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
      setTimeout(() => loadStats(), 300)
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
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

// 对话框处理
const openAddDialog = () => {
  dialogType.value = 'add'
  // 重置表单
  Object.assign(resourceForm, {
    id: null,
    title: '',
    description: '',
    category: '',
    subject: '',
    fileUrl: '',
    fileType: '',
    fileSize: null
  })
  dialogVisible.value = true
}

const openEditDialog = (row) => {
  dialogType.value = 'edit'
  // 填充表单
  Object.assign(resourceForm, {
    id: row.id,
    title: row.title,
    description: row.description,
    category: row.category,
    subject: row.subject,
    fileUrl: row.fileUrl,
    fileType: row.fileType,
    fileSize: row.fileSize
  })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        if (dialogType.value === 'add') {
          await adminAddResource(resourceForm)
          ElMessage.success('资源添加成功')
        } else {
          await adminEditResource(resourceForm)
          ElMessage.success('资源编辑成功')
        }
        dialogVisible.value = false
        loadResources()
        setTimeout(() => loadStats(), 300)
      } catch (error) {
        ElMessage.error(`${dialogType.value === 'add' ? '添加' : '编辑'}失败: ${error.message || ''}`)
      } finally {
        submitting.value = false
      }
    }
  })
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
  flex-wrap: wrap;
  gap: 10px;
}

.controls {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
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

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>
