<template>
  <!--
    VirtualList — Renderless Virtual Scroll Component
    Takes a large list and renders only visible items using absolute positioning.

    Props:
      items: Array — the full list
      itemHeight: Number — height per item (default 48)
      overscan: Number — extra items above/below viewport (default 5)

    Slots:
      default: { item, index, style } — render each visible item
  -->
  <div
    ref="viewportRef"
    class="virtual-list-viewport"
    :style="{ height: viewportHeight || '400px' }"
    @scroll="handleScroll"
  >
    <div class="virtual-list-spacer" :style="{ height: totalHeight + 'px' }">
      <div class="virtual-list-content" :style="{ transform: `translateY(${offsetY}px)` }">
        <div
          v-for="item in visibleItems"
          :key="item._key || item._index"
          class="virtual-list-item"
          :style="item._style"
        >
          <slot name="default" :item="item" :index="item._index" :style="item._style" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, onMounted, watch } from 'vue'

const props = defineProps({
  items: { type: Array, required: true },
  itemHeight: { type: Number, default: 48 },
  overscan: { type: Number, default: 5 },
  keyField: { type: String, default: '' }
})

const viewportRef = ref(null)
const scrollTop = ref(0)
const viewportHeight = ref(400)

const totalHeight = computed(() => props.items.length * props.itemHeight)

const startIndex = computed(() =>
  Math.max(0, Math.floor(scrollTop.value / props.itemHeight) - props.overscan)
)

const endIndex = computed(() =>
  Math.min(
    props.items.length,
    Math.ceil((scrollTop.value + viewportHeight.value) / props.itemHeight) + props.overscan
  )
)

const visibleItems = computed(() => {
  return props.items.slice(startIndex.value, endIndex.value).map((item, i) => {
    const idx = startIndex.value + i
    return {
      ...item,
      _index: idx,
      _key: props.keyField ? item[props.keyField] : idx,
      _style: {
        position: 'absolute',
        top: `${idx * props.itemHeight}px`,
        height: `${props.itemHeight}px`,
        left: 0,
        right: 0
      }
    }
  })
})

const offsetY = computed(() => startIndex.value * props.itemHeight)

function handleScroll() {
  if (viewportRef.value) {
    scrollTop.value = viewportRef.value.scrollTop
  }
}

function refresh() {
  if (viewportRef.value) {
    viewportHeight.value = viewportRef.value.clientHeight
  }
}

onMounted(() => {
  refresh()
  // Handle resize
  if (window.ResizeObserver && viewportRef.value) {
    new ResizeObserver(refresh).observe(viewportRef.value)
  }
})

watch(() => props.items, () => {
  scrollTop.value = 0
  if (viewportRef.value) viewportRef.value.scrollTop = 0
})

defineExpose({ refresh, scrollTo: (idx) => {
  if (viewportRef.value) {
    viewportRef.value.scrollTop = idx * props.itemHeight
  }
}})
</script>

<style scoped>
.virtual-list-viewport {
  overflow-y: auto;
  position: relative;
  contain: strict;
}

.virtual-list-spacer {
  position: relative;
}

.virtual-list-content {
  position: sticky;
  top: 0;
}

.virtual-list-item {
  box-sizing: border-box;
}
</style>
