/**
 * useVirtualScroll — Virtual scrolling composable
 * Efficiently renders only visible items in a large list.
 *
 * @param {Ref<Array>} items - reactive array of all items
 * @param {Object} options
 * @param {number} options.itemHeight - height of each item in px (default 48)
 * @param {number} options.overscan - extra items above/below viewport (default 5)
 * @returns {{ containerRef, totalHeight, offsetY, visibleItems, scrollTo }}
 */
import { ref, computed, shallowRef } from 'vue'

export function useVirtualScroll(itemsRef, options = {}) {
  const { itemHeight = 48, overscan = 5 } = options

  const containerRef = ref(null)
  const scrollTop = shallowRef(0)
  const viewportHeight = shallowRef(0)

  // Total scrollable height
  const totalHeight = computed(() => itemsRef.value.length * itemHeight)

  // Start index of visible range
  const startIndex = computed(() => {
    const idx = Math.floor(scrollTop.value / itemHeight) - overscan
    return Math.max(0, idx)
  })

  // End index of visible range
  const endIndex = computed(() => {
    const idx = Math.ceil((scrollTop.value + viewportHeight.value) / itemHeight) + overscan
    return Math.min(itemsRef.value.length, idx)
  })

  // Visible items (slice of the full array)
  const visibleItems = computed(() => {
    return itemsRef.value.slice(startIndex.value, endIndex.value).map((item, i) => ({
      ...item,
      _index: startIndex.value + i,
      _style: {
        position: 'absolute',
        top: `${(startIndex.value + i) * itemHeight}px`,
        height: `${itemHeight}px`,
        left: 0,
        right: 0
      }
    }))
  })

  // Offset transform for the inner container
  const offsetY = computed(() => startIndex.value * itemHeight)

  /**
   * Handle scroll event — update scrollTop
   */
  function handleScroll(event) {
    scrollTop.value = event.target.scrollTop
    viewportHeight.value = event.target.clientHeight
  }

  /**
   * Scroll to a specific index
   */
  function scrollTo(index) {
    if (containerRef.value) {
      containerRef.value.scrollTop = index * itemHeight
    }
  }

  /**
   * Re-measure viewport (call on resize)
   */
  function refresh() {
    if (containerRef.value) {
      viewportHeight.value = containerRef.value.clientHeight
    }
  }

  return {
    containerRef,
    totalHeight,
    offsetY,
    visibleItems,
    startIndex,
    endIndex,
    handleScroll,
    scrollTo,
    refresh
  }
}
