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
| POST | `/api/auth/register` | 用户注册（超限 429） | 公开 |
| POST | `/api/auth/login` | 用户登录，返回 JWT（超限 429） | 公开 |
| POST | `/api/auth/logout` | 用户登出（记录日志） | JWT |
| GET | `/api/user/me` | 获取当前用户信息 | JWT |
| PUT | `/api/user/me` | 更新当前用户资料（邮箱） | JWT |
| PUT | `/api/user/me/password` | 修改当前用户密码 | JWT |
| DELETE | `/api/user/{id}` | 删除用户 | ADMIN |
| PUT | `/api/user/{id}/role` | 修改用户角色 | ADMIN |
| GET | `/api/dashboard/stats` | 仪表盘统计（总用户 / 今日新增） | JWT |
| GET | `/api/dashboard/users` | 全部用户列表 | ADMIN |
| GET | `/api/logs` | 操作日志分页查询（page / size / keyword / action / module / status） | ADMIN |
| GET | `/api/logs/stats` | 今日日志统计 | ADMIN |

### 🛠️ 后端技术特性

- 🔐 Spring Security + JWT 无状态认证（token 内携带角色，映射 `ROLE_ADMIN` / `ROLE_USER`）
- 🔑 JWT 密钥由环境变量注入 + 启动期强度校验（缺失或 <32 字节直接拒绝启动）
- 🚦 登录/注册滑动窗口限流，超限返回 429（登录只计失败次数，成功即清零）
- 🛡️ 基于角色的接口鉴权（`hasRole("ADMIN")` 保护用户管理 / 日志等敏感接口）
- 🌐 CORS 来源白名单（不再通配符 + `allowCredentials`）
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
└── jdk1.8.0_202/            # ☕ 本地 JDK 8（可选，不入库，建议自装）
```

---

## ⚡ 快速启动

### 1️⃣ 启动后端

```bash
cd backend

# 方式一：直接用 Maven 跑（需要本机已装 JDK 8）
mvnw.cmd spring-boot:run

# 方式二：先打包再运行
mvnw.cmd clean package -DskipTests
java -jar target\ds1-backend-1.0.0.jar
```

> ⚠️ **已知问题（2026-09-14）**：`mvnw.cmd` 现在是坏的 —— 它会先下载 Maven，解压后因路径不匹配报
> 「系统找不到指定的路径」（原因见下方优化记录）。**已装 Maven** 的机器把上面命令里的 `mvnw.cmd` 换成 `mvn` 即可；
> 没装的机器暂时需要手动装一个 Maven 3.6+，或在 IDE 里直接运行 `Ds1Application`。
> `start-backend.bat` 走的也是同一个 `mvnw.cmd`，同样受影响。

> 🌱 默认使用 **H2 内存数据库**，无需安装任何数据库！  
> 后端跑在 → `http://localhost:8080`  
> H2 控制台 → `http://localhost:8080/h2-console`  
> 也可直接双击项目根目录的 `start-backend.bat`（优先使用本地 `jdk1.8.0_202`，否则用系统 Java）
>
> 🔑 **dev profile 内置了一个仅供本地开发的 JWT 密钥，clone 下来不用配任何环境变量就能跑**；
> 一旦设置 `JWT_SECRET` 环境变量即以它为准。这个内置密钥在非 dev profile 下会导致启动失败。

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

生产环境**必须**通过环境变量提供密钥与数据库凭据，缺了 `JWT_SECRET` 服务会直接拒绝启动（这是故意的，免得带着默认密钥上线）：

| 环境变量 | 必填 | 说明 |
|---|---|---|
| `JWT_SECRET` | ✅ | JWT 签名密钥，**至少 32 字节**（HS256 要求 256 bits） |
| `JWT_EXPIRATION` | | token 有效期（毫秒），默认 86400000（24 小时） |
| `DB_URL` | | MySQL 连接串，默认 `jdbc:mysql://localhost:3306/ds1db` |
| `DB_USERNAME` / `DB_PASSWORD` | ✅（password） | 数据库账号密码 |
| `CORS_ALLOWED_ORIGINS` | | 跨域白名单，逗号分隔；**prod 下默认为空，即不放开任何跨域** |
| `AUTH_LOGIN_MAX_FAILURES` 等 | | 限流阈值，见 `application.yml` 的 `app.auth-rate-limit` |

生成一个够强的密钥：

```bash
openssl rand -base64 48
```

---

## 📌 注意事项

