import { createPinia } from 'pinia'
import { useUserStore } from './modules/user'
import { useAppStore } from './modules/app'

const pinia = createPinia()

export function setupStore() {
  // 可以在这里初始化一些全局配置
}

export { useUserStore, useAppStore }
export default pinia
