<template>
  <div class="my-training">
    <el-tabs v-model="activeTab" @tab-change="handleTabChange">
      <!-- 我的课程 -->
      <el-tab-pane label="我的课程" name="courses">
        <div v-loading="loading" class="course-list">
          <el-row :gutter="20" v-if="myCourses.length > 0">
            <el-col :span="8" v-for="item in myCourses" :key="item.enrollment.id">
              <el-card class="course-card">
                <div class="course-header">
                  <h3>{{ item.course.title }}</h3>
                  <el-tag :type="getStatusType(item.enrollment.status)" size="small">
                    {{ getStatusText(item.enrollment.status) }}
                  </el-tag>
                </div>

                <div class="progress-section">
                  <el-progress
                    :percentage="item.enrollment.progress"
                    :status="item.enrollment.progress === 100 ? 'success' : ''"
                  />
                  <div class="progress-text">
                    已完成 {{ item.enrollment.progress }}%
                  </div>
                </div>

                <div class="course-meta">
                  <div>
                    <el-icon><User /></el-icon>
                    {{ item.course.instructorName }}
                  </div>
                  <div>
                    <el-icon><Timer /></el-icon>
                    {{ item.course.duration }}分钟
                  </div>
                </div>

                <div class="course-actions">
                  <el-button
                    type="primary"
                    size="small"
                    @click="continueLearning(item.course)"
                    :disabled="item.enrollment.status === 'completed'"
                  >
                    继续学习
                  </el-button>
                  <el-button
                    size="small"
                    @click="showExam(item.course)"
                    v-if="item.enrollment.status === 'learning' || item.enrollment.status === 'completed'"
                  >
                    考试
                  </el-button>
                  <el-button
                    type="success"
                    size="small"
                    @click="viewCertificate(item.enrollment)"
                    v-if="item.enrollment.certificateUrl"
                  >
                    查看证书
                  </el-button>
                </div>

                <div class="score-info" v-if="item.enrollment.score">
                  <el-tag type="success" effect="dark">得分: {{ item.enrollment.score }}</el-tag>
                </div>

                <div class="enroll-time">
                  报名时间: {{ formatTime(item.enrollment.enrollmentTime) }}
                </div>
              </el-card>
            </el-col>
          </el-row>

          <div v-if="myCourses.length === 0 && !loading" class="empty-state">
            <el-empty description="您还没有报名任何课程">
              <el-button type="primary" @click="$router.push('/campus/training')">
                去选课
              </el-button>
            </el-empty>
          </div>
        </div>
      </el-tab-pane>

      <!-- 考试记录 -->
      <el-tab-pane label="考试记录" name="exams">
        <el-table :data="examRecords" style="width: 100%" v-loading="loading" border>
          <el-table-column prop="courseName" label="课程名称" min-width="180" show-overflow-tooltip />
          <el-table-column prop="examTitle" label="考试标题" min-width="180" show-overflow-tooltip />
          <el-table-column prop="score" label="得分" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="row.isPassed ? 'success' : 'danger'" effect="dark" size="small">
                {{ row.score }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="isPassed" label="是否通过" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="row.isPassed ? 'success' : 'danger'" size="small">
                {{ row.isPassed ? '通过' : '未通过' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="attemptNumber" label="尝试次数" width="100" align="center" />
          <el-table-column prop="endTime" label="完成时间" width="180" align="center">
            <template #default="{ row }">
              {{ formatTime(row.endTime) }}
            </template>
          </el-table-column>
        </el-table>

        <div v-if="examRecords.length === 0 && !loading" class="empty-table">
          <el-empty description="暂无考试记录"></el-empty>
        </div>
      </el-tab-pane>

      <!-- 我的证书 -->
      <el-tab-pane label="我的证书" name="certificates">
        <div v-loading="loading" class="certificate-list">
          <el-row :gutter="20" v-if="completedCourses.length > 0">
            <el-col :span="8" v-for="item in completedCourses" :key="item.enrollment.id">
              <el-card class="certificate-card">
                <div class="certificate-header">
                  <el-icon><Trophy /></el-icon>
                  <h3>结业证书</h3>
                </div>
                <div class="certificate-content">
                  <p class="course-name">{{ item.course.title }}</p>
                  <p class="student-name">{{ item.enrollment.studentName }}</p>
                  <p class="score">最终得分: {{ item.enrollment.score }}</p>
                  <p class="date">{{ formatTime(item.enrollment.completionTime) }}</p>
                </div>
                <div class="certificate-actions">
                  <el-button type="primary" size="small" @click="downloadCertificate(item.enrollment)">
                    下载证书
                  </el-button>
                </div>
              </el-card>
            </el-col>
          </el-row>

          <div v-if="completedCourses.length === 0 && !loading" class="empty-state">
            <el-empty description="暂无结业证书">
              <el-button type="primary" @click="$router.push('/campus/training')">
                去学习
              </el-button>
            </el-empty>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>

    <!-- 考试对话框 -->
    <el-dialog
      v-model="examDialogVisible"
      :title="currentCourse?.title + ' - 在线考试'"
      width="70%"
      destroy-on-close
      :close-on-click-modal="false"
      :before-close="handleExamClose"
    >
      <div v-if="examQuestions.length > 0" class="exam-container">
        <div class="exam-progress">
          <el-progress :percentage="examProgress" :format="formatProgress" />
        </div>

        <div class="question-container" v-for="(question, index) in examQuestions" :key="question.id">
          <div class="question-item" v-if="currentQuestionIndex === index">
            <div class="question-header">
              <span class="question-number">第 {{ index + 1 }} 题 / 共 {{ examQuestions.length }} 题</span>
              <span class="question-score">分值: {{ question.score }}分</span>
            </div>
            <div class="question-text">{{ question.questionText }}</div>

            <div class="question-options" v-if="question.questionType !== 'subjective'">
              <el-radio-group v-model="answers[question.id]" class="options-group">
                <div v-for="option in JSON.parse(question.options)" :key="option.id" class="option-item">
                  <el-radio :label="option.text">{{ option.text }}</el-radio>
                </div>
              </el-radio-group>
            </div>

            <div class="question-input" v-else>
              <el-input
                v-model="answers[question.id]"
                type="textarea"
                :rows="4"
                placeholder="请输入答案"
              />
            </div>
          </div>
        </div>

        <div class="exam-actions">
          <el-button @click="previousQuestion" :disabled="currentQuestionIndex === 0">
            上一题
          </el-button>
          <el-button
            @click="nextQuestion"
            :disabled="currentQuestionIndex === examQuestions.length - 1"
          >
            下一题
          </el-button>
          <el-button
            type="primary"
            @click="handleExamSubmit"
            :disabled="!canSubmit"
            v-if="currentQuestionIndex === examQuestions.length - 1"
          >
            提交试卷
          </el-button>
        </div>
      </div>
      <div v-else class="no-exam">
        <el-empty description="暂无考试题目，请联系管理员">
          <el-button @click="examDialogVisible = false">关闭</el-button>
        </el-empty>
      </div>
    </el-dialog>

    <!-- 评价对话框 -->
    <el-dialog
      v-model="evaluateDialogVisible"
      title="课程评价"
      width="450px"
    >
      <el-form :model="evaluationForm" label-width="80px">
        <el-form-item label="评分" required>
          <el-rate v-model="evaluationForm.rating" :max="5" />
        </el-form-item>
        <el-form-item label="评价内容">
          <el-input
            v-model="evaluationForm.comment"
            type="textarea"
            :rows="4"
            placeholder="请输入您的评价（选填）"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="evaluateDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitEvaluation" :disabled="evaluationForm.rating === 0">
          提交评价
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getMyCourses,
  submitExam,
  evaluateCourse,
  getExamRecords
} from '@/api/training'
import { useUserStore } from '@/store/modules/user'
import {
  User,
  Timer,
  Trophy
} from '@element-plus/icons-vue'

const userStore = useUserStore()

const activeTab = ref('courses')
const myCourses = ref([])
const examRecords = ref([])
const completedCourses = ref([])
const loading = ref(false)

// 考试相关
const examDialogVisible = ref(false)
const currentCourse = ref(null)
const examQuestions = ref([])
const currentQuestionIndex = ref(0)
const answers = ref({})
const evaluateDialogVisible = ref(false)
const evaluationForm = ref({
  rating: 0,
  comment: ''
})

// 计算属性
const examProgress = computed(() => {
  if (examQuestions.value.length === 0) return 0
  return ((currentQuestionIndex.value + 1) / examQuestions.value.length) * 100
})

const canSubmit = computed(() => {
  return examQuestions.value.every(q => answers.value[q.id])
})

// 获取我的课程
const fetchMyCourses = async () => {
  try {
    loading.value = true
    const res = await getMyCourses({ studentId: userStore.userInfo.id })
    myCourses.value = res

    // 分类已完成课程
    completedCourses.value = res.filter(item =>
      item.enrollment.status === 'completed'
    )
  } catch (error) {
    ElMessage.error('获取课程失败')
  } finally {
    loading.value = false
  }
}

// 获取考试记录
const fetchExamRecords = async () => {
  try {
    loading.value = true
    const res = await getExamRecords({ studentId: userStore.userInfo.id })
    // 为考试记录添加课程名称（简化处理）
    examRecords.value = res.map(record => ({
      ...record,
      courseName: '课程ID: ' + record.examId,
      examTitle: '结业考试'
    }))
  } catch (error) {
    ElMessage.error('获取考试记录失败')
  } finally {
    loading.value = false
  }
}

// 继续学习
const continueLearning = (course) => {
  ElMessage.info('视频学习功能开发中...')
  // 这里可以跳转到视频播放页面
  // router.push(`/campus/training/learn/${course.id}`)
}

// 显示考试
const showExam = (course) => {
  currentCourse.value = course
  examDialogVisible.value = true
  currentQuestionIndex.value = 0
  answers.value = {}

  // 模拟考试题目（实际应从API获取）
  examQuestions.value = [
    {
      id: 1,
      questionType: 'single',
      questionText: '本课程的核心理念是什么？',
      options: JSON.stringify([
        { id: 1, text: 'A. 理论为主', is_correct: false },
        { id: 2, text: 'B. 实践为主', is_correct: true },
        { id: 3, text: 'C. 考试为主', is_correct: false }
      ]),
      score: 25
    },
    {
      id: 2,
      questionType: 'multiple',
      questionText: '本课程包含哪些内容？（多选）',
      options: JSON.stringify([
        { id: 1, text: 'A. 基础知识', is_correct: true },
        { id: 2, text: 'B. 实践案例', is_correct: true },
        { id: 3, text: 'C. 考试技巧', is_correct: false }
      ]),
      score: 25
    },
    {
      id: 3,
      questionType: 'judge',
      questionText: '本课程适合所有水平的学员学习',
      options: JSON.stringify([
        { id: 1, text: '正确', is_correct: true },
        { id: 2, text: '错误', is_correct: false }
      ]),
      score: 25
    },
    {
      id: 4,
      questionType: 'subjective',
      questionText: '请简述学习本课程的收获',
      score: 25
    }
  ]
}

// 上一题
const previousQuestion = () => {
  if (currentQuestionIndex.value > 0) {
    currentQuestionIndex.value--
  }
}

// 下一题
const nextQuestion = () => {
  if (currentQuestionIndex.value < examQuestions.value.length - 1) {
    currentQuestionIndex.value++
  }
}

// 提交考试
const handleExamSubmit = async () => {
  if (!canSubmit.value) {
    ElMessage.warning('请回答所有题目')
    return
  }

  ElMessageBox.confirm(
    '确定要提交试卷吗？提交后将无法修改',
    '提交确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      // 计算分数（简化版）
      let totalScore = 0
      examQuestions.value.forEach(q => {
        const userAnswer = answers.value[q.id]
        if (q.questionType === 'single' || q.questionType === 'judge') {
          // 简单判断是否正确（模拟）
          if (userAnswer) totalScore += q.score
        } else if (q.questionType === 'multiple') {
          // 多选题部分得分
          if (userAnswer) totalScore += q.score * 0.5
        } else {
          // 主观题给基础分
          if (userAnswer) totalScore += q.score * 0.3
        }
      })

      const res = await submitExam({
        examId: currentCourse.value.id,
        studentId: userStore.userInfo.id,
        studentName: userStore.userInfo.realName,
        score: totalScore,
        answers: JSON.stringify(answers.value)
      })

      if (res.isPassed) {
        ElMessage.success(`考试通过！得分: ${totalScore}分`)
        evaluateDialogVisible.value = true
      } else {
        ElMessage.warning(`考试未通过，得分: ${totalScore}分`)
      }

      examDialogVisible.value = false
      // 刷新数据
      fetchMyCourses()
      fetchExamRecords()
    } catch (error) {
      ElMessage.error('提交失败')
    }
  }).catch(() => {})
}

