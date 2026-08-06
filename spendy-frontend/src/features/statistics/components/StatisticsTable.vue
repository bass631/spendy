<script setup lang="ts">
import { ref, watch } from 'vue'
import type { CategoryStat } from '@/shared/api/statistics'
import type { StatisticsQuery } from '@/shared/api/statistics'
import { useCategoryExpenses } from '@/features/statistics/composables/useCategoryExpenses'

const props = defineProps<{
  categoryStats: CategoryStat[]
  totalAmount: number
  query: StatisticsQuery
}>()

const expandedId = ref<string | null>(null)
const { expenses, loading, error, load, reset } = useCategoryExpenses()

async function toggleRow(stat: CategoryStat) {
  if (expandedId.value === stat.categoryId) {
    expandedId.value = null
    reset()
    return
  }
  expandedId.value = stat.categoryId
  await load(stat.categoryId, props.query)
}

watch(
  () => props.query,
  async () => {
    if (expandedId.value) {
      await load(expandedId.value, props.query)
    }
  }
)

function formatDate(iso: string): string {
  const d = new Date(iso)
  const day = d.getDate()
  const months = ['янв', 'фев', 'мар', 'апр', 'май', 'июн', 'июл', 'авг', 'сен', 'окт', 'ноя', 'дек']
  const month = months[d.getMonth()]
  const year = d.getFullYear()
  const hours = d.getHours().toString().padStart(2, '0')
  const minutes = d.getMinutes().toString().padStart(2, '0')
  return `${day} ${month} ${year} ${hours}:${minutes}`
}
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
        <template v-for="stat in categoryStats" :key="stat.categoryId">
          <tr
            class="stat-row"
            :class="{ 'stat-row--expanded': expandedId === stat.categoryId }"
            @click="toggleRow(stat)"
          >
            <td class="stat-td">
              <span class="stat-category">
                <svg
                  class="stat-chevron"
                  :class="{ 'stat-chevron--open': expandedId === stat.categoryId }"
                  width="12"
                  height="12"
                  viewBox="0 0 12 12"
                  fill="none"
                >
                  <path d="M4 2l4 4-4 4" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
                </svg>
                {{ stat.categoryName }}
              </span>
            </td>
            <td class="stat-td stat-td--right">{{ stat.total.toFixed(2) }}</td>
            <td class="stat-td stat-td--right">{{ stat.count }}</td>
          </tr>
          <tr v-if="expandedId === stat.categoryId" class="stat-row--details">
            <td colspan="3" class="stat-td stat-td--details">
              <div v-if="loading" class="stat-details-loading">Загрузка...</div>
              <div v-else-if="error" class="stat-details-error">{{ error }}</div>
              <div v-else-if="expenses.length === 0" class="stat-details-empty">Нет расходов</div>
              <ul v-else class="stat-details-list">
                <li v-for="exp in expenses" :key="exp.id" class="stat-detail-item">
                  <span class="stat-detail-date">{{ formatDate(exp.createdAt) }}</span>
                  <div class="stat-detail-main">
                    <span v-if="exp.description" class="stat-detail-desc">{{ exp.description }}</span>
                    <span class="stat-detail-author">{{ exp.createdBy }}</span>
                  </div>
                  <span class="stat-detail-amount">{{ exp.amount.toFixed(2) }}</span>
                </li>
              </ul>
            </td>
          </tr>
        </template>
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

.stat-row {
  cursor: pointer;
  transition: background 0.2s;
  -webkit-tap-highlight-color: transparent;
  touch-action: manipulation;
}

.stat-row:hover {
  background: var(--color-surface);
}

.stat-row--expanded {
  background: var(--color-surface);
}

.stat-category {
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.stat-chevron {
  flex-shrink: 0;
  transition: transform 0.2s;
  color: var(--color-text-secondary);
}

.stat-chevron--open {
  transform: rotate(90deg);
}

.stat-row--total {
  cursor: default;
}

.stat-row--total .stat-td {
  font-weight: 600;
  border-bottom: none;
}

.stat-td--details {
  background: var(--color-surface);
  padding: 0;
}

.stat-details-loading,
.stat-details-empty,
.stat-details-error {
  font-size: 13px;
  color: var(--color-text-secondary);
  text-align: center;
  padding: 16px 8px;
}

.stat-details-error {
  color: var(--color-danger);
}

.stat-details-list {
  list-style: none;
  margin: 0;
  padding: 4px 0;
  display: flex;
  flex-direction: column;
}

.stat-detail-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  padding: 8px 12px;
  border-top: 1px solid var(--color-border);
}

.stat-detail-item:first-child {
  border-top: none;
}

.stat-detail-date {
  font-size: 12px;
  color: var(--color-text-secondary);
  flex-shrink: 0;
}

.stat-detail-main {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.stat-detail-desc {
  font-size: 13px;
  color: var(--color-text);
  word-break: break-word;
}

.stat-detail-author {
  font-size: 12px;
  color: var(--color-text-secondary);
}

.stat-detail-amount {
  font-size: 14px;
  font-weight: 600;
  color: var(--color-text);
  flex-shrink: 0;
}
</style>
