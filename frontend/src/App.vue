<template>
  <router-view v-slot="{ Component, route }">
    <transition name="page-fade" mode="out-in">
      <component :is="Component" :key="route.matched[0]?.path || route.path" />
    </transition>
  </router-view>
  <!-- Global Command Palette (Ctrl+K) -->
  <CommandPalette ref="cmdPalette" />
</template>

<script setup>
import { onMounted, ref } from 'vue'
import CommandPalette from '@/components/CommandPalette.vue'
import { useThemeStore } from '@/store/theme'

const cmdPalette = ref(null)

// Apply saved theme/dark mode globally on startup, so the login page also
// reflects the user's chosen theme (not just after logging in).
useThemeStore()

// Expose cmd palette globally via window for easy access
onMounted(() => {
  window.__cmdPalette = cmdPalette.value
})
</script>

<style>
/* ===== CSS Variables ===== */
:root {
  --color-primary: #667eea;
  --color-primary-dark: #764ba2;
  --color-primary-light: #a78bfa;
  --color-bg: #f0f2f5;
  --color-sidebar: #304156;
  --color-white: #ffffff;
  --color-text: #303133;
  --color-text-secondary: #909399;
  --color-border: #e6e6e6;
  --color-success: #67c23a;
  --color-warning: #e6a23c;
  --color-danger: #f56c6c;
  --sidebar-width: 220px;
  --sidebar-collapsed-width: 64px;

  /* Transition for theme switching */
  --theme-transition: background 0.3s, color 0.3s, border-color 0.3s;
}

/* ===== Dark Mode ===== */
html.dark {
  --color-bg: #141414;
  --color-white: #1f1f1f;
  --color-text: #e5e5e5;
  --color-text-secondary: #999;
  --color-border: #333;
  --color-sidebar: #1a1a2e;
}

html.dark body {
  background: var(--color-bg);
  color: var(--color-text);
}

html.dark .header {
  background: var(--color-white) !important;
  border-bottom-color: var(--color-border) !important;
}

html.dark .main-content {
  background: var(--color-bg) !important;
}

/* Element Plus dark overrides */
html.dark .el-card {
  background: var(--color-white);
  border-color: var(--color-border);
  color: var(--color-text);
}

html.dark .el-card__header {
  border-bottom-color: var(--color-border);
}

html.dark .el-table {
  --el-table-bg-color: var(--color-white);
  --el-table-tr-bg-color: var(--color-white);
  --el-table-header-bg-color: #2a2a2a;
  --el-table-row-hover-bg-color: #2a2a2a;
  --el-table-border-color: var(--color-border);
  --el-table-text-color: var(--color-text);
  --el-table-header-text-color: var(--color-text);
}

html.dark .el-table th.el-table__cell {
  background-color: #2a2a2a;
}

html.dark .el-table--striped .el-table__body tr.el-table__row--striped td.el-table__cell {
  background-color: #252525;
}

html.dark .el-pagination {
  --el-pagination-bg-color: transparent;
  --el-pagination-text-color: var(--color-text);
}

html.dark .el-pagination button {
  color: var(--color-text);
}

html.dark .el-input__wrapper {
  background-color: #2a2a2a;
  box-shadow: 0 0 0 1px var(--color-border) inset;
}

html.dark .el-input__inner {
  color: var(--color-text);
}

html.dark .el-select .el-input__wrapper {
  background-color: #2a2a2a;
}

html.dark .el-dialog {
  --el-dialog-bg-color: var(--color-white);
}

html.dark .el-descriptions__body .el-descriptions__table {
  --el-descriptions-item-bordered-label-background: #2a2a2a;
}

html.dark .stat-value {
  color: var(--color-text) !important;
}

html.dark .bar-value {
  color: var(--color-text) !important;
}

html.dark .action-tile {
  border-color: var(--color-border);
}

html.dark .action-tile:hover {
  background: #2a2a2a;
}

html.dark .sidebar-username {
  color: #ccc !important;
}

html.dark .user-info:hover {
  background: #2a2a2a;
}

html.dark .notification-item {
  border-bottom-color: #333;
}

html.dark .notification-title {
  border-bottom-color: #333;
  color: var(--color-text);
}

html.dark .notification-text {
  color: var(--color-text);
}

html.dark .auth-card {
  background: #1f1f1f;
}

html.dark .auth-title {
  color: #e5e5e5;
}

html.dark .auth-subtitle {
  color: #999;
}

html.dark .auth-footer {
  color: #999;
}

html, body, #app {
  height: 100%;
  margin: 0;
  padding: 0;
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB',
    'Microsoft YaHei', Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  transition: var(--theme-transition);
}

/* ===== Scrollbar ===== */
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

::-webkit-scrollbar-track {
  background: transparent;
}

::-webkit-scrollbar-thumb {
  background: #c0c4cc;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: #909399;
}

html.dark ::-webkit-scrollbar-thumb {
  background: #555;
}

html.dark ::-webkit-scrollbar-thumb:hover {
  background: #777;
}

/* ===== Selection ===== */
::selection {
  background: rgba(102, 126, 234, 0.25);
  color: inherit;
}

/* ===== Page Transition ===== */
.page-fade-enter-active,
.page-fade-leave-active {
  transition: opacity 0.25s ease, transform 0.25s ease;
}

.page-fade-enter-from {
  opacity: 0;
  transform: translateY(8px);
}

.page-fade-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}

/* ===== Particle Background (shared by login/register) ===== */
.particle-bg {
  position: fixed;
  inset: 0;
  overflow: hidden;
  z-index: 0;
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 50%, var(--color-primary) 100%);
  background-size: 200% 200%;
  animation: gradient-shift 18s ease infinite;
}

@keyframes gradient-shift {
  0% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
}

.particle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float-up linear infinite;
}

@keyframes float-up {
  0% {
    transform: translateY(100vh) scale(0);
    opacity: 0;
  }
  10% {
    opacity: 1;
  }
  90% {
    opacity: 1;
  }
  100% {
    transform: translateY(-10vh) scale(1);
    opacity: 0;
  }
}

/* ===== Auth Page Shared ===== */
.auth-container {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  position: relative;
  overflow: hidden;
}

.auth-card {
  width: 420px;
  max-width: 90vw;
  padding: 40px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.2);
  position: relative;
  z-index: 1;
  animation: card-in 0.5s ease-out;
}

@keyframes card-in {
  from {
    opacity: 0;
    transform: translateY(30px) scale(0.96);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.auth-logo {
  width: 64px;
  height: 64px;
  margin: 0 auto 16px;
  background: linear-gradient(135deg, var(--color-primary), var(--color-primary-dark));
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  color: #fff;
}

.auth-title {
  text-align: center;
  margin: 0 0 8px;
  font-size: 26px;
  font-weight: 700;
  color: #303133;
}

.auth-subtitle {
  text-align: center;
  margin: 0 0 32px;
  color: #909399;
  font-size: 14px;
}

.auth-footer {
  text-align: center;
  color: #909399;
  font-size: 14px;
}

.auth-footer a {
  color: var(--color-primary);
  text-decoration: none;
  font-weight: 500;
}

.auth-footer a:hover {
  text-decoration: underline;
}
</style>