// 考试关闭前确认
const handleExamClose = (done) => {
  if (Object.keys(answers.value).length > 0) {
    ElMessageBox.confirm('有未提交的答案，确定要关闭吗？', '关闭确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      done()
    }).catch(() => {})
  } else {
    done()
  }
}

// 提交评价
const submitEvaluation = async () => {
  if (evaluationForm.value.rating === 0) {
    ElMessage.warning('请先评分')
    return
  }

  try {
    await evaluateCourse({
      courseId: currentCourse.value.id,
      studentId: userStore.userInfo.id,
      studentName: userStore.userInfo.realName,
      rating: evaluationForm.value.rating,
      comment: evaluationForm.value.comment
    })
    ElMessage.success('评价提交成功！')
    evaluateDialogVisible.value = false
    evaluationForm.value = { rating: 0, comment: '' }
  } catch (error) {
    // 错误已在request.js中处理
    console.error('评价提交失败:', error)
  }
}

// 查看证书
const viewCertificate = (enrollment) => {
  ElMessage.info('证书查看功能开发中...')
  // window.open(enrollment.certificateUrl, '_blank')
}

// 下载证书
const downloadCertificate = (enrollment) => {
  ElMessage.info('证书下载功能开发中...')
  // 实际应该调用后端API生成PDF并下载
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return '-'
  return new Date(time).toLocaleString('zh-CN')
}

