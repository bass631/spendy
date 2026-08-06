import { ref } from 'vue'
import { fetchExpenses } from '@/shared/api/expenses'
import type { ExpenseResponse } from '@/shared/api/expenses'
import type { StatisticsQuery } from '@/shared/api/statistics'

export function useCategoryExpenses() {
  const expenses = ref<ExpenseResponse[]>([])
  const loading = ref(false)
  const error = ref<string | null>(null)

  async function load(categoryId: string, query: StatisticsQuery) {
    loading.value = true
    error.value = null
    const all: ExpenseResponse[] = []
    try {
      let page = 0
      let last = false
      while (!last) {
        const res = await fetchExpenses({
          categoryId,
          page,
          limit: 100,
          period: query.period,
          from: query.from,
          to: query.to,
        })
        all.push(...res.data.content)
        last = res.data.last
        page += 1
      }
      expenses.value = all
    } catch (e: any) {
      expenses.value = []
      error.value = e.response?.data?.detail || 'Ошибка загрузки расходов'
    } finally {
      loading.value = false
    }
  }

  function reset() {
    expenses.value = []
    loading.value = false
    error.value = null
  }

  return { expenses, loading, error, load, reset }
}
