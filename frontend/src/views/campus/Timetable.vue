<template>
  <div class="timetable">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>课程表</span>
          <div class="controls">
            <el-select v-model="weekDay" placeholder="选择星期" size="small" style="width: 120px;">
              <el-option label="全部" value=""></el-option>
              <el-option label="星期一" value="1"></el-option>
              <el-option label="星期二" value="2"></el-option>
              <el-option label="星期三" value="3"></el-option>
              <el-option label="星期四" value="4"></el-option>
              <el-option label="星期五" value="5"></el-option>
              <el-option label="星期六" value="6"></el-option>
              <el-option label="星期日" value="7"></el-option>
            </el-select>
            <el-button type="primary" size="small" @click="loadTimetable">查询</el-button>
          </div>
        </div>
      </template>

      <el-table v-loading="loading" :data="timetableData" border style="width: 100%">
        <el-table-column prop="weekDay" label="星期" width="100" align="center">
          <template #default="scope">
            {{ getWeekDayText(scope.row.weekDay) }}
          </template>
        </el-table-column>
        <el-table-column prop="period" label="节次" width="80" align="center" />
        <el-table-column prop="subject" label="科目" width="120" align="center" />
        <el-table-column prop="teacherId" label="教师" width="120" align="center">
          <template #default="scope">
            教师{{ scope.row.teacherId }}
          </template>
        </el-table-column>
        <el-table-column prop="classroom" label="教室" width="120" align="center" />
        <el-table-column prop="startTime" label="开始时间" width="100" align="center" />
        <el-table-column prop="endTime" label="结束时间" width="100" align="center" />
        <el-table-column prop="academicWeek" label="教学周" align="center">
          <template #default="scope">
            <el-tag type="info">{{ scope.row.academicWeek === 'all' ? '全周' : scope.row.academicWeek === 'odd' ? '单周' : '双周' }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getTimetable } from '@/api/campus'

const loading = ref(false)
const weekDay = ref('')
const timetableData = ref([])

const getWeekDayText = (day) => {
  const days = ['', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六', '星期日']
  return days[day] || ''
}

const loadTimetable = async () => {
  try {
    loading.value = true
    const params = {}
    if (weekDay.value) {
      params.weekDay = weekDay.value
    }
    const data = await getTimetable(params)
    timetableData.value = data
  } catch (error) {
    ElMessage.error('加载课程表失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadTimetable()
})
</script>

<style scoped>
.timetable {
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
</style>
