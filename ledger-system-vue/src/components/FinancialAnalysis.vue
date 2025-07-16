<template>
  <div class="financial-analysis">
    <div class="analysis-header">
      <h3 class="analysis-title">
        <el-icon><TrendCharts /></el-icon>
        {{ props.title || defaultProps.title }}
      </h3>
      <div class="analysis-actions">
        <el-button size="small" @click="exportAnalysis" type="primary">
          <el-icon><Download /></el-icon>
          导出分析
        </el-button>
      </div>
    </div>

    <div class="analysis-content">
      <!-- 计划 vs 实际对比 -->
      <div class="comparison-section">
        <h4 class="section-title">计划 vs 实际对比</h4>
        <div class="comparison-grid">
          <div class="comparison-card planned">
            <div class="card-header">
              <el-icon><Calendar /></el-icon>
              <span>计划数据</span>
            </div>
            <div class="card-content">
              <div class="metric-item">
                <span class="metric-label">计划成本</span>
                <span class="metric-value">{{
                  formatAmount(data.planCost)
                }}</span>
              </div>
              <div class="metric-item">
                <span class="metric-label">计划毛利润</span>
                <span class="metric-value">{{
                  formatAmount(data.planProfit)
                }}</span>
              </div>
              <div class="metric-item">
                <span class="metric-label">计划毛利率</span>
                <span class="metric-value">{{
                  formatPercentage(data.planProfitRate)
                }}</span>
              </div>
            </div>
          </div>

          <div class="comparison-card actual">
            <div class="card-header">
              <el-icon><DataAnalysis /></el-icon>
              <span>实际数据</span>
            </div>
            <div class="card-content">
              <div class="metric-item">
                <span class="metric-label">实际成本</span>
                <span class="metric-value">{{
                  formatAmount(data.actualCost)
                }}</span>
              </div>
              <div class="metric-item">
                <span class="metric-label">实际毛利润</span>
                <span class="metric-value">{{
                  formatAmount(data.actualProfit)
                }}</span>
              </div>
              <div class="metric-item">
                <span class="metric-label">实际毛利率</span>
                <span class="metric-value">{{
                  formatPercentage(data.actualProfitRate)
                }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 偏差分析 -->
      <div class="deviation-section">
        <h4 class="section-title">偏差分析</h4>
        <div class="deviation-grid">
          <div
            class="deviation-card"
            :class="getDeviationClass(data.profitDiffAmount)"
          >
            <div class="card-header">
              <el-icon><Warning /></el-icon>
              <span>利润偏差金额</span>
            </div>
            <div
              class="deviation-value"
              :class="getDeviationClass(data.profitDiffAmount)"
            >
              {{ formatAmount(data.profitDiffAmount) }}
            </div>
            <div class="deviation-trend">
              <el-icon v-if="(data.profitDiffAmount || 0) > 0" class="trend-up"
                ><ArrowUp
              /></el-icon>
              <el-icon
                v-else-if="(data.profitDiffAmount || 0) < 0"
                class="trend-down"
                ><ArrowDown
              /></el-icon>
              <span>{{ getDeviationText(data.profitDiffAmount) }}</span>
            </div>
          </div>

          <div
            class="deviation-card"
            :class="getDeviationClass(data.profitDiffRate)"
          >
            <div class="card-header">
              <el-icon><PieChart /></el-icon>
              <span>偏差率</span>
            </div>
            <div
              class="deviation-value"
              :class="getDeviationClass(data.profitDiffRate)"
            >
              {{ formatPercentage(data.profitDiffRate) }}
            </div>
            <div class="deviation-trend">
              <el-icon v-if="(data.profitDiffRate || 0) > 0" class="trend-up"
                ><ArrowUp
              /></el-icon>
              <el-icon
                v-else-if="(data.profitDiffRate || 0) < 0"
                class="trend-down"
                ><ArrowDown
              /></el-icon>
              <span>{{ getDeviationText(data.profitDiffRate) }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 财务指标图表 -->
      <div
        class="chart-section"
        v-if="props.showChart || defaultProps.showChart"
      >
        <h4 class="section-title">财务指标趋势</h4>
        <div class="chart-container">
          <div ref="chartRef" class="chart"></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from "vue";
import { ElMessage } from "element-plus";
import {
  TrendCharts,
  Download,
  Calendar,
  DataAnalysis,
  Warning,
  PieChart,
  ArrowUp,
  ArrowDown,
} from "@element-plus/icons-vue";
import * as echarts from "echarts";

interface FinancialData {
  planCost?: number;
  planProfit?: number;
  planProfitRate?: number;
  actualCost?: number;
  actualProfit?: number;
  actualProfitRate?: number;
  profitDiffAmount?: number;
  profitDiffRate?: number;
}

interface Props {
  data: FinancialData;
  showChart?: boolean;
  title?: string;
}

// 设置默认值
const defaultProps = {
  showChart: false,
  title: "财务分析",
};

// eslint-disable-next-line no-undef
const props = defineProps<Props>();

const chartRef = ref<HTMLElement>();
let chart: echarts.ECharts | null = null;

// 格式化金额
const formatAmount = (value?: number) => {
  if (value == null) return "-";
  return Number(value).toLocaleString("zh-CN", {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  });
};

// 格式化百分比
const formatPercentage = (value?: number) => {
  if (value == null) return "-";
  return `${Number(value).toFixed(2)}%`;
};

// 获取偏差样式类
const getDeviationClass = (value?: number) => {
  if (value == null) return "";
  if (value > 0) return "positive";
  if (value < 0) return "negative";
  return "neutral";
};

// 获取偏差文本
const getDeviationText = (value?: number) => {
  if (value == null) return "无数据";
  if (value > 0) return "超出计划";
  if (value < 0) return "低于计划";
  return "符合计划";
};

// 导出分析报告
const exportAnalysis = () => {
  const data = [
    ["指标", "数值"],
    ["计划成本", formatAmount(props.data.planCost)],
    ["计划毛利润", formatAmount(props.data.planProfit)],
    ["计划毛利率", formatPercentage(props.data.planProfitRate)],
    ["实际成本", formatAmount(props.data.actualCost)],
    ["实际毛利润", formatAmount(props.data.actualProfit)],
    ["实际毛利率", formatPercentage(props.data.actualProfitRate)],
    ["利润偏差金额", formatAmount(props.data.profitDiffAmount)],
    ["偏差率", formatPercentage(props.data.profitDiffRate)],
  ];

  // 这里可以调用导出API或使用XLSX库
  ElMessage.success("导出功能开发中...");
};

// 初始化图表
const initChart = () => {
  if (!chartRef.value || !(props.showChart || defaultProps.showChart)) return;

  chart = echarts.init(chartRef.value);

  const option = {
    title: {
      text: "财务指标对比",
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
        data: [props.data.planCost || 0, null, null],
        yAxisIndex: 0,
      },
      {
        name: "实际成本",
        type: "bar",
        data: [props.data.actualCost || 0, null, null],
        yAxisIndex: 0,
      },
      {
        name: "计划毛利润",
        type: "bar",
        data: [null, props.data.planProfit || 0, null],
        yAxisIndex: 0,
      },
      {
        name: "实际毛利润",
        type: "bar",
        data: [null, props.data.actualProfit || 0, null],
        yAxisIndex: 0,
      },
      {
        name: "计划毛利率",
        type: "line",
        data: [null, null, props.data.planProfitRate || 0],
        yAxisIndex: 1,
      },
      {
        name: "实际毛利率",
        type: "line",
        data: [null, null, props.data.actualProfitRate || 0],
        yAxisIndex: 1,
      },
    ],
  };

  chart.setOption(option);
};

// 监听数据变化
watch(
  () => props.data,
  () => {
    if (chart && (props.showChart || defaultProps.showChart)) {
      initChart();
    }
  },
  { deep: true }
);

onMounted(() => {
  if (props.showChart || defaultProps.showChart) {
    initChart();
  }
});
</script>

<style scoped>
.financial-analysis {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  padding: 24px;
  margin-bottom: 24px;
}

.analysis-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.analysis-title {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.analysis-content {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.section-title {
  margin: 0 0 16px 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.comparison-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.comparison-card {
  background: #f8fafc;
  border-radius: 8px;
  padding: 20px;
  border: 1px solid #e8eaed;
}

.comparison-card.planned {
  border-left: 4px solid #409eff;
}

.comparison-card.actual {
  border-left: 4px solid #67c23a;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 16px;
  font-weight: 600;
  color: #333;
}

.card-content {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.metric-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #f0f0f0;
}

.metric-item:last-child {
  border-bottom: none;
}

.metric-label {
  color: #666;
  font-size: 14px;
}

.metric-value {
  font-weight: 600;
  color: #333;
  font-size: 14px;
}

.deviation-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.deviation-card {
  background: #f8fafc;
  border-radius: 8px;
  padding: 20px;
  text-align: center;
  border: 1px solid #e8eaed;
}

.deviation-card.positive {
  border-left: 4px solid #67c23a;
}

.deviation-card.negative {
  border-left: 4px solid #f56c6c;
}

.deviation-card.neutral {
  border-left: 4px solid #909399;
}

.deviation-value {
  font-size: 24px;
  font-weight: bold;
  margin: 12px 0;
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

.deviation-trend {
  display: flex;
  align-items: center;
  justify-content: center;
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

.chart-container {
  height: 400px;
  margin-top: 16px;
}

.chart {
  width: 100%;
  height: 100%;
}

@media (max-width: 768px) {
  .comparison-grid,
  .deviation-grid {
    grid-template-columns: 1fr;
  }

  .analysis-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
}
</style>
