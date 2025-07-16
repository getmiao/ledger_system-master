<!-- prettier-ignore -->
<template>
  <div>
    <!-- 查询表单区域 -->
    <el-form :inline="true" :model="searchForm" class="search-form" @submit.prevent>
      <el-form-item label="主键">
        <el-input v-model="searchForm.id" placeholder="主键" clearable style="width: 120px" />
      </el-form-item>
      <el-form-item label="金额">
        <el-input v-model="searchForm.account" placeholder="金额" clearable style="width: 120px" type="number" />
      </el-form-item>
      <el-form-item label="流水日期">
        <el-date-picker v-model="searchForm.time" type="date" placeholder="流水日期" style="width: 150px" clearable />
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
        <el-button type="primary" @click="onAdd" :disabled="Object.keys(contractOptions).length === 0">新增收款</el-button>

        <div v-if="Object.keys(contractOptions).length === 0" style="color: #f56c6c; margin-top: 8px;">
          当前无可用销售合同，请先添加销售合同后再新增收款！
        </div>
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
      <el-table-column v-for="col in showColumns" :key="col.prop" :prop="col.prop" :label="col.label" v-bind="col.width ? { width: col.width } : {}">
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
    <el-pagination class="table-pagination" background layout="prev, pager, next, sizes, jumper, total" :total="total" v-model:current-page="page.current" v-model:page-size="page.pageSize" @current-change="fetchData" @size-change="fetchData" :page-sizes="[10, 20]" />
    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogType === 'add' ? '新增收款' : '编辑收款'">
      <el-form :model="form" label-width="100px">
        <template v-if="dialogType === 'edit'">
          <el-form-item label="主键">
            <el-input v-model="form.id" disabled />
          </el-form-item>
        </template>
        <template v-if="dialogType === 'add'">
          <el-form-item label="销售合同">
            <el-select v-model="form.salesId" placeholder="请选择销售合同" clearable :disabled="Object.keys(contractOptions).length === 0">
              <el-option v-for="(value, key) in contractOptions" :key="value" :label="key" :value="value" />
            </el-select>
          </el-form-item>
        </template>
        <el-form-item label="金额">
          <el-input v-model="form.account" type="number" />
        </el-form-item>
        <el-form-item label="流水日期">
          <el-date-picker v-model="form.time" type="datetime" value-format="x" format="YYYY-MM-DD HH:mm:ss" />
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
          <el-checkbox v-for="col in allExportColumns" :key="col.prop" :label="col.prop" style="display: block; margin-bottom: 8px;">
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
<!-- prettier-ignore -->
<script setup lang="ts">
import { onMounted, onBeforeUnmount, ref, reactive, computed, nextTick, watch } from "vue";
import api from "@/api";
import { ElMessage, ElMessageBox } from "element-plus";
import API from "@/api/typings";
import SalesPaymentVO = API.SalesPaymentVO;
import { deleteSalesPaymentUsingPost } from "@/api/salesPaymentController";
import { exportSalesPaymentUsingPost } from "@/api/exportController";
import { listSalesContractKeyVUsingPost } from "@/api/salesContractController";
import { useLoginUserStore } from '@/store/userStore';


// 页面表格字段
const allColumns = [
  { prop: "id", label: "主键" },
  { prop: "salesId", label: "销售合同ID" },
  { prop: "contractName", label: "合同名称" },
  { prop: "account", label: "收款金额", type: "amount" },
  { prop: "time", label: "流水日期", type: "date" },
  { prop: "createTime", label: "创建时间", type: "date" },
];
const visibleColumns = ref(allColumns.map((col) => col.prop));
const showColumns = computed(() =>
  allColumns.filter((col) => visibleColumns.value.includes(col.prop))
);

// 导出字段配置
const allExportColumns = [
  { prop: "id", label: "主键" },
  { prop: "year", label: "年份" },
  { prop: "projectSourceText", label: "项目来源" },
  { prop: "projectStatusText", label: "项目状态" },
  { prop: "projectDirector", label: "项目负责人" },
  { prop: "customer", label: "客户名称", width: "120" },
  { prop: "projectName", label: "项目名称", width: "150" },
  { prop: "contractName", label: "合同名称", width: "120" },
  { prop: "contractAmount", label: "合同金额", type: "amount" },
  { prop: "contractTypeText", label: "合同类型" },
  { prop: "biddingMethodText", label: "招标方式" },
  { prop: "settlementAmount", label: "合同结算金额", type: "amount" },
  { prop: "taxRate", label: "合同税率(%)" },
  { prop: "plannedRevenue", label: "计划营收" },
  { prop: "contractDate", label: "合同签订日期", type: "date" },
  { prop: "contractPeriod", label: "合同工期" },
  { prop: "projectPayment", label: "累计回款金额", type: "amount" },
  { prop: "projectPaymentRate", label: "累计回款比例(%)" },
  { prop: "invoiceAmount", label: "累计开票金额", type: "amount" },
];
// 导出弹窗字段选择用 allExportColumns
const exportDialogVisible = ref(false);
const selectedExportColumns = ref(allExportColumns.map((col) => col.prop));

const tableData = ref<any[]>([]);
const total = ref(0);
const page = reactive({ current: 1, pageSize: 20 });
const searchForm = reactive({
  id: undefined as number | undefined,
  salesId: undefined as number | undefined,
  account: undefined as number | undefined,
  time: '',
});

