<template>
  <div class="teacher-assessment">
    <div class="action-bar">
      <el-button type="primary" @click="handleAdd" :icon="Plus">新增考核记录</el-button>
    </div>

    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column type="index" label="序号" width="60" align="center" />
      <el-table-column prop="assessmentYear" label="考核年度" width="100" align="center" />
      <el-table-column prop="assessmentResult" label="考核结果" width="120" align="center">
        <template #default="{ row }">
          <el-tag :type="getResultType(row.assessmentResult)">
            {{ getResultText(row.assessmentResult) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="assessmentOpinion" label="考核意见" min-width="200" />
      <el-table-column prop="assessorName" label="考核人" width="120" align="center" />
      <el-table-column prop="assessmentDate" label="考核日期" width="120" align="center" />
      <el-table-column label="操作" width="100" align="center">
        <template #default="{ row }">
          <el-button size="small" type="danger" @click="handleDelete(row)" :icon="Delete">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 对话框 -->
    <el-dialog v-model="dialogVisible" title="新增考核记录" width="600px">
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px">
        <el-form-item label="考核年度" prop="assessmentYear">
          <el-input v-model="formData.assessmentYear" placeholder="请输入考核年度，如：2024" />
        </el-form-item>
        <el-form-item label="考核结果" prop="assessmentResult">
          <el-select v-model="formData.assessmentResult" placeholder="请选择考核结果" style="width: 100%">
            <el-option label="优秀" value="excellent" />
            <el-option label="良好" value="good" />
            <el-option label="合格" value="qualified" />
            <el-option label="不合格" value="unqualified" />
          </el-select>
        </el-form-item>
        <el-form-item label="考核意见" prop="assessmentOpinion">
          <el-input
            v-model="formData.assessmentOpinion"
            type="textarea"
            :rows="3"
            placeholder="请输入考核意见"
          />
        </el-form-item>
        <el-form-item label="考核人ID" prop="assessorId">
          <el-input v-model="formData.assessorId" type="number" placeholder="请输入考核人ID" />
        </el-form-item>
        <el-form-item label="考核人姓名" prop="assessorName">
          <el-input v-model="formData.assessorName" placeholder="请输入考核人姓名" />
        </el-form-item>
        <el-form-item label="考核日期" prop="assessmentDate">
          <el-date-picker
            v-model="formData.assessmentDate"
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
import { getTeacherAssessmentList, addTeacherAssessment } from '@/api/teacherInfo'

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
  assessmentYear: '',
  assessmentResult: '',
  assessmentOpinion: '',
  assessorId: null,
  assessorName: '',
  assessmentDate: ''
})

const rules = {
  assessmentYear: [{ required: true, message: '请输入考核年度', trigger: 'blur' }],
  assessmentResult: [{ required: true, message: '请选择考核结果', trigger: 'change' }],
  assessmentOpinion: [{ required: true, message: '请输入考核意见', trigger: 'blur' }],
  assessorId: [{ required: true, message: '请输入考核人ID', trigger: 'blur' }],
  assessorName: [{ required: true, message: '请输入考核人姓名', trigger: 'blur' }],
  assessmentDate: [{ required: true, message: '请选择考核日期', trigger: 'change' }]
}

const getResultType = (result) => {
  const typeMap = {
    excellent: 'success',
    good: 'primary',
    qualified: 'info',
    unqualified: 'danger'
  }
  return typeMap[result] || 'info'
}

const getResultText = (result) => {
  const textMap = {
    excellent: '优秀',
    good: '良好',
    qualified: '合格',
    unqualified: '不合格'
  }
  return textMap[result] || result
}

const fetchList = async () => {
  if (!props.teacherId) return
  loading.value = true
  try {
    const res = await getTeacherAssessmentList(props.teacherId)
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
    assessmentYear: '',
    assessmentResult: '',
    assessmentOpinion: '',
    assessorId: null,
    assessorName: '',
    assessmentDate: ''
  }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const res = await addTeacherAssessment(formData.value)
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
.teacher-assessment {
  padding: 0;
}

.action-bar {
  margin-bottom: 16px;
  text-align: right;
}
</style>