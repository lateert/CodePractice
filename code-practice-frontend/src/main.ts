import { createApp } from "vue";
import App from "./App.vue";
import ArcoVue, { addI18nMessages, useLocale } from "@arco-design/web-vue";
import "@arco-design/web-vue/dist/arco.css";
import ArcoVueIcon from "@arco-design/web-vue/es/icon";
import { createPinia } from "pinia";
import router from "./router";
import { setupAccessGuards } from "./access";
import "bytemd/dist/index.css";
import "highlight.js/styles/atom-one-dark.css";
import "highlight.js/lib/common";
import hljsVuePlugin from "@highlightjs/vue-plugin";
import enUS from "@arco-design/web-vue/es/locale/lang/en-us";

const ruLikeLocale: any = {
  ...enUS,
  locale: "ru-RU",
  empty: { ...(enUS as any).empty, description: "Нет данных" },
  modal: { ...(enUS as any).modal, okText: "Сохранить", cancelText: "Отмена" },
  drawer: { ...(enUS as any).drawer, okText: "Сохранить", cancelText: "Отмена" },
  popconfirm: { ...(enUS as any).popconfirm, okText: "Да", cancelText: "Нет" },
  pagination: {
    ...(enUS as any).pagination,
    goto: "Перейти",
    page: "стр.",
    countPerPage: "записей на стр.",
    total: "Всего: {0}",
  },
  table: { ...(enUS as any).table, okText: "ОК", resetText: "Сбросить" },
  upload: {
    ...(enUS as any).upload,
    start: "Начать",
    cancel: "Отмена",
    delete: "Удалить",
    retry: "Нажмите, чтобы повторить",
    buttonText: "Загрузить",
    preview: "Предпросмотр",
    drag: "Нажмите или перетащите файл сюда",
    dragHover: "Отпустите файл для загрузки",
    error: "Ошибка загрузки",
  },
};

addI18nMessages({ "ru-RU": ruLikeLocale });
useLocale("ru-RU");

if (typeof window !== "undefined") {
  window.addEventListener("error", (event) => {
    const message = event?.message || "";
    if (message.includes("ResizeObserver loop completed with undelivered notifications")) {
      event.stopImmediatePropagation();
    }
  });
}

const pinia = createPinia();
setupAccessGuards(pinia);

createApp(App)
  .use(ArcoVue)
  .use(pinia)
  .use(router)
  .use(ArcoVueIcon)
  .use(hljsVuePlugin)
  .mount("#app");
