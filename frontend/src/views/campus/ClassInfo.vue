<template>
  <div class="class-info">
    <el-row :gutter="20">
      <el-col :span="8" :xs="24">
        <el-card class="class-card">
          <template #header>
            <div class="card-header">
              <span>班级信息</span>
            </div>
          </template>
          <div v-if="classInfo" class="class-details">
            <div class="detail-item">
              <span class="label">班级名称:</span>
              <span class="value">{{ classInfo.className }}</span>
            </div>
            <div class="detail-item">
              <span class="label">年级:</span>
              <span class="value">{{ classInfo.grade }}</span>
            </div>
            <div class="detail-item">
              <span class="label">学年:</span>
              <span class="value">{{ classInfo.academicYear }}</span>
            </div>
            <div class="detail-item">
              <span class="label">班主任:</span>
              <span class="value">教师{{ classInfo.headTeacherId }}</span>
            </div>
            <div class="detail-item">
              <span class="label">学生人数:</span>
              <span class="value">{{ classInfo.studentCount }}</span>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="16" :xs="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>学生花名册</span>
              <el-input
                v-model="searchName"
                placeholder="搜索学生姓名"
                size="small"
                style="width: 150px;"
                clearable
                @input="filterStudents"
              />
            </div>
          </template>
          <el-table v-loading="loading" :data="filteredStudents" border style="width: 100%">
            <el-table-column type="index" label="序号" width="60" align="center" />
            <el-table-column prop="studentNumber" label="学号" width="120" align="center" />
            <el-table-column prop="name" label="姓名" width="120" align="center">
              <template #default="scope">
                {{ scope.row.realName }}
              </template>
            </el-table-column>
            <el-table-column prop="guardianName" label="监护人" width="120" align="center" />
            <el-table-column prop="guardianPhone" label="监护人电话" width="140" align="center" />
            <el-table-column prop="status" label="状态" width="100" align="center">
              <template #default="scope">
                <el-tag :type="getStatusTag(scope.row.status)">
                  {{ getStatusText(scope.row.status) }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { getClassInfo } from '@/api/campus'
import { useUserStore } from '@/store/modules/user'

const userStore = useUserStore()
const loading = ref(false)
const classInfo = ref(null)
const students = ref([])
const searchName = ref('')

const filteredStudents = computed(() => {
  if (!searchName.value) return students.value
  return students.value.filter(s =>
    s.realName && s.realName.includes(searchName.value)
  )
})

const getStatusText = (status) => {
  const map = {
    1: '在读',
    2: '毕业',
    3: '转学',
    4: '休学'
  }
  return map[status] || '未知'
}

const getStatusTag = (status) => {
  const map = {
    1: 'success',
    2: 'info',
    3: 'warning',
    4: 'danger'
  }
  return map[status] || 'info'
}

const loadClassInfo = async () => {
  try {
    loading.value = true
    const data = await getClassInfo()
    classInfo.value = data.classInfo
    students.value = data.students || []
  } catch (error) {
    ElMessage.error('加载班级信息失败')
  } finally {
    loading.value = false
  }
}

const filterStudents = () => {
  // 计算属性会自动处理
}

onMounted(() => {
  loadClassInfo()
})
</script>

<style scoped>
.class-info {
  width: 100%;
}

.class-card {
  margin-bottom: 20px;
}

.card-header {
  font-weight: 600;
}

.class-details {
  padding: 10px 0;
}

.detail-item {
  display: flex;
  justify-content: space-between;
  padding: 12px 0;
  border-bottom: 1px solid #f5f5f5;
}

.detail-item:last-child {
  border-bottom: none;
}

.label {
  color: #909399;
  font-size: 14px;
}

.value {
  color: #303133;
  font-size: 14px;
  font-weight: 500;
}
</style>
