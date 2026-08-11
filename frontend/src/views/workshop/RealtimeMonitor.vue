<template>
  <ErrorBoundary>
    <div class="realtime-monitor">
      <!-- Connection Status -->
      <div class="rm-status-bar">
        <div class="rm-connection">
          <span class="rm-dot" :class="{ online: connected, offline: !connected }" />
          <span>{{ connected ? '实时连接中' : '已断开 — 使用模拟数据' }}</span>
          <span class="rm-fps">刷新: {{ fps }} FPS</span>
        </div>
        <div class="rm-actions">
          <el-switch v-model="isMonitoring" active-text="监控" size="small" />
          <el-button size="small" :icon="Refresh" @click="resetData">重置</el-button>
        </div>
      </div>

      <!-- KPI Cards with Animated Numbers -->
      <el-row :gutter="16" class="rm-kpi-row">
        <el-col :span="6" v-for="metric in kpiMetrics" :key="metric.key">
          <el-card shadow="hover" class="rm-kpi-card" v-intersect="{ onEnter: () => {} }">
            <div class="rm-kpi-header">
              <span class="rm-kpi-icon" :style="{ background: metric.color }">{{ metric.icon }}</span>
              <span class="rm-kpi-label">{{ metric.label }}</span>
            </div>
            <AnimatedNumber
              :value="metric.value"
              :decimals="metric.decimals"
              :suffix="metric.unit"
              :duration="800"
              class="rm-kpi-value"
            />
            <div class="rm-kpi-sub">
              <span :class="metric.trend > 0 ? 'trend-up' : 'trend-down'">
                {{ metric.trend > 0 ? '↑' : '↓' }} {{ Math.abs(metric.trend) }}%
              </span>
              <span class="rm-kpi-desc">vs 上一周期</span>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- Charts Row -->
      <el-row :gutter="16" class="rm-chart-row">
        <!-- CPU Usage Chart -->
        <el-col :span="14">
          <el-card shadow="hover">
            <template #header><span>📊 CPU 使用率 (实时)</span></template>
            <div class="rm-chart">
              <svg viewBox="0 0 600 180" preserveAspectRatio="none" class="rm-svg">
                <!-- Grid lines -->
                <line v-for="y in 4" :key="'g'+y" :x1="0" :y1="y*36" :x2="600" :y2="y*36"
                  stroke="#f0f0f0" stroke-dasharray="4" />
                <!-- Area fill -->
                <path :d="cpuAreaPath" fill="rgba(102,126,234,0.12)" />
                <!-- Line -->
                <path :d="cpuLinePath" fill="none" stroke="#667eea" stroke-width="2"
                  stroke-linejoin="round" />
                <!-- Dots -->
                <circle
                  v-for="(pt, i) in cpuPoints"
                  :key="'c'+i"
                  :cx="pt.x" :cy="pt.y"
                  r="3"
                  fill="#667eea"
                />
              </svg>
            </div>
          </el-card>
        </el-col>

        <!-- Alert Feed -->
        <el-col :span="10">
          <el-card shadow="hover">
            <template #header><span>🚨 实时告警</span></template>
            <div class="rm-alert-feed">
              <TransitionGroup name="alert-slide" tag="div">
                <div
                  v-for="alert in alerts"
                  :key="alert.id"
                  class="rm-alert-item"
                  :class="'alert-' + alert.level"
                >
                  <span class="rm-alert-icon">{{ alert.icon }}</span>
                  <div class="rm-alert-body">
                    <div class="rm-alert-text">{{ alert.text }}</div>
                    <div class="rm-alert-time">{{ alert.time }}</div>
                  </div>
                </div>
              </TransitionGroup>
              <div v-if="alerts.length === 0" class="rm-alert-empty">
                ✅ 一切正常
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- Bottom: Memory + Network -->
      <el-row :gutter="16">
        <!-- Memory Usage -->
        <el-col :span="12">
          <el-card shadow="hover">
            <template #header><span>💾 内存使用</span></template>
            <div class="rm-memory">
              <div class="rm-mem-bar-wrap">
                <div
                  class="rm-mem-bar"
                  :style="{ width: memoryPercent + '%', background: memColor }"
                />
              </div>
              <div class="rm-mem-info">
                <span>{{ memoryUsed }} GB / {{ memoryTotal }} GB</span>
                <span class="rm-mem-pct">{{ memoryPercent }}%</span>
              </div>
              <div class="rm-mem-detail">
                <div class="rm-mem-item" v-for="seg in memorySegments" :key="seg.label">
                  <span class="rm-mem-dot" :style="{ background: seg.color }" />
                  <span>{{ seg.label }}</span>
                  <span class="rm-mem-val">{{ seg.value }} GB</span>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>

        <!-- Network IO -->
        <el-col :span="12">
          <el-card shadow="hover">
            <template #header><span>🌐 网络 I/O</span></template>
            <div class="rm-network">
              <div class="rm-net-row">
                <div class="rm-net-dir">
                  <span class="rm-net-label">↓ 下载</span>
                  <AnimatedNumber :value="networkDown" :decimals="1" suffix=" MB/s" :duration="500"
                    class="rm-net-value" />
                </div>
                <div class="rm-net-dir">
                  <span class="rm-net-label">↑ 上传</span>
                  <AnimatedNumber :value="networkUp" :decimals="1" suffix=" MB/s" :duration="500"
                    class="rm-net-value" />
                </div>
              </div>
              <div class="rm-net-bars">
                <div
                  v-for="i in 30"
                  :key="i"
                  class="rm-net-bar-col"
                >
                  <div class="rm-net-bar down" :style="{ height: (netHistory[i-1]?.down / 5 * 100) + '%' }" />
                  <div class="rm-net-bar up" :style="{ height: (netHistory[i-1]?.up / 3 * 100) + '%' }" />
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </ErrorBoundary>
</template>

