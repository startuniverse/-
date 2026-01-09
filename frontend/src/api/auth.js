import request from '@/utils/request'

/**
 * 用户登录
 */
export function login(username, password) {
  return request({
    url: '/auth/login',
    method: 'post',
    data: { username, password }
  })
}

/**
 * 用户注册
 */
export function register(data) {
  return request({
    url: '/auth/register',
    method: 'post',
    data
  })
}

/**
 * 教师注册
 */
export function registerTeacher(data) {
  return request({
    url: '/auth/register/teacher',
    method: 'post',
    data
  })
}

/**
 * 获取用户信息
 */
export function getUserInfo() {
  return request({
    url: '/auth/info',
    method: 'get'
  })
}

/**
 * 获取学校列表（公开接口）
 */
export function getSchoolList() {
  return request({
    url: '/backend/school/list',
    method: 'get'
  })
}

/**
 * 获取班级列表（公开接口）
 */
export function getClassList(schoolId) {
  return request({
    url: '/backend/class/list',
    method: 'get',
    params: { schoolId }
  })
}
