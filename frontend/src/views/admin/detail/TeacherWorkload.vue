<template>
  <div class="teacher-workload">
    <div class="action-bar">
      <el-button type="primary" @click="handleAdd" :icon="Plus">新增工作量统计</el-button>
    </div>

    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column type="index" label="序号" width="60" align="center" />
      <el-table-column prop="statisticalYear" label="统计年度" width="100" align="center" />
      <el-table-column prop="statisticalPeriod" label="统计周期" width="100" align="center">
        <template #default="{ row }">
          {{ row.statisticalPeriod === 'semester' ? '学期' : '学年' }}
        </template>
      </el-table-column>
      <el-table-column prop="teachingWorkload" label="教学工作量" width="110" align="center">
        <template #default="{ row }">
          {{ row.teachingWorkload }} 课时
        </template>
      </el-table-column>
      <el-table-column prop="researchWorkload" label="教研工作量" width="110" align="center">
        <template #default="{ row }">
          {{ row.researchWorkload }} 小时
        </template>
      </el-table-column>
      <el-table-column prop="managementWorkload" label="管理工作量" width="110" align="center">
        <template #default="{ row }">
          {{ row.managementWorkload }} 小时
        </template>
      </el-table-column>
      <el-table-column prop="guidanceWorkload" label="指导工作量" width="110" align="center">
        <template #default="{ row }">
          {{ row.guidanceWorkload }} 小时
        </template>
      </el-table-column>
      <el-table-column prop="otherWorkload" label="其他工作量" width="110" align="center">
        <template #default="{ row }">
          {{ row.otherWorkload }} 小时
        </template>
      </el-table-column>
      <el-table-column prop="totalWorkload" label="总工作量" width="110" align="center">
        <template #default="{ row }">
          <el-tag type="success">{{ row.totalWorkload }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="standardWorkload" label="标准工作量" width="110" align="center" />
      <el-table-column prop="completionRate" label="完成率" width="100" align="center">
        <template #default="{ row }">
          <el-tag :type="getCompletionRateType(row.completionRate)">
            {{ row.completionRate }}%
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="overtimePay" label="超课时费" width="100" align="center">
        <template #default="{ row }">
          ¥{{ row.overtimePay }}
        </template>
      </el-table-column>
      <el-table-column prop="remarks" label="备注" min-width="150" />
      <el-table-column label="操作" width="100" align="center">
        <template #default="{ row }">
          <el-button size="small" type="danger" @click="handleDelete(row)" :icon="Delete">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 对话框 -->
    <el-dialog v-model="dialogVisible" title="新增工作量统计" width="700px">
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="统计年度" prop="statisticalYear">
              <el-input v-model="formData.statisticalYear" placeholder="如：2024" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="统计周期" prop="statisticalPeriod">
              <el-select v-model="formData.statisticalPeriod" placeholder="请选择统计周期" style="width: 100%">
                <el-option label="学期" value="semester" />
                <el-option label="学年" value="year" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="教学工作量" prop="teachingWorkload">
              <el-input v-model="formData.teachingWorkload" type="number" step="0.01" placeholder="请输入课时数" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="教研工作量" prop="researchWorkload">
              <el-input v-model="formData.researchWorkload" type="number" step="0.01" placeholder="请输入小时数" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="管理工作量" prop="managementWorkload">
              <el-input v-model="formData.managementWorkload" type="number" step="0.01" placeholder="请输入小时数" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="指导工作量" prop="guidanceWorkload">
              <el-input v-model="formData.guidanceWorkload" type="number" step="0.01" placeholder="请输入小时数" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="其他工作量" prop="otherWorkload">
              <el-input v-model="formData.otherWorkload" type="number" step="0.01" placeholder="请输入小时数" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="总工作量" prop="totalWorkload">
              <el-input v-model="formData.totalWorkload" type="number" step="0.01" placeholder="系统自动计算" :disabled="true" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="标准工作量" prop="standardWorkload">
              <el-input v-model="formData.standardWorkload" type="number" step="0.01" placeholder="请输入标准值" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="完成率(%)" prop="completionRate">
              <el-input v-model="formData.completionRate" type="number" step="0.01" placeholder="请输入完成率" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="超课时费" prop="overtimePay">
              <el-input v-model="formData.overtimePay" type="number" step="0.01" placeholder="请输入金额" />
            </el-form-item>
          </el-col>
        </el-row>
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
import { ref, onMounted, watch, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Delete } from '@element-plus/icons-vue'
import { getTeacherWorkloadList, addTeacherWorkload } from '@/api/teacherInfo'

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
  statisticalYear: '',
  statisticalPeriod: '',
  teachingWorkload: null,
  researchWorkload: null,
  managementWorkload: null,
  guidanceWorkload: null,
  otherWorkload: null,
  totalWorkload: null,
  standardWorkload: null,
  completionRate: null,
  overtimePay: null,
  remarks: ''
})

const rules = {
  statisticalYear: [{ required: true, message: '请输入统计年度', trigger: 'blur' }],
  statisticalPeriod: [{ required: true, message: '请选择统计周期', trigger: 'change' }],
  teachingWorkload: [{ required: true, message: '请输入教学工作量', trigger: 'blur' }],
  researchWorkload: [{ required: true, message: '请输入教研工作量', trigger: 'blur' }],
  managementWorkload: [{ required: true, message: '请输入管理工作量', trigger: 'blur' }],
  guidanceWorkload: [{ required: true, message: '请输入指导工作量', trigger: 'blur' }],
  otherWorkload: [{ required: true, message: '请输入其他工作量', trigger: 'blur' }],
  standardWorkload: [{ required: true, message: '请输入标准工作量', trigger: 'blur' }],
  completionRate: [{ required: true, message: '请输入完成率', trigger: 'blur' }],
  overtimePay: [{ required: true, message: '请输入超课时费', trigger: 'blur' }]
}

// 自动计算总工作量
const calculateTotalWorkload = () => {
  const { teachingWorkload, researchWorkload, managementWorkload, guidanceWorkload, otherWorkload } = formData.value
  if (teachingWorkload !== null && researchWorkload !== null &&
      managementWorkload !== null && guidanceWorkload !== null && otherWorkload !== null) {
    formData.value.totalWorkload = parseFloat(teachingWorkload) + parseFloat(researchWorkload) +
                                   parseFloat(managementWorkload) + parseFloat(guidanceWorkload) +
                                   parseFloat(otherWorkload)
  }
}

// 监听工作量字段变化
watch(() => [
  formData.value.teachingWorkload,
  formData.value.researchWorkload,
  formData.value.managementWorkload,
  formData.value.guidanceWorkload,
  formData.value.otherWorkload
], () => {
  calculateTotalWorkload()
})

const getCompletionRateType = (rate) => {
  if (rate >= 100) return 'success'
  if (rate >= 80) return 'warning'
  return 'danger'
}

const fetchList = async () => {
  if (!props.teacherId) return
  loading.value = true
  try {
    const res = await getTeacherWorkloadList(props.teacherId)
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
    statisticalYear: '',
    statisticalPeriod: '',
    teachingWorkload: null,
    researchWorkload: null,
    managementWorkload: null,
    guidanceWorkload: null,
    otherWorkload: null,
    totalWorkload: null,
    standardWorkload: null,
    completionRate: null,
    overtimePay: null,
    remarks: ''
  }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const res = await addTeacherWorkload(formData.value)
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
.teacher-workload {
  padding: 0;
}

.action-bar {
  margin-bottom: 16px;
  text-align: right;
}
</style>