<template>
  <div class="dashboard-home">
    <!-- Welcome Banner -->
    <div class="welcome-banner">
      <div class="welcome-left">
        <el-avatar :size="48" class="welcome-avatar">
          {{ username.charAt(0) }}
        </el-avatar>
        <div class="welcome-text">
          <h2>👋 欢迎回来，{{ username }}</h2>
          <p>{{ greeting }}</p>
        </div>
      </div>
      <div class="welcome-date">{{ currentDate }}</div>
    </div>

    <!-- Daily Quote -->
    <DailyQuote />

    <!-- Statistics Cards -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" style="background: #e6f7ff;">
            <el-icon :size="28" color="#1890ff"><UserFilled /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ countUpTotal }}</div>
            <div class="stat-label">用户总数</div>
            <svg class="sparkline" viewBox="0 0 80 24" preserveAspectRatio="none">
              <polyline
                fill="none"
                stroke="#1890ff"
                stroke-width="2"
                :points="sparklinePoints"
              />
            </svg>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" style="background: #f6ffed;">
            <el-icon :size="28" color="#52c41a"><CirclePlus /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ countUpToday }}</div>
            <div class="stat-label">今日新增</div>
            <div class="stat-trend up" v-if="stats.todayNew > 0">
              <el-icon><CaretTop /></el-icon> 较昨日增长
            </div>
            <div class="stat-trend flat" v-else>
              — 暂无新增
            </div>
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
            <el-tag :type="myRole === 'ADMIN' ? 'danger' : 'success'" size="small" style="margin-top: 4px;">
              {{ myRole === 'ADMIN' ? '管理员' : '普通用户' }}
            </el-tag>
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
            <div class="stat-trend" v-if="backendOnline">
              响应 {{ responseTime }}ms
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- Middle Section: Chart + Timeline -->
    <el-row :gutter="20" class="middle-row">
      <!-- User Growth Chart -->
      <el-col :span="14">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header">
              <span>📈 用户增长趋势</span>
              <span class="card-subtitle">近7天注册量</span>
            </div>
          </template>
          <div class="bar-chart">
            <div
              v-for="(bar, i) in weeklyData"
              :key="i"
              class="bar-col"
            >
              <div class="bar-value">{{ bar.value }}</div>
              <div class="bar-wrap">
                <div
                  class="bar"
                  :style="{
                    height: bar.percent + '%',
                    background: i === weeklyData.length - 1
                      ? 'linear-gradient(180deg, #667eea, #764ba2)'
                      : 'linear-gradient(180deg, #a0cfff, #409EFF)'
                  }"
                />
              </div>
              <div class="bar-label">{{ bar.label }}</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- Activity Timeline -->
      <el-col :span="10">
        <el-card shadow="hover">
          <template #header>
            <span>🕐 最近活动</span>
          </template>
          <el-timeline>
            <el-timeline-item
              v-for="item in activities"
              :key="item.id"
              :timestamp="item.time"
              :color="item.color"
              placement="top"
            >
              {{ item.text }}
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>
    </el-row>

    <!-- Bottom: Personal Info + Quick Actions -->
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
                {{ myRole === 'ADMIN' ? '管理员' : '普通用户' }}
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
          <div class="quick-actions">
            <div class="action-tile" @click="$router.push('/dashboard/users')">
              <div class="action-icon" style="background: #e6f7ff; color: #1890ff;">
                <el-icon :size="24"><UserFilled /></el-icon>
              </div>
              <div class="action-info">
                <div class="action-title">用户管理</div>
                <div class="action-desc">查看和管理系统用户</div>
              </div>
            </div>
            <div class="action-tile" @click="loadStats">
              <div class="action-icon" style="background: #f6ffed; color: #52c41a;">
                <el-icon :size="24"><Refresh /></el-icon>
              </div>
              <div class="action-info">
                <div class="action-title">刷新数据</div>
                <div class="action-desc">重新加载统计数据</div>
              </div>
            </div>
            <div class="action-tile disabled">
              <div class="action-icon" style="background: #f0f5ff; color: #722ed1;">
                <el-icon :size="24"><DataAnalysis /></el-icon>
              </div>
              <div class="action-info">
                <div class="action-title">数据分析</div>
                <div class="action-desc">功能开发中...</div>
              </div>
            </div>
            <div class="action-tile disabled">
              <div class="action-icon" style="background: #fff7e6; color: #fa8c16;">
                <el-icon :size="24"><Download /></el-icon>
              </div>
              <div class="action-info">
                <div class="action-title">数据导出</div>
                <div class="action-desc">功能开发中...</div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, reactive, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/store/auth'
import { useCountUp } from '@/composables/useCountUp'
import {
  Refresh, UserFilled, CirclePlus, Avatar, Monitor,
  DataAnalysis, Download, CaretTop
} from '@element-plus/icons-vue'
import request from '@/utils/request'
import DailyQuote from '@/components/DailyQuote.vue'

const router = useRouter()
const authStore = useAuthStore()

const stats = ref({ totalUsers: 0, todayNew: 0 })
const backendOnline = ref(false)
const responseTime = ref(0)

const { current: countUpTotal } = useCountUp(0, 1200)
const { current: countUpToday } = useCountUp(0, 1200)

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
  if (h < 12) return '早上好！元气满满的一天 ☀️'
  if (h < 18) return '下午好！工作加油 🚀'
  return '晚上好！看看今天的数据吧 🌆'
})

// Watch stats to restart count-up
watch(() => stats.value.totalUsers, (val) => {
  countUpTotal.value = 0
  const start = performance.now()
  const target = val
  function tick(now) {
    const elapsed = now - start
    const progress = Math.min(elapsed / 1200, 1)
    const eased = 1 - Math.pow(1 - progress, 3)
    countUpTotal.value = Math.round(eased * target)
    if (progress < 1) requestAnimationFrame(tick)
  }
  requestAnimationFrame(tick)
})

