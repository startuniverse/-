<template>
  <div class="grade-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>📊 成绩管理</span>
          <div class="header-actions">
            <el-button type="primary" icon="Upload" @click="importGrades">导入成绩</el-button>
            <el-button type="success" icon="Download" @click="exportGrades">导出成绩</el-button>
          </div>
        </div>
      </template>

      <div class="filter-bar">
        <el-select v-model="filter.classId" placeholder="选择班级" clearable @change="handleFilterChange">
          <el-option label="高三1班" value="1" />
          <el-option label="高三2班" value="2" />
        </el-select>

        <el-select v-model="filter.subject" placeholder="选择科目" clearable @change="handleFilterChange" style="margin-left: 10px;">
          <el-option label="数学" value="math" />
          <el-option label="语文" value="chinese" />
          <el-option label="英语" value="english" />
          <el-option label="物理" value="physics" />
        </el-select>

        <el-input
          v-model="filter.search"
          placeholder="搜索学生姓名"
          clearable
          style="margin-left: 10px; width: 200px;"
          @input="handleFilterChange"
        />
      </div>

      <el-table :data="gradeList" style="width: 100%; margin-top: 15px;" v-loading="loading">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="studentName" label="学生姓名" width="120" />
        <el-table-column prop="subject" label="科目" width="100" />
        <el-table-column prop="examDate" label="考试日期" width="120" />
        <el-table-column prop="score" label="成绩" width="100">
          <template #default="scope">
            <el-tag :type="getScoreTagType(scope.row.score)" size="large" effect="dark">
              {{ scope.row.score }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="ranking" label="排名" width="80" />
        <el-table-column prop="comment" label="评语" min-width="200" show-overflow-tooltip />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="scope">
            <el-button link type="primary" @click="editGrade(scope.row)">编辑</el-button>
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

    <!-- 编辑成绩对话框 -->
    <el-dialog v-model="dialogVisible" title="编辑成绩" width="500px">
      <el-form :model="gradeForm" label-width="100px">
        <el-form-item label="学生">
          <el-input v-model="gradeForm.studentName" disabled />
        </el-form-item>
        <el-form-item label="科目">
          <el-input v-model="gradeForm.subject" disabled />
        </el-form-item>
        <el-form-item label="成绩" required>
          <el-input-number v-model="gradeForm.score" :min="0" :max="100" />
        </el-form-item>
        <el-form-item label="评语">
          <el-input v-model="gradeForm.comment" type="textarea" :rows="3" placeholder="请输入评语" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveGrade">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const gradeList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dialogVisible = ref(false)

const filter = ref({
  classId: '',
  subject: '',
  search: ''
})

const gradeForm = ref({
  studentName: '',
  subject: '',
  score: 0,
  comment: ''
})

// 模拟数据
const mockGrades = [
  { studentName: '张三', subject: '数学', examDate: '2025-01-15', score: 95, ranking: 1, comment: '优秀，继续保持' },
  { studentName: '李四', subject: '数学', examDate: '2025-01-15', score: 88, ranking: 2, comment: '良好，有进步' },
  { studentName: '王五', subject: '数学', examDate: '2025-01-15', score: 76, ranking: 3, comment: '需要加强练习' },
  { studentName: '赵六', subject: '数学', examDate: '2025-01-15', score: 92, ranking: 1, comment: '非常优秀' },
  { studentName: '钱七', subject: '数学', examDate: '2025-01-15', score: 85, ranking: 2, comment: '表现不错' }
]

const getScoreTagType = (score) => {
  if (score >= 90) return 'success'
  if (score >= 80) return 'warning'
  if (score >= 60) return ''
  return 'danger'
}

const loadGrades = async () => {
  loading.value = true
  try {
    await new Promise(resolve => setTimeout(resolve, 500))
    gradeList.value = mockGrades
    total.value = mockGrades.length
  } catch (error) {
    ElMessage.error('加载成绩数据失败')
  } finally {
    loading.value = false
  }
}

const handleFilterChange = () => {
  // 实际项目中这里会调用API进行筛选
  ElMessage.info('筛选功能演示')
}

const editGrade = (row) => {
  gradeForm.value = { ...row }
  dialogVisible.value = true
}

const saveGrade = () => {
  // 保存修改
  ElMessage.success('成绩更新成功')
  dialogVisible.value = false
  loadGrades()
}

const importGrades = () => {
  ElMessage.info('导入功能开发中...（支持Excel导入）')
}

const exportGrades = () => {
  ElMessage.success('成绩表已导出')
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  loadGrades()
}

onMounted(() => {
  loadGrades()
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
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>
