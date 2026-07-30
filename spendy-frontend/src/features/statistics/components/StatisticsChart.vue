<script setup lang="ts">
import { computed } from 'vue'
import { Doughnut } from 'vue-chartjs'
import {
  Chart as ChartJS,
  ArcElement,
  Tooltip,
  Legend,
} from 'chart.js'
import type { CategoryStat } from '@/shared/api/statistics'

ChartJS.register(ArcElement, Tooltip, Legend)

const props = defineProps<{
  categoryStats: CategoryStat[]
}>()

const colors = [
  '#6b8c7b',
  '#8aa99b',
  '#b5c9b9',
  '#c4a88a',
  '#a8b5a0',
  '#7a9a8a',
  '#9db5a8',
  '#b5a89a',
]

const chartData = computed(() => ({
  labels: props.categoryStats.map((s) => `${s.categoryName} (${Math.round(s.total).toLocaleString('ru-RU')})`),
  datasets: [
    {
      data: props.categoryStats.map((s) => s.total),
      backgroundColor: colors,
      borderWidth: 0,
    },
  ],
}))

const chartOptions = {
  responsive: true,
  plugins: {
    legend: {
      display: false,
    },
  },
}
</script>

<template>
  <div class="chart-wrapper">
    <Doughnut :data="chartData" :options="chartOptions" />
    <div class="chart-legend">
      <div v-for="(s, i) in categoryStats" :key="s.categoryName" class="legend-item">
        <span class="legend-dot" :style="{ background: colors[i % colors.length] }" />
        <span class="legend-label">{{ s.categoryName }} ({{ Math.round(s.total).toLocaleString('ru-RU') }})</span>
      </div>
    </div>
  </div>
</template>

<style scoped>
.chart-wrapper {
  max-width: 260px;
  margin: 0 auto 16px;
}

.chart-legend {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 6px 12px;
  margin-top: 12px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 6px;
}

.legend-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  flex-shrink: 0;
}

.legend-label {
  font-size: 12px;
  color: var(--color-text-secondary);
  word-break: break-word;
}
</style>
