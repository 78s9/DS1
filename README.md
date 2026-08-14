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

## 📖 功能详解

### 🔐 1. 认证模块

| 功能 | 说明 |
|---|---|
| 用户注册 | 用户名 / 邮箱 / 密码；表单校验（用户名 3-50 位、密码 ≥6 位、邮箱格式、两次密码一致）；**密码强度检测**（弱 / 中 / 强 进度条）；注册成功后 3 秒倒计时自动跳转登录页 |
| 用户登录 | 用户名 + 密码；「记住用户名」用 `localStorage` 持久化；忘记密码提示联系管理员 |
| JWT 认证 | 登录返回 JWT，前端存入 `localStorage`，Axios 请求拦截器自动携带 `Authorization: Bearer <token>` |
| 路由守卫 | 未登录访问受保护页 → 跳登录页；已登录访问登录/注册页 → 跳控制台；token 过期自动清理并跳转 |
| 默认账号 | 首次启动自动创建管理员 `admin` / `admin123`（角色 `ADMIN`） |

### 📊 2. 仪表盘（Dashboard Home）

- 👋 欢迎横幅 + 分时段问候语 + 当前日期
- 💡 每日一句（内置名人名言，可随机刷新）
- 📈 统计卡片：用户总数（数字滚动动画 + 迷你折线 sparkline）、今日新增、我的角色、后端状态（在线 / 响应耗时）
- 📊 用户增长趋势柱状图（近 7 天，动态模拟）
- 🕐 最近活动时间线
- 📋 个人信息（用户名 / 邮箱 / 角色 / 注册时间）
- ⚡ 快捷操作入口（用户管理 / 刷新数据 / 数据分析占位 / 数据导出占位）

### 👥 3. 用户管理（仅管理员可见）

- 用户列表表格（彩色头像、用户名、邮箱、角色、注册时间）
- 关键字搜索（用户名 / 邮箱）+ 角色筛选
- 前端分页（10 / 20 / 50）
- 点击行 / 查看按钮 → 用户详情弹窗
- 删除用户（真实调用后端 `DELETE /api/user/{id}`，不能删除自己）
- 角色切换（管理员 ⇄ 普通用户，调用 `PUT /api/user/{id}/role`，不能修改自己）
- 导出 Excel（占位，已禁用）

### 📋 4. 操作日志

- 日志统计卡片（今日操作 / 成功 / 失败 / 总记录数）
- 日志列表（用户、操作类型、模块、描述、IP、状态、时间）
- 多条件筛选：关键词、操作类型、模块、状态
- 服务端分页（10 / 20 / 50 / 100）

### 🔮 5. 数据工坊（Vue 3 特性展示，Tab 切换 + KeepAlive 缓存）

| 子页 | 功能 |
|---|---|
| 虚拟滚动表格 | 一次生成 **10 万条** 数据，仅渲染可视区行（虚拟滚动）；搜索 / 状态筛选 / 列排序 / 多选 / 批量操作 |
| 实时监控 | KPI 卡片（数字滚动动画）、CPU 实时曲线（SVG）、内存使用占比、网络 I/O 双向上行下行、实时告警流（TransitionGroup 动画） |
| 组件实验室 | Renderless 组件、复合组件（Provide/Inject）、Teleport 传送门、Suspense + 骨架屏、Schema 驱动动态组件 |
| 指令展示 | 自定义指令 `v-ripple` / `v-click-outside` / `v-intersect` / `v-autofocus` / `v-draggable` 的可交互演示 |

### ⚙️ 6. 系统设置

- 占位提示 + 规划中的功能标签
- 安全配置展示（JWT 密钥、Token 有效期、密码策略）
- 系统信息（应用名、版本、前后端框架）

### 🌐 7. 全局功能

- 🎨 **主题系统**：7 种主题色（极光紫 / 海洋蓝 / 翡翠绿 / 活力橙 / 中国红 / 青碧色 / 樱花粉）+ 暗黑模式，`localStorage` 持久化
- ⌨️ **命令面板**（`Ctrl+K` / `Cmd+K`）：模糊搜索导航与快捷操作，`↑↓` 导航、`Enter` 执行、`Esc` 关闭；`Ctrl+D` 一键切换暗黑模式
- 📝 **便签**（Sticky Notes）：右下角悬浮球，支持增删便签，`localStorage` 持久化
- 🖥️ 全屏模式
- 👻 404 页面（数字入场动画 + 返回导航）

### 👤 8. 顶栏用户中心（登录后）

- **用户下拉菜单**：显示邮箱 / 编辑资料 / 修改密码 / 退出登录
- ✏️ **编辑资料**：弹窗修改邮箱（`PUT /api/user/me`），保存后刷新用户信息
- 🔑 **修改密码**：校验原密码 + 新密码（≥6 位）+ 二次确认，成功后强制重新登录（`PUT /api/user/me/password`）
- 🚪 **退出登录**：确认后调用登出接口（`POST /api/auth/logout`，记录日志）再清除本地 token
- 🔔 **通知中心**：未读角标 + 消息列表 + 一键「全部已读」
- 🛡️ **角色权限**：菜单按角色显示，非管理员看不到「用户管理 / 操作日志」，直接访问会被路由守卫重定向回首页

### 🧩 9. 通用组件（`components/`）

| 组件 | 说明 |
|---|---|
| AnimatedNumber | 数字滚动动画（ease-out + requestAnimationFrame） |
| CommandPalette | 全局命令面板（Teleport + 键盘导航 + 分组结果） |
| DailyQuote | 每日一句 |
| ErrorBoundary | 组件错误边界（onErrorCaptured + 重试） |
| Flipper | FLIP 动画容器 |
| SkeletonLoader | 骨架屏（line / card / table 三种形态） |
| StickyNotes | 悬浮便签 |
| VirtualList | 无渲染虚拟滚动列表 |

