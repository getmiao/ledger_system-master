<!-- eslint-disable prettier/prettier -->
<template>
  <div class="login-bg">
    <div class="login-glass">
      <div class="login-title">LOGIN</div>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="0">
        <el-form-item prop="userAccount">
          <el-input v-model="form.userAccount" placeholder="username" prefix-icon="el-icon-user" />
        </el-form-item>
        <el-form-item prop="userPassword">
          <el-input v-model="form.userPassword" type="password" placeholder="••••••" prefix-icon="el-icon-lock" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="onLogin" class="login-btn">Sign In</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { useRouter } from "vue-router";
import { userLoginUsingPost } from "@/api/userController";
import { ElMessage } from "element-plus";
import { useLoginUserStore } from "@/store/userStore";

const loginUserStore = useLoginUserStore();
const router = useRouter();
const formRef = ref();
const form = ref({
  userAccount: "",
  userPassword: "",
});
const rules = {
  userAccount: [{ required: true, message: "请输入账号", trigger: "blur" }],
  userPassword: [{ required: true, message: "请输入密码", trigger: "blur" }],
};

async function onLogin() {
  try {
    const res = await userLoginUsingPost(form.value);
    if (res?.data?.code === 0) {
      ElMessage.success("登录成功");
      router.push("/");
    } else {
      ElMessage.error(res?.data?.message || "登录失败");
    }
  } catch (error) {
    ElMessage.error("登录失败，请稍后重试");
  }
}
</script>

<style scoped>
.login-bg {
  position: fixed;
  inset: 0;
  min-height: 100vh;
  width: 100vw;
  background: linear-gradient(135deg, #b7e0c7 0%, #409eff 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}
.login-glass {
  width: 380px;
  padding: 48px 36px 36px 36px;
  background: rgba(255, 255, 255, 0.22);
  border-radius: 20px;
  box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.18);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 0.22);
  display: flex;
  flex-direction: column;
  align-items: center;
}
.login-title {
  font-size: 2.2rem;
  font-weight: bold;
  color: #1976d2;
  margin-bottom: 36px;
  letter-spacing: 2px;
  text-align: center;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}
.login-btn {
  width: 100%;
  background: linear-gradient(90deg, #409eff 60%, #1976d2 100%);
  border: none;
  font-size: 1.1rem;
  letter-spacing: 1px;
  color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(33, 150, 243, 0.1);
  transition: background 0.2s;
}
.login-btn:hover {
  background: linear-gradient(90deg, #1976d2 60%, #409eff 100%);
}
.el-input__wrapper {
  background: rgba(255, 255, 255, 0.7);
  border-radius: 10px;
  border: 1px solid #e0e3ea;
}
html,
body,
#app {
  height: 100%;
  margin: 0;
  padding: 0;
  overflow: hidden;
}
</style>
