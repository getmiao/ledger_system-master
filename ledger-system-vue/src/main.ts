import { createApp } from "vue";
import App from "./App.vue";
import router from "./router";
import ElementPlus from "element-plus";
import "element-plus/dist/index.css";
import { createPinia } from "pinia";
import "@/assets/global.scss";

const pinia = createPinia();

// 屏蔽ResizeObserver loop相关的控制台报错
const observerErr = /ResizeObserver loop (limit|completed)/;
const realConsoleError = window.console.error;
window.console.error = (...args) => {
  if (observerErr.test(args[0])) return;
  realConsoleError.apply(window.console, args);
};

createApp(App).use(pinia).use(ElementPlus).use(router).mount("#app");
