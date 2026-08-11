<template>
  <ErrorBoundary>
    <div class="component-lab">
      <!-- ===== Section 1: Renderless Components ===== -->
      <el-card shadow="hover" class="lab-section">
        <template #header>
          <span class="lab-section-title">🔌 Renderless 组件模式</span>
          <el-tag size="small" type="info">Scoped Slots</el-tag>
        </template>
        <el-row :gutter="20">
          <!-- Mouse Tracker -->
          <el-col :span="12">
            <div class="lab-demo-box">
              <h4>MouseTracker — 鼠标追踪器</h4>
              <p class="lab-desc">Renderless 组件只提供逻辑，UI 完全由父组件决定</p>
              <MouseTracker v-slot="{ x, y, isMoving }">
                <div class="mouse-display" :class="{ moving: isMoving }">
                  <div class="mouse-coord">🖱️ X: {{ x }}, Y: {{ y }}</div>
                  <div class="mouse-status">{{ isMoving ? '移动中...' : '静止' }}</div>
                </div>
              </MouseTracker>
            </div>
          </el-col>

          <!-- Filtered List -->
          <el-col :span="12">
            <div class="lab-demo-box">
              <h4>FilteredList — 列表过滤</h4>
              <p class="lab-desc">过滤逻辑封装在 renderless 组件中，父组件只负责渲染</p>
              <FilteredList :items="labItems" v-slot="{ filtered, query, setQuery }">
                <el-input :model-value="query" @update:model-value="setQuery" placeholder="输入过滤关键词..." size="small" clearable class="mb8" />
                <div class="filter-result">
                  <el-tag v-for="item in filtered" :key="item" size="small" class="mr4 mb4" round>
                    {{ item }}
                  </el-tag>
                  <div v-if="filtered.length === 0" class="empty-hint">无匹配项</div>
                </div>
              </FilteredList>
            </div>
          </el-col>
        </el-row>
      </el-card>

      <!-- ===== Section 2: Compound Components ===== -->
      <el-card shadow="hover" class="lab-section">
        <template #header>
          <span class="lab-section-title">🧩 Compound Components (复合组件)</span>
          <el-tag size="small" type="success">Provide/Inject</el-tag>
        </template>
        <div class="lab-demo-box">
          <p class="lab-desc">TabContainer 通过 Provide/Inject 与子组件通信，无需 props drilling</p>
          <LabTabs v-model="activeLabTab">
            <LabTab name="reactivity" label="响应式">
              <div class="tab-demo-content">
                <p>✨ Vue 3 响应式系统基于 Proxy，支持深层响应。</p>
                <el-input v-model="reactiveDemo.text" placeholder="输入试试..." size="small" />
                <p class="mt8">你输入了: <b>{{ reactiveDemo.text }}</b> (长度: {{ reactiveDemo.text.length }})</p>
              </div>
            </LabTab>
            <LabTab name="composition" label="组合式 API">
              <div class="tab-demo-content">
                <p>🔧 Composition API 让代码按逻辑关注点组织，而非选项。</p>
                <p>计数演示: <b>{{ compCounter }}</b></p>
                <el-button size="small" @click="compCounter++">+1</el-button>
                <el-button size="small" @click="compCounter = 0">重置</el-button>
              </div>
            </LabTab>
            <LabTab name="teleport" label="Teleport">
              <div class="tab-demo-content">
                <p>🚀 Teleport 将组件渲染到 DOM 任意位置。</p>
                <p class="lab-desc">（见下方 Teleport 演示区）</p>
              </div>
            </LabTab>
          </LabTabs>
        </div>
      </el-card>

      <!-- ===== Section 3: Teleport + Provide/Inject ===== -->
      <el-card shadow="hover" class="lab-section">
        <template #header>
          <span class="lab-section-title">📍 Teleport 传送门</span>
          <el-tag size="small" type="warning">Teleport</el-tag>
        </template>
        <div class="lab-demo-box">
          <p class="lab-desc">点击下方按钮，弹窗将被 Teleport 到 &lt;body&gt; 底部，避开 overflow:hidden 等限制</p>
          <el-button type="primary" @click="teleportVisible = true">打开 Teleport 弹窗</el-button>
          <Teleport to="body">
            <Transition name="modal">
              <div v-if="teleportVisible" class="teleport-overlay" @click.self="teleportVisible = false">
                <div class="teleport-modal">
                  <h3>📦 这是通过 Teleport 渲染的弹窗</h3>
                  <p>检查 DOM 树 — 我在 &lt;body&gt; 的最底部！</p>
                  <p class="lab-desc">Teleport 解决了 z-index 和 overflow 上下文陷阱</p>
                  <el-button @click="teleportVisible = false">关闭</el-button>
                </div>
              </div>
            </Transition>
          </Teleport>
        </div>
      </el-card>

      <!-- ===== Section 4: Suspense + Skeleton ===== -->
      <el-card shadow="hover" class="lab-section">
        <template #header>
          <span class="lab-section-title">⏳ Suspense + 骨架屏</span>
          <el-tag size="small" type="danger">Experimental</el-tag>
        </template>
        <div class="lab-demo-box">
          <p class="lab-desc">异步组件加载时自动显示骨架屏，加载完成后揭示内容</p>
          <el-button type="primary" :icon="Refresh" @click="reloadAsync" :loading="loadingAsync">
            重新加载异步组件
          </el-button>
          <div class="suspense-demo mt12">
            <Suspense v-if="asyncKey" @resolve="loadingAsync = false" @pending="loadingAsync = true">
              <template #default>
                <AsyncDemoCard :key="asyncKey" />
              </template>
              <template #fallback>
                <SkeletonLoader variant="card" :count="1" />
              </template>
            </Suspense>
          </div>
        </div>
      </el-card>

      <!-- ===== Section 5: Dynamic Component + Schema Render ===== -->
      <el-card shadow="hover" class="lab-section">
        <template #header>
          <span class="lab-section-title">🎛️ 动态组件 & Schema 驱动</span>
          <el-tag size="small" type="info">component :is</el-tag>
        </template>
        <div class="lab-demo-box">
          <p class="lab-desc">根据 JSON Schema 在运行时动态渲染不同类型的组件</p>
          <div class="schema-renderer">
            <div v-for="(field, i) in dynamicSchema" :key="i" class="schema-field">
              <label class="schema-label">{{ field.label }}</label>
              <component
                :is="fieldComponent(field.type)"
                v-model="dynamicData[field.key]"
                v-bind="field.props || {}"
              />
            </div>
          </div>
          <div class="schema-result mt12">
            <el-tag>实时数据: {{ JSON.stringify(dynamicData) }}</el-tag>
          </div>
        </div>
      </el-card>
    </div>
  </ErrorBoundary>
