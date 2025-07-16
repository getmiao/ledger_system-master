<template>
  <el-container class="layout-container full-screen">
    <el-aside :width="'220px'" class="sidebar">
      <div class="logo-area">
        <!-- <img src="/logo.png" alt="logo" class="logo-img" /> -->
        <span class="logo-title">合同管理系统</span>
      </div>
      <!-- 删除折叠按钮 -->
      <el-menu
        :default-active="$route.path"
        class="el-menu-vertical-demo"
        background-color="transparent"
        text-color="#fff"
        active-text-color="#fff"
        router
        :collapse="false"
      >
        <el-menu-item index="/">
          <el-icon>
            <HomeFilled />
          </el-icon>
          <span>首页</span>
        </el-menu-item>
        <el-menu-item index="/contract-income-analysis">
          <el-icon>
            <TrendCharts />
          </el-icon>
          <span>合同收入分析</span>
        </el-menu-item>

        <el-sub-menu index="1">
          <template #title>
            <el-icon>
              <Document />
            </el-icon>
            <span>采购管理</span>
          </template>
          <el-menu-item index="/purchase/contract">采购合同</el-menu-item>
          <el-menu-item index="/purchase/payment">采购付款</el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="2">
          <template #title>
            <el-icon>
              <Document />
            </el-icon>
            <span>销售管理</span>
          </template>
          <el-menu-item index="/sales/contract">销售合同</el-menu-item>
          <el-menu-item index="/sales/payment">销售收款</el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="3">
          <template #title>
            <el-icon>
              <User />
            </el-icon>
            <span>用户管理</span>
          </template>
          <el-menu-item index="/user/manager">用户管理</el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="main-header">
        <div class="user-info">
          <template v-if="loginUserStore.loginUser?.name">
            <el-dropdown>
              <span class="user-info" style="cursor: pointer">
                <el-avatar :icon="UserFilled" />
                <span
                  class="user-name"
                  style="color: #409eff; font-weight: bold"
                >
                  {{ loginUserStore.loginUser?.name }}
                </span>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="goToProfile"
                    >修改信息</el-dropdown-item
                  >
                  <el-dropdown-item @click="logout">
                    <el-icon><i class="el-icon-switch-button"></i></el-icon>
                    退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <el-button type="primary" @click="goLogin">登录</el-button>
          </template>
        </div>
      </el-header>
      <el-main class="main-content">
        <el-card class="main-card" shadow="never">
          <router-view />
        </el-card>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import {
  HomeFilled,
  Document,
  User,
  Fold,
  Expand,
  UserFilled,
  TrendCharts,
} from "@element-plus/icons-vue";
import { useLoginUserStore } from "@/store/userStore";
import { useRouter } from "vue-router";

const loginUserStore = useLoginUserStore();
const collapsed = ref(false);
const router = useRouter();

function logout() {
  loginUserStore.fetchLogoutUser();
  router.replace("/user/login");
}

function goLogin() {
  router.replace("/user/login");
}

function goToProfile() {
  router.push("/user/profile");
}
</script>

<style lang="scss" scoped>
.layout-container.full-screen {
  position: fixed;
  inset: 0;
  width: 100vw;
  height: 100vh;
  min-width: 0;
  min-height: 0;
  margin: 0;
  padding: 0;
  background: linear-gradient(135deg, #e3eafc 0%, #f4f6fb 100%);
  z-index: 0;
}

.sidebar {
  background: linear-gradient(135deg, #409eff 60%, #1976d2 100%);
  min-height: 100vh;
  box-shadow: 2px 0 12px 0 rgba(33, 150, 243, 0.1);
  position: relative;
  transition: width 0.2s;
  padding-top: 18px;
  border-top-right-radius: 18px;
  border-bottom-right-radius: 18px;
}
.logo-area {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 32px;
}
.logo-img {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  margin-right: 10px;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}
.logo-title {
  color: #fff;
  font-size: 1.3rem;
  font-weight: bold;
  letter-spacing: 2px;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}
.collapse-btn {
  position: absolute;
  top: 18px;
  right: -16px;
  z-index: 10;
  width: 32px;
  height: 32px;
  background: #fff;
  border-radius: 50%;
  box-shadow: 0 2px 8px 0 rgba(33, 150, 243, 0.12);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  border: none;
  transition: right 0.2s, background 0.2s;
}
.collapse-btn .el-icon {
  color: #409eff;
}
.el-menu,
.el-menu-item,
.el-sub-menu__title {
  font-size: 16px;
  font-weight: 500;
}
.el-menu {
  border-right: none;
  background: transparent;
}
.el-menu-item {
  border-radius: 8px;
  margin: 4px 8px;
  transition: background 0.2s, color 0.2s;
}
.el-menu-item.is-active {
  background: #fff !important;
  color: #1976d2 !important;
  box-shadow: 0 2px 8px rgba(33, 150, 243, 0.08);
}
.el-menu-item:hover {
  background: rgba(255, 255, 255, 0.18) !important;
  color: #fff !important;
}
.main-header {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  height: 64px;
  background: #fff;
  box-shadow: 0 2px 8px 0 rgba(33, 150, 243, 0.06);
  border-bottom-left-radius: 18px;
  border-bottom-right-radius: 18px;
  margin-bottom: 8px;
  padding: 0 32px;
}
.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}
.user-name {
  font-weight: 500;
  color: #409eff;
  margin-left: 6px;
}
.main-content {
  padding: 32px 24px 24px 24px;
  background: transparent;
  min-height: calc(100vh - 80px);
}
.main-card {
  border-radius: 16px;
  box-shadow: 0 4px 24px 0 rgba(33, 150, 243, 0.08);
  background: #fff;
  border: none;
  padding: 32px 24px;
}
</style>
