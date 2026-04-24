<template>
  <div>
  <a-row id="globalHeader" align="center" :wrap="false">
    <a-col flex="auto">
      <a-menu
        mode="horizontal"
        :selected-keys="selectedKeys"
        @menu-item-click="doMenuClick"
      >
        <a-menu-item
          key="0"
          :style="{ padding: 0, marginRight: '38px' }"
          disabled
        >
          <div class="title-bar">
            <div class="logo-mark">CP</div>
            <div class="title">Code Practice</div>
          </div>
        </a-menu-item>
        <a-menu-item v-for="item in filterRouter" :key="item.path">
          {{ item.name }}
        </a-menu-item>
      </a-menu>
    </a-col>
    <a-col flex="200px">
      <div class="username" v-if="!isLoggedIn">
        Добро пожаловать в Code Practice, <a-link href="/user/login">Войти</a-link>
      </div>
      <a-dropdown v-else @select="handleSelect">
        <div class="username">
          {{ displayUserName }}
        </div>
        <template #content>
          <a-doption v-if="!userStore.loginUser?.userRole" value="login">Войти</a-doption>
          <template v-else>
            <a-doption value="profile">Профиль</a-doption>
            <a-doption value="password">Смена пароля</a-doption>
            <a-doption value="logout">Выйти</a-doption>
          </template>
        </template>
      </a-dropdown>
    </a-col>
  </a-row>
  <a-modal
    v-model:visible="passwordModalVisible"
    title="Смена пароля"
    @ok="submitPassword"
    @cancel="passwordModalVisible = false"
    ok-text="Сохранить"
    cancel-text="Отмена"
    :loading="passwordLoading"
  >
      <a-form :model="passwordForm" layout="vertical">
        <a-form-item label="Текущий пароль" required>
          <a-input-password v-model="passwordForm.oldPassword" placeholder="Текущий пароль" />
        </a-form-item>
        <a-form-item label="Новый пароль" required>
          <a-input-password v-model="passwordForm.newPassword" placeholder="Новый пароль" />
        </a-form-item>
      </a-form>
    </a-modal>

    <a-modal
      v-model:visible="profileModalVisible"
      title="Профиль"
      @ok="submitProfile"
      @cancel="profileModalVisible = false"
      ok-text="Сохранить"
      cancel-text="Отмена"
      :loading="profileLoading"
      :mask-closable="false"
      width="560px"
    >
      <a-form :model="profileForm" layout="vertical">
        <a-form-item label="Аватар">
          <a-space align="start" :size="16">
            <a-avatar :size="72" shape="square">
              <img
                v-if="profilePreviewUrl"
                alt=""
                :src="profilePreviewUrl"
                style="width: 100%; height: 100%; object-fit: cover"
              />
              <span v-else style="font-size: 12px">нет фото</span>
            </a-avatar>
            <a-space direction="vertical" fill style="flex: 1">
              <a-input
                v-model="profileForm.userAvatar"
                allow-clear
                placeholder="https://… ссылка на картинку (PNG, JPG, WebP до 1 МБ при загрузке)"
              />
              <a-upload
                :show-file-list="false"
                accept=".png,.jpg,.jpeg,.webp,.svg,image/png,image/jpeg,image/webp,image/svg+xml"
                :custom-request="uploadAvatarCustomRequest"
              >
                <a-button type="outline" size="small">Загрузить файл на сервер</a-button>
              </a-upload>
              <span class="profile-hint">Файл не больше 1 МБ.</span>
            </a-space>
          </a-space>
        </a-form-item>
        <a-form-item field="userName" label="Отображаемое имя">
          <a-input v-model="profileForm.userName" allow-clear placeholder="Как вас показывать в интерфейсе" />
        </a-form-item>
        <a-form-item label="О себе">
          <a-textarea
            v-model="profileForm.userProfile"
            allow-clear
            placeholder="Кратко о себе (необязательно)"
            :max-length="500"
            show-word-limit
            :auto-size="{ minRows: 2, maxRows: 6 }"
          />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script lang="ts" setup>
