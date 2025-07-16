<template>
  <div class="contract-financial-detail">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <el-button @click="goBack" icon="ArrowLeft" text>返回</el-button>
        <h2 class="page-title">
          <el-icon><TrendCharts /></el-icon>
          合同财务详情
        </h2>
        <p class="page-subtitle">
          {{ contractInfo.contractName || "合同财务分析" }}
        </p>
      </div>
      <div class="header-actions">
        <el-button type="primary" @click="exportReport">
          <el-icon><Download /></el-icon>
          导出财务报告
        </el-button>
      </div>
    </div>

    <!-- 合同基本信息 -->
    <div class="contract-info-section">
      <h3 class="section-title">合同基本信息</h3>
      <div class="info-grid">
        <div class="info-item">
          <span class="info-label">合同名称：</span>
          <span class="info-value">{{ contractInfo.contractName || "-" }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">分包单位：</span>
          <span class="info-value">{{
            contractInfo.subcontractingUnit || "-"
          }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">项目负责人：</span>
          <span class="info-value">{{ contractInfo.director || "-" }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">合同金额：</span>
          <span class="info-value">{{
            formatAmount(contractInfo.contractAmount)
          }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">合同日期：</span>
          <span class="info-value">{{
            formatDate(contractInfo.contractDate)
          }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">合同工期：</span>
          <span class="info-value">{{
            contractInfo.contractPeriod || "-"
          }}</span>
        </div>
      </div>
    </div>

    <!-- 财务分析组件 -->
    <FinancialAnalysis
      v-if="financialData"
      :data="financialData"
      :show-chart="true"
      title="合同财务分析"
    />

    <!-- 财务指标趋势图 -->
    <div class="trend-chart-section">
      <h3 class="section-title">财务指标趋势</h3>
      <div class="chart-container">
        <div ref="trendChartRef" class="trend-chart"></div>
      </div>
    </div>

    <!-- 偏差分析详情 -->
    <div class="deviation-analysis-section">
      <h3 class="section-title">偏差分析详情</h3>
      <div class="deviation-grid">
        <div class="deviation-item">
          <div class="deviation-header">
            <el-icon><Warning /></el-icon>
            <span>成本偏差分析</span>
          </div>
          <div class="deviation-content">
            <div class="deviation-row">
              <span class="deviation-label">计划成本：</span>
              <span class="deviation-value">{{
                formatAmount(financialData?.planCost)
              }}</span>
            </div>
            <div class="deviation-row">
              <span class="deviation-label">实际成本：</span>
              <span class="deviation-value">{{
                formatAmount(financialData?.actualCost)
              }}</span>
            </div>
            <div class="deviation-row">
              <span class="deviation-label">成本偏差：</span>
              <span
                class="deviation-value"
                :class="getDeviationClass(costDeviation)"
              >
                {{ formatAmount(costDeviation) }}
              </span>
            </div>
            <div class="deviation-row">
              <span class="deviation-label">成本偏差率：</span>
              <span
                class="deviation-value"
                :class="getDeviationClass(costDeviationRate)"
              >
                {{ formatPercentage(costDeviationRate) }}
              </span>
            </div>
          </div>
        </div>

        <div class="deviation-item">
          <div class="deviation-header">
            <el-icon><PieChart /></el-icon>
            <span>利润偏差分析</span>
          </div>
          <div class="deviation-content">
            <div class="deviation-row">
              <span class="deviation-label">计划毛利润：</span>
              <span class="deviation-value">{{
                formatAmount(financialData?.planProfit)
              }}</span>
            </div>
            <div class="deviation-row">
              <span class="deviation-label">实际毛利润：</span>
              <span class="deviation-value">{{
                formatAmount(financialData?.actualProfit)
              }}</span>
            </div>
            <div class="deviation-row">
              <span class="deviation-label">利润偏差：</span>
              <span
                class="deviation-value"
                :class="getDeviationClass(financialData?.profitDiffAmount)"
              >
                {{ formatAmount(financialData?.profitDiffAmount) }}
              </span>
            </div>
            <div class="deviation-row">
              <span class="deviation-label">偏差率：</span>
              <span
                class="deviation-value"
                :class="getDeviationClass(financialData?.profitDiffRate)"
              >
                {{ formatPercentage(financialData?.profitDiffRate) }}
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 建议和措施 -->
    <div class="suggestions-section">
      <h3 class="section-title">改进建议</h3>
      <div class="suggestions-content">
        <div class="suggestion-item" v-if="costDeviation > 0">
          <el-icon class="suggestion-icon warning"><Warning /></el-icon>
          <div class="suggestion-text">
            <strong>成本控制建议：</strong
            >实际成本超出计划成本，建议加强成本管控，优化采购流程，寻找更优质的供应商。
          </div>
        </div>
        <div
          class="suggestion-item"
          v-if="
            financialData?.profitDiffAmount &&
            financialData.profitDiffAmount < 0
          "
        >
          <el-icon class="suggestion-icon warning"><Warning /></el-icon>
          <div class="suggestion-text">
            <strong>利润提升建议：</strong
            >实际毛利润低于计划毛利润，建议优化项目执行效率，控制成本支出，提高项目利润率。
          </div>
        </div>
        <div
          class="suggestion-item"
          v-if="
            financialData?.profitDiffAmount &&
            financialData.profitDiffAmount > 0
          "
        >
          <el-icon class="suggestion-icon success"><SuccessFilled /></el-icon>
          <div class="suggestion-text">
            <strong>优秀表现：</strong
            >实际毛利润超出计划毛利润，项目执行效果良好，建议总结经验并推广到其他项目。
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import {
  TrendCharts,
  Download,
  Warning,
  PieChart,
  SuccessFilled,
} from "@element-plus/icons-vue";
import FinancialAnalysis from "@/components/FinancialAnalysis.vue";
import * as echarts from "echarts";
import api from "@/api";

const route = useRoute();
const router = useRouter();

// 页面数据
const contractInfo = ref<any>({});
const financialData = ref<any>(null);
const trendChartRef = ref<HTMLElement>();
let trendChart: echarts.ECharts | null = null;

// 计算属性
const costDeviation = computed(() => {
  if (!financialData.value) return 0;
  return (
    (financialData.value.actualCost || 0) - (financialData.value.planCost || 0)
  );
});

const costDeviationRate = computed(() => {
  if (!financialData.value?.planCost || financialData.value.planCost === 0)
    return 0;
  return (costDeviation.value / financialData.value.planCost) * 100;
});

// 格式化函数
const formatAmount = (value?: number) => {
  if (value == null) return "-";
  return Number(value).toLocaleString("zh-CN", {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  });
};

const formatPercentage = (value?: number) => {
  if (value == null) return "-";
  return `${Number(value).toFixed(2)}%`;
};

const formatDate = (value?: string) => {
  if (!value) return "-";
  return value.replace("T", " ").slice(0, 10);
};

const getDeviationClass = (value?: number) => {
  if (value == null) return "";
  if (value > 0) return "positive";
  if (value < 0) return "negative";
  return "neutral";
};

// 获取合同详情
const fetchContractDetail = async () => {
  const contractId = route.params.id;
  if (!contractId) {
    ElMessage.error("合同ID不存在");
    return;
  }

  // 确保ID是字符串类型，避免JavaScript大整数精度问题
  const id = Array.isArray(contractId) ? contractId[0] : contractId;

  try {
    const res =
      await api.purchaseContractController.getPurchaseContractVoByIdUsingGet({
        id: Number(id),
      });

    if (res?.data?.data) {
      contractInfo.value = res.data.data;

      // 构建财务数据 - 使用正确的字段名
      financialData.value = {
        planCost: res.data.data.plannedCost || 0,
        planProfit: res.data.data.plannedGrossProfit || 0,
        planProfitRate: res.data.data.plannedGrossProfitRate || 0,
        actualCost: res.data.data.plannedCost || 0, // 使用plannedCost作为实际成本
        actualProfit: res.data.data.actualGrossProfit || 0,
        actualProfitRate: res.data.data.actualGrossProfitRate || 0,
        profitDiffAmount: res.data.data.profitDeviationAmount || 0,
        profitDiffRate: res.data.data.deviationRate || 0,
      };

      // 初始化趋势图
      initTrendChart();
    }
  } catch (error) {
    ElMessage.error("获取合同详情失败");
  }
};

// 初始化趋势图
const initTrendChart = () => {
  if (!trendChartRef.value) return;

  trendChart = echarts.init(trendChartRef.value);

  const option = {
    title: {
      text: "财务指标趋势分析",
      left: "center",
    },
    tooltip: {
      trigger: "axis",
      formatter: function (params: any) {
        let result = params[0].name + "<br/>";
        params.forEach((param: any) => {
          if (
            param.seriesName.includes("成本") ||
            param.seriesName.includes("利润")
          ) {
            result +=
              param.marker +
              param.seriesName +
              ": ¥" +
              formatAmount(param.value) +
              "<br/>";
          } else {
            result +=
              param.marker +
              param.seriesName +
              ": " +
              formatPercentage(param.value) +
              "<br/>";
          }
        });
        return result;
      },
    },
    legend: {
      data: [
        "计划成本",
        "实际成本",
        "计划毛利润",
        "实际毛利润",
        "计划毛利率",
        "实际毛利率",
      ],
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
      data: ["成本", "毛利润", "毛利率"],
    },
    yAxis: [
      {
        type: "value",
        name: "金额 (元)",
        position: "left",
      },
      {
        type: "value",
        name: "百分比 (%)",
        position: "right",
        max: 100,
      },
    ],
    series: [
      {
        name: "计划成本",
        type: "bar",
        data: [financialData.value?.planCost || 0, null, null],
        yAxisIndex: 0,
        itemStyle: { color: "#409eff" },
      },
      {
        name: "实际成本",
        type: "bar",
        data: [financialData.value?.actualCost || 0, null, null],
        yAxisIndex: 0,
        itemStyle: { color: "#67c23a" },
      },
      {
        name: "计划毛利润",
        type: "bar",
        data: [null, financialData.value?.planProfit || 0, null],
        yAxisIndex: 0,
        itemStyle: { color: "#e6a23c" },
      },
      {
        name: "实际毛利润",
        type: "bar",
        data: [null, financialData.value?.actualProfit || 0, null],
        yAxisIndex: 0,
        itemStyle: { color: "#f56c6c" },
      },
      {
        name: "计划毛利率",
        type: "line",
        data: [null, null, financialData.value?.planProfitRate || 0],
        yAxisIndex: 1,
        itemStyle: { color: "#909399" },
      },
      {
        name: "实际毛利率",
        type: "line",
        data: [null, null, financialData.value?.actualProfitRate || 0],
        yAxisIndex: 1,
        itemStyle: { color: "#409eff" },
      },
    ],
  };

  trendChart.setOption(option);
};

// 返回上一页
const goBack = () => {
  router.back();
};

// 导出财务报告
const exportReport = () => {
  ElMessage.success("财务报告导出功能开发中...");
};

onMounted(() => {
  fetchContractDetail();
});
</script>

<style scoped>
.contract-financial-detail {
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
  align-items: center;
  gap: 16px;
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

.contract-info-section {
  background: #fff;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  margin-bottom: 24px;
}

.section-title {
  margin: 0 0 20px 0;
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 16px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.info-label {
  font-weight: 600;
  color: #666;
  min-width: 100px;
}

.info-value {
  color: #333;
  font-weight: 500;
}

.trend-chart-section {
  background: #fff;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  margin-bottom: 24px;
}

.chart-container {
  height: 400px;
  margin-top: 16px;
}

.trend-chart {
  width: 100%;
  height: 100%;
}

.deviation-analysis-section {
  background: #fff;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  margin-bottom: 24px;
}

.deviation-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 20px;
}

.deviation-item {
  background: #f8fafc;
  border-radius: 8px;
  padding: 20px;
  border: 1px solid #e8eaed;
}

.deviation-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 16px;
  font-weight: 600;
  color: #333;
}

.deviation-content {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.deviation-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #f0f0f0;
}

.deviation-row:last-child {
  border-bottom: none;
}

.deviation-label {
  color: #666;
  font-size: 14px;
}

.deviation-value {
  font-weight: 600;
  color: #333;
  font-size: 14px;
}

.deviation-value.positive {
  color: #67c23a;
}

.deviation-value.negative {
  color: #f56c6c;
}

.deviation-value.neutral {
  color: #909399;
}

.suggestions-section {
  background: #fff;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.suggestions-content {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.suggestion-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 16px;
  background: #f8fafc;
  border-radius: 8px;
  border-left: 4px solid #e6a23c;
}

.suggestion-item .suggestion-icon.success {
  color: #67c23a;
}

.suggestion-item .suggestion-icon.warning {
  color: #e6a23c;
}

.suggestion-text {
  flex: 1;
  line-height: 1.6;
  color: #333;
}

@media (max-width: 768px) {
  .contract-financial-detail {
    padding: 16px;
  }

  .page-header {
    flex-direction: column;
    gap: 16px;
  }

  .info-grid,
  .deviation-grid {
    grid-template-columns: 1fr;
  }
}
</style>
