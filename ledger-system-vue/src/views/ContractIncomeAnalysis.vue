<template>
  <div class="contract-income-analysis">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h2 class="page-title">
          <el-icon>
            <TrendCharts />
          </el-icon>
          合同收入分析
        </h2>
        <p class="page-subtitle">按时间维度分析合同收入和付款情况</p>
      </div>
      <div class="header-actions">
        <el-button type="primary" @click="exportAnalysis">
          <el-icon>
            <Download />
          </el-icon>
          导出分析报告
        </el-button>
      </div>
    </div>

    <!-- 筛选条件 -->
    <div class="filter-section">
      <el-card class="filter-card">
        <div class="filter-row">
          <div class="filter-item">
            <span class="filter-label">项目类型：</span>
            <el-radio-group v-model="viewType" @change="onViewTypeChange">
              <el-radio-button label="all">全部</el-radio-button>
              <el-radio-button label="internal">集团内</el-radio-button>
              <el-radio-button label="external">集团外</el-radio-button>
            </el-radio-group>
          </div>
          <div class="filter-item">
            <span class="filter-label">时间维度：</span>
            <el-select v-model="timeDimension" @change="onTimeDimensionChange">
              <el-option label="月度分析" value="month" />
              <el-option label="季度分析" value="quarter" />
              <el-option label="年度分析" value="year" />
            </el-select>
          </div>
          <div class="filter-item">
            <span class="filter-label">时间范围：</span>
            <el-date-picker
              v-model="dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
              @change="onDateRangeChange"
            />
          </div>
          <div class="filter-item">
            <span class="filter-label">合同类型：</span>
            <el-select v-model="contractType" @change="fetchAnalysisData">
              <el-option label="全部合同" value="all" />
              <el-option label="销售合同" value="sales" />
              <el-option label="采购合同" value="purchase" />
            </el-select>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 统计概览 -->
    <div class="overview-section">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-header">
              <el-icon>
                <Money />
              </el-icon>
              <span>总收入</span>
            </div>
            <div class="stat-value">{{ formatAmount(totalIncome) }}</div>
            <div class="stat-trend positive">
              <el-icon>
                <ArrowUp />
              </el-icon>
              <span>较上期增长 {{ incomeGrowth }}%</span>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-header">
              <el-icon>
                <Wallet />
              </el-icon>
              <span>总付款</span>
            </div>
            <div class="stat-value">{{ formatAmount(totalPayment) }}</div>
            <div class="stat-trend negative">
              <el-icon>
                <ArrowDown />
              </el-icon>
              <span>较上期增长 {{ paymentGrowth }}%</span>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-header">
              <el-icon>
                <Document />
              </el-icon>
              <span>合同数量</span>
            </div>
            <div class="stat-value">{{ contractCount }}</div>
            <div class="stat-trend neutral">
              <el-icon>
                <InfoFilled />
              </el-icon>
              <span>活跃合同</span>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-header">
              <el-icon>
                <PieChart />
              </el-icon>
              <span>净收入</span>
            </div>
            <div class="stat-value">{{ formatAmount(netIncome) }}</div>
            <div
              class="stat-trend"
              :class="netIncomeGrowth >= 0 ? 'positive' : 'negative'"
            >
              <el-icon
                :class="netIncomeGrowth >= 0 ? 'ArrowUp' : 'ArrowDown'"
              />
              <span
                >较上期{{ netIncomeGrowth >= 0 ? "增长" : "下降" }}
                {{ Math.abs(netIncomeGrowth) }}%</span
              >
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 图表分析 -->
    <div class="chart-section">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-card class="chart-card">
            <div class="chart-header">
              <h3>收入趋势</h3>
            </div>
            <div class="chart-container">
              <div ref="incomeChartRef" class="chart"></div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card class="chart-card">
            <div class="chart-header">
              <h3>付款趋势</h3>
            </div>
            <div class="chart-container">
              <div ref="paymentChartRef" class="chart"></div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 详细数据表格 -->
    <div class="table-section">
      <el-card class="table-card">
        <div class="table-header">
          <h3>详细数据</h3>
          <div class="table-actions">
            <el-button size="small" @click="exportTableData">
              <el-icon>
                <Download />
              </el-icon>
              导出数据
            </el-button>
          </div>
        </div>
        <el-table :data="tableData" style="width: 100%" border>
          <el-table-column prop="period" label="时间周期" width="120" />
          <el-table-column prop="salesIncome" label="销售收入" width="150">
            <template #default="scope">
              {{ formatAmount(scope.row.salesIncome) }}
            </template>
          </el-table-column>
          <el-table-column prop="purchasePayment" label="采购付款" width="150">
            <template #default="scope">
              {{ formatAmount(scope.row.purchasePayment) }}
            </template>
          </el-table-column>
          <el-table-column prop="netAmount" label="净收入" width="150">
            <template #default="scope">
              <span :class="scope.row.netAmount >= 0 ? 'positive' : 'negative'">
                {{ formatAmount(scope.row.netAmount) }}
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="contractCount" label="合同数量" width="120" />
          <el-table-column prop="growthRate" label="增长率" width="120">
            <template #default="scope">
              <span
                :class="scope.row.growthRate >= 0 ? 'positive' : 'negative'"
              >
                {{ scope.row.growthRate >= 0 ? "+" : ""
                }}{{ scope.row.growthRate.toFixed(2) }}%
              </span>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from "vue";
