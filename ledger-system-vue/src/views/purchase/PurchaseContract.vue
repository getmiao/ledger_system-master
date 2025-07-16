<!-- eslint-disable prettier/prettier -->
<template>
  <div>
    <!-- 查询表单区域 -->
    <el-form :inline="true" :model="searchForm" class="search-form" @submit.prevent>
      <el-form-item label="分包单位">
        <el-input v-model="searchForm.subcontractingUnit" placeholder="分包单位" clearable style="width: 120px" />
      </el-form-item>
      <el-form-item label="项目负责人">
        <el-input v-model="searchForm.director" placeholder="项目负责人" clearable style="width: 120px" />
      </el-form-item>
      <el-form-item label="联系方式">
        <el-input v-model="searchForm.phone" placeholder="联系方式" clearable style="width: 120px" />
      </el-form-item>
      <el-form-item label="合同名称">
        <el-input v-model="searchForm.contractName" placeholder="合同名称" clearable style="width: 120px" />
      </el-form-item>
      <el-form-item label="合同签订日期">
        <el-date-picker v-model="searchForm.contractDate" type="date" placeholder="合同签订日期" style="width: 150px" clearable />
      </el-form-item>
      <el-form-item label="合同工期">
        <el-input v-model="searchForm.contractPeriod" placeholder="合同工期" clearable style="width: 120px" />
      </el-form-item>
      <el-form-item label="已收发票金额">
        <el-input v-model="searchForm.receivedInvoiceAmount" placeholder="已收发票金额" clearable style="width: 120px" type="number" />
      </el-form-item>
      <el-form-item label="实际营收">
        <el-input v-model="searchForm.actualRevenue" placeholder="实际营收" clearable style="width: 120px" type="number" />
      </el-form-item>
      <el-form-item label="采购方式">
        <el-input v-model="searchForm.purchaseMethod" placeholder="采购方式" clearable style="width: 120px" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="fetchData">查询</el-button>
        <el-button @click="onReset">重置</el-button>
        <el-button @click="showExportDialog">导出</el-button>
      </el-form-item>
    </el-form>
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
      <el-table-column label="操作" width="220">
        <template #default="scope">
          <el-button size="small" @click="onEdit(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="onDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination class="table-pagination" background layout="prev, pager, next, sizes, jumper, total" :total="total" v-model:current-page="page.current" v-model:page-size="page.pageSize" @current-change="fetchData" @size-change="fetchData" />
    <!-- 新增/编辑弹窗（可根据实际字段补充） -->
    <el-dialog v-model="dialogVisible" :title="dialogType === 'add' ? '新增合同' : '编辑合同'">
      <el-form :model="form" label-width="100px">
        <template v-if="dialogType === 'edit'">
          <el-form-item label="主键">
            <el-input v-model="form.id" disabled />
          </el-form-item>
        </template>
        <el-form-item label="销售合同">
          <el-select v-model="form.salesId" placeholder="请选择销售合同" filterable>
            <el-option v-for="(id, name) in contractOptions" :key="id" :label="name" :value="id" />
          </el-select>
        </el-form-item>
        <el-form-item label="分包单位">
          <el-input v-model="form.subcontractingUnit" />
        </el-form-item>
        <el-form-item label="项目负责人">
          <el-input v-model="form.director" />
        </el-form-item>
        <el-form-item label="联系方式">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="合同名称">
          <el-input v-model="form.contractName" />
        </el-form-item>
        <el-form-item label="合同金额">
          <el-input v-model="form.contractAmount" type="number" />
        </el-form-item>
        <el-form-item label="合同结算金额">
          <el-input v-model="form.settlementAmount" type="number" />
        </el-form-item>
        <el-form-item label="合同签订日期">
          <el-date-picker v-model="form.contractDate" type="datetime" value-format="x" format="YYYY-MM-DD HH:mm:ss" />
        </el-form-item>
        <el-form-item label="合同工期">
          <el-input v-model="form.contractPeriod" />
        </el-form-item>
        <el-form-item label="已收发票金额">
          <el-input v-model="form.receivedInvoiceAmount" type="number" />
        </el-form-item>
        <el-form-item label="实际营收">
          <el-input v-model="form.actualRevenue" type="number" />
        </el-form-item>
        <el-form-item label="采购方式">
          <el-input v-model="form.purchaseMethod" />
        </el-form-item>
        <el-form-item label="采购招标开始时间">
          <el-date-picker v-model="form.purchaseStart" type="date" value-format="x" format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="采购招标结束时间">
          <el-date-picker v-model="form.purchaseEnd" type="date" value-format="x" format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="支委会决策时间">
          <el-date-picker v-model="form.committeeDate" type="date" value-format="x" format="YYYY-MM-DD" />
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
import { useLoginUserStore } from "@/store/userStore";
import api from "@/api";
import { ElMessage, ElMessageBox } from "element-plus";
import API from "@/api/typings";
import PurchaseContractVO = API.PurchaseContractVO;
import { deletePurchaseContractUsingPost } from "@/api/purchaseContractController";
import { exportPurchaseContractUsingPost } from "@/api/exportController";
import { listSalesContractKeyVUsingPost } from "@/api/salesContractController";

