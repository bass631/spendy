export function logError(error: unknown, context?: string) {
  const message = error instanceof Error ? error.message : String(error)
  console.error(`[Monitoring] ${context ? `[${context}] ` : ''}${message}`)
  if (import.meta.env.PROD) {
    // DČ: send to external service
  }
}
