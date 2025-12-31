<template>
  <div class="grades">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>成绩查询</span>
          <div class="controls">
            <el-input
              v-model="subject"
              placeholder="科目"
              size="small"
              style="width: 120px;"
              clearable
            />
            <el-input
              v-model="academicTerm"
              placeholder="学期"
              size="small"
              style="width: 120px;"
              clearable
            />
            <el-button type="primary" size="small" @click="loadGrades">查询</el-button>
          </div>
        </div>
      </template>

      <el-table v-loading="loading" :data="gradesData" border style="width: 100%">
        <el-table-column prop="subject" label="科目" width="120" align="center" />
        <el-table-column prop="examType" label="考试类型" width="120" align="center">
          <template #default="scope">
            <el-tag :type="getExamTypeTag(scope.row.examType)">
              {{ getExamTypeText(scope.row.examType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="score" label="分数" width="100" align="center">
          <template #default="scope">
            <span :style="{ color: getScoreColor(scope.row.score) }">
              {{ scope.row.score }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="examDate" label="考试日期" width="120" align="center" />
        <el-table-column prop="academicTerm" label="学期" width="120" align="center" />
        <el-table-column prop="remark" label="备注" align="center" />
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getGrades } from '@/api/campus'

const loading = ref(false)
const subject = ref('')
const academicTerm = ref('')
const gradesData = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const getExamTypeText = (type) => {
  const map = {
    midterm: '期中考试',
    final: '期末考试',
    daily: '日常测验'
  }
  return map[type] || type
}

const getExamTypeTag = (type) => {
  const map = {
    midterm: 'warning',
    final: 'danger',
    daily: 'info'
  }
  return map[type] || 'info'
}

const getScoreColor = (score) => {
  if (score >= 90) return '#67C23A'
  if (score >= 80) return '#E6A23C'
  if (score >= 60) return '#409EFF'
  return '#F56C6C'
}

const loadGrades = async () => {
  try {
    loading.value = true
    const params = {
      current: currentPage.value,
      size: pageSize.value
    }
    if (subject.value) params.subject = subject.value
    if (academicTerm.value) params.academicTerm = academicTerm.value

    const data = await getGrades(params)
    gradesData.value = data.records
    total.value = data.total
  } catch (error) {
    ElMessage.error('加载成绩失败')
  } finally {
    loading.value = false
  }
}

const handleSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1
  loadGrades()
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
.grades {
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
