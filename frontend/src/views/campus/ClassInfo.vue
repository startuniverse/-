<template>
  <div class="class-info">
    <!-- 未加入班级时显示申请功能 -->
    <div v-if="!classInfo && !hasApplication" class="join-section">
      <el-card>
        <template #header>
          <div class="card-header">
            <span>加入班级</span>
          </div>
        </template>
        <div class="join-content">
          <el-alert
            title="您还没有班级信息，请搜索并申请加入班级"
            type="info"
            :closable="false"
            style="margin-bottom: 20px;"
          />

          <div class="search-section">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索老师姓名或工号"
              size="large"
              style="width: 300px; margin-right: 10px;"
              @keyup.enter="handleSearchTeachers"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-button type="primary" size="large" @click="handleSearchTeachers" :icon="Search">
              搜索老师
            </el-button>
          </div>

          <div v-if="teacherResults.length > 0" class="teacher-results">
            <h3>搜索结果</h3>
            <el-table :data="teacherResults" border style="width: 100%">
              <el-table-column prop="teacherName" label="姓名" width="120" align="center" />
              <el-table-column prop="teacherNumber" label="工号" width="120" align="center" />
              <el-table-column prop="title" label="职称" width="120" align="center" />
              <el-table-column prop="subject" label="科目" width="100" align="center" />
              <el-table-column prop="phone" label="联系电话" width="140" align="center" />
              <el-table-column label="操作" width="120" align="center">
                <template #default="scope">
                  <el-button type="primary" size="small" @click="showApplyDialog(scope.row)">
                    申请加入
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>

          <div v-if="hasApplication" class="application-status">
            <h3>我的申请</h3>
            <el-table :data="myApplications" border style="width: 100%">
              <el-table-column prop="teacherName" label="班主任" width="120" align="center" />
              <el-table-column prop="className" label="班级" width="120" align="center" />
              <el-table-column prop="reason" label="申请原因" min-width="200" show-overflow-tooltip />
              <el-table-column prop="status" label="状态" width="100" align="center">
                <template #default="scope">
                  <el-tag :type="getApplicationStatusType(scope.row.status)">
                    {{ getApplicationStatusText(scope.row.status) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="created_at" label="申请时间" width="160" align="center">
                <template #default="scope">
                  {{ formatTime(scope.row.createdAt) }}
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 已加入班级显示班级信息 -->
    <div v-else>
      <el-row :gutter="20">
        <el-col :span="8" :xs="24">
          <el-card class="class-card">
            <template #header>
              <div class="card-header">
                <span>班级信息</span>
                <div>
                  <el-button type="default" size="small" @click="refreshClassInfo" style="margin-right: 10px;">
                    刷新
                  </el-button>
                  <el-button type="primary" size="small" @click="showJoinSection = true">
                    申请加入其他班级
                  </el-button>
                </div>
              </div>
            </template>
            <div v-if="classInfo" class="class-details">
              <div class="detail-item">
                <span class="label">班级名称:</span>
                <span class="value">{{ classInfo.className }}</span>
              </div>
              <div class="detail-item">
                <span class="label">年级:</span>
                <span class="value">{{ classInfo.grade }}</span>
              </div>
              <div class="detail-item">
                <span class="label">学年:</span>
                <span class="value">{{ classInfo.academicYear }}</span>
              </div>
              <div class="detail-item">
                <span class="label">班主任:</span>
                <span class="value">{{ classInfo.headTeacherName }}</span>
              </div>
              <div class="detail-item">
                <span class="label">学生人数:</span>
                <span class="value">{{ classInfo.studentCount }}</span>
              </div>
            </div>
          </el-card>
        </el-col>

        <el-col :span="16" :xs="24">
          <el-card>
            <template #header>
              <div class="card-header">
                <span>学生花名册</span>
                <el-input
                  v-model="searchName"
                  placeholder="搜索学生姓名"
                  size="small"
                  style="width: 150px;"
                  clearable
                  @input="filterStudents"
                />
              </div>
            </template>
            <el-table v-loading="loading" :data="filteredStudents" border style="width: 100%">
              <el-table-column type="index" label="序号" width="60" align="center" />
              <el-table-column prop="studentNumber" label="学号" width="120" align="center" />
              <el-table-column prop="name" label="姓名" width="120" align="center">
                <template #default="scope">
                  {{ scope.row.realName }}
                </template>
              </el-table-column>
              <el-table-column prop="guardianName" label="监护人" width="120" align="center" />
              <el-table-column prop="guardianPhone" label="监护人电话" width="140" align="center" />
              <el-table-column prop="status" label="状态" width="100" align="center">
                <template #default="scope">
                  <el-tag :type="getStatusTag(scope.row.status)">
                    {{ getStatusText(scope.row.status) }}
                  </el-tag>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-col>
      </el-row>

      <!-- 申请加入其他班级区域（当学生已有班级时显示） -->
      <div v-if="showJoinSection" style="margin-top: 20px;">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>申请加入其他班级</span>
              <div>
                <el-button type="default" size="small" @click="refreshApplications" style="margin-right: 10px;">
                  刷新申请
                </el-button>
                <el-button type="default" size="small" @click="showJoinSection = false">
                  关闭
                </el-button>
              </div>
            </div>
          </template>
          <div class="join-content">
            <div class="search-section">
              <el-input
                v-model="searchKeyword"
                placeholder="搜索老师姓名或工号"
                size="large"
                style="width: 300px; margin-right: 10px;"
                @keyup.enter="handleSearchTeachers"
              >
                <template #prefix>
                  <el-icon><Search /></el-icon>
                </template>
              </el-input>
              <el-button type="primary" size="large" @click="handleSearchTeachers" :icon="Search">
                搜索老师
              </el-button>
            </div>

            <div v-if="teacherResults.length > 0" class="teacher-results">
              <h3>搜索结果</h3>
              <el-table :data="teacherResults" border style="width: 100%">
                <el-table-column prop="teacherName" label="姓名" width="120" align="center" />
                <el-table-column prop="teacherNumber" label="工号" width="120" align="center" />
                <el-table-column prop="title" label="职称" width="120" align="center" />
                <el-table-column prop="subject" label="科目" width="100" align="center" />
                <el-table-column prop="phone" label="联系电话" width="140" align="center" />
                <el-table-column label="操作" width="120" align="center">
                  <template #default="scope">
                    <el-button type="primary" size="small" @click="showApplyDialog(scope.row)">
                      申请加入
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </div>

            <div v-if="hasApplication" class="application-status" style="margin-top: 20px;">
              <h3>我的申请</h3>
              <el-table :data="myApplications" border style="width: 100%">
                <el-table-column prop="teacherName" label="班主任" width="120" align="center" />
                <el-table-column prop="className" label="班级" width="120" align="center" />
                <el-table-column prop="reason" label="申请原因" min-width="200" show-overflow-tooltip />
                <el-table-column prop="status" label="状态" width="100" align="center">
                  <template #default="scope">
                    <el-tag :type="getApplicationStatusType(scope.row.status)">
                      {{ getApplicationStatusText(scope.row.status) }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="created_at" label="申请时间" width="160" align="center">
                  <template #default="scope">
                    {{ formatTime(scope.row.createdAt) }}
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </div>
        </el-card>
      </div>
    </div>

    <!-- 申请加入班级对话框 -->
    <el-dialog
      v-model="applyDialogVisible"
      title="申请加入班级"
      width="500px"
    >
      <el-form :model="applyForm" label-width="100px">
        <el-form-item label="班主任">
          <el-input v-model="applyForm.teacherName" disabled />
        </el-form-item>
        <el-form-item label="班级" required>
          <el-select v-model="applyForm.classId" placeholder="请选择班级" style="width: 100%">
            <el-option
              v-for="classItem in classOptions"
              :key="classItem.id"
              :label="classItem.className"
              :value="classItem.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="申请原因" required>
          <el-input
            v-model="applyForm.reason"
            type="textarea"
            :rows="4"
            placeholder="请输入申请加入班级的原因"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="applyDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitApplication" :disabled="!isFormValid">
            提交申请
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { getClassInfo } from '@/api/campus'
import { searchTeachers, createClassApplication, getStudentApplications, getTeacherClasses } from '@/api/classApplication'
import { useUserStore } from '@/store/modules/user'

const userStore = useUserStore()
const loading = ref(false)
const classInfo = ref(null)
const students = ref([])
const searchName = ref('')
const showJoinSection = ref(false)

// 搜索老师相关
const searchKeyword = ref('')
const teacherResults = ref([])

// 申请相关
const hasApplication = ref(false)
const myApplications = ref([])
const applyDialogVisible = ref(false)
const applyForm = ref({
  teacherId: null,
  teacherName: '',
  classId: null,
  reason: ''
})
const classOptions = ref([])

const filteredStudents = computed(() => {
  if (!searchName.value) return students.value
  return students.value.filter(s =>
    s.realName && s.realName.includes(searchName.value)
  )
})

const isFormValid = computed(() => {
  return applyForm.value.classId && applyForm.value.reason && applyForm.value.reason.trim().length > 0
})

const getStatusText = (status) => {
  const map = {
    1: '在读',
    2: '毕业',
    3: '转学',
    4: '休学'
  }
  return map[status] || '未知'
}

const getStatusTag = (status) => {
  const map = {
    1: 'success',
    2: 'info',
    3: 'warning',
    4: 'danger'
  }
  return map[status] || 'info'
}

const getApplicationStatusType = (status) => {
  const map = {
    0: 'info',    // 待审核
    1: 'success', // 已通过
    2: 'danger'   // 已驳回
  }
  return map[status] || 'info'
}

const getApplicationStatusText = (status) => {
  const map = {
    0: '待审核',
    1: '已通过',
    2: '已驳回'
  }
  return map[status] || '未知'
}

const formatTime = (time) => {
  if (!time) return '-'
  return new Date(time).toLocaleString('zh-CN')
}

// 搜索老师
const handleSearchTeachers = async () => {
  if (!searchKeyword.value.trim()) {
    ElMessage.warning('请输入搜索关键词')
    return
  }

  try {
    const res = await searchTeachers({ keyword: searchKeyword.value })
    teacherResults.value = res
    if (res.length === 0) {
      ElMessage.info('未找到匹配的老师')
    }
  } catch (error) {
    ElMessage.error('搜索失败')
  }
}

// 显示申请对话框
const showApplyDialog = async (teacher) => {
  applyForm.value = {
    teacherId: teacher.teacherId,
    teacherName: teacher.teacherName,
    classId: null,
    reason: ''
  }

  // 动态获取该老师管理的班级列表
  try {
    const res = await getTeacherClasses(teacher.teacherId)
    classOptions.value = res
    if (res.length === 0) {
      ElMessage.warning('该老师暂无管理的班级')
    }
  } catch (error) {
    ElMessage.error('获取班级列表失败')
    classOptions.value = []
  }

  applyDialogVisible.value = true
}

// 提交申请
const submitApplication = async () => {
  try {
    const formData = {
      studentId: userStore.userInfo.id,
      studentName: userStore.userInfo.realName,
      classId: applyForm.value.classId,
      className: classOptions.value.find(c => c.id === applyForm.value.classId)?.className,
      teacherId: applyForm.value.teacherId,
      teacherName: applyForm.value.teacherName,
      reason: applyForm.value.reason
    }

    await createClassApplication(formData)
    ElMessage.success('申请提交成功')
    applyDialogVisible.value = false
    showJoinSection.value = true
    await loadMyApplications()
  } catch (error) {
    ElMessage.error('申请提交失败')
  }
}

// 加载我的申请
const loadMyApplications = async () => {
  try {
    const res = await getStudentApplications(userStore.userInfo.id)
    myApplications.value = res
    hasApplication.value = res.length > 0
  } catch (error) {
    console.error('加载申请失败:', error)
  }
}

// 加载班级信息
const loadClassInfo = async () => {
  try {
    loading.value = true
    const data = await getClassInfo()
    // 将studentCount合并到classInfo中，以便前端显示
    if (data.classInfo) {
      classInfo.value = {
        ...data.classInfo,
        studentCount: data.studentCount || 0
      }
    } else {
      classInfo.value = null
    }
    students.value = data.students || []
  } catch (error) {
    // 如果没有班级信息，显示申请界面
    if (error.response && error.response.status === 404) {
      classInfo.value = null
    } else {
      ElMessage.error('加载班级信息失败')
    }
  } finally {
    loading.value = false
  }
}

// 刷新班级信息
const refreshClassInfo = async () => {
  await loadClassInfo()
  ElMessage.success('已刷新')
}

// 刷新申请列表
const refreshApplications = async () => {
  await loadMyApplications()
  ElMessage.success('申请列表已刷新')
}

const filterStudents = () => {
  // 计算属性会自动处理
}

onMounted(async () => {
  await loadClassInfo()
  await loadMyApplications()
})
</script>

<style scoped>
.class-info {
  width: 100%;
}

.join-section {
  margin-bottom: 20px;
}

.search-section {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.teacher-results {
  margin-top: 20px;
}

.teacher-results h3,
.application-status h3 {
  margin-bottom: 10px;
  color: #303133;
}

.application-status {
  margin-top: 20px;
}

.class-card {
  margin-bottom: 20px;
}

.card-header {
  font-weight: 600;
}

.class-details {
  padding: 10px 0;
}

.detail-item {
  display: flex;
  justify-content: space-between;
  padding: 12px 0;
  border-bottom: 1px solid #f5f5f5;
}

.detail-item:last-child {
  border-bottom: none;
}

.label {
  color: #909399;
  font-size: 14px;
}

.value {
  color: #303133;
  font-size: 14px;
  font-weight: 500;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>
