import axios from 'axios'
import axiosRetry from 'axios-retry'
import { useAuthStore } from '@/features/auth/store/authStore'
import router from '@/app/router'

const client = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
})

axiosRetry(client, {
  retries: 2,
  retryDelay: axiosRetry.exponentialDelay,
  retryCondition: (error) => {
    return axiosRetry.isNetworkOrIdempotentRequestError(error) && error.response?.status !== 409
  },
})

client.interceptors.request.use((config) => {
  const token = localStorage.getItem('accessToken')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

client.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true
      const refreshToken = localStorage.getItem('refreshToken')
      if (refreshToken) {
        try {
          const res = await axios.post(`${import.meta.env.VITE_API_BASE_URL || '/api'}/auth/refresh`, {
            refreshToken,
          })
          localStorage.setItem('accessToken', res.data.accessToken)
          originalRequest.headers.Authorization = `Bearer ${res.data.accessToken}`
          return client(originalRequest)
        } catch {
          useAuthStore().logout()
          router.push('/login')
        }
      } else {
        useAuthStore().logout()
        router.push('/login')
      }
    }
    if (error.response?.status === 409) {
      console.warn('[Conflict] Data was modified by another device', error.response.data)
    }
    return Promise.reject(error)
  },
)

export default client
