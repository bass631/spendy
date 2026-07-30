import { App } from 'vue'

export function setupErrorHandler(app: App) {
  app.config.errorHandler = (err, instance, info) => {
    console.error('[Global error]', err, info)
    if (import.meta.env.PROD) {
      // DČ: send to external monitoring service
    }
  }
}
