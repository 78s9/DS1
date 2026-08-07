import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

// Create an Axios instance
const request = axios.create({
  baseURL: '/api',
  timeout: 15000
})

// Request interceptor — attach JWT token
request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// Response interceptor — handle errors globally
request.interceptors.response.use(
  (response) => {
    return response.data
  },
  (error) => {
    if (error.response) {
      const { status } = error.response
      const isLoginRequest = error.config.url.includes('/auth/login')

      switch (status) {
        case 401:
          // For login failures, let the Login page handle the error message
          if (isLoginRequest) {
            break
          }
          // For other 401s (expired token), redirect to login
          ElMessage.error('登录已过期，请重新登录')
          localStorage.removeItem('token')
          localStorage.removeItem('user')
          router.push('/login')
          break
        case 403:
          ElMessage.error('没有访问权限')
          break
        case 500:
          ElMessage.error('服务器内部错误')
          break
        default:
          ElMessage.error(error.response.data?.message || '请求失败')
      }
    } else {
      ElMessage.error('网络连接失败，请检查网络')
    }
    return Promise.reject(error)
  }
)

export default request
