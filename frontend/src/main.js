import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
// Only import icons referenced by string name in templates (keeps the bundle small)
import { UserFilled, CircleCheck, InfoFilled, Warning } from '@element-plus/icons-vue'
import zhCn from 'element-plus/es/locale/lang/zh-cn'

import App from './App.vue'
import router from './router'
import { registerDirectives } from './directives'

const app = createApp(App)

// Register the icons used dynamically by name (e.g. <component :is="n.icon" />)
const globalIcons = { UserFilled, CircleCheck, InfoFilled, Warning }
for (const [key, component] of Object.entries(globalIcons)) {
  app.component(key, component)
}

// Register global custom directives
registerDirectives(app)

app.use(createPinia())
app.use(router)
app.use(ElementPlus, { locale: zhCn })
app.mount('#app')
