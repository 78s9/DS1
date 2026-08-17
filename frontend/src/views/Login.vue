<template>
  <div class="auth-container">
    <!-- Particle Background -->
    <div class="particle-bg">
      <div
        v-for="p in particles"
        :key="p.id"
        class="particle"
        :style="p.style"
      />
    </div>

    <!-- Login Card -->
    <div class="auth-card">
      <div class="auth-logo">🚀</div>
      <h2 class="auth-title">DS1 管理系统</h2>
      <p class="auth-subtitle">{{ greeting }}，欢迎回来 👋</p>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        size="large"
        @keyup.enter="handleLogin"
      >
        <el-form-item prop="username">
          <el-input
            v-model="form.username"
            placeholder="用户名"
            :prefix-icon="User"
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="密码"
            :prefix-icon="Lock"
            show-password
          />
        </el-form-item>

        <div class="login-extra">
          <el-checkbox v-model="rememberMe" size="small">记住用户名</el-checkbox>
          <a class="forgot-link" href="javascript:void(0)" @click="onForgot">忘记密码？</a>
        </div>

        <el-form-item>
          <el-button
            type="primary"
            :loading="loading"
            style="width: 100%"
            @click="handleLogin"
          >
            {{ loading ? '登录中...' : '登 录' }}
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 演示账号提示 -->
      <div class="demo-hint">
        <span>演示账号 <b>admin</b> / <b>admin123</b></span>
        <el-button link type="primary" size="small" @click="fillDemo">一键填充</el-button>
      </div>

      <div class="auth-footer">
        还没有账户？
        <router-link to="/register">立即注册</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { User, Lock } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
import { useAuthStore } from '@/store/auth'

const router = useRouter()
const authStore = useAuthStore()
const formRef = ref(null)
const loading = ref(false)
const rememberMe = ref(false)

const form = reactive({
  username: localStorage.getItem('rememberedUser') || '',
  password: ''
})

// Pre-fill if remembered
if (form.username) {
  rememberMe.value = true
}

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function handleLogin() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const res = await request.post('/auth/login', form)
    if (res.code === 200) {
      if (rememberMe.value) {
        localStorage.setItem('rememberedUser', form.username)
      } else {
        localStorage.removeItem('rememberedUser')
      }
      authStore.setAuth(res.data)
      ElMessage.success('登录成功 🎉')
      router.push('/dashboard')
    } else {
      ElMessage.error(res.message || '登录失败')
    }
  } catch (err) {
    // Show the actual error message from the backend
    const msg = err.response?.data?.message || '登录失败，请检查用户名和密码'
    ElMessage.error(msg)
  } finally {
    loading.value = false
  }
}

function onForgot() {
  ElMessage.info('请联系管理员重置密码 📧')
}

// 分时段问候语
const greeting = computed(() => {
  const h = new Date().getHours()
  if (h < 6) return '夜深了'
  if (h < 12) return '早上好'
  if (h < 18) return '下午好'
  return '晚上好'
})

// 一键填充演示账号
function fillDemo() {
  form.username = 'admin'
  form.password = 'admin123'
  rememberMe.value = true
}

// Generate random floating particles
const particles = ref(
  Array.from({ length: 12 }, (_, i) => ({
    id: i,
    style: {
      left: `${Math.random() * 100}%`,
      width: `${20 + Math.random() * 60}px`,
      height: `${20 + Math.random() * 60}px`,
      animationDuration: `${8 + Math.random() * 12}s`,
      animationDelay: `${Math.random() * 10}s`,
    }
  }))
)
</script>

<style scoped>
.login-extra {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: -8px 0 18px;
}

.forgot-link {
  font-size: 13px;
  color: #909399;
  text-decoration: none;
}

.forgot-link:hover {
  color: #409EFF;
}

/* ===== 演示账号提示 ===== */
.demo-hint {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin: -4px 0 20px;
  padding: 10px 14px;
  background: rgba(102, 126, 234, 0.06);
  border: 1px dashed rgba(102, 126, 234, 0.35);
  border-radius: 8px;
  font-size: 13px;
  color: var(--color-text-secondary);
}

.demo-hint b {
  color: var(--color-primary);
  font-weight: 600;
}

/* 主按钮使用主题渐变 */
.auth-card :deep(.el-button--primary) {
  background: linear-gradient(135deg, var(--color-primary), var(--color-primary-dark));
  border: none;
}

.auth-card :deep(.el-button--primary:hover),
.auth-card :deep(.el-button--primary:focus) {
  background: linear-gradient(135deg, var(--color-primary), var(--color-primary-dark));
  filter: brightness(1.08);
}
</style>
