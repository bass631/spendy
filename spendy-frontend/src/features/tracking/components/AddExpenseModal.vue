<script setup lang="ts">
import { ref, watch, nextTick } from 'vue'
import BaseModal from '@/shared/ui/BaseModal.vue'
import BaseButton from '@/shared/ui/BaseButton.vue'
import { useAddExpense } from '@/features/tracking/composables/useAddExpense'
import { useTrackingStore } from '@/features/tracking/store/trackingStore'
import { fetchExpenses, updateExpense, deleteExpense } from '@/shared/api/expenses'
import type { ExpenseResponse } from '@/shared/api/expenses'

const props = defineProps<{
  show: boolean
  categoryName: string
  categoryId: string
}>()

const emit = defineEmits<{
  close: []
  saved: []
}>()

const store = useTrackingStore()
const { loading, error, addExpense } = useAddExpense()
const amount = ref('')
const description = ref('')

const localCategoryName = ref(props.categoryName)

watch(() => props.categoryName, (val) => {
  localCategoryName.value = val
})

const showSettings = ref(false)
const showRenameInput = ref(false)
const showDeleteConfirm = ref(false)
const newName = ref('')
const renameLoading = ref(false)
const deleteLoading = ref(false)
const actionError = ref<string | null>(null)
const deleteConfirmName = ref('')

const expenses = ref<ExpenseResponse[]>([])
const expensesLoading = ref(false)
const expensesExpanded = ref(false)
const expensesLoaded = ref(false)
const expensesPage = ref(0)
const expensesHasMore = ref(true)
const expensesLoadingMore = ref(false)

async function toggleExpenses() {
  expensesExpanded.value = !expensesExpanded.value
  if (expensesExpanded.value && !expensesLoaded.value) {
    await loadExpenses()
    expensesLoaded.value = true
  }
}

watch(() => props.show, (val) => {
  if (!val) {
    expensesExpanded.value = false
    expensesLoaded.value = false
    editExpenseId.value = null
    deleteConfirmId.value = null
    actionErrorMsg.value = null
  }
})

async function loadExpenses() {
  expensesLoading.value = true
  expensesPage.value = 0
  expensesHasMore.value = true
  try {
    const res = await fetchExpenses({ categoryId: props.categoryId, page: 0, limit: 10 })
    expenses.value = res.data.content
    expensesHasMore.value = !res.data.last
  } finally {
    expensesLoading.value = false
    setupObserver()
  }
}

async function loadMoreExpenses() {
  if (expensesLoadingMore.value || !expensesHasMore.value) return
  expensesLoadingMore.value = true
  try {
    const nextPage = expensesPage.value + 1
    const res = await fetchExpenses({ categoryId: props.categoryId, page: nextPage, limit: 10 })
    expenses.value = [...expenses.value, ...res.data.content]
    expensesPage.value = nextPage
    expensesHasMore.value = !res.data.last
  } finally {
    expensesLoadingMore.value = false
    setupObserver()
  }
}

const sentinelRef = ref<HTMLElement | null>(null)
let observer: IntersectionObserver | null = null

function setupObserver() {
  observer?.disconnect()
  nextTick(() => {
    if (sentinelRef.value) {
      observer = new IntersectionObserver(
        (entries) => {
          if (entries[0].isIntersecting) {
            loadMoreExpenses()
          }
        },
        { threshold: 0.1 }
      )
      observer.observe(sentinelRef.value)
    }
  })
}

watch(() => props.show, (val) => {
  if (!val) {
    expensesExpanded.value = false
    expensesLoaded.value = false
    editExpenseId.value = null
    deleteConfirmId.value = null
    actionErrorMsg.value = null
  }
})

const editExpenseId = ref<string | null>(null)
const editAmount = ref('')
const editDescription = ref('')
const deleteConfirmId = ref<string | null>(null)
const actionLoading = ref(false)
const actionErrorMsg = ref<string | null>(null)

