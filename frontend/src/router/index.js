import { createRouter, createWebHistory } from 'vue-router'

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

  // 校园门户路由
  {
    path: '/',
    name: 'Layout',
    component: () => import('@/views/Layout.vue'),
    meta: { requiresAuth: true },
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

  if (requiresAuth && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router
