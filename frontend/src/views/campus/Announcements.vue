<template>
  <div class="announcements">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>通知公告</span>
          <div class="controls">
            <el-select v-model="type" placeholder="公告类型" size="small" style="width: 120px;" clearable>
              <el-option label="通知" value="notice"></el-option>
              <el-option label="公告" value="announcement"></el-option>
              <el-option label="紧急" value="emergency"></el-option>
            </el-select>
            <el-button type="primary" size="small" @click="loadAnnouncements">查询</el-button>
          </div>
        </div>
      </template>

      <el-table v-loading="loading" :data="announcementsData" border style="width: 100%">
        <el-table-column prop="type" label="类型" width="100" align="center">
          <template #default="scope">
            <el-tag :type="getTypeTag(scope.row.type)">{{ getTypeText(scope.row.type) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" min-width="200" />
        <el-table-column prop="publisherId" label="发布者" width="120" align="center">
          <template #default="scope">
            {{ getPublisherText(scope.row) }}
          </template>
        </el-table-column>
        <el-table-column prop="publishTime" label="发布时间" width="160" align="center">
          <template #default="scope">
            {{ formatPublishTime(scope.row.publishTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="priority" label="优先级" width="100" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.priority === 1 ? 'danger' : 'info'">
              {{ scope.row.priority === 1 ? '紧急' : '普通' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" align="center">
          <template #default="scope">
            <el-button type="primary" link size="small" @click="viewDetail(scope.row)">
              查看
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

    <!-- 详情对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="selectedAnnouncement?.title"
      width="600px"
    >
      <div class="announcement-detail">
        <div class="detail-meta">
          <span>类型: {{ getTypeText(selectedAnnouncement?.type) }}</span>
          <span>发布者: {{ getPublisherText(selectedAnnouncement) }}</span>
        </div>
        <div class="detail-meta">
          <span>发布时间: {{ formatPublishTime(selectedAnnouncement?.publishTime) }}</span>
          <span>优先级: {{ selectedAnnouncement?.priority === 1 ? '紧急' : '普通' }}</span>
        </div>
        <div class="detail-content">
          {{ selectedAnnouncement?.content }}
        </div>
      </div>
      <template #footer>
        <el-button @click="dialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getAnnouncements } from '@/api/campus'

const loading = ref(false)
const type = ref('')
const announcementsData = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const selectedAnnouncement = ref(null)

const getTypeText = (type) => {
  const map = {
    notice: '通知',
    announcement: '公告',
    emergency: '紧急'
  }
  return map[type] || type
}

const getTypeTag = (type) => {
  const map = {
    notice: 'info',
    announcement: 'success',
    emergency: 'danger'
  }
  return map[type] || 'info'
}

const getPublisherText = (row) => {
  // 如果有publisherName字段，直接显示
  if (row.publisherName) {
    return row.publisherName
  }
  // 否则显示发布者ID
  return row.publisherId ? `教师${row.publisherId}` : '未知'
}

const formatPublishTime = (time) => {
  if (!time) return '-'
  try {
    return new Date(time).toLocaleString('zh-CN')
  } catch (e) {
    return time
  }
}

const loadAnnouncements = async () => {
  try {
    loading.value = true
    const params = {
      current: currentPage.value,
      size: pageSize.value
    }
    if (type.value) params.type = type.value

    const data = await getAnnouncements(params)
    announcementsData.value = data.records
    total.value = data.total
  } catch (error) {
    ElMessage.error('加载公告失败')
  } finally {
    loading.value = false
  }
}

const viewDetail = (row) => {
  selectedAnnouncement.value = row
  dialogVisible.value = true
}

const handleSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1
  loadAnnouncements()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  loadAnnouncements()
}

onMounted(() => {
  loadAnnouncements()
})
</script>

<style scoped>
.announcements {
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

.announcement-detail {
  padding: 10px;
}

.detail-meta {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #ebeef5;
  color: #909399;
  font-size: 13px;
}

.detail-content {
  line-height: 1.8;
  color: #303133;
  white-space: pre-line;
}
</style>
