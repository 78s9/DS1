/**
 * useClipboard — Clipboard API composable
 * @returns {{ copy, cut, paste, isSupported, lastCopied }}
 */
import { ref } from 'vue'
import { ElMessage } from 'element-plus'

export function useClipboard() {
  const isSupported = ref(!!navigator.clipboard)
  const lastCopied = ref('')

  async function copy(text) {
    if (!isSupported.value) {
      // Fallback for older browsers
      try {
        const textarea = document.createElement('textarea')
        textarea.value = text
        textarea.style.position = 'fixed'
        textarea.style.opacity = '0'
        document.body.appendChild(textarea)
        textarea.select()
        document.execCommand('copy')
        document.body.removeChild(textarea)
        lastCopied.value = text
        ElMessage.success('已复制到剪贴板 📋')
        return true
      } catch {
        ElMessage.error('复制失败，请手动复制')
        return false
      }
    }

    try {
      await navigator.clipboard.writeText(text)
      lastCopied.value = text
      ElMessage.success('已复制到剪贴板 📋')
      return true
    } catch {
      ElMessage.error('复制失败，请检查权限')
      return false
    }
  }

  async function paste() {
    if (!isSupported.value) return ''
    try {
      const text = await navigator.clipboard.readText()
      return text
    } catch {
      ElMessage.error('粘贴失败，请检查权限')
      return ''
    }
  }

  return { copy, paste, isSupported, lastCopied }
}
