<template>
  <div class="user-management">
    <!-- Page Header -->
    <div class="page-header">
      <h3>👥 用户管理</h3>
      <div class="header-actions">
        <el-button :icon="Download" disabled>
          导出 Excel
        </el-button>
      </div>
    </div>

    <!-- Filter Bar -->
    <div class="filter-bar">
      <el-input
        v-model="search"
        placeholder="搜索用户名或邮箱..."
        clearable
        :prefix-icon="Search"
        style="width: 260px;"
      />
      <el-select
        v-model="filterRole"
        placeholder="按角色筛选"
        clearable
        style="width: 140px;"
      >
        <el-option label="全部角色" value="" />
        <el-option label="管理员" value="ADMIN" />
        <el-option label="普通用户" value="USER" />
      </el-select>
    </div>

    <!-- Users Table -->
    <el-card shadow="hover">
      <el-table
        :data="pagedUsers"
        stripe
        v-loading="loading"
        empty-text="暂无用户数据"
        style="width: 100%"
        @row-click="viewUser"
        row-class-name="table-row"
      >
        <el-table-column label="#" width="60" align="center">
          <template #default="{ $index }">
            {{ (currentPage - 1) * pageSize + $index + 1 }}
          </template>
        </el-table-column>
        <el-table-column prop="username" label="用户名" min-width="130" sortable="custom">
          <template #default="{ row }">
            <div class="user-cell">
              <el-avatar
                :size="28"
                :style="{ background: avatarColor(row.id), fontSize: '13px' }"
              >
                {{ row.username.charAt(0).toUpperCase() }}
              </el-avatar>
              {{ row.username }}
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="email" label="邮箱" min-width="180" />
        <el-table-column prop="role" label="角色" width="110" align="center">
          <template #default="{ row }">
            <el-tag :type="row.role === 'ADMIN' ? 'danger' : 'success'" size="small" effect="plain">
              {{ row.role === 'ADMIN' ? '👑 管理员' : '👤 用户' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="注册时间" width="180" align="center" sortable="custom">
          <template #default="{ row }">
            {{ formatTime(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click.stop="viewUser(row)">
              <el-icon><View /></el-icon> 查看
            </el-button>
            <el-button
              type="warning"
              link
              size="small"
              :disabled="isSelf(row)"
              @click.stop="toggleRole(row)"
            >
              <el-icon><Switch /></el-icon>
              {{ row.role === 'ADMIN' ? '降为用户' : '升为管理员' }}
            </el-button>
            <el-popconfirm
              title="确定要删除该用户吗？"
              confirm-button-text="删除"
              cancel-button-text="取消"
              @confirm="handleDelete(row)"
              @click.stop
            >
              <template #reference>
                <el-button type="danger" link size="small" :disabled="isSelf(row)">
                  <el-icon><Delete /></el-icon>
                </el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <!-- Pagination -->
      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          :total="filteredUsers.length"
          layout="total, sizes, prev, pager, next, jumper"
          small
        />
      </div>
    </el-card>

    <!-- View User Detail Dialog -->
    <el-dialog v-model="detailVisible" title="用户详情" width="420px">
      <el-descriptions v-if="currentUser" :column="1" border>
        <el-descriptions-item label="ID">{{ currentUser.id }}</el-descriptions-item>
        <el-descriptions-item label="用户名">
          <div class="user-cell">
            <el-avatar
              :size="24"
              :style="{ background: avatarColor(currentUser.id), fontSize: '11px' }"
            >
              {{ currentUser.username.charAt(0).toUpperCase() }}
            </el-avatar>
            {{ currentUser.username }}
          </div>
        </el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ currentUser.email }}</el-descriptions-item>
        <el-descriptions-item label="角色">
          <el-tag :type="currentUser.role === 'ADMIN' ? 'danger' : 'success'" size="small">
            {{ currentUser.role === 'ADMIN' ? '管理员' : '普通用户' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="注册时间">{{ formatTime(currentUser.createdAt) }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { Search, View, Delete, Download, Switch } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
import { useAuthStore } from '@/store/auth'

const authStore = useAuthStore()

// ===== Data =====
const users = ref([])
const loading = ref(false)
const search = ref('')
const filterRole = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const detailVisible = ref(false)
const currentUser = ref(null)
// ===== Avatar Colors =====
const avatarColors = ['#1890ff', '#52c41a', '#fa8c16', '#722ed1', '#eb2f96', '#13c2c2']
function avatarColor(id) {
  return avatarColors[(id || 1) % avatarColors.length]
}

// ===== Computed =====
const filteredUsers = computed(() => {
  let result = [...users.value]

  // Search filter
  if (search.value) {
    const keyword = search.value.toLowerCase()
    result = result.filter(u =>
      u.username.toLowerCase().includes(keyword) ||
      (u.email && u.email.toLowerCase().includes(keyword))
    )
  }

  // Role filter
  if (filterRole.value) {
    result = result.filter(u => u.role === filterRole.value)
  }

  return result
})

const pagedUsers = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  return filteredUsers.value.slice(start, start + pageSize.value)
})

// ===== Methods =====
function formatTime(t) {
  return t ? new Date(t).toLocaleString('zh-CN') : '—'
}

function viewUser(row) {
  currentUser.value = row
  detailVisible.value = true
}

function isSelf(row) {
  return row.username === authStore.user?.username
}

async function handleDelete(row) {
  if (isSelf(row)) return
  try {
    await request.delete(`/user/${row.id}`)
    ElMessage.success(`已删除用户「${row.username}」`)
    loadUsers()
  } catch {
    // error handled by interceptor
  }
}

async function toggleRole(row) {
  if (isSelf(row)) return
  const newRole = row.role === 'ADMIN' ? 'USER' : 'ADMIN'
  try {
    await request.put(`/user/${row.id}/role`, { role: newRole })
    ElMessage.success(`已将「${row.username}」角色更新为 ${newRole}`)
    loadUsers()
  } catch {
    // error handled by interceptor
  }
}

async function loadUsers() {
  loading.value = true
  try {
    const res = await request.get('/dashboard/users')
    if (res.code === 200) users.value = res.data || []
  } catch {
    users.value = []
  } finally {
    loading.value = false
  }
}

// Reset page when filter changes
watch([filterRole, search], () => {
  currentPage.value = 1
})

onMounted(() => {
  loadUsers()
})
</script>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.page-header h3 { margin: 0; font-size: 18px; color: #303133; }

.header-actions { display: flex; gap: 8px; }

/* ===== Filter Bar ===== */
.filter-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}

/* ===== Table ===== */
.user-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

:deep(.table-row) {
  cursor: pointer;
}

:deep(.table-row:hover > td) {
  background: #f5f7fa !important;
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
</style>
