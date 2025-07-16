<template>
  <div class="test-filter">
    <h2>筛选逻辑测试页面</h2>

    <el-card>
      <template #header>
        <span>数据筛选测试</span>
      </template>

      <div class="filter-controls">
        <el-radio-group v-model="viewType" @change="testFilter">
          <el-radio-button label="all">全部</el-radio-button>
          <el-radio-button label="internal">集团内</el-radio-button>
          <el-radio-button label="external">集团外</el-radio-button>
        </el-radio-group>
      </div>

      <div class="test-results">
        <h3>筛选结果:</h3>
        <div class="result-section">
          <h4>销售合同 (projectSource字段)</h4>
          <el-table :data="salesContracts" style="width: 100%" border>
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="contractName" label="合同名称" />
            <el-table-column prop="projectSource" label="项目来源" width="100">
              <template #default="scope">
                <el-tag
                  :type="scope.row.projectSource === 0 ? 'success' : 'warning'"
                >
                  {{ scope.row.projectSource === 0 ? "集团内" : "集团外" }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column
              prop="contractAmount"
              label="合同金额"
              width="120"
            />
          </el-table>
        </div>

        <div class="result-section">
          <h4>采购合同 (绑定到销售合同)</h4>
          <el-table :data="purchaseContracts" style="width: 100%" border>
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="contractName" label="合同名称" />
            <el-table-column
              prop="salesId"
              label="关联销售合同ID"
              width="120"
            />
            <el-table-column
              prop="contractAmount"
              label="合同金额"
              width="120"
            />
          </el-table>
        </div>

        <div class="result-section">
          <h4>销售收款</h4>
          <el-table :data="salesPayments" style="width: 100%" border>
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="salesId" label="销售合同ID" width="120" />
            <el-table-column prop="account" label="收款金额" width="120" />
            <el-table-column prop="time" label="收款时间" width="120" />
          </el-table>
        </div>

        <div class="result-section">
          <h4>采购付款</h4>
          <el-table :data="purchasePayments" style="width: 100%" border>
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="purchaseId" label="采购合同ID" width="120" />
            <el-table-column prop="account" label="付款金额" width="120" />
            <el-table-column prop="time" label="付款时间" width="120" />
          </el-table>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import {
  getAllSalesContracts,
  getAllPurchaseContracts,
  getAllSalesPayments,
  getAllPurchasePayments,
} from "@/api/contractAnalysis";

const viewType = ref("all");
const salesContracts = ref<any[]>([]);
const purchaseContracts = ref<any[]>([]);
const salesPayments = ref<any[]>([]);
const purchasePayments = ref<any[]>([]);

async function testFilter() {
  try {
    // 获取所有数据
    const [
      allSalesContracts,
      allPurchaseContracts,
      allSalesPayments,
      allPurchasePayments,
    ] = await Promise.all([
      getAllSalesContracts(),
      getAllPurchaseContracts(),
      getAllSalesPayments(),
      getAllPurchasePayments(),
    ]);

    // 根据viewType筛选数据
    if (viewType.value === "internal") {
      // 筛选集团内的销售合同 (projectSource = 0)
      salesContracts.value = allSalesContracts.filter(
        (contract) => contract.projectSource === 0
      );

      // 获取集团内销售合同的ID列表
      const internalSalesContractIds = salesContracts.value.map(
        (contract) => contract.id
      );

      // 筛选绑定到集团内销售合同的采购合同
      purchaseContracts.value = allPurchaseContracts.filter(
        (contract) =>
          contract.salesId &&
          internalSalesContractIds.includes(contract.salesId)
      );

      // 筛选集团内销售合同的收款记录
      salesPayments.value = allSalesPayments.filter(
        (payment) =>
          payment.salesId && internalSalesContractIds.includes(payment.salesId)
      );

      // 筛选集团内采购合同的付款记录
      const internalPurchaseContractIds = purchaseContracts.value.map(
        (contract) => contract.id
      );
      purchasePayments.value = allPurchasePayments.filter(
        (payment) =>
          payment.purchaseId &&
          internalPurchaseContractIds.includes(payment.purchaseId)
      );
    } else if (viewType.value === "external") {
      // 筛选集团外的销售合同 (projectSource = 1)
      salesContracts.value = allSalesContracts.filter(
        (contract) => contract.projectSource === 1
      );

      // 获取集团外销售合同的ID列表
      const externalSalesContractIds = salesContracts.value.map(
        (contract) => contract.id
      );

      // 筛选绑定到集团外销售合同的采购合同
      purchaseContracts.value = allPurchaseContracts.filter(
        (contract) =>
          contract.salesId &&
          externalSalesContractIds.includes(contract.salesId)
      );

      // 筛选集团外销售合同的收款记录
      salesPayments.value = allSalesPayments.filter(
        (payment) =>
          payment.salesId && externalSalesContractIds.includes(payment.salesId)
      );

      // 筛选集团外采购合同的付款记录
      const externalPurchaseContractIds = purchaseContracts.value.map(
        (contract) => contract.id
      );
      purchasePayments.value = allPurchasePayments.filter(
        (payment) =>
          payment.purchaseId &&
          externalPurchaseContractIds.includes(payment.purchaseId)
      );
    } else {
      // 全部数据
      salesContracts.value = allSalesContracts;
      purchaseContracts.value = allPurchaseContracts;
      salesPayments.value = allSalesPayments;
      purchasePayments.value = allPurchasePayments;
    }

    console.log(
      `筛选结果: 销售合同${salesContracts.value.length}条, 采购合同${purchaseContracts.value.length}条, 销售收款${salesPayments.value.length}条, 采购付款${purchasePayments.value.length}条`
    );
  } catch (error) {
    console.error("筛选测试失败:", error);
  }
}

// 页面加载时执行测试
testFilter();
</script>

<style scoped>
.test-filter {
  padding: 20px;
}

.filter-controls {
  margin-bottom: 20px;
}

.result-section {
  margin-bottom: 30px;
}

.result-section h4 {
  margin-bottom: 10px;
  color: #333;
}
</style>
