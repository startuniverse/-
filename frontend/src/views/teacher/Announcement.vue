<template>
  <div class="announcement">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>📢 发布通知</span>
          <el-button type="primary" icon="List" @click="showHistory">
            通知记录
          </el-button>
        </div>
      </template>

      <el-form
        ref="formRef"
        :model="announcementForm"
        :rules="rules"
        label-width="120px"
        class="announcement-form"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="通知标题" prop="title">
              <el-input
                v-model="announcementForm.title"
                placeholder="请输入通知标题（如：家长会通知）"
                clearable
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="通知类型" prop="type">
              <el-select v-model="announcementForm.type" placeholder="请选择类型" style="width: 100%;">
                <el-option label="重要通知" value="important" />
                <el-option label="日常通知" value="daily" />
                <el-option label="活动通知" value="activity" />
                <el-option label="考试安排" value="exam" />
                <el-option label="放假安排" value="holiday" />
                <el-option label="其他" value="other" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="接收对象" prop="recipients">
              <el-select
                v-model="announcementForm.recipients"
                multiple
                placeholder="请选择接收对象"
                style="width: 100%;"
              >
                <el-option label="全体学生" value="students" />
                <el-option label="全体家长" value="parents" />
                <el-option label="高三1班" value="class1" />
                <el-option label="高三2班" value="class2" />
                <el-option label="高三3班" value="class3" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="发布时间" prop="publishTime">
              <el-radio-group v-model="announcementForm.publishTime">
                <el-radio-button label="immediate">立即发布</el-radio-button>
                <el-radio-button label="schedule">定时发布</el-radio-button>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row v-if="announcementForm.publishTime === 'schedule'" :gutter="20">
          <el-col :span="24">
            <el-form-item label="定时时间" prop="scheduleTime">
              <el-date-picker
                v-model="announcementForm.scheduleTime"
                type="datetime"
                placeholder="选择定时发布时间"
                format="YYYY-MM-DD HH:mm"
                value-format="YYYY-MM-DD HH:mm:ss"
                style="width: 100%;"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="置顶设置" prop="isTop">
              <el-switch
                v-model="announcementForm.isTop"
                active-text="置顶显示"
                inactive-text="普通显示"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="短信通知" prop="sendSMS">
              <el-switch
                v-model="announcementForm.sendSMS"
                active-text="发送短信"
                inactive-text="不发送"
              />
              <span class="form-tip">（需额外费用）</span>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="通知摘要" prop="summary">
          <el-input
            v-model="announcementForm.summary"
            placeholder="简要描述通知内容（选填）"
            clearable
          />
        </el-form-item>

        <el-form-item label="详细内容" prop="content">
          <el-input
            v-model="announcementForm.content"
            type="textarea"
            :rows="8"
            placeholder="请输入详细通知内容...
