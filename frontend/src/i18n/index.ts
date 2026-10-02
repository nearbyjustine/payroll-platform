import { createI18n } from 'vue-i18n'
import en from './en'
import fil from './fil'

const saved = (() => {
  try {
    return localStorage.getItem('locale')
  } catch {
    return null
  }
})()

export const i18n = createI18n({
  legacy: false,
  locale: saved === 'fil' ? 'fil' : 'en',
  fallbackLocale: 'en',
  messages: { en, fil },
})
