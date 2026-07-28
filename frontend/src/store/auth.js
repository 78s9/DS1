import { defineStore } from 'pinia'
import { ref } from 'vue'
import request from '@/utils/request'

export const useAuthStore = defineStore('auth', () => {
  const user = ref(JSON.parse(localStorage.getItem('user') || 'null'))
  const token = ref(localStorage.getItem('token') || '')

  function setAuth(authData) {
    token.value = authData.token
    user.value = {
      username: authData.username,
      email: authData.email,
      role: authData.role,
      createdAt: authData.createdAt
    }
    localStorage.setItem('token', authData.token)
    localStorage.setItem('user', JSON.stringify(user.value))
  }

  function logout() {
    token.value = ''
    user.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }

  async function fetchUserInfo() {
    try {
      const res = await request.get('/user/me')
      if (res.code === 200) {
        user.value = res.data
        localStorage.setItem('user', JSON.stringify(res.data))
      }
    } catch {
      logout()
    }
  }

  return { user, token, setAuth, logout, fetchUserInfo }
})