async function handleSave() {
  const num = parseFloat(amount.value)
  if (isNaN(num) || num <= 0) return
  await addExpense(props.categoryId, num, description.value || undefined)
  emit('saved')
  amount.value = ''
  description.value = ''
  await loadExpenses()
}

function handleClose() {
  amount.value = ''
  description.value = ''
  showSettings.value = false
  showRenameInput.value = false
  showDeleteConfirm.value = false
  newName.value = ''
  actionError.value = null
  editExpenseId.value = null
  deleteConfirmId.value = null
  actionErrorMsg.value = null
  emit('close')
}

function backToMenu() {
  showRenameInput.value = false
  showDeleteConfirm.value = false
  actionError.value = null
  deleteConfirmName.value = ''
}

function toggleSettings() {
  showSettings.value = !showSettings.value
  backToMenu()
}

function openRename() {
  newName.value = localCategoryName.value
  showRenameInput.value = true
  showDeleteConfirm.value = false
  actionError.value = null
}

function openDeleteConfirm() {
  showDeleteConfirm.value = true
  showRenameInput.value = false
  actionError.value = null
  deleteConfirmName.value = ''
}

async function handleRename() {
  if (!newName.value.trim()) return
  renameLoading.value = true
  actionError.value = null
  try {
    const updated = await store.renameCategory(props.categoryId, newName.value.trim())
    localCategoryName.value = updated.name
    showSettings.value = false
    showRenameInput.value = false
  } catch (e: any) {
    actionError.value = e.response?.data?.detail || 'Ошибка при переименовании'
  } finally {
    renameLoading.value = false
  }
}

async function handleDelete() {
  deleteLoading.value = true
  actionError.value = null
  try {
    await store.deleteCategory(props.categoryId)
    showSettings.value = false
    showDeleteConfirm.value = false
    emit('close')
  } catch (e: any) {
    actionError.value = e.response?.data?.detail || 'Ошибка при удалении'
  } finally {
    deleteLoading.value = false
  }
}

function startEdit(exp: ExpenseResponse) {
  editExpenseId.value = exp.id
  editAmount.value = exp.amount.toString()
  editDescription.value = exp.description || ''
  actionErrorMsg.value = null
}

function cancelEdit() {
  editExpenseId.value = null
  editAmount.value = ''
  editDescription.value = ''
  actionErrorMsg.value = null
}

async function handleEditSave(expId: string) {
  const num = parseFloat(editAmount.value)
  if (isNaN(num) || num <= 0) return
  actionLoading.value = true
  actionErrorMsg.value = null
  try {
    await updateExpense(expId, { amount: num, description: editDescription.value || undefined })
    editExpenseId.value = null
    editAmount.value = ''
    editDescription.value = ''
    await loadExpenses()
  } catch (e: any) {
    actionErrorMsg.value = e.response?.data?.detail || 'Ошибка при сохранении'
  } finally {
    actionLoading.value = false
  }
}

function confirmDelete(expId: string) {
  deleteConfirmId.value = expId
  actionErrorMsg.value = null
}

function cancelDeleteConfirm() {
  deleteConfirmId.value = null
  actionErrorMsg.value = null
}

