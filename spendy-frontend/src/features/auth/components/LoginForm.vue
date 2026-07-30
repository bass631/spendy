<script setup lang="ts">
import { ref } from 'vue'
import BaseButton from '@/shared/ui/BaseButton.vue'
import { useAuth } from '@/features/auth/composables/useAuth'

const { loading, error, handleLogin } = useAuth()
const username = ref('')
const password = ref('')

function submit() {
  if (!username.value || !password.value) return
  handleLogin(username.value, password.value)
}
</script>

<template>
  <div class="login-container">
    <div class="login-card">
      <img src="/icon/app-icon.svg" alt="Spendy" class="login-icon" />
      <h1 class="login-title">Spendy</h1>
      <p class="login-subtitle">Учёт финансовых расходов</p>
      <form @submit.prevent="submit" class="login-form">
        <input
          v-model="username"
          type="text"
          placeholder="Имя пользователя"
          class="login-input"
          autocomplete="username"
        />
        <input
          v-model="password"
          type="password"
          placeholder="Пароль"
          class="login-input"
          autocomplete="current-password"
        />
        <p v-if="error" class="login-error">{{ error }}</p>
        <BaseButton
          label="Войти"
          :disabled="loading"
          @click="submit"
        />
      </form>
    </div>
  </div>
</template>

<style scoped>
.login-container {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100dvh;
  padding: 16px;
  padding-top: calc(16px + var(--safe-area-top));
  padding-bottom: calc(16px + var(--safe-area-bottom));
}

.login-card {
  width: 100%;
  max-width: 360px;
  text-align: center;
}

.login-icon {
  width: 56px;
  height: 56px;
  margin-bottom: 12px;
}

.login-title {
  font-size: 24px;
  font-weight: 700;
  color: var(--color-primary);
}

.login-subtitle {
  font-size: 14px;
  color: var(--color-text-secondary);
  margin-bottom: 24px;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.login-input {
  width: 100%;
  padding: 12px 14px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  font-size: 15px;
  outline: none;
  transition: border-color 0.2s;
}

.login-input:focus {
  border-color: var(--color-primary);
}

.login-error {
  color: var(--color-danger);
  font-size: 13px;
}
</style>
