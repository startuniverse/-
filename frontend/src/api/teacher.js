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

// 添加成绩
export function addGrade(data) {
  return request({
    url: '/teacher/grades',
    method: 'post',
    data
  })
}

// 更新成绩
export function updateGrade(id, data) {
  return request({
    url: `/teacher/grades/${id}`,
    method: 'put',
    data
  })
}

// 删除成绩
export function deleteGrade(id) {
  return request({
    url: `/teacher/grades/${id}`,
    method: 'delete'
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

// 获取教师课程表
export function getTeacherTimetable(params) {
  return request({
    url: '/teacher/timetable',
    method: 'get',
    params
  })
}

// 添加课程表
export function addTeacherTimetable(data) {
  return request({
    url: '/teacher/timetable',
    method: 'post',
    data
  })
}

// 删除课程表
export function deleteTeacherTimetable(id) {
  return request({
    url: `/teacher/timetable/${id}`,
    method: 'delete'
  })
}

// 批量添加课程表
export function batchAddTeacherTimetable(data) {
  return request({
    url: '/teacher/timetable/batch',
    method: 'post',
    data
  })
}

// 获取教师负责的班级列表
export function getTeacherClasses() {
  return request({
    url: '/teacher/classes',
    method: 'get'
  })
}

// 获取教师班级的学生列表
export function getTeacherStudents() {
  return request({
    url: '/teacher/students',
    method: 'get'
  })
}
