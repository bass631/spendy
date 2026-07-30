<script setup lang="ts">
defineProps<{
  label: string
  variant?: 'primary' | 'secondary' | 'danger'
  disabled?: boolean
}>()

defineEmits<{
  click: []
}>()
</script>

<template>
  <button
    class="base-btn"
    :class="[`base-btn--${variant ?? 'primary'}`]"
    :disabled="disabled"
    @click="$emit('click')"
  >
    {{ label }}
  </button>
</template>

<style scoped>
.base-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 12px 24px;
  min-height: var(--touch-target-min);
  border: none;
  border-radius: var(--radius-md);
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.2s, opacity 0.2s;
  width: 100%;
  -webkit-tap-highlight-color: transparent;
  user-select: none;
}

.base-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.base-btn--primary {
  background: var(--color-primary);
  color: #fff;
}

.base-btn--primary:hover:not(:disabled) {
  background: var(--color-primary-dark);
}

.base-btn--secondary {
  background: var(--color-surface);
  color: var(--color-text);
  border: 1px solid var(--color-border);
}

.base-btn--secondary:hover:not(:disabled) {
  background: var(--color-bg);
}

.base-btn--danger {
  background: var(--color-danger);
  color: #fff;
}
</style>
