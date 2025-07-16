<!-- eslint-disable prettier/prettier -->
<template>
  <div class="overview-container">
    <!-- 页面头部 -->
    <div class="overview-header">
      <div class="header-left">
        <h2 class="page-title">
          <el-icon>
            <TrendCharts />
          </el-icon>
          合同台账总览
        </h2>
        <p class="page-subtitle">公司合同执行情况概览</p>
      </div>
      <div class="header-actions">
        <el-radio-group v-model="viewType" size="default" @change="onViewTypeChange">
          <el-radio-button label="all">全部</el-radio-button>
          <el-radio-button label="internal">集团内</el-radio-button>
          <el-radio-button label="external">集团外</el-radio-button>
        </el-radio-group>
        <el-button type="primary" size="small" @click="exportToExcel">
          <el-icon>
            <Download />
          </el-icon>
          导出Excel
        </el-button>
      </div>
    </div>

    <!-- 加载状态 -->
    <el-skeleton v-if="loading" :rows="6" animated style="margin: 40px" />

    <div v-else>
      <!-- 关键指标卡片 -->
      <div class="key-metrics-section">
        <h3 class="section-title">关键财务指标</h3>
        <div class="metrics-grid">
          <div class="metric-card primary">
            <div class="metric-icon">
              <el-icon>
                <Money />
              </el-icon>
            </div>
            <div class="metric-content">
              <div class="metric-value">{{ cardList[0]?.value || "-" }}</div>
              <div class="metric-label">
                {{ cardList[0]?.label || "累计合同金额" }}
              </div>
              <div class="metric-trend" v-if="trendList[0] && trendList[0].show">
                <el-icon :class="trendList[0].up ? 'trend-up' : 'trend-down'">
                  <component :is="trendList[0].up ? ArrowUp : ArrowDown" />
                </el-icon>
                <span>
                  {{ trendList[0].up ? "较上月增长" : "较上月下降" }}
                  {{ trendList[0].percent }}%
                </span>
              </div>
            </div>
          </div>

          <div class="metric-card success">
            <div class="metric-icon">
              <el-icon>
                <Wallet />
              </el-icon>
            </div>
            <div class="metric-content">
              <div class="metric-value">{{ cardList[6]?.value || "-" }}</div>
              <div class="metric-label">
                {{ cardList[6]?.label || "累计毛利润" }}
              </div>
              <div class="metric-trend" v-if="trendList[1] && trendList[1].show">
                <el-icon :class="trendList[1].up ? 'trend-up' : 'trend-down'">
                  <component :is="trendList[1].up ? ArrowUp : ArrowDown" />
                </el-icon>
                <span>
                  {{ trendList[1].up ? "较上月增长" : "较上月下降" }}
                  {{ trendList[1].percent }}%
                </span>
              </div>
            </div>
          </div>

          <div class="metric-card warning">
            <div class="metric-icon">
              <el-icon>
                <PieChart />
              </el-icon>
            </div>
            <div class="metric-content">
              <div class="metric-value">{{ cardList[7]?.value || "-" }}</div>
              <div class="metric-label">
                {{ cardList[7]?.label || "累计毛利率" }}
              </div>
              <div class="metric-trend" v-if="trendList[2] && trendList[2].show">
                <el-icon :class="trendList[2].up ? 'trend-up' : 'trend-down'">
                  <component :is="trendList[2].up ? ArrowUp : ArrowDown" />
                </el-icon>
                <span>
                  {{ trendList[2].up ? "较上月增长" : "较上月下降" }}
                  {{ trendList[2].percent }}%
                </span>
              </div>
            </div>
          </div>

          <div class="metric-card info">
            <div class="metric-icon">
              <el-icon>
                <TrendCharts />
              </el-icon>
            </div>
            <div class="metric-content">
              <div class="metric-value">{{ cardList[1]?.value || "-" }}</div>
              <div class="metric-label">
                {{ cardList[1]?.label || "累计项目回款" }}
              </div>
              <div class="metric-trend" v-if="trendList[3] && trendList[3].show">
                <el-icon :class="trendList[3].up ? 'trend-up' : 'trend-down'">
                  <component :is="trendList[3].up ? ArrowUp : ArrowDown" />
                </el-icon>
                <span>
                  {{ trendList[3].up ? "较上月增长" : "较上月下降" }}
                  {{ trendList[3].percent }}%
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 详细指标卡片 -->
      <div class="detailed-metrics-section">
        <h3 class="section-title">合同执行情况</h3>
        <div class="card-section">
          <div class="data-card" v-for="card in cardList.slice(2, 6)" :key="card.label">
            <div class="card-label">{{ card.label }}</div>
            <div class="card-value">{{ card.value }}</div>
          </div>
        </div>
      </div>

      <!-- 合同收付款情况 -->
      <div class="receivables-payables-section">
        <h3 class="section-title">合同收付款情况</h3>
        <div class="receivables-payables-grid">
          <div class="receivables-card">
            <div class="card-header">
              <el-icon>
                <Money />
              </el-icon>
              <span>累计收款</span>
            </div>
            <div class="card-amount">{{ cardList[8]?.value || "-" }}</div>
            <div class="card-trend positive">
              <el-icon>
                <ArrowUp />
              </el-icon>
              <span>较上月增长 {{ trendList[4].percent }}%</span>
            </div>
          </div>

          <div class="payables-card">
            <div class="card-header">
              <el-icon>
                <Wallet />
              </el-icon>
              <span>累计付款</span>
            </div>
            <div class="card-amount">{{ cardList[9]?.value || "-" }}</div>
            <div class="card-trend negative">
              <el-icon>
                <ArrowDown />
              </el-icon>
              <span>较上月下降 {{ trendList[5].percent }}%</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import {
  getAllSalesContracts,
  getAllPurchaseContracts,
  getAllSalesPayments,
  getAllPurchasePayments,
} from "@/api/contractAnalysis";
import {
  TrendCharts,
  Download,
  Money,
  Wallet,
  PieChart,
  ArrowUp,
  ArrowDown,
} from "@element-plus/icons-vue";