</template>

<script setup>
import { ref, reactive, h, computed, defineAsyncComponent, shallowRef } from 'vue'
import { Refresh } from '@element-plus/icons-vue'
import { ElInput, ElSelect, ElOption, ElSwitch, ElSlider } from 'element-plus'
import ErrorBoundary from '@/components/ErrorBoundary.vue'
import SkeletonLoader from '@/components/SkeletonLoader.vue'

// ==============================
// Renderless Components (defined inline for demo)
// ==============================

// MouseTracker — Renderless
const MouseTracker = {
  setup(props, { slots }) {
    const x = ref(0)
    const y = ref(0)
    const isMoving = ref(false)
    let timer = null

    function onMove(e) {
      x.value = e.clientX
      y.value = e.clientY
      isMoving.value = true
      clearTimeout(timer)
      timer = setTimeout(() => { isMoving.value = false }, 300)
    }

    // Setup/teardown
    if (typeof window !== 'undefined') {
      window.addEventListener('mousemove', onMove)
    }

    return () => slots.default?.({ x: x.value, y: y.value, isMoving: isMoving.value })
  }
}

// FilteredList — Renderless
const FilteredList = {
  props: { items: Array },
  setup(props, { slots }) {
    const query = ref('')
    const filtered = computed(() =>
      !query.value
        ? props.items
        : props.items.filter(item => item.toLowerCase().includes(query.value.toLowerCase()))
    )
    function setQuery(val) { query.value = val }
    return () => slots.default?.({ filtered: filtered.value, query, setQuery })
  }
}

// ==============================
// Compound Components (LabTabs / LabTab)
// ==============================
import { provide, inject, onMounted } from 'vue'

const TabSymbol = Symbol('lab-tabs')

