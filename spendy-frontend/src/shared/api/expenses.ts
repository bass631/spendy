import client from './client'

export interface AddExpenseRequest {
  categoryId: string
  amount: number
  description?: string
}

export interface UpdateExpenseRequest {
  amount: number
  description?: string
}

export interface ExpenseResponse {
  id: string
  categoryId: string
  categoryName: string
  amount: number
  description: string | null
  createdBy: string
  createdAt: string
}

export function addExpense(data: AddExpenseRequest) {
  return client.post<ExpenseResponse>('/expenses', data)
}

export interface FetchExpensesParams {
  categoryId?: string
  page?: number
  limit?: number
  period?: string
  from?: string
  to?: string
}

export function fetchExpenses(params: FetchExpensesParams) {
  return client.get<{ content: ExpenseResponse[]; totalElements: number; totalPages: number; number: number; last: boolean }>('/expenses', { params })
}

export function updateExpense(id: string, data: UpdateExpenseRequest) {
  return client.put<ExpenseResponse>(`/expenses/${id}`, data)
}

export function deleteExpense(id: string) {
  return client.delete(`/expenses/${id}`)
}