const viewType = ref("all");
const loading = ref(true);
const cardList = ref([
  { label: "累计合同金额（元）", value: "-" },
  { label: "累计项目回款", value: "-" },
  { label: "累计回款比例", value: "-" },
  { label: "累计销售开票金额", value: "-" },
  { label: "累计采购开票金额", value: "-" },
  { label: "累计采购合同金额（元）", value: "-" },
  { label: "累计净收入（元）", value: "-" },
  { label: "净收入率", value: "-" },
  { label: "累计收款", value: "-" },
  { label: "累计付款", value: "-" },
]);

// 新增：动态趋势数据
const trendList = ref([
  { percent: 0, up: true }, // 合同金额
  { percent: 0, up: true }, // 净收入
  { percent: 0, up: true }, // 净收入率
  { percent: 0, up: true }, // 项目回款
  { percent: 0, up: true }, // 累计收款
  { percent: 0, up: false }, // 累计付款
]);

// eslint-disable-next-line @typescript-eslint/no-explicit-any
const cache = {} as Record<string, any>;

function getMonth(date: Date) {
  return date.getFullYear() + "-" + (date.getMonth() + 1);
}

function getLastMonth(date: Date) {
  const d = new Date(date);
  d.setMonth(d.getMonth() - 1);
  return getMonth(d);
}

// eslint-disable-next-line @typescript-eslint/no-explicit-any
function getMonthData(list: any[], field: string, month: string) {
  return list
    .filter((item) => {
      if (!item.contractDate && !item.time) return false;
      const d = new Date(item.contractDate || item.time);
      return getMonth(d) === month;
    })
    .reduce((sum, item) => sum + (Number(item[field]) || 0), 0);
}

