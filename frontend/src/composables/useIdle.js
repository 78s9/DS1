/**
 * useIdle — Detect user idle state
 * Fires when the user hasn't interacted (mouse, keyboard, touch) for a duration.
 *
 * @param {number} timeout - idle timeout in ms (default 60000 = 1min)
 * @returns {{ isIdle, lastActivity, resetIdle }}
 */
import { ref, onMounted, onUnmounted } from 'vue'

export function useIdle(timeout = 60000) {
  const isIdle = ref(false)
  const lastActivity = ref(Date.now())
  let timer = null

  const events = ['mousemove', 'keydown', 'mousedown', 'touchstart', 'scroll', 'wheel']

  function resetTimer() {
    if (timer) clearTimeout(timer)
    isIdle.value = false
    lastActivity.value = Date.now()

    timer = setTimeout(() => {
      isIdle.value = true
    }, timeout)
  }

  function onActivity() {
    resetTimer()
  }

  function resetIdle() {
    resetTimer()
  }

  onMounted(() => {
    events.forEach(e => window.addEventListener(e, onActivity, { passive: true }))
    resetTimer()
  })

  onUnmounted(() => {
    if (timer) clearTimeout(timer)
    events.forEach(e => window.removeEventListener(e, onActivity))
  })

  return { isIdle, lastActivity, resetIdle }
}
