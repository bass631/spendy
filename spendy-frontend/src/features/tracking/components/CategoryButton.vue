<script setup lang="ts">
defineProps<{
  name: string
  color?: string
}>()

defineEmits<{
  click: []
}>()
</script>

<template>
  <button
    class="category-btn"
    :style="{ background: color || 'var(--color-primary)' }"
    @click="$emit('click')"
  >
    <span class="category-name">{{ name }}</span>
  </button>
</template>

<style scoped>
.category-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  padding: 18px 16px;
  border: none;
  border-radius: var(--radius-md);
  font-size: 15px;
  font-weight: 600;
  color: #fff;
  cursor: pointer;
  transition: filter 0.15s, box-shadow 0.15s, transform 0.1s;
  box-shadow: var(--shadow-sm);
  min-height: 60px;
  -webkit-tap-highlight-color: transparent;
  touch-action: manipulation;
}

.category-btn:hover {
  filter: brightness(1.1);
  box-shadow: var(--shadow-md);
}

.category-btn:active {
  transform: scale(0.96);
}

.category-name {
  line-height: 1.3;
  text-align: center;
  word-break: break-word;
}
</style>