### 🔌 10. 组合式函数（`composables/`）

| 函数 | 说明 |
|---|---|
| useClipboard | 剪贴板复制 / 粘贴（含降级方案） |
| useCountUp | 数字计数动画 |
| useDebounce | 防抖（支持 Ref 与函数两种模式） |
| useDragDrop | 拖拽排序逻辑层 |
| useIdle | 用户空闲状态检测 |
| useVirtualScroll | 虚拟滚动逻辑层 |
| useWebSocket | WebSocket 封装（自动重连 / 心跳 / 消息队列） |

### 🎯 11. 自定义指令（`directives/`）

| 指令 | 说明 |
|---|---|
| `v-ripple` | Material 波纹点击效果 |
| `v-click-outside` | 点击元素外部触发回调 |
| `v-intersect` | 基于 IntersectionObserver 的可见性检测 |
| `v-autofocus` | 挂载后自动聚焦（支持选中 / 延迟） |
| `v-draggable` | 原生 HTML5 拖拽排序 |

### 🔌 后端 API 一览

| 方法 | 路径 | 说明 | 鉴权 |
|---|---|---|---|
| POST | `/api/auth/register` | 用户注册 | 公开 |
| POST | `/api/auth/login` | 用户登录，返回 JWT | 公开 |
| POST | `/api/auth/logout` | 用户登出（记录日志） | JWT |
| GET | `/api/user/me` | 获取当前用户信息 | JWT |
| PUT | `/api/user/me` | 更新当前用户资料（邮箱） | JWT |
| PUT | `/api/user/me/password` | 修改当前用户密码 | JWT |
| DELETE | `/api/user/{id}` | 删除用户 | ADMIN |
| PUT | `/api/user/{id}/role` | 修改用户角色 | ADMIN |
| GET | `/api/dashboard/stats` | 仪表盘统计（总用户 / 今日新增） | JWT |
| GET | `/api/dashboard/users` | 全部用户列表 | ADMIN |
| GET | `/api/logs` | 操作日志分页查询（page / size / keyword / action / module） | ADMIN |
| GET | `/api/logs/stats` | 今日日志统计 | ADMIN |

### 🛠️ 后端技术特性

- 🔐 Spring Security + JWT 无状态认证（token 内携带角色，映射 `ROLE_ADMIN` / `ROLE_USER`）
- 🛡️ 基于角色的接口鉴权（`hasRole("ADMIN")` 保护用户管理 / 日志等敏感接口）
- ⚠️ 全局异常处理（`@RestControllerAdvice`，业务异常与参数校验统一转 400，前端能拿到具体错误信息）
- 📝 AOP 切面自动记录操作日志（`LogAspect`，跳过查询类请求减少噪音）
- 🗃️ JPA 实体自动填充创建 / 更新时间
- 🌱 启动时自动初始化默认管理员（`DataInitializer`）
- 🌍 CORS 跨域配置

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

## 🧹 优化记录

### 2026-08-14

**后端 — 修复 & 异常处理统一**

- 🐛 修复操作日志重复/误记：`LogAspect` 拦截所有 `@RestController`，而 `AuthController` 又手动记录日志，导致注册/登录记两条、失败操作被误记成 SUCCESS（切面看不到 controller 内部 catch 掉的异常）。现将 `AuthController` 从切面排除，由其自行记录，可保留失败时的尝试用户名。
- 🧩 新增 `BusinessException`（携带 HTTP 状态码），`UserService` 按语义抛出：登录失败 401、资源不存在 404、其余校验 400；`GlobalExceptionHandler` 统一转换为响应，未预期异常记日志并返回 500 通用信息（不泄露内部细节）。
- ♻️ `AuthController` 移除硬编码状态码，改为 rethrow `BusinessException`，响应格式统一交给全局异常处理器。
- 🛡️ `UserService.updateProfile` 增加邮箱非空校验。

**前端**

- 🛡️ `request.js` 拦截器用可选链保护 `error.config`，避免极端情况下拦截器自身抛错。

### 2026-08-13

**后端 — 安全 & 健壮性**

- 🔐 修复 JWT 角色映射失效：`JwtAuthFilter` 原来给所有 token 一律授权 `ROLE_USER`，token 里的 `role` 声明未被使用。现改为从 token 解析并映射 `ROLE_ADMIN` / `ROLE_USER`；同时将「校验 + 取用户名」两次 parse 合并为一次 `JwtUtil.parseToken()`，并删除不再使用的辅助方法。
- 📄 分页参数钳制：`OperationLogService.query` 对 `page`（<1 归 1）与 `size`（钳到 1~100）做上下限处理，避免 `page=0` 抛异常与 `size` 被恶意放大。
- 🧩 抽取 `ClientIpUtil`：合并 `AuthController` 与 `LogAspect` 中重复的 `getClientIp` 逻辑，并补充 `X-Forwarded-For` 多段 IP 取首段的处理。
- 🗑️ 清理死代码：移除 `OperationLogService.getStats` 中未使用的 `actionTypes` 列表。

**前端 — 体积 & 清理**

- 🎨 图标按需注册：`main.js` 由 `import *` 全量注册约 300 个图标，改为仅注册字符串名动态引用的 4 个图标（`UserFilled`、`CircleCheck`、`InfoFilled`、`Warning`），缩小打包体积。
- 🧹 去除重复样式：删除 `App.vue` 中重复的 `html.dark .header` 规则。

> 📌 后续可优化：Element Plus 全量引入可改为 `unplugin-vue-components` 按需引入（当前最大体积项）。

---

## 📄 License

MIT — 随便玩，随便改 🎸
