<template>
  <el-container class="dashboard-container">
    <!-- Sidebar -->
    <el-aside :width="isCollapsed ? '64px' : '220px'" class="sidebar">
      <!-- Logo -->
      <div class="logo">
        <span class="logo-icon">🚀</span>
        <transition name="fade">
          <span v-show="!isCollapsed" class="logo-text">DS1 管理系统</span>
        </transition>
      </div>

      <!-- Menu -->
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapsed"
        :collapse-transition="false"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
        router
      >
        <el-menu-item index="/dashboard">
          <el-icon><HomeFilled /></el-icon>
          <span>首页</span>
        </el-menu-item>
        <el-menu-item v-if="isAdmin" index="/dashboard/users">
          <el-icon><UserFilled /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
        <el-menu-item v-if="isAdmin" index="/dashboard/logs">
          <el-icon><Document /></el-icon>
          <span>操作日志</span>
        </el-menu-item>
        <el-menu-item index="/dashboard/workshop">
          <el-icon><MagicStick /></el-icon>
          <span>数据工坊</span>
        </el-menu-item>
        <el-menu-item index="/dashboard/settings">
          <el-icon><Setting /></el-icon>
          <span>系统设置</span>
        </el-menu-item>
      </el-menu>

      <!-- Sidebar Footer -->
      <div class="sidebar-footer">
        <el-avatar :size="32" icon="UserFilled" />
        <transition name="fade">
          <div v-show="!isCollapsed" class="sidebar-user-info">
            <div class="sidebar-username">{{ username }}</div>
            <el-tag size="small" :type="myRole === 'ADMIN' ? 'danger' : 'success'">
              {{ myRole }}
            </el-tag>
          </div>
        </transition>
      </div>
    </el-aside>

    <!-- Main -->
    <el-container>
      <!-- Header -->
      <el-header class="header">
        <div class="header-left">
          <el-button
            :icon="isCollapsed ? Expand : Fold"
            text
            size="large"
            @click="isCollapsed = !isCollapsed"
          />
          <el-breadcrumb separator="/" class="breadcrumb">
            <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="currentPage">{{ currentPage }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <div class="header-right">
          <!-- Theme Color Picker -->
          <el-popover placement="bottom" :width="200" trigger="click">
            <template #reference>
              <el-tooltip content="切换主题色" placement="bottom">
                <el-button text size="large" style="font-size:18px">
                  🎨
                </el-button>
              </el-tooltip>
            </template>
            <div class="theme-picker">
              <div
                v-for="(t, key) in themeList"
                :key="key"
                class="theme-dot"
                :class="{ active: themeStore.themeName === key }"
                :style="{ background: t.primary }"
                :title="t.name"
                @click="themeStore.setTheme(key)"
              />
            </div>
          </el-popover>

          <!-- Dark Mode Toggle -->
          <el-tooltip :content="themeStore.isDark ? '切换亮色模式' : '切换暗黑模式'" placement="bottom">
            <el-button
              text
              size="large"
              style="font-size:18px"
              @click="themeStore.toggleDark()"
            >
              {{ themeStore.isDark ? '☀️' : '🌙' }}
            </el-button>
          </el-tooltip>

          <!-- Fullscreen Toggle -->
          <el-tooltip :content="isFullscreen ? '退出全屏' : '全屏模式'" placement="bottom">
            <el-button
              :icon="isFullscreen ? FullScreen : FullScreen"
              text
              size="large"
              @click="toggleFullscreen"
            />
          </el-tooltip>

          <!-- Notifications -->
          <el-popover placement="bottom" :width="300" trigger="click">
            <template #reference>
              <el-badge :value="unreadCount" :hidden="unreadCount === 0">
                <el-button :icon="Bell" text size="large" />
              </el-badge>
            </template>
            <div class="notification-list">
              <div class="notification-title">
                <span>消息通知</span>
                <el-button v-if="unreadCount > 0" type="primary" link size="small" @click="markAllRead">
                  全部已读
                </el-button>
              </div>
              <div v-for="n in notifications" :key="n.id" class="notification-item">
                <div class="notification-icon" :style="{ background: n.color }">
                  <el-icon :size="14"><component :is="n.icon" /></el-icon>
                </div>
                <div class="notification-body">
                  <div class="notification-text">{{ n.text }}</div>
                  <div class="notification-time">{{ n.time }}</div>
                </div>
              </div>
              <div v-if="notifications.length === 0" class="notification-empty">
                暂无新消息 🎉
              </div>
            </div>
          </el-popover>

          <!-- User Dropdown -->
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32" icon="UserFilled" />
              <span class="username">{{ username }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item disabled>
                  <el-icon><Message /></el-icon>
                  {{ email }}
                </el-dropdown-item>
                <el-dropdown-item command="editProfile">
                  <el-icon><Edit /></el-icon>
                  编辑资料
                </el-dropdown-item>
                <el-dropdown-item command="changePassword">
                  <el-icon><Lock /></el-icon>
                  修改密码
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- Content -->
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>

    <!-- Profile Edit Dialog -->
    <el-dialog v-model="profileDialogVisible" title="编辑个人资料" width="420px" destroy-on-close>
      <el-form :model="profileForm" label-width="80px">
        <el-form-item label="用户名">
          <el-input v-model="profileForm.username" disabled />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="profileForm.email" placeholder="请输入新邮箱" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="profileDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="saveProfile">保存</el-button>
      </template>
    </el-dialog>

    <!-- Change Password Dialog -->
    <el-dialog v-model="passwordDialogVisible" title="修改密码" width="420px" destroy-on-close>
      <el-form :model="passwordForm" label-width="80px">
        <el-form-item label="原密码">
          <el-input v-model="passwordForm.oldPassword" type="password" show-password placeholder="请输入原密码" />
        </el-form-item>
        <el-form-item label="新密码">
          <el-input v-model="passwordForm.newPassword" type="password" show-password placeholder="至少 6 位" />
        </el-form-item>
        <el-form-item label="确认密码">
          <el-input v-model="passwordForm.confirmPassword" type="password" show-password placeholder="再次输入新密码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="passwordDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="savingPassword" @click="savePassword">确定</el-button>
      </template>
    </el-dialog>
  </el-container>

  <!-- Sticky Notes FAB -->
  <StickyNotes />
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/store/auth'
import { useThemeStore, THEMES } from '@/store/theme'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  HomeFilled, UserFilled, Setting, MagicStick, Document,
  Expand, Fold, Bell, FullScreen,
  ArrowDown, Edit, SwitchButton, Message, Lock,
  CircleCheck, InfoFilled, Warning
} from '@element-plus/icons-vue'
import request from '@/utils/request'
import StickyNotes from '@/components/StickyNotes.vue'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()
const themeStore = useThemeStore()
const themeList = THEMES

const isCollapsed = ref(false)
const isFullscreen = ref(false)

const activeMenu = computed(() => route.path)
const username = computed(() => authStore.user?.username || '未知')
const email = computed(() => authStore.user?.email || '未知')
const myRole = computed(() => authStore.user?.role || 'USER')
const isAdmin = computed(() => authStore.user?.role === 'ADMIN')

const currentPage = computed(() => {
  const map = {
    '/dashboard': '',
    '/dashboard/users': '用户管理',
    '/dashboard/logs': '操作日志',
    '/dashboard/workshop': '数据工坊',
    '/dashboard/settings': '系统设置'
  }
  return map[route.path] || ''
})

// ===== Fullscreen =====
function toggleFullscreen() {
  if (!document.fullscreenElement) {
    document.documentElement.requestFullscreen()
    isFullscreen.value = true
  } else {
    document.exitFullscreen()
    isFullscreen.value = false
  }
}

// Listen for ESC to exit fullscreen
document.addEventListener('fullscreenchange', () => {
  isFullscreen.value = !!document.fullscreenElement
})

// ===== Notifications =====
const unreadCount = ref(3)
const notifications = ref([
  { id: 1, icon: 'CircleCheck', color: '#e6f7ff', text: '系统更新完成', time: '10分钟前' },
  { id: 2, icon: 'InfoFilled', color: '#f0f5ff', text: '欢迎使用 DS1 管理系统', time: '1小时前' },
  { id: 3, icon: 'Warning', color: '#fff7e6', text: '请及时完善个人资料', time: '1天前' },
])

function markAllRead() {
  unreadCount.value = 0
  ElMessage.success('已全部标记为已读')
}

// ===== Change Password =====
const passwordDialogVisible = ref(false)
const savingPassword = ref(false)
const passwordForm = ref({ oldPassword: '', newPassword: '', confirmPassword: '' })

function openPasswordDialog() {
  passwordForm.value = { oldPassword: '', newPassword: '', confirmPassword: '' }
  passwordDialogVisible.value = true
}

async function savePassword() {
  const { oldPassword, newPassword, confirmPassword } = passwordForm.value
  if (!oldPassword || !newPassword) {
    ElMessage.warning('请填写原密码和新密码')
    return
  }
  if (newPassword.length < 6) {
    ElMessage.warning('新密码至少 6 位')
    return
  }
  if (newPassword !== confirmPassword) {
    ElMessage.warning('两次输入的新密码不一致')
    return
  }

  savingPassword.value = true
  try {
    await request.put('/user/me/password', { oldPassword, newPassword })
    ElMessage.success('密码修改成功，请重新登录')
    passwordDialogVisible.value = false
    authStore.logout()
    router.push('/login')
  } catch {
    // error handled by interceptor
  } finally {
    savingPassword.value = false
  }
}

// ===== Profile Edit =====
const profileDialogVisible = ref(false)
const saving = ref(false)
const profileForm = ref({ username: '', email: '' })

function openEditDialog() {
  profileForm.value.username = authStore.user?.username || ''
  profileForm.value.email = authStore.user?.email || ''
  profileDialogVisible.value = true
}

async function saveProfile() {
  saving.value = true
  try {
    await request.put('/user/me', { email: profileForm.value.email })
    await authStore.fetchUserInfo()
    ElMessage.success('资料更新成功 🎉')
    profileDialogVisible.value = false
  } catch {
    // error handled by interceptor
  } finally {
    saving.value = false
  }
}

// ===== Dropdown =====
async function handleCommand(command) {
  if (command === 'editProfile') {
    openEditDialog()
  } else if (command === 'changePassword') {
    openPasswordDialog()
  } else if (command === 'logout') {
    try {
      await ElMessageBox.confirm('确定要退出登录吗？', '提示', { type: 'warning' })
    } catch {
      return
    }
    try {
      await request.post('/auth/logout')
    } catch {
      // 登出接口失败不影响本地退出
    }
    authStore.logout()
    router.push('/login')
    ElMessage.success('已退出登录')
  }
}
</script>

<style scoped>
.dashboard-container { height: 100vh; }

/* ===== Sidebar ===== */
.sidebar {
  background-color: var(--color-sidebar);
  overflow-y: auto;
  overflow-x: hidden;
  transition: width 0.3s ease;
  display: flex;
  flex-direction: column;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #fff;
  font-size: 17px;
  font-weight: bold;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  padding: 0 12px;
  white-space: nowrap;
  overflow: hidden;
}

.logo-icon { font-size: 22px; flex-shrink: 0; }

.logo-text { overflow: hidden; }

.sidebar :deep(.el-menu) {
  border-right: none;
  flex: 1;
}

.sidebar-footer {
  padding: 12px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  display: flex;
  align-items: center;
  gap: 10px;
  overflow: hidden;
  white-space: nowrap;
}

.sidebar-user-info {
  overflow: hidden;
}

.sidebar-username {
  color: #bfcbd9;
  font-size: 13px;
  line-height: 1.3;
}

/* ===== Header ===== */
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  border-bottom: 1px solid #e6e6e6;
  padding: 0 20px;
  height: 60px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.breadcrumb {
  font-size: 14px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 4px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 6px;
  transition: background 0.2s;
}

.user-info:hover {
  background: #f5f7fa;
}

.username { color: #303133; }

/* ===== Notifications ===== */
.notification-list {
  max-height: 320px;
  overflow-y: auto;
}

.notification-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  padding-bottom: 10px;
  border-bottom: 1px solid #ebeef5;
  margin-bottom: 8px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.notification-item {
  display: flex;
  gap: 10px;
  padding: 10px 0;
  border-bottom: 1px solid #f5f5f5;
}

.notification-item:last-child { border-bottom: none; }

.notification-icon {
  width: 28px;
  height: 28px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.notification-body { flex: 1; }

.notification-text {
  font-size: 13px;
  color: #303133;
}

.notification-time {
  font-size: 12px;
  color: #c0c4cc;
  margin-top: 2px;
}

.notification-empty {
  text-align: center;
  padding: 20px 0;
  color: #909399;
  font-size: 14px;
}

/* ===== Theme Picker ===== */
.theme-picker {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  justify-content: center;
  padding: 4px;
}

.theme-dot {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  border: 2px solid transparent;
}

.theme-dot:hover {
  transform: scale(1.2);
}

.theme-dot.active {
  border-color: #fff;
  box-shadow: 0 0 0 3px currentColor;
}

/* ===== Main ===== */
.main-content {
  background: #f0f2f5;
  padding: 20px;
}

/* ===== Transitions ===== */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
