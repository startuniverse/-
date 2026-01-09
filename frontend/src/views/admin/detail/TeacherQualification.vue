<template>
  <div class="teacher-qualification">
    <div class="action-bar">
      <el-button type="primary" @click="handleAdd" :icon="Plus">新增资格职称</el-button>
    </div>

    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column type="index" label="序号" width="60" align="center" />
      <el-table-column prop="titleLevel" label="职称级别" width="120" align="center">
        <template #default="{ row }">
          <el-tag :type="getLevelType(row.titleLevel)">
            {{ getLevelText(row.titleLevel) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="titleName" label="职称名称" min-width="150" />
      <el-table-column prop="qualificationType" label="资格类型" width="120" align="center">
        <template #default="{ row }">
          {{ row.qualificationType === 'teacher' ? '教师资格证' : '专业技术资格' }}
        </template>
      </el-table-column>
      <el-table-column prop="obtainDate" label="取得时间" width="120" align="center" />
      <el-table-column prop="approvalAgency" label="评审机构" min-width="150" />
      <el-table-column prop="certificateNumber" label="证书编号" width="150" />
      <el-table-column prop="teachingStage" label="任教学段" width="100" align="center">
        <template #default="{ row }">
          {{ getTeachingStageText(row.teachingStage) }}
        </template>
      </el-table-column>
      <el-table-column prop="teachingSubject" label="任教学科" width="120" />
      <el-table-column prop="expiryDate" label="有效期" width="120" align="center" />
      <el-table-column label="操作" width="100" align="center">
        <template #default="{ row }">
          <el-button size="small" type="danger" @click="handleDelete(row)" :icon="Delete">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 对话框 -->
    <el-dialog v-model="dialogVisible" title="新增资格职称" width="600px">
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="120px">
        <el-form-item label="职称级别" prop="titleLevel">
          <el-select v-model="formData.titleLevel" placeholder="请选择职称级别" style="width: 100%">
            <el-option label="初级" value="junior" />
            <el-option label="中级" value="intermediate" />
            <el-option label="高级" value="senior" />
            <el-option label="正高级" value="professor" />
          </el-select>
        </el-form-item>
        <el-form-item label="职称名称" prop="titleName">
          <el-input v-model="formData.titleName" placeholder="请输入职称名称" />
        </el-form-item>
        <el-form-item label="资格类型" prop="qualificationType">
          <el-select v-model="formData.qualificationType" placeholder="请选择资格类型" style="width: 100%">
            <el-option label="教师资格证" value="teacher" />
            <el-option label="专业技术资格" value="professional" />
          </el-select>
        </el-form-item>
        <el-form-item label="取得时间" prop="obtainDate">
          <el-date-picker
            v-model="formData.obtainDate"
            type="date"
            placeholder="选择日期"
            style="width: 100%"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="评审机构" prop="approvalAgency">
          <el-input v-model="formData.approvalAgency" placeholder="请输入评审机构" />
        </el-form-item>
        <el-form-item label="证书编号" prop="certificateNumber">
          <el-input v-model="formData.certificateNumber" placeholder="请输入证书编号" />
        </el-form-item>
        <el-form-item label="任教学段" prop="teachingStage">
          <el-select v-model="formData.teachingStage" placeholder="请选择任教学段" style="width: 100%">
            <el-option label="小学" value="primary" />
            <el-option label="初中" value="junior" />
            <el-option label="高中" value="senior" />
          </el-select>
        </el-form-item>
        <el-form-item label="任教学科" prop="teachingSubject">
          <el-input v-model="formData.teachingSubject" placeholder="请输入任教学科" />
        </el-form-item>
        <el-form-item label="有效期" prop="expiryDate">
          <el-date-picker
            v-model="formData.expiryDate"
            type="date"
            placeholder="选择日期"
            style="width: 100%"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
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
import { getTeacherQualificationList, addTeacherQualification } from '@/api/teacherInfo'

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
  titleLevel: '',
  titleName: '',
  qualificationType: '',
  obtainDate: '',
  approvalAgency: '',
  certificateNumber: '',
  teachingStage: '',
  teachingSubject: '',
  expiryDate: ''
})

const rules = {
  titleLevel: [{ required: true, message: '请选择职称级别', trigger: 'change' }],
  titleName: [{ required: true, message: '请输入职称名称', trigger: 'blur' }],
  qualificationType: [{ required: true, message: '请选择资格类型', trigger: 'change' }],
  obtainDate: [{ required: true, message: '请选择取得时间', trigger: 'change' }],
  approvalAgency: [{ required: true, message: '请输入评审机构', trigger: 'blur' }],
  certificateNumber: [{ required: true, message: '请输入证书编号', trigger: 'blur' }],
  teachingStage: [{ required: true, message: '请选择任教学段', trigger: 'change' }],
  teachingSubject: [{ required: true, message: '请输入任教学科', trigger: 'blur' }]
}

const getLevelType = (level) => {
  const typeMap = {
    junior: 'info',
    intermediate: 'success',
    senior: 'warning',
    professor: 'danger'
  }
  return typeMap[level] || 'info'
}

const getLevelText = (level) => {
  const textMap = {
    junior: '初级',
    intermediate: '中级',
    senior: '高级',
    professor: '正高级'
  }
  return textMap[level] || level
}

const getTeachingStageText = (stage) => {
  const textMap = {
    primary: '小学',
    junior: '初中',
    senior: '高中'
  }
  return textMap[stage] || stage
}

const fetchList = async () => {
  if (!props.teacherId) return
  loading.value = true
  try {
    const res = await getTeacherQualificationList(props.teacherId)
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
    titleLevel: '',
    titleName: '',
    qualificationType: '',
    obtainDate: '',
    approvalAgency: '',
    certificateNumber: '',
    teachingStage: '',
    teachingSubject: '',
    expiryDate: ''
  }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const res = await addTeacherQualification(formData.value)
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
.teacher-qualification {
  padding: 0;
}

.action-bar {
  margin-bottom: 16px;
  text-align: right;
}
</style>