async function fetchData() {
  loading.value = true;
  if (cache[viewType.value]) {
    cardList.value = cache[viewType.value];
    loading.value = false;
    return;
  }
  try {
    // 获取真实数据
    const [salesContracts, purchaseContracts, salesPayments, purchasePayments] =
      await Promise.all([
        getAllSalesContracts(),
        getAllPurchaseContracts(),
        getAllSalesPayments(),
        getAllPurchasePayments(),
      ]);

    // 根据viewType筛选数据
    let filteredSalesContracts = salesContracts;
    let filteredPurchaseContracts = purchaseContracts;
    let filteredSalesPayments = salesPayments;
    let filteredPurchasePayments = purchasePayments;

    if (viewType.value === "internal") {
      // 筛选集团内的销售合同 (projectSource = 0)
      filteredSalesContracts = salesContracts.filter(
        (contract) => contract.projectSource === 0
      );

      // 获取集团内销售合同的ID列表
      const internalSalesContractIds = filteredSalesContracts.map(
        (contract) => contract.id
      );

      // 筛选绑定到集团内销售合同的采购合同
      filteredPurchaseContracts = purchaseContracts.filter(
        (contract) =>
          contract.salesId &&
          internalSalesContractIds.includes(contract.salesId)
      );

      // 筛选集团内销售合同的收款记录
      filteredSalesPayments = salesPayments.filter(
        (payment) =>
          payment.salesId && internalSalesContractIds.includes(payment.salesId)
      );

      // 筛选集团内采购合同的付款记录
      const internalPurchaseContractIds = filteredPurchaseContracts.map(
        (contract) => contract.id
      );
      filteredPurchasePayments = purchasePayments.filter(
        (payment) =>
          payment.purchaseId &&
          internalPurchaseContractIds.includes(payment.purchaseId)
      );
    } else if (viewType.value === "external") {
      // 筛选集团外的销售合同 (projectSource = 1)
      filteredSalesContracts = salesContracts.filter(
        (contract) => contract.projectSource === 1
      );

      // 获取集团外销售合同的ID列表
      const externalSalesContractIds = filteredSalesContracts.map(
        (contract) => contract.id
      );

      // 筛选绑定到集团外销售合同的采购合同
      filteredPurchaseContracts = purchaseContracts.filter(
        (contract) =>
          contract.salesId &&
          externalSalesContractIds.includes(contract.salesId)
      );

      // 筛选集团外销售合同的收款记录
      filteredSalesPayments = salesPayments.filter(
        (payment) =>
          payment.salesId && externalSalesContractIds.includes(payment.salesId)
      );

      // 筛选集团外采购合同的付款记录
      const externalPurchaseContractIds = filteredPurchaseContracts.map(
        (contract) => contract.id
      );
      filteredPurchasePayments = purchasePayments.filter(
        (payment) =>
          payment.purchaseId &&
          externalPurchaseContractIds.includes(payment.purchaseId)
      );
    }
    // viewType.value === 'all' 时使用所有数据

    console.log(
      `筛选结果: 销售合同${filteredSalesContracts.length}条, 采购合同${filteredPurchaseContracts.length}条, 销售收款${filteredSalesPayments.length}条, 采购付款${filteredPurchasePayments.length}条`
    );

    // 计算统计数据
    const totalContractAmount = filteredSalesContracts.reduce(
      (sum, contract) => sum + (Number(contract.contractAmount) || 0),
      0
    );
    const totalProjectPayment = filteredSalesPayments.reduce(
      (sum, payment) => sum + (Number(payment.account) || 0),
      0
    );
    const totalPaymentRate =
      totalContractAmount > 0
        ? ((totalProjectPayment / totalContractAmount) * 100).toFixed(2) + "%"
        : "0%";
    const totalPurchaseContractAmount = filteredPurchaseContracts.reduce(
      (sum, contract) => sum + (Number(contract.contractAmount) || 0),
      0
    );
    const totalPurchasePayment = filteredPurchasePayments.reduce(
      (sum, payment) => sum + (Number(payment.account) || 0),
      0
    );
    const netIncome = totalProjectPayment - totalPurchasePayment;
    const netIncomeRate =
      totalProjectPayment > 0
        ? ((netIncome / totalProjectPayment) * 100).toFixed(2) + "%"
        : "0%";

    // 动态趋势计算
    const now = new Date();
    const thisMonth = getMonth(now);
    const lastMonth = getLastMonth(now);
    // 合同金额
    const contractAmountThis = getMonthData(
      filteredSalesContracts,
      "contractAmount",
      thisMonth
    );
    const contractAmountLast = getMonthData(
      filteredSalesContracts,
      "contractAmount",
      lastMonth
    );
    // 净收入
    const netIncomeThis =
      contractAmountThis -
      getMonthData(filteredPurchaseContracts, "contractAmount", thisMonth);
    const netIncomeLast =
      contractAmountLast -
      getMonthData(filteredPurchaseContracts, "contractAmount", lastMonth);
    // 净收入率
    const netIncomeRateThis =
      contractAmountThis > 0 ? (netIncomeThis / contractAmountThis) * 100 : 0;
    const netIncomeRateLast =
      contractAmountLast > 0 ? (netIncomeLast / contractAmountLast) * 100 : 0;
    // 项目回款
    const paymentThis = getMonthData(
      filteredSalesPayments,
      "account",
      thisMonth
    );
    const paymentLast = getMonthData(
      filteredSalesPayments,
      "account",
      lastMonth
    );
    // 累计收款
    const receiveThis = paymentThis;
    const receiveLast = paymentLast;
    // 累计付款
    const payThis = getMonthData(
      filteredPurchasePayments,
      "account",
      thisMonth
    );
    const payLast = getMonthData(
      filteredPurchasePayments,
      "account",
      lastMonth
    );

    // eslint-disable-next-line no-inner-declarations
    function getTrend(thisVal: number, lastVal: number) {
      if (!lastVal || lastVal === 0)
        return { percent: 0, up: true, show: false };
      const percent = ((thisVal - lastVal) / Math.abs(lastVal)) * 100;
      return { percent: Math.abs(percent), up: percent >= 0, show: true };
    }
    trendList.value = [
      getTrend(contractAmountThis, contractAmountLast),
      getTrend(netIncomeThis, netIncomeLast),
      getTrend(netIncomeRateThis, netIncomeRateLast),
      getTrend(paymentThis, paymentLast),
      getTrend(receiveThis, receiveLast),
      getTrend(payThis, payLast),
    ];

    cardList.value = [
      { label: "累计合同金额（元）", value: formatAmount(totalContractAmount) },
      { label: "累计项目回款", value: formatAmount(totalProjectPayment) },
      { label: "累计回款比例", value: totalPaymentRate },
      { label: "累计销售开票金额", value: formatAmount(totalProjectPayment) },
      { label: "累计采购开票金额", value: formatAmount(totalPurchasePayment) },
      {
        label: "累计采购合同金额（元）",
        value: formatAmount(totalPurchaseContractAmount),
      },
      { label: "累计净收入（元）", value: formatAmount(netIncome) },
      { label: "净收入率", value: netIncomeRate },
      { label: "累计收款", value: formatAmount(totalProjectPayment) },
      { label: "累计付款", value: formatAmount(totalPurchasePayment) },
    ];
    cache[viewType.value] = cardList.value;
  } catch (e) {
    console.error("获取数据失败:", e);
    cardList.value = cardList.value.map((c) => ({ ...c, value: "-" }));
  }
  loading.value = false;
}

