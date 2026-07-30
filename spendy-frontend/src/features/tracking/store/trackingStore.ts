import { defineStore } from 'pinia'
import { ref } from 'vue'
import * as categoriesApi from '@/shared/api/categories'
import * as expensesApi from '@/shared/api/expenses'
import type { CategoryResponse } from '@/shared/api/categories'

export const useTrackingStore = defineStore('tracking', () => {
  const categories = ref<CategoryResponse[]>([])
  const loading = ref(false)

  async function loadCategories() {
    loading.value = true
    try {
      const res = await categoriesApi.fetchCategories()
      categories.value = res.data
    } finally {
      loading.value = false
    }
  }

  async function addCategory(name: string): Promise<CategoryResponse> {
    const res = await categoriesApi.createCategory(name)
    categories.value.unshift(res.data)
    return res.data
  }

  async function renameCategory(id: string, name: string): Promise<CategoryResponse> {
    const res = await categoriesApi.renameCategory(id, name)
    const idx = categories.value.findIndex((c) => c.id === id)
    if (idx !== -1) categories.value[idx] = res.data
    return res.data
  }

  async function deleteCategory(id: string) {
    await categoriesApi.deleteCategory(id)
    categories.value = categories.value.filter((c) => c.id !== id)
  }

  async function addExpense(categoryId: string, amount: number, description?: string) {
    await expensesApi.addExpense({ categoryId, amount, description })
    await loadCategories()
  }

  return { categories, loading, loadCategories, addCategory, renameCategory, deleteCategory, addExpense }
})
