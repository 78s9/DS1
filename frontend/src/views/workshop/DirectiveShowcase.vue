<template>
  <ErrorBoundary>
    <div class="directive-showcase">
      <!-- ===== v-ripple ===== -->
      <el-card shadow="hover" class="ds-section">
        <template #header>
          <span class="ds-title">💧 v-ripple — Material 波纹效果</span>
          <el-tag size="small" type="primary">v-ripple</el-tag>
        </template>
        <div class="ds-demo">
          <div class="ds-demo-row">
            <div v-ripple class="ripple-box ripple-default">
              <span>默认波纹 (白色)</span>
            </div>
            <div v-ripple="'rgba(64, 158, 255, 0.4)'" class="ripple-box ripple-blue">
              <span>蓝色波纹</span>
            </div>
            <div v-ripple:center="'rgba(245, 108, 108, 0.4)'" class="ripple-box ripple-red">
              <span>居中波纹 (红色)</span>
            </div>
            <div v-ripple class="ripple-box ripple-dark">
              <span>暗色背景波纹</span>
            </div>
          </div>
          <div class="ds-code-hint">
            <code>v-ripple</code> | <code>v-ripple="'#409EFF'"</code> | <code>v-ripple:center</code>
          </div>
        </div>
      </el-card>

      <!-- ===== v-click-outside ===== -->
      <el-card shadow="hover" class="ds-section">
        <template #header>
          <span class="ds-title">🎯 v-click-outside — 外部点击检测</span>
          <el-tag size="small" type="success">v-click-outside</el-tag>
        </template>
        <div class="ds-demo">
          <div
            v-click-outside="closeDropdown"
            class="click-outside-demo"
          >
            <el-button type="primary" @click="dropdownOpen = !dropdownOpen">
              {{ dropdownOpen ? '点击外部关闭' : '点击打开下拉' }}
            </el-button>
            <Transition name="dropdown">
              <div v-if="dropdownOpen" class="dropdown-menu" @click.stop>
                <div class="dropdown-item" @click="pickItem('选项 A')">📌 选项 A</div>
                <div class="dropdown-item" @click="pickItem('选项 B')">📎 选项 B</div>
                <div class="dropdown-item" @click="pickItem('选项 C')">📋 选项 C</div>
              </div>
            </Transition>
          </div>
          <p v-if="selectedItem" class="ds-result">
            已选择: <b>{{ selectedItem }}</b>
          </p>
          <div class="ds-code-hint">
            <code>v-click-outside="handler"</code> — 点击绑定元素外部时触发 handler
          </div>
        </div>
      </el-card>

      <!-- ===== v-intersect ===== -->
      <el-card shadow="hover" class="ds-section">
        <template #header>
          <span class="ds-title">👁️ v-intersect — 可见性检测</span>
          <el-tag size="small" type="warning">v-intersect</el-tag>
        </template>
        <div class="ds-demo">
          <p class="ds-desc">基于 IntersectionObserver API，滚动下方彩色卡片查看曝光追踪效果：</p>
          <div class="intersect-scroll">
            <div
              v-for="card in intersectCards"
              :key="card.id"
              v-intersect="{
                onEnter: () => onCardEnter(card),
                onLeave: () => onCardLeave(card),
                threshold: 0.5
              }"
              class="intersect-card"
              :class="{ 'card-visible': card.visible }"
              :style="{ background: card.bg }"
            >
              <span class="card-number">{{ card.id }}</span>
              <span class="card-status">{{ card.visible ? '👁️ 可见' : '🙈 隐藏' }}</span>
              <span class="card-count">进入次数: {{ card.enterCount }}</span>
            </div>
          </div>
          <div class="intersect-log">
            <span>📊 可见卡片:</span>
            <el-tag v-for="id in visibleCardIds" :key="id" size="small" type="success" class="mr4">
              #{{ id }}
            </el-tag>
            <span v-if="visibleCardIds.length === 0" class="empty-hint">滚动查看卡片...</span>
          </div>
          <div class="ds-code-hint">
            <code>v-intersect="{ onEnter, onLeave, threshold: 0.5 }"</code>
          </div>
        </div>
      </el-card>

      <!-- ===== v-autofocus ===== -->
      <el-card shadow="hover" class="ds-section">
        <template #header>
          <span class="ds-title">⌨️ v-autofocus — 自动聚焦</span>
          <el-tag size="small" type="danger">v-autofocus</el-tag>
        </template>
        <div class="ds-demo">
          <div class="ds-demo-row">
            <div v-if="showAutofocus" class="autofocus-demo">
              <el-input
                v-autofocus:select
                placeholder="这个输入框会自动聚焦并选中文字..."
                :model-value="'选中这段文字试试'"
                size="default"
                style="width: 320px"
              />
              <p class="ds-desc mt8">✅ 输入框已自动聚焦 (v-autofocus:select)</p>
            </div>
            <div v-else>
              <el-button type="primary" @click="showAutofocus = true">
                展示自动聚焦输入框
              </el-button>
            </div>
          </div>
          <div class="ds-code-hint">
            <code>v-autofocus</code> — 挂载后聚焦 |
            <code>v-autofocus:select</code> — 聚焦并选中 |
            <code>v-autofocus:delay="500"</code> — 延迟聚焦
          </div>
        </div>
      </el-card>

      <!-- ===== v-draggable ===== -->
      <el-card shadow="hover" class="ds-section">
        <template #header>
          <span class="ds-title">↕️ v-draggable — 拖拽排序</span>
          <el-tag size="small" type="info">v-draggable</el-tag>
        </template>
        <div class="ds-demo">
          <p class="ds-desc">拖拽下方列表项进行排序（原生 HTML5 Drag & Drop API）：</p>
          <div
            v-draggable="{ list: dragList, onReorder: onDragReorder }"
            class="drag-list"
          >
            <div
              v-for="(item, idx) in dragList"
              :key="item"
              :data-drag-item="idx"
              class="drag-item"
              draggable="true"
            >
              <span class="drag-handle">⋮⋮</span>
              <span class="drag-label">{{ item }}</span>
              <span class="drag-idx">#{{ idx + 1 }}</span>
            </div>
          </div>
          <p class="ds-result mt8" v-if="dragLog">↻ 排序变更: {{ dragLog }}</p>
          <div class="ds-code-hint">
            <code>v-draggable="{ list, onReorder }"</code> — 需配合 <code>draggable="true"</code> 和 <code>data-drag-item</code>
          </div>
        </div>
      </el-card>
    </div>
  </ErrorBoundary>
