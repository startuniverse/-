<template>
  <div class="document-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>文档管理</span>
          <div class="controls">
            <el-button type="primary" size="small" @click="handleAdd">发布文档</el-button>
          </div>
        </div>
      </template>

      <el-table v-loading="loading" :data="documentsData" border style="width: 100%">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="title" label="标题" min-width="200" />
        <el-table-column prop="category" label="分类" width="100" align="center">
          <template #default="scope">
            <el-tag type="info">{{ getCategoryText(scope.row.category) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="scope">
            <el-tag :type="getStatusTag(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="publishTime" label="发布时间" width="160" align="center" />
        <el-table-column label="操作" width="220" align="center" fixed="right">
          <template #default="scope">
            <el-button
              v-if="scope.row.status === 1"
              type="success"
              link
              size="small"
              @click="handleApprove(scope.row, 2)"
            >
              通过
            </el-button>
            <el-button
              v-if="scope.row.status === 1"
              type="danger"
              link
              size="small"
              @click="handleApprove(scope.row, 3)"
            >
              驳回
            </el-button>
            <el-button type="primary" link size="small" @click="handleView(scope.row)">
              查看
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

    <!-- 发布文档对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="发布文档"
      width="600px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入文档标题" />
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-select v-model="form.category" placeholder="请选择分类" style="width: 100%;">
            <el-option label="政策文件" value="policy"></el-option>
            <el-option label="通知公告" value="notice"></el-option>
            <el-option label="工作计划" value="plan"></el-option>
            <el-option label="报告" value="report"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="4"
            placeholder="请输入文档内容"
          />
        </el-form-item>
        <el-form-item label="文件URL" prop="fileUrl">
          <el-input v-model="form.fileUrl" placeholder="请输入文件URL（可选）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">发布</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getDocuments, publishDocument, approveDocument, deleteDocument } from '@/api/admin'

const loading = ref(false)
const documentsData = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const dialogVisible = ref(false)
const formRef = ref()
const form = ref({
  title: '',
  category: '',
  content: '',
  fileUrl: ''
})

const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  category: [{ required: true, message: '请选择分类', trigger: 'change' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
}

const getCategoryText = (category) => {
  const map = {
    policy: '政策文件',
    notice: '通知公告',
    plan: '工作计划',
    report: '报告'
  }
  return map[category] || category
}

const getStatusText = (status) => {
  const map = {
    0: '草稿',
    1: '待审批',
    2: '已发布',
    3: '已驳回'
  }
  return map[status] || '未知'
}

const getStatusTag = (status) => {
  const map = {
    0: 'info',
    1: 'warning',
    2: 'success',
    3: 'danger'
  }
  return map[status] || 'info'
}

const loadDocuments = async () => {
  try {
    loading.value = true
    const params = {
      current: currentPage.value,
      size: pageSize.value
    }
    const data = await getDocuments(params)
    documentsData.value = data.records
    total.value = data.total
  } catch (error) {
    ElMessage.error('加载文档列表失败')
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  form.value = {
    title: '',
    category: '',
    content: '',
    fileUrl: ''
  }
  dialogVisible.value = true
}

const handleApprove = async (row, status) => {
  const action = status === 2 ? '通过' : '驳回'
  const message = status === 2 ? '确定要通过该文档吗？' : '请输入驳回理由'

  if (status === 3) {
    ElMessageBox.prompt(message, '文档审批', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputType: 'textarea'
    }).then(async ({ value }) => {
      try {
        await approveDocument({ id: row.id, status, comment: value })
        ElMessage.success(`${action}成功`)
        loadDocuments()
      } catch (error) {
        ElMessage.error(`${action}失败`)
      }
    }).catch(() => {})
  } else {
    ElMessageBox.confirm(message, '文档审批', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(async () => {
      try {
        await approveDocument({ id: row.id, status })
        ElMessage.success(`${action}成功`)
        loadDocuments()
      } catch (error) {
        ElMessage.error(`${action}失败`)
      }
    }).catch(() => {})
  }
}

const handleView = (row) => {
  ElMessageBox.alert(row.content || '暂无内容', row.title, {
    confirmButtonText: '关闭',
    dangerouslyUseHTMLString: true
  })
}

const handleDelete = async (row) => {
  ElMessageBox.confirm(`确定要删除文档 "${row.title}" 吗？`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteDocument(row.id)
      ElMessage.success('删除成功')
      loadDocuments()
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
        await publishDocument(form.value)
        ElMessage.success('文档发布成功')
        dialogVisible.value = false
        loadDocuments()
      } catch (error) {
        ElMessage.error('文档发布失败')
      }
    }
  })
}

const handleSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1
  loadDocuments()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  loadDocuments()
}

onMounted(() => {
  loadDocuments()
})
</script>

<style scoped>
.document-management {
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
