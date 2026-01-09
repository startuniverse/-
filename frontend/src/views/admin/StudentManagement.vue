<template>
  <div class="student-management">
    <!-- 统计卡片 -->
    <el-row :gutter="20" style="margin-bottom: 20px;">
      <el-col :span="6">
        <el-card class="stat-card stat-1" shadow="hover">
          <div class="stat-content">
            <div class="stat-number">{{ statistics.totalStudents || 0 }}</div>
            <div class="stat-label">总学生数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card stat-2" shadow="hover">
          <div class="stat-content">
            <div class="stat-number">{{ statistics.inSchool || 0 }}</div>
            <div class="stat-label">在读学生</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card stat-3" shadow="hover">
          <div class="stat-content">
            <div class="stat-number">{{ statistics.pendingApplications || 0 }}</div>
            <div class="stat-label">待审核申请</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card stat-4" shadow="hover">
          <div class="stat-content">
            <div class="stat-number">{{ statistics.classes || 0 }}</div>
            <div class="stat-label">班级总数</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-tabs v-model="activeTab" type="card" style="margin-bottom: 20px;" @tab-change="handleTabChange">
      <el-tab-pane label="学生列表" name="students">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>学生管理</span>
              <div class="controls">
                <el-input
                  v-model="searchKeyword"
                  placeholder="搜索学生姓名或学号"
                  size="small"
                  style="width: 200px;"
                  clearable
                  @keyup.enter="loadStudents"
                />
                <el-button type="primary" size="small" @click="loadStudents" :icon="Search">查询</el-button>
                <el-button type="success" size="small" @click="handleAdd" :icon="Plus">新增</el-button>
                <el-button type="info" size="small" @click="refreshData" :icon="Refresh">刷新</el-button>
              </div>
            </div>
          </template>

          <el-table v-loading="loading" :data="studentsData" border style="width: 100%">
            <el-table-column type="index" label="序号" width="60" align="center" />
            <el-table-column prop="studentNumber" label="学号" width="120" align="center" />
            <el-table-column prop="name" label="姓名" width="120" align="center" />
            <el-table-column prop="className" label="班级" width="120" align="center" />
            <el-table-column prop="guardianName" label="监护人" width="120" align="center" />
            <el-table-column prop="guardianPhone" label="监护人电话" width="140" align="center" />
            <el-table-column prop="enrollmentDate" label="入学日期" width="120" align="center">
              <template #default="scope">
                {{ formatDate(scope.row.enrollmentDate) }}
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100" align="center">
              <template #default="scope">
                <el-tag :type="getStatusTag(scope.row.status)">
                  {{ getStatusText(scope.row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="180" align="center" fixed="right">
              <template #default="scope">
                <el-button type="primary" link size="small" @click="handleEdit(scope.row)">
                  编辑
                </el-button>
                <el-button type="danger" link size="small" @click="handleDelete(scope.row)">
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
      </el-tab-pane>

      <el-tab-pane label="班级申请" name="applications">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>班级申请审核</span>
              <div class="controls">
                <el-button type="primary" size="small" @click="loadApplications" :icon="Refresh">刷新</el-button>
              </div>
            </div>
          </template>

          <el-table v-loading="loading" :data="applicationsData" border style="width: 100%">
            <el-table-column type="index" label="序号" width="60" align="center" />
            <el-table-column prop="studentName" label="学生姓名" width="120" align="center" />
            <el-table-column prop="className" label="申请班级" width="120" align="center" />
            <el-table-column prop="teacherName" label="班主任" width="120" align="center" />
            <el-table-column prop="reason" label="申请原因" min-width="200" show-overflow-tooltip />
            <el-table-column prop="status" label="状态" width="100" align="center">
              <template #default="scope">
                <el-tag :type="getApplicationStatusTag(scope.row.status)">
                  {{ getApplicationStatusText(scope.row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="approvalComment" label="审核意见" min-width="150" show-overflow-tooltip />
            <el-table-column prop="createdAt" label="申请时间" width="160" align="center">
              <template #default="scope">
                {{ formatTime(scope.row.createdAt) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120" align="center" fixed="right">
              <template #default="scope">
                <el-button v-if="scope.row.status === 0" type="primary" size="small" @click="handleViewApplication(scope.row)">
                  查看
                </el-button>
                <el-button v-else link type="info" size="small" @click="handleViewApplication(scope.row)">
                  详情
                </el-button>
              </template>
            </el-table-column>
          </el-table>

          <div v-if="applicationsData.length === 0 && !loading" class="empty-state">
            <el-empty description="暂无申请记录" />
          </div>

          <div class="pagination">
            <el-pagination
              v-model:current-page="appCurrentPage"
              v-model:page-size="appPageSize"
              :total="appTotal"
              :page-sizes="[10, 20, 50]"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handleAppSizeChange"
              @current-change="handleAppCurrentChange"
            />
          </div>
        </el-card>
      </el-tab-pane>
    </el-tabs>

    <!-- 编辑/新增学生对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑学生' : '新增学生'"
      width="500px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="学号" prop="studentNumber">
          <el-input v-model="form.studentNumber" placeholder="请输入学号" />
        </el-form-item>
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="form.realName" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="班级" prop="classId">
          <el-select v-model="form.classId" placeholder="请选择班级" style="width: 100%;">
            <el-option
              v-for="classItem in classOptions"
              :key="classItem.id"
              :label="classItem.className"
              :value="classItem.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="监护人" prop="guardianName">
          <el-input v-model="form.guardianName" placeholder="请输入监护人姓名" />
        </el-form-item>
        <el-form-item label="联系电话" prop="guardianPhone">
          <el-input v-model="form.guardianPhone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态" style="width: 100%;">
            <el-option label="在读" :value="1"></el-option>
            <el-option label="毕业" :value="2"></el-option>
            <el-option label="转学" :value="3"></el-option>
            <el-option label="休学" :value="4"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 申请详情对话框 -->
    <el-dialog
      v-model="appDialogVisible"
      title="申请详情"
      width="500px"
    >
      <el-form label-width="100px" v-if="currentApplication">
        <el-form-item label="学生姓名">
          <el-input v-model="currentApplication.studentName" disabled />
        </el-form-item>
        <el-form-item label="申请班级">
          <el-input v-model="currentApplication.className" disabled />
        </el-form-item>
        <el-form-item label="班主任">
          <el-input v-model="currentApplication.teacherName" disabled />
        </el-form-item>
        <el-form-item label="申请原因">
          <el-input v-model="currentApplication.reason" type="textarea" :rows="3" disabled />
        </el-form-item>
        <el-form-item label="状态">
          <el-tag :type="getApplicationStatusTag(currentApplication.status)">
            {{ getApplicationStatusText(currentApplication.status) }}
          </el-tag>
        </el-form-item>
        <el-form-item v-if="currentApplication.approvalComment" label="审核意见">
          <el-input v-model="currentApplication.approvalComment" type="textarea" :rows="3" disabled />
        </el-form-item>
        <el-form-item label="申请时间">
          <el-input :value="formatTime(currentApplication.createdAt)" disabled />
        </el-form-item>
        <el-form-item v-if="currentApplication.approvalTime" label="审核时间">
          <el-input :value="formatTime(currentApplication.approvalTime)" disabled />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="appDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus, Refresh } from '@element-plus/icons-vue'
import {
  getStudents,
  addStudent,
  updateStudent,
  deleteStudent,
  getStudentStatistics,
  getAllApplications,
  getClassStatistics
} from '@/api/admin'

const activeTab = ref('students')
const loading = ref(false)
const searchKeyword = ref('')
const studentsData = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 统计信息
const statistics = ref({
  totalStudents: 0,
  inSchool: 0,
  pendingApplications: 0,
  classes: 0
})

// 申请相关
const applicationsData = ref([])
const appCurrentPage = ref(1)
const appPageSize = ref(10)
const appTotal = ref(0)

// 对话框相关
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const form = ref({
  studentNumber: '',
  realName: '',
  classId: null,
  guardianName: '',
  guardianPhone: '',
  status: 1
})

const rules = {
  studentNumber: [{ required: true, message: '请输入学号', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  classId: [{ required: true, message: '请输入班级ID', trigger: 'blur' }]
}

// 班级选项（实际应该从后端获取）
const classOptions = ref([
  { id: 1, className: '一年级1班' },
  { id: 2, className: '一年级2班' },
  { id: 3, className: '二年级1班' },
  { id: 4, className: '二年级2班' },
  { id: 5, className: '三年级1班' }
])

const getStatusText = (status) => {
  const map = { 1: '在读', 2: '毕业', 3: '转学', 4: '休学' }
  return map[status] || '未知'
}

const getStatusTag = (status) => {
  const map = { 1: 'success', 2: 'info', 3: 'warning', 4: 'danger' }
  return map[status] || 'info'
}

const getApplicationStatusText = (status) => {
  const map = { 0: '待审核', 1: '已通过', 2: '已驳回' }
  return map[status] || '未知'
}

const getApplicationStatusTag = (status) => {
  const map = { 0: 'info', 1: 'success', 2: 'danger' }
  return map[status] || 'info'
}

const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleDateString('zh-CN')
}

const formatTime = (time) => {
  if (!time) return '-'
  return new Date(time).toLocaleString('zh-CN')
}

// 加载统计信息
const loadStatistics = async () => {
  try {
    const [studentStats, classStats, appStats] = await Promise.all([
      getStudentStatistics(),
      getClassStatistics(),
      getAllApplications({ current: 1, size: 9999, status: 0 })
    ])

    statistics.value = {
      totalStudents: studentStats.total || 0,
      inSchool: studentStats.inSchool || 0,
      pendingApplications: appStats.total || 0,
      classes: classStats.total || 0
    }
  } catch (error) {
    console.error('加载统计信息失败:', error)
  }
}

// 加载学生列表
const loadStudents = async () => {
  try {
    loading.value = true
    const params = {
      current: currentPage.value,
      size: pageSize.value,
      keyword: searchKeyword.value
    }
    const data = await getStudents(params)
    studentsData.value = data.records
    total.value = data.total
  } catch (error) {
    ElMessage.error('加载学生列表失败')
  } finally {
    loading.value = false
  }
}

// 加载申请列表
const loadApplications = async () => {
  try {
    loading.value = true
    const params = {
      current: appCurrentPage.value,
      size: appPageSize.value,
      status: null  // 获取所有状态
    }
    const data = await getAllApplications(params)
    applicationsData.value = data.records
    appTotal.value = data.total
  } catch (error) {
    ElMessage.error('加载申请列表失败')
  } finally {
    loading.value = false
  }
}

const refreshData = () => {
  loadStudents()
  loadStatistics()
  ElMessage.success('刷新成功')
}

const handleAdd = () => {
  isEdit.value = false
  form.value = {
    studentNumber: '',
    realName: '',
    classId: null,
    guardianName: '',
    guardianPhone: '',
    status: 1
  }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  form.value = {
    id: row.id,
    studentNumber: row.studentNumber,
    realName: row.name,
    classId: row.classId,
    guardianName: row.guardianName,
    guardianPhone: row.guardianPhone,
    status: row.status
  }
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  ElMessageBox.confirm(`确定要删除学生 ${row.name} 吗？`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteStudent(row.id)
      ElMessage.success('删除成功')
      loadStudents()
      loadStatistics()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const submitData = {
          name: form.value.realName,
          studentNumber: form.value.studentNumber,
          classId: form.value.classId,
          guardianName: form.value.guardianName,
          guardianPhone: form.value.guardianPhone,
          status: form.value.status
        }

        if (isEdit.value) {
          submitData.id = form.value.id
          await updateStudent(submitData)
          ElMessage.success('更新成功')
        } else {
          await addStudent(submitData)
          ElMessage.success('新增成功')
        }

        dialogVisible.value = false
        loadStudents()
        loadStatistics()
      } catch (error) {
        ElMessage.error(isEdit.value ? '更新失败' : '新增失败')
      }
    }
  })
}

const handleSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1
  loadStudents()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  loadStudents()
}

const handleAppSizeChange = (val) => {
  appPageSize.value = val
  appCurrentPage.value = 1
  loadApplications()
}

const handleAppCurrentChange = (val) => {
  appCurrentPage.value = val
  loadApplications()
}

const handleViewApplication = (row) => {
  // 这里可以扩展为查看申请详情
  ElMessageBox.alert(
    `<div style="line-height: 1.8;">
      <p><strong>学生姓名:</strong> ${row.studentName}</p>
      <p><strong>申请班级:</strong> ${row.className}</p>
      <p><strong>班主任:</strong> ${row.teacherName}</p>
      <p><strong>申请原因:</strong> ${row.reason}</p>
      <p><strong>状态:</strong> ${getApplicationStatusText(row.status)}</p>
      ${row.approvalComment ? `<p><strong>审核意见:</strong> ${row.approvalComment}</p>` : ''}
      <p><strong>申请时间:</strong> ${formatTime(row.createdAt)}</p>
      ${row.approvalTime ? `<p><strong>审核时间:</strong> ${formatTime(row.approvalTime)}</p>` : ''}
    </div>`,
    '申请详情',
    {
      dangerouslyUseHTMLString: true,
      confirmButtonText: '关闭'
    }
  )
}

// 监听标签页切换
const handleTabChange = (tab) => {
  if (tab === 'applications') {
    loadApplications()
  }
}

onMounted(() => {
  loadStatistics()
  loadStudents()
})
</script>

<style scoped>
.student-management {
  width: 100%;
}

.stat-card {
  text-align: center;
  padding: 20px;
}

.stat-content {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-number {
  font-size: 28px;
  font-weight: bold;
  color: #409EFF;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.stat-1 .stat-number { color: #409EFF; }
.stat-2 .stat-number { color: #67C23A; }
.stat-3 .stat-number { color: #E6A23C; }
.stat-4 .stat-number { color: #909399; }

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

.empty-state {
  margin-top: 20px;
  text-align: center;
}
</style>
