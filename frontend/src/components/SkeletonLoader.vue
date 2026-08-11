<template>
  <div class="skeleton-loader" :style="{ width, height }">
    <div
      v-for="i in count"
      :key="i"
      class="skeleton-item"
      :class="variant"
      :style="{ width: itemWidth, marginBottom: gap }"
    >
      <div v-if="variant === 'card'" class="skeleton-card">
        <div class="skeleton-line skeleton-title" />
        <div class="skeleton-line skeleton-text" />
        <div class="skeleton-line skeleton-text short" />
      </div>
      <div v-else-if="variant === 'table'" class="skeleton-table-row">
        <span class="skeleton-cell" v-for="c in columns" :key="c" />
      </div>
      <div v-else class="skeleton-line" />
    </div>
  </div>
</template>

<script setup>
defineProps({
  count: { type: Number, default: 3 },
  variant: { type: String, default: 'line' },  // line | card | table
  width: { type: String, default: '100%' },
  height: { type: String, default: 'auto' },
  itemWidth: { type: String, default: '100%' },
  gap: { type: String, default: '12px' },
  columns: { type: Number, default: 4 }
})
</script>

<style scoped>
.skeleton-loader {
  padding: 8px 0;
}

/* Shimmer animation */
.skeleton-item {
  position: relative;
  overflow: hidden;
}

.skeleton-line,
.skeleton-cell {
  height: 16px;
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  animation: shimmer 1.5s infinite;
  border-radius: 4px;
}

.skeleton-card {
  padding: 16px;
  background: #fafafa;
  border-radius: 8px;
  border: 1px solid #f0f0f0;
}

.skeleton-title {
  height: 20px;
  width: 50%;
  margin-bottom: 12px;
}

.skeleton-text {
  height: 14px;
  width: 100%;
  margin-bottom: 8px;
}

.skeleton-text.short {
  width: 70%;
}

.skeleton-table-row {
  display: flex;
  gap: 16px;
}

.skeleton-cell {
  flex: 1;
  height: 14px;
}

@keyframes shimmer {
  0%   { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}

/* Dark mode */
html.dark .skeleton-line,
html.dark .skeleton-cell {
  background: linear-gradient(90deg, #333 25%, #444 50%, #333 75%);
  background-size: 200% 100%;
}

html.dark .skeleton-card {
  background: #2a2a2a;
  border-color: #333;
}
</style>