- 🧪 JWT 密钥不再硬编码进仓库，改为环境变量 `JWT_SECRET` 注入；dev profile 有内置兜底密钥，**生产务必自己配一个**。
- ⚠️ **历史遗留**：2026-09-11 之前，一个真实可用的 JWT 密钥一直明文写在 `application.yml` 并已提交进 git 历史。
  即使现在删掉，它仍然存在于旧提交里，应当视作**已泄露**——所以不光是「以后别提交」，用旧密钥签发的 token 也应视为可伪造。
- 🗃️ H2 是内存数据库，重启后数据会消失 — 开发调试正合适
- ☕ 后端基于 JDK 1.8；JDK 不再随仓库提交，请自装 JDK 8 或保留本地 `jdk1.8.0_202`

---

## 🧹 优化记录

### 2026-09-14

> 本轮来自一次对外部视角的项目通读，四项依次完成，前两项在本地实跑验证。

**🔴 仓库瘦身：JDK 其实一直没被移出去**

- 🗑️ `jdk1.8.0_202/`（769 个文件，含 21MB 的 `src.zip`）与 `DS1.lnk` 真正停止跟踪，本地文件保留。
- 🐛 2026-08-17 那节声称已用 `git rm --cached` 移除，但该操作**从未提交**；而 `.gitignore` 对已被跟踪的文件不生效，所以 `jdk1.8.0_202/`、`*.lnk` 两条排除规则写了等于没写。这批文件白留了近一个月。
- ⚠️ **注意：这次提交不会让 GitHub 上的仓库变小。** 旧的 74MB 包仍在历史里，clone 依旧全量下载。要真正瘦身必须重写历史（`git filter-repo` / BFG）后强推 —— 那是破坏性操作，尚未执行。
- 📄 已修正 2026-08-17 那条不实记录。

**🔴 角色/账号变更对已签发的 token 即时生效**

- 🔐 无状态 JWT 的通病：token 一签发，里面的 `role` 就固定了。管理员把某人降级为 USER、或直接删号后，对方手上那个 token 在过期前（默认 24h）仍然自称 ADMIN，权限回收等于没做；被删的账号也照样能继续调接口。
- 🧩 `JwtAuthFilter` 改为用 token 里的用户名回查一次数据库，以库里为准：账号不存在则不认证，角色取数据库的值。代价是每个带 token 的请求多一次按用户名索引的查询；若日后成为瓶颈可加秒级 TTL 缓存，但那个 TTL 就是权限回收的最大延迟，需要显式选值而不是随手加。
- 🛡️ 顺带修掉一处让上面改动等于白做的配套问题：Spring Security 未配置 `AuthenticationEntryPoint` 时，「未认证」返回 403 而非 401，而前端 `request.js` 只在 401 分支清 token 跳登录页 —— 账号已删的用户会拿着作废的 token 卡在页面里出不去。现在未认证统一 401，已认证但权限不足仍保持 403。
- 📌 仍未解决：**改密码后旧 token 依然有效**。上面这招治不了它（用户还在、角色也没变），需要 token 版本号之类的机制。

**🟠 数据工坊两处内存泄漏**

- 🐛 `ComponentLab.vue` 的 `MouseTracker` 注释写着 "Setup/teardown"，但只有 setup：`window.addEventListener('mousemove', …)` 没有对应的 removeEventListener。每进一次组件实验室就多挂一个全局处理器，且回调会持续写入已销毁实例的 ref。
- 🐛 `AdvancedTable.vue` 的 `ResizeObserver` 返回值被直接丢弃，文件里也没有卸载钩子 —— 它持有被观察元素的强引用，不断开的话整个视口连同已渲染的行都无法回收。

**🟡 模拟数据加上显式标识**

- 📊 实时监控页状态栏原本是绿色脉冲点 +「实时连接中」，而 `connected` 写死 `true`，全页没有任何连接（`composables/useWebSocket.js` 写得很完整，但全项目没有任何地方 import 它）。现改为「模拟数据」标签 + 说明，并删掉 `connected` ref 与随之失效的 `.rm-dot` 样式。
- 🏷️ 其余加标识处：虚拟滚动表格的 10 万条记录、仪表盘「用户增长趋势」（除今日新增外的前 6 天是随机拆分）、「最近活动」、用户总数卡片的 sparkline、顶栏通知中心。均附 `title` 说明具体假在哪。

**验证结果**（本地 `dev` profile 实跑）

