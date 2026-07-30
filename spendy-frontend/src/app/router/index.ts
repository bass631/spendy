import { createRouter, createWebHistory } from 'vue-router'
import TrackingView from '@/features/tracking/components/TrackingView.vue'
import StatisticsView from '@/features/statistics/components/StatisticsView.vue'
import LoginForm from '@/features/auth/components/LoginForm.vue'

const router = createRouter({
  history: createWebHistory('/'),
  routes: [
    { path: '/', redirect: '/tracking' },
    { path: '/login', name: 'login', component: LoginForm },
    { path: '/tracking', name: 'tracking', component: TrackingView, meta: { requiresAuth: true } },
    { path: '/statistics', name: 'statistics', component: StatisticsView, meta: { requiresAuth: true } },
  ],
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('accessToken')
  if (to.meta.requiresAuth && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router
