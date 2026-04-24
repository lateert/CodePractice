import type { Pinia } from "pinia";
import router from "@/router";
import ACCESS_ENUM from "@/access/accessEnum";
import checkAccess from "@/access/checkAccess";
import { useUserStore } from "@/stores/user";

export function setupAccessGuards(pinia: Pinia) {
  router.beforeEach(async (to, from, next) => {
    const userStore = useUserStore(pinia);
    console.log("Информация о пользователе", userStore.loginUser);
    let loginUser = userStore.loginUser;
    const path = to.path;
    if ("Не авторизован" !== loginUser.userName) {
      if (path === "/user/login") {
        next("/");
        return;
      }
    }
    if (!loginUser || !loginUser.userRole) {
      await userStore.getLoginUser();
      loginUser = userStore.loginUser;
    }
    const needAccess = (to.meta?.access as string) ?? ACCESS_ENUM.NOT_LOGIN;
    if (needAccess !== ACCESS_ENUM.NOT_LOGIN) {
      if (
        !loginUser ||
        !loginUser.userRole ||
        loginUser.userRole === ACCESS_ENUM.NOT_LOGIN
      ) {
        next(`/user/login?redirect=${to.fullPath}`);
        return;
      }
      if (!checkAccess(loginUser, needAccess)) {
        next("/noAuth");
        return;
      }
    }
    if (
      to.meta?.studentMenuOnly &&
      (loginUser.userRole === ACCESS_ENUM.TEACHER ||
        loginUser.userRole === ACCESS_ENUM.ADMIN)
    ) {
      next("/");
      return;
    }
    next();
  });
}
