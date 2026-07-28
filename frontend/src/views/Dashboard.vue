<template>
  <el-container class="dashboard-container">
    <!-- Sidebar -->
    <el-aside width="220px" class="sidebar">
      <div class="logo">
        <span>DS1 管理系统</span>
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
        <el-menu-item index="/dashboard/users" disabled>
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
          <el-icon><Fold /></el-icon>
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
        <!-- Welcome Card -->
        <el-row :gutter="20">
          <el-col :span="8">
            <el-card shadow="hover">
              <template #header>
                <span>用户信息</span>
              </template>
              <div class="info-item">
                <span class="label">用户名：</span>
                <span>{{ username }}</span>
              </div>
              <div class="info-item">
                <span class="label">邮箱：</span>
                <span>{{ email }}</span>
              </div>
              <div class="info-item">
                <span class="label">角色：</span>
                <el-tag size="small" type="success">{{ role }}</el-tag>
              </div>
              <div class="info-item">
                <span class="label">注册时间：</span>
                <span>{{ createdAt }}</span>
              </div>
            </el-card>
          </el-col>

          <el-col :span="8">
            <el-card shadow="hover">
              <template #header>
                <span>快速操作</span>
              </template>
              <el-button type="primary" @click="refreshInfo">
                <el-icon><Refresh /></el-icon>
                刷新用户信息
              </el-button>
            </el-card>
          </el-col>

          <el-col :span="8">
            <el-card shadow="hover">
              <template #header>
                <span>系统状态</span>
              </template>
              <div class="info-item">
                <span class="label">后端状态：</span>
                <el-tag size="small" :type="backendOnline ? 'success' : 'danger'">
                  {{ backendOnline ? '在线' : '离线' }}
                </el-tag>
              </div>
              <div class="info-item">
                <span class="label">当前时间：</span>
                <span>{{ currentTime }}</span>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/store/auth'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const authStore = useAuthStore()

const activeMenu = ref('/dashboard')
const backendOnline = ref(false)
const currentTime = ref('')
let timer = null

const username = computed(() => authStore.user?.username || '未知')
const email = computed(() => authStore.user?.email || '未知')
const role = computed(() => authStore.user?.role || 'USER')
const createdAt = computed(() => {
  const t = authStore.user?.createdAt
  if (!t) return '未知'
  return new Date(t).toLocaleString('zh-CN')
})

function updateTime() {
  currentTime.value = new Date().toLocaleString('zh-CN')
}

async function refreshInfo() {
  try {
    await authStore.fetchUserInfo()
    backendOnline.value = true
    ElMessage.success('用户信息已刷新')
  } catch {
    backendOnline.value = false
  }
}

async function handleCommand(command) {
  if (command === 'logout') {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      type: 'warning'
    })
    authStore.logout()
    router.push('/login')
    ElMessage.success('已退出登录')
  }
}

onMounted(() => {
  updateTime()
  timer = setInterval(updateTime, 1000)
  refreshInfo()
})

onUnmounted(() => {
  clearInterval(timer)
})
</script>

<style scoped>
.dashboard-container {
  height: 100vh;
}

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
  font-size: 18px;
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

.header-left {
  font-size: 20px;
  cursor: pointer;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.username {
  color: #303133;
}

.main-content {
  background: #f0f2f5;
  padding: 20px;
}

.info-item {
  padding: 8px 0;
  font-size: 14px;
}

.info-item .label {
  color: #909399;
}
</style>
