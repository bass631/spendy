import { useStatisticsStore } from '@/features/statistics/store/statisticsStore'
import { storeToRefs } from 'pinia'
import type { StatisticsQuery } from '@/shared/api/statistics'

export function useStatisticsQuery() {
  const store = useStatisticsStore()
  const { data, loading } = storeToRefs(store)

  function load(query: StatisticsQuery) {
    store.loadStatistics(query)
  }

  function refresh() {
    store.loadStatistics({ period: store.currentPeriod ?? 'month' })
  }

  return { data, loading, load, refresh }
}
