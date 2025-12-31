import request from '@/utils/request'

/**
 * 获取仪表盘信息
 */
export function getDashboard() {
  return request({
    url: '/campus/dashboard',
    method: 'get'
  })
}

/**
 * 获取个人信息
 */
export function getProfile() {
  return request({
    url: '/campus/profile',
    method: 'get'
  })
}

/**
 * 更新个人信息
 */
export function updateProfile(data) {
  return request({
    url: '/campus/profile/update',
    method: 'post',
    data
  })
}

/**
 * 获取班级信息
 */
export function getClassInfo() {
  return request({
    url: '/campus/class/info',
    method: 'get'
  })
}

/**
 * 获取课程表
 */
export function getTimetable(params) {
  return request({
    url: '/campus/timetable',
    method: 'get',
    params
  })
}

/**
 * 获取成绩
 */
export function getGrades(params) {
  return request({
    url: '/campus/grades',
    method: 'get',
    params
  })
}

/**
 * 获取通知公告
 */
export function getAnnouncements(params) {
  return request({
    url: '/campus/announcements',
    method: 'get',
    params
  })
}

/**
 * 修改密码
 */
export function changePassword(data) {
  return request({
    url: '/campus/profile/change-password',
    method: 'post',
    data
  })
}
