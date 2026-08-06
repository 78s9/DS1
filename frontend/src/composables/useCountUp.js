import { ref, onUnmounted } from 'vue'

/**
 * Animated number counting composable.
 * @param {number} end - Target value
 * @param {number} duration - Animation duration in ms (default 1500)
 * @returns {{ current: import('vue').Ref<number> }}
 */
export function useCountUp(end, duration = 1500) {
  const current = ref(0)
  let animationId = null
  let startTime = null

  function step(timestamp) {
    if (!startTime) startTime = timestamp
    const elapsed = timestamp - startTime
    const progress = Math.min(elapsed / duration, 1)

    // Ease-out cubic
    const eased = 1 - Math.pow(1 - progress, 3)
    current.value = Math.round(eased * end)

    if (progress < 1) {
      animationId = requestAnimationFrame(step)
    } else {
      current.value = end
    }
  }

  function start() {
    if (animationId) cancelAnimationFrame(animationId)
    current.value = 0
    startTime = null
    animationId = requestAnimationFrame(step)
  }

  // Auto-start on creation
  start()

  onUnmounted(() => {
    if (animationId) cancelAnimationFrame(animationId)
  })

  return { current }
}
