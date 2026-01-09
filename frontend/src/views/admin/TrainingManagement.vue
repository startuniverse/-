<template>
  <div class="training-management">
    <el-card class="header-card">
      <div class="header-content">
        <h2>培训课程管理</h2>
        <el-button type="primary" @click="showAddDialog" :icon="Plus">
          新增课程
        </el-button>
      </div>
    </el-card>

    <!-- 统计卡片 -->
    <el-row :gutter="20" style="margin-bottom: 20px;">
      <el-col :span="6">
        <el-card class="stat-card stat-1" shadow="hover">
          <div class="stat-content">
            <div class="stat-number">{{ statistics.totalCourses || 0 }}</div>
            <div class="stat-label">总课程数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card stat-2" shadow="hover">
          <div class="stat-content">
            <div class="stat-number">{{ statistics.totalEnrollments || 0 }}</div>
            <div class="stat-label">总报名数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card stat-3" shadow="hover">
          <div class="stat-content">
            <div class="stat-number">{{ statistics.completedCourses || 0 }}</div>
            <div class="stat-label">完成数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card stat-4" shadow="hover">
          <div class="stat-content">
            <div class="stat-number">{{ statistics.totalViews || 0 }}</div>
            <div class="stat-label">总浏览量</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 筛选工具栏 -->
    <el-card class="filter-card">
      <el-row :gutter="15">
        <el-col :span="5">
          <el-select v-model="filters.category" placeholder="分类" clearable @change="handleFilter">
            <el-option label="职业技能" value="职业技能" />
            <el-option label="安全教育" value="安全教育" />
            <el-option label="师德师风" value="师德师风" />
            <el-option label="专业技能" value="专业技能" />
          </el-select>
        </el-col>
        <el-col :span="5">
          <el-select v-model="filters.subject" placeholder="科目" clearable @change="handleFilter">
            <el-option label="语文" value="语文" />
            <el-option label="数学" value="数学" />
            <el-option label="英语" value="英语" />
            <el-option label="综合" value="综合" />
          </el-select>
        </el-col>
        <el-col :span="5">
          <el-select v-model="filters.status" placeholder="状态" clearable @change="handleFilter">
            <el-option label="草稿" :value="0" />
            <el-option label="已发布" :value="1" />
            <el-option label="已下架" :value="2" />
          </el-select>
        </el-col>
        <el-col :span="5">
          <el-input v-model="filters.keyword" placeholder="搜索标题" clearable @input="handleFilter">
            <template #append>
              <el-button :icon="Search" />
            </template>
          </el-input>
        </el-col>
        <el-col :span="4">
          <el-button @click="resetFilters">重置</el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 课程列表 -->
    <el-card style="margin-top: 20px;">
      <el-table
        :data="courses"
        style="width: 100%"
        v-loading="loading"
        :header-cell-style="{ background: '#f5f7fa', fontWeight: '600' }"
        border
      >
        <el-table-column prop="title" label="课程标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="category" label="分类" width="120" align="center" />
        <el-table-column prop="subject" label="科目" width="100" align="center" />
        <el-table-column prop="difficultyLevel" label="难度" width="100" align="center">
          <template #default="{ row }">
            <el-tag size="small" :type="getDifficultyType(row.difficultyLevel)">
              {{ getDifficultyText(row.difficultyLevel) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="duration" label="时长(分钟)" width="100" align="center" />
        <el-table-column prop="instructorName" label="讲师" width="120" align="center" />
        <el-table-column label="统计" width="150" align="center">
          <template #default="{ row }">
            <div style="font-size: 12px; line-height: 1.6;">
              <div>浏览: {{ row.viewCount }}</div>
              <div>报名: {{ row.enrollCount }}</div>
              <div>完成: {{ row.completionCount }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="startDate" label="开始时间" width="160" align="center">
          <template #default="{ row }">
            {{ formatTime(row.startDate) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right" align="center">
          <template #default="{ row }">
            <el-button-group>
              <el-button size="small" @click="handleEdit(row)">编辑</el-button>
              <el-button
                size="small"
                :type="row.status === 1 ? 'warning' : 'success'"
                @click="handleToggleStatus(row)"
              >
                {{ row.status === 1 ? '下架' : '发布' }}
              </el-button>
              <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
            </el-button-group>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="pagination.current"
          :page-size="pagination.size"
          :total="pagination.total"
          @current-change="handlePageChange"
          layout="total, sizes, prev, pager, next, jumper"
          :page-sizes="[10, 20, 50, 100]"
          @size-change="handleSizeChange"
        />
      </div>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEditing ? '编辑课程' : '新增课程'"
      width="750px"
      destroy-on-close
    >
      <el-form
        ref="formRef"
        :model="courseForm"
        :rules="rules"
        label-width="120px"
        :disabled="formLoading"
        :inline="false"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="课程标题" prop="title">
              <el-input v-model="courseForm.title" placeholder="请输入课程标题" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="讲师姓名" prop="instructorName">
              <el-input v-model="courseForm.instructorName" placeholder="请输入讲师姓名" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="课程分类" prop="category">
              <el-select v-model="courseForm.category" placeholder="请选择分类" style="width: 100%">
                <el-option label="职业技能" value="职业技能" />
                <el-option label="安全教育" value="安全教育" />
                <el-option label="师德师风" value="师德师风" />
                <el-option label="专业技能" value="专业技能" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属科目" prop="subject">
              <el-select v-model="courseForm.subject" placeholder="请选择科目" style="width: 100%">
                <el-option label="语文" value="语文" />
                <el-option label="数学" value="数学" />
                <el-option label="英语" value="英语" />
                <el-option label="综合" value="综合" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="难度等级" prop="difficultyLevel">
              <el-select v-model="courseForm.difficultyLevel" placeholder="请选择难度" style="width: 100%">
                <el-option label="初级" value="beginner" />
                <el-option label="中级" value="intermediate" />
                <el-option label="高级" value="advanced" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="课程时长" prop="duration">
              <el-input-number
                v-model="courseForm.duration"
                :min="1"
                :max="9999"
                style="width: 100%"
                placeholder="分钟"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开始日期" prop="startDate">
              <el-date-picker
                v-model="courseForm.startDate"
                type="datetime"
                placeholder="选择开始时间"
                style="width: 100%"
                format="YYYY-MM-DD HH:mm:ss"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束日期" prop="endDate">
              <el-date-picker
                v-model="courseForm.endDate"
                type="datetime"
                placeholder="选择结束时间"
                style="width: 100%"
                format="YYYY-MM-DD HH:mm:ss"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="封面图片" prop="coverImage">
          <el-input v-model="courseForm.coverImage" placeholder="请输入图片URL（可选）" />
          <div style="margin-top: 8px;">
            <el-upload
              action="/api/upload"
              :show-file-list="false"
              :on-success="handleCoverUpload"
              :before-upload="beforeUpload"
              style="display: inline-block;"
            >
              <el-button size="small" type="primary">上传图片</el-button>
            </el-upload>
          </div>
        </el-form-item>

        <el-form-item label="视频URL" prop="videoUrl">
          <el-input v-model="courseForm.videoUrl" placeholder="请输入视频URL（可选）" />
        </el-form-item>

        <el-form-item label="课程描述" prop="description">
          <el-input
            v-model="courseForm.description"
            type="textarea"
            :rows="4"
            placeholder="请输入课程详细描述"
          />
        </el-form-item>

        <el-form-item label="目标受众" prop="targetAudience">
          <el-input
            v-model="courseForm.targetAudience"
            type="textarea"
            :rows="2"
            placeholder="例如：全体教师、新入职教师等"
          />
        </el-form-item>

        <el-form-item label="课程状态" prop="status">
          <el-radio-group v-model="courseForm.status">
            <el-radio :label="0">草稿</el-radio>
            <el-radio :label="1">已发布</el-radio>
            <el-radio :label="2">已下架</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="formLoading">
            {{ isEditing ? '保存修改' : '新增课程' }}
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search } from '@element-plus/icons-vue'
import { getAdminCourses, adminSaveCourse, getTrainingStatistics } from '@/api/training'

const loading = ref(false)
const formLoading = ref(false)
const dialogVisible = ref(false)
const isEditing = ref(false)
const courses = ref([])
const statistics = ref({
  totalCourses: 0,
  totalEnrollments: 0,
  completedCourses: 0,
  totalViews: 0
})

const filters = ref({
  category: '',
  subject: '',
  status: '',
  keyword: ''
})

const pagination = ref({
  current: 1,
  size: 10,
  total: 0
})

const courseForm = ref({
  id: null,
  title: '',
  description: '',
  category: '',
  subject: '',
  coverImage: '',
  videoUrl: '',
  duration: null,
  difficultyLevel: '',
  targetAudience: '',
  instructorName: '',
  status: 0,
  startDate: '',
  endDate: ''
})

const formRef = ref(null)

const rules = {
  title: [
    { required: true, message: '请输入课程标题', trigger: 'blur' },
    { min: 2, max: 100, message: '长度在 2 到 100 个字符', trigger: 'blur' }
  ],
  category: [{ required: true, message: '请选择课程分类', trigger: 'change' }],
  subject: [{ required: true, message: '请选择所属科目', trigger: 'change' }],
  difficultyLevel: [{ required: true, message: '请选择难度等级', trigger: 'change' }],
  duration: [{ required: true, message: '请输入课程时长', trigger: 'blur' }],
  instructorName: [{ required: true, message: '请输入讲师姓名', trigger: 'blur' }]
}

// 获取课程列表
const fetchCourses = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.value.current,
      size: pagination.value.size,
      ...filters.value
    }

    // 移除空值
    Object.keys(params).forEach(key => {
      if (params[key] === '' || params[key] === null) delete params[key]
    })

    console.log('请求参数:', params) // 调试日志
    const res = await getAdminCourses(params)
    console.log('课程列表响应:', res) // 调试日志

    if (res && res.records) {
      courses.value = res.records || []
      pagination.value.total = res.total || 0
    } else {
      courses.value = []
      pagination.value.total = 0
    }
  } catch (error) {
    console.error('获取课程列表失败:', error)
    ElMessage.error('获取课程列表失败')
    courses.value = []
    pagination.value.total = 0
  } finally {
    loading.value = false
  }
}

// 获取统计信息
const fetchStatistics = async () => {
  try {
    const res = await getTrainingStatistics()
    console.log('统计信息响应:', res) // 调试日志

    // 确保 res 存在且是对象
    if (res && typeof res === 'object') {
      statistics.value = res
    } else {
      console.warn('统计信息格式异常，使用默认值')
      statistics.value = {
        totalCourses: 0,
        totalEnrollments: 0,
        completedCourses: 0,
        totalViews: 0
      }
    }
  } catch (error) {
    console.error('获取统计信息失败:', error)
    // 出错时设置默认值，避免模板访问 undefined
    statistics.value = {
      totalCourses: 0,
      totalEnrollments: 0,
      completedCourses: 0,
      totalViews: 0
    }
  }
}

// 筛选处理
const handleFilter = () => {
  pagination.value.current = 1
  fetchCourses()
}

// 重置筛选
const resetFilters = () => {
  filters.value = { category: '', subject: '', status: '', keyword: '' }
  pagination.value.current = 1
  fetchCourses()
}

// 分页处理
const handlePageChange = (page) => {
  pagination.value.current = page
  fetchCourses()
}

const handleSizeChange = (size) => {
  pagination.value.size = size
  pagination.value.current = 1
  fetchCourses()
}

// 显示新增对话框
const showAddDialog = () => {
  isEditing.value = false
  courseForm.value = {
    id: null,
    title: '',
    description: '',
    category: '',
    subject: '',
    coverImage: '',
    videoUrl: '',
    duration: null,
    difficultyLevel: '',
    targetAudience: '',
    instructorName: '',
    status: 0,
    startDate: '',
    endDate: ''
  }
  dialogVisible.value = true
}

// 编辑课程
const handleEdit = (row) => {
  isEditing.value = true
  courseForm.value = { ...row }
  dialogVisible.value = true
}

// 切换状态
const handleToggleStatus = async (row) => {
  const newStatus = row.status === 1 ? 2 : 1
  const action = row.status === 1 ? '下架' : '发布'

  ElMessageBox.confirm(
    `确定要${action} "${row.title}" 吗？`,
    '状态切换',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const formData = { ...row, status: newStatus }
      await adminSaveCourse(formData)
      ElMessage.success(`${action}成功`)
      fetchCourses()
      fetchStatistics()
    } catch (error) {
      ElMessage.error(`${action}失败`)
    }
  }).catch(() => {})
}

