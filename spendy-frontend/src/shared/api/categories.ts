import client from './client'

export interface CategoryResponse {
  id: string
  name: string
  usageCount: number
}

export function fetchCategories() {
  return client.get<CategoryResponse[]>('/categories')
}

export function createCategory(name: string) {
  return client.post<CategoryResponse>('/categories', { name })
}

export function renameCategory(id: string, name: string) {
  return client.put<CategoryResponse>(`/categories/${id}`, { name })
}

export function deleteCategory(id: string) {
  return client.delete(`/categories/${id}`)
}
