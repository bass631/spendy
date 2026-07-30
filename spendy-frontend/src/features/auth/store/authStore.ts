import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import * as authApi from '@/shared/api/auth'

export const useAuthStore = defineStore('auth', () => {
  const accessToken = ref(localStorage.getItem('accessToken'))
  const refreshToken = ref(localStorage.getItem('refreshToken'))

  const isAuthenticated = computed(() => !!accessToken.value)

  async function login(username: string, password: string) {
    const res = await authApi.login({ username, password })
    setTokens(res.data.token, '')
  }

  function setTokens(access: string, refresh: string) {
    accessToken.value = access
    refreshToken.value = refresh
    localStorage.setItem('accessToken', access)
    localStorage.setItem('refreshToken', refresh)
  }

  function logout() {
    const rt = refreshToken.value
    accessToken.value = null
    refreshToken.value = null
    localStorage.removeItem('accessToken')
    localStorage.removeItem('refreshToken')
    if (rt) {
      authApi.logout(rt).catch(() => {})
    }
  }

  return { accessToken, refreshToken, isAuthenticated, login, logout, setTokens }
})
