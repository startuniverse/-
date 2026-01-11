<template>
  <div class="class-management">
    <!-- 我的班级 -->
    <el-card style="margin-bottom: 20px;">
      <template #header>
        <div class="card-header">
          <span>📋 我的班级</span>
          <el-button type="primary" @click="loadMyClasses" :icon="Refresh">刷新</el-button>
        </div>
      </template>

      <div v-if="myClasses.length > 0" class="my-classes-list">
        <div v-for="cls in myClasses" :key="cls.id" class="class-item">
          <div class="class-info">
            <div class="class-name">{{ cls.className }}</div>
            <div class="class-details">
              <el-tag size="small" type="primary">{{ cls.grade }}</el-tag>
              <span class="detail-item">学年: {{ cls.academicYear }}</span>
              <span class="detail-item">学生数: {{ cls.actualStudentCount || 0 }}</span>
            </div>
          </div>
          <div class="class-actions">
            <el-button type="danger" size="small" @click="handleUnselectClass(cls)">
              取消选择
            </el-button>
          </div>
        </div>
      </div>

      <div v-else class="empty-state">
        <el-empty description="您还没有选择负责的班级">
          <el-button type="primary" @click="showClassSelector = true">
            选择班级
          </el-button>
        </el-empty>
      </div>
    </el-card>

    <!-- 可选班级列表 -->
    <el-card>
      <template #header>
        <div class="card-header">
          <span>🏫 可选班级</span>
          <div>
            <el-input
              v-model="searchKeyword"
              placeholder="搜索班级名称"
              size="small"
              style="width: 150px; margin-right: 10px;"
              clearable
              @input="filterClasses"
            />
            <el-button type="primary" @click="loadAvailableClasses" :icon="Refresh">刷新</el-button>
          </div>
        </div>
      </template>

      <el-table :data="filteredClasses" style="width: 100%" v-loading="loading">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="className" label="班级名称" width="140" align="center" />
        <el-table-column prop="grade" label="年级" width="100" align="center" />
        <el-table-column prop="academicYear" label="学年" width="120" align="center" />
        <el-table-column prop="studentCount" label="学生数" width="100" align="center" />
        <el-table-column prop="headTeacherName" label="班主任" width="120" align="center">
          <template #default="scope">
            <el-tag v-if="scope.row.isSelected" type="success" size="small">我负责</el-tag>
            <span v-else-if="scope.row.headTeacherName">{{ scope.row.headTeacherName }}</span>
            <span v-else style="color: #909399;">未分配</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="140" fixed="right" align="center">
          <template #default="scope">
            <el-button
              v-if="!scope.row.isSelected"
              type="success"
              size="small"
              @click="handleSelectClass(scope.row)"
            >
              选择
            </el-button>
            <el-button
              v-else
              type="danger"
              size="small"
              @click="handleUnselectClass(scope.row)"
            >
              取消
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div v-if="filteredClasses.length === 0 && !loading" class="empty-state">
        <el-empty description="暂无可选班级" />
      </div>
    </el-card>

    <!-- 选择班级提示对话框 -->
    <el-dialog
      v-model="showWelcomeDialog"
      title="👋 欢迎加入教学平台"
      width="500px"
      :close-on-click-modal="false"
      :show-close="false"
    >
      <div class="welcome-content">
        <p>欢迎注册成为我们的教师！</p>
        <p>为了开始使用系统，您需要先选择您负责的班级。</p>
        <p>选择班级后，您将可以：</p>
        <ul>
          <li>管理班级学生</li>
          <li>发布作业和通知</li>
          <li>记录和管理成绩</li>
          <li>查看课程表</li>
        </ul>
        <p style="margin-top: 16px; color: #67c23a;">点击下方按钮开始选择班级！</p>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button type="primary" @click="goToClassSelection">
            去选择班级
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import { getAvailableClasses, selectClass, unselectClass, getMyClasses } from '@/api/teacher'
import { useUserStore } from '@/store/modules/user'
import { useRouter } from 'vue-router'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const myClasses = ref([])
const availableClasses = ref([])
const searchKeyword = ref('')
const showWelcomeDialog = ref(false)

// 计算属性：过滤后的可选班级
const filteredClasses = computed(() => {
  if (!searchKeyword.value) return availableClasses.value
  return availableClasses.value.filter(cls =>
    cls.className && cls.className.includes(searchKeyword.value)
  )
})

// 加载我的班级
const loadMyClasses = async () => {
  try {
    const data = await getMyClasses()
    myClasses.value = data

    // 如果没有班级，检查是否需要显示欢迎对话框
    if (data.length === 0) {
      const hasSeenWelcome = localStorage.getItem('hasSeenClassWelcome')
      if (!hasSeenWelcome) {
        showWelcomeDialog.value = true
      }
    }
  } catch (error) {
    ElMessage.error('加载我的班级失败')
  }
}

// 加载可选班级
const loadAvailableClasses = async () => {
  loading.value = true
  try {
    const data = await getAvailableClasses()
    availableClasses.value = data
  } catch (error) {
    ElMessage.error('加载可选班级失败')
  } finally {
    loading.value = false
  }
}

// 选择班级
const handleSelectClass = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要选择 "${row.className}" 作为您的负责班级吗？`,
      '确认选择',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info'
      }
    )

    await selectClass(row.id)
    ElMessage.success('班级选择成功！')

    // 刷新数据
    await loadMyClasses()
    await loadAvailableClasses()

    // 隐藏欢迎对话框
    showWelcomeDialog.value = false
    localStorage.setItem('hasSeenClassWelcome', 'true')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('选择班级失败')
    }
  }
}

// 取消选择班级
const handleUnselectClass = async (row) => {
  try {
    // 检查班级是否有学生
    if (row.actualStudentCount > 0) {
      ElMessage.warning('该班级还有学生，无法取消选择')
      return
    }

    await ElMessageBox.confirm(
      `确定要取消选择 "${row.className}" 吗？`,
      '确认取消',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await unselectClass(row.id)
    ElMessage.success('已取消选择')

    // 刷新数据
    await loadMyClasses()
    await loadAvailableClasses()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('取消选择失败')
    }
  }
}

// 搜索过滤
const filterClasses = () => {
  // 计算属性会自动处理
}

// 去选择班级（从欢迎对话框）
const goToClassSelection = () => {
  showWelcomeDialog.value = false
  localStorage.setItem('hasSeenClassWelcome', 'true')
  // 滚动到可选班级区域
  window.scrollTo({ top: 300, behavior: 'smooth' })
}

onMounted(async () => {
  await Promise.all([
    loadMyClasses(),
    loadAvailableClasses()
  ])
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.empty-state {
  margin-top: 20px;
  text-align: center;
}

.my-classes-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.class-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 8px;
  border: 1px solid #e4e7ed;
}

.class-info {
  flex: 1;
}

.class-name {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
}

.class-details {
  display: flex;
  gap: 12px;
  align-items: center;
  font-size: 13px;
  color: #606266;
}

.detail-item {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.class-actions {
  display: flex;
  gap: 8px;
}

.welcome-content p {
  margin: 8px 0;
  line-height: 1.6;
}

.welcome-content ul {
  margin: 8px 0;
  padding-left: 20px;
}

.welcome-content ul li {
  margin: 4px 0;
  line-height: 1.5;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>
