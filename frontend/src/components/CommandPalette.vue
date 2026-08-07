<template>
  <Teleport to="body">
    <transition name="cmd-fade">
      <div v-if="visible" class="cmd-overlay" @click.self="close">
        <div class="cmd-panel">
          <!-- Search Input -->
          <div class="cmd-search">
            <el-icon :size="18" color="#909399"><Search /></el-icon>
            <input
              ref="inputRef"
              v-model="query"
              type="text"
              placeholder="输入命令搜索..."
              class="cmd-input"
              @keydown="onKeydown"
            />
            <kbd class="cmd-hint">ESC</kbd>
          </div>

          <!-- Results -->
          <div class="cmd-results" v-if="filtered.length">
            <div
              v-for="(group, gIdx) in groupedResults"
              :key="gIdx"
              class="cmd-group"
            >
              <div class="cmd-group-label">{{ group.label }}</div>
              <div
                v-for="(item, idx) in group.items"
                :key="item.id"
                class="cmd-item"
                :class="{ active: activeIndex === getFlatIndex(group, gIdx, idx) }"
                @click="execute(item)"
                @mouseenter="activeIndex = getFlatIndex(group, gIdx, idx)"
              >
                <span class="cmd-icon">{{ item.icon }}</span>
                <div class="cmd-body">
                  <span class="cmd-title">{{ item.title }}</span>
                  <span class="cmd-desc">{{ item.desc }}</span>
                </div>
                <kbd v-if="item.shortcut" class="cmd-shortcut">{{ item.shortcut }}</kbd>
              </div>
            </div>
          </div>

          <!-- Empty State -->
          <div v-else class="cmd-empty">
            <div class="cmd-empty-icon">🔍</div>
            <p>未找到匹配的命令</p>
          </div>

          <!-- Footer -->
          <div class="cmd-footer">
            <span><kbd>↑↓</kbd> 导航</span>
            <span><kbd>Enter</kbd> 执行</span>
            <span><kbd>Esc</kbd> 关闭</span>
          </div>
        </div>
      </div>
    </transition>
  </Teleport>
</template>