import { routes } from "@/router/routes";
import { useRouter } from "vue-router";
import { computed, ref } from "vue";
import { useUserStore } from "@/stores/user";
import checkAccess from "@/access/checkAccess";
import accessEnum from "@/access/accessEnum";
import { UserControllerService } from "../../generated";
import type { UserUpdateMyRequest } from "../../generated";
import { Message } from "@arco-design/web-vue";
import type { RequestOption } from "@arco-design/web-vue/es/upload/interfaces";

const router = useRouter();

const userStore = useUserStore();

// Выделение пункта меню по умолчанию
const selectedKeys = ref(["/"]);

const passwordModalVisible = ref(false);
const passwordForm = ref({ oldPassword: "", newPassword: "" });
const passwordLoading = ref(false);

const profileModalVisible = ref(false);
const profileLoading = ref(false);
const profileForm = ref<UserUpdateMyRequest>({
  userName: "",
  userAvatar: "",
  userProfile: "",
});

function isLikelyDirectImageUrl(raw: string): boolean {
  const u = raw.trim();
  if (!u) return true;
  if (u.startsWith("data:image/")) return true;
  try {
    const parsed = new URL(u);
    if (parsed.protocol !== "http:" && parsed.protocol !== "https:") return false;
    const host = parsed.hostname.toLowerCase();
    if (host === "www.google.com" || host === "google.com") {
      if (parsed.pathname.startsWith("/search") || parsed.pathname.startsWith("/url")) return false;
    }
    const path = parsed.pathname.toLowerCase();
    if (path.includes("/file/local/")) return true;
    return /\.(png|jpe?g|webp|svg|gif|bmp|avif)(\?|$)/i.test(path);
  } catch {
    return false;
  }
}

const profilePreviewUrl = computed(() => {
  const u = profileForm.value.userAvatar?.trim();
  if (!u || !isLikelyDirectImageUrl(u)) return "";
  return u;
});

const isLoggedIn = computed(() => {
  const loginUser = userStore.loginUser;
  return Boolean(loginUser?.id && loginUser?.userRole && loginUser.userRole !== accessEnum.NOT_LOGIN);
});

const displayUserName = computed(() => {
  const raw = userStore.loginUser?.userName;
  const normalized = typeof raw === "string" ? raw.trim() : "";
  return normalized && normalized.toLowerCase() !== "null" ? normalized : "Пользователь";
});

const handleSelect = async (v: string | number | Record<string, unknown> | undefined) => {
  const key = typeof v === "string" ? v : typeof v === "number" ? String(v) : "";
  if (key === "password") {
    passwordForm.value = { oldPassword: "", newPassword: "" };
    passwordModalVisible.value = true;
    return;
  }
  if (key === "profile") {
    const u = userStore.loginUser;
    profileForm.value = {
      userName: (u?.userName ?? "").trim(),
      userAvatar: (u?.userAvatar ?? "").trim(),
      userProfile: (u?.userProfile ?? "").trim(),
    };
    profileModalVisible.value = true;
    return;
  }
  if (key === "logout") {
    const res = await UserControllerService.userLogout();
    if (res.code === 0) {
      userStore.resetGuest();
      router.push("/user/login");
      Message.success("Выход выполнен");
    } else {
      Message.error("Ошибка выхода: " + (res as { message?: string }).message);
    }
  }
};

async function submitProfile() {
  const avatar = profileForm.value.userAvatar?.trim() ?? "";
  if (avatar && !isLikelyDirectImageUrl(avatar)) {
    Message.error(
      "Укажите прямую ссылку на изображение (URL должен оканчиваться на .png, .jpg и т.п.) или загрузите файл. Ссылки на страницы поиска не подходят.",
    );
    return;
  }
  const body: UserUpdateMyRequest = {
    userName: profileForm.value.userName?.trim() || undefined,
    userAvatar: avatar || undefined,
    userProfile: profileForm.value.userProfile?.trim() || undefined,
  };
  profileLoading.value = true;
  try {
    const data = await UserControllerService.updateMyUser(body);
    if (data.code === 0) {
      await userStore.getLoginUser();
      Message.success("Профиль сохранён");
      profileModalVisible.value = false;
    } else {
      Message.error(data.message ?? "Не удалось сохранить профиль");
    }
  } finally {
    profileLoading.value = false;
  }
}

