import request from '@/utils/request'

// ==================== 教师基础信息 ====================

/**
 * 获取教师基础信息列表
 */
export function getTeacherBasicList(params) {
  return request({
    url: '/teacher-info/basic/list',
    method: 'get',
    params
  })
}

/**
 * 新增教师基础信息
 */
export function addTeacherBasic(data) {
  return request({
    url: '/teacher-info/basic/add',
    method: 'post',
    data
  })
}

/**
 * 更新教师基础信息
 */
export function updateTeacherBasic(data) {
  return request({
    url: '/teacher-info/basic/update',
    method: 'post',
    data
  })
}

/**
 * 删除教师基础信息
 */
export function deleteTeacherBasic(id) {
  return request({
    url: '/teacher-info/basic/delete',
    method: 'delete',
    params: { id }
  })
}

/**
 * 根据教师ID查询完整信息
 */
export function getTeacherInfo(teacherId) {
  return request({
    url: `/teacher-info/basic/${teacherId}`,
    method: 'get'
  })
}

/**
 * 获取教师综合信息
 */
export function getTeacherComprehensiveInfo(teacherId) {
  return request({
    url: `/teacher-info/comprehensive/${teacherId}`,
    method: 'get'
  })
}

// ==================== 岗位信息 ====================

/**
 * 获取岗位信息列表
 */
export function getTeacherPositionList(teacherId) {
  return request({
    url: '/teacher-info/position/list',
    method: 'get',
    params: { teacherId }
  })
}

/**
 * 新增岗位信息
 */
export function addTeacherPosition(data) {
  return request({
    url: '/teacher-info/position/add',
    method: 'post',
    data
  })
}

// ==================== 教育背景 ====================

/**
 * 获取教育背景列表
 */
export function getTeacherEducationList(teacherId) {
  return request({
    url: '/teacher-info/education/list',
    method: 'get',
    params: { teacherId }
  })
}

/**
 * 新增教育背景
 */
export function addTeacherEducation(data) {
  return request({
    url: '/teacher-info/education/add',
    method: 'post',
    data
  })
}

// ==================== 资格职称 ====================

/**
 * 获取资格职称列表
 */
export function getTeacherQualificationList(teacherId) {
  return request({
    url: '/teacher-info/qualification/list',
    method: 'get',
    params: { teacherId }
  })
}

/**
 * 新增资格职称
 */
export function addTeacherQualification(data) {
  return request({
    url: '/teacher-info/qualification/add',
    method: 'post',
    data
  })
}

// ==================== 荣誉称号 ====================

/**
 * 获取荣誉称号列表
 */
export function getTeacherHonorList(teacherId) {
  return request({
    url: '/teacher-info/honor/list',
    method: 'get',
    params: { teacherId }
  })
}

/**
 * 新增荣誉称号
 */
export function addTeacherHonor(data) {
  return request({
    url: '/teacher-info/honor/add',
    method: 'post',
    data
  })
}

// ==================== 考核记录 ====================

/**
 * 获取考核记录列表
 */
export function getTeacherAssessmentList(teacherId) {
  return request({
    url: '/teacher-info/assessment/list',
    method: 'get',
    params: { teacherId }
  })
}

/**
 * 新增考核记录
 */
export function addTeacherAssessment(data) {
  return request({
    url: '/teacher-info/assessment/add',
    method: 'post',
    data
  })
}

/**
 * 获取考核统计
 */
export function getTeacherAssessmentStatistics(teacherId) {
  return request({
    url: '/teacher-info/assessment/statistics',
    method: 'get',
    params: { teacherId }
  })
}

// ==================== 奖惩记录 ====================

/**
 * 获取奖惩记录列表
 */
export function getTeacherRewardPunishmentList(teacherId, type) {
  return request({
    url: '/teacher-info/reward-punishment/list',
    method: 'get',
    params: { teacherId, type }
  })
}

/**
 * 新增奖惩记录
 */
export function addTeacherRewardPunishment(data) {
  return request({
    url: '/teacher-info/reward-punishment/add',
    method: 'post',
    data
  })
}

