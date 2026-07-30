import { ref } from 'vue'
import { useAuthStore } from '@/features/auth/store/authStore'
import { useRouter } from 'vue-router'

export function useAuth() {
  const authStore = useAuthStore()
  const router = useRouter()
  const loading = ref(false)
  const error = ref<string | null>(null)

  async function handleLogin(username: string, password: string) {
    loading.value = true
    error.value = null
    try {
      await authStore.login(username, password)
      router.push('/tracking')
    } catch (e: any) {
      error.value = e.response?.data?.detail || 'Ошибка входа'
    } finally {
      loading.value = false
    }
  }

  return { loading, error, handleLogin }
}