<script setup>
import { ref, computed, reactive, onMounted, onUnmounted } from 'vue'
import { Refresh } from '@element-plus/icons-vue'
import AnimatedNumber from '@/components/AnimatedNumber.vue'
import ErrorBoundary from '@/components/ErrorBoundary.vue'

const connected = ref(true)
const isMonitoring = ref(true)
const fps = ref(10)
let monitorTimer = null
let startTime = performance.now()

// ===== KPI Metrics =====
const kpiMetrics = reactive([
  { key: 'cpu', label: 'CPU 使用率', icon: '💻', value: 0, decimals: 1, unit: '%', color: '#e6f7ff', trend: 0 },
  { key: 'qps', label: '每秒请求', icon: '⚡', value: 0, decimals: 0, unit: '', color: '#f6ffed', trend: 0 },
  { key: 'active', label: '活跃连接', icon: '👥', value: 0, decimals: 0, unit: '', color: '#fff7e6', trend: 0 },
  { key: 'latency', label: '平均延迟', icon: '⏱️', value: 0, decimals: 0, unit: 'ms', color: '#f0f5ff', trend: 0 },
])

// ===== CPU Chart =====
const CPU_HISTORY = 60
const cpuHistory = ref(Array.from({ length: CPU_HISTORY }, () => 20 + Math.random() * 10))
const cpuPoints = computed(() => {
  const w = 600, h = 180, pad = 10
  const data = cpuHistory.value
  return data.map((val, i) => ({
    x: pad + (i / (data.length - 1)) * (w - pad * 2),
    y: h - pad - (val / 100) * (h - pad * 2)
  }))
})
const cpuLinePath = computed(() => {
  return cpuPoints.value.map((p, i) => `${i === 0 ? 'M' : 'L'}${p.x.toFixed(1)},${p.y.toFixed(1)}`).join(' ')
})
const cpuAreaPath = computed(() => {
  const pts = cpuPoints.value
  if (!pts.length) return ''
  const last = pts[pts.length - 1]
  return cpuLinePath.value + ` L${last.x},180 L${pts[0].x},180 Z`
})

// ===== Memory =====
const memoryTotal = 16
const memoryUsed = ref(4.2)
const memoryPercent = computed(() => Math.round(memoryUsed.value / memoryTotal * 100))
const memColor = computed(() => {
  if (memoryPercent.value > 85) return '#f56c6c'
  if (memoryPercent.value > 65) return '#e6a23c'
  return '#667eea'
})
const memorySegments = reactive([
  { label: '应用', color: '#667eea', value: 0 },
  { label: '缓存', color: '#a0cfff', value: 0 },
  { label: '系统', color: '#d3adf7', value: 0 },
])

