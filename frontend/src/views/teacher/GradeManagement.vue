<template>
  <div class="grade-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>📊 成绩管理</span>
          <div class="header-actions">
            <el-button type="primary" icon="Plus" @click="showAddDialog">添加成绩</el-button>
            <el-button type="success" icon="Download" @click="exportGrades">导出成绩</el-button>
          </div>
        </div>
      </template>

      <div class="filter-bar">
        <el-select v-model="filter.studentId" placeholder="选择学生" clearable filterable @change="handleFilterChange">
          <el-option
            v-for="student in studentList"
            :key="student.id"
            :label="`${student.realName} (${student.studentNumber})`"
            :value="student.id"
          />
        </el-select>

        <el-select v-model="filter.subject" placeholder="选择科目" clearable @change="handleFilterChange" style="margin-left: 10px;">
          <el-option label="数学" value="数学" />
          <el-option label="语文" value="语文" />
          <el-option label="英语" value="英语" />
          <el-option label="物理" value="物理" />
          <el-option label="化学" value="化学" />
          <el-option label="生物" value="生物" />
          <el-option label="政治" value="政治" />
          <el-option label="历史" value="历史" />
          <el-option label="地理" value="地理" />
        </el-select>

        <el-select v-model="filter.examType" placeholder="考试类型" clearable @change="handleFilterChange" style="margin-left: 10px;">
          <el-option label="日常" value="日常" />
          <el-option label="期中" value="期中" />
          <el-option label="期末" value="期末" />
        </el-select>

        <el-button type="primary" style="margin-left: 10px;" @click="handleFilterChange">查询</el-button>
        <el-button type="info" @click="resetFilter">重置</el-button>
      </div>

      <!-- 空状态提示 -->
      <div v-if="studentList.length === 0" class="empty-state">
        <el-empty description="您还没有负责的班级或班级中没有学生">
          <div>
            <p style="margin-bottom: 10px;">请先在班级管理中选择负责的班级</p>
            <el-button type="primary" @click="$router.push('/teacher/class-management')">
              去选择班级
            </el-button>
          </div>
        </el-empty>
      </div>

      <el-table v-else :data="gradeList" style="width: 100%; margin-top: 15px;" v-loading="loading" stripe>
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="studentName" label="学生姓名" width="120" />
        <el-table-column prop="studentNumber" label="学号" width="120" />
        <el-table-column prop="subject" label="科目" width="100" />
        <el-table-column prop="examType" label="考试类型" width="100" />
        <el-table-column prop="examDate" label="考试日期" width="120" />
        <el-table-column prop="score" label="成绩" width="100">
          <template #default="scope">
            <el-tag :type="getScoreTagType(scope.row.score)" size="large" effect="dark">
              {{ scope.row.score }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="academicTerm" label="学年" width="120" />
        <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <el-button link type="primary" @click="editGrade(scope.row)">编辑</el-button>
            <el-button link type="danger" @click="confirmDeleteGrade(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          layout="total, prev, pager, next"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 添加成绩对话框 -->
    <el-dialog v-model="addDialogVisible" title="添加成绩" width="600px" @close="resetAddForm">
      <el-form :model="addForm" label-width="120px" :rules="rules" ref="addFormRef">
        <el-form-item label="学生" prop="studentId">
          <el-select v-model="addForm.studentId" placeholder="选择学生" filterable style="width: 100%;">
            <el-option
              v-for="student in studentList"
              :key="student.id"
              :label="`${student.realName} (${student.studentNumber}) - ${student.className}`"
              :value="student.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="科目" prop="subject">
          <el-select v-model="addForm.subject" placeholder="选择科目" style="width: 100%;">
            <el-option label="数学" value="数学" />
            <el-option label="语文" value="语文" />
            <el-option label="英语" value="英语" />
            <el-option label="物理" value="物理" />
            <el-option label="化学" value="化学" />
            <el-option label="生物" value="生物" />
            <el-option label="政治" value="政治" />
            <el-option label="历史" value="历史" />
            <el-option label="地理" value="地理" />
          </el-select>
        </el-form-item>
        <el-form-item label="成绩" prop="score">
          <el-input-number v-model="addForm.score" :min="0" :max="100" :precision="1" :step="0.5" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="考试类型" prop="examType">
          <el-select v-model="addForm.examType" placeholder="选择考试类型" style="width: 100%;">
            <el-option label="日常" value="日常" />
            <el-option label="期中" value="期中" />
            <el-option label="期末" value="期末" />
          </el-select>
        </el-form-item>
        <el-form-item label="考试日期" prop="examDate">
          <el-date-picker
            v-model="addForm.examDate"
            type="date"
            placeholder="选择日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 100%;"
          />
        </el-form-item>
        <el-form-item label="学年" prop="academicTerm">
          <el-input v-model="addForm.academicTerm" placeholder="例如：2024-2025" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="addForm.remark" type="textarea" :rows="3" placeholder="请输入备注信息（可选）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="addDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleAddGrade" :loading="submitting">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 编辑成绩对话框 -->
    <el-dialog v-model="editDialogVisible" title="编辑成绩" width="500px" @close="resetEditForm">
      <el-form :model="editForm" label-width="100px" :rules="editRules" ref="editFormRef">
        <el-form-item label="学生">
          <el-input v-model="editForm.studentName" disabled />
        </el-form-item>
        <el-form-item label="科目">
          <el-input v-model="editForm.subject" disabled />
        </el-form-item>
        <el-form-item label="成绩" prop="score">
          <el-input-number v-model="editForm.score" :min="0" :max="100" :precision="1" :step="0.5" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="editForm.remark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleUpdateGrade" :loading="submitting">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getGradeManagement, addGrade, updateGrade, deleteGrade, getTeacherStudents } from '@/api/teacher'

