<template>
  <div class="oauth-callback">
    <a-spin tip="Выполняется вход через GitHub..." />
  </div>
</template>

<script lang="ts" setup>
import axios from "axios";
import { Message } from "@arco-design/web-vue";
import { onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useUserStore } from "@/stores/user";

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();

onMounted(async () => {
  try {
    const code = route.query.code as string;
    if (!code) {
      Message.error("GitHub OAuth: отсутствует code");
      await router.replace("/user/login");
      return;
    }
    const res = await axios.get("/api/v1/oauth/github/callback", {
      params: { code, state: route.query.state },
      withCredentials: true,
    });
    if (res.data?.code === 0) {
      await userStore.getLoginUser();
      Message.success("Вход через GitHub выполнен");
      await router.replace("/");
      return;
    }
    Message.error("GitHub OAuth: вход не выполнен");
    await router.replace("/user/login");
  } catch (e) {
    Message.error("GitHub OAuth: ошибка авторизации");
    await router.replace("/user/login");
  }
});
</script>

<style scoped>
.oauth-callback {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 220px;
}
</style>
