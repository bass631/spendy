import { onMounted } from 'vue'
import { useTrackingStore } from '@/features/tracking/store/trackingStore'
import { storeToRefs } from 'pinia'

export function useCategoriesSortedByFrequency() {
  const store = useTrackingStore()
  const { categories, loading } = storeToRefs(store)

  onMounted(() => {
    store.loadCategories()
  })

  return { categories, loading, refresh: store.loadCategories }
}
