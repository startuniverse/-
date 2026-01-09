<template>
  <div class="status-change">
    <!-- 申请按钮和统计 -->
    <el-row :gutter="20" style="margin-bottom: 20px;">
      <el-col :span="24">
        <el-card class="action-card">
          <div class="action-content">
            <div class="action-info">
              <h3>学籍异动申请</h3>
              <p>您可以在这里提交休学、转学、复学、退学等学籍异动申请</p>
            </div>
            <div class="action-buttons">
              <el-button type="primary" size="large" @click="showApplyDialog">
                <el-icon><Plus /></el-icon>
                新建申请
              </el-button>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 我的申请列表 -->
    <el-card>
      <template #header>
        <div class="card-header">
          <span>我的学籍异动申请</span>
          <div class="controls">
            <el-button type="primary" size="small" @click="loadMyApplications">刷新</el-button>
          </div>
        </div>
      </template>

      <el-table v-loading="loading" :data="tableData" border style="width: 100%">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="changeTypeDesc" label="异动类型" width="120" align="center">
          <template #default="scope">
            <el-tag :type="getTypeTag(scope.row.changeType)">{{ scope.row.changeTypeDesc }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="reason" label="申请原因" min-width="250" show-overflow-tooltip />
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
        <el-table-column prop="targetSchool" label="目标学校" width="150" align="center" show-overflow-tooltip />
        <el-table-column prop="statusDesc" label="审核状态" width="100" align="center">
          <template #default="scope">
            <el-tag :type="getStatusTag(scope.row.status)">{{ scope.row.statusDesc }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="approvalComment" label="审核意见" min-width="200" show-overflow-tooltip>
          <template #default="scope">
            {{ scope.row.approvalComment || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="approvalTime" label="审核时间" width="160" align="center">
          <template #default="scope">
            {{ formatDateTime(scope.row.approvalTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="申请时间" width="160" align="center">
          <template #default="scope">
            {{ formatDateTime(scope.row.createdAt) }}
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

    <!-- 申请对话框 -->
    <el-dialog
      v-model="applyDialogVisible"
      title="提交学籍异动申请"
      width="600px"
      @close="resetForm"
    >
      <el-form
        ref="applyFormRef"
        :model="applyForm"
        :rules="rules"
        label-width="120px"
      >
        <el-form-item label="异动类型" prop="changeType">
          <el-select v-model="applyForm.changeType" placeholder="请选择异动类型" style="width: 100%;">
            <el-option label="休学" :value="1"></el-option>
            <el-option label="转学" :value="2"></el-option>
            <el-option label="复学" :value="3"></el-option>
            <el-option label="退学" :value="4"></el-option>
            <el-option label="其他" :value="5"></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="开始日期" prop="startDate">
          <el-date-picker
            v-model="applyForm.startDate"
            type="date"
            placeholder="选择开始日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 100%;"
          />
        </el-form-item>

        <el-form-item v-if="applyForm.changeType === 1 || applyForm.changeType === 4" label="结束日期" prop="endDate">
          <el-date-picker
            v-model="applyForm.endDate"
            type="date"
            placeholder="选择结束日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 100%;"
          />
          <div class="form-tip">休学/退学需要填写结束日期</div>
        </el-form-item>

        <el-form-item v-if="applyForm.changeType === 2" label="目标学校" prop="targetSchool">
          <el-input v-model="applyForm.targetSchool" placeholder="请输入目标学校名称" />
          <div class="form-tip">转学需要填写目标学校</div>
        </el-form-item>

        <el-form-item label="申请原因" prop="reason">
          <el-input
            v-model="applyForm.reason"
            type="textarea"
            :rows="4"
            placeholder="请详细说明申请原因"
          />
        </el-form-item>

        <el-form-item>
          <div class="form-notice">
            <el-icon><Warning /></el-icon>
            <span>提交申请后，管理员将在3个工作日内审核，请耐心等待</span>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="applyDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">提交申请</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Warning } from '@element-plus/icons-vue'
import { applyStatusChange, getMyStatusChanges } from '@/api/campus'

const loading = ref(false)
const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const applyDialogVisible = ref(false)
const applyFormRef = ref()

const applyForm = ref({
  changeType: null,
  startDate: '',
  endDate: '',
  targetSchool: '',
  reason: ''
})

const rules = {
  changeType: [
    { required: true, message: '请选择异动类型', trigger: 'change' }
  ],
  startDate: [
    { required: true, message: '请选择开始日期', trigger: 'change' }
  ],
  endDate: [
    { required: true, message: '请选择结束日期', trigger: 'change' }
  ],
  targetSchool: [
    { required: true, message: '请输入目标学校', trigger: 'blur' }
  ],
  reason: [
    { required: true, message: '请输入申请原因', trigger: 'blur' },
    { min: 10, message: '原因描述至少10个字', trigger: 'blur' }
  ]
}

// 监听异动类型变化，动态调整校验规则
watch(() => applyForm.value.changeType, (newVal) => {
  if (applyFormRef.value) {
    applyFormRef.value.clearValidate()
  }

  // 动态设置必填规则
  if (newVal === 1 || newVal === 4) {
    // 休学或退学：需要结束日期
    rules.endDate = [{ required: true, message: '请选择结束日期', trigger: 'change' }]
  } else {
    rules.endDate = [{ required: false }]
  }

  if (newVal === 2) {
    // 转学：需要目标学校
    rules.targetSchool = [{ required: true, message: '请输入目标学校', trigger: 'blur' }]
  } else {
    rules.targetSchool = [{ required: false }]
  }
})

// 显示申请对话框
const showApplyDialog = () => {
  applyDialogVisible.value = true
  resetForm()
}

// 重置表单
const resetForm = () => {
  applyForm.value = {
    changeType: null,
    startDate: '',
    endDate: '',
    targetSchool: '',
    reason: ''
  }
  if (applyFormRef.value) {
    applyFormRef.value.clearValidate()
  }
}

// 提交申请
const handleSubmit = async () => {
  if (!applyFormRef.value) return

  await applyFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        // 构建提交数据
        const submitData = {
          changeType: applyForm.value.changeType,
          startDate: applyForm.value.startDate,
          reason: applyForm.value.reason
        }

        // 根据异动类型添加可选字段
        if (applyForm.value.changeType === 1 || applyForm.value.changeType === 4) {
          submitData.endDate = applyForm.value.endDate
        }

        if (applyForm.value.changeType === 2) {
          submitData.targetSchool = applyForm.value.targetSchool
        }

        await applyStatusChange(submitData)
        ElMessage.success('申请提交成功')
        applyDialogVisible.value = false
        loadMyApplications()
      } catch (error) {
        ElMessage.error('申请提交失败')
      }
    }
  })
}

// 加载我的申请列表
const loadMyApplications = async () => {
  try {
    loading.value = true
    const params = {
      current: currentPage.value,
      size: pageSize.value
    }
    const data = await getMyStatusChanges(params)
    tableData.value = data.records
    total.value = data.total
  } catch (error) {
    ElMessage.error('加载申请列表失败')
  } finally {
    loading.value = false
  }
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

// 分页处理
const handleSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1
  loadMyApplications()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  loadMyApplications()
}

onMounted(() => {
  loadMyApplications()
})
</script>

<style scoped>
.status-change {
  width: 100%;
}

.action-card {
  background: linear-gradient(135deg, #409EFF 0%, #337ecc 100%);
  color: white;
  border: none;
}

.action-card :deep(.el-card__header) {
  border-bottom: none;
}

.action-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
}

.action-info h3 {
  margin: 0 0 5px 0;
  font-size: 20px;
  font-weight: 600;
}

.action-info p {
  margin: 0;
  opacity: 0.9;
  font-size: 14px;
}

.action-buttons {
  display: flex;
  gap: 10px;
}

.action-buttons :deep(.el-button) {
  background: white;
  color: #409EFF;
  border: none;
  font-weight: 600;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.action-buttons :deep(.el-button:hover) {
  background: #f5f7fa;
  transform: translateY(-1px);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.controls {
  display: flex;
  gap: 8px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

.form-notice {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  background: #f0f9ff;
  border: 1px solid #91caff;
  border-radius: 6px;
  color: #0958d9;
  font-size: 13px;
}

.form-notice .el-icon {
  font-size: 16px;
}

/* 表单样式优化 */
.status-change :deep(.el-form-item__label) {
  font-weight: 600;
  color: #2c3e50;
}

.status-change :deep(.el-input__wrapper),
.status-change :deep(.el-textarea__inner) {
  background-color: #f8fafc;
  border: 1px solid rgba(64, 158, 255, 0.2);
  border-radius: 8px;
}

.status-change :deep(.el-input__wrapper:hover),
.status-change :deep(.el-textarea__inner:hover) {
  border-color: rgba(64, 158, 255, 0.5);
}

.status-change :deep(.el-input__wrapper.is-focus),
.status-change :deep(.el-textarea__inner:focus) {
  border-color: #409EFF;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

/* 对话框按钮样式 */
.status-change :deep(.el-button--primary) {
  background: linear-gradient(135deg, #409EFF 0%, #337ecc 100%);
  border: none;
  border-radius: 6px;
  font-weight: 600;
}

.status-change :deep(.el-button--primary:hover) {
  background: linear-gradient(135deg, #337ecc 0%, #2c68c8 100%);
}
</style>