// ===== Network =====
const networkDown = ref(0)
const networkUp = ref(0)
const netHistory = ref(Array.from({ length: 30 }, () => ({ down: 0, up: 0 })))

// ===== Alerts =====
const alerts = reactive([])
let alertId = 0

function addAlert(level, text) {
  const icons = { info: 'ℹ️', warn: '⚠️', error: '🚨' }
  alerts.unshift({
    id: ++alertId,
    level,
    icon: icons[level],
    text,
    time: new Date().toLocaleTimeString('zh-CN')
  })
  // Keep only last 20
  if (alerts.length > 20) alerts.length = 20
}

// ===== Simulation =====
function tick() {
  if (!isMonitoring.value) return

  // CPU
  const cpuVal = Math.max(5, Math.min(98, cpuHistory.value[cpuHistory.value.length - 1] + (Math.random() - 0.5) * 15))
  cpuHistory.value.push(cpuVal)
  if (cpuHistory.value.length > CPU_HISTORY) cpuHistory.value.shift()
  kpiMetrics[0].value = Math.round(cpuVal * 10) / 10
  kpiMetrics[0].trend = Math.round((Math.random() - 0.45) * 10)

  // QPS
  kpiMetrics[1].value = Math.round(800 + Math.random() * 400)
  kpiMetrics[1].trend = Math.round((Math.random() - 0.4) * 15)

  // Active connections
  kpiMetrics[2].value = Math.round(200 + Math.random() * 100)
  kpiMetrics[2].trend = Math.round((Math.random() - 0.48) * 8)

  // Latency
  kpiMetrics[3].value = Math.round(15 + Math.random() * 40)
  kpiMetrics[3].trend = Math.round((Math.random() - 0.55) * 20)

  // Memory
  memoryUsed.value = Math.max(2, Math.min(15.8, memoryUsed.value + (Math.random() - 0.5) * 0.2))
  memorySegments[0].value = Math.round(memoryUsed.value * 0.6 * 10) / 10
  memorySegments[1].value = Math.round(memoryUsed.value * 0.25 * 10) / 10
  memorySegments[2].value = Math.round(memoryUsed.value * 0.15 * 10) / 10

  // Network
  networkDown.value = Math.round((0.5 + Math.random() * 4.5) * 10) / 10
  networkUp.value = Math.round((0.2 + Math.random() * 2.8) * 10) / 10
  netHistory.value.push({ down: networkDown.value, up: networkUp.value })
  if (netHistory.value.length > 30) netHistory.value.shift()

  // Random alerts
  if (Math.random() < 0.15) {
    if (cpuVal > 80) addAlert('error', `CPU 使用率过高: ${cpuVal.toFixed(1)}%`)
    else if (cpuVal > 60) addAlert('warn', `CPU 使用率上升: ${cpuVal.toFixed(1)}%`)
    else if (Math.random() < 0.3) addAlert('info', `系统运行正常，QPS: ${kpiMetrics[1].value}`)
  }

  // FPS
  const now = performance.now()
  const elapsed = now - startTime
  fps.value = Math.round(1000 / (elapsed || 16))
  startTime = now
}

function resetData() {
  cpuHistory.value = Array.from({ length: CPU_HISTORY }, () => 20 + Math.random() * 10)
  netHistory.value = Array.from({ length: 30 }, () => ({ down: 0, up: 0 }))
  memoryUsed.value = 4.2
  alerts.length = 0
  addAlert('info', '数据已重置')
}

onMounted(() => {
  addAlert('info', '实时监控已启动')
  monitorTimer = setInterval(tick, 1000)
})

onUnmounted(() => {
  if (monitorTimer) clearInterval(monitorTimer)
})
</script>

<style scoped>
.realtime-monitor { max-width: 1200px; }

/* Status Bar */
.rm-status-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding: 8px 16px;
  background: #fafafa;
  border-radius: 8px;
  border: 1px solid #f0f0f0;
}

.rm-connection {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #606266;
}

.rm-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
}

