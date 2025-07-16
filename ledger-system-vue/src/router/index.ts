import { createRouter, createWebHistory, RouteRecordRaw } from "vue-router";
import { useLoginUserStore } from "@/store/userStore";
import UserProfile from "@/views/user/UserProfile.vue";

const routes: Array<RouteRecordRaw> = [
  {
    path: "/user/login",
    name: "login",
    component: () => import("@/views/user/UserLogin.vue"),
  },
  {
    path: "/user/profile",
    name: "UserProfile",
    component: UserProfile,
    meta: { title: "修改信息" },
  },
  {
    path: "/",
    component: () => import("@/layout/BasicLayout.vue"),
    children: [
      {
        path: "",
        name: "index",
        component: () => import("@/views/HomePage.vue"),
      },
      {
        path: "purchase/contract",
        name: "purchaseContract",
        component: () => import("@/views/purchase/PurchaseContract.vue"),
      },
      {
        path: "purchase/payment",
        name: "purchasePayment",
        component: () => import("@/views/purchase/PurchasePayment.vue"),
      },
      {
        path: "sales/contract",
        name: "salesContract",
        component: () => import("@/views/sales/SalesContract.vue"),
      },
      {
        path: "sales/payment",
        name: "salesPayment",
        component: () => import("@/views/sales/SalesPayment.vue"),
      },
      {
        path: "sales/detail/:id",
        name: "salesDetail",
        component: () => import("@/views/sales/SalesDetail.vue"),
      },
      {
        path: "user/manager",
        name: "manager",
        component: () => import("@/views/user/UserManager.vue"),
      },
      {
        path: "contract-income-analysis",
        name: "contractIncomeAnalysis",
        component: () => import("@/views/ContractIncomeAnalysis.vue"),
      },
      {
        path: "contract-financial-detail/:id",
        name: "contractFinancialDetail",
        component: () => import("@/views/ContractFinancialDetail.vue"),
      },
    ],
  },
];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes,
});

router.beforeEach(async (to, from, next) => {
  const loginUserStore = useLoginUserStore();
  const isLogin = !!loginUserStore.loginUser?.id;

  // 如果未登录且不是登录页面，尝试获取用户信息
  if (to.path !== "/user/login" && !isLogin) {
    try {
      await loginUserStore.fetchLoginUser();
    } catch (error) {
      // 获取用户信息失败
    }
  }

  next();
});

export default router;
