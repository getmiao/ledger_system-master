<!-- prettier-ignore -->
<template>
  <div class="sales-detail-container">
    <el-alert v-if="!salesDetail?.id" type="error" show-icon title="当前销售合同不存在或已被删除，无法进行任何操作！" />
    <el-card class="detail-card">
      <div class="detail-header">
        <el-button type="primary" @click="goBack">返回</el-button>
        <span class="detail-title">销售合同详情</span>
      </div>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="合同编号">{{ salesDetail?.contractName }}</el-descriptions-item>
        <el-descriptions-item label="项目名称">{{ salesDetail?.contractName }}</el-descriptions-item>
        <el-descriptions-item label="负责人">{{ salesDetail?.projectDirector }}</el-descriptions-item>
        <el-descriptions-item label="合同金额">{{ salesDetail?.contractAmount }}</el-descriptions-item>
        <el-descriptions-item label="项目来源">{{ projectSourceMap[salesDetail?.projectSource] || salesDetail?.projectSource
        }}</el-descriptions-item>
        <el-descriptions-item label="项目状态">{{ projectStatusMap[salesDetail?.projectStatus] || salesDetail?.projectStatus
        }}</el-descriptions-item>
        <el-descriptions-item label="年份">{{ salesDetail?.year }}</el-descriptions-item>
        <el-descriptions-item label="招标方式">{{ biddingMethodMap[salesDetail?.biddingMethod] || salesDetail?.biddingMethod
        }}</el-descriptions-item>
        <el-descriptions-item label="合同类型">{{ contractTypeMap[salesDetail?.contractType] || salesDetail?.contractType
        }}</el-descriptions-item>
      </el-descriptions>
    </el-card>
    <el-card class="detail-card" style="margin-top: 24px">
      <div class="detail-header">
        <span class="detail-title">关联采购合同</span>
        <el-button type="primary" @click="showAddPurchase = true" :disabled="!salesDetail?.id">新增采购合同</el-button>
        <el-button style="margin-left: 12px" @click="showColumnDialog = true" :disabled="!salesDetail?.id">自定义列</el-button>
        <el-button type="success" style="margin-left: 12px" @click="onOpenAddReceipt" :disabled="!salesDetail?.id">新增收款</el-button>
        <el-button type="warning" style="margin-left: 12px" @click="onOpenAddPayment" :disabled="!salesDetail?.id">新增付款</el-button>
        <div v-if="!salesDetail?.id" style="color: #f56c6c; margin-top: 8px;">当前销售合同不存在或已被删除，无法添加收款/付款！</div>
      </div>
      <el-table :data="purchaseList" style="width: 100%">
        <el-table-column v-for="col in visibleColumns" :key="col.prop" :prop="col.prop" :label="col.label" />
      </el-table>
      <el-dialog v-model="showColumnDialog" title="自定义显示列" width="400px">
        <el-checkbox-group v-model="visibleColumnProps">
          <el-checkbox v-for="col in allColumns" :key="col.prop" :label="col.prop">{{ col.label }}</el-checkbox>
        </el-checkbox-group>
        <template #footer>
          <el-button @click="showColumnDialog = false">关闭</el-button>
        </template>
      </el-dialog>
    </el-card>
    <el-dialog v-model="showAddPurchase" title="新增采购合同" width="600px">
      <el-form :model="addPurchaseForm" :rules="addPurchaseRules" ref="addPurchaseFormRef" label-width="100px">
        <el-form-item label="销售合同">
          <el-input v-model="addPurchaseForm.salesId" :value="salesId" disabled />
        </el-form-item>
        <el-form-item label="分包单位" prop="subcontractingUnit">
          <el-input v-model="addPurchaseForm.subcontractingUnit" />
        </el-form-item>
        <el-form-item label="项目负责人" prop="director">
          <el-input v-model="addPurchaseForm.director" />
        </el-form-item>
        <el-form-item label="联系方式" prop="phone">
          <el-input v-model="addPurchaseForm.phone" />
        </el-form-item>
        <el-form-item label="合同名称" prop="contractName">
          <el-input v-model="addPurchaseForm.contractName" />
        </el-form-item>
        <el-form-item label="合同金额" prop="contractAmount">
          <el-input v-model="addPurchaseForm.contractAmount" type="number" />
        </el-form-item>
        <el-form-item label="合同结算金额" prop="settlementAmount">
          <el-input v-model="addPurchaseForm.settlementAmount" type="number" />
        </el-form-item>
        <el-form-item label="合同签订日期" prop="contractDate">
          <el-date-picker v-model="addPurchaseForm.contractDate" type="datetime" value-format="x" format="YYYY-MM-DD HH:mm:ss" />
        </el-form-item>
        <el-form-item label="合同工期" prop="contractPeriod">
          <el-input v-model="addPurchaseForm.contractPeriod" />
        </el-form-item>
        <el-form-item label="已收发票金额" prop="receivedInvoiceAmount">
          <el-input v-model="addPurchaseForm.receivedInvoiceAmount" type="number" />
        </el-form-item>
        <el-form-item label="实际营收" prop="actualRevenue">
          <el-input v-model="addPurchaseForm.actualRevenue" type="number" />
        </el-form-item>
        <el-form-item label="采购方式" prop="purchaseMethod">
          <el-input v-model="addPurchaseForm.purchaseMethod" />
        </el-form-item>
        <el-form-item label="采购招标开始时间" prop="purchaseStart">
          <el-date-picker v-model="addPurchaseForm.purchaseStart" type="date" value-format="x" format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="采购招标结束时间" prop="purchaseEnd">
          <el-date-picker v-model="addPurchaseForm.purchaseEnd" type="date" value-format="x" format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="支委会决策时间" prop="committeeDate">
          <el-date-picker v-model="addPurchaseForm.committeeDate" type="date" value-format="x" format="YYYY-MM-DD" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="onCancelAddPurchase">取消</el-button>
        <el-button type="primary" :loading="addPurchaseLoading" @click="onAddPurchase">确定</el-button>
      </template>
    </el-dialog>

    <!-- 新增收款弹窗 -->
    <el-dialog v-model="showAddReceipt" title="新增收款" width="400px">
      <el-form :model="addReceiptForm" :rules="addReceiptRules" ref="addReceiptFormRef" label-width="100px">
        <el-form-item label="销售合同ID">
          <el-input v-model="addReceiptForm.salesId" disabled />
        </el-form-item>
        <el-form-item label="金额" prop="account">
          <el-input v-model="addReceiptForm.account" type="number" />
        </el-form-item>
        <el-form-item label="流水日期" prop="time">
          <el-date-picker v-model="addReceiptForm.time" type="datetime" value-format="x" format="YYYY-MM-DD HH:mm:ss" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="addReceiptForm.remark" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="onCancelAddReceipt">取消</el-button>
        <el-button type="primary" :loading="addReceiptLoading" @click="onAddReceipt">确定</el-button>
      </template>
    </el-dialog>

    <!-- 新增付款弹窗 -->
    <el-dialog v-model="showAddPayment" title="新增付款" width="400px">
      <el-form :model="addPaymentForm" :rules="addPaymentRules" ref="addPaymentFormRef" label-width="100px">
        <el-form-item label="采购合同" prop="purchaseId">
          <el-select v-model="addPaymentForm.purchaseId" placeholder="请选择采购合同">
            <el-option v-for="item in purchaseList" :key="item.id" :label="item.contractName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="金额" prop="account">
          <el-input v-model="addPaymentForm.account" type="number" />
        </el-form-item>
        <el-form-item label="流水日期" prop="time">
          <el-date-picker v-model="addPaymentForm.time" type="datetime" value-format="x" format="YYYY-MM-DD HH:mm:ss" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="addPaymentForm.remark" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="onCancelAddPayment">取消</el-button>
        <el-button type="primary" :loading="addPaymentLoading" @click="onAddPayment">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>
