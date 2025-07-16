<!-- eslint-disable prettier/prettier -->
<template>
  <div>
    <!-- 查询表单区域 -->
    <el-form :inline="true" :model="searchForm" class="search-form" @submit.prevent>
      <el-form-item label="项目来源">
        <el-select v-model="searchForm.projectSource" placeholder="项目来源" clearable style="width: 120px">
          <el-option label="集团内" :value="0" />
          <el-option label="集团外" :value="1" />
        </el-select>
      </el-form-item>
      <el-form-item label="项目状态">
        <el-select v-model="searchForm.projectStatus" placeholder="项目状态" clearable style="width: 120px">
          <el-option label="在建" :value="0" />
          <el-option label="完工整改" :value="1" />
          <el-option label="竣工验收" :value="2" />
          <el-option label="竣工结算" :value="3" />
          <el-option label="质保期内" :value="4" />
          <el-option label="完结" :value="5" />
        </el-select>
      </el-form-item>
      <el-form-item label="项目负责人">
        <el-input v-model="searchForm.projectDirector" placeholder="项目负责人" clearable style="width: 120px" />
      </el-form-item>
      <el-form-item label="项目名称">
        <el-input v-model="searchForm.projectName" placeholder="项目名称" clearable style="width: 120px" />
      </el-form-item>
      <el-form-item label="合同名称">
        <el-input v-model="searchForm.contractName" placeholder="合同名称" clearable style="width: 120px" />
      </el-form-item>
      <el-form-item label="合同类型">
        <el-select v-model="searchForm.contractType" placeholder="合同类型" clearable style="width: 120px">
          <el-option label="建设施工" :value="0" />
          <el-option label="货物采购" :value="1" />
          <el-option label="技术服务" :value="2" />
        </el-select>
      </el-form-item>
      <el-form-item label="招标方式">
        <el-select v-model="searchForm.biddingMethod" placeholder="招标方式" clearable style="width: 120px">
          <el-option label="公开招标" value="0" />
          <el-option label="竞争性磋商" value="1" />
          <el-option label="询价比" value="2" />
          <el-option label="直接委托" value="3" />
          <el-option label="其他" value="4" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="fetchData">查询</el-button>
        <el-button @click="onReset">重置</el-button>
        <el-button @click="showExportDialog">导出</el-button>
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
        <el-button type="primary" @click="onAdd">新增合同</el-button>
        <!-- 选择列自定义弹层 -->
        <div style="display: inline-block; position: relative">
          <el-button class="choose-column-btn" @click.stop="showColumnDropdown = !showColumnDropdown">
            选择列<i class="el-icon-arrow-down el-icon--right"></i>
          </el-button>
          <div v-if="showColumnDropdown" class="custom-dropdown-menu" @click.stop style="
              position: absolute;
              z-index: 1000;
              left: 0;
              top: 40px;
              min-width: 160px;
              background: #fff;
              border-radius: 8px;
              box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.08);
              padding: 8px 0;
            ">
            <div v-for="col in allColumns" :key="col.prop" class="custom-dropdown-item" @click="toggleColumn(col.prop)" style="
                display: flex;
                align-items: center;
                padding: 4px 16px;
                cursor: pointer;
              ">
              <span class="custom-checkbox" :class="{ checked: visibleColumns.includes(col.prop) }" style="margin-right: 8px"></span>
              <span>{{ col.label }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
    <el-table :data="tableData" style="width: 100%" border>
      <el-table-column v-for="col in showColumns" :key="col.prop" :prop="col.prop" :label="col.label" :width="col.width || undefined">
        <template #default="scope">
          {{ formatCell(scope.row, col) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" fixed="right" width="160">
        <template #default="scope">
          <el-button size="small" @click="onViewDetail(scope.row)">查看</el-button>
          <!-- 保留原有操作按钮 -->
          <el-button size="small" type="primary" @click="onEdit(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="onDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination class="table-pagination" background layout="prev, pager, next, sizes, jumper, total" :total="total" v-model:current-page="page.current" v-model:page-size="page.pageSize" @current-change="fetchData" @size-change="fetchData" />
    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogType === 'add' ? '新增合同' : '编辑合同'">
      <el-form :model="form" label-width="100px">
        <template v-if="dialogType === 'edit'">
          <el-form-item label="主键">
            <el-input v-model="form.id" disabled />
          </el-form-item>
        </template>
        <el-form-item label="年份">
          <el-input v-model="form.year" />
        </el-form-item>
        <el-form-item label="项目来源">
          <el-select v-model="form.projectSource" placeholder="项目来源" clearable>
            <el-option label="集团内" :value="0" />
            <el-option label="集团外" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="项目状态">
          <el-select v-model="form.projectStatus" placeholder="项目状态" clearable>
            <el-option label="在建" :value="0" />
            <el-option label="完工整改" :value="1" />
            <el-option label="竣工验收" :value="2" />
            <el-option label="竣工结算" :value="3" />
            <el-option label="质保期内" :value="4" />
            <el-option label="完结" :value="5" />
          </el-select>
        </el-form-item>
        <el-form-item label="项目负责人">
          <el-input v-model="form.projectDirector" />
        </el-form-item>
        <el-form-item label="客户名称">
          <el-input v-model="form.customer" />
        </el-form-item>
        <el-form-item label="项目名称">
          <el-input v-model="form.projectName" />
        </el-form-item>
        <el-form-item label="合同名称">
          <el-input v-model="form.contractName" />
        </el-form-item>
        <el-form-item label="合同金额">
          <el-input v-model="form.contractAmount" type="number" />
        </el-form-item>
        <el-form-item label="合同类型">
          <el-select v-model="form.contractType" placeholder="合同类型" clearable>
            <el-option label="建设施工" :value="0" />
            <el-option label="货物采购" :value="1" />
            <el-option label="技术服务" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="招标方式">
          <el-select v-model="form.biddingMethod" placeholder="招标方式" clearable>
            <el-option label="公开招标" value="0" />
            <el-option label="竞争性磋商" value="1" />
            <el-option label="询价比" value="2" />
            <el-option label="直接委托" value="3" />
            <el-option label="其他" value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="合同结算金额">
          <el-input v-model="form.settlementAmount" type="number" />
        </el-form-item>
        <el-form-item label="合同税率">
          <el-input v-model="form.taxRate" type="number" />
        </el-form-item>
        <el-form-item label="合同签订日期">
          <el-date-picker v-model="form.contractDate" type="datetime" value-format="x" format="YYYY-MM-DD HH:mm:ss" />
        </el-form-item>
        <el-form-item label="合同工期">
          <el-input v-model="form.contractPeriod" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="onSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 导出字段选择对话框 -->
    <el-dialog v-model="exportDialogVisible" title="选择导出字段" width="600px">
      <div class="export-checkbox-scroll">
        <el-checkbox-group v-model="selectedExportColumns">
          <el-checkbox v-for="col in allColumns" :key="col.prop" :label="col.prop" style="display: block; margin-bottom: 8px">
            {{ col.label }}
          </el-checkbox>
        </el-checkbox-group>
      </div>
      <template #footer>
        <el-button @click="exportDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmExport">确认导出</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import {
  onMounted,
  onBeforeUnmount,
  ref,
  reactive,
  computed,
  nextTick,
} from "vue";
import api from "@/api";
import { ElMessage, ElMessageBox } from "element-plus";
import API from "@/api/typings";
import SalesContractVO = API.SalesContractVO;
import { deleteSalesContractUsingPost } from "@/api/salesContractController";
import { exportSalesContractUsingPost } from "@/api/exportController";
import { useRouter } from "vue-router";
import { useLoginUserStore } from "@/store/userStore";

const router = useRouter();

// 页面表格字段
const allColumns = [
  { prop: "id", label: "主键" },
  { prop: "year", label: "年份" },
  { prop: "projectSource", label: "项目来源" },
  { prop: "projectStatus", label: "项目状态" },
  { prop: "projectDirector", label: "项目负责人" },
  { prop: "customer", label: "客户名称" },
  { prop: "projectName", label: "项目名称" },
  { prop: "contractName", label: "合同名称" },
  { prop: "contractAmount", label: "合同金额", type: "amount" },
  { prop: "contractType", label: "合同类型" },
  { prop: "biddingMethod", label: "招标方式" },
  { prop: "settlementAmount", label: "合同结算金额", type: "amount" },
  { prop: "taxRate", label: "合同税率(%)" },
  { prop: "contractDate", label: "合同签订日期", type: "date" },
  { prop: "contractPeriod", label: "合同工期" },
  { prop: "projectPayment", label: "累计回款金额", type: "amount" },
  { prop: "projectPaymentRate", label: "累计回款比例(%)" },
  { prop: "invoiceAmount", label: "累计开票金额", type: "amount" },
  { prop: "createId", label: "创建人ID" },
  { prop: "createTime", label: "创建时间", type: "date" },
  { prop: "updateTime", label: "更新时间", type: "date" },
];
const visibleColumns = ref(allColumns.map((col) => col.prop));
const showColumns = computed(() =>
  allColumns.filter((col) => visibleColumns.value.includes(col.prop))
);

// 导出相关
const exportDialogVisible = ref(false);
const selectedExportColumns = ref(allColumns.map((col) => col.prop));

const tableData = ref<any[]>([]);
const total = ref(0);
const page = reactive({ current: 1, pageSize: 10 });
const searchForm = reactive({
  id: undefined as number | undefined,
  year: undefined as number | undefined,
  projectSource: undefined as number | undefined,
  projectStatus: undefined as number | undefined,
  projectDirector: "",
  customer: "",
  projectName: "",
  contractName: "",
  contractAmount: undefined as number | undefined,
  contractType: undefined as number | undefined,
  biddingMethod: "",
  settlementAmount: undefined as number | undefined,
  taxRate: undefined as number | undefined,
  contractDate: "",
  contractPeriod: "",
});

const dialogVisible = ref(false);
const dialogType = ref("add");
const form = reactive<any>({
  id: undefined,
  year: undefined,
  projectSource: undefined,
  projectStatus: undefined,
  projectDirector: "",
  customer: "",
  projectName: "",
  contractName: "",
  contractAmount: undefined,
  contractType: undefined,
  biddingMethod: "",
  settlementAmount: undefined,
  taxRate: undefined,
  contractDate: "",
  contractPeriod: "",
});

const loginUserStore = useLoginUserStore();
const userRole = computed(() => loginUserStore.loginUser.userRole || "user");

function toggleColumn(prop: string) {
  if (visibleColumns.value.includes(prop)) {
    visibleColumns.value = visibleColumns.value.filter((p) => p !== prop);
  } else {
    visibleColumns.value.push(prop);
  }
}

function formatCell(row: any, col: any) {
  const val = row[col.prop];
  if (col.type === "amount") {
    return val != null
      ? Number(val).toLocaleString("zh-CN", { minimumFractionDigits: 2 })
      : "-";
  }
  if (col.type === "date") {
    return val ? String(val).replace("T", " ").slice(0, 10) : "-";
  }
  // 枚举映射
  if (col.prop === "projectSource") {
    return val === 0 ? "集团内" : val === 1 ? "集团外" : val;
  }
  if (col.prop === "projectStatus") {
    const map = [
      "在建",
      "完工整改",
      "竣工验收",
      "竣工结算",
      "质保期内",
      "完结",
    ];
    return typeof val === "number" ? map[val] : val;
  }
  if (col.prop === "contractType") {
    const map = ["建设施工", "货物采购", "技术服务"];
    return typeof val === "number" ? map[val] : val;
  }
  if (col.prop === "biddingMethod") {
    const map = ["公开招标", "竞争性磋商", "询价比", "直接委托", "其他"];
    return map[val] ?? val;
  }
  return val ?? "-";
}

function onReset() {
  Object.keys(searchForm).forEach((key) => (searchForm[key] = ""));
  fetchData();
}

async function fetchData() {
  const res =
    await api.salesContractController.listSalesContractVoByPageUsingPost({
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

const deleteLoading = ref(false);
async function onDelete(row: any) {
  deleteLoading.value = true;
  try {
    await deleteSalesContractUsingPost({ id: row.id });
    ElMessage.success("删除成功");
    fetchData();
  } catch (e) {
    ElMessage.error("删除失败");
  } finally {
    deleteLoading.value = false;
  }
}

function onViewDetail(row: any) {
  router.push(`/sales/detail/${row.id}`);
}

async function onSubmit() {
  if (dialogType.value === "add") {
    const res = await api.salesContractController.addSalesContractUsingPost(
      form
    );
    if (res?.data?.data) {
      ElMessage.success("新增成功");
      dialogVisible.value = false;
      fetchData();
    }
  } else {
    const res = await api.salesContractController.updateSalesContractUsingPost(
      form
    );
    if (res?.data?.data) {
      ElMessage.success("编辑成功");
      dialogVisible.value = false;
      fetchData();
    }
  }
}

const showExportDialog = () => {
  exportDialogVisible.value = true;
};

const confirmExport = async () => {
  if (selectedExportColumns.value.length === 0) {
    ElMessage.warning("请至少选择一个字段");
    return;
  }
  try {
    const res = await exportSalesContractUsingPost(selectedExportColumns.value);
    let headers = res.headers;
    let contentType = "";
    let disposition = "";
    if (headers && typeof headers.get === "function") {
      // fetch风格
      contentType = headers.get("content-type") || "";
      disposition = headers.get("content-disposition") || "";
    } else if (headers) {
      // 普通对象
      contentType = headers["content-type"] || "";
      disposition = headers["content-disposition"] || "";
    }
    contentType = String(contentType);
    disposition = String(disposition);
    // 只要content-type不是json，且有disposition，就判定为正常
    if (
      disposition &&
      contentType &&
      contentType.indexOf("application/json") === -1
    ) {
      // 正常下载
      const blob = new Blob([res.data], {
        type: contentType,
      });
      let filename = "销售合同.xlsx";
      const match = disposition.match(/filename\*=utf-8''(.+)/);
      if (match && match[1]) {
        filename = decodeURIComponent(match[1]);
      }
      const link = document.createElement("a");
      link.href = window.URL.createObjectURL(blob);
      link.setAttribute("download", filename);
      document.body.appendChild(link);
      link.click();
      link.remove();
      window.URL.revokeObjectURL(link.href);
      ElMessage.success("导出成功");
      exportDialogVisible.value = false;
      return;
    }
    // 其它情况，尝试解析为json错误
    let text = "";
    if (res.data && res.data.text) {
      text = await res.data.text();
    } else if (res.text) {
      text = await res.text();
    }
    let msg = "导出失败，请稍后重试";
    try {
      const json = JSON.parse(text);
      msg = json.msg || msg;
    } catch (error) {
      // 解析错误信息失败
    }
    ElMessage.error(msg);
  } catch (error) {
    ElMessage.error("导出失败，请稍后重试");
  }
};

const showColumnDropdown = ref(false);
function handleClickOutside(e: MouseEvent) {
  const menu = document.querySelector(".custom-dropdown-menu");
  const btn = document.querySelector(".choose-column-btn");
  if (
    (menu && menu.contains(e.target as Node)) ||
    (btn && btn.contains(e.target as Node))
  ) {
    return;
  }
  showColumnDropdown.value = false;
}
onMounted(() => {
  document.addEventListener("click", handleClickOutside);
});
onBeforeUnmount(() => {
  document.removeEventListener("click", handleClickOutside);
});

onMounted(fetchData);

// 操作按钮权限判断
function canEditOrDelete(row: any) {
  // super/admin可操作全部，user只能操作自己创建的数据
  if (userRole.value === "super" || userRole.value === "admin") return true;
  return row.createId === loginUserStore.loginUser.id;
}
</script>

<style scoped>
.search-form {
  background: #fff;
  border-radius: 14px;
  box-shadow: 0 2px 12px 0 rgba(33, 150, 243, 0.06);
  padding: 18px 24px 0 24px;
  margin-bottom: 18px;
}
.el-form-item {
  margin-right: 18px;
}
.el-table {
  border-radius: 12px;
  box-shadow: 0 2px 12px 0 rgba(33, 150, 243, 0.08);
  background: #fff;
}
.el-table th,
.el-table td {
  background: #f8fafc;
  color: #333;
}
.el-table th {
  font-weight: bold;
  background: #f0f4fa;
}
.el-pagination {
  margin-top: 18px;
  justify-content: center;
}
.el-dialog {
  border-radius: 14px;
  box-shadow: 0 4px 24px 0 rgba(33, 150, 243, 0.1);
}
.el-button {
  border-radius: 8px;
  font-weight: 500;
}
.export-checkbox-scroll {
  max-height: 400px;
  overflow-y: auto;
  padding-right: 8px;
}
.custom-dropdown-menu {
  max-height: 400px;
  overflow-y: auto;
}
.custom-dropdown-item:hover {
  background: #f5f7fa;
}
.custom-checkbox {
  width: 16px;
  height: 16px;
  border: 1.5px solid #409eff;
  border-radius: 4px;
  display: inline-block;
  position: relative;
  background: #fff;
  transition: border-color 0.2s;
}
.custom-checkbox.checked {
  background: #409eff;
  border-color: #409eff;
}
.custom-checkbox.checked::after {
  content: "";
  position: absolute;
  left: 4px;
  top: 1px;
  width: 5px;
  height: 9px;
  border: solid #fff;
  border-width: 0 2px 2px 0;
  transform: rotate(45deg);
}
</style>
