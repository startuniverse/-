<template>
  <div class="teacher-training">
    <div class="action-bar">
      <el-button type="primary" @click="handleAdd" :icon="Plus">新增培训记录</el-button>
    </div>

    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column type="index" label="序号" width="60" align="center" />
      <el-table-column prop="trainingName" label="培训项目名称" min-width="200" />
      <el-table-column prop="trainingDate" label="培训日期" width="120" align="center" />
      <el-table-column prop="duration" label="学时" width="80" align="center" />
      <el-table-column prop="trainingAgency" label="培训机构" min-width="150" />
      <el-table-column prop="result" label="成绩/结业情况" min-width="120" />
      <el-table-column prop="category" label="培训分类" width="120" align="center">
        <template #default="{ row }">
          {{ getCategoryText(row.category) }}
        </template>
      </el-table-column>
      <el-table-column prop="certificateUrl" label="证书" width="100" align="center">
        <template #default="{ row }">
          <el-link v-if="row.certificateUrl" :href="row.certificateUrl" type="primary" target="_blank">查看</el-link>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="100" align="center">
        <template #default="{ row }">
          <el-button size="small" type="danger" @click="handleDelete(row)" :icon="Delete">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 对话框 -->
    <el-dialog v-model="dialogVisible" title="新增培训记录" width="600px">
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px">
        <el-form-item label="培训项目" prop="trainingName">
          <el-input v-model="formData.trainingName" placeholder="请输入培训项目名称" />
        </el-form-item>
        <el-form-item label="培训日期" prop="trainingDate">
          <el-date-picker
            v-model="formData.trainingDate"
            type="date"
            placeholder="选择日期"
            style="width: 100%"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="学时" prop="duration">
          <el-input v-model="formData.duration" type="number" placeholder="请输入学时">
            <template #append>小时</template>
          </el-input>
        </el-form-item>
        <el-form-item label="培训机构" prop="trainingAgency">
          <el-input v-model="formData.trainingAgency" placeholder="请输入培训机构" />
        </el-form-item>
        <el-form-item label="成绩/结业" prop="result">
          <el-input v-model="formData.result" placeholder="请输入成绩或结业情况" />
        </el-form-item>
        <el-form-item label="证书URL" prop="certificateUrl">
          <el-input v-model="formData.certificateUrl" placeholder="请输入证书链接地址" />
        </el-form-item>
        <el-form-item label="培训分类" prop="category">
          <el-select v-model="formData.category" placeholder="请选择培训分类" style="width: 100%">
            <el-option label="教学技能" value="skill" />
            <el-option label="信息化" value="info" />
            <el-option label="师德" value="ethics" />
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
import { getTeacherTrainingList, addTeacherTraining } from '@/api/teacherInfo'

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
  trainingName: '',
  trainingDate: '',
  duration: null,
  trainingAgency: '',
  result: '',
  certificateUrl: '',
  category: ''
})

const rules = {
  trainingName: [{ required: true, message: '请输入培训项目名称', trigger: 'blur' }],
  trainingDate: [{ required: true, message: '请选择培训日期', trigger: 'change' }],
  duration: [{ required: true, message: '请输入学时', trigger: 'blur' }],
  trainingAgency: [{ required: true, message: '请输入培训机构', trigger: 'blur' }],
  category: [{ required: true, message: '请选择培训分类', trigger: 'change' }]
}

const getCategoryText = (category) => {
  const textMap = {
    skill: '教学技能',
    info: '信息化',
    ethics: '师德'
  }
  return textMap[category] || category
}

const fetchList = async () => {
  if (!props.teacherId) return
  loading.value = true
  try {
    const res = await getTeacherTrainingList(props.teacherId)
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
    trainingName: '',
    trainingDate: '',
    duration: null,
    trainingAgency: '',
    result: '',
    certificateUrl: '',
    category: ''
  }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const res = await addTeacherTraining(formData.value)
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
.teacher-training {
  padding: 0;
}

.action-bar {
  margin-bottom: 16px;
  text-align: right;
}
</style>