例如：
各位家长好：
     本周五下午3点将召开家长会，请各位家长准时参加。
     地点：学校大礼堂
     注意事项：请携带学生手册"
          />
        </el-form-item>

        <el-form-item label="附件上传">
          <el-upload
            class="upload-demo"
            action="#"
            :auto-upload="false"
            :on-change="handleFileChange"
            :file-list="fileList"
            :limit="3"
            :on-exceed="handleExceed"
          >
            <el-button type="primary" icon="Upload">选择文件</el-button>
            <template #tip>
              <div class="el-upload__tip">
                可上传通知附件（如：活动安排表、报名表等），最多3个文件
              </div>
            </template>
          </el-upload>
        </el-form-item>

        <el-form-item label="是否允许评论" prop="allowComments">
          <el-switch
            v-model="announcementForm.allowComments"
            active-text="允许评论"
            inactive-text="禁止评论"
          />
        </el-form-item>

        <el-form-item label="需要回执" prop="needReceipt">
          <el-switch
            v-model="announcementForm.needReceipt"
            active-text="需要确认"
            inactive-text="不需要"
          />
          <span class="form-tip">（学生/家长需确认收到）</span>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="submitAnnouncement" :loading="submitting">
            立即发布
          </el-button>
          <el-button @click="resetForm">重置表单</el-button>
          <el-button type="info" @click="saveDraft">保存草稿</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 通知记录对话框 -->
    <el-dialog v-model="historyVisible" title="通知发布记录" width="900px" destroy-on-close>
      <el-table :data="historyAnnouncements" style="width: 100%" v-loading="historyLoading">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="title" label="通知标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="type" label="类型" width="120">
          <template #default="scope">
            <el-tag :type="getTypeColor(scope.row.type)">{{ getTypeName(scope.row.type) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="publishTime" label="发布时间" width="160" align="center">
          <template #default="scope">
            {{ scope.row.publishTime ? new Date(scope.row.publishTime).toLocaleString('zh-CN') : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="priority" label="优先级" width="100" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.priority === 1 ? 'danger' : 'info'">
              {{ scope.row.priority === 1 ? '置顶' : '普通' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="scope">
            <el-button link type="primary" @click="viewAnnouncement(scope.row)">查看</el-button>
            <el-button link type="danger" @click="deleteAnnouncement(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination" style="margin-top: 15px;">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[5, 10, 20]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="historyVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { publishAnnouncement, getTeacherAnnouncements, deleteTeacherAnnouncement } from '@/api/teacher'

const formRef = ref()
const submitting = ref(false)
const historyVisible = ref(false)
const historyLoading = ref(false)

const announcementForm = reactive({
  title: '',
  type: '',
  recipients: [],
  publishTime: 'immediate',
  scheduleTime: '',
  isTop: false,
  sendSMS: false,
  summary: '',
  content: '',
  allowComments: true,
  needReceipt: false
})

const fileList = ref([])
const historyAnnouncements = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const rules = {
  title: [{ required: true, message: '请输入通知标题', trigger: 'blur' }],
  type: [{ required: true, message: '请选择通知类型', trigger: 'change' }],
  recipients: [{ required: true, message: '请选择接收对象', trigger: 'change' }],
  content: [{ required: true, message: '请输入详细内容', trigger: 'blur' }],
  scheduleTime: [
    {
      validator: (rule, value, callback) => {
        if (announcementForm.publishTime === 'schedule' && !value) {
          callback(new Error('请选择定时发布时间'))
        } else {
          callback()
        }
      },
      trigger: 'change'
    }
  ]
}

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
  ElMessage.warning('最多只能上传3个文件')
}

// 获取类型名称
const getTypeName = (type) => {
  const map = {
    important: '重要通知',
    daily: '日常通知',
    activity: '活动通知',
    exam: '考试安排',
    holiday: '放假安排',
    other: '其他'
  }
  return map[type] || type
}

// 获取类型颜色
const getTypeColor = (type) => {
  const map = {
    important: 'danger',
    daily: 'info',
    activity: 'success',
    exam: 'warning',
    holiday: 'primary',
    other: ''
  }
  return map[type] || ''
}

// 获取接收对象名称
const getRecipientName = (recipient) => {
  const map = {
    students: '学生',
    parents: '家长',
    class1: '高三1班',
    class2: '高三2班',
    class3: '高三3班'
  }
  return map[recipient] || recipient
}

// 获取状态样式
const getStatusType = (status) => {
  if (status === '已发布') return 'success'
  if (status === '待发布') return 'warning'
  if (status === '已撤回') return 'info'
  return 'danger'
}

// 提交通知
const submitAnnouncement = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        // 准备提交数据
        const submitData = {
          title: announcementForm.title,
          type: announcementForm.type,
          recipients: announcementForm.recipients,
          publishTime: announcementForm.publishTime,
          scheduleTime: announcementForm.scheduleTime,
          isTop: announcementForm.isTop,
          sendSMS: announcementForm.sendSMS,
          content: announcementForm.content,
          allowComments: announcementForm.allowComments,
          needReceipt: announcementForm.needReceipt
        }

        // 调用API
        const result = await publishAnnouncement(submitData)

        const message = announcementForm.publishTime === 'immediate'
          ? '通知发布成功！'
          : '通知已设置定时发布！'

        ElMessage.success(message)
        resetForm()

        // 重新加载历史记录
        loadHistory()
      } catch (error) {
        ElMessage.error('发布失败: ' + (error.message || '请重试'))
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
  announcementForm.publishTime = 'immediate'
}

// 保存草稿
const saveDraft = () => {
  ElMessage.success('草稿已保存')
}

// 加载历史记录
const loadHistory = async () => {
  historyLoading.value = true
  try {
    const params = {
      current: currentPage.value,
      size: pageSize.value
    }
    const result = await getTeacherAnnouncements(params)
    historyAnnouncements.value = result.records
    total.value = result.total
  } catch (error) {
    ElMessage.error('加载历史记录失败')
  } finally {
    historyLoading.value = false
  }
}

// 显示历史记录
const showHistory = () => {
  historyVisible.value = true
  loadHistory()
}

// 查看通知详情
const viewAnnouncement = (row) => {
  // 将后端类型转换为前端显示
  const typeText = getTypeName(row.type)
  const publishTimeText = row.publishTime ? new Date(row.publishTime).toLocaleString('zh-CN') : '未知'

  ElMessageBox.alert(
    `<div style="line-height: 1.8;">
      <strong>标题：</strong>${row.title}<br/>
      <strong>类型：</strong>${typeText}<br/>
      <strong>发布时间：</strong>${publishTimeText}<br/>
      <strong>优先级：</strong>${row.priority === 1 ? '置顶' : '普通'}<br/>
      <strong>内容：</strong><br/>
      <div style="margin-top: 8px; padding: 8px; background: #f5f5f5; border-radius: 4px;">${row.content}</div>
    </div>`,
    '通知详情',
    {
      dangerouslyUseHTMLString: true,
      confirmButtonText: '关闭'
    }
  )
}

// 删除通知
const deleteAnnouncement = (row) => {
  ElMessageBox.confirm(`确定要删除"${row.title}"吗？`, '警告', {
    type: 'warning'
  }).then(async () => {
    try {
      await deleteTeacherAnnouncement(row.id)
      ElMessage.success('删除成功')
      loadHistory()
    } catch (error) {
      ElMessage.error('删除失败: ' + (error.message || '请重试'))
    }
  }).catch(() => {})
}

// 分页处理
const handleSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1
  loadHistory()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  loadHistory()
}

onMounted(() => {
  // 可以在这里加载默认数据
})
</script>

<style scoped>
.announcement {
  width: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
}

.announcement-form {
  margin-top: 10px;
}

.form-tip {
  color: #909399;
  font-size: 12px;
  margin-left: 8px;
}

.upload-demo {
  width: 100%;
}
</style>