const loading = ref(false)
const submitting = ref(false)
const gradeList = ref([])
const studentList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const addDialogVisible = ref(false)
const editDialogVisible = ref(false)

const filter = ref({
  studentId: null,
  subject: '',
  examType: ''
})

const addForm = ref({
  studentId: null,
  subject: '',
  score: 0,
  examType: '日常',
  examDate: '',
  academicTerm: '2024-2025',
  remark: ''
})

const editForm = ref({
  id: null,
  studentName: '',
  subject: '',
  score: 0,
  remark: ''
})

const addFormRef = ref(null)
const editFormRef = ref(null)

const rules = {
  studentId: [{ required: true, message: '请选择学生', trigger: 'change' }],
  subject: [{ required: true, message: '请选择科目', trigger: 'change' }],
  score: [{ required: true, message: '请输入成绩', trigger: 'blur' }],
  examType: [{ required: true, message: '请选择考试类型', trigger: 'change' }],
  examDate: [{ required: true, message: '请选择考试日期', trigger: 'change' }],
  academicTerm: [{ required: true, message: '请输入学年', trigger: 'blur' }]
}

const editRules = {
  score: [{ required: true, message: '请输入成绩', trigger: 'blur' }]
}

const getScoreTagType = (score) => {
  if (score >= 90) return 'success'
  if (score >= 80) return 'warning'
  if (score >= 60) return ''
  return 'danger'
}

// 加载学生列表
const loadStudents = async () => {
  try {
    const res = await getTeacherStudents()
    studentList.value = res
  } catch (error) {
    // 如果是因为没有班级，给出更友好的提示
    if (error.message && error.message.includes('班级')) {
      ElMessage.warning('您还没有负责的班级，请先在班级管理中选择班级')
    } else {
      ElMessage.error('加载学生列表失败')
    }
    studentList.value = []
  }
}

// 加载成绩列表
const loadGrades = async () => {
  loading.value = true
  try {
    const params = {
      current: currentPage.value,
      size: pageSize.value,
      ...filter.value
    }
    // 移除空值参数
    Object.keys(params).forEach(key => {
      if (params[key] === null || params[key] === '') {
        delete params[key]
      }
    })

    const res = await getGradeManagement(params)
    gradeList.value = res.records
    total.value = res.total
  } catch (error) {
    ElMessage.error('加载成绩数据失败')
  } finally {
    loading.value = false
  }
}