import { ElMessage } from "element-plus";
import {
  TrendCharts,
  Download,
  Money,
  Wallet,
  Document,
  PieChart,
  ArrowUp,
  ArrowDown,
  InfoFilled,
} from "@element-plus/icons-vue";
import * as echarts from "echarts";
import * as XLSX from "xlsx";
import { getContractIncomeAnalysis } from "@/api/contractAnalysis";

// 页面数据
const viewType = ref("all");
const timeDimension = ref("month");
const dateRange = ref<string[]>([]);
const contractType = ref("all");

// 统计数据
const totalIncome = ref(0);
const totalPayment = ref(0);
const contractCount = ref(0);
const netIncome = ref(0);
const incomeGrowth = ref(0);
const paymentGrowth = ref(0);
const netIncomeGrowth = ref(0);

// 图表引用
const incomeChartRef = ref<HTMLElement>();
const paymentChartRef = ref<HTMLElement>();
let incomeChart: echarts.ECharts | null = null;
let paymentChart: echarts.ECharts | null = null;

// 表格数据
const tableData = ref<any[]>([]);

// 格式化金额
const formatAmount = (value?: number) => {
  if (value == null) return "¥0.00";
  return `¥${Number(value).toLocaleString("zh-CN", {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  })}`;
};

// 项目类型变化
const onViewTypeChange = () => {
  fetchAnalysisData();
};

// 时间维度变化
const onTimeDimensionChange = () => {
  fetchAnalysisData();
};

// 日期范围变化
const onDateRangeChange = () => {
  fetchAnalysisData();
};

// 获取分析数据
const fetchAnalysisData = async () => {
  try {
    // 调用后端API获取真实数据
    const analysisData = await getContractIncomeAnalysis({
      viewType: viewType.value,
      timeDimension: timeDimension.value,
      startDate: dateRange.value?.[0],
      endDate: dateRange.value?.[1],
      contractType: contractType.value,
    });

    // 更新统计数据
    updateStatistics(analysisData.statistics);

    // 更新表格数据
    tableData.value = analysisData.data;

    // 更新图表
    updateCharts(analysisData.data);
  } catch (error) {
    ElMessage.error("获取分析数据失败");
  }
};

// 更新统计数据
const updateStatistics = (statistics: any) => {
  totalIncome.value = statistics.totalIncome || 0;
  totalPayment.value = statistics.totalPayment || 0;
  netIncome.value = statistics.netIncome || 0;
  contractCount.value = statistics.contractCount || 0;
  incomeGrowth.value = statistics.incomeGrowth || 0;
  paymentGrowth.value = statistics.paymentGrowth || 0;
  netIncomeGrowth.value = statistics.netIncomeGrowth || 0;
};

// 更新图表
const updateCharts = (data: any[]) => {
  updateIncomeChart(data);
  updatePaymentChart(data);
};

// 更新收入图表
const updateIncomeChart = (data: any[]) => {
  if (!incomeChartRef.value) return;

  if (!incomeChart) {
    incomeChart = echarts.init(incomeChartRef.value);
  }

  const option = {
    title: {
      text: "收入趋势",
      left: "center",
    },
    tooltip: {
      trigger: "axis",
      formatter: function (params: any) {
        let result = params[0].name + "<br/>";
        params.forEach((param: any) => {
          result +=
            param.marker +
            param.seriesName +
            ": " +
            formatAmount(param.value) +
            "<br/>";
        });
        return result;
      },
    },
    legend: {
      data: ["销售收入", "净收入"],
      top: 30,
    },
    grid: {
      left: "3%",
      right: "4%",
      bottom: "3%",
      containLabel: true,
    },
    xAxis: {
      type: "category",
      data: data.map((item) => item.period),
    },
    yAxis: {
      type: "value",
      name: "金额 (元)",
    },
    series: [
      {
        name: "销售收入",
        type: "line",
        data: data.map((item) => item.salesIncome),
        itemStyle: { color: "#409eff" },
      },
      {
        name: "净收入",
        type: "line",
        data: data.map((item) => item.netAmount),
        itemStyle: { color: "#67c23a" },
      },
    ],
  };

  incomeChart.setOption(option);
};