const dialogVisible = ref(false);
const dialogType = ref("add");
const form = reactive<any>({
  id: undefined,
  salesId: undefined,
  account: undefined,
  time: '',
});
// 下拉选项对象类型声明
const contractOptions = ref<Record<string, string>>({});

const loginUserStore = useLoginUserStore();
const userRole = computed(() => loginUserStore.loginUser.userRole || 'user');

// 获取销售合同列表
async function fetchContractOptions() {
  try {
    const res = await listSalesContractKeyVUsingPost();
    if (res?.data?.data) {
      contractOptions.value = res.data.data;
    }
  } catch (error) {
    // 获取销售合同列表失败
  }
}

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
  Object.keys(searchForm || {}).forEach((key) => ((searchForm as any)[key] = ""));
  fetchData();
}

async function fetchData() {
  const params: any = {
    ...searchForm,
    current: page.current,
    pageSize: page.pageSize
  };
  // 自动过滤空字符串、null、undefined字段
  Object.keys(params).forEach(key => {
    if (params[key] === "" || params[key] === null || params[key] === undefined) {
      delete params[key];
    }
  });
  const res = await api.salesPaymentController.listSalesPaymentVoByPageUsingPost(params);
  if (res?.data?.data) {
    tableData.value = res.data.data.records || [];
    total.value = Number(res.data.data.total) || 0;
  }
}

function onAdd() {
  dialogType.value = "add";
  Object.assign(form, {});
  form.salesId = ""; // 初始化为字符串，确保类型一致
  dialogVisible.value = true;
}

function onEdit(row: any) {
  dialogType.value = "edit";
  Object.assign(form, row);
  dialogVisible.value = true;
}

async function onDelete(row: any) {
  await ElMessageBox.confirm("确定删除该收款吗？", "提示", { type: "warning" });
  const res = await deleteSalesPaymentUsingPost({
    id: row.id,
  });
  if (res.data.code === 0) {
    ElMessage.success("删除成功");
    fetchData();
  }
}

async function onSubmit() {
  if (dialogType.value === "add") {
    if (!form.salesId) {
      ElMessage.error('请选择销售合同');
      return;
    }
    // 防呆：salesId 必须在下拉选项里
    if (!Object.values(contractOptions.value).includes(form.salesId)) {
      ElMessage.error('销售合同ID无效，请重新选择');
      return;
    }
    const res = await api.salesPaymentController.addSalesPaymentUsingPost(form);
    if (res?.data?.data) {
      ElMessage.success("新增成功");
      dialogVisible.value = false;
      fetchData();
    }
  } else {
    const res = await api.salesPaymentController.updateSalesPaymentUsingPost(form);
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
    const res = await exportSalesPaymentUsingPost(selectedExportColumns.value);
    let headers = res.headers;
    let contentType = '';
    let disposition = '';
    if (headers && typeof headers.get === 'function') {
      contentType = headers.get('content-type') as string;
      disposition = headers.get('content-disposition') as string;
    } else if (headers) {
      contentType = headers['content-type'] as string;
      disposition = headers['content-disposition'] as string;
    }
    contentType = contentType ? String(contentType) : '';
    disposition = disposition ? String(disposition) : '';
    if (disposition && contentType && !contentType.includes('application/json')) {
      const blob = new Blob([res.data], { type: contentType });
      let filename = "销售回款.xlsx";
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
    let text = '';
    if (res.data && typeof res.data.text === 'function') {
      text = await res.data.text();
    } else if (typeof res.text === 'function') {
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
  const menu = document.querySelector('.custom-dropdown-menu');
  const btn = document.querySelector('.choose-column-btn');
  if ((menu && menu.contains(e.target as Node)) || (btn && btn.contains(e.target as Node))) {
    return;
  }
  showColumnDropdown.value = false;
}

// 操作按钮权限判断
function canEditOrDelete(row: any) {
  // super/admin可操作全部，user只能操作自己创建的数据
  if (userRole.value === 'super' || userRole.value === 'admin') return true;
  return row.createId === loginUserStore.loginUser.id;
}

onMounted(() => {
  fetchData();
  fetchContractOptions();
  document.addEventListener('click', handleClickOutside);
});
onBeforeUnmount(() => {
  document.removeEventListener('click', handleClickOutside);
});

// 新增：无合同时提示
watch(contractOptions, (val) => {
  if (Object.keys(val).length === 0) {
    ElMessage.warning('当前无可用销售合同，请先添加销售合同后再新增收款！');
  }
});
</script>
<!-- prettier-ignore -->
<style lang="scss" scoped>
.table-pagination {
  margin-top: 16px;
  text-align: right;
  background: #fff;
  z-index: 2;
  padding-bottom: 8px;
}

.search-form {
  background: #fff;
  border-radius: 14px;
  box-shadow: 0 2px 12px 0 rgba(33,150,243,0.06);
  padding: 18px 24px 0 24px;
  margin-bottom: 18px;
}
.el-form-item {
  margin-right: 18px;
}
.el-table {
  border-radius: 12px;
  box-shadow: 0 2px 12px 0 rgba(33,150,243,0.08);
  background: #fff;
}
.el-table th, .el-table td {
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
  box-shadow: 0 4px 24px 0 rgba(33,150,243,0.10);
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
