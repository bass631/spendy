<script setup lang="ts">
import { provide, ref, computed } from 'vue'
import { useAuthStore } from '@/features/auth/store/authStore'
import { useRouter } from 'vue-router'

const authStore = useAuthStore()
const router = useRouter()

const addCategorySignal = ref(0)
provide('addCategorySignal', addCategorySignal)
const exportSignal = ref(0)
provide('exportSignal', exportSignal)
const isTrackingPage = computed(() => router.currentRoute.value.path.startsWith('/tracking'))
const isStatisticsPage = computed(() => router.currentRoute.value.path.startsWith('/statistics'))

function handleAddCategory() {
  addCategorySignal.value++
}

function handleExport() {
  if (window.confirm('Выгрузить данные за выбранный период в XLSX?')) {
    exportSignal.value++
  }
}

function handleLogout() {
  if (window.confirm('Вы уверены, что хотите выйти?')) {
    authStore.logout()
    router.push('/login')
  }
}
</script>

<template>
  <div class="app-container">
    <header v-if="authStore.isAuthenticated" class="app-header">
      <div class="app-header-left">
        <img src="/icon/app-icon.svg" alt="Spendy" class="app-icon" />
        <span class="app-title">Spendy</span>
      </div>
      <div class="app-header-right">
        <button v-if="isTrackingPage" class="btn-icon" @click="handleAddCategory" title="Добавить категорию">
          <svg width="22" height="22" viewBox="0 0 22 22" fill="none">
            <path d="M11 5v12M5 11h12" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
          </svg>
        </button>
        <button v-if="isStatisticsPage" class="btn-icon" @click="handleExport" title="Выгрузить в XLSX">
          <svg width="22" height="22" viewBox="0 0 22 22" fill="none">
            <path d="M11 2v12M7 10l4 4 4-4M4 16v2a1 1 0 001 1h12a1 1 0 001-1v-2" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </button>
        <button class="btn-icon" @click="handleLogout" title="Выход">
          <svg width="22" height="22" viewBox="0 0 22 22" fill="none">
            <path d="M9 3H4a1 1 0 00-1 1v14a1 1 0 001 1h5M14 16l4-4-4-4M18 12H8" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </button>
      </div>
    </header>
    <main class="app-main">
      <router-view />
    </main>
    <nav v-if="authStore.isAuthenticated" class="app-tabbar">
      <router-link to="/tracking" class="tabbar-item" active-class="tabbar-item--active">
        Учёт
      </router-link>
      <router-link to="/statistics" class="tabbar-item" active-class="tabbar-item--active">
        Статистика
      </router-link>
    </nav>
  </div>
</template>

<style scoped>
.app-container {
  display: flex;
  flex-direction: column;
  height: 100dvh;
  overflow: hidden;
  background: var(--color-bg);
  color: var(--color-text);
}

.app-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  padding-top: calc(12px + var(--safe-area-top));
  background: var(--color-surface);
  border-bottom: 1px solid var(--color-border);
}

.app-header-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.app-header-right {
  display: flex;
  align-items: center;
  gap: 8px;
}

.app-icon {
  width: 28px;
  height: 28px;
}

.app-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--color-primary);
}

.btn-icon {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: none;
  border: 1px solid var(--color-border);
  border-radius: 10px;
  color: var(--color-text-secondary);
  cursor: pointer;
  transition: background 0.15s, color 0.15s;
  -webkit-tap-highlight-color: transparent;
  touch-action: manipulation;
}

.btn-icon:hover {
  background: var(--color-bg);
  color: var(--color-text);
}

.app-main {
  flex: 1;
  overflow-y: auto;
  -webkit-overflow-scrolling: touch;
  overscroll-behavior: contain;
  padding: 16px;
}

.app-tabbar {
  display: flex;
  background: var(--color-surface);
  padding-bottom: calc(0px + var(--safe-area-bottom));
  border-top: 1px solid var(--color-border);
}

.tabbar-item {
  flex: 1;
  text-align: center;
  padding: 14px 0 12px;
  min-height: var(--touch-target-min);
  display: flex;
  align-items: center;
  justify-content: center;
  text-decoration: none;
  font-size: 16px;
  font-weight: 500;
  color: var(--color-text-secondary);
  -webkit-tap-highlight-color: transparent;
  position: relative;
  margin-bottom: -1px;
  border: 1px solid var(--color-border);
  border-bottom: none;
  border-radius: 8px 8px 0 0;
  background: var(--color-bg);
  transition: color 0.2s, background 0.15s;
  z-index: 0;
}

.tabbar-item:first-child {
  margin-left: 0;
}

.tabbar-item:last-child {
  margin-right: 0;
}

.tabbar-item--active {
  color: var(--color-primary);
  font-weight: 700;
  background: var(--color-surface);
  border-bottom: none;
  z-index: 1;
  box-shadow: 0 -1px 4px rgba(0,0,0,0.08);
  margin-left: -1px;
  margin-right: -1px;
}
</style>
