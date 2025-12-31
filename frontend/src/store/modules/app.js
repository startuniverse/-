import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useAppStore = defineStore('app', () => {
  const sidebarCollapsed = ref(false)
  const theme = ref('light')
  const device = ref('desktop') // desktop | mobile

  const isMobile = computed(() => device.value === 'mobile')

  function toggleSidebar() {
    sidebarCollapsed.value = !sidebarCollapsed.value
  }

  function setTheme(newTheme) {
    theme.value = newTheme
  }

  function setDevice(newDevice) {
    device.value = newDevice
  }

  return {
    sidebarCollapsed,
    theme,
    device,
    isMobile,
    toggleSidebar,
    setTheme,
    setDevice
  }
})
