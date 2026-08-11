/**
 * v-intersect — IntersectionObserver directive
 * Usage: v-intersect="{ onEnter, onLeave, threshold, rootMargin }"
 *        v-intersect:leave="onLeaveCallback"  (shorthand for leave only)
 */
const instanceMap = new WeakMap()

export default {
  mounted(el, binding) {
    const options = typeof binding.value === 'object' ? binding.value : { onEnter: binding.value }
    const {
      onEnter = () => {},
      onLeave = () => {},
      threshold = 0,
      rootMargin = '0px',
      once = false
    } = options

    const observer = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting) {
            onEnter(entry, el)
            if (once) observer.unobserve(el)
          } else {
            onLeave(entry, el)
          }
        })
      },
      { threshold, rootMargin }
    )

    observer.observe(el)
    instanceMap.set(el, { observer, once, onEnter })
  },

  unmounted(el) {
    const instance = instanceMap.get(el)
    if (instance) {
      instance.observer.unobserve(el)
      instance.observer.disconnect()
      instanceMap.delete(el)
    }
  }
}
