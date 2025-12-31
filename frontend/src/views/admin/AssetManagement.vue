<template>
  <div class="asset-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>资产管理</span>
          <div class="controls">
            <el-select v-model="category" placeholder="资产分类" size="small" style="width: 120px;" clearable>
              <el-option label="设备" value="equipment"></el-option>
              <el-option label="家具" value="furniture"></el-option>
              <el-option label="电子" value="electronic"></el-option>
            </el-select>
            <el-button type="primary" size="small" @click="loadAssets">查询</el-button>
            <el-button type="success" size="small" @click="handleAdd">新增</el-button>
          </div>
        </div>
      </template>

      <el-table v-loading="loading" :data="assetsData" border style="width: 100%">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="assetName" label="资产名称" min-width="150" />
        <el-table-column prop="category" label="分类" width="100" align="center">
          <template #default="scope">
            <el-tag type="info">{{ getCategoryText(scope.row.category) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="quantity" label="数量" width="80" align="center" />
        <el-table-column prop="totalValue" label="总价值" width="120" align="center">
          <template #default="scope">
            ¥{{ scope.row.totalValue }}
          </template>
        </el-table-column>
        <el-table-column prop="location" label="存放位置" width="120" align="center" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="scope">
            <el-tag :type="getStatusTag(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" align="center" fixed="right">
          <template #default="scope">
            <el-button type="primary" link size="small" @click="handleEdit(scope.row)">
              编辑
            </el-button>
            <el-button type="danger" link size="small" @click="handleDelete(scope.row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 编辑/新增对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑资产' : '新增资产'"
      width="600px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="120px"
      >
        <el-form-item label="资产名称" prop="assetName">
          <el-input v-model="form.assetName" placeholder="请输入资产名称" />
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-select v-model="form.category" placeholder="请选择分类" style="width: 100%;">
            <el-option label="设备" value="equipment"></el-option>
            <el-option label="家具" value="furniture"></el-option>
            <el-option label="电子" value="electronic"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="规格型号" prop="specification">
          <el-input v-model="form.specification" placeholder="请输入规格型号" />
        </el-form-item>
        <el-form-item label="数量" prop="quantity">
          <el-input-number v-model="form.quantity" :min="1" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="单价" prop="unitPrice">
          <el-input v-model.number="form.unitPrice" placeholder="请输入单价">
            <template #prefix>¥</template>
          </el-input>
        </el-form-item>
        <el-form-item label="存放位置" prop="location">
          <el-input v-model="form.location" placeholder="请输入存放位置" />
        </el-form-item>
        <el-form-item label="责任人" prop="responsiblePerson">
          <el-input v-model="form.responsiblePerson" placeholder="请输入责任人" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态" style="width: 100%;">
            <el-option label="正常" :value="1"></el-option>
            <el-option label="维修中" :value="2"></el-option>
            <el-option label="报废" :value="3"></el-option>
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
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAssets, addAsset, updateAsset, deleteAsset } from '@/api/admin'
import { useUserStore } from '@/store/modules/user'

const userStore = useUserStore()
const loading = ref(false)
const category = ref('')
const assetsData = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const form = ref({
  assetName: '',
  category: '',
  specification: '',
  quantity: 1,
  unitPrice: 0,
  location: '',
  responsiblePerson: '',
  status: 1
})

const rules = {
  assetName: [{ required: true, message: '请输入资产名称', trigger: 'blur' }],
  category: [{ required: true, message: '请选择分类', trigger: 'change' }],
  quantity: [{ required: true, message: '请输入数量', trigger: 'blur' }],
  unitPrice: [{ required: true, message: '请输入单价', trigger: 'blur' }]
}

const getCategoryText = (category) => {
  const map = {
    equipment: '设备',
    furniture: '家具',
    electronic: '电子'
  }
  return map[category] || category
}

const getStatusText = (status) => {
  const map = {
    1: '正常',
    2: '维修中',
    3: '报废'
  }
  return map[status] || '未知'
}

const getStatusTag = (status) => {
  const map = {
    1: 'success',
    2: 'warning',
    3: 'danger'
  }
  return map[status] || 'info'
}

const loadAssets = async () => {
  try {
    loading.value = true
    const schoolId = userStore.userInfo?.schoolId || 1
    const params = {
      schoolId,
      current: currentPage.value,
      size: pageSize.value
    }
    if (category.value) params.category = category.value

    const data = await getAssets(params)
    assetsData.value = data.records
    total.value = data.total
  } catch (error) {
    ElMessage.error('加载资产列表失败')
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  isEdit.value = false
  form.value = {
    assetName: '',
    category: '',
    specification: '',
    quantity: 1,
    unitPrice: 0,
    location: '',
    responsiblePerson: '',
    status: 1
  }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  form.value = { ...row }
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  ElMessageBox.confirm(`确定要删除资产 ${row.assetName} 吗？`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteAsset(row.id)
      ElMessage.success('删除成功')
      loadAssets()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        // 计算总价值
        const totalValue = (form.value.unitPrice * form.value.quantity).toFixed(2)

        // 构建提交数据
        const submitData = {
          assetName: form.value.assetName,
          category: form.value.category,
          specification: form.value.specification,
          quantity: form.value.quantity,
          unitPrice: form.value.unitPrice,
          totalValue: totalValue,
          location: form.value.location,
          responsiblePerson: form.value.responsiblePerson,
          status: form.value.status
        }

        if (isEdit.value) {
          // 编辑模式：需要传递id
          submitData.id = form.value.id
          await updateAsset(submitData)
          ElMessage.success('更新成功')
        } else {
          // 新增模式
          await addAsset(submitData)
          ElMessage.success('新增成功')
        }

        dialogVisible.value = false
        loadAssets()
      } catch (error) {
        ElMessage.error(isEdit.value ? '更新失败' : '新增失败')
      }
    }
  })
}

const handleSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1
  loadAssets()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  loadAssets()
}

onMounted(() => {
  loadAssets()
})
</script>

<style scoped>
.asset-management {
  width: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.controls {
  display: flex;
  gap: 10px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>
