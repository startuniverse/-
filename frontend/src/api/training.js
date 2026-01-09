import request from '@/utils/request'

/**
 * 获取培训课程列表
 */
export function getTrainingCourses(params) {
  return request({
    url: '/training/course/list',
    method: 'get',
    params
  })
}

/**
 * 获取课程详情
 */
export function getCourseDetail(id) {
  return request({
    url: `/training/course/detail/${id}`,
    method: 'get'
  })
}

/**
 * 报名课程
 */
export function enrollCourse(data) {
  return request({
    url: '/training/enroll',
    method: 'post',
    data
  })
}

/**
 * 获取我的课程
 */
export function getMyCourses(params) {
  return request({
    url: '/training/my-courses',
    method: 'get',
    params
  })
}

/**
 * 提交考试
 */
export function submitExam(data) {
  return request({
    url: '/training/exam/submit',
    method: 'post',
    data
  })
}

/**
 * 课程评价
 */
export function evaluateCourse(data) {
  return request({
    url: '/training/evaluate',
    method: 'post',
    data
  })
}

/**
 * 管理员获取课程列表（包含所有状态）
 */
export function getAdminCourses(params) {
  return request({
    url: '/training/admin/courses',
    method: 'get',
    params
  })
}

/**
 * 管理员保存课程
 */
export function adminSaveCourse(data) {
  return request({
    url: '/training/admin/course/save',
    method: 'post',
    data
  })
}

/**
 * 获取培训统计
 */
export function getTrainingStatistics() {
  return request({
    url: '/training/admin/statistics',
    method: 'get'
  })
}

/**
 * 获取考试记录
 */
export function getExamRecords(params) {
  return request({
    url: '/training/exam/records',
    method: 'get',
    params
  })
}

/**
 * 获取课程评价列表
 */
export function getEvaluations(params) {
  return request({
    url: '/training/evaluation/list',
    method: 'get',
    params
  })
}

/**
 * 获取课程评分
 */
export function getCourseRating(id) {
  return request({
    url: `/training/course/rating/${id}`,
    method: 'get'
  })
}
