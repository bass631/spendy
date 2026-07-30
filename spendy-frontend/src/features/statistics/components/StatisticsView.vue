<script setup lang="ts">
import { ref, computed, onMounted, inject, watch } from 'vue'
import PeriodPicker from './PeriodPicker.vue'
import StatisticsChart from './StatisticsChart.vue'
import StatisticsTable from './StatisticsTable.vue'
import { exportStatistics } from '@/shared/api/statistics'
import { useStatisticsQuery } from '@/features/statistics/composables/useStatisticsQuery'
import { useStatisticsStore } from '@/features/statistics/store/statisticsStore'

const store = useStatisticsStore()
const { data, loading, load, refresh } = useStatisticsQuery()
const selectedPeriod = ref<string>(store.currentPeriod || 'month')
const showChart = ref(true)

onMounted(() => {
  refresh()
})

const currentQuery = computed(() => {
  if (selectedPeriod.value.startsWith('custom:')) {
    const [, from, to] = selectedPeriod.value.split(':')
    return { period: 'custom' as const, from, to }
  }
  return { period: selectedPeriod.value as 'day' | 'week' | 'month' | 'year' }
})

const exportLoading = ref(false)
const exportSignal = inject('exportSignal', ref(0))
watch(exportSignal, async () => {
  if (exportLoading.value || !data.value) return
  exportLoading.value = true
  try {
    const res = await exportStatistics(currentQuery.value)
    const url = URL.createObjectURL(new Blob([res.data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' }))
    const a = document.createElement('a')
    a.href = url
    a.download = 'statistics.xlsx'
    a.click()
    URL.revokeObjectURL(url)
  } finally {
    exportLoading.value = false
  }
})

function onPeriodChange(value: string) {
  selectedPeriod.value = value
  if (value === 'custom') return
  const q = currentQuery.value
  load(q)
}
</script>

<template>
  <div class="statistics-view">
    <div class="statistics-sticky">
      <PeriodPicker :model-value="selectedPeriod" @update:model-value="onPeriodChange" />

      <div class="statistics-mode-toggle">
        <button
          class="mode-btn"
          :class="{ 'mode-btn--active': showChart }"
          @click="showChart = true"
        >
          График
        </button>
        <button
          class="mode-btn"
          :class="{ 'mode-btn--active': !showChart }"
          @click="showChart = false"
        >
          Таблица
        </button>
      </div>
    </div>

    <div v-if="loading" class="statistics-loading">Загрузка...</div>
    <template v-else-if="data">
      <p class="statistics-total">Общая сумма: {{ data.totalAmount.toFixed(2) }}</p>
      <StatisticsChart v-if="showChart" :category-stats="data.categoryStats" />
      <StatisticsTable v-else :category-stats="data.categoryStats" :total-amount="data.totalAmount" />
    </template>
  </div>
</template>

<style scoped>
.statistics-view {
  display: flex;
  flex-direction: column;
  gap: 12px;
  -webkit-overflow-scrolling: touch;
}

.statistics-sticky {
  position: sticky;
  top: 0;
  z-index: 10;
  background: var(--color-bg);
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.statistics-mode-toggle {
  display: flex;
  gap: 6px;
  margin-bottom: 16px;
}

.mode-btn {
  flex: 1;
  padding: 8px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-sm);
  background: var(--color-surface);
  font-size: 13px;
  color: var(--color-text-secondary);
  cursor: pointer;
  transition: all 0.2s;
}

.mode-btn--active {
  background: var(--color-primary);
  color: #fff;
  border-color: var(--color-primary);
}

.statistics-loading {
  text-align: center;
  padding: 32px;
  color: var(--color-text-secondary);
}

.statistics-total {
  font-size: 18px;
  font-weight: 600;
  text-align: center;
  margin-bottom: 16px;
  color: var(--color-text);
}
</style>
