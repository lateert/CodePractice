<template>
  <div class="body">
    <a-card :style="{ width: '480px', margin: '40px auto' }">
      <h2>Регистрация</h2>
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
        <a-form-item field="email" label="Email">
          <a-input v-model="form.email" placeholder="Введите email" />
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
        <a-form-item
          field="userPasswordCheck"
          tooltip="Не менее 8 символов"
          label="Повторите пароль"
        >
          <a-input-password
            v-model="form.checkPassword"
            placeholder="Повторите пароль"
          />
        </a-form-item>
        <a-button
          type="primary"
          html-type="submit"
          class="register-btn"
          >Зарегистрироваться</a-button
        >
        <div class="login-hint">
          Есть аккаунт? <a-link href="/user/login">Войти</a-link>
        </div>
      </a-form>
    </a-card>
  </div>
</template>
<script lang="ts" setup>
import { reactive } from "vue";
import { UserControllerService, UserRegisterRequest } from "../../../generated";
import { Message } from "@arco-design/web-vue";
import { useRouter } from "vue-router";

const router = useRouter();

const form = reactive({
  userAccount: "",
  userPassword: "",
  checkPassword: "",
  email: "",
} as UserRegisterRequest);

const handleSubmit = async () => {
  const res = await UserControllerService.userRegister(form);
  console.log(res);
  if (res.code === 0) {
    Message.success("Регистрация успешна");
    router.push({
      path: "/user/login",
      replace: true,
    });
  } else {
    Message.error("Ошибка регистрации: " + res.message);
  }
};
</script>

<style scoped>
.register-btn {
  width: 180px;
  height: 40px;
  margin: 0 auto;
  font-size: 15px;
}

.login-hint {
  margin-top: 10px;
  font-size: 12px;
  color: rgb(128, 128, 128);
}
</style>