async function handleDeleteExpense(expId: string) {
  actionLoading.value = true
  actionErrorMsg.value = null
  try {
    await deleteExpense(expId)
    deleteConfirmId.value = null
    await loadExpenses()
  } catch (e: any) {
    actionErrorMsg.value = e.response?.data?.detail || 'Ошибка при удалении'
  } finally {
    actionLoading.value = false
  }
}

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
  <BaseModal title="Добавление расходов" :show="show" @close="handleClose">
    <template #header-action>
      <div class="settings-container">
        <button class="settings-btn" @click="toggleSettings" title="Настройки категории">
          <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
            <path d="M8.604 3.598c.355-1.463 2.437-1.463 2.792 0a1.437 1.437 0 002.144.888c1.286-.783 2.758.689 1.975 1.975a1.437 1.437 0 00.888 2.144c1.463.355 1.463 2.437 0 2.792a1.437 1.437 0 00-.888 2.144c.783 1.286-.689 2.758-1.975 1.975a1.437 1.437 0 00-2.144.888c-.355 1.463-2.437 1.463-2.792 0a1.437 1.437 0 00-2.144-.888c-1.286.783-2.758-.689-1.975-1.975a1.437 1.437 0 00-.888-2.144c-1.463-.355-1.463-2.437 0-2.792a1.437 1.437 0 00.888-2.144c-.783-1.286.689-2.758 1.975-1.975.83.507 1.913.058 2.144-.888z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
            <circle cx="10" cy="10" r="2.5" stroke="currentColor" stroke-width="1.5"/>
          </svg>
        </button>
        <div v-if="showSettings" class="settings-dropdown" @click.stop>
          <template v-if="!showRenameInput && !showDeleteConfirm">
            <button class="settings-option" @click="openRename">Переименовать</button>
            <button class="settings-option settings-option--danger" @click="openDeleteConfirm">Удалить</button>
          </template>
          <template v-if="showRenameInput">
            <p class="settings-label">Новое название</p>
            <input v-model="newName" type="text" class="settings-input" />
            <p v-if="actionError" class="settings-error">{{ actionError }}</p>
            <div class="settings-actions">
              <BaseButton label="Сохранить" :disabled="renameLoading || !newName.trim()" @click="handleRename" />
              <BaseButton label="Отмена" variant="secondary" @click="backToMenu" />
            </div>
          </template>
          <template v-if="showDeleteConfirm">
            <p class="settings-label">Введите название категории для удаления:</p>
            <p class="settings-delete-name">{{ localCategoryName }}</p>
            <input
              v-model="deleteConfirmName"
              type="text"
              class="settings-input"
              placeholder="Название категории"
              autocomplete="off"
              @keyup.enter="handleDelete"
            />
            <p v-if="actionError" class="settings-error">{{ actionError }}</p>
            <div class="settings-actions">
              <BaseButton
                label="Удалить"
                variant="danger"
                :disabled="deleteLoading || deleteConfirmName !== localCategoryName"
                @click="handleDelete"
              />
              <BaseButton label="Отмена" variant="secondary" @click="backToMenu" />
            </div>
          </template>
        </div>
      </div>
    </template>
    <div class="add-expense-body">
      <p class="add-expense-category">{{ localCategoryName }}</p>
      <input
        v-model="amount"
        type="text"
        inputmode="decimal"
        pattern="[0-9]*"
        placeholder="Сумма расходов, ₽"
        class="add-expense-input"
        autocomplete="off"
      />
      <textarea
        v-model="description"
        placeholder="Описание"
        class="add-expense-textarea"
        rows="2"
      />
      <p v-if="error" class="add-expense-error">{{ error }}</p>
      <div class="add-expense-actions">
        <BaseButton label="Добавить" :disabled="loading || !amount" @click="handleSave" />
        <BaseButton label="Отмена" variant="secondary" @click="handleClose" />
      </div>
    </div>
    <div class="expenses-history">
      <button class="expenses-history-toggle" @click="toggleExpenses">
        <svg
          class="expenses-chevron"
          :class="{ 'expenses-chevron--open': expensesExpanded }"
          width="12"
          height="12"
          viewBox="0 0 12 12"
          fill="none"
        >
          <path d="M4 2l4 4-4 4" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
        </svg>
        <span>Ранее внесенные расходы</span>
      </button>
      <div v-if="expensesExpanded" class="expenses-history-content">
        <div v-if="expensesLoading" class="expenses-loading">Загрузка...</div>
        <div v-else-if="expenses.length === 0" class="expenses-empty">Нет расходов</div>
        <div v-else class="expenses-list">
        <div
          v-for="exp in expenses"
          :key="exp.id"
          class="expense-item"
        >
          <template v-if="editExpenseId === exp.id">
            <div class="expense-edit-form">
              <input
                v-model="editAmount"
                type="text"
                inputmode="decimal"
                pattern="[0-9]*"
                placeholder="Сумма"
                class="expense-edit-input"
                autocomplete="off"
              />
              <textarea
                v-model="editDescription"
                placeholder="Описание"
                class="expense-edit-textarea"
                rows="1"
              />
              <p v-if="actionErrorMsg" class="expense-action-error">{{ actionErrorMsg }}</p>
              <div class="expense-edit-actions">
                <BaseButton label="Сохранить" :disabled="actionLoading || !editAmount" @click="handleEditSave(exp.id)" />
                <BaseButton label="Отмена" variant="secondary" :disabled="actionLoading" @click="cancelEdit" />
              </div>
            </div>
          </template>
          <template v-else-if="deleteConfirmId === exp.id">
            <div class="expense-delete-confirm">
              <p class="expense-delete-text">Удалить расход {{ exp.amount.toFixed(2) }}?</p>
              <p v-if="actionErrorMsg" class="expense-action-error">{{ actionErrorMsg }}</p>
              <div class="expense-edit-actions">
                <BaseButton label="Да, удалить" variant="danger" :disabled="actionLoading" @click="handleDeleteExpense(exp.id)" />
                <BaseButton label="Нет" variant="secondary" :disabled="actionLoading" @click="cancelDeleteConfirm" />
              </div>
            </div>
          </template>
          <template v-else>
            <div class="expense-info">
              <span class="expense-date">{{ formatDate(exp.createdAt) }}</span>
              <span class="expense-author">{{ exp.createdBy }}</span>
              <div class="expense-actions">
                <span class="expense-amount">{{ exp.amount.toFixed(2) }}</span>
                <button class="expense-action-btn" @click="startEdit(exp)" title="Редактировать">
                  <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                    <path d="M11.5 1.5l3 3L5 14H2v-3l9.5-9.5z" stroke="currentColor" stroke-width="1.5" fill="none"/>
                  </svg>
                </button>
                <button class="expense-action-btn expense-action-btn--danger" @click="confirmDelete(exp.id)" title="Удалить">
                  <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                    <path d="M2 4h12M5 4V2.5A1.5 1.5 0 016.5 1h3A1.5 1.5 0 0111 2.5V4M3 4v9.5A1.5 1.5 0 004.5 15h7a1.5 1.5 0 001.5-1.5V4" stroke="currentColor" stroke-width="1.5" fill="none"/>
                  </svg>
                </button>
              </div>
            </div>
            <p v-if="exp.description" class="expense-desc">{{ exp.description }}</p>
        </template>
      </div>
      <div v-if="expensesHasMore || expensesLoadingMore" ref="sentinelRef" class="expenses-sentinel">
        <span v-if="expensesLoadingMore" class="expenses-loading-more">Загрузка...</span>
      </div>
    </div>
  </div>
  </div>
  </BaseModal>