const AVATAR_MAX_BYTES = 1024 * 1024;

function uploadAvatarCustomRequest(option: RequestOption) {
  const file = option.fileItem.file;
  if (!file) {
    option.onError("Файл не выбран");
    return;
  }
  if (file.size > AVATAR_MAX_BYTES) {
    const mb = (file.size / (1024 * 1024)).toFixed(2);
    Message.error(`Файл слишком большой (${mb} МБ). Максимум 1 МБ — сожмите картинку или вставьте ссылку.`);
    option.onError("size");
    return;
  }
  option.onProgress(10);
  const fd = new FormData();
  fd.append("file", file);
  fd.append("biz", "user_avatar");
  fetch("/api/file/upload", {
    method: "POST",
    body: fd,
    credentials: "include",
  })
    .then(async (res) => {
      option.onProgress(80);
      const json = (await res.json()) as { code?: number; data?: string; message?: string };
      if (json.code === 0 && json.data) {
        profileForm.value.userAvatar = json.data;
        option.onSuccess(json);
        Message.success("Файл загружен, ссылка подставлена в поле аватара");
      } else {
        option.onError(json.message ?? "Ошибка загрузки");
        Message.error(json.message ?? "Ошибка загрузки файла");
      }
    })
    .catch((e) => {
      option.onError(e);
      Message.error("Сеть или сервер недоступны при загрузке");
    });
}

async function submitPassword() {
  const { oldPassword, newPassword } = passwordForm.value;
  if (!oldPassword?.trim() || !newPassword?.trim()) {
    Message.error("Введите текущий и новый пароль");
    return;
  }
  if (newPassword.length < 6) {
    Message.error("Новый пароль не менее 6 символов");
    return;
  }
  passwordLoading.value = true;
  try {
    const data = await UserControllerService.updateMyPassword({
      oldPassword: oldPassword.trim(),
      newPassword: newPassword.trim(),
    });
    if (data.code === 0) {
      Message.success("Пароль изменён");
      passwordModalVisible.value = false;
    } else {
      Message.error(data.message ?? "Ошибка смены пароля");
    }
  } finally {
    passwordLoading.value = false;
  }
}

// При переходе по маршруту — выделить пункт меню
router.afterEach((to) => {
  selectedKeys.value = [to.path];
});

// Переход по маршруту
const doMenuClick = (key: string) => {
  router.push({
    path: key,
  });
};

const filterRouter = computed(() => {
  const role = userStore.loginUser?.userRole;
  return routes.filter((item) => {
    if (item.meta?.hidden) {
      return false;
    }
    if (
      !checkAccess(userStore.loginUser, item?.meta?.access as string)
    ) {
      return false;
    }
    if (
      item.meta?.studentMenuOnly &&
      (role === accessEnum.TEACHER || role === accessEnum.ADMIN)
    ) {
      return false;
    }
    return true;
  });
});
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.title-bar {
  display: flex;
  align-items: center;
}
.logo {
  height: 40px;
}
.logo-mark {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  background: #165dff;
  color: #fff;
  font-size: 14px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
}
.title {
  color: #444;
  margin-left: 16px;
  font-weight: bolder;
}
.username {
  color: #444;
  font-weight: bolder;
  cursor: pointer;
  float: right;
  margin-right: 16px;
}

.profile-hint {
  display: block;
  font-size: 12px;
  color: var(--color-text-3);
  line-height: 1.4;
  max-width: 420px;
}
</style>