<!-- prettier-ignore -->
<script setup lang="ts">
import { ref, onMounted, nextTick, computed, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import {
  getSalesContractById,
  getPurchaseListBySalesId,
  addPurchaseContract,
} from "@/api/salesDetail";
import { ElMessage } from "element-plus";
import api from "@/api";

const route = useRoute();
const router = useRouter();
// 用 computed 绑定 salesId，确保始终和路由参数一致
const salesId = computed(() => {
  const id = route.params.id;
  return Array.isArray(id) ? id[0] : id;
});
// eslint-disable-next-line @typescript-eslint/no-explicit-any
const salesDetail = ref<any>(null);
// eslint-disable-next-line @typescript-eslint/no-explicit-any
const purchaseList = ref<any[]>([]);
const showAddPurchase = ref(false);
const addPurchaseForm = ref({
  salesId: salesId,
  subcontractingUnit: "",
  director: "",
  phone: "",
  contractName: "",
  contractAmount: "",
  settlementAmount: "",
  contractDate: "",
  contractPeriod: "",
  receivedInvoiceAmount: "",
  actualRevenue: "",
  purchaseMethod: "",
  purchaseStart: "",
  purchaseEnd: "",
  committeeDate: ""
});
const addPurchaseFormRef = ref();
const addPurchaseLoading = ref(false);
const addPurchaseRules = {
  subcontractingUnit: [{ required: true, message: "分包单位不能为空", trigger: "blur" }],
  director: [{ required: true, message: "项目负责人不能为空", trigger: "blur" }],
  phone: [{ required: true, message: "联系方式不能为空", trigger: "blur" }],
  contractName: [{ required: true, message: "合同名称不能为空", trigger: "blur" }],
  contractAmount: [{ required: true, message: "合同金额不能为空", trigger: "blur" }],
  settlementAmount: [{ required: true, message: "合同结算金额不能为空", trigger: "blur" }],
  contractDate: [{ required: true, message: "合同签订日期不能为空", trigger: "change" }],
  contractPeriod: [{ required: true, message: "合同工期不能为空", trigger: "blur" }],
  receivedInvoiceAmount: [{ required: true, message: "已收发票金额不能为空", trigger: "blur" }],
  actualRevenue: [{ required: false, message: "实际营收不能为空", trigger: "blur" }],
  purchaseMethod: [{ required: true, message: "采购方式不能为空", trigger: "blur" }],
  purchaseStart: [{ required: true, message: "采购招标开始时间不能为空", trigger: "change" }],
  purchaseEnd: [{ required: true, message: "采购招标结束时间不能为空", trigger: "change" }],
  committeeDate: [{ required: true, message: "支委会决策时间不能为空", trigger: "change" }]
};

const projectSourceMap: Record<string | number, string> = {
  0: "集团内",
  1: "集团外"
};
const projectStatusMap: Record<string | number, string> = {
  0: "在建",
  1: "完工整改",
  2: "竣工验收",
  3: "竣工结算",
  4: "质保期内",
  5: "完结"

};
const biddingMethodMap: Record<string | number, string> = {
  0: "公开招标",
  1: "竞争性磋商",
  2: "询价比",
  3: "直接委托",
  4: "其他"
};
const contractTypeMap: Record<string | number, string> = {
  0: "建设施工",
  1: "货物采购",
  2: "技术服务"

};

const allColumns = [
  { prop: "id", label: "采购合同编号" },
  { prop: "salesId", label: "销售合同ID" },
  { prop: "salesName", label: "销售合同名称" },
  { prop: "subcontractingUnit", label: "分包单位" },
  { prop: "director", label: "负责人" },
  { prop: "phone", label: "联系方式" },
  { prop: "contractName", label: "合同名称" },
  { prop: "contractAmount", label: "合同金额" },
  { prop: "settlementAmount", label: "合同结算金额" },
  { prop: "contractDate", label: "合同签订日期" },
  { prop: "contractPeriod", label: "合同工期" },
  { prop: "receivedInvoiceAmount", label: "已收发票金额" },
  { prop: "plannedRevenue", label: "计划营收" },
  { prop: "actualRevenue", label: "实际营收" },
  { prop: "purchaseMethod", label: "采购方式" },
  { prop: "purchaseStart", label: "采购招标开始时间" },
  { prop: "purchaseEnd", label: "采购招标结束时间" },
  { prop: "committeeDate", label: "支委会决策时间" },
  { prop: "totalPaymentAmount", label: "累计付款金额" },
  { prop: "totalInvoiceAmount", label: "累计开票金额" },
  { prop: "plannedCost", label: "计划成本" },
  { prop: "plannedGrossProfit", label: "计划毛利润" },
  { prop: "actualGrossProfit", label: "实际毛利润" },
  { prop: "plannedGrossProfitRate", label: "计划毛利率(%)" },
  { prop: "actualGrossProfitRate", label: "实际毛利率(%)" },
  { prop: "profitDeviationAmount", label: "利润偏差金额" },
  { prop: "deviationRate", label: "偏差率(%)" },
  { prop: "accountsPayable", label: "应付账款" }
];
const defaultVisibleProps = [
  "salesName",
  "subcontractingUnit",
  "director",
  "purchaseMethod",
  "phone",
  "contractName",
  "contractAmount",
  "settlementAmount"
];
const visibleColumnProps = ref(defaultVisibleProps);
const visibleColumns = computed(() => allColumns.filter(col => visibleColumnProps.value.includes(col.prop)));
const showColumnDialog = ref(false);

const showAddReceipt = ref(false);
const addReceiptForm = ref({
  salesId: '',
  account: '',
  time: '',
  remark: ''
});
const addReceiptFormRef = ref();
const addReceiptLoading = ref(false);
const addReceiptRules = {
  account: [{ required: true, message: '金额不能为空', trigger: 'blur' }],
  time: [{ required: true, message: '流水日期不能为空', trigger: 'change' }],
  remark: [{ required: false }]
};
function onCancelAddReceipt() {
  showAddReceipt.value = false;
  nextTick(() => addReceiptFormRef.value?.resetFields());
}
function onOpenAddReceipt() {
  if (!salesDetail.value?.id) {
    ElMessage.error('当前销售合同不存在或已被删除，无法添加收款！');
    return;
  }
  // 强制用最新 salesId 赋值
  addReceiptForm.value.salesId = String(salesId.value);
  showAddReceipt.value = true;
}
async function onAddReceipt() {
  await nextTick();
  // 再次强制赋值，防止污染
  addReceiptForm.value.salesId = String(salesId.value);
  addReceiptFormRef.value.validate(async (valid: boolean) => {
    if (!valid) return;
    if (!salesDetail.value?.id || String(addReceiptForm.value.salesId) !== String(salesDetail.value.id)) {
      ElMessage.error('销售合同ID无效，无法新增收款！');
      return;
    }
    addReceiptLoading.value = true;
    try {
      const submitData = Object.fromEntries(Object.entries({ ...addReceiptForm.value, account: Number(addReceiptForm.value.account), salesId: addReceiptForm.value.salesId }).filter(([k, v]) => v !== '' && v !== null && v !== undefined));
      const res = await api.salesPaymentController.addSalesPaymentUsingPost(submitData);
      if (res?.data?.code === 0) {
        ElMessage.success('新增收款成功');
        showAddReceipt.value = false;
        fetchDetail();
        nextTick(() => addReceiptFormRef.value?.resetFields());
      } else {
        ElMessage.error('新增收款失败');
      }
    } catch (e) {
      ElMessage.error('新增收款失败');
    } finally {
      addReceiptLoading.value = false;
    }
  });
}

const showAddPayment = ref(false);
const addPaymentForm = ref({
  purchaseId: '',
  account: '',
  time: '',
  remark: ''
});
const addPaymentFormRef = ref();
const addPaymentLoading = ref(false);
const addPaymentRules = {
  purchaseId: [{ required: true, message: '采购合同不能为空', trigger: 'change' }],
  account: [{ required: true, message: '金额不能为空', trigger: 'blur' }],
  time: [{ required: true, message: '流水日期不能为空', trigger: 'change' }],
  remark: [{ required: false }]
};
function onCancelAddPayment() {
  showAddPayment.value = false;
  nextTick(() => addPaymentFormRef.value?.resetFields());
}
// 新增：防呆校验，按钮事件
function onOpenAddPayment() {
  if (!salesDetail.value?.id) {
    ElMessage.error('当前销售合同不存在或已被删除，无法添加付款！');
    return;
  }
  showAddPayment.value = true;
}
async function onAddPayment() {
  await nextTick();
  addPaymentFormRef.value.validate(async (valid: boolean) => {
    if (!valid) return;
    // 防呆：采购合同必须属于本销售合同
    if (!addPaymentForm.value.purchaseId || !purchaseList.value.find(item => item.id === addPaymentForm.value.purchaseId)) {
      ElMessage.error('请选择本销售合同下的采购合同！');
      return;
    }
    addPaymentLoading.value = true;
    try {
      // 过滤空值参数
      const submitData = Object.fromEntries(Object.entries({ ...addPaymentForm.value, account: Number(addPaymentForm.value.account), purchaseId: addPaymentForm.value.purchaseId }).filter(([k, v]) => v !== '' && v !== null && v !== undefined));
      const res = await api.purchasePaymentController.addPurchasePaymentUsingPost(submitData);
      if (res?.data?.code === 0) {
        ElMessage.success('新增付款成功');
        showAddPayment.value = false;
        fetchDetail();
        nextTick(() => addPaymentFormRef.value?.resetFields());
      } else {
        ElMessage.error('新增付款失败');
      }
    } catch (e) {
      ElMessage.error('新增付款失败');
    } finally {
      addPaymentLoading.value = false;
    }
  });
}

function goBack() {
  router.back();
}

async function fetchDetail() {
  // 验证salesId的有效性，但不转换为Number
  const currentSalesId = String(salesId.value);
  
  if (!currentSalesId || currentSalesId.trim() === '' || isNaN(Number(currentSalesId)) || Number(currentSalesId) <= 0) {
    console.warn(`无效的salesId: ${currentSalesId}`);
    salesDetail.value = {};
    purchaseList.value = [];
    return;
  }

  try {
    const res = await getSalesContractById(currentSalesId);
    // getSalesContractById 返回的是 res.data.data，所以这里直接使用 res
    salesDetail.value = (res as any) || {};
    purchaseList.value = await getPurchaseListBySalesId(currentSalesId);
  } catch (error) {
    console.error("获取销售合同详情失败:", error);
    salesDetail.value = {};
    purchaseList.value = [];
  }
}

function onCancelAddPurchase() {
  showAddPurchase.value = false;
  nextTick(() => {
    addPurchaseFormRef.value?.resetFields();
    addPurchaseForm.value = { salesId: String(salesId.value), subcontractingUnit: "", director: "", phone: "", contractName: "", contractAmount: "", settlementAmount: "", contractDate: "", contractPeriod: "", receivedInvoiceAmount: "", actualRevenue: "", purchaseMethod: "", purchaseStart: "", purchaseEnd: "", committeeDate: "" };
  });
}
async function onAddPurchase() {
  await nextTick();
  addPurchaseFormRef.value.validate(async (valid: boolean) => {
    if (!valid) return;
    addPurchaseLoading.value = true;
    try {
      const res = await addPurchaseContract(String(salesId.value), addPurchaseForm.value);
      if (res?.data?.data) {
        ElMessage.success("新增采购合同成功");
        showAddPurchase.value = false;
        fetchDetail();
        nextTick(() => addPurchaseFormRef.value?.resetFields());
      }
    } catch (e) {
      ElMessage.error("新增采购合同失败");
    } finally {
      addPurchaseLoading.value = false;
    }
  });
}

// 监听路由变化，自动刷新数据
watch(() => route.params.id, (newId) => {
  if (newId) {
    // 不需要给salesId赋值，因为它是computed属性，会自动更新
    fetchDetail();
    addReceiptForm.value.salesId = String(salesId.value);
  }
});

onMounted(() => {
  fetchDetail();
  addReceiptForm.value.salesId = String(salesId.value);
});
</script>
<!-- prettier-ignore -->
<style scoped>
.sales-detail-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 32px 0;
}

.detail-card {
  margin-bottom: 24px;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.detail-title {
  font-size: 20px;
  font-weight: bold;
}
</style>