<script setup>
import { ref, computed, watch, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/store/auth'
import { useThemeStore } from '@/store/theme'
import { Search } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const authStore = useAuthStore()
const themeStore = useThemeStore()

const visible = ref(false)
const query = ref('')
const activeIndex = ref(0)
const inputRef = ref(null)

// ===== Commands =====
const commands = computed(() => [
  // Navigation
  { id: 'nav-home', group: '导航', icon: '🏠', title: '首页仪表盘', desc: '返回控制台首页', action: () => router.push('/dashboard') },
  { id: 'nav-users', group: '导航', icon: '👥', title: '用户管理', desc: '查看和管理系统用户', action: () => router.push('/dashboard/users') },
  { id: 'nav-settings', group: '导航', icon: '⚙️', title: '系统设置', desc: '系统配置与信息', action: () => router.push('/dashboard/settings') },

  // Actions
  { id: 'act-dark', group: '快捷操作', icon: themeStore.isDark ? '☀️' : '🌙', title: themeStore.isDark ? '切换亮色模式' : '切换暗黑模式', desc: '目前：' + (themeStore.isDark ? '暗黑' : '亮色'), shortcut: 'Ctrl+D', action: () => themeStore.toggleDark() },
  { id: 'act-fullscreen', group: '快捷操作', icon: '🖥️', title: '全屏模式', desc: '切换浏览器全屏', action: toggleFs },
  { id: 'act-logout', group: '快捷操作', icon: '🚪', title: '退出登录', desc: '安全退出当前账户', action: logout },
  { id: 'act-refresh', group: '快捷操作', icon: '🔄', title: '刷新数据', desc: '重新加载仪表盘数据', action: () => location.reload() },

  // Fun
  { id: 'fun-about', group: '关于', icon: '🚀', title: '关于 DS1', desc: '全栈用户认证系统 v1.0', action: () => ElMessage.success('DS1 — Vue 3 + Spring Boot 全栈项目 🎉') },
  { id: 'fun-hello', group: '关于', icon: '👋', title: '打个招呼', desc: '和 DS1 说 Hello', action: () => ElMessage('👋 你好！欢迎使用 DS1 管理系统~') },
])

const flatCommands = computed(() => commands.value)

// ===== Filter =====
const filtered = computed(() => {
  if (!query.value.trim()) return flatCommands.value
  const q = query.value.toLowerCase()
  return flatCommands.value.filter(c =>
    c.title.toLowerCase().includes(q) ||
    c.desc.toLowerCase().includes(q) ||
    c.group.toLowerCase().includes(q)
  )
})

// Group results
const groupedResults = computed(() => {
  const groups = {}
  for (const item of filtered.value) {
    if (!groups[item.group]) groups[item.group] = { label: item.group, items: [] }
    groups[item.group].items.push(item)
  }
  return Object.values(groups)
})

function getFlatIndex(group, gIdx, idx) {
  let count = 0
  for (let i = 0; i < gIdx; i++) {
    count += groupedResults.value[i].items.length
  }
  return count + idx
}

// ===== Keyboard =====
function onKeydown(e) {
  const total = filtered.value.length
  if (e.key === 'ArrowDown') {
    e.preventDefault()
    activeIndex.value = (activeIndex.value + 1) % total
  } else if (e.key === 'ArrowUp') {
    e.preventDefault()
    activeIndex.value = (activeIndex.value - 1 + total) % total
  } else if (e.key === 'Enter') {
    e.preventDefault()
    const item = filtered.value[activeIndex.value]
    if (item) execute(item)
  } else if (e.key === 'Escape') {
    close()
  }
}

function execute(item) {
  close()
  item.action()
}

function toggleFs() {
  if (!document.fullscreenElement) {
    document.documentElement.requestFullscreen()
  } else {
    document.exitFullscreen()
  }
}

function logout() {
  authStore.logout()
  router.push('/login')
  ElMessage.success('已退出登录')
}

// ===== Open / Close =====
function open() {
  visible.value = true
  query.value = ''
  activeIndex.value = 0
  // Focus input after transition
  setTimeout(() => inputRef.value?.focus(), 150)
}

function close() {
  visible.value = false
}

// ===== Global Shortcut =====
function onGlobalKeydown(e) {
  // Ctrl+K or Cmd+K → open palette
  if ((e.ctrlKey || e.metaKey) && e.key === 'k') {
    e.preventDefault()
    open()
  }
  // Ctrl+D → toggle dark mode
  if ((e.ctrlKey || e.metaKey) && e.key === 'd') {
    e.preventDefault()
    themeStore.toggleDark()
    ElMessage.success(themeStore.isDark ? '🌙 已切换暗黑模式' : '☀️ 已切换亮色模式')
  }
}

onMounted(() => {
  window.addEventListener('keydown', onGlobalKeydown)
})

onUnmounted(() => {
  window.removeEventListener('keydown', onGlobalKeydown)
})

// Expose for manual trigger
defineExpose({ open, close })
</script>

<style scoped>
/* ===== Overlay ===== */
.cmd-overlay {
  position: fixed;
  inset: 0;
  z-index: 9999;
  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: flex-start;
  justify-content: center;
  padding-top: 15vh;
}

/* ===== Panel ===== */
.cmd-panel {
  width: 560px;
  max-width: 92vw;
  max-height: 60vh;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 25px 60px rgba(0, 0, 0, 0.3);
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

html.dark .cmd-panel {
  background: #1f1f1f;
  box-shadow: 0 25px 60px rgba(0, 0, 0, 0.6);
}

/* ===== Search ===== */
.cmd-search {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 16px 20px;
  border-bottom: 1px solid #ebeef5;
}

html.dark .cmd-search {
  border-bottom-color: #333;
}

.cmd-input {
  flex: 1;
  border: none;
  outline: none;
  font-size: 16px;
  color: #303133;
  background: transparent;
  font-family: inherit;
}

html.dark .cmd-input {
  color: #e5e5e5;
}

.cmd-input::placeholder {
  color: #c0c4cc;
}

.cmd-hint {
  font-size: 11px;
  padding: 2px 6px;
  border-radius: 4px;
  background: #f0f2f5;
  color: #909399;
  font-family: monospace;
}

html.dark .cmd-hint {
  background: #333;
}

/* ===== Results ===== */
.cmd-results {
  flex: 1;
  overflow-y: auto;
  padding: 8px;
  max-height: 360px;
}

.cmd-group-label {
  font-size: 12px;
  font-weight: 600;
  color: #909399;
  padding: 8px 12px 4px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.cmd-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.15s;
}

.cmd-item:hover,
.cmd-item.active {
  background: #f0f2f5;
}

html.dark .cmd-item:hover,
html.dark .cmd-item.active {
  background: #2a2a2a;
}

.cmd-icon {
  font-size: 18px;
  flex-shrink: 0;
  width: 24px;
  text-align: center;
}

.cmd-body {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.cmd-title {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
}

html.dark .cmd-title {
  color: #e5e5e5;
}

.cmd-desc {
  font-size: 12px;
  color: #909399;
  margin-top: 1px;
}

.cmd-shortcut {
  font-size: 11px;
  padding: 2px 6px;
  border-radius: 4px;
  background: #f0f2f5;
  color: #909399;
  font-family: monospace;
  flex-shrink: 0;
}

html.dark .cmd-shortcut {
  background: #333;
}

/* ===== Empty ===== */
.cmd-empty {
  text-align: center;
  padding: 40px 20px;
  color: #909399;
}

.cmd-empty-icon {
  font-size: 40px;
  margin-bottom: 12px;
}

.cmd-empty p {
  margin: 0;
  font-size: 14px;
}

/* ===== Footer ===== */
.cmd-footer {
  display: flex;
  gap: 16px;
  padding: 10px 20px;
  border-top: 1px solid #ebeef5;
  font-size: 12px;
  color: #909399;
}

html.dark .cmd-footer {
  border-top-color: #333;
}

.cmd-footer kbd {
  font-size: 11px;
  padding: 1px 5px;
  border-radius: 3px;
  background: #f0f2f5;
  font-family: monospace;
}

html.dark .cmd-footer kbd {
  background: #333;
}

/* ===== Transitions ===== */
.cmd-fade-enter-active {
  transition: opacity 0.2s ease;
}

.cmd-fade-leave-active {
  transition: opacity 0.15s ease;
}

.cmd-fade-enter-from,
.cmd-fade-leave-to {
  opacity: 0;
}

.cmd-fade-enter-active .cmd-panel {
  animation: cmd-slide 0.2s ease-out;
}

@keyframes cmd-slide {
  from {
    transform: translateY(-10px) scale(0.97);
    opacity: 0;
  }
  to {
    transform: translateY(0) scale(1);
    opacity: 1;
  }
}
</style>