// 格式化进度
const formatProgress = (percentage) => {
  return `${percentage}%`
}

// 获取状态样式
const getStatusType = (status) => {
  const map = {
    'enrolled': 'info',
    'learning': 'primary',
    'completed': 'success',
    'dropped': 'danger'
  }
  return map[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const map = {
    'enrolled': '已报名',
    'learning': '学习中',
    'completed': '已完成',
    'dropped': '已退出'
  }
  return map[status] || status
}

// 标签页切换
const handleTabChange = (tab) => {
  if (tab === 'exams') {
    fetchExamRecords()
  } else if (tab === 'courses') {
    fetchMyCourses()
  }
}

onMounted(() => {
  fetchMyCourses()
})
</script>

<style scoped>
.my-training {
  padding: 20px;
}

/* 我的课程样式 */
.course-card {
  margin-bottom: 15px;
  transition: transform 0.2s;
}

.course-card:hover {
  transform: translateY(-3px);
}

.course-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.course-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.progress-section {
  margin-bottom: 12px;
}

.progress-text {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
  text-align: center;
}

.course-meta {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #606266;
  margin-bottom: 12px;
}

.course-meta > div {
  display: flex;
  align-items: center;
  gap: 4px;
}

.course-actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin-bottom: 8px;
}

.score-info {
  margin-bottom: 8px;
  text-align: center;
}