const LabTabs = {
  props: { modelValue: String },
  emits: ['update:modelValue'],
  setup(props, { emit, slots }) {
    const activeName = computed({
      get: () => props.modelValue,
      set: (v) => emit('update:modelValue', v)
    })
    const tabs = reactive([])

    provide(TabSymbol, { activeName, tabs })

    function selectTab(name) {
      activeName.value = name
    }

    return () => {
      const tabDefs = slots.default?.() || []
      // Extract tab defs from VNodes (simple approach)
      return h('div', { class: 'lab-tabs' }, [
        h('div', { class: 'lab-tab-header' },
          tabs.map(tab =>
            h('button', {
              key: tab.name,
              class: ['lab-tab-btn', { active: activeName.value === tab.name }],
              onClick: () => selectTab(tab.name)
            }, tab.label)
          )
        ),
        h('div', { class: 'lab-tab-panel' }, tabDefs)
      ])
    }
  }
}

const LabTab = {
  props: { name: String, label: String },
  setup(props, { slots }) {
    const { activeName, tabs } = inject(TabSymbol)
    tabs.push({ name: props.name, label: props.label })

    return () => {
      if (activeName.value !== props.name) return null
      return h('div', { class: 'lab-tab-content' }, slots.default?.())
    }
  }
}

// ==============================
// Suspense Async Component
// ==============================
const AsyncDemoCard = defineAsyncComponent(() =>
  new Promise((resolve) => {
    setTimeout(() => {
      resolve({
        setup() {
          return () => h('div', { class: 'async-loaded-card' }, [
            h('span', { style: 'font-size:24px' }, '🎉'),
            h('h4', '异步组件加载完成！'),
            h('p', { class: 'lab-desc' }, '这个组件模拟了 1.5 秒的网络延迟加载。Suspense 在等待时渲染了骨架屏。'),
            h('p', { class: 'lab-desc' }, '时间戳: ' + new Date().toLocaleTimeString('zh-CN'))
          ])
        }
      })
    }, 1500)
  })
)

// ==============================
// Schema-driven Dynamic Components
// ==============================
const dynamicSchema = [
  { key: 'name', label: '名称', type: 'input', props: { placeholder: '输入名称', size: 'small' } },
  { key: 'category', label: '分类', type: 'select', props: { placeholder: '选择分类', size: 'small' } },
  { key: 'enabled', label: '启用', type: 'switch', props: { size: 'small' } },
  { key: 'volume', label: '音量', type: 'slider', props: { min: 0, max: 100, size: 'small' } },
]

const dynamicData = reactive({
  name: '',
  category: '',
  enabled: false,
  volume: 50
})

function fieldComponent(type) {
  const map = {
    input: ElInput,
    select: ElSelect,
    switch: ElSwitch,
    slider: ElSlider
  }
  const comp = map[type]
  if (type === 'select') {
    return {
      ...comp,
      setup(props, { attrs }) {
        return () => h(comp, attrs, {
          default: () => [
            h(ElOption, { value: 'tech', label: '技术' }),
            h(ElOption, { value: 'design', label: '设计' }),
            h(ElOption, { value: 'product', label: '产品' }),
          ]
        })
      }
    }
  }
  return comp
}

// ==============================
// Page State
// ==============================
const labItems = ['Vue 3', 'Composition API', 'Renderless', 'Teleport', 'Suspense', 'KeepAlive',
  'Provide/Inject', 'Custom Directive', 'Virtual Scroll', 'Transition', 'Pinia', 'Vite',
  'TypeScript', 'JSX/TSX', 'SSR', 'Reactivity']

const activeLabTab = ref('reactivity')
const reactiveDemo = reactive({ text: '' })
const compCounter = ref(0)
const teleportVisible = ref(false)
const loadingAsync = ref(false)
const asyncKey = ref(Date.now())

function reloadAsync() {
  asyncKey.value = Date.now()
}
</script>

<style scoped>
.component-lab {
  max-width: 1100px;
}

.lab-section {
  margin-bottom: 16px;
}

.lab-section-title {
  font-weight: 600;
}

