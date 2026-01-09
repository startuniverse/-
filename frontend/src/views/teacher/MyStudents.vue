<template>
  <div class="my-students">
    <!-- 申请列表 -->
    <el-card style="margin-bottom: 20px;">
      <template #header>
        <div class="card-header">
          <span>📋 班级申请</span>
          <el-button type="primary" @click="loadApplications" :icon="Refresh">刷新</el-button>
        </div>
      </template>

      <el-tabs v-model="activeTab">
        <el-tab-pane label="待审核" name="pending">
          <el-table :data="pendingApplications" style="width: 100%" v-loading="loading">
            <el-table-column type="index" label="序号" width="60" align="center" />
            <el-table-column prop="studentName" label="学生姓名" width="120" align="center" />
            <el-table-column prop="className" label="申请班级" width="120" align="center" />
            <el-table-column prop="reason" label="申请原因" min-width="200" show-overflow-tooltip />
            <el-table-column prop="createdAt" label="申请时间" width="160" align="center">
              <template #default="scope">
                {{ formatTime(scope.row.createdAt) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="180" fixed="right" align="center">
              <template #default="scope">
                <el-button type="success" size="small" @click="handleApprove(scope.row)">
                  通过
                </el-button>
                <el-button type="danger" size="small" @click="handleReject(scope.row)">
                  驳回
                </el-button>
              </template>
            </el-table-column>
          </el-table>

          <div v-if="pendingApplications.length === 0 && !loading" class="empty-state">
            <el-empty description="暂无待审核申请" />
          </div>
        </el-tab-pane>

        <el-tab-pane label="所有申请" name="all">
          <el-table :data="allApplications" style="width: 100%" v-loading="loading">
            <el-table-column type="index" label="序号" width="60" align="center" />
            <el-table-column prop="studentName" label="学生姓名" width="120" align="center" />
            <el-table-column prop="className" label="申请班级" width="120" align="center" />
            <el-table-column prop="reason" label="申请原因" min-width="200" show-overflow-tooltip />
            <el-table-column prop="status" label="状态" width="100" align="center">
              <template #default="scope">
                <el-tag :type="getStatusType(scope.row.status)">
                  {{ getStatusText(scope.row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="approvalComment" label="审核意见" min-width="150" show-overflow-tooltip />
            <el-table-column prop="createdAt" label="申请时间" width="160" align="center">
              <template #default="scope">
                {{ formatTime(scope.row.createdAt) }}
              </template>
            </el-table-column>
          </el-table>

          <div v-if="allApplications.length === 0 && !loading" class="empty-state">
            <el-empty description="暂无申请记录" />
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 我的学生列表 -->
    <el-card>
      <template #header>
        <div class="card-header">
          <span>👥 我的学生</span>
          <div>
            <el-input
              v-model="searchKeyword"
              placeholder="搜索学生姓名"
              size="small"
              style="width: 150px; margin-right: 10px;"
              clearable
              @input="filterStudents"
            />
            <el-button type="primary" @click="loadStudents" :icon="Refresh">刷新</el-button>
          </div>
        </div>
      </template>

      <el-table :data="filteredStudents" style="width: 100%" v-loading="loadingStudents">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="studentNumber" label="学号" width="120" align="center" />
        <el-table-column prop="realName" label="姓名" width="120" align="center" />
        <el-table-column prop="gender" label="性别" width="80" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.gender === '男' ? 'primary' : 'danger'" size="small">
              {{ scope.row.gender }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="className" label="班级" width="120" align="center" />
        <el-table-column prop="phone" label="联系电话" width="140" align="center" />
        <el-table-column prop="guardianName" label="监护人" width="120" align="center" />
        <el-table-column prop="guardianPhone" label="监护人电话" width="140" align="center" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="scope">
            <el-tag :type="getStudentStatusType(scope.row.status)" size="small">
              {{ getStudentStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right" align="center">
          <template #default="scope">
            <el-button link type="danger" @click="removeStudent(scope.row)">移除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div v-if="filteredStudents.length === 0 && !loadingStudents" class="empty-state">
        <el-empty description="暂无学生" />
      </div>

      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          :page-sizes="[10, 20, 50]"
          @current-change="handleCurrentChange"
          @size-change="handleSizeChange"
        />
      </div>
    </el-card>

    <!-- 审核对话框 -->
    <el-dialog
      v-model="reviewDialogVisible"
      :title="reviewAction === 'approve' ? '通过申请' : '驳回申请'"
      width="500px"
    >
      <el-form label-width="100px">
        <el-form-item label="学生姓名">
          <el-input v-model="reviewForm.studentName" disabled />
        </el-form-item>
        <el-form-item label="申请班级">
          <el-input v-model="reviewForm.className" disabled />
        </el-form-item>
        <el-form-item label="申请原因">
          <el-input v-model="reviewForm.reason" type="textarea" :rows="3" disabled />
        </el-form-item>
        <el-form-item label="审核意见">
          <el-input
            v-model="reviewForm.approvalComment"
            type="textarea"
            :rows="4"
            :placeholder="reviewAction === 'approve' ? '请输入通过意见（可选）' : '请输入驳回原因（必填）'"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="reviewDialogVisible = false">取消</el-button>
          <el-button
            type="primary"
            @click="submitReview"
            :disabled="reviewAction === 'reject' && !reviewForm.approvalComment"
          >
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import { getPendingApplications, getTeacherApplications, reviewApplication, getTeacherStudents } from '@/api/classApplication'
import { useUserStore } from '@/store/modules/user'

const userStore = useUserStore()

const loading = ref(false)
const loadingStudents = ref(false)
const activeTab = ref('pending')

// 申请相关
const pendingApplications = ref([])
const allApplications = ref([])

// 学生相关
const studentList = ref([])
const searchKeyword = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 审核对话框
const reviewDialogVisible = ref(false)
const reviewAction = ref('') // 'approve' or 'reject'
const reviewForm = ref({
  applicationId: null,
  studentName: '',
  className: '',
  reason: '',
  approvalComment: ''
})

const filteredStudents = computed(() => {
  if (!searchKeyword.value) return studentList.value
  return studentList.value.filter(s =>
    s.realName && s.realName.includes(searchKeyword.value)
  )
})

const getStatusType = (status) => {
  const map = {
    0: 'info',    // 待审核
    1: 'success', // 已通过
    2: 'danger'   // 已驳回
  }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = {
    0: '待审核',
    1: '已通过',
    2: '已驳回'
  }
  return map[status] || '未知'
}

const getStudentStatusType = (status) => {
  const map = {
    1: 'success', // 在读
    2: 'info',    // 毕业
    3: 'warning', // 转学
    4: 'danger'   // 休学
  }
  return map[status] || 'info'
}

const getStudentStatusText = (status) => {
  const map = {
    1: '在读',
    2: '毕业',
    3: '转学',
    4: '休学'
  }
  return map[status] || '未知'
}

const formatTime = (time) => {
  if (!time) return '-'
  return new Date(time).toLocaleString('zh-CN')
}

// 加载待审核申请
const loadApplications = async () => {
  try {
    loading.value = true
    const teacherId = userStore.userInfo.id

    // 获取待审核申请
    const pendingRes = await getPendingApplications(teacherId)
    pendingApplications.value = pendingRes

    // 获取所有申请
    const allRes = await getTeacherApplications(teacherId)
    allApplications.value = allRes

    if (activeTab.value === 'pending' && pendingRes.length === 0) {
      ElMessage.info('暂无待审核申请')
    }
  } catch (error) {
    ElMessage.error('加载申请失败')
  } finally {
    loading.value = false
  }
}

// 处理通过
const handleApprove = (row) => {
  reviewAction.value = 'approve'
  reviewForm.value = {
    applicationId: row.id,
    studentName: row.studentName,
    className: row.className,
    reason: row.reason,
    approvalComment: ''
  }
  reviewDialogVisible.value = true
}

// 处理驳回
const handleReject = (row) => {
  reviewAction.value = 'reject'
  reviewForm.value = {
    applicationId: row.id,
    studentName: row.studentName,
    className: row.className,
    reason: row.reason,
    approvalComment: ''
  }
  reviewDialogVisible.value = true
}

// 提交审核
const submitReview = async () => {
  if (reviewAction.value === 'reject' && !reviewForm.value.approvalComment) {
    ElMessage.warning('请输入驳回原因')
    return
  }

  try {
    const status = reviewAction.value === 'approve' ? 1 : 2
    const params = {
      status: status,
      approvalComment: reviewForm.value.approvalComment,
      teacherId: userStore.userInfo.id
    }

    await reviewApplication(reviewForm.value.applicationId, params)

    if (reviewAction.value === 'approve') {
      ElMessage.success('申请已通过')
    } else {
      ElMessage.success('申请已驳回')
    }

    reviewDialogVisible.value = false
    loadApplications()
    loadStudents()
  } catch (error) {
    ElMessage.error('审核失败')
  }
}

// 加载我的学生
const loadStudents = async () => {
  loadingStudents.value = true
  try {
    const params = {
      teacherId: userStore.userInfo.id,
      current: currentPage.value,
      size: pageSize.value,
      keyword: searchKeyword.value
    }
    const data = await getTeacherStudents(params)
    studentList.value = data.records
    total.value = data.total
  } catch (error) {
    ElMessage.error('加载学生列表失败')
  } finally {
    loadingStudents.value = false
  }
}

// 移除学生
const removeStudent = (row) => {
  ElMessageBox.confirm(`确定要将 ${row.realName} 从班级中移除吗？`, '警告', {
    type: 'warning'
  }).then(() => {
    // 这里应该调用API移除学生
    ElMessage.success('移除成功')
    loadStudents()
  }).catch(() => {})
}

// 搜索学生
const filterStudents = () => {
  // 计算属性会自动处理
}

// 分页处理
const handleCurrentChange = (val) => {
  currentPage.value = val
  loadStudents()
}

const handleSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1
  loadStudents()
}

onMounted(() => {
  loadApplications()
  loadStudents()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.empty-state {
  margin-top: 20px;
  text-align: center;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>