.rm-dot.online { background: #67c23a; box-shadow: 0 0 6px rgba(103, 194, 58, 0.6); animation: pulse-dot 2s infinite; }
.rm-dot.offline { background: #f56c6c; }

@keyframes pulse-dot {
  0%, 100% { box-shadow: 0 0 4px rgba(103, 194, 58, 0.4); }
  50% { box-shadow: 0 0 12px rgba(103, 194, 58, 0.8); }
}

.rm-fps {
  font-size: 11px;
  color: #c0c4cc;
  margin-left: 8px;
}

.rm-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

/* KPI Cards */
.rm-kpi-row { margin-bottom: 16px; }

.rm-kpi-card :deep(.el-card__body) {
  padding: 18px 20px;
}

.rm-kpi-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.rm-kpi-icon {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
}

.rm-kpi-label {
  font-size: 13px;
  color: #909399;
}

.rm-kpi-value {
  font-size: 32px;
  font-weight: 700;
  color: #303133;
  line-height: 1.2;
}

.rm-kpi-sub {
  font-size: 12px;
  margin-top: 4px;
}

.trend-up { color: #f56c6c; }
.trend-down { color: #67c23a; }

.rm-kpi-desc {
  color: #c0c4cc;
  margin-left: 4px;
}

/* Charts */
.rm-chart-row { margin-bottom: 16px; }

.rm-chart {
  width: 100%;
  height: 180px;
}

.rm-svg {
  width: 100%;
  height: 100%;
}

.rm-svg line {
  stroke: #f0f0f0;
}

html.dark .rm-svg line {
  stroke: #333;
}

/* Alert Feed */
.rm-alert-feed {
  max-height: 180px;
  overflow-y: auto;
}

.rm-alert-item {
  display: flex;
  gap: 8px;
  padding: 8px 0;
  border-bottom: 1px solid #f5f5f5;
  font-size: 13px;
}

.rm-alert-item:last-child { border-bottom: none; }

.rm-alert-icon { font-size: 16px; flex-shrink: 0; }

.rm-alert-body { flex: 1; }

.rm-alert-text { color: #303133; }
.rm-alert-time { font-size: 11px; color: #c0c4cc; margin-top: 2px; }

.rm-alert-empty {
  text-align: center;
  padding: 40px 0;
  color: #909399;
  font-size: 15px;
}

/* Alert transitions */
.alert-slide-enter-active {
  transition: all 0.4s ease;
}

.alert-slide-enter-from {
  opacity: 0;
  transform: translateX(-20px);
}

/* Memory */
.rm-memory {
  padding: 4px 0;
}

.rm-mem-bar-wrap {
  height: 24px;
  background: #f0f0f0;
  border-radius: 12px;
  overflow: hidden;
  margin-bottom: 8px;
}

.rm-mem-bar {
  height: 100%;
  border-radius: 12px;
  transition: width 0.6s ease, background 0.6s ease;
}

.rm-mem-info {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
  color: #606266;
  margin-bottom: 12px;
}

.rm-mem-pct {
  font-weight: 700;
  font-size: 18px;
  color: #303133;
}

.rm-mem-detail {
  display: flex;
  gap: 24px;
}

.rm-mem-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #909399;
}

.rm-mem-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
}

.rm-mem-val {
  color: #606266;
  font-weight: 500;
}

/* Network */
.rm-network {
  padding: 4px 0;
}

.rm-net-row {
  display: flex;
  gap: 40px;
  margin-bottom: 16px;
}

.rm-net-dir {
  flex: 1;
}

.rm-net-label {
  font-size: 13px;
  color: #909399;
}

.rm-net-value {
  font-size: 24px;
  font-weight: 700;
  color: #303133;
}

.rm-net-bars {
  display: flex;
  align-items: flex-end;
  gap: 2px;
  height: 80px;
}

.rm-net-bar-col {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  gap: 1px;
  height: 100%;
}

.rm-net-bar {
  width: 100%;
  border-radius: 1px;
  transition: height 0.5s ease;
}

.rm-net-bar.down { background: #667eea; }
.rm-net-bar.up { background: #a0cfff; }

/* Dark mode */
html.dark .rm-status-bar {
  background: #2a2a2a;
  border-color: #333;
}

html.dark .rm-connection {
  color: #ccc;
}

html.dark .rm-mem-bar-wrap {
  background: #333;
}

html.dark .rm-kpi-value,
html.dark .rm-mem-pct,
html.dark .rm-net-value {
  color: #e0e0e0 !important;
}

html.dark .rm-alert-item {
  border-bottom-color: #333;
}

html.dark .rm-alert-text {
  color: #e0e0e0;
}
</style>
