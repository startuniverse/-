<template>
  <div class="teacher-research">
    <div class="action-bar">
      <el-button type="primary" @click="handleAdd" :icon="Plus">新增教研活动</el-button>
    </div>

    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column type="index" label="序号" width="60" align="center" />
      <el-table-column prop="activityType" label="活动类型" width="120" align="center">
        <template #default="{ row }">
          <el-tag :type="getActivityTypeType(row.activityType)">
            {{ getActivityTypeText(row.activityType) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="activityName" label="活动名称" min-width="200" />
      <el-table-column prop="activityDate" label="活动日期" width="120" align="center" />
      <el-table-column prop="activityLevel" label="活动级别" width="100" align="center">
        <template #default="{ row }">
          {{ getActivityLevelText(row.activityLevel) }}
        </template>
      </el-table-column>
      <el-table-column prop="role" label="担任角色" width="100" align="center">
        <template #default="{ row }">
          {{ getRoleText(row.role) }}
        </template>
      </el-table-column>
      <el-table-column prop="creditHours" label="学时/学分" width="100" align="center" />
      <el-table-column prop="achievement" label="成果描述" min-width="180" />
      <el-table-column prop="proofUrl" label="证明文件" width="100" align="center">
        <template #default="{ row }">
          <el-link v-if="row.proofUrl" :href="row.proofUrl" type="primary" target="_blank">查看</el-link>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column prop="reviewStatus" label="审核状态" width="100" align="center">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.reviewStatus)">
            {{ getStatusText(row.reviewStatus) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="reviewerName" label="审核人" width="100" align="center" />
      <el-table-column prop="reviewDate" label="审核日期" width="120" align="center" />
      <el-table-column label="操作" width="100" align="center">
        <template #default="{ row }">
          <el-button size="small" type="danger" @click="handleDelete(row)" :icon="Delete">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 对话框 -->
    <el-dialog v-model="dialogVisible" title="新增教研活动" width="700px">
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="活动类型" prop="activityType">
              <el-select v-model="formData.activityType" placeholder="请选择活动类型" style="width: 100%">
                <el-option label="讲座" value="lecture" />
                <el-option label="研讨会" value="seminar" />
                <el-option label="培训" value="training" />
                <el-option label="竞赛" value="competition" />
                <el-option label="课题研究" value="project" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="活动级别" prop="activityLevel">
              <el-select v-model="formData.activityLevel" placeholder="请选择活动级别" style="width: 100%">
                <el-option label="校级" value="school" />
                <el-option label="区级" value="district" />
                <el-option label="市级" value="city" />
                <el-option label="省级" value="provincial" />
                <el-option label="国家级" value="national" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="活动名称" prop="activityName">
          <el-input v-model="formData.activityName" placeholder="请输入活动名称" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="活动日期" prop="activityDate">
              <el-date-picker
                v-model="formData.activityDate"
                type="date"
                placeholder="选择日期"
                style="width: 100%"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="学时/学分" prop="creditHours">
              <el-input v-model="formData.creditHours" type="number" placeholder="请输入学时/学分" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="担任角色" prop="role">
          <el-select v-model="formData.role" placeholder="请选择担任角色" style="width: 100%">
            <el-option label="主持人" value="host" />
            <el-option label="主讲人" value="speaker" />
            <el-option label="参与者" value="participant" />
            <el-option label="组织者" value="organizer" />
          </el-select>
        </el-form-item>
        <el-form-item label="成果描述" prop="achievement">
          <el-input
            v-model="formData.achievement"
            type="textarea"
            :rows="2"
            placeholder="请输入成果描述"
          />
        </el-form-item>
        <el-form-item label="证明文件URL" prop="proofUrl">
          <el-input v-model="formData.proofUrl" placeholder="请输入证明文件链接地址" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="审核状态" prop="reviewStatus">
              <el-select v-model="formData.reviewStatus" placeholder="请选择审核状态" style="width: 100%">
                <el-option label="待审核" value="pending" />
                <el-option label="已通过" value="approved" />
                <el-option label="已驳回" value="rejected" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="审核人ID" prop="reviewerId">
              <el-input v-model="formData.reviewerId" type="number" placeholder="请输入审核人ID" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="审核人姓名" prop="reviewerName">
              <el-input v-model="formData.reviewerName" placeholder="请输入审核人姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="审核日期" prop="reviewDate">
              <el-date-picker
                v-model="formData.reviewDate"
                type="date"
                placeholder="选择日期"
                style="width: 100%"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
              />
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
import { getTeacherResearchList, addTeacherResearch } from '@/api/teacherInfo'

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
  activityType: '',
  activityName: '',
  activityDate: '',
  activityLevel: '',
  role: '',
  creditHours: null,
  achievement: '',
  proofUrl: '',
  reviewStatus: '',
  reviewerId: null,
  reviewerName: '',
  reviewDate: ''
})

const rules = {
  activityType: [{ required: true, message: '请选择活动类型', trigger: 'change' }],
  activityName: [{ required: true, message: '请输入活动名称', trigger: 'blur' }],
  activityDate: [{ required: true, message: '请选择活动日期', trigger: 'change' }],
  activityLevel: [{ required: true, message: '请选择活动级别', trigger: 'change' }],
  role: [{ required: true, message: '请选择担任角色', trigger: 'change' }],
  creditHours: [{ required: true, message: '请输入学时/学分', trigger: 'blur' }],
  achievement: [{ required: true, message: '请输入成果描述', trigger: 'blur' }]
}

const getActivityTypeType = (type) => {
  const typeMap = {
    lecture: 'info',
    seminar: 'success',
    training: 'warning',
    competition: 'danger',
    project: 'primary'
  }
  return typeMap[type] || 'info'
}

const getActivityTypeText = (type) => {
  const textMap = {
    lecture: '讲座',
    seminar: '研讨会',
    training: '培训',
    competition: '竞赛',
    project: '课题研究'
  }
  return textMap[type] || type
}

const getActivityLevelText = (level) => {
  const textMap = {
    school: '校级',
    district: '区级',
    city: '市级',
    provincial: '省级',
    national: '国家级'
  }
  return textMap[level] || level
}

const getRoleText = (role) => {
  const textMap = {
    host: '主持人',
    speaker: '主讲人',
    participant: '参与者',
    organizer: '组织者'
  }
  return textMap[role] || role
}

const getStatusType = (status) => {
  const typeMap = {
    pending: 'warning',
    approved: 'success',
    rejected: 'danger'
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    pending: '待审核',
    approved: '已通过',
    rejected: '已驳回'
  }
  return textMap[status] || status
}

const fetchList = async () => {
  if (!props.teacherId) return
  loading.value = true
  try {
    const res = await getTeacherResearchList(props.teacherId)
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
    activityType: '',
    activityName: '',
    activityDate: '',
    activityLevel: '',
    role: '',
    creditHours: null,
    achievement: '',
    proofUrl: '',
    reviewStatus: '',
    reviewerId: null,
    reviewerName: '',
    reviewDate: ''
  }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const res = await addTeacherResearch(formData.value)
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
.teacher-research {
  padding: 0;
}

.action-bar {
  margin-bottom: 16px;
  text-align: right;
}
</style>