watch(() => stats.value.todayNew, (val) => {
  countUpToday.value = 0
  const start = performance.now()
  const target = val
  function tick(now) {
    const elapsed = now - start
    const progress = Math.min(elapsed / 1200, 1)
    const eased = 1 - Math.pow(1 - progress, 3)
    countUpToday.value = Math.round(eased * target)
    if (progress < 1) requestAnimationFrame(tick)
  }
  requestAnimationFrame(tick)
})

// Sparkline points
const sparklinePoints = computed(() => {
  const total = stats.value.totalUsers
  if (!total) return '0,24 80,24'
  const w = 80, h = 24, n = 6
  const pts = []
  for (let i = 0; i < n; i++) {
    const x = (i / (n - 1)) * w
    const y = h - 4 - Math.abs(Math.sin(i * 1.2 + 1)) * (h - 8) * 0.6
    pts.push(`${x.toFixed(1)},${y.toFixed(1)}`)
  }
  return pts.join(' ')
})

// Weekly simulated data
const weeklyData = reactive([
  { label: '周一', value: 0, percent: 0 },
  { label: '周二', value: 0, percent: 0 },
  { label: '周三', value: 0, percent: 0 },
  { label: '周四', value: 0, percent: 0 },
  { label: '周五', value: 0, percent: 0 },
  { label: '周六', value: 0, percent: 0 },
  { label: '今天', value: 0, percent: 0 },
])

function generateWeeklyData() {
  const base = Math.max(stats.value.totalUsers - stats.value.todayNew, 0)
  const todayVal = stats.value.todayNew
  // Simulate previous 6 days
  const raw = []
  let remaining = base
  for (let i = 5; i >= 0; i--) {
    const share = Math.round(remaining * (0.1 + Math.random() * 0.2))
    raw.unshift(share)
    remaining -= share
  }
  raw.push(todayVal)

  const maxVal = Math.max(...raw, 1)
  for (let i = 0; i < 7; i++) {
    weeklyData[i].value = raw[i]
    weeklyData[i].percent = Math.round((raw[i] / maxVal) * 100)
  }
}

// Activities (mock)
const activities = ref([
  { id: 1, text: '登录系统', time: '刚刚', color: '#409EFF' },
  { id: 2, text: '查看仪表盘', time: '1分钟前', color: '#67c23a' },
  { id: 3, text: '系统启动完成', time: '10分钟前', color: '#e6a23c' },
])

// Load
async function loadStats() {
  const start = performance.now()
  try {
    const res = await request.get('/dashboard/stats')
    if (res.code === 200) {
      stats.value = res.data
      generateWeeklyData()
    }
    backendOnline.value = true
  } catch {
    backendOnline.value = false
  }
  responseTime.value = Math.round(performance.now() - start)
}

onMounted(() => {
  loadStats()
})
</script>

<style scoped>
/* ===== Welcome Banner ===== */
.welcome-banner {
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  color: #fff;
  border-radius: 8px;
  padding: 24px 28px;
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.welcome-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.welcome-avatar {
  background: rgba(255, 255, 255, 0.25);
  color: #fff;
  font-size: 22px;
  font-weight: 700;
}

.welcome-banner h2 { margin: 0 0 4px; font-size: 20px; }
.welcome-banner p { margin: 0; opacity: 0.85; font-size: 14px; }
.welcome-date { font-size: 14px; opacity: 0.8; }

/* ===== Stats ===== */
.stats-row { margin-bottom: 20px; }

.stat-card {
  cursor: pointer;
  transition: transform 0.2s;
}

.stat-card:hover {
  transform: translateY(-2px);
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

.stat-info { flex: 1; }

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

.stat-trend {
  font-size: 12px;
  margin-top: 4px;
  color: #909399;
  display: flex;
  align-items: center;
  gap: 2px;
}

.stat-trend.up { color: #52c41a; }

.sparkline {
  width: 100%;
  height: 24px;
  margin-top: 4px;
}

/* ===== Middle Row ===== */
.middle-row { margin-bottom: 20px; }

.card-header {
  display: flex;
  align-items: baseline;
  gap: 8px;
}

.card-subtitle {
  font-size: 12px;
  color: #909399;
  font-weight: normal;
}

/* ===== Bar Chart ===== */
.bar-chart {
  display: flex;
  align-items: flex-end;
  justify-content: space-around;
  height: 200px;
  padding: 0 8px;
}

.bar-col {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  flex: 1;
}

.bar-value {
  font-size: 12px;
  font-weight: 600;
  color: #303133;
}

.bar-wrap {
  width: 32px;
  flex: 1;
  display: flex;
  align-items: flex-end;
}

.bar {
  width: 100%;
  border-radius: 4px 4px 0 0;
  transition: height 0.8s ease-out;
  min-height: 2px;
}

.bar-label {
  font-size: 11px;
  color: #909399;
  margin-top: 4px;
}

/* ===== Quick Actions ===== */
.quick-actions {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.action-tile {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
}

.action-tile:hover {
  border-color: #667eea;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.15);
  transform: translateY(-2px);
}

.action-tile.disabled {
  opacity: 0.55;
  cursor: not-allowed;
}

.action-tile.disabled:hover {
  border-color: #ebeef5;
  box-shadow: none;
  transform: none;
}

.action-icon {
  width: 44px;
  height: 44px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.action-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

.action-desc {
  font-size: 12px;
  color: #909399;
  margin-top: 2px;
}
</style>