</template>

<style scoped>
.add-expense-body {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.add-expense-category {
  font-size: 14px;
  color: var(--color-text-secondary);
  text-align: center;
}

.add-expense-input {
  width: 100%;
  padding: 12px 14px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  font-size: 16px;
  outline: none;
}

.add-expense-input:focus,
.add-expense-textarea:focus {
  border-color: var(--color-primary);
}

.add-expense-textarea {
  width: 100%;
  padding: 12px 14px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  font-size: 15px;
  outline: none;
  resize: vertical;
  font-family: inherit;
}

.add-expense-error {
  color: var(--color-danger);
  font-size: 13px;
}

.add-expense-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.settings-container {
  position: relative;
}

.settings-btn {
  background: none;
  border: none;
  cursor: pointer;
  color: var(--color-text-secondary);
  padding: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
  transition: background 0.2s;
}

.settings-btn:hover {
  background: var(--color-bg);
}

.settings-dropdown {
  position: absolute;
  top: 100%;
  right: 0;
  z-index: 1100;
  min-width: 220px;
  background: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-md);
  padding: 8px;
  margin-top: 4px;
}

.settings-option {
  display: block;
  width: 100%;
  padding: 10px 12px;
  border: none;
  border-radius: var(--radius-sm);
  background: none;
  font-size: 14px;
  text-align: left;
  cursor: pointer;
  color: var(--color-text);
  transition: background 0.2s;
}

