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

    <!-- Register Card -->
    <div class="auth-card" v-if="!registered">
      <div class="auth-logo">✨</div>
      <h2 class="auth-title">创建账户</h2>
      <p class="auth-subtitle">注册 DS1 管理系统账户</p>

      <!-- Step Indicators -->
      <div class="step-indicator">
        <div class="step active">
          <div class="step-dot">1</div>
          <span>填写信息</span>
        </div>
        <div class="step-line"></div>
        <div class="step">
          <div class="step-dot">2</div>
          <span>完成注册</span>
        </div>
      </div>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        size="large"
        @keyup.enter="handleRegister"
      >
        <el-form-item prop="username">
          <el-input
            v-model="form.username"
            placeholder="用户名"
            :prefix-icon="User"
          />
        </el-form-item>

        <el-form-item prop="email">
          <el-input
            v-model="form.email"
            placeholder="邮箱"
            :prefix-icon="Message"
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="密码（至少6位）"
            :prefix-icon="Lock"
            show-password
          />
          <!-- Password Strength Meter -->
          <div v-if="form.password" class="password-strength">
            <div class="strength-bar">
              <div
                class="strength-fill"
                :class="strengthClass"
                :style="{ width: strengthPercent + '%' }"
              />
            </div>
            <span class="strength-text" :class="strengthClass">{{ strengthLabel }}</span>
          </div>
        </el-form-item>

        <el-form-item prop="confirmPassword">
          <el-input
            v-model="form.confirmPassword"
            type="password"
            placeholder="确认密码"
            :prefix-icon="Lock"
            show-password
          />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            :loading="loading"
            style="width: 100%"
            @click="handleRegister"
          >
            {{ loading ? '注册中...' : '注 册' }}
          </el-button>
        </el-form-item>
      </el-form>

      <div class="auth-footer">
        已有账户？
        <router-link to="/login">返回登录</router-link>
      </div>
    </div>

    <!-- Success State -->
    <div class="auth-card success-card" v-else>
      <div class="success-icon">✅</div>
      <h2 class="auth-title">注册成功！</h2>
      <p class="auth-subtitle">{{ countdown }} 秒后自动跳转登录页</p>
      <el-button type="primary" @click="$router.push('/login')" style="width: 100%">
        立即登录
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { User, Lock, Message } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)
const registered = ref(false)
const countdown = ref(3)
let countdownTimer = null

const form = reactive({
  username: '',
  email: '',
  password: '',
  confirmPassword: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== form.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 50, message: '用户名长度3-50个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不少于6个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

// ===== Password Strength =====
const strengthScore = computed(() => {
  const p = form.password || ''
  let score = 0
  if (p.length >= 6) score++
  if (p.length >= 10) score++
  if (/[A-Z]/.test(p)) score++
  if (/[0-9]/.test(p)) score++
  if (/[^A-Za-z0-9]/.test(p)) score++
  return score
})

const strengthLabel = computed(() => {
  if (strengthScore.value <= 1) return '弱'
  if (strengthScore.value <= 3) return '中'
  return '强'
})

const strengthClass = computed(() => {
  if (strengthScore.value <= 1) return 'strength-weak'
  if (strengthScore.value <= 3) return 'strength-medium'
  return 'strength-strong'
})

const strengthPercent = computed(() => {
  return Math.min((strengthScore.value / 5) * 100, 100)
})

// Register
async function handleRegister() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const res = await request.post('/auth/register', {
      username: form.username,
      email: form.email,
      password: form.password
    })
    if (res.code === 200) {
      registered.value = true
      countdown.value = 3
      countdownTimer = setInterval(() => {
        countdown.value--
        if (countdown.value <= 0) {
          clearInterval(countdownTimer)
          router.push('/login')
        }
      }, 1000)
    } else {
      ElMessage.error(res.message || '注册失败')
    }
  } catch {
    // Error already handled by interceptor
  } finally {
    loading.value = false
  }
}

onUnmounted(() => {
  if (countdownTimer) clearInterval(countdownTimer)
})

// Generate random floating particles
const particles = ref(
  Array.from({ length: 10 }, (_, i) => ({
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
/* ===== Step Indicator ===== */
.step-indicator {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0;
  margin-bottom: 28px;
}

.step {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #c0c4cc;
}

.step.active {
  color: #667eea;
}

.step-dot {
  width: 22px;
  height: 22px;
  border-radius: 50%;
  background: #e8e8e8;
  color: #999;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 11px;
  font-weight: 700;
}

.step.active .step-dot {
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: #fff;
}

.step-line {
  width: 40px;
  height: 1px;
  background: #e8e8e8;
  margin: 0 8px;
}

/* ===== Password Strength ===== */
.password-strength {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-top: 6px;
}

.strength-bar {
  flex: 1;
  height: 4px;
  background: #ebeef5;
  border-radius: 2px;
  overflow: hidden;
}

.strength-fill {
  height: 100%;
  border-radius: 2px;
  transition: width 0.3s ease, background 0.3s ease;
}

.strength-weak { background: #f56c6c; color: #f56c6c; }
.strength-medium { background: #e6a23c; color: #e6a23c; }
.strength-strong { background: #67c23a; color: #67c23a; }

.strength-text {
  font-size: 12px;
  font-weight: 500;
  min-width: 20px;
}

/* ===== Success Card ===== */
.success-card {
  text-align: center;
}

.success-icon {
  font-size: 64px;
  margin-bottom: 12px;
  animation: success-pop 0.5s ease-out;
}

@keyframes success-pop {
  0% { transform: scale(0); opacity: 0; }
  60% { transform: scale(1.2); }
  100% { transform: scale(1); opacity: 1; }
}
</style>