// ==================== 师德考核 ====================

/**
 * 获取师德考核列表
 */
export function getTeacherEthicsList(teacherId) {
  return request({
    url: '/teacher-info/ethics/list',
    method: 'get',
    params: { teacherId }
  })
}

/**
 * 新增师德考核
 */
export function addTeacherEthics(data) {
  return request({
    url: '/teacher-info/ethics/add',
    method: 'post',
    data
  })
}

/**
 * 获取师德考核统计
 */
export function getTeacherEthicsStatistics(teacherId) {
  return request({
    url: '/teacher-info/ethics/statistics',
    method: 'get',
    params: { teacherId }
  })
}

// ==================== 培训记录 ====================

/**
 * 获取培训记录列表
 */
export function getTeacherTrainingList(teacherId, category) {
  return request({
    url: '/teacher-info/training/list',
    method: 'get',
    params: { teacherId, category }
  })
}

/**
 * 新增培训记录
 */
export function addTeacherTraining(data) {
  return request({
    url: '/teacher-info/training/add',
    method: 'post',
    data
  })
}

/**
 * 获取培训统计
 */
export function getTeacherTrainingStatistics(teacherId) {
  return request({
    url: '/teacher-info/training/statistics',
    method: 'get',
    params: { teacherId }
  })
}

// ==================== 继续教育学分 ====================

/**
 * 获取学分记录列表
 */
export function getTeacherCreditList(teacherId, academicYear) {
  return request({
    url: '/teacher-info/credit/list',
    method: 'get',
    params: { teacherId, academicYear }
  })
}

/**
 * 新增学分记录
 */
export function addTeacherCredit(data) {
  return request({
    url: '/teacher-info/credit/add',
    method: 'post',
    data
  })
}

/**
 * 获取学分统计
 */
export function getTeacherCreditStatistics(teacherId) {
  return request({
    url: '/teacher-info/credit/statistics',
    method: 'get',
    params: { teacherId }
  })
}

// ==================== 教学任务 ====================

/**
 * 获取教学任务列表
 */
export function getTeacherTeachingTaskList(teacherId, academicYear, semester) {
  return request({
    url: '/teacher-info/teaching-task/list',
    method: 'get',
    params: { teacherId, academicYear, semester }
  })
}

/**
 * 新增教学任务
 */
export function addTeacherTeachingTask(data) {
  return request({
    url: '/teacher-info/teaching-task/add',
    method: 'post',
    data
  })
}

/**
 * 获取教学工作量统计
 */
export function getTeacherTeachingTaskStatistics(teacherId) {
  return request({
    url: '/teacher-info/teaching-task/statistics',
    method: 'get',
    params: { teacherId }
  })
}

// ==================== 教研活动 ====================

/**
 * 获取教研活动列表
 */
export function getTeacherResearchList(teacherId, activityType) {
  return request({
    url: '/teacher-info/research/list',
    method: 'get',
    params: { teacherId, activityType }
  })
}

/**
 * 新增教研活动
 */
export function addTeacherResearch(data) {
  return request({
    url: '/teacher-info/research/add',
    method: 'post',
    data
  })
}

/**
 * 获取教研活动统计
 */
export function getTeacherResearchStatistics(teacherId) {
  return request({
    url: '/teacher-info/research/statistics',
    method: 'get',
    params: { teacherId }
  })
}

// ==================== 工作量统计 ====================

/**
 * 获取工作量统计列表
 */
export function getTeacherWorkloadList(teacherId, statisticalYear, statisticalPeriod) {
  return request({
    url: '/teacher-info/workload/list',
    method: 'get',
    params: { teacherId, statisticalYear, statisticalPeriod }
  })
}

/**
 * 新增工作量统计
 */
export function addTeacherWorkload(data) {
  return request({
    url: '/teacher-info/workload/add',
    method: 'post',
    data
  })
}

/**
 * 获取工作量趋势
 */
export function getTeacherWorkloadTrend(teacherId, years) {
  return request({
    url: '/teacher-info/workload/trend',
    method: 'get',
    params: { teacherId, years }
  })
}
