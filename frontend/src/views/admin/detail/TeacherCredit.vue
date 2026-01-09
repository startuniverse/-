<template>
  <div class="teacher-credit">
    <div class="action-bar">
      <el-button type="primary" @click="handleAdd" :icon="Plus">新增学分记录</el-button>
    </div>

    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column type="index" label="序号" width="60" align="center" />
      <el-table-column prop="academicYear" label="学年度" width="100" align="center" />
      <el-table-column prop="creditType" label="学分类型" width="140" align="center">
        <template #default="{ row }">
          <el-tag :type="getCreditTypeType(row.creditType)">
            {{ getCreditTypeText(row.creditType) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="projectName" label="项目名称" min-width="200" />
      <el-table-column prop="creditHours" label="学分数" width="100" align="center" />
      <el-table-column prop="certificationStatus" label="认证状态" width="120" align="center">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.certificationStatus)">
            {{ getStatusText(row.certificationStatus) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="certificationDate" label="认证日期" width="120" align="center" />
      <el-table-column prop="certifierName" label="认证人" width="120" align="center" />
      <el-table-column prop="remarks" label="备注" min-width="150" />
      <el-table-column label="操作" width="100" align="center">
        <template #default="{ row }">
          <el-button size="small" type="danger" @click="handleDelete(row)" :icon="Delete">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 对话框 -->
    <el-dialog v-model="dialogVisible" title="新增学分记录" width="600px">
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px">
        <el-form-item label="学年度" prop="academicYear">
          <el-input v-model="formData.academicYear" placeholder="请输入学年度，如：2024-2025" />
        </el-form-item>
        <el-form-item label="学分类型" prop="creditType">
          <el-select v-model="formData.creditType" placeholder="请选择学分类型" style="width: 100%">
            <el-option label="培训学习" value="training" />
            <el-option label="教研活动" value="research" />
            <el-option label="学术研修" value="academic" />
            <el-option label="在线学习" value="online" />
          </el-select>
        </el-form-item>
        <el-form-item label="项目名称" prop="projectName">
          <el-input v-model="formData.projectName" placeholder="请输入项目名称" />
        </el-form-item>
        <el-form-item label="学分数" prop="creditHours">
          <el-input v-model="formData.creditHours" type="number" placeholder="请输入学分数" />
        </el-form-item>
        <el-form-item label="认证状态" prop="certificationStatus">
          <el-select v-model="formData.certificationStatus" placeholder="请选择认证状态" style="width: 100%">
            <el-option label="待认证" value="pending" />
            <el-option label="已认证" value="certified" />
            <el-option label="已驳回" value="rejected" />
          </el-select>
        </el-form-item>
        <el-form-item label="认证日期" prop="certificationDate">
          <el-date-picker
            v-model="formData.certificationDate"
            type="date"
            placeholder="选择日期"
            style="width: 100%"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="认证人ID" prop="certifierId">
          <el-input v-model="formData.certifierId" type="number" placeholder="请输入认证人ID" />
        </el-form-item>
        <el-form-item label="认证人姓名" prop="certifierName">
          <el-input v-model="formData.certifierName" placeholder="请输入认证人姓名" />
        </el-form-item>
        <el-form-item label="备注" prop="remarks">
          <el-input
            v-model="formData.remarks"
            type="textarea"
            :rows="2"
            placeholder="请输入备注"
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
import { getTeacherCreditList, addTeacherCredit } from '@/api/teacherInfo'

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
  creditType: '',
  projectName: '',
  creditHours: null,
  certificationStatus: '',
  certificationDate: '',
  certifierId: null,
  certifierName: '',
  remarks: ''
})

const rules = {
  academicYear: [{ required: true, message: '请输入学年度', trigger: 'blur' }],
  creditType: [{ required: true, message: '请选择学分类型', trigger: 'change' }],
  projectName: [{ required: true, message: '请输入项目名称', trigger: 'blur' }],
  creditHours: [{ required: true, message: '请输入学分数', trigger: 'blur' }],
  certificationStatus: [{ required: true, message: '请选择认证状态', trigger: 'change' }]
}

const getCreditTypeType = (type) => {
  const typeMap = {
    training: 'info',
    research: 'success',
    academic: 'warning',
    online: 'primary'
  }
  return typeMap[type] || 'info'
}

const getCreditTypeText = (type) => {
  const textMap = {
    training: '培训学习',
    research: '教研活动',
    academic: '学术研修',
    online: '在线学习'
  }
  return textMap[type] || type
}

const getStatusType = (status) => {
  const typeMap = {
    pending: 'warning',
    certified: 'success',
    rejected: 'danger'
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    pending: '待认证',
    certified: '已认证',
    rejected: '已驳回'
  }
  return textMap[status] || status
}

const fetchList = async () => {
  if (!props.teacherId) return
  loading.value = true
  try {
    const res = await getTeacherCreditList(props.teacherId)
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
    creditType: '',
    projectName: '',
    creditHours: null,
    certificationStatus: '',
    certificationDate: '',
    certifierId: null,
    certifierName: '',
    remarks: ''
  }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const res = await addTeacherCredit(formData.value)
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
.teacher-credit {
  padding: 0;
}

.action-bar {
  margin-bottom: 16px;
  text-align: right;
}
</style>