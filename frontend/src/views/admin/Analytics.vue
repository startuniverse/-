<template>
  <div class="analytics">
    <el-row :gutter="20">
      <!-- 教师分布分析 -->
      <el-col :span="24">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>教师分布分析</span>
              <el-button type="primary" size="small" @click="loadTeacherDistribution">刷新</el-button>
            </div>
          </template>
          <div v-loading="loading.teacher" class="chart-container">
            <div ref="teacherChartRef" style="width: 100%; height: 400px;"></div>
          </div>
        </el-card>
      </el-col>

      <!-- 学校布局分析 -->
      <el-col :span="12" :xs="24">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>学校布局分析</span>
            </div>
          </template>
          <div v-loading="loading.layout" class="chart-container">
            <div ref="layoutChartRef" style="width: 100%; height: 300px;"></div>
          </div>
        </el-card>
      </el-col>

      <!-- 成绩趋势分析 -->
      <el-col :span="12" :xs="24">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>成绩趋势分析</span>
            </div>
          </template>
          <div v-loading="loading.trend" class="chart-container">
            <div ref="trendChartRef" style="width: 100%; height: 300px;"></div>
          </div>
        </el-card>
      </el-col>

      <!-- 政策参考报告 -->
      <el-col :span="24">
        <el-card class="report-card">
          <template #header>
            <div class="card-header">
              <span>政策参考报告</span>
              <el-button type="success" size="small" @click="generateReport">生成报告</el-button>
            </div>
          </template>
          <div v-if="report" class="report-content">
            <h3>{{ report.title }}</h3>
            <p class="meta">生成日期: {{ report.generatedDate }}</p>
            <div class="summary">
              <h4>摘要</h4>
              <p>{{ report.summary }}</p>
            </div>
            <div class="metrics">
              <h4>关键指标</h4>
              <el-descriptions :column="2" border>
                <el-descriptions-item
                  v-for="(value, key) in report.keyMetrics"
                  :key="key"
                  :label="key"
                >
                  {{ value }}
                </el-descriptions-item>
              </el-descriptions>
            </div>
            <div class="recommendations">
              <h4>建议</h4>
              <el-timeline>
                <el-timeline-item
                  v-for="(item, index) in report.recommendations"
                  :key="index"
                  :type="index === 0 ? 'primary' : 'info'"
                >
                  {{ item }}
                </el-timeline-item>
              </el-timeline>
            </div>
          </div>
          <div v-else class="empty-report">
            <el-empty description="点击生成报告按钮生成分析报告" />
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'
import { getAnalyticsChart, getTeacherDistribution, getPolicyReport } from '@/api/admin'

const teacherChartRef = ref(null)
const layoutChartRef = ref(null)
const trendChartRef = ref(null)

let teacherChart = null
let layoutChart = null
let trendChart = null

const loading = ref({
  teacher: false,
  layout: false,
  trend: false
})

const report = ref(null)

// 加载教师分布图表
const loadTeacherDistribution = async () => {
  try {
    loading.value.teacher = true
    const data = await getTeacherDistribution()

    if (!teacherChart) {
      teacherChart = echarts.init(teacherChartRef.value)
    }

    const option = {
      tooltip: {
        trigger: 'item',
        formatter: '{b}: {c} ({d}%)'
      },
      legend: {
        orient: 'vertical',
        left: 'left'
      },
      series: [
        {
          name: '教师职称分布',
          type: 'pie',
          radius: '50%',
          data: Object.entries(data.byTitle).map(([name, value]) => ({ name, value })),
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          }
        }
      ]
    }

    teacherChart.setOption(option)
  } catch (error) {
    ElMessage.error('加载教师分布数据失败')
  } finally {
    loading.value.teacher = false
  }
}

// 加载学校布局图表
const loadLayoutChart = async () => {
  try {
    loading.value.layout = true
    const data = await getAnalyticsChart({ chartType: 'pie', analysisType: 'layout' })

    if (!layoutChart) {
      layoutChart = echarts.init(layoutChartRef.value)
    }

    const option = {
      tooltip: {
        trigger: 'item',
        formatter: '{b}: {c} ({d}%)'
      },
      series: [
        {
          name: '学校类型',
          type: 'pie',
          radius: ['40%', '70%'],
          avoidLabelOverlap: false,
          itemStyle: {
            borderRadius: 10,
            borderColor: '#fff',
            borderWidth: 2
          },
          label: {
            show: false,
            position: 'center'
          },
          emphasis: {
            label: {
              show: true,
              fontSize: 18,
              fontWeight: 'bold'
            }
          },
          labelLine: {
            show: false
          },
          data: data.data || [
            { value: 25, name: '小学' },
            { value: 20, name: '中学' },
            { value: 5, name: '职业学校' }
          ]
        }
      ]
    }

    layoutChart.setOption(option)
  } catch (error) {
    ElMessage.error('加载学校布局数据失败')
  } finally {
    loading.value.layout = false
  }
}

// 加载趋势图表
const loadTrendChart = async () => {
  try {
    loading.value.trend = true
    const data = await getAnalyticsChart({ chartType: 'line', analysisType: 'trend' })

    if (!trendChart) {
      trendChart = echarts.init(trendChartRef.value)
    }

    const option = {
      tooltip: {
        trigger: 'axis'
      },
      xAxis: {
        type: 'category',
        data: data.xAxis || ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
      },
      yAxis: {
        type: 'value'
      },
      series: [
        {
          name: '平均分',
          type: 'line',
          smooth: true,
          data: data.series || [820, 932, 901, 934, 1290, 1330, 1320],
          areaStyle: {
            opacity: 0.3
          },
          itemStyle: {
            color: '#409EFF'
          }
        }
      ]
    }

    trendChart.setOption(option)
  } catch (error) {
    ElMessage.error('加载趋势数据失败')
  } finally {
    loading.value.trend = false
  }
}

// 生成政策报告
const generateReport = async () => {
  try {
    const data = await getPolicyReport()
    report.value = data
    ElMessage.success('报告生成成功')
  } catch (error) {
    ElMessage.error('生成报告失败')
  }
}

// 响应式图表
const handleResize = () => {
  teacherChart && teacherChart.resize()
  layoutChart && layoutChart.resize()
  trendChart && trendChart.resize()
}

onMounted(() => {
  loadTeacherDistribution()
  loadLayoutChart()
  loadTrendChart()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  teacherChart && teacherChart.dispose()
  layoutChart && layoutChart.dispose()
  trendChart && trendChart.dispose()
})
</script>

<style scoped>
.analytics {
  width: 100%;
}

.chart-card,
.report-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
}

.chart-container {
  min-height: 300px;
}

.report-content h3 {
  margin: 0 0 10px 0;
  color: #303133;
}

.meta {
  color: #909399;
  font-size: 13px;
  margin-bottom: 20px;
}

.summary,
.metrics,
.recommendations {
  margin-bottom: 25px;
}

.summary h4,
.metrics h4,
.recommendations h4 {
  margin: 0 0 10px 0;
  color: #303133;
}

.summary p {
  color: #606266;
  line-height: 1.6;
  margin: 0;
}

.empty-report {
  padding: 40px 0;
}
</style>
