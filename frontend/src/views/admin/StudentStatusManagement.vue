<template>
  <div class="status-management">
    <!-- 统计卡片 -->
    <el-row :gutter="20" style="margin-bottom: 20px;">
      <el-col :span="6" :xs="24">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-value">{{ statistics.total || 0 }}</div>
              <div class="stat-label">总申请数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6" :xs="24">
        <el-card class="stat-card pending" shadow="hover">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-value">{{ statistics.pending || 0 }}</div>
              <div class="stat-label">待审核</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6" :xs="24">
        <el-card class="stat-card approved" shadow="hover">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-value">{{ statistics.approved || 0 }}</div>
              <div class="stat-label">已通过</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6" :xs="24">
        <el-card class="stat-card rejected" shadow="hover">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-value">{{ statistics.rejected || 0 }}</div>
              <div class="stat-label">已驳回</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card>
      <template #header>
        <div class="card-header">
          <span>学籍异动管理</span>
          <div class="controls">
            <el-input
              v-model="searchParams.studentName"
              placeholder="搜索学生姓名"
              size="small"
              style="width: 150px;"
              clearable
              @keyup.enter="loadStatusChanges"
            />
            <el-select
              v-model="searchParams.changeType"
              placeholder="异动类型"
              size="small"
              style="width: 120px;"
              clearable
            >
              <el-option label="休学" :value="1"></el-option>
              <el-option label="转学" :value="2"></el-option>
              <el-option label="复学" :value="3"></el-option>
              <el-option label="退学" :value="4"></el-option>
              <el-option label="其他" :value="5"></el-option>
            </el-select>
            <el-select
              v-model="searchParams.status"
              placeholder="审核状态"
              size="small"
              style="width: 120px;"
              clearable
            >
              <el-option label="待审核" :value="0"></el-option>
              <el-option label="已通过" :value="1"></el-option>
              <el-option label="已驳回" :value="2"></el-option>
            </el-select>
            <el-button type="primary" size="small" @click="loadStatusChanges">查询</el-button>
            <el-button type="success" size="small" @click="refreshStatistics">刷新统计</el-button>
          </div>
        </div>
      </template>

      <el-table v-loading="loading" :data="tableData" border style="width: 100%">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="studentName" label="学生姓名" width="120" align="center" />
        <el-table-column prop="studentNumber" label="学号" width="120" align="center" />
        <el-table-column prop="className" label="班级" width="120" align="center" />
        <el-table-column prop="changeTypeDesc" label="异动类型" width="100" align="center">
          <template #default="scope">
            <el-tag :type="getTypeTag(scope.row.changeType)">{{ scope.row.changeTypeDesc }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="reason" label="申请原因" min-width="200" show-overflow-tooltip />
        <el-table-column prop="startDate" label="开始日期" width="120" align="center">
          <template #default="scope">
            {{ formatDate(scope.row.startDate) }}
          </template>
        </el-table-column>
        <el-table-column prop="endDate" label="结束日期" width="120" align="center">
          <template #default="scope">
            {{ formatDate(scope.row.endDate) }}
          </template>
        </el-table-column>
        <el-table-column prop="statusDesc" label="状态" width="100" align="center">
          <template #default="scope">
            <el-tag :type="getStatusTag(scope.row.status)">{{ scope.row.statusDesc }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="applicantName" label="申请人" width="100" align="center" />
        <el-table-column prop="createdAt" label="申请时间" width="160" align="center">
          <template #default="scope">
            {{ formatDateTime(scope.row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" align="center" fixed="right">
          <template #default="scope">
            <el-button
              v-if="scope.row.status === 0"
              type="primary"
              size="small"
              @click="handleApprove(scope.row)"
            >
              审核
            </el-button>
            <el-button
              v-if="scope.row.status === 0"
              type="danger"
              size="small"
              @click="handleReject(scope.row)"
            >
              驳回
            </el-button>
            <el-button
              v-if="scope.row.status !== 0"
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

    <!-- 审核对话框 -->
    <el-dialog
      v-model="approveDialogVisible"
      title="审核学籍异动申请"
      width="500px"
    >
      <el-form
        ref="approveFormRef"
        :model="approveForm"
        label-width="100px"
      >
        <el-form-item label="学生姓名">
          <span>{{ approveForm.studentName }}</span>
        </el-form-item>
        <el-form-item label="异动类型">
          <span>{{ approveForm.changeTypeDesc }}</span>
        </el-form-item>
        <el-form-item label="申请原因">
          <span>{{ approveForm.reason }}</span>
        </el-form-item>
        <el-form-item label="开始日期">
          <span>{{ formatDate(approveForm.startDate) }}</span>
        </el-form-item>
        <el-form-item v-if="approveForm.endDate" label="结束日期">
          <span>{{ formatDate(approveForm.endDate) }}</span>
        </el-form-item>
        <el-form-item v-if="approveForm.targetSchool" label="目标学校">
          <span>{{ approveForm.targetSchool }}</span>
        </el-form-item>
        <el-form-item label="审核意见" prop="approvalComment">
          <el-input
            v-model="approveForm.approvalComment"
            type="textarea"
            :rows="3"
            placeholder="请输入审核意见（可选）"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="approveDialogVisible = false">取消</el-button>
          <el-button type="danger" @click="confirmReject">驳回</el-button>
          <el-button type="success" @click="confirmApprove">通过</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getStatusChanges, approveStatusChange, deleteStatusChange, getStatusChangeStatistics } from '@/api/admin'

const loading = ref(false)
const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const searchParams = ref({
  studentName: '',
  changeType: null,
  status: null
})

const statistics = ref({
  total: 0,
  pending: 0,
  approved: 0,
  rejected: 0
})

const approveDialogVisible = ref(false)
const approveForm = ref({
  id: null,
  studentName: '',
  changeTypeDesc: '',
  reason: '',
  startDate: '',
  endDate: '',
  targetSchool: '',
  approvalComment: ''
})

// 加载学籍异动列表
const loadStatusChanges = async () => {
  try {
    loading.value = true
    const params = {
      current: currentPage.value,
      size: pageSize.value,
      ...searchParams.value
    }
    // 移除空值参数
    Object.keys(params).forEach(key => {
      if (params[key] === null || params[key] === '') {
        delete params[key]
      }
    })

    const data = await getStatusChanges(params)
    tableData.value = data.records
    total.value = data.total
  } catch (error) {
    ElMessage.error('加载学籍异动列表失败')
  } finally {
    loading.value = false
  }
}

// 加载统计信息
const loadStatistics = async () => {
  try {
    const data = await getStatusChangeStatistics()
    statistics.value = data
  } catch (error) {
    console.error('加载统计信息失败:', error)
  }
}

// 刷新统计
const refreshStatistics = () => {
  loadStatistics()
  loadStatusChanges()
}

// 获取类型标签样式
const getTypeTag = (type) => {
  const map = { 1: 'warning', 2: 'primary', 3: 'success', 4: 'danger', 5: 'info' }
  return map[type] || 'info'
}

// 获取状态标签样式
const getStatusTag = (status) => {
  const map = { 0: 'warning', 1: 'success', 2: 'danger' }
  return map[status] || 'info'
}

// 格式化日期
const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleDateString('zh-CN')
}

// 格式化日期时间
const formatDateTime = (datetime) => {
  if (!datetime) return '-'
  return new Date(datetime).toLocaleString('zh-CN')
}

// 处理审核（通过）
const handleApprove = (row) => {
  approveForm.value = {
    id: row.id,
    studentName: row.studentName,
    changeTypeDesc: row.changeTypeDesc,
    reason: row.reason,
    startDate: row.startDate,
    endDate: row.endDate,
    targetSchool: row.targetSchool,
    approvalComment: ''
  }
  approveDialogVisible.value = true
}

// 处理驳回（直接驳回）
const handleReject = (row) => {
  ElMessageBox.prompt('请输入驳回原因', '驳回申请', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputType: 'textarea'
  }).then(({ value }) => {
    confirmApproveReject(row.id, 2, value)
  }).catch(() => {})
}

// 确认通过
const confirmApprove = async () => {
  try {
    await approveStatusChange({
      id: approveForm.value.id,
      status: 1,
      approvalComment: approveForm.value.approvalComment
    })
    ElMessage.success('审核通过')
    approveDialogVisible.value = false
    loadStatusChanges()
    loadStatistics()
  } catch (error) {
    ElMessage.error('审核失败')
  }
}

// 确认驳回（从对话框）
const confirmReject = async () => {
  if (!approveForm.value.approvalComment) {
    ElMessage.warning('请输入驳回原因')
    return
  }
  try {
    await approveStatusChange({
      id: approveForm.value.id,
      status: 2,
      approvalComment: approveForm.value.approvalComment
    })
    ElMessage.success('已驳回')
    approveDialogVisible.value = false
    loadStatusChanges()
    loadStatistics()
  } catch (error) {
    ElMessage.error('驳回失败')
  }
}

// 统一的审核/驳回方法
const confirmApproveReject = async (id, status, comment) => {
  try {
    await approveStatusChange({ id, status, approvalComment: comment })
    ElMessage.success(status === 1 ? '审核通过' : '已驳回')
    loadStatusChanges()
    loadStatistics()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

// 处理删除
const handleDelete = async (row) => {
  ElMessageBox.confirm(`确定要删除该申请吗？`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteStatusChange(row.id)
      ElMessage.success('删除成功')
      loadStatusChanges()
      loadStatistics()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

// 分页处理
const handleSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1
  loadStatusChanges()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  loadStatusChanges()
}

onMounted(() => {
  loadStatusChanges()
  loadStatistics()
})
</script>

<style scoped>
.status-management {
  width: 100%;
}

.stat-card {
  margin-bottom: 20px;
  min-height: 100px;
  background: linear-gradient(135deg, #409EFF 0%, #337ecc 100%);
  color: white;
}

.stat-card.pending {
  background: linear-gradient(135deg, #E6A23C 0%, #c7872c 100%);
}

.stat-card.approved {
  background: linear-gradient(135deg, #67C23A 0%, #529b2e 100%);
}

.stat-card.rejected {
  background: linear-gradient(135deg, #F56C6C 0%, #dd5454 100%);
}

.stat-card .stat-content {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 80px;
}

.stat-card .stat-info {
  text-align: center;
}

.stat-card .stat-value {
  font-size: 32px;
  font-weight: bold;
  margin-bottom: 5px;
}

.stat-card .stat-label {
  font-size: 14px;
  opacity: 0.9;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.controls {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>
