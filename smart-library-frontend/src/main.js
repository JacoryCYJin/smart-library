import './assets/main.css' // 确保这里引入了刚才配置好的 CSS

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'

// 1. 引入 Arco Design
import ArcoVue from '@arco-design/web-vue';
// Arco 的图标库（可选，做后台通常都需要）
import ArcoVueIcon from '@arco-design/web-vue/es/icon';

const app = createApp(App)

app.use(createPinia())
app.use(router)

// 2. 注册 Arco
app.use(ArcoVue);
app.use(ArcoVueIcon);

app.mount('#app')