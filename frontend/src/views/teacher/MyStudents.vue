<template>
  <div class="my-students">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>👥 我的学生</span>
          <el-button type="primary" icon="Plus" @click="addStudent">添加学生</el-button>
        </div>
      </template>

      <el-table :data="studentList" style="width: 100%" v-loading="loading">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="studentNumber" label="学号" width="120" />
        <el-table-column prop="realName" label="姓名" width="120" />
        <el-table-column prop="gender" label="性别" width="80">
          <template #default="scope">
            <el-tag :type="scope.row.gender === '男' ? 'primary' : 'danger'" size="small">
              {{ scope.row.gender }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="className" label="班级" />
        <el-table-column prop="phone" label="联系电话" />
        <el-table-column prop="parentPhone" label="家长电话" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <el-button link type="primary" @click="viewDetail(scope.row)">查看</el-button>
            <el-button link type="danger" @click="removeStudent(scope.row)">移除</el-button>
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const studentList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 模拟数据
const mockStudents = [
  { studentNumber: '2024001', realName: '张三', gender: '男', className: '高三1班', phone: '13800138001', parentPhone: '13900138001' },
  { studentNumber: '2024002', realName: '李四', gender: '女', className: '高三1班', phone: '13800138002', parentPhone: '13900138002' },
  { studentNumber: '2024003', realName: '王五', gender: '男', className: '高三1班', phone: '13800138003', parentPhone: '13900138003' },
  { studentNumber: '2024004', realName: '赵六', gender: '女', className: '高三1班', phone: '13800138004', parentPhone: '13900138004' },
  { studentNumber: '2024005', realName: '钱七', gender: '男', className: '高三1班', phone: '13800138005', parentPhone: '13900138005' }
]

const loadStudents = async () => {
  loading.value = true
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500))
    studentList.value = mockStudents
    total.value = mockStudents.length
  } catch (error) {
    ElMessage.error('加载学生列表失败')
  } finally {
    loading.value = false
  }
}

const addStudent = () => {
  ElMessage.info('添加学生功能开发中...')
}

const viewDetail = (row) => {
  ElMessage.info(`查看 ${row.realName} 的详细信息`)
}

const removeStudent = (row) => {
  ElMessageBox.confirm(`确定要将 ${row.realName} 从班级中移除吗？`, '警告', {
    type: 'warning'
  }).then(() => {
    ElMessage.success('移除成功')
    loadStudents()
  }).catch(() => {})
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
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>
