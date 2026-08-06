<template>
  <div class="system-settings">
    <div class="page-header">
      <h3>⚙️ 系统设置</h3>
    </div>

    <el-card shadow="hover">
      <div class="placeholder">
        <div class="placeholder-icon">🚧</div>
        <h2>功能开发中</h2>
        <p>系统设置页面正在紧锣密鼓地开发中，敬请期待！</p>
        <div class="coming-features">
          <el-tag>🎨 主题切换</el-tag>
          <el-tag>🔔 通知管理</el-tag>
          <el-tag>🔒 安全设置</el-tag>
          <el-tag>📋 操作日志</el-tag>
          <el-tag>💾 数据备份</el-tag>
        </div>
      </div>
    </el-card>

    <!-- Basic Settings (static demo) -->
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <span>🔐 安全配置</span>
          </template>
          <el-form label-width="100px" size="default">
            <el-form-item label="JWT 密钥">
              <el-input value="**********" disabled />
            </el-form-item>
            <el-form-item label="Token 有效期">
              <el-input value="24 小时" disabled />
            </el-form-item>
            <el-form-item label="密码策略">
              <el-tag type="success">已启用 (≥6位)</el-tag>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <span>📊 系统信息</span>
          </template>
          <el-descriptions :column="1" border size="small">
            <el-descriptions-item label="应用名称">DS1 管理系统</el-descriptions-item>
            <el-descriptions-item label="版本号">1.0.0</el-descriptions-item>
            <el-descriptions-item label="前端框架">Vue 3 + Element Plus</el-descriptions-item>
            <el-descriptions-item label="后端框架">Spring Boot 2.7</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
</script>

<style scoped>
.page-header {
  margin-bottom: 16px;
}

.page-header h3 { margin: 0; font-size: 18px; color: #303133; }

.placeholder {
  text-align: center;
  padding: 48px 20px;
}

.placeholder-icon {
  font-size: 72px;
  margin-bottom: 16px;
}

.placeholder h2 {
  margin: 0 0 8px;
  font-size: 22px;
  color: #303133;
}

.placeholder p {
  margin: 0 0 20px;
  color: #909399;
  font-size: 15px;
}

.coming-features {
  display: flex;
  justify-content: center;
  gap: 10px;
  flex-wrap: wrap;
}
</style>
