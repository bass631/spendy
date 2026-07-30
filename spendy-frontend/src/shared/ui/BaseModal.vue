<script setup lang="ts">
defineProps<{
  title: string
  show: boolean
}>()

defineEmits<{
  close: []
}>()
</script>

<template>
  <Teleport to="body">
    <div v-if="show" class="modal-overlay" @click.self="$emit('close')">
      <div class="modal-content">
        <div class="modal-handle" />
        <div class="modal-header">
          <h2 class="modal-title">{{ title }}</h2>
          <slot name="header-action">
            <button class="modal-close" @click="$emit('close')">&times;</button>
          </slot>
        </div>
        <div class="modal-body">
          <slot />
        </div>
      </div>
    </div>
  </Teleport>
</template>

<style scoped>
.modal-overlay {
  position: fixed;
  inset: 0;
  display: flex;
  align-items: flex-end;
  justify-content: center;
  background: var(--color-overlay);
  z-index: 1000;
  padding: 0;
}

.modal-content {
  background: var(--color-surface);
  border-radius: var(--radius-lg) var(--radius-lg) 0 0;
  width: 100%;
  max-width: 480px;
  max-height: 90dvh;
  overflow-y: auto;
  -webkit-overflow-scrolling: touch;
  box-shadow: var(--shadow-md);
  animation: slide-up 0.3s ease-out;
  padding-bottom: var(--safe-area-bottom);
}

.modal-handle {
  width: 36px;
  height: 5px;
  border-radius: 3px;
  background: var(--color-border);
  margin: 8px auto 0;
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 20px 0;
}

.modal-title {
  font-size: 17px;
  font-weight: 600;
  color: var(--color-text);
}

.modal-close {
  min-width: var(--touch-target-min);
  min-height: var(--touch-target-min);
  background: none;
  border: none;
  font-size: 22px;
  cursor: pointer;
  color: var(--color-text-secondary);
  line-height: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}

.modal-body {
  padding: 16px 20px 20px;
}

@keyframes slide-up {
  from {
    transform: translateY(100%);
  }
  to {
    transform: translateY(0);
  }
}

@media (min-width: 768px) {
  .modal-overlay {
    align-items: center;
    padding: 16px;
  }

  .modal-content {
    border-radius: var(--radius-lg);
    max-height: none;
    animation: none;
  }

  .modal-handle {
    display: none;
  }
}
</style>
