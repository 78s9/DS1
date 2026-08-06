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
      <p class="auth-subtitle">请登录您的账户</p>

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
          <el-checkbox v-model="rememberMe" label="记住密码" size="small" />
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

      <div class="auth-footer">
        还没有账户？
        <router-link to="/register">立即注册</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
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
  } catch {
    // Error already handled by interceptor
  } finally {
    loading.value = false
  }
}

function onForgot() {
  ElMessage.info('请联系管理员重置密码 📧')
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
</style>
