import { defineStore } from 'pinia'
import { ref } from 'vue'
import * as statisticsApi from '@/shared/api/statistics'
import type { StatisticsResponse, StatisticsQuery } from '@/shared/api/statistics'

export const useStatisticsStore = defineStore('statistics', () => {
  const data = ref<StatisticsResponse | null>(null)
  const loading = ref(false)
  const currentPeriod = ref<StatisticsQuery['period']>('month')

  async function loadStatistics(query: StatisticsQuery) {
    loading.value = true
    currentPeriod.value = query.period
    try {
      const res = await statisticsApi.fetchStatistics(query)
      data.value = res.data
    } finally {
      loading.value = false
    }
  }

  return { data, loading, currentPeriod, loadStatistics }
})
