<script setup lang="ts">
import { ref } from 'vue'
import BaseButton from '@/shared/ui/BaseButton.vue'
import { exportStatistics } from '@/shared/api/statistics'
import type { StatisticsQuery } from '@/shared/api/statistics'

const props = defineProps<{
  query: StatisticsQuery
}>()

const loading = ref(false)

async function handleExport() {
  loading.value = true
  try {
    const res = await exportStatistics(props.query)
    const url = URL.createObjectURL(new Blob([res.data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' }))
    const a = document.createElement('a')
    a.href = url
    a.download = 'statistics.xlsx'
    a.click()
    URL.revokeObjectURL(url)
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <BaseButton
    label="Выгрузить в XLSX"
    variant="secondary"
    :disabled="loading"
    @click="handleExport"
  />
</template>
