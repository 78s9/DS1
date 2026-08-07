<template>
  <!-- FAB Button -->
  <div class="sticky-fab" @click="open = !open" :title="open ? '收起便签' : '打开便签'">
    <span class="fab-icon">{{ open ? '✕' : '📝' }}</span>
    <span v-if="!open" class="fab-badge" v-show="notes.length">{{ notes.length }}</span>
  </div>

  <!-- Notes Panel -->
  <transition name="notes-slide">
    <div v-if="open" class="sticky-panel">
      <div class="sticky-header">
        <span>📝 我的便签</span>
        <el-button text size="small" @click="addNote" :icon="Plus">新建</el-button>
      </div>

      <div class="sticky-list" v-if="notes.length">
        <div
          v-for="note in notes"
          :key="note.id"
          class="sticky-card"
          :style="{ background: note.color }"
        >
          <textarea
            v-model="note.text"
            class="sticky-textarea"
            placeholder="写点什么..."
            @input="save"
            rows="3"
          />
          <div class="sticky-meta">
            <span class="sticky-time">{{ note.time }}</span>
            <el-button text size="small" type="danger" @click="delNote(note.id)">
              <el-icon><Delete /></el-icon>
            </el-button>
          </div>
        </div>
      </div>

      <div v-else class="sticky-empty">
        <span>🎉 还没有便签</span>
        <span>点击「新建」开始记录</span>
      </div>
    </div>
  </transition>
</template>

<script setup>
import { ref, watch } from 'vue'
import { Plus, Delete } from '@element-plus/icons-vue'

const COLORS = ['#fff8e1', '#e8f5e9', '#e3f2fd', '#fce4ec', '#f3e5f5', '#e0f7fa']

const open = ref(false)

// Load notes from localStorage
const notes = ref(JSON.parse(localStorage.getItem('ds1-sticky-notes') || '[]'))

function save() {
  // Update timestamps
  notes.value.forEach(n => {
    if (!n.time) n.time = formatTime(Date.now())
  })
  localStorage.setItem('ds1-sticky-notes', JSON.stringify(notes.value))
}

function addNote() {
  notes.value.unshift({
    id: Date.now(),
    text: '',
    color: COLORS[Math.floor(Math.random() * COLORS.length)],
    time: formatTime(Date.now())
  })
  save()
}

function delNote(id) {
  notes.value = notes.value.filter(n => n.id !== id)
  save()
}

function formatTime(ts) {
  return new Date(ts).toLocaleString('zh-CN', {
    month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit'
  })
}

// Auto-save on text change
watch(notes, () => save(), { deep: true })
</script>

<style scoped>
/* ===== FAB Button ===== */
.sticky-fab {
  position: fixed;
  bottom: 28px;
  right: 28px;
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background: var(--color-primary);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  z-index: 998;
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.4);
  transition: transform 0.2s, box-shadow 0.2s;
  user-select: none;
}

.sticky-fab:hover {
  transform: scale(1.1);
  box-shadow: 0 6px 24px rgba(102, 126, 234, 0.55);
}

.fab-icon {
  font-size: 18px;
}

.fab-badge {
  position: absolute;
  top: -4px;
  right: -4px;
  background: #f56c6c;
  color: #fff;
  font-size: 11px;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
}

/* ===== Panel ===== */
.sticky-panel {
  position: fixed;
  bottom: 90px;
  right: 28px;
  width: 320px;
  max-height: 420px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
  z-index: 997;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

html.dark .sticky-panel {
  background: #1f1f1f;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.5);
}

.sticky-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 16px;
  border-bottom: 1px solid #ebeef5;
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

html.dark .sticky-header {
  border-bottom-color: #333;
  color: #e5e5e5;
}

.sticky-list {
  flex: 1;
  overflow-y: auto;
  padding: 10px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.sticky-card {
  padding: 12px;
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.08);
}

html.dark .sticky-card {
  filter: brightness(0.85);
}

.sticky-textarea {
  width: 100%;
  border: none;
  outline: none;
  background: transparent;
  font-size: 13px;
  color: #333;
  resize: vertical;
  font-family: inherit;
  line-height: 1.5;
}

.sticky-textarea::placeholder {
  color: #999;
}

.sticky-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 8px;
}

.sticky-time {
  font-size: 11px;
  color: #999;
}

.sticky-empty {
  text-align: center;
  padding: 30px 20px;
  color: #909399;
  font-size: 13px;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

/* ===== Transition ===== */
.notes-slide-enter-active {
  transition: all 0.3s ease-out;
}

.notes-slide-leave-active {
  transition: all 0.2s ease-in;
}

.notes-slide-enter-from {
  opacity: 0;
  transform: translateY(20px) scale(0.95);
}

.notes-slide-leave-to {
  opacity: 0;
  transform: translateY(10px) scale(0.95);
}
</style>
