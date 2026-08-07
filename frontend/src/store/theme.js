import { defineStore } from 'pinia'
import { ref, watch } from 'vue'

// Predefined color themes
export const THEMES = {
  purple:  { name: '极光紫', primary: '#667eea', primaryDark: '#764ba2', label: '🟣' },
  blue:    { name: '海洋蓝', primary: '#1890ff', primaryDark: '#096dd9', label: '🔵' },
  green:   { name: '翡翠绿', primary: '#52c41a', primaryDark: '#389e0d', label: '🟢' },
  orange:  { name: '活力橙', primary: '#fa8c16', primaryDark: '#d46b08', label: '🟠' },
  red:     { name: '中国红', primary: '#f5222d', primaryDark: '#cf1322', label: '🔴' },
  cyan:    { name: '青碧色', primary: '#13c2c2', primaryDark: '#08979c', label: '🩵' },
  pink:    { name: '樱花粉', primary: '#eb2f96', primaryDark: '#c41d7f', label: '🌸' },
}

export const useThemeStore = defineStore('theme', () => {
  // Load saved preferences or use defaults
  const savedDark = localStorage.getItem('ds1-darkMode')
  const savedTheme = localStorage.getItem('ds1-theme')

  const isDark = ref(savedDark === 'true')
  const themeName = ref(savedTheme && THEMES[savedTheme] ? savedTheme : 'purple')

  const currentTheme = ref(THEMES[themeName.value])

  // Persist and apply
  function applyTheme() {
    const root = document.documentElement
    const t = currentTheme.value

    root.style.setProperty('--color-primary', t.primary)
    root.style.setProperty('--color-primary-dark', t.primaryDark)
    root.style.setProperty('--color-primary-light', t.primary + 'cc')

    // Dark mode class
    root.classList.toggle('dark', isDark.value)

    localStorage.setItem('ds1-darkMode', isDark.value)
    localStorage.setItem('ds1-theme', themeName.value)
  }

  function toggleDark() {
    isDark.value = !isDark.value
    applyTheme()
  }

  function setTheme(name) {
    if (!THEMES[name]) return
    themeName.value = name
    currentTheme.value = THEMES[name]
    applyTheme()
  }

  // Apply on init
  applyTheme()

  return { isDark, themeName, currentTheme, toggleDark, setTheme, applyTheme }
})
