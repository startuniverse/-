<template>
  <div class="teacher-teaching">
    <div class="action-bar">
      <el-button type="primary" @click="handleAdd" :icon="Plus">新增教学任务</el-button>
    </div>

    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column type="index" label="序号" width="60" align="center" />
      <el-table-column prop="academicYear" label="学年度" width="100" align="center" />
      <el-table-column prop="semester" label="学期" width="80" align="center">
        <template #default="{ row }">
          {{ row.semester === 1 ? '第一学期' : '第二学期' }}
        </template>
      </el-table-column>
      <el-table-column prop="courseName" label="课程名称" min-width="180" />
      <el-table-column prop="courseCode" label="课程代码" width="120" />
      <el-table-column prop="classNumber" label="教学班号" width="100" />
      <el-table-column prop="targetGrade" label="授课对象年级" width="120" />
      <el-table-column prop="studentCount" label="学生人数" width="90" align="center" />
      <el-table-column prop="weeklyHours" label="周学时" width="80" align="center" />
      <el-table-column prop="totalHours" label="总学时" width="80" align="center" />
      <el-table-column prop="teachingLocation" label="教学场所" width="120" />
      <el-table-column prop="teachingMode" label="教学模式" width="100" align="center">
        <template #default="{ row }">
          {{ getTeachingModeText(row.teachingMode) }}
        </template>
      </el-table-column>
      <el-table-column prop="courseNature" label="课程性质" width="100" align="center">
        <template #default="{ row }">
          {{ getCourseNatureText(row.courseNature) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="100" align="center">
        <template #default="{ row }">
          <el-button size="small" type="danger" @click="handleDelete(row)" :icon="Delete">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 对话框 -->
    <el-dialog v-model="dialogVisible" title="新增教学任务" width="700px">
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="学年度" prop="academicYear">
              <el-input v-model="formData.academicYear" placeholder="如：2024-2025" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="学期" prop="semester">
              <el-select v-model="formData.semester" placeholder="请选择学期" style="width: 100%">
                <el-option label="第一学期" :value="1" />
                <el-option label="第二学期" :value="2" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="课程名称" prop="courseName">
          <el-input v-model="formData.courseName" placeholder="请输入课程名称" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="课程代码" prop="courseCode">
              <el-input v-model="formData.courseCode" placeholder="请输入课程代码" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="教学班号" prop="classNumber">
              <el-input v-model="formData.classNumber" placeholder="请输入教学班号" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="授课对象年级" prop="targetGrade">
              <el-input v-model="formData.targetGrade" placeholder="如：2024级" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="学生人数" prop="studentCount">
              <el-input v-model="formData.studentCount" type="number" placeholder="请输入人数" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="周学时" prop="weeklyHours">
              <el-input v-model="formData.weeklyHours" type="number" placeholder="请输入周学时" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="总学时" prop="totalHours">
              <el-input v-model="formData.totalHours" type="number" placeholder="请输入总学时" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="教学场所" prop="teachingLocation">
          <el-input v-model="formData.teachingLocation" placeholder="请输入教学场所" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="教学模式" prop="teachingMode">
              <el-select v-model="formData.teachingMode" placeholder="请选择教学模式" style="width: 100%">
                <el-option label="线上" value="online" />
                <el-option label="线下" value="offline" />
                <el-option label="混合" value="hybrid" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="课程性质" prop="courseNature">
              <el-select v-model="formData.courseNature" placeholder="请选择课程性质" style="width: 100%">
                <el-option label="必修" value="required" />
                <el-option label="选修" value="elective" />
                <el-option label="公选" value="public" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
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
import { ref, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Delete } from '@element-plus/icons-vue'
import { getTeacherTeachingTaskList, addTeacherTeachingTask } from '@/api/teacherInfo'

const props = defineProps({
  teacherId: {
    type: Number,
    required: true
  }
})

const loading = ref(false)
const list = ref([])
const dialogVisible = ref(false)
const formRef = ref(null)

const formData = ref({
  teacherId: null,
  academicYear: '',
  semester: null,
  courseName: '',
  courseCode: '',
  classNumber: '',
  targetGrade: '',
  studentCount: null,
  weeklyHours: null,
  totalHours: null,
  teachingLocation: '',
  teachingMode: '',
  courseNature: ''
})

const rules = {
  academicYear: [{ required: true, message: '请输入学年度', trigger: 'blur' }],
  semester: [{ required: true, message: '请选择学期', trigger: 'change' }],
  courseName: [{ required: true, message: '请输入课程名称', trigger: 'blur' }],
  courseCode: [{ required: true, message: '请输入课程代码', trigger: 'blur' }],
  classNumber: [{ required: true, message: '请输入教学班号', trigger: 'blur' }],
  targetGrade: [{ required: true, message: '请输入授课对象年级', trigger: 'blur' }],
  studentCount: [{ required: true, message: '请输入学生人数', trigger: 'blur' }],
  weeklyHours: [{ required: true, message: '请输入周学时', trigger: 'blur' }],
  totalHours: [{ required: true, message: '请输入总学时', trigger: 'blur' }],
  teachingLocation: [{ required: true, message: '请输入教学场所', trigger: 'blur' }],
  teachingMode: [{ required: true, message: '请选择教学模式', trigger: 'change' }],
  courseNature: [{ required: true, message: '请选择课程性质', trigger: 'change' }]
}

const getTeachingModeText = (mode) => {
  const textMap = {
    online: '线上',
    offline: '线下',
    hybrid: '混合'
  }
  return textMap[mode] || mode
}

const getCourseNatureText = (nature) => {
  const textMap = {
    required: '必修',
    elective: '选修',
    public: '公选'
  }
  return textMap[nature] || nature
}

const fetchList = async () => {
  if (!props.teacherId) return
  loading.value = true
  try {
    const res = await getTeacherTeachingTaskList(props.teacherId)
    if (res.code === 200) {
      list.value = res.data
    }
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  formData.value = {
    teacherId: props.teacherId,
    academicYear: '',
    semester: null,
    courseName: '',
    courseCode: '',
    classNumber: '',
    targetGrade: '',
    studentCount: null,
    weeklyHours: null,
    totalHours: null,
    teachingLocation: '',
    teachingMode: '',
    courseNature: ''
  }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const res = await addTeacherTeachingTask(formData.value)
        if (res.code === 200) {
          ElMessage.success('新增成功')
          dialogVisible.value = false
          fetchList()
        }
      } catch (error) {
        ElMessage.error('新增失败')
      }
    }
  })
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    // 这里需要添加删除API
    ElMessage.success('删除成功')
    fetchList()
  }).catch(() => {})
}

watch(() => props.teacherId, () => {
  fetchList()
})

onMounted(() => {
  fetchList()
})
</script>

<style scoped>
.teacher-teaching {
  padding: 0;
}

.action-bar {
  margin-bottom: 16px;
  text-align: right;
}
</style>