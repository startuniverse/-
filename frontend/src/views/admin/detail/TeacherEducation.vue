<template>
  <div class="teacher-education">
    <div class="action-bar">
      <el-button type="primary" @click="handleAdd" :icon="Plus">新增教育背景</el-button>
    </div>

    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column type="index" label="序号" width="60" align="center" />
      <el-table-column prop="schoolName" label="学校名称" min-width="180" />
      <el-table-column prop="major" label="专业" width="150" />
      <el-table-column prop="degree" label="学历" width="100" align="center" />
      <el-table-column prop="graduationDate" label="毕业日期" width="120" align="center">
        <template #default="{ row }">
          {{ row.graduationDate }}
        </template>
      </el-table-column>
      <el-table-column prop="learningForm" label="学习形式" width="100" align="center">
        <template #default="{ row }">
          {{ row.learningForm === 'fulltime' ? '全日制' : '在职' }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="100" align="center">
        <template #default="{ row }">
          <el-button size="small" type="danger" @click="handleDelete(row)" :icon="Delete">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 对话框 -->
    <el-dialog v-model="dialogVisible" title="新增教育背景" width="500px">
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px">
        <el-form-item label="学校名称" prop="schoolName">
          <el-input v-model="formData.schoolName" placeholder="请输入学校名称" />
        </el-form-item>
        <el-form-item label="专业" prop="major">
          <el-input v-model="formData.major" placeholder="请输入专业" />
        </el-form-item>
        <el-form-item label="学历" prop="degree">
          <el-select v-model="formData.degree" placeholder="请选择学历" style="width: 100%">
            <el-option label="博士" value="博士" />
            <el-option label="硕士" value="硕士" />
            <el-option label="本科" value="本科" />
            <el-option label="大专" value="大专" />
          </el-select>
        </el-form-item>
        <el-form-item label="毕业日期" prop="graduationDate">
          <el-date-picker
            v-model="formData.graduationDate"
            type="date"
            placeholder="选择日期"
            style="width: 100%"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="学习形式" prop="learningForm">
          <el-select v-model="formData.learningForm" placeholder="请选择学习形式" style="width: 100%">
            <el-option label="全日制" value="fulltime" />
            <el-option label="在职" value="parttime" />
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
import { ref, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Delete } from '@element-plus/icons-vue'
import { getTeacherEducationList, addTeacherEducation } from '@/api/teacherInfo'

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
  schoolName: '',
  major: '',
  degree: '',
  graduationDate: '',
  learningForm: ''
})

const rules = {
  schoolName: [{ required: true, message: '请输入学校名称', trigger: 'blur' }],
  major: [{ required: true, message: '请输入专业', trigger: 'blur' }],
  degree: [{ required: true, message: '请选择学历', trigger: 'change' }],
  graduationDate: [{ required: true, message: '请选择毕业日期', trigger: 'change' }],
  learningForm: [{ required: true, message: '请选择学习形式', trigger: 'change' }]
}

const fetchList = async () => {
  if (!props.teacherId) return
  loading.value = true
  try {
    const res = await getTeacherEducationList(props.teacherId)
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
    schoolName: '',
    major: '',
    degree: '',
    graduationDate: '',
    learningForm: ''
  }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const res = await addTeacherEducation(formData.value)
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
.teacher-education {
  padding: 0;
}

.action-bar {
  margin-bottom: 16px;
  text-align: right;
}
</style>