| 场景 | 结果 |
|---|---|
| USER 的 token 访问 `/api/logs` | ✅ 403 |
| 提升为 ADMIN 后，仍用**旧** token 访问 `/api/logs` | ✅ 200（即时升权） |
| 降回 USER 后，仍用**旧** token 访问 `/api/logs` | ✅ 403（即时降权） |
| 删号后，同一 token 访问 `/api/dashboard/stats` | ✅ 401（立即作废） |
| 伪造 token / 完全不带 token | ✅ 401 |
| 合法 USER 的 token 访问 ADMIN 专属接口 | ✅ 403（未被误伤） |
| `git ls-tree -r HEAD \| grep -c jdk1.8` | ✅ 0（原为 768） |
| `npm run build` / `mvnw compile` | ✅ 通过 |

> 📌 本轮新发现、尚未处理：
> - **`backend/mvnw.cmd` 是坏的**。它是手写的 24 行脚本（不是标准 Maven Wrapper），把 Maven 解压到 `dists/apache-maven-3.6.3/apache-maven-3.6.3`，却在第 17/19 行去 `dists/apache-maven-3.6.3-bin/…` 找 `mvn.cmd` —— 路径对不上。在没装 Maven 的机器上，它会先下载 10MB 再报「系统找不到指定的路径」。而 README 的快速启动恰恰让人跑 `mvnw.cmd`。
> - 历史里那个明文 JWT 密钥仍未轮换。仓库是**公开**的，任何 clone 过的人都能用旧密钥签发 `role=ADMIN` 的 token —— 前提是那个密钥曾在别处跑过。
> - 前端若干死控件：`UserManagement` 的 `sortable="custom"` 列没有 `@sort-change`、`OperationLogs` 的「刷新」不刷新统计卡、`Dashboard` 的 `markAllRead` 只改角标不改列表样式、`AdvancedTable` 的「批量通过」不清不楚地报成功但什么都不做。
> - `GlobalExceptionHandler` 的 `@ExceptionHandler(Exception.class)` 会把 JSON 格式错误（`HttpMessageNotReadableException`）也吞成 500，应为 400。
> - 全项目零测试（`backend/src` 下只有 `main`）。

### 2026-09-11

> 本轮针对一次安全审查中列出的高危项动手，四项均已在本地跑通验证。

**🔴 JWT 密钥外部化 + 启动期校验**

- 🔐 `application.yml` 里硬编码并已入库的 `jwt.secret` 改为 `${JWT_SECRET:}` 注入。原先任何人拿到这份代码都能签发 `role=ADMIN` 的 token，等于管理员权限公开。
- 🛡️ 新增 `JwtSecretValidator`：密钥为空或不足 32 字节（HS256 要求 256 bits）时**直接拒绝启动**，而不是打条警告继续跑。空密钥/弱密钥悄无声息地上生产，才是最危险的。
- 🧩 dev profile 保留一个内置兜底密钥（前缀 `ds1-dev-only`），保证 clone 下来零配置可运行；该密钥一旦出现在非 dev profile 下会启动失败。
- 🐛 `JwtUtil` 原来用 `signWith(HS256, String)`：jjwt 0.9.x 会把该字符串**当 BASE64 解码**，非 BASE64 字符被静默丢弃，导致真正参与签名的字节与校验的长度对不上。改为显式取 UTF-8 原始字节（RFC 7518），让上面的长度校验真正有意义。
- 🗄️ prod 的 MySQL 账号密码同样外部化（`DB_URL` / `DB_USERNAME` / `DB_PASSWORD`），`useSSL` 默认改为 `true`。

**🔴 登录/注册限流**

- 🚦 新增 `RateLimiter`（进程内滑动窗口，无第三方依赖），接入 `AuthController`，超限返回 **429**。
- 登录按「同一 IP + 用户名」只累计**失败**次数（默认 5 次 / 5 分钟），登录成功即清零 —— 正常用户不会因为自己的打字错误被锁死。
- 注册按「同一 IP」累计**全部**尝试（默认 10 次 / 60 分钟），针对批量刷号。
- 被限流期间不再续期窗口，避免攻击者靠持续重试把自己（或同 IP 用户）永久锁在门外。
- 阈值全部走配置 `app.auth-rate-limit.*`，可用环境变量调整，无需重新打包。

**🟠 隔离日志写入异常**

