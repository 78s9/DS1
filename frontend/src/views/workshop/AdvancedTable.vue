<template>
  <ErrorBoundary>
    <div class="advanced-table">
      <!-- Toolbar -->
      <div class="at-toolbar">
        <div class="at-toolbar-left">
          <el-input
            v-model="searchQuery"
            placeholder="搜索姓名、邮箱、城市..."
            clearable
            :prefix-icon="Search"
            style="width: 280px"
            size="default"
          />
          <el-select v-model="filterStatus" placeholder="状态筛选" clearable size="default" style="width: 130px">
            <el-option label="全部" value="" />
            <el-option label="活跃" value="active" />
            <el-option label="待审核" value="pending" />
            <el-option label="已禁用" value="disabled" />
          </el-select>
        </div>
        <div class="at-toolbar-right">
          <span class="at-info">共 <b>{{ filteredCount.toLocaleString() }}</b> 条，渲染 <b>{{ visibleCount }}</b> 个DOM节点</span>
          <el-button size="default" :icon="Refresh" @click="regenerate">重新生成</el-button>
          <el-button size="default" type="primary" :icon="Check" @click="batchApprove" :disabled="selectedIds.size === 0">
            批量通过 ({{ selectedIds.size }})
          </el-button>
        </div>
      </div>

      <!-- Virtual Scroll Table -->
      <div
        ref="viewportRef"
        class="at-viewport"
        @scroll="handleScroll"
      >
        <!-- Table Header -->
        <div class="at-header">
          <div class="at-th at-th-check" style="width:48px">
            <el-checkbox v-model="selectAll" @change="toggleSelectAll" />
          </div>
          <div
            v-for="col in columns"
            :key="col.key"
            class="at-th"
            :style="{ width: col.width, cursor: col.sortable ? 'pointer' : 'default' }"
            @click="col.sortable && toggleSort(col.key)"
          >
            {{ col.label }}
            <span v-if="sortKey === col.key" class="sort-arrow">
              {{ sortDir === 'asc' ? '▲' : '▼' }}
            </span>
          </div>
        </div>

        <!-- Virtual Rows -->
        <div class="at-spacer" :style="{ height: totalHeight + 'px' }">
          <div class="at-content" :style="{ transform: `translateY(${offsetY}px)` }">
            <div
              v-for="item in visibleItems"
              :key="item._key"
              class="at-row"
              :class="{ 'at-row-selected': selectedIds.has(item.id) }"
              @click="toggleRow(item.id)"
            >
              <div class="at-td at-td-check" style="width:48px">
                <el-checkbox
                  :model-value="selectedIds.has(item.id)"
                  @click.stop
                  @change="toggleRow(item.id)"
                />
              </div>
              <div class="at-td" :style="{ width: columns[0].width }">
                <span class="at-avatar" :style="{ background: avatarColor(item.id) }">
                  {{ item.name.charAt(0) }}
                </span>
                {{ item.name }}
              </div>
              <div class="at-td" :style="{ width: columns[1].width }">{{ item.email }}</div>
              <div class="at-td" :style="{ width: columns[2].width }">{{ item.city }}</div>
              <div class="at-td" :style="{ width: columns[3].width }">
                <el-tag
                  :type="statusType(item.status)"
                  size="small"
                  effect="plain"
                >
                  {{ statusLabel(item.status) }}
                </el-tag>
              </div>
              <div class="at-td" :style="{ width: columns[4].width }">{{ item.amount }}</div>
              <div class="at-td" :style="{ width: columns[5].width }">{{ item.date }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </ErrorBoundary>
</template>

<script setup>
import { ref, computed, reactive, shallowRef } from 'vue'
import { Search, Refresh, Check } from '@element-plus/icons-vue'
import ErrorBoundary from '@/components/ErrorBoundary.vue'

// ===== Data Generation =====
const ROW_HEIGHT = 52
const OVERS = 10

const columns = [
  { key: 'name', label: '姓名', width: '160px', sortable: true },
  { key: 'email', label: '邮箱', width: '220px', sortable: false },
  { key: 'city', label: '城市', width: '120px', sortable: true },
  { key: 'status', label: '状态', width: '100px', sortable: true },
  { key: 'amount', label: '金额', width: '100px', sortable: true },
  { key: 'date', label: '日期', width: '130px', sortable: true },
]

const statuses = ['active', 'pending', 'disabled']
const cities = ['北京', '上海', '广州', '深圳', '杭州', '成都', '武汉', '南京', '西安', '重庆']
const avColors = ['#1890ff','#52c41a','#fa8c16','#722ed1','#eb2f96','#13c2c2','#f5222d','#a0d911']

function avatarColor(id) { return avColors[(id || 1) % avColors.length] }
function statusType(s) { return { active: 'success', pending: 'warning', disabled: 'danger' }[s] }
function statusLabel(s) { return { active: '活跃', pending: '待审核', disabled: '已禁用' }[s] }

function generateData(count = 100000) {
  const data = []
  for (let i = 1; i <= count; i++) {
    data.push({
      id: i,
      name: `用户${String(i).padStart(6, '0')}`,
      email: `user${i}@example.com`,
      city: cities[i % cities.length],
      status: i % 7 === 0 ? 'disabled' : i % 5 === 0 ? 'pending' : 'active',
      amount: `¥${((i * 137.5) % 10000).toFixed(2)}`,
      date: new Date(2024, 0, 1 + (i % 365)).toLocaleDateString('zh-CN'),
      _key: i,
      _style: {}
    })
  }
  return data
}

// ===== Reactive State =====
const allData = shallowRef(generateData(100000))
const searchQuery = ref('')
const filterStatus = ref('')
const sortKey = ref('')
const sortDir = ref('asc')
const selectedIds = reactive(new Set())
const selectAll = ref(false)

// ===== Filtered & Sorted =====
const filteredData = computed(() => {
  let result = allData.value

  if (searchQuery.value) {
    const kw = searchQuery.value.toLowerCase()
    result = result.filter(r =>
      r.name.includes(kw) ||
      r.email.toLowerCase().includes(kw) ||
      r.city.includes(kw)
    )
  }

  if (filterStatus.value) {
    result = result.filter(r => r.status === filterStatus.value)
  }

  if (sortKey.value) {
    const dir = sortDir.value === 'asc' ? 1 : -1
    result = [...result].sort((a, b) => {
      const va = a[sortKey.value]
      const vb = b[sortKey.value]
      if (typeof va === 'number') return (va - vb) * dir
      return String(va).localeCompare(String(vb)) * dir
    })
  }

  return result
})

const filteredCount = computed(() => filteredData.value.length)

// ===== Virtual Scroll =====
const viewportRef = ref(null)
const scrollTop = shallowRef(0)
const viewportHeight = shallowRef(600)

const totalHeight = computed(() => filteredData.value.length * ROW_HEIGHT)

const startIdx = computed(() =>
  Math.max(0, Math.floor(scrollTop.value / ROW_HEIGHT) - OVERS)
)

const endIdx = computed(() =>
  Math.min(
    filteredData.value.length,
    Math.ceil((scrollTop.value + viewportHeight.value) / ROW_HEIGHT) + OVERS
  )
)

const visibleItems = computed(() => {
  return filteredData.value.slice(startIdx.value, endIdx.value).map((item, i) => {
    return {
      ...item,
      _key: item.id,
      _index: startIdx.value + i
    }
  })
})

const visibleCount = computed(() => visibleItems.value.length)

const offsetY = computed(() => startIdx.value * ROW_HEIGHT)

function handleScroll() {
  if (viewportRef.value) {
    scrollTop.value = viewportRef.value.scrollTop
  }
}

function toggleSort(key) {
  if (sortKey.value === key) {
    sortDir.value = sortDir.value === 'asc' ? 'desc' : 'asc'
  } else {
    sortKey.value = key
    sortDir.value = 'asc'
  }
}

function toggleRow(id) {
  if (selectedIds.has(id)) {
    selectedIds.delete(id)
  } else {
    selectedIds.add(id)
  }
  selectAll.value = false
}

function toggleSelectAll(val) {
  if (val) {
    filteredData.value.forEach(r => selectedIds.add(r.id))
  } else {
    selectedIds.clear()
  }
}

function batchApprove() {
  const count = selectedIds.size
  selectedIds.clear()
  selectAll.value = false
  ElMessage.success(`已批量处理 ${count} 条记录`)
}

function regenerate() {
  allData.value = generateData(100000)
  selectedIds.clear()
  selectAll.value = false
  ElMessage.success('已重新生成 100,000 条数据')
}

// Init viewport height
import { onMounted } from 'vue'
import { ElMessage } from 'element-plus'
onMounted(() => {
  if (viewportRef.value) {
    viewportHeight.value = viewportRef.value.clientHeight
  }
  if (window.ResizeObserver && viewportRef.value) {
    new ResizeObserver(() => {
      if (viewportRef.value) viewportHeight.value = viewportRef.value.clientHeight
    }).observe(viewportRef.value)
  }
})
</script>

<style scoped>
.advanced-table {
  display: flex;
  flex-direction: column;
}

/* Toolbar */
.at-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 12px;
}

