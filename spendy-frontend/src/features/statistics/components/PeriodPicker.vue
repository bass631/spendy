<script setup lang="ts">
import { ref, watch } from 'vue'

const props = defineProps<{
  modelValue: string
}>()

const emit = defineEmits<{
  'update:modelValue': [value: string]
}>()

const periods = [
  { value: 'day', label: 'День' },
  { value: 'week', label: 'Неделя' },
  { value: 'month', label: 'Месяц' },
  { value: 'year', label: 'Год' },
]

const customFrom = ref('')
const customTo = ref('')
const showCustomDatepicker = ref(false)

watch([customFrom, customTo], () => {
  if (customFrom.value && customTo.value) {
    emit('update:modelValue', `custom:${customFrom.value}:${customTo.value}`)
  }
})

function selectPeriod(value: string) {
  showCustomDatepicker.value = false
  emit('update:modelValue', value)
}

function toggleCustom() {
  showCustomDatepicker.value = !showCustomDatepicker.value
  if (showCustomDatepicker.value) {
    emit('update:modelValue', 'custom')
  }
}
</script>

<template>
  <div class="period-picker">
    <div class="period-tabs">
      <button
        v-for="p in periods"
        :key="p.value"
        class="period-tab"
        :class="{ 'period-tab--active': modelValue === p.value }"
        @click="selectPeriod(p.value)"
      >
        {{ p.label }}
      </button>
    </div>
    <button
      class="period-custom-btn"
      :class="{ 'period-custom-btn--active': showCustomDatepicker }"
      @click="toggleCustom"
    >
      Выбрать даты
    </button>
    <div v-if="showCustomDatepicker" class="period-custom">
      <input
        v-model="customFrom"
        type="date"
        class="period-input"
      />
      <span class="period-separator">—</span>
      <input
        v-model="customTo"
        type="date"
        class="period-input"
      />
    </div>
  </div>
</template>

<style scoped>
.period-picker {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.period-tabs {
  display: flex;
  gap: 6px;
  overflow-x: auto;
}

.period-tab {
  flex: 1;
  padding: 8px 16px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-sm);
  background: var(--color-surface);
  font-size: 13px;
  color: var(--color-text-secondary);
  cursor: pointer;
  transition: all 0.2s;
}

.period-tab--active {
  background: var(--color-primary);
  color: #fff;
  border-color: var(--color-primary);
}

.period-custom-btn {
  align-self: center;
  padding: 8px 20px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-sm);
  background: var(--color-surface);
  font-size: 13px;
  color: var(--color-text-secondary);
  cursor: pointer;
  transition: all 0.2s;
}

.period-custom-btn:hover {
  background: var(--color-bg);
}

.period-custom-btn--active {
  background: var(--color-primary);
  color: #fff;
  border-color: var(--color-primary);
}

.period-custom {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.period-input {
  flex: 1;
  max-width: 160px;
  padding: 8px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-sm);
  font-size: 13px;
}

.period-separator {
  color: var(--color-text-secondary);
}
</style>
