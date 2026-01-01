<template>
  <div class="assignment">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>📝 作业布置</span>
          <el-button type="primary" icon="Plus" @click="showHistory">
            查看历史作业
          </el-button>
        </div>
      </template>

      <el-form
        ref="formRef"
        :model="assignmentForm"
        :rules="rules"
        label-width="120px"
        class="assignment-form"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="作业标题" prop="title">
              <el-input
                v-model="assignmentForm.title"
                placeholder="请输入作业标题（如：第三章课后练习）"
                clearable
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属科目" prop="subject">
              <el-select v-model="assignmentForm.subject" placeholder="请选择科目" style="width: 100%;">
                <el-option label="语文" value="chinese" />
                <el-option label="数学" value="math" />
                <el-option label="英语" value="english" />
                <el-option label="物理" value="physics" />
                <el-option label="化学" value="chemistry" />
                <el-option label="生物" value="biology" />
                <el-option label="历史" value="history" />
                <el-option label="地理" value="geography" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="发布班级" prop="targetClasses">
              <el-select
                v-model="assignmentForm.targetClasses"
                multiple
                placeholder="请选择班级"
                style="width: 100%;"
              >
                <el-option label="高三1班" value="class1" />
                <el-option label="高三2班" value="class2" />
                <el-option label="高三3班" value="class3" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="截止时间" prop="deadline">
              <el-date-picker
                v-model="assignmentForm.deadline"
                type="datetime"
                placeholder="选择截止时间"
                format="YYYY-MM-DD HH:mm"
                value-format="YYYY-MM-DD HH:mm:ss"
                style="width: 100%;"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="难度等级" prop="difficulty">
              <el-radio-group v-model="assignmentForm.difficulty">
                <el-radio-button label="easy">简单</el-radio-button>
                <el-radio-button label="medium">中等</el-radio-button>
                <el-radio-button label="hard">困难</el-radio-button>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="是否计分" prop="isGraded">
              <el-switch
                v-model="assignmentForm.isGraded"
                active-text="计分"
                inactive-text="不计分"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="作业内容" prop="content">
          <el-input
            v-model="assignmentForm.content"
            type="textarea"
            :rows="6"
            placeholder="请输入作业详细内容和要求...
