import request from '@/utils/request'

/**
 * 搜索老师
 */
export function searchTeachers(params) {
  return request({
    url: '/class-application/search-teachers',
    method: 'get',
    params
  })
}

/**
 * 学生创建班级申请
 */
export function createClassApplication(data) {
  return request({
    url: '/class-application/apply',
    method: 'post',
    data
  })
}

/**
 * 获取学生的申请列表
 */
export function getStudentApplications(studentId) {
  return request({
    url: `/class-application/student/${studentId}`,
    method: 'get'
  })
}

/**
 * 获取老师的待审核申请列表
 */
export function getPendingApplications(teacherId) {
  return request({
    url: `/class-application/teacher/pending/${teacherId}`,
    method: 'get'
  })
}

/**
 * 获取老师的所有申请列表
 */
export function getTeacherApplications(teacherId) {
  return request({
    url: `/class-application/teacher/${teacherId}`,
    method: 'get'
  })
}

/**
 * 老师审核申请
 */
export function reviewApplication(applicationId, data) {
  return request({
    url: `/class-application/review/${applicationId}`,
    method: 'post',
    data
  })
}

/**
 * 老师获取自己的学生列表
 */
export function getTeacherStudents(params) {
  return request({
    url: '/class-application/teacher/students',
    method: 'get',
    params
  })
}

/**
 * 获取老师管理的班级列表
 */
export function getTeacherClasses(teacherId) {
  return request({
    url: '/class-application/teacher/classes',
    method: 'get',
    params: { teacherId }
  })
}