.at-toolbar-left,
.at-toolbar-right {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.at-info {
  font-size: 12px;
  color: #909399;
  margin-right: 8px;
}

/* Viewport — the scrollable container */
.at-viewport {
  height: 580px;
  overflow-y: auto;
  overflow-x: hidden;
  border: 1px solid #ebeef5;
  border-radius: 6px;
  position: relative;
  contain: strict;
}

/* Header row — sticky */
.at-header {
  display: flex;
  align-items: center;
  height: 44px;
  background: #fafafa;
  border-bottom: 1px solid #ebeef5;
  position: sticky;
  top: 0;
  z-index: 10;
  padding-right: 6px; /* scrollbar space */
}

.at-th {
  padding: 0 12px;
  font-size: 13px;
  font-weight: 600;
  color: #606266;
  white-space: nowrap;
  user-select: none;
}

.sort-arrow {
  font-size: 10px;
  margin-left: 2px;
}

/* Spacer to enable scrollbar */
.at-spacer {
  position: relative;
}

.at-content {
  /* Rows laid out in normal flow; container shifted by translateY */
}

/* Row */
.at-row {
  display: flex;
  align-items: center;
  height: 52px;
  border-bottom: 1px solid #f0f0f0;
  transition: background 0.15s;
  cursor: pointer;
}

.at-row:hover {
  background: #f5f7fa;
}

.at-row-selected {
  background: #ecf5ff;
}

.at-td {
  padding: 0 12px;
  font-size: 13px;
  color: #303133;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  display: flex;
  align-items: center;
  gap: 6px;
}

.at-td-check {
  justify-content: center;
}

.at-avatar {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  color: #fff;
  font-size: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

/* Dark mode */
html.dark .at-header {
  background: #2a2a2a;
  border-bottom-color: #333;
}

html.dark .at-th {
  color: #ccc;
}

html.dark .at-viewport {
  border-color: #333;
}

html.dark .at-row {
  border-bottom-color: #333;
}

html.dark .at-row:hover {
  background: #2a2a2a;
}

html.dark .at-row-selected {
  background: #1a3a5c;
}

html.dark .at-td {
  color: #e0e0e0;
}
</style>
