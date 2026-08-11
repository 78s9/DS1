/**
 * v-click-outside — Detect clicks outside the bound element
 * Usage: v-click-outside="handlerFunction"
 *        v-click-outside:exclude="[ref1, ref2]"  (exclude elements from detection)
 */
const handlerMap = new WeakMap()

export default {
  mounted(el, binding) {
    const excludeRefs = binding.arg === 'exclude'
      ? (Array.isArray(binding.value) ? binding.value : [])
      : []

    function handler(event) {
      // Ignore if clicking on the element itself or its children
      if (el.contains(event.target)) return

      // Ignore excluded elements
      if (excludeRefs.some(ref => {
        const dom = ref?.$el || ref
        return dom && dom.contains(event.target)
      })) return

      binding.value(event)
    }

    // Use capture phase to catch clicks early
    document.addEventListener('click', handler, true)
    handlerMap.set(el, handler)
  },

  unmounted(el) {
    const handler = handlerMap.get(el)
    if (handler) {
      document.removeEventListener('click', handler, true)
      handlerMap.delete(el)
    }
  }
}
