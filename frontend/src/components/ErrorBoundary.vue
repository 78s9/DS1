<template>
  <div>
    <slot v-if="!error" />
    <div v-else class="error-boundary-fallback">
      <div class="error-icon">⚠️</div>
      <h3>组件渲染出错</h3>
      <p class="error-message">{{ error.message }}</p>
      <pre v-if="showStack" class="error-stack">{{ error.stack }}</pre>
      <div class="error-actions">
        <el-button type="primary" size="small" @click="retry">
          重试
        </el-button>
        <el-button size="small" @click="showStack = !showStack">
          {{ showStack ? '隐藏' : '显示' }}详情
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onErrorCaptured } from 'vue'

const error = ref(null)
const showStack = ref(false)

onErrorCaptured((err, instance, info) => {
  console.error('[ErrorBoundary] Captured error:', err, info)
  error.value = err
  // Return false to prevent the error from propagating further
  return false
})

function retry() {
  error.value = null
  showStack.value = false
}
</script>

<style scoped>
.error-boundary-fallback {
  padding: 40px 24px;
  text-align: center;
  background: #fff;
  border: 1px solid #fde2e2;
  border-radius: 8px;
}

.error-icon {
  font-size: 48px;
  margin-bottom: 12px;
}

.error-boundary-fallback h3 {
  margin: 0 0 8px;
  color: #f56c6c;
  font-size: 18px;
}

.error-message {
  color: #606266;
  font-size: 14px;
  margin: 0 0 16px;
}

.error-stack {
  text-align: left;
  background: #f5f5f5;
  padding: 12px;
  border-radius: 6px;
  font-size: 12px;
  max-height: 200px;
  overflow: auto;
  margin-bottom: 16px;
  white-space: pre-wrap;
  word-break: break-all;
}

.error-actions {
  display: flex;
  gap: 8px;
  justify-content: center;
}
</style>
