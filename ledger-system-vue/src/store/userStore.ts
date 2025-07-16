import { defineStore } from "pinia";
import { ref } from "vue";
import {
  getLoginUserUsingGet,
  userLogoutUsingPost,
} from "@/api/userController";

/**
 * 登录用户信息全局状态
 */
export const useLoginUserStore = defineStore("loginUser", () => {
  // 优先从localStorage读取，保证刷新后依然有数据
  const localUser = localStorage.getItem("loginUser");
  const loginUser = ref<API.LoginUserVO>(
    localUser ? JSON.parse(localUser) : { name: "" }
  );

  async function fetchLoginUser() {
    const res = await getLoginUserUsingGet();
    if (res.data.code === 0 && res.data.data) {
      loginUser.value = res.data.data;
      localStorage.setItem("loginUser", JSON.stringify(res.data.data));
    }
  }

  async function fetchLogoutUser() {
    const res = await userLogoutUsingPost();
    if (res.data.code === 0) {
      setLoginUser({});
      localStorage.removeItem("loginUser");
    }
  }

  function setLoginUser(newLoginUser: API.LoginUserVO) {
    loginUser.value = newLoginUser;
  }

  return { loginUser, setLoginUser, fetchLoginUser, fetchLogoutUser };
});