// 删除课程
const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定要删除 "${row.title}" 吗？此操作不可恢复`,
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'danger'
    }
  ).then(async () => {
    try {
      // 调用删除API（需要后端支持，这里使用逻辑删除）
      const formData = { ...row, deleted: 1 }
      await adminSaveCourse(formData)
      ElMessage.success('删除成功')
      fetchCourses()
      fetchStatistics()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      formLoading.value = true
      try {
        // 处理日期格式
        const formData = {
          ...courseForm.value,
          startDate: courseForm.value.startDate ? new Date(courseForm.value.startDate).toISOString() : null,
          endDate: courseForm.value.endDate ? new Date(courseForm.value.endDate).toISOString() : null
        }

        await adminSaveCourse(formData)
        ElMessage.success(isEditing.value ? '修改成功' : '新增成功')
        dialogVisible.value = false
        fetchCourses()
        fetchStatistics()
      } catch (error) {
        ElMessage.error(isEditing.value ? '修改失败' : '新增失败')
      } finally {
        formLoading.value = false
      }
    }
  })
}

// 图片上传处理
const handleCoverUpload = (response, uploadFile) => {
  // 假设返回的URL在response.url（因为request拦截器会解包）
  if (response && response.url) {
    courseForm.value.coverImage = response.url
    ElMessage.success('图片上传成功')
  } else if (response && response.data && response.data.url) {
    // 兼容未解包的情况
    courseForm.value.coverImage = response.data.url
    ElMessage.success('图片上传成功')
  } else {
    // 模拟上传成功
    courseForm.value.coverImage = 'https://via.placeholder.com/800x450?text=Course+Cover'
    ElMessage.success('图片URL已设置（模拟）')
  }
}

const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!')
    return false
  }
  return true
}

// 工具函数
const getStatusType = (status) => {
  const map = { 0: 'info', 1: 'success', 2: 'warning' }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = { 0: '草稿', 1: '已发布', 2: '已下架' }
  return map[status] || '未知'
}

const getDifficultyType = (level) => {
  const map = { 'beginner': 'success', 'intermediate': 'warning', 'advanced': 'danger' }
  return map[level] || 'info'
}

const getDifficultyText = (level) => {
  const map = { 'beginner': '初级', 'intermediate': '中级', 'advanced': '高级' }
  return map[level] || level
}

const formatTime = (time) => {
  if (!time) return '-'
  return new Date(time).toLocaleString('zh-CN')
}

onMounted(() => {
  fetchCourses()
  fetchStatistics()
})
</script>

<style scoped>
.training-management {
  padding: 20px;
}

.header-card {
  margin-bottom: 20px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-content h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
}

/* 统计卡片样式 */
.stat-card {
  color: white;
  text-align: center;
  border-radius: 8px;
  overflow: hidden;
}

.stat-1 {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-2 {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stat-3 {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stat-4 {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-content {
  padding: 20px 10px;
}

.stat-number {
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  opacity: 0.95;
}

.filter-card {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

:deep(.el-table__header) {
  background: #f5f7fa !important;
}

:deep(.el-table__body) {
  font-size: 13px;
}

/* 对话框样式 */
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>
