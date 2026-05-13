<template>
  <div class="body">
    <a-card :style="{ width: '480px', margin: '40px auto' }">
      <h2>Вход</h2>
      <a-form
        style="max-width: 480px; margin: 0 auto"
        label-align="left"
        auto-label-width
        :model="form"
        @submit="handleSubmit"
      >
        <a-form-item field="userAccount" label="Логин">
          <a-input v-model="form.userAccount" placeholder="Введите логин" />
        </a-form-item>
        <a-form-item
          field="userPassword"
          tooltip="Не менее 8 символов"
          label="Пароль"
        >
          <a-input-password
            v-model="form.userPassword"
            placeholder="Введите пароль"
          />
        </a-form-item>
        <a-button
          type="primary"
          html-type="submit"
          style="width: 120px; margin: 0 auto"
          >Войти</a-button
        >
        <a-button
          style="width: 200px; margin: 12px auto 0 auto; display: block"
          @click="loginWithGithub"
        >
          Войти через GitHub
        </a-button>
        <div class="register">
          Нет аккаунта? <a-link href="/user/register" class="register"
            >Зарегистрироваться</a-link
          >
        </div>
      </a-form>
    </a-card>
  </div>
</template>
<script lang="ts" setup>
import { reactive } from "vue";
import { UserControllerService, UserLoginRequest } from "../../../generated";
import { Message } from "@arco-design/web-vue";
import { useRouter } from "vue-router";
import axios from "axios";
import { useUserStore } from "@/stores/user";

const router = useRouter();
const userStore = useUserStore();

const form = reactive({
  userAccount: "",
  userPassword: "",
} as UserLoginRequest);

const handleSubmit = async () => {
  const res = await UserControllerService.userLogin(form);
  console.log(res);
  if (res.code === 0) {
    Message.success("Вход выполнен");
    await userStore.getLoginUser();
    router.push({
      path: "/",
      replace: true,
    });
  } else {
    if (res.code === 40100 || res.code === 40101) {
      Message.error("Ошибка входа: неверный логин или пароль");
    } else if (res.code === 40000) {
      Message.error("Ошибка входа: некорректные данные");
    } else if (res.code === 50000) {
      Message.error("Ошибка входа: внутренняя ошибка сервера");
    } else {
      Message.error("Ошибка входа. Код: " + res.code);
    }
  }
};

const loginWithGithub = async () => {
  try {
    const res = await axios.get("/api/v1/oauth/github/login-url", {
      params: { state: "code-practice-login" },
    });
    const payload = res.data as { code?: number; data?: string; message?: string };
    if (payload?.code === 0 && payload?.data) {
      window.location.href = payload.data;
      return;
    }
    Message.error(
      payload?.message?.trim() ||
        "Не удалось получить ссылку OAuth (проверьте GITHUB_CLIENT_ID и GITHUB_CLIENT_SECRET на сервере)"
    );
  } catch (e) {
    Message.error("Ошибка при инициализации GitHub OAuth");
  }
};
</script>

<style scoped>
.register {
  font-size: 11px;
}
.register:first-line {
  color: rgb(128, 128, 128);
}
</style>
