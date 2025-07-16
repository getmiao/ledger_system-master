<!-- eslint-disable prettier/prettier -->
<template>
  <div class="user-profile">
    <el-card>
      <h2>修改信息</h2>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px" style="max-width: 420px; margin: 0 auto">
        <el-form-item label="用户名" prop="name">
          <el-input v-model="form.name" placeholder="请输入用户名" clearable />
        </el-form-item>
        <el-form-item label="原密码" prop="oldPassword" required>
          <el-input v-model="form.oldPassword" placeholder="请输入原密码" show-password clearable />
        </el-form-item>
        <el-form-item label="新密码" prop="userPassword">
          <el-input v-model="form.userPassword" placeholder="如需修改请输入新密码（不少于8位）" show-password clearable />
        </el-form-item>
        <el-form-item label="部门" prop="department">
          <el-input v-model="form.department" placeholder="请输入部门" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" style="width: 100%" @click="onSubmit">保存修改</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { ElMessage } from "element-plus";
import api from "@/api";
import { useLoginUserStore } from "@/store/userStore";
import { useRouter } from "vue-router";

const loginUserStore = useLoginUserStore();
const router = useRouter();
const formRef = ref();

const form = ref({
  id: loginUserStore.loginUser.id,
  name: loginUserStore.loginUser.name,
  oldPassword: "",
  userPassword: "",
  department: loginUserStore.loginUser.department,
});

// 表单验证规则
const rules = {
  name: [{ required: true, message: "用户名不能为空", trigger: "blur" }],
  oldPassword: [{ required: true, message: "原密码不能为空", trigger: "blur" }],
  userPassword: [
    {
      validator: (rule: any, value: string, callback: any) => {
        if (value && value.length < 8) {
          callback(new Error("新密码长度不能少于8位"));
        } else {
          callback();
        }
      },
      trigger: "blur",
    },
  ],
  department: [{ required: true, message: "部门不能为空", trigger: "blur" }],
};

async function onSubmit() {
  try {
    await formRef.value.validate();

    const params: any = {
      id: form.value.id,
      name: form.value.name,
      department: form.value.department,
      oldPassword: form.value.oldPassword,
    };

    if (form.value.userPassword) {
      params.userPassword = form.value.userPassword;
    }

    const res = await api.userController.updateUserUsingPost(params);
    if (res?.data?.data) {
      ElMessage.success("修改成功，请重新登录！");
      loginUserStore.fetchLogoutUser();
      router.replace("/user/login");
    }
  } catch (error) {
    // 表单验证失败或提交失败
  }
}
</script>

<style scoped>
.user-profile {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 60vh;
}
</style>
