<template>
  <div class="teacher-honor">
    <div class="action-bar">
      <el-button type="primary" @click="handleAdd" :icon="Plus">新增荣誉称号</el-button>
    </div>

    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column type="index" label="序号" width="60" align="center" />
      <el-table-column prop="honorType" label="称号类型" width="140" align="center">
        <template #default="{ row }">
          <el-tag :type="getHonorTypeType(row.honorType)">
            {{ getHonorTypeText(row.honorType) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="honorName" label="称号名称" min-width="180" />
      <el-table-column prop="awardDate" label="获得日期" width="120" align="center" />
      <el-table-column prop="awardAgency" label="颁发机构" min-width="150" />
      <el-table-column prop="expiryDate" label="有效期" width="120" align="center" />
      <el-table-column label="操作" width="100" align="center">
        <template #default="{ row }">
          <el-button size="small" type="danger" @click="handleDelete(row)" :icon="Delete">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 对话框 -->
    <el-dialog v-model="dialogVisible" title="新增荣誉称号" width="500px">
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px">
        <el-form-item label="称号类型" prop="honorType">
          <el-select v-model="formData.honorType" placeholder="请选择称号类型" style="width: 100%">
            <el-option label="骨干教师" value="backbone" />
            <el-option label="学科带头人" value="leader" />
            <el-option label="名师" value="master" />
            <el-option label="专家" value="expert" />
          </el-select>
        </el-form-item>
        <el-form-item label="称号名称" prop="honorName">
          <el-input v-model="formData.honorName" placeholder="请输入称号名称" />
        </el-form-item>
        <el-form-item label="获得日期" prop="awardDate">
          <el-date-picker
            v-model="formData.awardDate"
            type="date"
            placeholder="选择日期"
            style="width: 100%"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="颁发机构" prop="awardAgency">
          <el-input v-model="formData.awardAgency" placeholder="请输入颁发机构" />
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
import { getTeacherHonorList, addTeacherHonor } from '@/api/teacherInfo'

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
  honorType: '',
  honorName: '',
  awardDate: '',
  awardAgency: '',
  expiryDate: ''
})

const rules = {
  honorType: [{ required: true, message: '请选择称号类型', trigger: 'change' }],
  honorName: [{ required: true, message: '请输入称号名称', trigger: 'blur' }],
  awardDate: [{ required: true, message: '请选择获得日期', trigger: 'change' }],
  awardAgency: [{ required: true, message: '请输入颁发机构', trigger: 'blur' }]
}

const getHonorTypeType = (type) => {
  const typeMap = {
    backbone: 'info',
    leader: 'success',
    master: 'warning',
    expert: 'danger'
  }
  return typeMap[type] || 'info'
}

const getHonorTypeText = (type) => {
  const textMap = {
    backbone: '骨干教师',
    leader: '学科带头人',
    master: '名师',
    expert: '专家'
  }
  return textMap[type] || type
}

const fetchList = async () => {
  if (!props.teacherId) return
  loading.value = true
  try {
    const res = await getTeacherHonorList(props.teacherId)
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
    honorType: '',
    honorName: '',
    awardDate: '',
    awardAgency: '',
    expiryDate: ''
  }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const res = await addTeacherHonor(formData.value)
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
.teacher-honor {
  padding: 0;
}

.action-bar {
  margin-bottom: 16px;
  text-align: right;
}
</style>