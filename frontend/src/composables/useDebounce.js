/**
 * useDebounce — Debounce a reactive value or function
 * @param {Ref|Function} source - ref value or callback function
 * @param {number} delay - debounce delay in ms
 * @returns {{ debounced: Ref, cancel, flush }}
 */
import { ref, watch, onUnmounted } from 'vue'

export function useDebounce(source, delay = 300) {
  const debounced = ref(typeof source === 'function' ? undefined : source.value)
  let timer = null
  let pendingValue = null

  function cancel() {
    if (timer) {
      clearTimeout(timer)
      timer = null
    }
    pendingValue = null
  }

  function flush() {
    cancel()
    if (pendingValue !== null) {
      debounced.value = pendingValue
      pendingValue = null
    }
  }

  // If source is a ref, watch it. If it's a function, return a debounced wrapper.
  if (typeof source !== 'function') {
    watch(
      source,
      (val) => {
        pendingValue = val
        cancel()
        timer = setTimeout(() => {
          debounced.value = val
          pendingValue = null
          timer = null
        }, delay)
      },
      { immediate: false }
    )
    onUnmounted(cancel)

    return { debounced, cancel, flush }
  }

  // Function mode: return a debounced function
  function debouncedFn(...args) {
    cancel()
    return new Promise((resolve) => {
      timer = setTimeout(() => {
        const result = source(...args)
        debounced.value = result
        resolve(result)
        timer = null
      }, delay)
    })
  }

  debouncedFn.cancel = cancel
  debouncedFn.flush = flush

  onUnmounted(cancel)

  return debouncedFn
}