例如：
1. 完成课本第45页练习题1-10题
2. 预习下一章节内容
3. 提交格式：PDF或图片"
          />
        </el-form-item>

        <el-form-item label="附件上传">
          <el-upload
            class="upload-demo"
            action="#"
            :auto-upload="false"
            :on-change="handleFileChange"
            :file-list="fileList"
            :limit="5"
            :on-exceed="handleExceed"
          >
            <el-button type="primary" icon="Upload">选择文件</el-button>
            <template #tip>
              <div class="el-upload__tip">
                支持上传PDF、Word、图片等文件，最多5个，单个文件不超过10MB
              </div>
            </template>
          </el-upload>
        </el-form-item>

        <el-form-item label="补充说明" prop="notes">
          <el-input
            v-model="assignmentForm.notes"
            type="textarea"
            :rows="3"
            placeholder="可选：补充说明、提示信息等"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="submitAssignment" :loading="submitting">
            立即发布
          </el-button>
          <el-button @click="resetForm">重置表单</el-button>
          <el-button type="info" @click="saveDraft">保存草稿</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 历史作业对话框 -->
    <el-dialog
      v-if="historyVisible"
      v-model="historyVisible"
      title="历史作业记录"
      width="800px"
    >
      <el-table :data="historyAssignments" style="width: 100%" v-loading="historyLoading">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="title" label="作业标题" min-width="150" />
        <el-table-column prop="subject" label="科目" width="100">
          <template #default="scope">
            <el-tag type="primary">{{ getSubjectName(scope.row.subject) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="targetClasses" label="发布班级" width="120">
          <template #default="scope">
            <span>{{ formatTargetClasses(scope.row.targetClasses) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="deadline" label="截止时间" width="160" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">{{ scope.row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="scope">
            <el-button link type="primary" @click="viewAssignment(scope.row)">查看</el-button>
            <el-button link type="danger" @click="deleteAssignment(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="historyVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const formRef = ref()
const submitting = ref(false)
const historyVisible = ref(false)
const historyLoading = ref(false)

const assignmentForm = reactive({
  title: '',
  subject: '',
  targetClasses: [],
  deadline: '',
  difficulty: 'medium',
  isGraded: true,
  content: '',
  notes: ''
})

const fileList = ref([])

const rules = {
  title: [{ required: true, message: '请输入作业标题', trigger: 'blur' }],
  subject: [{ required: true, message: '请选择科目', trigger: 'change' }],
  targetClasses: [{ required: true, message: '请选择发布班级', trigger: 'change' }],
  deadline: [{ required: true, message: '请选择截止时间', trigger: 'change' }],
  content: [{ required: true, message: '请输入作业内容', trigger: 'blur' }]
}

// 历史作业数据
const historyAssignments = ref([])

// 处理文件上传
const handleFileChange = (file, fileListParam) => {
  // 检查文件大小（10MB）
  if (file.size > 10 * 1024 * 1024) {
    ElMessage.warning('文件大小不能超过10MB')
    fileListParam.pop()
    return
  }
  fileList.value = fileListParam
}

// 文件数量超限
const handleExceed = () => {
  ElMessage.warning('最多只能上传5个文件')
}

// 获取科目名称
const getSubjectName = (subject) => {
  const map = {
    chinese: '语文',
    math: '数学',
    english: '英语',
    physics: '物理',
    chemistry: '化学',
    biology: '生物',
    history: '历史',
    geography: '地理'
  }
  return map[subject] || subject
}

// 获取状态样式
const getStatusType = (status) => {
  if (status === '进行中') return 'warning'
  if (status === '已完成') return 'success'
  if (status === '已截止') return 'info'
  return 'danger'
}

// 格式化发布班级
const formatTargetClasses = (classes) => {
  if (!classes) return ''
  // 如果是数组，直接join
  if (Array.isArray(classes)) {
    return classes.join('、')
  }
  // 如果是字符串，尝试解析
  if (typeof classes === 'string') {
    try {
      // 尝试解析JSON格式的数组字符串
      const parsed = JSON.parse(classes)
      if (Array.isArray(parsed)) {
        return parsed.join('、')
      }
      return classes
    } catch (e) {
      // 解析失败，直接返回原字符串
      return classes
    }
  }
  return String(classes)
}

// 提交作业
const submitAssignment = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        // 调用后端API发布作业
        // 拦截器成功时返回data，失败时抛异常
        await request({
          url: '/teacher/assignments',
          method: 'post',
          data: {
            title: assignmentForm.title,
            subject: assignmentForm.subject,
            content: assignmentForm.content,
            targetClasses: assignmentForm.targetClasses,
            deadline: assignmentForm.deadline,
            difficulty: assignmentForm.difficulty,
            isGraded: assignmentForm.isGraded,
            notes: assignmentForm.notes
          }
        })

        ElMessage.success('作业发布成功！')
        resetForm()
        // 刷新历史作业列表
        await loadHistoryAssignments()
      } catch (error) {
        console.error('发布失败:', error)
        ElMessage.error('发布失败，请重试')
      } finally {
        submitting.value = false
      }
    }
  })
}

// 重置表单
const resetForm = () => {
  formRef.value?.resetFields()
  fileList.value = []
}

// 保存草稿
const saveDraft = () => {
  ElMessage.success('草稿已保存')
}

// 加载历史作业
const loadHistoryAssignments = async () => {
  historyLoading.value = true
  try {
    // 拦截器返回data部分，包含records数组
    const data = await request({
      url: '/teacher/assignments',
      method: 'get'
    })

    historyAssignments.value = data.records || []
  } catch (error) {
    console.error('加载失败:', error)
    ElMessage.error('加载历史作业失败')
  } finally {
    historyLoading.value = false
  }
}

// 显示历史作业
const showHistory = () => {
  historyVisible.value = true
  // 使用 nextTick 确保 DOM 更新后再加载数据
  nextTick(() => {
    loadHistoryAssignments()
  })
}

// 查看作业详情
const viewAssignment = (row) => {
  ElMessageBox.alert(
    `<div style="line-height: 1.8;">
      <strong>标题：</strong>${row.title}<br/>
      <strong>科目：</strong>${getSubjectName(row.subject)}<br/>
      <strong>班级：</strong>${formatTargetClasses(row.targetClasses)}<br/>
      <strong>截止：</strong>${row.deadline}<br/>
      <strong>状态：</strong>${row.status}
    </div>`,
    '作业详情',
    {
      dangerouslyUseHTMLString: true,
      confirmButtonText: '关闭'
    }
  )
}

// 删除作业
const deleteAssignment = (row) => {
  ElMessageBox.confirm(`确定要删除"${row.title}"吗？`, '警告', {
    type: 'warning'
  }).then(async () => {
    try {
      // 拦截器成功时返回true，失败时抛异常
      await request({
        url: `/teacher/assignments/${row.id}`,
        method: 'delete'
      })

      ElMessage.success('删除成功')
      await loadHistoryAssignments()
    } catch (error) {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

onMounted(() => {
  // 可以在这里加载默认数据
})
</script>

<style scoped>
.assignment {
  width: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
}

.assignment-form {
  margin-top: 10px;
}

.upload-demo {
  width: 100%;
}
</style>
