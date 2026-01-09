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

/**
 * 提交学籍异动申请
 */
export function applyStatusChange(data) {
  return request({
    url: '/backend/status-change/apply',
    method: 'post',
    data
  })
}

/**
 * 获取我的学籍异动列表
 */
export function getMyStatusChanges(params) {
  return request({
    url: '/backend/status-change/my-list',
    method: 'get',
    params
  })
}

/**
 * 获取学习资源列表
 */
export function getStudentResources(params) {
  return request({
    url: '/resource/list',
    method: 'get',
    params
  })
}

/**
 * 下载资源
 */
export function downloadResource(id) {
  return request({
    url: `/resource/download/${id}`,
    method: 'post'
  })
}

/**
 * 分享资源
 */
export function shareResource(id) {
  return request({
    url: '/resource/share',
    method: 'post',
    params: { id }
  })
}

/**
 * 获取资源详情（增加浏览次数）
 */
export function getResourceDetail(id) {
  return request({
    url: `/resource/detail/${id}`,
    method: 'get'
  })
}