// 筛选
const handleFilterChange = () => {
  currentPage.value = 1
  loadGrades()
}

// 重置筛选
const resetFilter = () => {
  filter.value = {
    studentId: null,
    subject: '',
    examType: ''
  }
  currentPage.value = 1
  loadGrades()
}

// 显示添加对话框
const showAddDialog = () => {
  if (studentList.value.length === 0) {
    ElMessage.warning('您还没有负责的班级或班级中没有学生')
    return
  }
  addDialogVisible.value = true
}

// 重置添加表单
const resetAddForm = () => {
  addForm.value = {
    studentId: null,
    subject: '',
    score: 0,
    examType: '日常',
    examDate: new Date().toISOString().split('T')[0],
    academicTerm: '2024-2025',
    remark: ''
  }
  if (addFormRef.value) {
    addFormRef.value.resetFields()
  }
}

// 添加成绩
const handleAddGrade = async () => {
  if (!addFormRef.value) return

  await addFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        const res = await addGrade(addForm.value)
        ElMessage.success(res.message || '成绩添加成功')
        addDialogVisible.value = false
        loadGrades()
      } catch (error) {
        ElMessage.error(error.message || '添加成绩失败')
      } finally {
        submitting.value = false
      }
    }
  })
}

// 编辑成绩
const editGrade = (row) => {
  editForm.value = {
    id: row.id,
    studentName: row.studentName,
    subject: row.subject,
    score: row.score,
    remark: row.remark || ''
  }
  editDialogVisible.value = true
}

// 重置编辑表单
const resetEditForm = () => {
  if (editFormRef.value) {
    editFormRef.value.resetFields()
  }
}

// 更新成绩
const handleUpdateGrade = async () => {
  if (!editFormRef.value) return

  await editFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        const data = {
          score: editForm.value.score,
          remark: editForm.value.remark
        }
        const res = await updateGrade(editForm.value.id, data)
        ElMessage.success(res.message || '成绩更新成功')
        editDialogVisible.value = false
        loadGrades()
      } catch (error) {
        ElMessage.error(error.message || '更新成绩失败')
      } finally {
        submitting.value = false
      }
    }
  })
}

// 确认删除成绩
const confirmDeleteGrade = (row) => {
  ElMessageBox.confirm(
    `确定要删除 ${row.studentName} 的 ${row.subject} 成绩（${row.score}分）吗？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    handleDeleteGrade(row.id)
  }).catch(() => {
    // 取消删除
  })
}

// 删除成绩
const handleDeleteGrade = async (id) => {
  try {
    const res = await deleteGrade(id)
    ElMessage.success(res.message || '成绩删除成功')
    loadGrades()
  } catch (error) {
    ElMessage.error(error.message || '删除成绩失败')
  }
}

// 导出成绩
const exportGrades = () => {
  ElMessage.success('成绩表导出功能开发中...')
  // 实际项目中可以调用 exportGrades API
  // exportGrades(filter.value).then(res => {
  //   const blob = new Blob([res], { type: 'application/vnd.ms-excel' })
  //   const url = window.URL.createObjectURL(blob)
  //   const a = document.createElement('a')
  //   a.href = url
  //   a.download = '成绩表.xlsx'
  //   a.click()
  // })
}

// 分页处理
const handleCurrentChange = (val) => {
  currentPage.value = val
  loadGrades()
}

onMounted(() => {
  // 先加载学生列表，再加载成绩
  loadStudents().then(() => {
    // 设置默认考试日期
    addForm.value.examDate = new Date().toISOString().split('T')[0]
    // 只有当有学生时才加载成绩，避免无意义的错误
    if (studentList.value.length > 0) {
      loadGrades()
    }
  })
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.filter-bar {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  align-items: center;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.empty-state {
  margin: 40px 0;
  text-align: center;
}
</style>