.settings-option:hover {
  background: var(--color-bg);
}

.settings-option--danger {
  color: var(--color-danger);
}

.settings-label {
  font-size: 13px;
  color: var(--color-text-secondary);
  margin-bottom: 8px;
  text-align: center;
}

.settings-input {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  font-size: 14px;
  outline: none;
  margin-bottom: 8px;
}

.settings-input:focus {
  border-color: var(--color-primary);
}

.settings-error {
  color: var(--color-danger);
  font-size: 12px;
  margin-bottom: 6px;
  text-align: center;
}

.settings-actions {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.expenses-history {
  border-top: 1px solid var(--color-border);
  padding-top: 10px;
  margin-top: 14px;
}

.expenses-history-toggle {
  display: flex;
  align-items: center;
  gap: 8px;
  width: 100%;
  background: none;
  border: none;
  padding: 6px 0;
  font-size: 14px;
  font-weight: 600;
  color: var(--color-text);
  cursor: pointer;
  -webkit-tap-highlight-color: transparent;
}

.expenses-chevron {
  flex-shrink: 0;
  transition: transform 0.2s;
  color: var(--color-text-secondary);
}

.expenses-chevron--open {
  transform: rotate(90deg);
}

.expenses-history-content {
  margin-top: 10px;
}

.expenses-loading,
.expenses-empty {
  font-size: 13px;
  color: var(--color-text-secondary);
  text-align: center;
  padding: 12px 0;
}

.expenses-sentinel {
  display: flex;
  justify-content: center;
  padding: 8px 0;
}

.expenses-loading-more {
  font-size: 12px;
  color: var(--color-text-secondary);
}

.expenses-list {
  max-height: 260px;
  overflow-y: auto;
  -webkit-overflow-scrolling: touch;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.expense-item {
  background: var(--color-bg);
  border-radius: var(--radius-sm);
  padding: 10px 12px;
}

.expense-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.expense-date {
  font-size: 12px;
  color: var(--color-text-secondary);
}

.expense-amount {
  font-size: 15px;
  font-weight: 600;
  color: var(--color-text);
}

.expense-desc {
  font-size: 13px;
  color: var(--color-text-secondary);
  margin-top: 4px;
}

.expense-author {
  font-size: 12px;
  color: var(--color-text-secondary);
  margin-left: 8px;
}

.expense-actions {
  display: flex;
  align-items: center;
  gap: 4px;
}

.expense-action-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 44px;
  height: 44px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-sm);
  background: var(--color-surface);
  cursor: pointer;
  color: var(--color-text-secondary);
  transition: background 0.2s, color 0.2s;
  -webkit-tap-highlight-color: transparent;
  touch-action: manipulation;
}

.expense-action-btn svg {
  pointer-events: none;
}

.expense-action-btn:hover {
  background: var(--color-bg);
  color: var(--color-text);
}

.expense-action-btn--danger:hover {
  color: var(--color-danger);
  border-color: var(--color-danger);
}

.expense-edit-form {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.expense-edit-input {
  width: 100%;
  padding: 8px 10px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-sm);
  font-size: 14px;
  outline: none;
}

.expense-edit-input:focus,
.expense-edit-textarea:focus {
  border-color: var(--color-primary);
}

.expense-edit-textarea {
  width: 100%;
  padding: 8px 10px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-sm);
  font-size: 13px;
  outline: none;
  resize: vertical;
  font-family: inherit;
}

.expense-action-error {
  color: var(--color-danger);
  font-size: 12px;
}

.expense-edit-actions {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.expense-delete-confirm {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.expense-delete-text {
  font-size: 13px;
  color: var(--color-text);
  text-align: center;
}
</style>
