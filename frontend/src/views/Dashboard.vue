<template>
  <el-container class="dashboard-container">
    <!-- Sidebar -->
    <el-aside width="220px" class="sidebar">
      <div class="logo">
        <span>🚀 DS1 管理系统</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
        router
      >
        <el-menu-item index="/dashboard">
          <el-icon><HomeFilled /></el-icon>
          <span>首页</span>
        </el-menu-item>
        <el-menu-item index="/dashboard/users">
          <el-icon><UserFilled /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <!-- Main -->
    <el-container>
      <!-- Header -->
      <el-header class="header">
        <div class="header-left">
          <el-icon :size="20"><Fold /></el-icon>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32" icon="UserFilled" />
              <span class="username">{{ username }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item disabled>{{ email }}</el-dropdown-item>
                <el-dropdown-item command="editProfile">
                  <el-icon><Edit /></el-icon>
                  编辑资料
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
  </el-container>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/store/auth'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const activeMenu = computed(() => route.path)
const username = computed(() => authStore.user?.username || '未知')
const email = computed(() => authStore.user?.email || '未知')

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
  } else if (command === 'logout') {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', { type: 'warning' })
    authStore.logout()
    router.push('/login')
    ElMessage.success('已退出登录')
  }
}
</script>

<style scoped>
.dashboard-container { height: 100vh; }

.sidebar {
  background-color: #304156;
  overflow-y: auto;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 17px;
  font-weight: bold;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  border-bottom: 1px solid #e6e6e6;
  padding: 0 20px;
}

.header-left { font-size: 20px; cursor: pointer; }

.header-right { display: flex; align-items: center; }

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.username { color: #303133; }

.main-content {
  background: #f0f2f5;
  padding: 20px;
}
</style>
