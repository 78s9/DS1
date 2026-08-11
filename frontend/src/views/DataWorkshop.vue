<template>
  <div class="data-workshop">
    <!-- Page Header -->
    <div class="workshop-banner">
      <div class="workshop-left">
        <span class="workshop-icon">🔮</span>
        <div>
          <h2>数据工坊</h2>
          <p>Vue 3 极限特性展示 — 虚拟滚动 · 自定义指令 · 异步组件 · 组合式 API</p>
        </div>
      </div>
      <div class="workshop-badge">
        <el-tag type="warning" effect="dark" round>Vue 3 进阶</el-tag>
      </div>
    </div>

    <!-- Tab Navigation with KeepAlive -->
    <el-card shadow="never" class="workshop-tabs-card">
      <el-tabs v-model="activeTab" type="border-card" @tab-change="onTabChange">
        <el-tab-pane name="table" lazy>
          <template #label>
            <span class="tab-label">
              <el-icon><Grid /></el-icon> 虚拟滚动表格
            </span>
          </template>
        </el-tab-pane>
        <el-tab-pane name="realtime" lazy>
          <template #label>
            <span class="tab-label">
              <el-icon><Monitor /></el-icon> 实时监控
            </span>
          </template>
        </el-tab-pane>
        <el-tab-pane name="lab" lazy>
          <template #label>
            <span class="tab-label">
              <el-icon><MagicStick /></el-icon> 组件实验室
            </span>
          </template>
        </el-tab-pane>
        <el-tab-pane name="directives" lazy>
          <template #label>
            <span class="tab-label">
              <el-icon><Operation /></el-icon> 指令展示
            </span>
          </template>
        </el-tab-pane>
      </el-tabs>

      <!-- KeepAlive caches each tab's component -->
      <div class="tab-content">
        <KeepAlive :max="4">
          <AdvancedTable v-if="activeTab === 'table'" />
          <RealtimeMonitor v-else-if="activeTab === 'realtime'" />
          <ComponentLab v-else-if="activeTab === 'lab'" />
          <DirectiveShowcase v-else-if="activeTab === 'directives'" />
        </KeepAlive>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, defineAsyncComponent } from 'vue'
import { Grid, Monitor, MagicStick, Operation } from '@element-plus/icons-vue'

// Async components with Suspense — each page lazy-loaded
const AdvancedTable = defineAsyncComponent(() => import('@/views/workshop/AdvancedTable.vue'))
const RealtimeMonitor = defineAsyncComponent(() => import('@/views/workshop/RealtimeMonitor.vue'))
const ComponentLab = defineAsyncComponent(() => import('@/views/workshop/ComponentLab.vue'))
const DirectiveShowcase = defineAsyncComponent(() => import('@/views/workshop/DirectiveShowcase.vue'))

const activeTab = ref('table')

function onTabChange(tabName) {
  // Track tab switches (for potential analytics)
}
</script>

<style scoped>
.data-workshop {
  max-width: 1400px;
}

.workshop-banner {
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
  color: #e0e0e0;
  border-radius: 12px;
  padding: 24px 28px;
  margin-bottom: 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: relative;
  overflow: hidden;
}

.workshop-banner::after {
  content: '';
  position: absolute;
  right: 60px;
  top: -20px;
  width: 120px;
  height: 120px;
  background: rgba(102, 126, 234, 0.15);
  border-radius: 50%;
  pointer-events: none;
}

.workshop-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.workshop-icon {
  font-size: 40px;
}

.workshop-banner h2 {
  margin: 0 0 4px;
  font-size: 22px;
  color: #fff;
}

.workshop-banner p {
  margin: 0;
  font-size: 13px;
  opacity: 0.65;
}

.workshop-tabs-card {
  border-radius: 8px;
}

.workshop-tabs-card :deep(.el-card__body) {
  padding: 0;
}

.tab-label {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
}

.tab-content {
  padding: 20px;
  min-height: 500px;
}
</style>
