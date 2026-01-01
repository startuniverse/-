import request from '@/utils/request'

/**
 * 教师相关API
 */

// 获取教师仪表盘数据
export function getTeacherDashboard() {
  return request({
    url: '/teacher/dashboard',
    method: 'get'
  })
}

// 获取我的学生列表
export function getMyStudents(params) {
  return request({
    url: '/teacher/students',
    method: 'get',
    params
  })
}

// 获取成绩管理数据
export function getGradeManagement(params) {
  return request({
    url: '/teacher/grades',
    method: 'get',
    params
  })
}

// 更新成绩
export function updateGrade(data) {
  return request({
    url: '/teacher/grades/update',
    method: 'post',
    data
  })
}

// 导出成绩
export function exportGrades(params) {
  return request({
    url: '/teacher/grades/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

// 获取班级列表
export function getClassList() {
  return request({
    url: '/teacher/classes',
    method: 'get'
  })
}

// 发布作业
export function publishAssignment(data) {
  return request({
    url: '/teacher/assignments',
    method: 'post',
    data
  })
}

// 发布通知
export function publishAnnouncement(data) {
  return request({
    url: '/teacher/announcements',
    method: 'post',
    data
  })
}