// 页面表格字段
const allColumns = [
  { prop: "id", label: "主键" },
  { prop: "salesId", label: "销售合同ID" },
  { prop: "salesName", label: "销售合同名称" },
  { prop: "subcontractingUnit", label: "分包单位" },
  { prop: "director", label: "项目负责人" },
  { prop: "phone", label: "联系方式" },
  { prop: "contractName", label: "合同名称" },
  { prop: "contractAmount", label: "合同金额", type: "amount" },
  { prop: "settlementAmount", label: "合同结算金额", type: "amount" },
  { prop: "contractDate", label: "合同签订日期", type: "date" },
  { prop: "contractPeriod", label: "合同工期" },
  { prop: "receivedInvoiceAmount", label: "已收发票金额", type: "amount" },
  { prop: "plannedRevenue", label: "计划营收" },
  { prop: "actualRevenue", label: "实际营收" },
  { prop: "purchaseMethod", label: "采购方式" },
  { prop: "taxRate", label: "税率(%)" },
  { prop: "purchaseStart", label: "采购招标开始时间", type: "date" },
  { prop: "purchaseEnd", label: "采购招标结束时间", type: "date" },
  { prop: "committeeDate", label: "支委会决策时间", type: "date" },
  { prop: "totalPaymentAmount", label: "累计付款金额", type: "amount" },
  { prop: "totalInvoiceAmount", label: "累计开票金额", type: "amount" },
  { prop: "plannedCost", label: "计划成本", type: "amount" },
  { prop: "plannedGrossProfit", label: "计划毛利润", type: "amount" },
  { prop: "actualCost", label: "实际成本", type: "amount" },
  { prop: "actualGrossProfit", label: "实际毛利润", type: "amount" },
  { prop: "plannedGrossProfitRate", label: "计划毛利率(%)" },
  { prop: "actualGrossProfitRate", label: "实际毛利率(%)" },
  { prop: "profitDeviationAmount", label: "利润偏差金额", type: "amount" },
  { prop: "deviationRate", label: "偏差率(%)" },
  { prop: "accountsPayable", label: "应付账款", type: "amount" },
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
const contractOptions = ref<Record<string, number>>({});
const tableData = ref<PurchaseContractVO[]>([]);
const total = ref(0);
const page = reactive({ current: 1, pageSize: 10 });

// 财务分析相关

const searchForm = reactive({
  id: undefined as number | undefined,
  salesId: undefined as number | undefined,
  subcontractingUnit: "",
  director: "",
  phone: "",
  contractName: "",
  contractAmount: undefined as number | undefined,
  settlementAmount: undefined as number | undefined,
  contractDate: "",
  contractPeriod: "",
  receivedInvoiceAmount: undefined as number | undefined,
  actualRevenue: undefined as number | undefined,
  purchaseMethod: "",
  purchaseStart: "",
  purchaseEnd: "",
  committeeDate: "",
});

const dialogVisible = ref(false);
const dialogType = ref("add");
const form = reactive<any>({
  id: undefined,
  salesId: undefined,
  subcontractingUnit: "",
  director: "",
  phone: "",
  contractName: "",
  contractAmount: undefined,
  settlementAmount: undefined,
  contractDate: "",
  contractPeriod: "",
  receivedInvoiceAmount: undefined,
  actualRevenue: undefined,
  purchaseMethod: "",
  purchaseStart: "",
  purchaseEnd: "",
  committeeDate: "",
});

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
  return val ?? "-";
}

function onReset() {
  Object.keys(searchForm).forEach((key) => (searchForm[key] = ""));
  fetchData();
}

async function fetchData() {
  const res =
    await api.purchaseContractController.listPurchaseContractVoByPageUsingPost({
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

// eslint-disable-next-line @typescript-eslint/no-explicit-any
async function onDelete(row: any) {
  await ElMessageBox.confirm("确定删除该合同吗？", "提示", { type: "warning" });
  const res = await deletePurchaseContractUsingPost({
    id: row.id,
  });
  if (res.data.code === 0) {
    ElMessage.success("删除成功");
    fetchData();
  }
}

async function onSubmit() {
  if (dialogType.value === "add") {
    const res =
      await api.purchaseContractController.addPurchaseContractUsingPost(form);
    if (res?.data?.data) {
      ElMessage.success("新增成功");
      dialogVisible.value = false;
      fetchData();
    }
  } else {
    const res =
      await api.purchaseContractController.editPurchaseContractUsingPost(form);
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
    const res = await exportPurchaseContractUsingPost(
      selectedExportColumns.value
    );
    let headers = res.headers;
    let contentType = "";
    let disposition = "";
    if (headers && typeof headers.get === "function") {
      contentType = headers.get("content-type") as string;
      disposition = headers.get("content-disposition") as string;
    } else if (headers) {
      contentType = headers["content-type"] as string;
      disposition = headers["content-disposition"] as string;
    }
    contentType = contentType ? String(contentType) : "";
    disposition = disposition ? String(disposition) : "";
    if (
      disposition &&
      contentType &&
      !contentType.includes("application/json")
    ) {
      const blob = new Blob([res.data], { type: contentType });
      let filename = "采购合同.xlsx";
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
    let text = "";
    if (res.data && typeof res.data.text === "function") {
      text = await res.data.text();
    } else if (typeof res.text === "function") {
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

// 获取销售合同列表
const fetchContractOptions = async () => {
  try {
    const res = await listSalesContractKeyVUsingPost();
    if (res?.data?.data) {
      contractOptions.value = res.data.data;
    }
  } catch (error) {
    // 获取销售合同列表失败
  }
};

const loginUserStore = useLoginUserStore();
const userRole = computed(() => loginUserStore.loginUser.userRole || "user");

// 操作按钮权限判断
function canEditOrDelete(row: any) {
  // super/admin可操作全部，user只能操作自己创建的数据
  if (userRole.value === "super" || userRole.value === "admin") return true;
  return row.createId === loginUserStore.loginUser.id;
}

onMounted(() => {
  fetchData();
  fetchContractOptions();
});
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
