<script setup lang="ts">
import { ref } from 'vue'
import BaseModal from '@/shared/ui/BaseModal.vue'
import BaseButton from '@/shared/ui/BaseButton.vue'
import { useTrackingStore } from '@/features/tracking/store/trackingStore'

const props = defineProps<{
  show: boolean
}>()

const emit = defineEmits<{
  close: []
  created: [categoryId: string, categoryName: string]
}>()

const store = useTrackingStore()
const name = ref('')
const loading = ref(false)
const error = ref<string | null>(null)

async function handleCreate() {
  if (!name.value.trim()) return
  loading.value = true
  error.value = null
  try {
    const cat = await store.addCategory(name.value.trim())
    name.value = ''
    // ДЗ: переходим в модальное окно добавления расходов
    emit('created', cat.id, cat.name)
  } catch (e: any) {
    error.value = e.response?.data?.detail || 'Ошибка при создании категории'
  } finally {
    loading.value = false
  }
}

function handleClose() {
  name.value = ''
  emit('close')
}
</script>

<template>
  <BaseModal title="Добавление новой категории" :show="show" @close="handleClose">
    <div class="add-category-body">
      <input
        v-model="name"
        type="text"
        placeholder="Название новой категории"
        class="add-category-input"
      />
      <p v-if="error" class="add-category-error">{{ error }}</p>
      <div class="add-category-actions">
        <BaseButton label="Создать" :disabled="loading || !name.trim()" @click="handleCreate" />
        <BaseButton label="Отмена" variant="secondary" @click="handleClose" />
      </div>
    </div>
  </BaseModal>
</template>

<style scoped>
.add-category-body {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.add-category-input {
  width: 100%;
  padding: 12px 14px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  font-size: 15px;
  outline: none;
}

.add-category-input:focus {
  border-color: var(--color-primary);
}

.add-category-error {
  color: var(--color-danger);
  font-size: 13px;
}

.add-category-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
</style>
