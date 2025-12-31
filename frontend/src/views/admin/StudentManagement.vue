<template>
  <div class="student-management">
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
            />
            <el-button type="primary" size="small" @click="loadStudents">查询</el-button>
            <el-button type="success" size="small" @click="handleAdd">新增</el-button>
          </div>
        </div>
      </template>

      <el-table v-loading="loading" :data="studentsData" border style="width: 100%">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="studentNumber" label="学号" width="120" align="center" />
        <el-table-column prop="name" label="姓名" width="120" align="center" />
        <el-table-column prop="classId" label="班级" width="100" align="center" />
        <el-table-column prop="guardianName" label="监护人" width="120" align="center" />
        <el-table-column prop="guardianPhone" label="监护人电话" width="140" align="center" />
        <el-table-column prop="enrollmentDate" label="入学日期" width="120" align="center" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="scope">
            <el-tag :type="getStatusTag(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" align="center" fixed="right">
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

    <!-- 编辑/新增对话框 -->
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
        <el-form-item label="班级ID" prop="classId">
          <el-input v-model.number="form.classId" placeholder="请输入班级ID" />
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getStudents, addStudent, updateStudent, deleteStudent } from '@/api/admin'

const loading = ref(false)
const searchKeyword = ref('')
const studentsData = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

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

const getStatusText = (status) => {
  const map = { 1: '在读', 2: '毕业', 3: '转学', 4: '休学' }
  return map[status] || '未知'
}

const getStatusTag = (status) => {
  const map = { 1: 'success', 2: 'info', 3: 'warning', 4: 'danger' }
  return map[status] || 'info'
}

const loadStudents = async () => {
  try {
    loading.value = true
    const params = {
      current: currentPage.value,
      size: pageSize.value,
      keyword: searchKeyword.value  // 添加搜索关键词
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
    realName: row.name,  // 后端返回的是name，表单需要realName
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
        // 构建提交数据，字段名需要与后端匹配
        const submitData = {
          name: form.value.realName,
          studentNumber: form.value.studentNumber,
          classId: form.value.classId,
          guardianName: form.value.guardianName,
          guardianPhone: form.value.guardianPhone,
          status: form.value.status
        }

        if (isEdit.value) {
          // 编辑模式：需要传递id
          submitData.id = form.value.id
          await updateStudent(submitData)
          ElMessage.success('更新成功')
        } else {
          // 新增模式
          await addStudent(submitData)
          ElMessage.success('新增成功')
        }

        dialogVisible.value = false
        loadStudents()
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

onMounted(() => {
  loadStudents()
})
</script>

<style scoped>
.student-management {
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
</style>
