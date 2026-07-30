import client from './client'

export interface CategoryStat {
  categoryName: string
  total: number
  count: number
}

export interface StatisticsResponse {
  totalAmount: number
  categoryStats: CategoryStat[]
}

export interface StatisticsQuery {
  period: 'day' | 'week' | 'month' | 'year' | 'custom'
  from?: string
  to?: string
}

export function fetchStatistics(query: StatisticsQuery) {
  return client.get<StatisticsResponse>('/statistics', { params: query })
}

export function exportStatistics(query: StatisticsQuery) {
  return client.get('/statistics/export', {
    params: query,
    responseType: 'blob',
  })
}