.enroll-time {
  font-size: 11px;
  color: #909399;
  text-align: center;
  padding-top: 8px;
  border-top: 1px solid #f5f5f5;
}

.empty-state {
  margin-top: 40px;
}

.empty-table {
  margin-top: 20px;
}

/* 证书样式 */
.certificate-card {
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  border: 2px solid #ffd700;
  text-align: center;
  margin-bottom: 15px;
}

.certificate-header {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-bottom: 15px;
  color: #d4a574;
}

.certificate-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 700;
}

.certificate-content {
  padding: 15px;
  background: white;
  border-radius: 8px;
  margin-bottom: 15px;
}

.certificate-content p {
  margin: 8px 0;
}

.course-name {
  font-weight: 600;
  font-size: 16px;
  color: #303133;
}

.student-name {
  font-size: 18px;
  font-weight: 700;
  color: #d4a574;
}

.score {
  font-size: 14px;
  color: #606266;
}

.date {
  font-size: 12px;
  color: #909399;
}

.certificate-actions {
  text-align: center;
}

/* 考试样式 */
.exam-container {
  min-height: 400px;
}

.exam-progress {
  margin-bottom: 20px;
}

.question-container {
  min-height: 250px;
}

.question-item {
  padding: 20px;
  background: #f5f7fa;
  border-radius: 8px;
  margin-bottom: 15px;
}

.question-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 15px;
  font-size: 14px;
  color: #606266;
}

.question-text {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 20px;
  line-height: 1.5;
  color: #303133;
}

.question-options {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.options-group {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.option-item {
  padding: 10px;
  background: white;
  border-radius: 4px;
  border: 1px solid #ebeef5;
}

.option-item:hover {
  background: #f5f7fa;
}

.question-input {
  margin-top: 15px;
}

.exam-actions {
  display: flex;
  justify-content: center;
  gap: 15px;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}

.no-exam {
  min-height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .el-col-8 {
    width: 100%;
  }

  .certificate-card {
    margin-bottom: 10px;
  }
}
</style>
