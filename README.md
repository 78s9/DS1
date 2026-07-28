# 🚀 DS1 — 全栈用户认证系统

> 一个轻量级的 **Vue 3 + Spring Boot** 全栈登录注册项目，开箱即用 ✨

---

## 🧱 技术栈

| 层 | 技术 | 说明 |
|---|---|---|
| 🖥️ 前端 | Vue 3 + Vite + Element Plus | 现代化 SPA，界面美观 |
| 🗂️ 状态管理 | Pinia | Vue 3 官方推荐，轻量高效 |
| 🌐 HTTP | Axios | 请求拦截 + JWT 自动携带 |
| ⚙️ 后端 | Spring Boot 2.7 | Java 生态常青树 |
| 🔐 安全 | Spring Security + JWT | 无状态认证，安全可靠 |
| 🗄️ 数据库 | H2 (开发) / MySQL (生产) | 开发无需安装，上线无缝切换 |

---

## 🎯 功能一览

- 🔑 **用户注册** — 填写用户名密码，秒变会员
- 🔓 **用户登录** — JWT 令牌认证，安全又高效
- 🛡️ **路由守卫** — 未登录自动跳转登录页，已登录直接进控制台
- 📊 **仪表盘** — 登录成功后的个人主页（等你来扩展！）

---

## 🏗️ 项目结构

```
DS1/
├── frontend/                # 🖥️ Vue 3 前端
│   ├── src/
│   │   ├── views/           # 页面：Login / Register / Dashboard
│   │   ├── router/          # 路由 + 导航守卫
│   │   ├── store/           # Pinia 状态管理（auth）
│   │   └── utils/           # Axios 封装（JWT 拦截器）
│   └── vite.config.js       # Vite 配置 + API 代理
│
├── backend/                 # ⚙️ Spring Boot 后端
│   └── src/main/java/com/ds1/
│       ├── config/          # Security / CORS / JWT 过滤器
│       ├── controller/      # AuthController / UserController
│       ├── service/         # 业务逻辑层
│       ├── entity/          # JPA 实体（User）
│       ├── dto/             # 请求/响应 DTO
│       └── util/            # JWT 工具类
│
└── jdk1.8.0_202/            # ☕ 内置 JDK 8（无需额外安装）
```

---

## ⚡ 快速启动

### 1️⃣ 启动后端

```bash
cd backend

# Windows（使用项目自带 JDK 8）
..\jdk1.8.0_202\bin\java.exe -jar target/ds1-backend-1.0.0.jar

# 或者用 Maven 直接跑
mvnw spring-boot:run
```

> 🌱 默认使用 **H2 内存数据库**，无需安装任何数据库！  
> 后端跑在 → `http://localhost:8080`  
> H2 控制台 → `http://localhost:8080/h2-console`

### 2️⃣ 启动前端

```bash
cd frontend

npm install    # 首次运行
npm run dev    # 启动开发服务器
```

> 前端跑在 → `http://localhost:3000`  
> API 请求会自动代理到后端 `8080` 端口 🪄

### 3️⃣ 打开浏览器 🎉

访问 `http://localhost:3000` → 注册一个账号 → 登录 → 进入 Dashboard！

---

## 🔧 切换生产环境

```yaml
# backend/src/main/resources/application.yml
spring:
  profiles:
    active: prod   # 改为 prod，使用 MySQL
```

生产环境记得修改 MySQL 连接信息和你自己的 JWT 密钥哦 🔐

---

## 📌 注意事项

- 🧪 开发环境 JWT 密钥是硬编码的，**生产环境一定要换掉**！
- 🗃️ H2 是内存数据库，重启后数据会消失 — 开发调试正合适
- ☕ 项目自带 JDK 1.8，兼容性杠杠的

---

## 📄 License

MIT — 随便玩，随便改 🎸
