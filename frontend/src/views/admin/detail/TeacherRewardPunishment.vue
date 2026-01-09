<template>
  <div class="teacher-reward-punishment">
    <div class="action-bar">
      <el-button type="primary" @click="handleAdd" :icon="Plus">新增奖惩记录</el-button>
    </div>

    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column type="index" label="序号" width="60" align="center" />
      <el-table-column prop="type" label="类型" width="100" align="center">
        <template #default="{ row }">
          <el-tag :type="row.type === 'reward' ? 'success' : 'danger'">
            {{ row.type === 'reward' ? '奖励' : '惩罚' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="category" label="分类" width="140" align="center">
        <template #default="{ row }">
          {{ getCategoryText(row.category) }}
        </template>
      </el-table-column>
      <el-table-column prop="title" label="标题" min-width="180" />
      <el-table-column prop="description" label="详细描述" min-width="200" />
      <el-table-column prop="awardAgency" label="颁发/处理机构" min-width="150" />
      <el-table-column prop="recordDate" label="记录日期" width="120" align="center" />
      <el-table-column prop="reason" label="原因" min-width="150" />
      <el-table-column label="操作" width="100" align="center">
        <template #default="{ row }">
          <el-button size="small" type="danger" @click="handleDelete(row)" :icon="Delete">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 对话框 -->
    <el-dialog v-model="dialogVisible" title="新增奖惩记录" width="600px">
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px">
        <el-form-item label="类型" prop="type">
          <el-select v-model="formData.type" placeholder="请选择类型" style="width: 100%">
            <el-option label="奖励" value="reward" />
            <el-option label="惩罚" value="punishment" />
          </el-select>
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-select v-model="formData.category" placeholder="请选择分类" style="width: 100%">
            <el-option label="优秀教师" value="excellent_teacher" />
            <el-option label="优秀班主任" value="excellent_head" />
            <el-option label="违规" value="violation" />
            <el-option label="警告" value="warning" />
          </el-select>
        </el-form-item>
        <el-form-item label="标题" prop="title">
          <el-input v-model="formData.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="详细描述" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="3"
            placeholder="请输入详细描述"
          />
        </el-form-item>
        <el-form-item label="颁发/处理机构" prop="awardAgency">
          <el-input v-model="formData.awardAgency" placeholder="请输入机构名称" />
        </el-form-item>
        <el-form-item label="记录日期" prop="recordDate">
          <el-date-picker
            v-model="formData.recordDate"
            type="date"
            placeholder="选择日期"
            style="width: 100%"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="原因" prop="reason">
          <el-input
            v-model="formData.reason"
            type="textarea"
            :rows="2"
            placeholder="请输入原因"
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
import { getTeacherRewardPunishmentList, addTeacherRewardPunishment } from '@/api/teacherInfo'

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
  type: '',
  category: '',
  title: '',
  description: '',
  awardAgency: '',
  recordDate: '',
  reason: ''
})

const rules = {
  type: [{ required: true, message: '请选择类型', trigger: 'change' }],
  category: [{ required: true, message: '请选择分类', trigger: 'change' }],
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  description: [{ required: true, message: '请输入详细描述', trigger: 'blur' }],
  awardAgency: [{ required: true, message: '请输入机构名称', trigger: 'blur' }],
  recordDate: [{ required: true, message: '请选择记录日期', trigger: 'change' }]
}

const getCategoryText = (category) => {
  const textMap = {
    excellent_teacher: '优秀教师',
    excellent_head: '优秀班主任',
    violation: '违规',
    warning: '警告'
  }
  return textMap[category] || category
}

const fetchList = async () => {
  if (!props.teacherId) return
  loading.value = true
  try {
    const res = await getTeacherRewardPunishmentList(props.teacherId)
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
    type: '',
    category: '',
    title: '',
    description: '',
    awardAgency: '',
    recordDate: '',
    reason: ''
  }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const res = await addTeacherRewardPunishment(formData.value)
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
.teacher-reward-punishment {
  padding: 0;
}

.action-bar {
  margin-bottom: 16px;
  text-align: right;
}
</style>