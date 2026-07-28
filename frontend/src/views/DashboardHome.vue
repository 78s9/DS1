<template>
  <div class="dashboard-home">
    <!-- Welcome Banner -->
    <div class="welcome-banner">
      <div class="welcome-text">
        <h2>👋 欢迎回来，{{ username }}</h2>
        <p>{{ greeting }}</p>
      </div>
      <div class="welcome-date">{{ currentDate }}</div>
    </div>

    <!-- Statistics Cards -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" style="background: #e6f7ff;">
            <el-icon :size="28" color="#1890ff"><UserFilled /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.totalUsers }}</div>
            <div class="stat-label">用户总数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" style="background: #f6ffed;">
            <el-icon :size="28" color="#52c41a"><CirclePlus /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.todayNew }}</div>
            <div class="stat-label">今日新增</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" style="background: #fff7e6;">
            <el-icon :size="28" color="#fa8c16"><Avatar /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value role">{{ myRole }}</div>
            <div class="stat-label">我的角色</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" style="background: #f0f5ff;">
            <el-icon :size="28" color="#722ed1"><Monitor /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">
              <el-tag :type="backendOnline ? 'success' : 'danger'" size="small">
                {{ backendOnline ? '在线' : '离线' }}
              </el-tag>
            </div>
            <div class="stat-label">后端状态</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- Bottom Cards -->
    <el-row :gutter="20">
      <!-- User Info -->
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <span>📋 个人信息</span>
          </template>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="用户名">{{ username }}</el-descriptions-item>
            <el-descriptions-item label="邮箱">{{ email }}</el-descriptions-item>
            <el-descriptions-item label="角色">
              <el-tag size="small" :type="myRole === 'ADMIN' ? 'danger' : 'success'">
                {{ myRole }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="注册时间">{{ createdAt }}</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>

      <!-- Quick Actions -->
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <span>⚡ 快捷操作</span>
          </template>
          <el-space wrap :size="12">
            <el-button type="primary" :icon="Refresh" @click="loadStats">刷新统计</el-button>
            <el-button type="success" :icon="UserFilled" @click="goUsers">用户管理</el-button>
            <el-button type="warning" :icon="TrendCharts" disabled>更多功能开发中...</el-button>
          </el-space>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/store/auth'
import { Refresh, UserFilled, CirclePlus, Avatar, Monitor, TrendCharts } from '@element-plus/icons-vue'
import request from '@/utils/request'

const router = useRouter()
const authStore = useAuthStore()

const stats = ref({ totalUsers: 0, todayNew: 0 })
const backendOnline = ref(false)

const username = computed(() => authStore.user?.username || '未知')
const email = computed(() => authStore.user?.email || '未知')
const myRole = computed(() => authStore.user?.role || 'USER')
const createdAt = computed(() => {
  const t = authStore.user?.createdAt
  return t ? new Date(t).toLocaleString('zh-CN') : '未知'
})

const currentDate = computed(() => new Date().toLocaleDateString('zh-CN', {
  year: 'numeric', month: 'long', day: 'numeric', weekday: 'long'
}))

const greeting = computed(() => {
  const h = new Date().getHours()
  if (h < 6) return '夜深了，注意休息 🌙'
  if (h < 12) return '早上好！今天也是元气满满的一天 ☀️'
  if (h < 18) return '下午好！工作效率棒棒的 🚀'
  return '晚上好！看看今天的数据吧 🌆'
})

async function loadStats() {
  try {
    const res = await request.get('/dashboard/stats')
    if (res.code === 200) stats.value = res.data
    backendOnline.value = true
  } catch {
    backendOnline.value = false
  }
}

onMounted(() => {
  loadStats()
})

function goUsers() {
  router.push('/dashboard/users')
}
</script>

<style scoped>
.welcome-banner {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  border-radius: 8px;
  padding: 24px 28px;
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.welcome-banner h2 { margin: 0 0 6px; font-size: 20px; }
.welcome-banner p { margin: 0; opacity: 0.85; font-size: 14px; }
.welcome-date { font-size: 14px; opacity: 0.8; }

.stats-row { margin-bottom: 20px; }

.stat-card {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-card :deep(.el-card__body) {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
}

.stat-icon {
  width: 52px;
  height: 52px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-value {
  font-size: 26px;
  font-weight: 700;
  color: #303133;
}

.stat-value.role { font-size: 18px; }

.stat-label {
  font-size: 13px;
  color: #909399;
  margin-top: 2px;
}
</style>
