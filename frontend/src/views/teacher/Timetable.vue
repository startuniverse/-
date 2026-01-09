<template>
  <div class="timetable-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>我的课表管理</span>
          <div class="controls">
            <el-button type="primary" size="small" @click="showAddDialog">添加课程</el-button>
            <el-button type="success" size="small" @click="showBatchAddDialog">批量添加</el-button>
            <el-button type="primary" size="small" @click="loadTimetable">刷新</el-button>
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
        <el-table-column prop="className" label="班级" width="120" align="center" />
        <el-table-column prop="classroom" label="教室" width="120" align="center" />
        <el-table-column prop="startTime" label="开始时间" width="100" align="center" />
        <el-table-column prop="endTime" label="结束时间" width="100" align="center" />
        <el-table-column prop="academicWeek" label="教学周" width="120" align="center">
          <template #default="scope">
            <el-tag type="info">{{ scope.row.academicWeek === 'all' ? '全周' : scope.row.academicWeek === 'odd' ? '单周' : '双周' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" align="center">
          <template #default="scope">
            <el-button type="danger" size="small" @click="deleteTimetable(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 添加课程对话框 -->
    <el-dialog v-model="addDialogVisible" title="添加课程" width="500px">
      <el-form :model="addForm" label-width="100px">
        <el-form-item label="星期">
          <el-select v-model="addForm.weekDay" placeholder="选择星期">
            <el-option label="星期一" :value="1" />
            <el-option label="星期二" :value="2" />
            <el-option label="星期三" :value="3" />
            <el-option label="星期四" :value="4" />
            <el-option label="星期五" :value="5" />
            <el-option label="星期六" :value="6" />
            <el-option label="星期日" :value="7" />
          </el-select>
        </el-form-item>
        <el-form-item label="节次">
          <el-input-number v-model="addForm.period" :min="1" :max="8" />
        </el-form-item>
        <el-form-item label="科目">
          <el-input v-model="addForm.subject" placeholder="输入科目名称" />
        </el-form-item>
        <el-form-item label="班级">
          <el-select v-model="addForm.classId" placeholder="选择班级" :disabled="teacherClasses.length === 0">
            <el-option
              v-for="cls in teacherClasses"
              :key="cls.id"
              :label="cls.className"
              :value="cls.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="教室">
          <el-input v-model="addForm.classroom" placeholder="输入教室号" />
        </el-form-item>
        <el-form-item label="开始时间">
          <el-time-picker v-model="addForm.startTime" format="HH:mm" placeholder="选择时间" />
        </el-form-item>
        <el-form-item label="结束时间">
          <el-time-picker v-model="addForm.endTime" format="HH:mm" placeholder="选择时间" />
        </el-form-item>
        <el-form-item label="教学周">
          <el-select v-model="addForm.academicWeek" placeholder="选择教学周">
            <el-option label="全周" value="all" />
            <el-option label="单周" value="odd" />
            <el-option label="双周" value="even" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="addDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="addTimetable">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 批量添加对话框 -->
    <el-dialog v-model="batchDialogVisible" title="批量添加课程" width="650px">
      <div style="margin-bottom: 10px;">
        <el-button type="primary" size="small" @click="addBatchRow">添加行</el-button>
        <el-button type="success" size="small" @click="submitBatchAdd">提交全部</el-button>
        <el-alert type="info" :closable="false" style="margin-top: 8px;">
          提示：如果没有选择班级，系统将自动使用您负责的第一个班级
        </el-alert>
      </div>
      <el-table :data="batchForm" border style="width: 100%" height="350">
        <el-table-column label="星期" width="90">
          <template #default="scope">
            <el-select v-model="scope.row.weekDay" size="small">
              <el-option label="一" :value="1" />
              <el-option label="二" :value="2" />
              <el-option label="三" :value="3" />
              <el-option label="四" :value="4" />
              <el-option label="五" :value="5" />
              <el-option label="六" :value="6" />
              <el-option label="日" :value="7" />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="节次" width="70">
          <template #default="scope">
            <el-input-number v-model="scope.row.period" :min="1" :max="8" size="small" />
          </template>
        </el-table-column>
        <el-table-column label="科目" width="90">
          <template #default="scope">
            <el-input v-model="scope.row.subject" size="small" placeholder="科目" />
          </template>
        </el-table-column>
        <el-table-column label="班级" width="120">
          <template #default="scope">
            <el-select v-model="scope.row.classId" size="small" placeholder="可选" clearable>
              <el-option
                v-for="cls in teacherClasses"
                :key="cls.id"
                :label="cls.className"
                :value="cls.id"
              />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="教室" width="80">
          <template #default="scope">
            <el-input v-model="scope.row.classroom" size="small" placeholder="教室" />
          </template>
        </el-table-column>
        <el-table-column label="时间" width="120">
          <template #default="scope">
            <div style="display: flex; gap: 2px;">
              <el-time-picker v-model="scope.row.startTime" format="HH:mm" size="small" placeholder="始" style="width: 52px;" />
              <el-time-picker v-model="scope.row.endTime" format="HH:mm" size="small" placeholder="终" style="width: 52px;" />
            </div>
          </template>
        </el-table-column>
        <el-table-column label="教学周" width="70">
          <template #default="scope">
            <el-select v-model="scope.row.academicWeek" size="small">
              <el-option label="全" value="all" />
              <el-option label="单" value="odd" />
              <el-option label="双" value="even" />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="60">
          <template #default="scope">
            <el-button type="danger" size="small" @click="removeBatchRow(scope.$index)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="batchDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitBatchAdd">提交</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getTeacherTimetable, addTeacherTimetable, deleteTeacherTimetable, batchAddTeacherTimetable, getTeacherClasses } from '@/api/teacher'

const loading = ref(false)
const timetableData = ref([])
const teacherClasses = ref([])

// 添加表单
const addDialogVisible = ref(false)
const addForm = ref({
  weekDay: 1,
  period: 1,
  subject: '',
  classId: null,
  classroom: '',
  startTime: null,
  endTime: null,
  academicWeek: 'all'
})

// 批量添加
const batchDialogVisible = ref(false)
const batchForm = ref([])

const getWeekDayText = (day) => {
  const days = ['', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六', '星期日']
  return days[day] || ''
}

// 加载教师负责的班级列表
const loadTeacherClasses = async () => {
  try {
    const data = await getTeacherClasses()
    teacherClasses.value = data
    // 如果有班级，设置默认选择
    if (data.length > 0) {
      addForm.value.classId = data[0].id
    }
  } catch (error) {
    ElMessage.error('加载班级列表失败')
  }
}

const loadTimetable = async () => {
  try {
    loading.value = true
    const data = await getTeacherTimetable()
    // 为每条记录添加班级名称显示
    timetableData.value = data.map(item => ({
      ...item,
      className: teacherClasses.value.find(c => c.id === item.classId)?.className || item.classId
    }))
  } catch (error) {
    ElMessage.error('加载课程表失败')
  } finally {
    loading.value = false
  }
}

const showAddDialog = () => {
  addForm.value = {
    weekDay: 1,
    period: 1,
    subject: '',
    classId: teacherClasses.value.length > 0 ? teacherClasses.value[0].id : null,
    classroom: '',
    startTime: null,
    endTime: null,
    academicWeek: 'all'
  }
  addDialogVisible.value = true
}

const showBatchAddDialog = () => {
  batchForm.value = []
  batchDialogVisible.value = true
}

const addBatchRow = () => {
  batchForm.value.push({
    weekDay: 1,
    period: 1,
    subject: '',
    classId: null,
    classroom: '',
    startTime: null,
    endTime: null,
    academicWeek: 'all'
  })
}

const removeBatchRow = (index) => {
  batchForm.value.splice(index, 1)
}

const addTimetable = async () => {
  try {
    if (!addForm.value.subject || !addForm.value.startTime || !addForm.value.endTime) {
      ElMessage.warning('请填写完整信息（科目、开始时间、结束时间）')
      return
    }

    const params = {
      ...addForm.value,
      startTime: formatTime(addForm.value.startTime),
      endTime: formatTime(addForm.value.endTime)
    }

    await addTeacherTimetable(params)
    ElMessage.success('添加成功')
    addDialogVisible.value = false
    loadTimetable()
  } catch (error) {
    ElMessage.error('添加失败: ' + (error.message || '未知错误'))
  }
}

const submitBatchAdd = async () => {
  try {
    if (batchForm.value.length === 0) {
      ElMessage.warning('请至少添加一行数据')
      return
    }

    // 验证数据完整性
    for (let item of batchForm.value) {
      if (!item.subject || !item.startTime || !item.endTime) {
        ElMessage.warning('请填写完整信息（科目、开始时间、结束时间）')
        return
      }
    }

    // 格式化数据
    const formattedData = batchForm.value.map(item => ({
      ...item,
      startTime: formatTime(item.startTime),
      endTime: formatTime(item.endTime)
    }))

    await batchAddTeacherTimetable(formattedData)
    ElMessage.success('批量添加成功')
    batchDialogVisible.value = false
    loadTimetable()
  } catch (error) {
    ElMessage.error('批量添加失败: ' + (error.message || '未知错误'))
  }
}

const deleteTimetable = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这条课程表记录吗？', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await deleteTeacherTimetable(id)
    ElMessage.success('删除成功')
    loadTimetable()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败: ' + (error.message || '未知错误'))
    }
  }
}

const formatTime = (time) => {
  if (!time) return ''
  if (typeof time === 'string') return time
  // 如果是Date对象，格式化为HH:mm
  const hours = String(time.getHours()).padStart(2, '0')
  const minutes = String(time.getMinutes()).padStart(2, '0')
  return `${hours}:${minutes}`
}

onMounted(async () => {
  await loadTeacherClasses()
  loadTimetable()
})
</script>

<style scoped>
.timetable-management {
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

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>
