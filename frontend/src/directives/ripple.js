/**
 * v-ripple — Material Design ripple effect
 * Usage: v-ripple              (default color)
 *        v-ripple="'#409EFF'"  (custom color)
 *        v-ripple:center       (ripple from center)
 */
function createRipple(event, el, color, center) {
  const rect = el.getBoundingClientRect()
  const size = Math.max(rect.width, rect.height) * 2.5

  const ripple = document.createElement('span')
  ripple.className = 'v-ripple-inner'
  ripple.style.width = ripple.style.height = `${size}px`
  ripple.style.position = 'absolute'
  ripple.style.borderRadius = '50%'
  ripple.style.background = color || 'rgba(255, 255, 255, 0.35)'
  ripple.style.transform = 'scale(0)'
  ripple.style.animation = 'ripple-effect 0.6s ease-out forwards'
  ripple.style.pointerEvents = 'none'

  if (center) {
    ripple.style.left = `${(rect.width - size) / 2}px`
    ripple.style.top = `${(rect.height - size) / 2}px`
  } else {
    ripple.style.left = `${event.clientX - rect.left - size / 2}px`
    ripple.style.top = `${event.clientY - rect.top - size / 2}px`
  }

  el.appendChild(ripple)

  ripple.addEventListener('animationend', () => {
    ripple.remove()
  })
}

// Inject keyframes once
let injected = false
function injectKeyframes() {
  if (injected) return
  const style = document.createElement('style')
  style.textContent = `
    @keyframes ripple-effect {
      0%   { transform: scale(0); opacity: 0.5; }
      100% { transform: scale(1); opacity: 0; }
    }
  `
  document.head.appendChild(style)
  injected = true
}

export default {
  mounted(el, binding) {
    injectKeyframes()
    el.style.position = el.style.position || 'relative'
    el.style.overflow = el.style.overflow || 'hidden'

    const color = typeof binding.value === 'string' ? binding.value : null
    const center = binding.arg === 'center'

    function handler(event) {
      createRipple(event, el, color, center)
    }

    el.addEventListener('click', handler)
    el._rippleHandler = handler
  },

  unmounted(el) {
    if (el._rippleHandler) {
      el.removeEventListener('click', el._rippleHandler)
      delete el._rippleHandler
    }
  }
}
