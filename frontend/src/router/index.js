import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'

const routes = [
  // 公共路由
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { title: '注册' }
  },
  {
    path: '/teacher-register',
    name: 'TeacherRegister',
    component: () => import('@/views/TeacherRegister.vue'),
    meta: { title: '教师注册' }
  },

  // 校园门户路由 - 普通用户（学生/家长）和管理员
  {
    path: '/',
    name: 'Layout',
    component: () => import('@/views/Layout.vue'),
    meta: { requiresAuth: true, roles: ['USER', 'STUDENT', 'PARENT', 'ADMIN', 'TEACHER'] },
    children: [
      {
        path: '',
        name: 'Dashboard',
        component: () => import('@/views/campus/Dashboard.vue'),
        meta: { title: '仪表盘' }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/campus/Profile.vue'),
        meta: { title: '个人信息' }
      },
      {
        path: 'class',
        name: 'ClassInfo',
        component: () => import('@/views/campus/ClassInfo.vue'),
        meta: { title: '班级信息' }
      },
      {
        path: 'timetable',
        name: 'Timetable',
        component: () => import('@/views/campus/Timetable.vue'),
        meta: { title: '课程表' }
      },
      {
        path: 'grades',
        name: 'Grades',
        component: () => import('@/views/campus/Grades.vue'),
        meta: { title: '成绩查询' }
      },
      {
        path: 'announcements',
        name: 'Announcements',
        component: () => import('@/views/campus/Announcements.vue'),
        meta: { title: '通知公告' }
      }
    ]
  },

  // 教师门户路由
  {
    path: '/teacher',
    name: 'TeacherLayout',
    component: () => import('@/views/teacher/Layout.vue'),
    meta: { requiresAuth: true, roles: ['TEACHER'] },
    children: [
      {
        path: '',
        name: 'TeacherDashboard',
        component: () => import('@/views/teacher/Dashboard.vue'),
        meta: { title: '教学仪表盘' }
      },
      {
        path: 'profile',
        name: 'TeacherProfile',
        component: () => import('@/views/teacher/Profile.vue'),
        meta: { title: '个人信息' }
      },
      {
        path: 'my-students',
        name: 'MyStudents',
        component: () => import('@/views/teacher/MyStudents.vue'),
        meta: { title: '我的学生' }
      },
      {
        path: 'grade-management',
        name: 'GradeManagement',
        component: () => import('@/views/teacher/GradeManagement.vue'),
        meta: { title: '成绩管理' }
      },
      {
        path: 'assignment',
        name: 'TeacherAssignment',
        component: () => import('@/views/teacher/Assignment.vue'),
        meta: { title: '作业布置' }
      },
      {
        path: 'timetable',
        name: 'TeacherTimetable',
        component: () => import('@/views/campus/Timetable.vue'),
        meta: { title: '我的课表' }
      },
      {
        path: 'announcements',
        name: 'TeacherAnnouncements',
        component: () => import('@/views/teacher/Announcement.vue'),
        meta: { title: '发布通知' }
      },
      {
        path: 'class-management',
        name: 'ClassManagement',
        component: () => import('@/views/teacher/MyStudents.vue'),
        meta: { title: '班级管理' }
      }
    ]
  },

  // 后台管理路由
  {
    path: '/admin',
    name: 'AdminLayout',
    component: () => import('@/views/admin/Layout.vue'),
    meta: { requiresAuth: true, roles: ['ADMIN'] },
    children: [
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: () => import('@/views/admin/Dashboard.vue'),
        meta: { title: '管理仪表盘' }
      },
      {
        path: 'students',
        name: 'StudentManagement',
        component: () => import('@/views/admin/StudentManagement.vue'),
        meta: { title: '学生管理' }
      },
      {
        path: 'documents',
        name: 'DocumentManagement',
        component: () => import('@/views/admin/DocumentManagement.vue'),
        meta: { title: '文档管理' }
      },
      {
        path: 'resources',
        name: 'ResourceManagement',
        component: () => import('@/views/admin/ResourceManagement.vue'),
        meta: { title: '资源管理' }
      },
      {
        path: 'analytics',
        name: 'Analytics',
        component: () => import('@/views/admin/Analytics.vue'),
        meta: { title: '数据分析' }
      },
      {
        path: 'assets',
        name: 'AssetManagement',
        component: () => import('@/views/admin/AssetManagement.vue'),
        meta: { title: '资产管理' }
      }
    ]
  },

  // 404页面
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/NotFound.vue'),
    meta: { title: '页面未找到' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  if (to.meta.title) {
    document.title = `${to.meta.title} - 教育局平台`
  }

  // 检查是否需要认证
  const requiresAuth = to.meta.requiresAuth
  const token = localStorage.getItem('token')

  // 如果需要认证但没有token，跳转到登录页
  if (requiresAuth && !token) {
    next('/login')
    return
  }

  // 如果有token且需要角色权限检查
  if (token && to.meta.roles) {
    // 从localStorage获取用户信息
    const userInfoStr = localStorage.getItem('userInfo')
    if (userInfoStr) {
      try {
        const userInfo = JSON.parse(userInfoStr)
        const userRoles = userInfo.roles || []
        const requiredRoles = to.meta.roles

        console.log('=== 路由守卫调试 ===')
        console.log('目标路径:', to.path)
        console.log('用户角色:', userRoles)
        console.log('要求角色:', requiredRoles)

        // 检查用户是否有访问该路由的权限
        const hasPermission = requiredRoles.some(role => userRoles.includes(role))
        console.log('是否有权限:', hasPermission)

        if (!hasPermission) {
          // 没有权限，根据角色跳转到对应的默认页面
          // 防止无限循环：检查当前路径，避免重复跳转
          const currentPath = to.path

          if (userRoles.includes('TEACHER') && !currentPath.startsWith('/teacher')) {
            ElMessage.warning('您没有访问该页面的权限，正在跳转到教师工作台...')
            next('/teacher')
          } else if (userRoles.includes('ADMIN') && !currentPath.startsWith('/admin')) {
            ElMessage.warning('您没有访问该页面的权限，正在跳转到管理后台...')
            next('/admin/dashboard')
          } else if (!currentPath.startsWith('/campus') && currentPath !== '/') {
            // 如果用户没有角色，或者角色不在上述列表中，跳转到校园门户首页
            ElMessage.warning('您没有访问该页面的权限，正在跳转到首页...')
            next('/')
          } else {
            // 如果已经在首页但没有权限，说明用户没有角色
            // 允许访问，但显示警告
            ElMessage.warning('您的账号尚未分配角色，请联系管理员')
            next()
          }
          return
        }
      } catch (e) {
        console.error('解析用户信息失败:', e)
      }
    }
  }

  next()
})

export default router
