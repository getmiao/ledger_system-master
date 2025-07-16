<!-- eslint-disable prettier/prettier -->
<template>
  <div>
    <!-- 查询表单区域 -->
    <el-form :inline="true" :model="searchForm" class="search-form" @submit.prevent>
      <el-form-item label="主键">
        <el-input v-model="searchForm.id" placeholder="主键" clearable style="width: 120px" />
      </el-form-item>
      <el-form-item label="用户姓名">
        <el-input v-model="searchForm.name" placeholder="用户姓名" clearable style="width: 120px" />
      </el-form-item>
      <el-form-item label="联系方式">
        <el-input v-model="searchForm.phone" placeholder="联系方式" clearable style="width: 120px" />
      </el-form-item>
      <el-form-item label="部门">
        <el-input v-model="searchForm.department" placeholder="部门" clearable style="width: 120px" />
      </el-form-item>
      <el-form-item label="角色">
        <el-select v-model="searchForm.userRole" placeholder="角色" clearable style="width: 120px">
          <el-option label="超级管理员" :value="'super'" />
          <el-option label="管理员" :value="'admin'" />
          <el-option label="普通用户" :value="'user'" />
          <el-option label="禁用" :value="'ban'" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="fetchData">查询</el-button>
        <el-button @click="onReset">重置</el-button>
      </el-form-item>
    </el-form>
    <!-- 表格操作栏 -->
    <div style="
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 16px;
      ">
      <div>
        <el-button type="primary" @click="onAdd">新增用户</el-button>
        <el-dropdown>
          <el-button>选择列<i class="el-icon-arrow-down el-icon--right"></i></el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item v-for="col in allColumns" :key="col.prop" @click="toggleColumn(col.prop)">
                <el-checkbox :checked="visibleColumns.includes(col.prop)">{{ col.label }}
                </el-checkbox>
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>
    <el-table :data="tableData" style="width: 100%" border>
      <el-table-column v-for="col in showColumns" :key="col.prop" :prop="col.prop" :label="col.label">
        <template #default="scope">
          {{ formatCell(scope.row, col) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180">
        <template #default="scope">
          <el-button size="small" @click="onEdit(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="onDelete(scope.row)">删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination class="table-pagination" background layout="prev, pager, next, sizes, jumper, total" :total="total" v-model:current-page="page.current" v-model:page-size="page.pageSize" @current-change="fetchData" @size-change="fetchData" />
    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogType === 'add' ? '新增用户' : '编辑用户'">
      <el-form :model="form" label-width="100px">
        <template v-if="dialogType === 'edit'">
          <el-form-item label="主键">
            <el-input v-model="form.id" disabled />
          </el-form-item>
        </template>
        <template v-if="dialogType === 'add'">
          <el-form-item label="账号">
            <el-input v-model="form.userAccount" />
          </el-form-item>
        </template>
        <el-form-item label="用户姓名">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="联系方式">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="部门">
          <el-input v-model="form.department" />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="form.userRole" placeholder="角色" clearable :disabled="(userRole || '').trim().toLowerCase() !== 'super'">
            <el-option label="超级管理员" :value="'super'" />
            <el-option label="管理员" :value="'admin'" />
            <el-option label="普通用户" :value="'user'" />
            <el-option label="禁用" :value="'ban'" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="onSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from "vue";
import api from "@/api";
import { ElMessage, ElMessageBox } from "element-plus";
import { useLoginUserStore } from "@/store/userStore";

const allColumns = [
  { prop: "id", label: "主键" },
  { prop: "name", label: "用户姓名" },
  { prop: "phone", label: "联系方式" },
  { prop: "department", label: "部门" },
  { prop: "userRole", label: "角色" },
];
const visibleColumns = ref(allColumns.map((col) => col.prop));
const showColumns = computed(() =>
  allColumns.filter((col) => visibleColumns.value.includes(col.prop))
);

const tableData = ref<any[]>([]);
const total = ref(0);
const page = reactive({ current: 1, pageSize: 10 });
const searchForm = reactive({
  id: undefined as number | undefined,
  name: "",
  phone: "",
  department: "",
  userRole: "",
});

const dialogVisible = ref(false);
const dialogType = ref("add");
const form = reactive<any>({});

const loginUserStore = useLoginUserStore();
const userRole = loginUserStore.loginUser.userRole || "user";

function toggleColumn(prop: string) {
  if (visibleColumns.value.includes(prop)) {
    visibleColumns.value = visibleColumns.value.filter((p) => p !== prop);
  } else {
    visibleColumns.value.push(prop);
  }
}

function formatCell(row: any, col: any) {
  const val = row[col.prop];
  if (col.type === "date") {
    return val ? String(val).replace("T", " ").slice(0, 10) : "-";
  }
  if (col.prop === "userRole") {
    const map: Record<string, string> = {
      super: "超级管理员",
      admin: "管理员",
      user: "普通用户",
      ban: "禁用",
    };
    return map[val] ?? val ?? "-";
  }
  return val ?? "-";
}

function onReset() {
  searchForm.id = undefined;
  searchForm.name = "";
  searchForm.phone = "";
  searchForm.department = "";
  searchForm.userRole = "";
  fetchData();
}

async function fetchData() {
  const res = await api.userController.listUserByPageUsingPost({
    current: page.current,
    pageSize: page.pageSize,
    ...searchForm,
  });
  if (res?.data?.data) {
    tableData.value = res.data.data.records || [];
    total.value = Number(res.data.data.total) || 0;
  }
}

function onAdd() {
  dialogType.value = "add";
  Object.assign(form, {});
  dialogVisible.value = true;
}

function onEdit(row: any) {
  dialogType.value = "edit";
  Object.assign(form, row);
  dialogVisible.value = true;
}

async function onDelete(row: any) {
  await ElMessageBox.confirm("确定删除该用户吗？", "提示", { type: "warning" });
  const res = await api.userController.deleteUserUsingPost({ id: row.id });
  if (res?.data?.data) {
    ElMessage.success("删除成功");
    fetchData();
  }
}

async function onSubmit() {
  if (dialogType.value === "add") {
    const res = await api.userController.addUserUsingPost(form);
    if (res?.data?.data) {
      ElMessage.success("新增成功");
      dialogVisible.value = false;
      fetchData();
    }
  } else {
    const res = await api.userController.updateUserUsingPost(form);
    if (res?.data?.data) {
      ElMessage.success("编辑成功");
      dialogVisible.value = false;
      fetchData();
    }
  }
}

onMounted(fetchData);
</script>

<style lang="scss" scoped>
.table-pagination {
  margin-top: 16px;
  text-align: right;
  background: #fff;
  z-index: 2;
  padding-bottom: 8px;
}

.search-form {
  margin-bottom: 12px;
}
</style>
