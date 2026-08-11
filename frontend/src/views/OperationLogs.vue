<template>
  <div class="operation-logs">
    <!-- Page Header -->
    <div class="page-header">
      <h3>📋 操作日志</h3>
      <div class="header-actions">
        <el-button :icon="Refresh" @click="loadLogs" :loading="loading">刷新</el-button>
      </div>
    </div>

    <!-- Stats Cards -->
    <el-row :gutter="16" class="stats-row">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" style="background: #e6f7ff; color: #1890ff;">📊</div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.todayTotal }}</div>
            <div class="stat-label">今日操作</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" style="background: #f6ffed; color: #52c41a;">✅</div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.todaySuccess }}</div>
            <div class="stat-label">成功</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" style="background: #fff1f0; color: #f5222d;">❌</div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.todayFail }}</div>
            <div class="stat-label">失败</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" style="background: #f0f5ff; color: #722ed1;">📈</div>
          <div class="stat-info">
            <div class="stat-value">{{ totalLogs }}</div>
            <div class="stat-label">总记录数</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- Filter Bar -->
    <div class="filter-bar">
      <el-input
        v-model="keyword"
        placeholder="搜索用户名或操作描述..."
        clearable
        :prefix-icon="Search"
        style="width: 260px;"
      />
      <el-select v-model="filterAction" placeholder="操作类型" clearable style="width: 130px;">
        <el-option label="全部类型" value="" />
        <el-option label="登录" value="LOGIN" />
        <el-option label="注销" value="LOGOUT" />
        <el-option label="注册" value="REGISTER" />
        <el-option label="查询" value="QUERY" />
        <el-option label="创建" value="CREATE" />
        <el-option label="更新" value="UPDATE" />
        <el-option label="删除" value="DELETE" />
      </el-select>
      <el-select v-model="filterModule" placeholder="操作模块" clearable style="width: 130px;">
        <el-option label="全部模块" value="" />
        <el-option label="认证" value="认证" />
        <el-option label="用户" value="用户" />
        <el-option label="仪表盘" value="仪表盘" />
      </el-select>
      <el-select v-model="filterStatus" placeholder="状态" clearable style="width: 110px;">
        <el-option label="全部" value="" />
        <el-option label="成功" value="SUCCESS" />
        <el-option label="失败" value="FAIL" />
      </el-select>
    </div>

    <!-- Logs Table -->
    <el-card shadow="hover">
      <el-table
        :data="pagedLogs"
        stripe
        v-loading="loading"
        empty-text="暂无操作日志"
        style="width: 100%"
      >
        <el-table-column label="#" width="60" align="center">
          <template #default="{ $index }">
            {{ (currentPage - 1) * pageSize + $index + 1 }}
          </template>
        </el-table-column>
        <el-table-column prop="username" label="用户" width="120">
          <template #default="{ row }">
            <span class="log-username">{{ row.username }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="action" label="操作类型" width="110" align="center">
          <template #default="{ row }">
            <el-tag :type="actionTag(row.action)" size="small" effect="plain">
              {{ actionLabel(row.action) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="module" label="模块" width="90" align="center">
          <template #default="{ row }">
            <el-tag size="small" type="info">{{ row.module }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="操作描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="ip" label="IP地址" width="140" align="center" />
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 'SUCCESS' ? 'success' : 'danger'" size="small" effect="dark">
              {{ row.status === 'SUCCESS' ? '成功' : '失败' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="时间" width="170" align="center">
          <template #default="{ row }">
            {{ formatTime(row.createdAt) }}
          </template>
        </el-table-column>
      </el-table>

      <!-- Pagination -->
      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="totalLogs"
          layout="total, sizes, prev, pager, next, jumper"
          small
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { Search, Refresh } from '@element-plus/icons-vue'
import request from '@/utils/request'

// ===== State =====
const logs = ref([])
const loading = ref(false)
const keyword = ref('')
const filterAction = ref('')
const filterModule = ref('')
const filterStatus = ref('')
const currentPage = ref(1)
const pageSize = ref(20)
const totalLogs = ref(0)
const stats = ref({ todayTotal: 0, todaySuccess: 0, todayFail: 0 })

// ===== Computed =====
const pagedLogs = computed(() => logs.value)

// ===== Helpers =====
function actionTag(action) {
  const map = { LOGIN: 'primary', LOGOUT: 'info', REGISTER: 'success', QUERY: '', CREATE: 'success', UPDATE: 'warning', DELETE: 'danger' }
  return map[action] || ''
}

function actionLabel(action) {
  const map = { LOGIN: '登录', LOGOUT: '注销', REGISTER: '注册', QUERY: '查询', CREATE: '创建', UPDATE: '更新', DELETE: '删除' }
  return map[action] || action
}

function formatTime(t) {
  return t ? new Date(t).toLocaleString('zh-CN') : '—'
}

// ===== API =====
async function loadLogs() {
  loading.value = true
  try {
    const params = { page: currentPage.value, size: pageSize.value }
    if (keyword.value) params.keyword = keyword.value
    if (filterAction.value) params.action = filterAction.value
    if (filterModule.value) params.module = filterModule.value

    const res = await request.get('/logs', { params })
    if (res.code === 200) {
      logs.value = res.data.list || []
      totalLogs.value = res.data.total || 0
    }
  } catch {
    logs.value = []
    totalLogs.value = 0
  } finally {
    loading.value = false
  }
}

async function loadStats() {
  try {
    const res = await request.get('/logs/stats')
    if (res.code === 200) {
      stats.value = res.data
    }
  } catch {
    // stats are non-critical
  }
}

// ===== Watchers =====
watch([keyword, filterAction, filterModule, filterStatus], () => {
  currentPage.value = 1
  loadLogs()
})

watch(currentPage, () => loadLogs())
watch(pageSize, () => { currentPage.value = 1; loadLogs() })

// ===== Init =====
onMounted(() => {
  loadLogs()
  loadStats()
})
</script>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.page-header h3 {
  margin: 0;
  font-size: 18px;
  color: #303133;
}

.header-actions {
  display: flex;
  gap: 8px;
}

/* ===== Stats ===== */
.stats-row {
  margin-bottom: 16px;
}

.stat-card :deep(.el-card__body) {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 18px 20px;
}

.stat-icon {
  width: 44px;
  height: 44px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  flex-shrink: 0;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #303133;
  line-height: 1.2;
}

.stat-label {
  font-size: 12px;
  color: #909399;
  margin-top: 2px;
}

/* ===== Filter Bar ===== */
.filter-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}

/* ===== Table ===== */
.log-username {
  font-weight: 500;
  color: #303133;
}

/* ===== Pagination ===== */
.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #ebeef5;
}

/* Dark mode */
html.dark .stat-value {
  color: #e0e0e0 !important;
}

html.dark .log-username {
  color: #e0e0e0;
}
</style>
