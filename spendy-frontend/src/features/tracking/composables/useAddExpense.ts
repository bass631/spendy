import { ref } from 'vue'
import { useTrackingStore } from '@/features/tracking/store/trackingStore'

export function useAddExpense() {
  const store = useTrackingStore()
  const loading = ref(false)
  const error = ref<string | null>(null)

  async function addExpense(categoryId: string, amount: number, description?: string) {
    loading.value = true
    error.value = null
    try {
      await store.addExpense(categoryId, amount, description)
    } catch (e: any) {
      error.value = e.response?.data?.detail || 'Ошибка при добавлении расхода'
    } finally {
      loading.value = false
    }
  }

  return { loading, error, addExpense }
}
