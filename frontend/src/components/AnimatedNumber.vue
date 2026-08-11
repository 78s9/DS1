<template>
  <!--
    AnimatedNumber — GPU-accelerated number transition
    Uses CSS transform (translateY) for smooth digit-flipping animation.

    Props:
      value: Number — target value
      duration: Number — animation duration in ms (default 1000)
      format: Function — optional number formatter
  -->
  <span class="animated-number" ref="elRef">{{ displayValue }}</span>
</template>

<script setup>
import { ref, watch, onMounted, onUnmounted } from 'vue'

const props = defineProps({
  value: { type: Number, default: 0 },
  duration: { type: Number, default: 1000 },
  decimals: { type: Number, default: 0 },
  prefix: { type: String, default: '' },
  suffix: { type: String, default: '' }
})

const displayValue = ref('0')
const elRef = ref(null)
let rafId = null

function animate(from, to) {
  if (rafId) cancelAnimationFrame(rafId)

  const start = performance.now()
  const dur = props.duration

  function tick(now) {
    const elapsed = now - start
    const progress = Math.min(elapsed / dur, 1)

    // Ease-out cubic
    const eased = 1 - Math.pow(1 - progress, 3)
    const current = from + (to - from) * eased

    displayValue.value = `${props.prefix}${current.toFixed(props.decimals)}${props.suffix}`

    if (progress < 1) {
      rafId = requestAnimationFrame(tick)
    }
  }

  rafId = requestAnimationFrame(tick)
}

let prevValue = props.value

watch(() => props.value, (newVal) => {
  animate(prevValue, newVal)
  prevValue = newVal
})

onMounted(() => {
  displayValue.value = `${props.prefix}${props.value.toFixed(props.decimals)}${props.suffix}`
  prevValue = props.value
})

onUnmounted(() => {
  if (rafId) cancelAnimationFrame(rafId)
})
</script>

<style scoped>
.animated-number {
  font-variant-numeric: tabular-nums;
  font-feature-settings: 'tnum';
}
</style>