</template>

<script setup>
import { ref, reactive } from 'vue'
import ErrorBoundary from '@/components/ErrorBoundary.vue'

// ===== v-click-outside =====
const dropdownOpen = ref(false)
const selectedItem = ref('')

function closeDropdown() {
  dropdownOpen.value = false
}

function pickItem(item) {
  selectedItem.value = item
  dropdownOpen.value = false
}

// ===== v-intersect =====
const intersectCards = reactive(
  Array.from({ length: 12 }, (_, i) => ({
    id: i + 1,
    bg: `hsl(${i * 30}, 60%, 75%)`,
    visible: false,
    enterCount: 0
  }))
)

const visibleCardIds = ref([])

function onCardEnter(card) {
  card.visible = true
  card.enterCount++
  if (!visibleCardIds.value.includes(card.id)) {
    visibleCardIds.value.push(card.id)
  }
}

function onCardLeave(card) {
  card.visible = false
  visibleCardIds.value = visibleCardIds.value.filter(id => id !== card.id)
}

// ===== v-autofocus =====
const showAutofocus = ref(true)

// ===== v-draggable =====
const dragList = reactive([
  'Apple', 'Banana', 'Cherry', 'Date', 'Elderberry',
  'Fig', 'Grape', 'Honeydew', 'Kiwi', 'Lemon'
])
const dragLog = ref('')

function onDragReorder(newList) {
  dragLog.value = newList.join(' → ')
}
</script>

<style scoped>
.directive-showcase {
  max-width: 1000px;
}

.ds-section {
  margin-bottom: 16px;
}