// 更新付款图表
const updatePaymentChart = (data: any[]) => {
  if (!paymentChartRef.value) return;

  if (!paymentChart) {
    paymentChart = echarts.init(paymentChartRef.value);
  }

  const option = {
    title: {
      text: "付款趋势",
      left: "center",
    },
    tooltip: {
      trigger: "axis",
      formatter: function (params: any) {
        let result = params[0].name + "<br/>";
        params.forEach((param: any) => {
          result +=
            param.marker +
            param.seriesName +
            ": " +
            formatAmount(param.value) +
            "<br/>";
        });
        return result;
      },
    },
    legend: {
      data: ["采购付款"],
      top: 30,
    },
    grid: {
      left: "3%",
      right: "4%",
      bottom: "3%",
      containLabel: true,
    },
    xAxis: {
      type: "category",
      data: data.map((item) => item.period),
    },
    yAxis: {
      type: "value",
      name: "金额 (元)",
    },
    series: [
      {
        name: "采购付款",
        type: "bar",
        data: data.map((item) => item.purchasePayment),
        itemStyle: { color: "#f56c6c" },
      },
    ],
  };

  paymentChart.setOption(option);
};

// 导出分析报告
const exportAnalysis = () => {
  const wsData = [
    ["合同收入分析报告"],
    [""],
    [
      "项目类型",
      viewType.value === "all"
        ? "全部"
        : viewType.value === "internal"
        ? "集团内"
        : "集团外",
    ],
    [
      "时间维度",
      timeDimension.value === "month"
        ? "月度分析"
        : timeDimension.value === "quarter"
        ? "季度分析"
        : "年度分析",
    ],
    ["时间范围", (dateRange.value as string[])?.join(" 至 ") || "全部时间"],
    [
      "合同类型",
      contractType.value === "all"
        ? "全部合同"
        : contractType.value === "sales"
        ? "销售合同"
        : "采购合同",
    ],
    [""],
    ["统计概览"],
    ["总收入", formatAmount(totalIncome.value)],
    ["总付款", formatAmount(totalPayment.value)],
    ["净收入", formatAmount(netIncome.value)],
    ["合同数量", contractCount.value],
    [""],
    ["详细数据"],
    ["时间周期", "销售收入", "采购付款", "净收入", "合同数量", "增长率"],
    ...tableData.value.map((item) => [
      item.period,
      formatAmount(item.salesIncome),
      formatAmount(item.purchasePayment),
      formatAmount(item.netAmount),
      item.contractCount,
      `${item.growthRate >= 0 ? "+" : ""}${item.growthRate.toFixed(2)}%`,
    ]),
  ];

  const ws = XLSX.utils.aoa_to_sheet(wsData);
  const wb = XLSX.utils.book_new();
  XLSX.utils.book_append_sheet(wb, ws, "合同收入分析");
  XLSX.writeFile(
    wb,
    `合同收入分析_${new Date().toISOString().slice(0, 10)}.xlsx`
  );

  ElMessage.success("分析报告导出成功");
};

// 导出表格数据
const exportTableData = () => {
  const wsData = [
    ["时间周期", "销售收入", "采购付款", "净收入", "合同数量", "增长率"],
    ...tableData.value.map((item) => [
      item.period,
      item.salesIncome,
      item.purchasePayment,
      item.netAmount,
      item.contractCount,
      item.growthRate,
    ]),
  ];

  const ws = XLSX.utils.aoa_to_sheet(wsData);
  const wb = XLSX.utils.book_new();
  XLSX.utils.book_append_sheet(wb, ws, "详细数据");
  XLSX.writeFile(
    wb,
    `合同收入详细数据_${new Date().toISOString().slice(0, 10)}.xlsx`
  );

  ElMessage.success("数据导出成功");
};

// 监听窗口大小变化
window.addEventListener("resize", () => {
  if (incomeChart) {
    incomeChart.resize();
  }
  if (paymentChart) {
    paymentChart.resize();
  }
});

onMounted(() => {
  // 设置默认时间范围
  const endDate = new Date();
  const startDate = new Date();
  startDate.setMonth(startDate.getMonth() - 11);
  dateRange.value = [
    startDate.toISOString().slice(0, 10),
    endDate.toISOString().slice(0, 10),
  ];

  fetchAnalysisData();
});
</script>

<style scoped>
.contract-income-analysis {
  padding: 24px;
  background: #f5f7fa;
  min-height: 100vh;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
  background: #fff;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
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

.filter-section {
  margin-bottom: 24px;
}

.filter-card {
  background: #fff;
}

.filter-row {
  display: flex;
  align-items: center;
  gap: 24px;
  flex-wrap: wrap;
}

.filter-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.filter-label {
  font-weight: 500;
  color: #333;
  white-space: nowrap;
}

.overview-section {
  margin-bottom: 24px;
}

.stat-card {
  height: 120px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.stat-header {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #666;
  font-size: 14px;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: #333;
}

.stat-trend {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
}

.stat-trend.positive {
  color: #67c23a;
}

.stat-trend.negative {
  color: #f56c6c;
}

.stat-trend.neutral {
  color: #909399;
}

.chart-section {
  margin-bottom: 24px;
}

.chart-card {
  height: 400px;
}

.chart-header {
  margin-bottom: 16px;
}

.chart-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.chart-container {
  height: 320px;
}

.chart {
  width: 100%;
  height: 100%;
}

.table-section {
  margin-bottom: 24px;
}

.table-card {
  background: #fff;
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.table-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.table-actions {
  display: flex;
  gap: 12px;
}

.positive {
  color: #67c23a;
}

.negative {
  color: #f56c6c;
}
</style>