- 🐛 `LogAspect` 的 `logService.log(...)` 原来在 `try` 块**内**：一旦 DB 抖动导致写日志失败，异常会被同一个 `catch` 捕获 —— **已经成功提交的「删除用户」会给客户端返回 500**。`AuthController` 里同样的写法还会让注册/登录在成功时误报失败。
- 🧩 新增 `OperationLogService.logQuietly(...)`，把「写失败只记应用日志、绝不向上抛」下沉到服务层，切面与 `AuthController` 两条写日志路径一起受益，也避免在两个类里各写一遍 try/catch。

**🟠 CORS 收敛**

- 🛡️ `CorsConfig` 由 `allowedOriginPatterns("*")` + `allowCredentials(true)` 改为读取白名单 `app.cors.allowed-origins`；dev 默认放开 `localhost:3000`，prod 默认为空（不放开任何跨域），需要跨域时用 `CORS_ALLOWED_ORIGINS` 显式指定。
- 鉴权走 `Authorization` 头而非 Cookie，原先的实际危害有限，但通配符 + 携带凭证是教科书式错误配置，一旦改用 Cookie 承载凭证就会直接变成任意站点可读取用户数据的漏洞。

**验证结果**（本地 `dev` profile 实跑）

| 场景 | 结果 |
|---|---|
| 正常登录 | ✅ 200，拿到 token |
| 连续错误密码 5 次 | ✅ 均 401 |
| 第 6 次错误密码 | ✅ 429「登录失败次数过多，请 5 分钟后再试」 |
| 3 次错误 → 登录成功 → 再错 3 次 | ✅ 全程无 429，确认成功后配额已清零 |
| `Origin: http://localhost:3000` | ✅ 返回 `Access-Control-Allow-Origin` |
| `Origin: http://evil.com` | ✅ 已拒绝，无 ACAO 头 |
| `JWT_SECRET=tooshort` 启动 | ✅ 启动失败：「当前 8 字节，HS256 要求至少 32 字节」 |

> 📌 本轮未处理，仍留在待办清单上：改密码后旧 token 最长 24h 仍有效（无状态 JWT 通病）、`/h2-console/**` permitAll、`register` 唯一性检查的 TOCTOU、`updateProfile` 不校验邮箱格式、`DataInitializer` 用 `System.out.println` 打印默认密码、Spring Boot 2.7 / jjwt 0.9.1 已 EOL。

### 2026-08-17

**脚本 & 构建**

- 🐛 修复 `build-backend.bat` 路径损坏：路径里混进了退格符 `\b`（`DS1\backend` 变成 `DS1<退格>ackend`），脚本无法运行；同时移除硬编码的绝对路径，统一改用 `%~dp0` 相对定位。
- 🛡️ `start-backend.bat` / `start-frontend.bat` 增强：JDK 改为存在性判断（本地有 `jdk1.8.0_202` 才用，否则回退系统 Java）；前端仅首次（`node_modules` 不存在时）才执行 `npm install`。

**后端 — 权限 & 日志**

- 🛡️ 修复「修改角色」无自校验：`UserController.updateRole` 现传入 `Principal`，`UserService.updateRole` 增加「不能修改自己的角色」校验，与 `deleteUser` 及 README 描述对齐，避免管理员通过 API 自我降级、锁死系统。
- 🐛 修复操作日志「状态」筛选为死控件：`OperationLogController` / `OperationLogService` / `OperationLogRepository` 全链路补上 `status` 参数，前端 `loadLogs` 补发 `params.status`。
- 📊 日志统计补全局「总记录数」：`/api/logs/stats` 返回 `total`（`logRepository.count()`），前端卡片改用 `stats.total`，不再复用会随筛选变化的分页 `totalLogs`。

**前端**

- ♻️ `Dashboard.vue` 的 `fullscreenchange` 监听改为命名函数并在 `onUnmounted` 清理。

**工程 & 仓库**

- 🧹 `.gitignore` 加 `!backend/.mvn/wrapper/maven-wrapper.jar` 白名单（wrapper jar 重新纳入跟踪，新 clone 不再依赖联网下载）；排除 `jdk1.8.0_202/` 与 `*.lnk`。
- 🗑️ ~~停止跟踪整个 JDK（768 文件，约 375MB）与 `DS1.lnk`（`git rm --cached`，本地文件保留）。~~
  ⚠️ **这条当时并没有生效** —— 见 2026-09-14 记录：`git rm --cached` 执行后未提交，且 `.gitignore` 对已跟踪文件无效，
  768 个文件一直留在仓库里直到本次修复。
- 📄 README 快速启动命令修正：`mvnw` → `mvnw.cmd`（Windows）、补充打包步骤、JDK 改为「自装/可选」表述、日志 API 参数补 `status`。

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