.ds-section :deep(.el-card__header) {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.ds-title {
  font-weight: 600;
}

.ds-demo {
  padding: 8px 0;
}

.ds-demo-row {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  margin-bottom: 12px;
  align-items: center;
}

.ds-desc {
  font-size: 13px;
  color: #909399;
  margin: 0 0 12px;
}

.ds-result {
  font-size: 14px;
  color: #303133;
  margin: 8px 0;
}

.ds-code-hint {
  margin-top: 10px;
  padding: 8px 12px;
  background: #fafafa;
  border-radius: 6px;
  font-size: 12px;
  color: #909399;
}

.ds-code-hint code {
  background: #f0f0f0;
  padding: 2px 6px;
  border-radius: 3px;
  color: #e6a23c;
  font-family: 'Courier New', monospace;
}

/* ===== Ripple ===== */
.ripple-box {
  width: 150px;
  height: 100px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  font-size: 13px;
  font-weight: 500;
  user-select: none;
  transition: box-shadow 0.2s;
  position: relative;
  overflow: hidden;
}

.ripple-default {
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: #fff;
}

.ripple-blue {
  background: #e6f7ff;
  color: #1890ff;
  border: 2px solid #91d5ff;
}

.ripple-red {
  background: #fff1f0;
  color: #f56c6c;
  border: 2px solid #ffa39e;
}

.ripple-dark {
  background: #1a1a2e;
  color: #e0e0e0;
  border: 1px solid #333;
}

/* ===== Click Outside Dropdown ===== */
.click-outside-demo {
  position: relative;
  display: inline-block;
}

.dropdown-menu {
  position: absolute;
  top: calc(100% + 8px);
  left: 0;
  background: #fff;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  box-shadow: 0 6px 20px rgba(0,0,0,0.1);
  min-width: 160px;
  z-index: 100;
  overflow: hidden;
}

.dropdown-item {
  padding: 10px 16px;
  cursor: pointer;
  font-size: 13px;
  color: #303133;
  transition: background 0.15s;
}

.dropdown-item:hover {
  background: #f5f7fa;
}

/* Dropdown transition */
.dropdown-enter-active {
  transition: opacity 0.2s, transform 0.2s;
}

.dropdown-leave-active {
  transition: opacity 0.15s, transform 0.15s;
}

.dropdown-enter-from {
  opacity: 0;
  transform: translateY(-6px);
}

.dropdown-leave-to {
  opacity: 0;
  transform: translateY(-4px);
}

/* ===== Intersect Scroll ===== */
.intersect-scroll {
  max-height: 280px;
  overflow-y: auto;
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  padding: 8px;
  border: 1px solid #f0f0f0;
  border-radius: 8px;
  margin-bottom: 12px;
}

.intersect-card {
  width: calc(50% - 5px);
  min-height: 100px;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 4px;
  color: #fff;
  transition: transform 0.3s, box-shadow 0.3s, opacity 0.3s;
  opacity: 0.5;
}

.intersect-card.card-visible {
  opacity: 1;
  transform: scale(1.02);
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
}

.card-number {
  font-size: 22px;
  font-weight: 700;
}

.card-status {
  font-size: 12px;
}

.card-count {
  font-size: 11px;
  opacity: 0.8;
}

.intersect-log {
  font-size: 13px;
  color: #606266;
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 6px;
}

.mr4 { margin-right: 4px; }
.mt8 { margin-top: 8px; }

.empty-hint {
  color: #c0c4cc;
  font-size: 12px;
}

/* ===== Autofocus ===== */
.autofocus-demo {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

/* ===== Drag List ===== */
.drag-list {
  border: 1px solid #ebeef5;
  border-radius: 8px;
  overflow: hidden;
  max-width: 360px;
}

.drag-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 14px;
  background: #fff;
  border-bottom: 1px solid #f5f5f5;
  cursor: grab;
  transition: background 0.15s, opacity 0.2s;
  user-select: none;
}

.drag-item:last-child {
  border-bottom: none;
}

.drag-item:hover {
  background: #fafafa;
}

.drag-item.dragging {
  opacity: 0.5;
  background: #e6f7ff;
}

.drag-item.drag-over {
  border-top: 2px solid var(--color-primary);
}

.drag-handle {
  color: #c0c4cc;
  font-size: 18px;
  letter-spacing: -2px;
  cursor: grab;
}

.drag-label {
  flex: 1;
  font-size: 14px;
  color: #303133;
}

.drag-idx {
  font-size: 11px;
  color: #c0c4cc;
}

/* Dark mode */
html.dark .ds-code-hint {
  background: #2a2a2a;
}

html.dark .ds-code-hint code {
  background: #333;
  color: #e6a23c;
}

html.dark .ds-result {
  color: #e0e0e0;
}

html.dark .intersect-scroll {
  border-color: #333;
}

html.dark .dropdown-menu {
  background: #1f1f1f;
  border-color: #333;
}

html.dark .dropdown-item {
  color: #e0e0e0;
}

html.dark .dropdown-item:hover {
  background: #2a2a2a;
}

html.dark .drag-list {
  border-color: #333;
}

html.dark .drag-item {
  background: #1f1f1f;
  border-bottom-color: #333;
}

html.dark .drag-item:hover {
  background: #2a2a2a;
}

html.dark .drag-item.dragging {
  background: #1a3a5c;
}

html.dark .drag-label {
  color: #e0e0e0;
}

html.dark .ripple-dark {
  background: #333;
  border-color: #444;
}
</style>
