import request from '@/utils/request'

/**
 * 获取管理仪表盘
 */
export function getAdminDashboard() {
  return request({
    url: '/backend/system/settings',
    method: 'get'
  })
}

/**
 * 获取学生列表
 */
export function getStudents(params) {
  return request({
    url: '/backend/student/list',
    method: 'get',
    params
  })
}

/**
 * 新增学生
 */
export function addStudent(data) {
  return request({
    url: '/backend/student/add',
    method: 'post',
    data
  })
}

/**
 * 更新学生
 */
export function updateStudent(data) {
  return request({
    url: '/backend/student/update',
    method: 'post',
    data
  })
}

/**
 * 删除学生
 */
export function deleteStudent(id) {
  return request({
    url: '/backend/student/delete',
    method: 'delete',
    params: { id }
  })
}

/**
 * 获取文档列表
 */
export function getDocuments(params) {
  return request({
    url: '/backend/document/query',
    method: 'get',
    params
  })
}

/**
 * 发布文档
 */
export function publishDocument(data) {
  return request({
    url: '/backend/document/publish',
    method: 'post',
    data
  })
}

/**
 * 审批文档
 */
export function approveDocument(params) {
  return request({
    url: '/backend/document/approve',
    method: 'post',
    params
  })
}

/**
 * 删除文档
 */
export function deleteDocument(id) {
  return request({
    url: '/backend/document/delete',
    method: 'post',
    params: { id }
  })
}

/**
 * 获取资源列表
 */
export function getResources(params) {
  return request({
    url: '/resource/list',
    method: 'get',
    params
  })
}

/**
 * 管理员获取资源列表（包含所有状态）
 */
export function getAdminResources(params) {
  return request({
    url: '/resource/admin/list',
    method: 'get',
    params
  })
}

/**
 * 获取资源统计
 */
export function getResourceStatistics() {
  return request({
    url: '/resource/statistics',
    method: 'get'
  })
}

/**
 * 删除资源
 */
export function deleteResource(id) {
  return request({
    url: '/resource/delete',
    method: 'delete',
    params: { id }
  })
}

/**
 * 管理员添加资源
 */
export function adminAddResource(data) {
  return request({
    url: '/resource/admin/add',
    method: 'post',
    data
  })
}

/**
 * 管理员编辑资源
 */
export function adminEditResource(data) {
  return request({
    url: '/resource/admin/edit',
    method: 'put',
    data
  })
}

/**
 * 审核资源
 */
export function reviewResource(params) {
  return request({
    url: '/resource/review',
    method: 'post',
    params
  })
}

/**
 * 获取数据分析图表
 */
export function getAnalyticsChart(params) {
  return request({
    url: '/bigdata/visualization/chart',
    method: 'get',
    params
  })
}

/**
 * 获取教师分布分析
 */
export function getTeacherDistribution() {
  return request({
    url: '/bigdata/analysis/teacher-distribution',
    method: 'get'
  })
}

/**
 * 获取政策报告
 */
export function getPolicyReport() {
  return request({
    url: '/bigdata/policy/report',
    method: 'get'
  })
}

/**
 * 获取资产列表
 */
export function getAssets(params) {
  return request({
    url: '/backend/asset/list',
    method: 'get',
    params
  })
}

/**
 * 新增资产
 */
export function addAsset(data) {
  return request({
    url: '/backend/asset/add',
    method: 'post',
    data
  })
}

/**
 * 更新资产
 */
export function updateAsset(data) {
  return request({
    url: '/backend/asset/update',
    method: 'post',
    data
  })
}

/**
 * 删除资产
 */
export function deleteAsset(id) {
  return request({
    url: '/backend/asset/delete',
    method: 'delete',
    params: { id }
  })
}

/**
 * 获取学籍异动列表
 */
export function getStatusChanges(params) {
  return request({
    url: '/backend/status-change/list',
    method: 'get',
    params
  })
}

/**
 * 审核学籍异动
 */
export function approveStatusChange(params) {
  return request({
    url: '/backend/status-change/approve',
    method: 'post',
    params
  })
}

/**
 * 删除学籍异动
 */
export function deleteStatusChange(id) {
  return request({
    url: '/backend/status-change/delete',
    method: 'delete',
    params: { id }
  })
}

/**
 * 获取学籍异动统计
 */
export function getStatusChangeStatistics() {
  return request({
    url: '/backend/status-change/statistics',
    method: 'get'
  })
}

/**
 * 获取学生统计信息
 */
export function getStudentStatistics() {
  return request({
    url: '/backend/student/statistics',
    method: 'get'
  })
}

/**
 * 获取所有班级申请列表
 */
export function getAllApplications(params) {
  return request({
    url: '/class-application/admin/all',
    method: 'get',
    params
  })
}

/**
 * 获取班级统计信息
 */
export function getClassStatistics() {
  return request({
    url: '/backend/class/statistics',
    method: 'get'
  })
}
