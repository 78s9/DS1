import { createRouter, createWebHashHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { guest: true, title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { guest: true, title: '注册' }
  },
  {
    path: '/dashboard',
    component: () => import('@/views/Dashboard.vue'),
    meta: { requiresAuth: true, title: '控制台' },
    children: [
      {
        path: '',
        name: 'DashboardHome',
        component: () => import('@/views/DashboardHome.vue'),
        meta: { title: '首页' }
      },
      {
        path: 'users',
        name: 'UserManagement',
        component: () => import('@/views/UserManagement.vue'),
        meta: { title: '用户管理' }
      },
      {
        path: 'settings',
        name: 'SystemSettings',
        component: () => import('@/views/SystemSettings.vue'),
        meta: { title: '系统设置' }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/NotFound.vue'),
    meta: { title: '404' }
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

// Navigation guard: check JWT auth
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')

  if (to.meta.requiresAuth && !token) {
    // Not authenticated — redirect to login
    next({ name: 'Login' })
  } else if (to.meta.guest && token) {
    // Already authenticated — redirect to dashboard
    next({ name: 'DashboardHome' })
  } else {
    next()
  }
})

// After each route: update document title
router.afterEach((to) => {
  const title = to.meta.title
  document.title = title ? `${title} - DS1 管理系统` : 'DS1 管理系统'
})

export default router
