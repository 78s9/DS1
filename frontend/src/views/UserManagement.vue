<template>
  <div class="user-management">
    <!-- Page Header -->
    <div class="page-header">
      <h3>👥 用户管理</h3>
      <el-input
        v-model="search"
        placeholder="搜索用户名或邮箱..."
        clearable
        style="width: 260px;"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
    </div>

    <!-- Users Table -->
    <el-card shadow="hover">
      <el-table
        :data="filteredUsers"
        stripe
        v-loading="loading"
        empty-text="暂无用户数据 📭"
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="70" align="center" />
        <el-table-column prop="username" label="用户名" min-width="120">
          <template #default="{ row }">
            <el-avatar :size="24" icon="UserFilled" style="margin-right: 8px; vertical-align: middle;" />
            {{ row.username }}
          </template>
        </el-table-column>
        <el-table-column prop="email" label="邮箱" min-width="180" />
        <el-table-column prop="role" label="角色" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.role === 'ADMIN' ? 'danger' : 'success'" size="small">
              {{ row.role }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="注册时间" width="180" align="center">
          <template #default="{ row }">
            {{ formatTime(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" align="center">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="viewUser(row)">
              <el-icon><View /></el-icon> 查看
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- Pagination -->
      <div class="pagination-wrap" v-if="filteredUsers.length > 0">
        <span class="total-info">共 {{ filteredUsers.length }} 条记录</span>
      </div>
    </el-card>

    <!-- View User Detail Dialog -->
    <el-dialog v-model="detailVisible" title="用户详情" width="400px">
      <el-descriptions v-if="currentUser" :column="1" border>
        <el-descriptions-item label="ID">{{ currentUser.id }}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{ currentUser.username }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ currentUser.email }}</el-descriptions-item>
        <el-descriptions-item label="角色">
          <el-tag :type="currentUser.role === 'ADMIN' ? 'danger' : 'success'" size="small">
            {{ currentUser.role }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="注册时间">{{ formatTime(currentUser.createdAt) }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Search, View } from '@element-plus/icons-vue'
import request from '@/utils/request'

const users = ref([])
const loading = ref(false)
const search = ref('')
const detailVisible = ref(false)
const currentUser = ref(null)

const filteredUsers = computed(() => {
  if (!search.value) return users.value
  const keyword = search.value.toLowerCase()
  return users.value.filter(u =>
    u.username.toLowerCase().includes(keyword) ||
    (u.email && u.email.toLowerCase().includes(keyword))
  )
})

function formatTime(t) {
  return t ? new Date(t).toLocaleString('zh-CN') : '—'
}

function viewUser(row) {
  currentUser.value = row
  detailVisible.value = true
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

.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #ebeef5;
}

.total-info { font-size: 13px; color: #909399; }
</style>