.lab-section :deep(.el-card__header) {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.lab-demo-box {
  padding: 12px 0;
}

.lab-demo-box h4 {
  margin: 0 0 4px;
  font-size: 15px;
  color: #303133;
}

.lab-desc {
  font-size: 12px;
  color: #909399;
  margin: 0 0 12px;
}

.mb8 { margin-bottom: 8px; }
.mt8 { margin-top: 8px; }
.mt12 { margin-top: 12px; }
.mr4 { margin-right: 4px; }
.mb4 { margin-bottom: 4px; }

/* Mouse Tracker */
.mouse-display {
  padding: 24px;
  text-align: center;
  background: #f5f7fa;
  border-radius: 8px;
  transition: background 0.3s;
}

.mouse-display.moving {
  background: #e6f7ff;
}

.mouse-coord {
  font-size: 18px;
  font-weight: 700;
  color: #303133;
  font-variant-numeric: tabular-nums;
}

.mouse-status {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

/* Filter */
.filter-result {
  display: flex;
  flex-wrap: wrap;
  min-height: 36px;
}

.empty-hint {
  color: #c0c4cc;
  font-size: 13px;
}

/* Compound Tabs */
.lab-tab-header {
  display: flex;
  gap: 4px;
  border-bottom: 2px solid #ebeef5;
  margin-bottom: 12px;
}

.lab-tab-btn {
  padding: 8px 20px;
  border: none;
  background: transparent;
  cursor: pointer;
  font-size: 14px;
  color: #606266;
  border-bottom: 2px solid transparent;
  margin-bottom: -2px;
  transition: all 0.2s;
}

.lab-tab-btn.active {
  color: var(--color-primary);
  border-bottom-color: var(--color-primary);
  font-weight: 600;
}

.lab-tab-btn:hover {
  color: var(--color-primary);
}

.lab-tab-content {
  padding: 12px 0;
}

.tab-demo-content p {
  margin: 0 0 8px;
  color: #606266;
}

/* Teleport */
.teleport-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.45);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
}

.teleport-modal {
  background: #fff;
  border-radius: 12px;
  padding: 32px;
  max-width: 420px;
  width: 90%;
  text-align: center;
  box-shadow: 0 20px 60px rgba(0,0,0,0.3);
}

.teleport-modal h3 {
  margin: 0 0 12px;
  color: #303133;
}

.teleport-modal p {
  margin: 0 0 8px;
  color: #606266;
}

/* Modal transition */
.modal-enter-active {
  transition: opacity 0.3s ease;
}

.modal-enter-active .teleport-modal {
  transition: transform 0.3s ease, opacity 0.3s ease;
}

.modal-leave-active {
  transition: opacity 0.2s ease;
}

.modal-leave-active .teleport-modal {
  transition: transform 0.2s ease, opacity 0.2s ease;
}

.modal-enter-from {
  opacity: 0;
}

.modal-enter-from .teleport-modal {
  transform: scale(0.9) translateY(20px);
  opacity: 0;
}

.modal-leave-to {
  opacity: 0;
}

/* Schema Renderer */
.schema-renderer {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  align-items: flex-end;
}

.schema-field {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.schema-label {
  font-size: 12px;
  color: #909399;
  font-weight: 500;
}

.schema-result {
  padding: 8px 12px;
  background: #fafafa;
  border-radius: 6px;
  font-family: monospace;
}

/* Async loaded card */
.async-loaded-card {
  padding: 24px;
  text-align: center;
  background: linear-gradient(135deg, #f6ffed, #e6f7ff);
  border-radius: 8px;
  border: 2px solid #d9f7be;
}

.async-loaded-card h4 {
  margin: 8px 0;
}

/* Dark mode */
html.dark .lab-demo-box h4,
html.dark .tab-demo-content p,
html.dark .teleport-modal h3,
html.dark .teleport-modal p {
  color: #e0e0e0;
}

html.dark .mouse-display {
  background: #2a2a2a;
}

html.dark .mouse-display.moving {
  background: #1a3a5c;
}

html.dark .mouse-coord {
  color: #e0e0e0;
}

html.dark .lab-tab-header {
  border-bottom-color: #333;
}

html.dark .lab-tab-btn {
  color: #aaa;
}

html.dark .teleport-modal {
  background: #1f1f1f;
}

html.dark .async-loaded-card {
  background: linear-gradient(135deg, #1a2a1a, #1a2a3a);
  border-color: #2a4a2a;
}

html.dark .schema-result {
  background: #2a2a2a;
}

html.dark .lab-tab-btn:hover,
html.dark .lab-tab-btn.active {
  color: var(--color-primary);
}
</style>
