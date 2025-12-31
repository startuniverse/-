import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login, getUserInfo } from '@/api/auth'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(null)
  const roles = ref([])
  const permissions = ref([])

  const isLogin = computed(() => !!token.value)
  const hasRole = (role) => roles.value.includes(role)
  const hasPermission = (permission) => permissions.value.includes(permission)

  // 登录
  async function loginAction(username, password) {
    try {
      const res = await login(username, password)
      token.value = res.token
      localStorage.setItem('token', res.token)

      // 登录接口返回的数据结构: { token: "...", userInfo: {...} }
      // 直接设置userInfo
      if (res && res.userInfo) {
        userInfo.value = res.userInfo
        roles.value = res.userInfo.roles || []
        permissions.value = res.userInfo.permissions || []
      }

      return res
    } catch (error) {
      throw error
    }
  }

  // 获取用户信息
  async function fetchUserInfo() {
    try {
      const res = await getUserInfo()
      // 后端返回的数据结构: { id, username, realName, phone, email, department, title, roles, permissions }
      // 直接设置为userInfo
      userInfo.value = res
      roles.value = res.roles || []
      permissions.value = res.permissions || []
      return res
    } catch (error) {
      throw error
    }
  }

  // 退出登录
  function logout() {
    token.value = ''
    userInfo.value = null
    roles.value = []
    permissions.value = []
    localStorage.removeItem('token')
  }

  // 初始化
  function init() {
    if (token.value) {
      fetchUserInfo().catch(() => {
        logout()
      })
    }
  }

  return {
    token,
    userInfo,
    roles,
    permissions,
    isLogin,
    hasRole,
    hasPermission,
    loginAction,
    fetchUserInfo,
    logout,
    init
  }
})
