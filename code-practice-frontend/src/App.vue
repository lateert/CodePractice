<template>
  <div id="app">
    <template
      v-if="route.path === '/user/login' || route.path === '/user/register'"
    >
      <router-view />
    </template>
    <template v-else>
      <BasicLayout />
    </template>
  </div>
</template>

<style>
.arco-pagination-total-text,
.arco-pagination-total {
  display: none !important;
}

/* Единый стиль для модалок подтверждения удаления */
.delete-confirm-modal .arco-modal {
  border-radius: 14px;
  overflow: hidden;
}

.delete-confirm-modal .arco-modal-header {
  padding: 18px 20px 8px;
}

.delete-confirm-modal .arco-modal-title {
  font-weight: 700;
}

.delete-confirm-modal .arco-modal-body {
  padding: 12px 20px 8px;
  color: var(--color-text-1);
  font-size: 16px;
}

.delete-confirm-modal .arco-modal-footer {
  padding: 14px 20px 18px;
}
</style>
<script setup lang="ts">
import BasicLayout from "@/layouts/BasicLayout.vue";
import { onMounted } from "vue";
import { useRoute } from "vue-router";

const route = useRoute();

/**
 * Глобальная инициализация (однократный вызов при загрузке приложения)
 */
const doInit = () => {
  console.log("hello world");
};

onMounted(() => {
  doInit();
});
</script>