// 格式化金额
const formatAmount = (value: number) => {
  return `¥${value.toLocaleString("zh-CN", {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  })}`;
};

onMounted(() => {
  fetchData();
});
</script>

<style scoped>
.overview-container {
  width: 100%;
  max-width: 1400px;
  margin: 0 auto;
  padding: 24px;
  background: #f5f7fa;
  min-height: 100vh;
}

.overview-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
  background: #fff;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  flex-wrap: wrap;
  gap: 16px;
}

.header-left {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.page-title {
  display: flex;
  align-items: center;
  gap: 12px;
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  color: #333;
}

.page-subtitle {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 16px;
}

.section-title {
  margin: 0 0 20px 0;
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.key-metrics-section {
  margin-bottom: 32px;
}

.metrics-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
}

.metric-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  display: flex;
  align-items: center;
  gap: 16px;
  transition: all 0.3s ease;
}

.metric-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
}

.metric-card.primary {
  border-left: 4px solid #409eff;
}

.metric-card.success {
  border-left: 4px solid #67c23a;
}

.metric-card.warning {
  border-left: 4px solid #e6a23c;
}

.metric-card.info {
  border-left: 4px solid #909399;
}

.metric-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 24px;
}

.metric-card.primary .metric-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.metric-card.success .metric-icon {
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
}

.metric-card.warning .metric-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.metric-card.info .metric-icon {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.metric-content {
  flex: 1;
}

.metric-value {
  font-size: 28px;
  font-weight: bold;
  color: #333;
  margin-bottom: 4px;
}

.metric-label {
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
}

.metric-trend {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #666;
}

.trend-up {
  color: #67c23a;
}

.trend-down {
  color: #f56c6c;
}

.detailed-metrics-section {
  margin-bottom: 32px;
}

.card-section {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 16px;
}

.data-card {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border: 1px solid #e8e8e8;
  transition: all 0.3s ease;
}

.data-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

.card-label {
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
  font-weight: 500;
}

.card-value {
  font-size: 24px;
  font-weight: bold;
  color: #1976d2;
  line-height: 1.2;
}

.receivables-payables-section {
  margin-bottom: 32px;
}

.receivables-payables-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 20px;
}

.receivables-card,
.payables-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.receivables-card:hover,
.payables-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 16px;
  font-weight: 600;
  color: #333;
}

.card-amount {
  font-size: 32px;
  font-weight: bold;
  color: #333;
  margin-bottom: 12px;
}

.card-trend {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
}

.card-trend.positive {
  color: #67c23a;
}

.card-trend.negative {
  color: #f56c6c;
}

@media (max-width: 768px) {
  .overview-container {
    padding: 16px;
  }

  .overview-header {
    flex-direction: column;
    align-items: stretch;
  }

  .metrics-grid,
  .card-section,
  .receivables-payables-grid {
    grid-template-columns: 1fr;
  }
}
</style>
