import { defineStore } from "pinia";
import { ref } from "vue";
import accessEnum from "@/access/accessEnum";
import { UserControllerService } from "../../generated";
import type { LoginUserVO } from "../../generated";

function guestUser(): LoginUserVO {
  return { userName: "Не авторизован" };
}

export const useUserStore = defineStore("user", () => {
  const loginUser = ref<LoginUserVO>(guestUser());

  function setLoginUser(payload: LoginUserVO) {
    loginUser.value = payload;
  }

  async function getLoginUser() {
    const res = await UserControllerService.getLoginUser();
    if (res.code === 0 && res.data) {
      loginUser.value = res.data;
    } else {
      loginUser.value = {
        ...loginUser.value,
        userRole: accessEnum.NOT_LOGIN,
      };
    }
  }

  function resetGuest() {
    loginUser.value = guestUser();
  }

  return { loginUser, setLoginUser, getLoginUser, resetGuest };
});
