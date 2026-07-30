<script setup lang="ts">
import { ref, inject, watch } from 'vue'
import CategoryButton from './CategoryButton.vue'
import AddExpenseModal from './AddExpenseModal.vue'
import AddCategoryModal from './AddCategoryModal.vue'
import { useCategoriesSortedByFrequency } from '@/features/tracking/composables/useCategoriesSortedByFrequency'

const { categories, loading } = useCategoriesSortedByFrequency()

const showExpenseModal = ref(false)
const showCategoryModal = ref(false)
const selectedCategoryId = ref('')
const selectedCategoryName = ref('')

const addCategorySignal = inject('addCategorySignal', ref(0))
watch(addCategorySignal, () => {
  showCategoryModal.value = true
})

const categoryColors = [
  '#6b8c7b',
  '#8aa99b',
  '#b5c9b9',
  '#c4a88a',
  '#a8b5a0',
  '#7a9a8a',
  '#9db5a8',
  '#b5a89a',
  '#8899aa',
  '#aabb99',
]

function openExpenseModal(id: string, name: string) {
  selectedCategoryId.value = id
  selectedCategoryName.value = name
  showExpenseModal.value = true
}

function openCategoryModal() {
  showCategoryModal.value = true
}

function onCategoryCreated(catId: string, catName: string) {
  showCategoryModal.value = false
  openExpenseModal(catId, catName)
}

function onExpenseSaved() {
  showExpenseModal.value = false
}

function onExpenseClose() {
  showExpenseModal.value = false
}

function onCategoryClose() {
  showCategoryModal.value = false
}
</script>

<template>
  <div class="tracking-view">
    <div v-if="loading" class="tracking-loading">Загрузка...</div>
    <div v-else class="tracking-grid">
      <CategoryButton
        v-for="(cat, i) in categories"
        :key="cat.id"
        :name="cat.name"
        :color="categoryColors[i % categoryColors.length]"
        @click="openExpenseModal(cat.id, cat.name)"
      />
    </div>
    <AddExpenseModal
      :show="showExpenseModal"
      :category-id="selectedCategoryId"
      :category-name="selectedCategoryName"
      @saved="onExpenseSaved"
      @close="onExpenseClose"
    />
    <AddCategoryModal
      :show="showCategoryModal"
      @created="onCategoryCreated"
      @close="onCategoryClose"
    />
  </div>
</template>

<style scoped>
.tracking-view {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 100%;
  -webkit-overflow-scrolling: touch;
}

.tracking-loading {
  text-align: center;
  padding: 32px;
  color: var(--color-text-secondary);
}

.tracking-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
  width: 100%;
  max-width: 400px;
}
</style>
