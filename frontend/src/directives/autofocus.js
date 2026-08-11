/**
 * v-autofocus — Auto-focus input on mount
 * Usage: v-autofocus
 *        v-autofocus:select   (focus and select all text)
 *        v-autofocus:delay="500" (delay in ms before focusing)
 */
export default {
  mounted(el, binding) {
    const delay = binding.arg === 'delay'
      ? (Number(binding.value) || 0)
      : (typeof binding.value === 'number' ? binding.value : 0)

    const selectAll = binding.arg === 'select'

    function focus() {
      const target = el.tagName === 'INPUT' || el.tagName === 'TEXTAREA'
        ? el
        : el.querySelector('input, textarea, select')

      if (target) {
        target.focus()
        if (selectAll && target.select) {
          target.select()
        }
      }
    }

    if (delay > 0) {
      setTimeout(focus, delay)
    } else {
      // nextTick ensures the element is in the DOM
      requestAnimationFrame(focus)
    }
  }
}
