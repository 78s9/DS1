<template>
  <div class="not-found-container">
    <div class="not-found-content">
      <div class="not-found-code">
        <span class="digit" v-for="(d, i) in '404'" :key="i" :style="{ animationDelay: i * 0.15 + 's' }">{{ d }}</span>
      </div>
      <div class="not-found-icon">👻</div>
      <h2>页面走丢了</h2>
      <p>你访问的页面不存在，或者已被移动到其他地方</p>
      <div class="not-found-actions">
        <el-button type="primary" size="large" @click="$router.push('/dashboard')">
          <el-icon><HomeFilled /></el-icon>
          返回首页
        </el-button>
        <el-button size="large" @click="$router.back()">
          <el-icon><Back /></el-icon>
          返回上页
        </el-button>
      </div>
    </div>

    <!-- Background decoration -->
    <div class="bg-circle circle-1" />
    <div class="bg-circle circle-2" />
    <div class="bg-circle circle-3" />
  </div>
</template>

<script setup>
import { HomeFilled, Back } from '@element-plus/icons-vue'
</script>

<style scoped>
.not-found-container {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e7ed 100%);
  position: relative;
  overflow: hidden;
}

.not-found-content {
  text-align: center;
  position: relative;
  z-index: 1;
}

.not-found-code {
  display: flex;
  justify-content: center;
  gap: 12px;
  margin-bottom: 12px;
}

.digit {
  font-size: 120px;
  font-weight: 900;
  color: #303133;
  opacity: 0;
  animation: digit-in 0.6s ease-out forwards;
  text-shadow: 4px 4px 0 rgba(102, 126, 234, 0.15);
}

@keyframes digit-in {
  from {
    opacity: 0;
    transform: translateY(-40px) rotateX(90deg);
  }
  to {
    opacity: 1;
    transform: translateY(0) rotateX(0);
  }
}

.not-found-icon {
  font-size: 64px;
  margin-bottom: 12px;
  animation: ghost-float 2s ease-in-out infinite;
}

@keyframes ghost-float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-12px); }
}

.not-found-content h2 {
  margin: 0 0 8px;
  font-size: 24px;
  color: #303133;
}

.not-found-content p {
  margin: 0 0 28px;
  color: #909399;
  font-size: 15px;
}

.not-found-actions {
  display: flex;
  gap: 12px;
  justify-content: center;
}

/* ===== Background Circles ===== */
.bg-circle {
  position: absolute;
  border-radius: 50%;
  opacity: 0.06;
}

.circle-1 {
  width: 400px;
  height: 400px;
  background: #667eea;
  top: -100px;
  right: -100px;
}

.circle-2 {
  width: 300px;
  height: 300px;
  background: #764ba2;
  bottom: -80px;
  left: -60px;
}

.circle-3 {
  width: 200px;
  height: 200px;
  background: #667eea;
  top: 50%;
  left: 10%;
}
</style>
