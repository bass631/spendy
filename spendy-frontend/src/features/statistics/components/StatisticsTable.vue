<script setup lang="ts">
import type { CategoryStat } from '@/shared/api/statistics'

defineProps<{
  categoryStats: CategoryStat[]
  totalAmount: number
}>()
</script>

<template>
  <div class="stat-table-wrapper">
    <table class="stat-table">
      <thead>
        <tr>
          <th class="stat-th">Категория</th>
          <th class="stat-th stat-th--right">Сумма</th>
          <th class="stat-th stat-th--right">Количество</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="stat in categoryStats" :key="stat.categoryName" class="stat-row">
          <td class="stat-td">{{ stat.categoryName }}</td>
          <td class="stat-td stat-td--right">{{ stat.total.toFixed(2) }}</td>
          <td class="stat-td stat-td--right">{{ stat.count }}</td>
        </tr>
      </tbody>
      <tfoot>
        <tr class="stat-row stat-row--total">
          <td class="stat-td">Итого</td>
          <td class="stat-td stat-td--right">{{ totalAmount.toFixed(2) }}</td>
          <td class="stat-td"></td>
        </tr>
      </tfoot>
    </table>
  </div>
</template>

<style scoped>
.stat-table-wrapper {
  overflow-x: auto;
}

.stat-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
}

.stat-th {
  text-align: left;
  padding: 10px 8px;
  border-bottom: 2px solid var(--color-border);
  color: var(--color-text-secondary);
  font-weight: 600;
}

.stat-th--right {
  text-align: right;
}

.stat-td {
  padding: 10px 8px;
  border-bottom: 1px solid var(--color-border);
}

.stat-td--right {
  text-align: right;
}

.stat-row--total .stat-td {
  font-weight: 600;
  border-bottom: none;
}
</style>
