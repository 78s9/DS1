<template>
  <!--
    Flipper — FLIP animation wrapper
    Animates elements between positions using the FLIP technique
    (First, Last, Invert, Play).

    Props:
      tag: String — wrapper tag (default 'div')
      duration: Number — animation duration in ms

    Usage:
      <Flipper>
        <div v-for="item in items" :key="item.id" :data-flip-key="item.id">
          {{ item.name }}
        </div>
      </Flipper>
  -->
  <component :is="tag" ref="flipRef" class="flip-container">
    <slot />
  </component>
</template>

<script setup>
import { ref, onMounted, onUpdated, nextTick } from 'vue'

const props = defineProps({
  tag: { type: String, default: 'div' },
  duration: { type: Number, default: 300 },
  easing: { type: String, default: 'cubic-bezier(0.25, 0.8, 0.25, 1.2)' }
})

const flipRef = ref(null)
let positions = new Map()

function getRect(el) {
  return el.getBoundingClientRect()
}

function snapshot() {
  positions.clear()
  if (!flipRef.value) return
  const children = flipRef.value.querySelectorAll('[data-flip-key]')
  children.forEach(child => {
    positions.set(child.dataset.flipKey, getRect(child))
  })
}

function invert() {
  if (!flipRef.value) return
  const children = flipRef.value.querySelectorAll('[data-flip-key]')
  const currentPositions = new Map()

  children.forEach(child => {
    const key = child.dataset.flipKey
    const prev = positions.get(key)
    const curr = getRect(child)
    currentPositions.set(key, curr)

    if (prev) {
      const dx = prev.left - curr.left
      const dy = prev.top - curr.top

      if (dx !== 0 || dy !== 0) {
        // Invert: apply inverse transform
        child.style.transform = `translate(${dx}px, ${dy}px)`
        child.style.transition = 'none'
        child._flipDx = dx
        child._flipDy = dy
      }
    }
  })

  return currentPositions
}

function play() {
  if (!flipRef.value) return
  const children = flipRef.value.querySelectorAll('[data-flip-key]')

  // Force reflow
  flipRef.value.offsetHeight

  children.forEach(child => {
    if (child._flipDx !== undefined) {
      child.style.transition = `transform ${props.duration}ms ${props.easing}`
      child.style.transform = ''
      delete child._flipDx
      delete child._flipDy
    }
  })
}

onMounted(() => {
  snapshot()
})

onUpdated(() => {
  nextTick(() => {
    const newPositions = invert()
    if (newPositions) {
      positions = newPositions
    }
    play()
  })
})
</script>

<style scoped>
.flip-container {
  position: relative;
}
</style>
