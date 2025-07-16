<template>
  <div class="test-api">
    <h2>API 测试页面</h2>

    <el-card>
      <template #header>
        <span>API 连接测试</span>
      </template>

      <el-button @click="testSalesContracts" :loading="loading.sales"
        >测试销售合同API</el-button
      >
      <el-button @click="testPurchaseContracts" :loading="loading.purchase"
        >测试采购合同API</el-button
      >
      <el-button @click="testSalesPayments" :loading="loading.salesPayments"
        >测试销售收款API</el-button
      >
      <el-button
        @click="testPurchasePayments"
        :loading="loading.purchasePayments"
        >测试采购付款API</el-button
      >

      <div class="test-results">
        <h3>测试结果:</h3>
        <pre>{{ testResults }}</pre>
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

const loading = ref({
  sales: false,
  purchase: false,
  salesPayments: false,
  purchasePayments: false,
});

const testResults = ref("");

async function testSalesContracts() {
  loading.value.sales = true;
  try {
    const data = await getAllSalesContracts();
    testResults.value = `销售合同API测试成功: 获取到${
      data.length
    }条记录\n${JSON.stringify(data.slice(0, 2), null, 2)}`;
  } catch (error) {
    testResults.value = `销售合同API测试失败: ${error}`;
  } finally {
    loading.value.sales = false;
  }
}

async function testPurchaseContracts() {
  loading.value.purchase = true;
  try {
    const data = await getAllPurchaseContracts();
    testResults.value = `采购合同API测试成功: 获取到${
      data.length
    }条记录\n${JSON.stringify(data.slice(0, 2), null, 2)}`;
  } catch (error) {
    testResults.value = `采购合同API测试失败: ${error}`;
  } finally {
    loading.value.purchase = false;
  }
}

async function testSalesPayments() {
  loading.value.salesPayments = true;
  try {
    const data = await getAllSalesPayments();
    testResults.value = `销售收款API测试成功: 获取到${
      data.length
    }条记录\n${JSON.stringify(data.slice(0, 2), null, 2)}`;
  } catch (error) {
    testResults.value = `销售收款API测试失败: ${error}`;
  } finally {
    loading.value.salesPayments = false;
  }
}

async function testPurchasePayments() {
  loading.value.purchasePayments = true;
  try {
    const data = await getAllPurchasePayments();
    testResults.value = `采购付款API测试成功: 获取到${
      data.length
    }条记录\n${JSON.stringify(data.slice(0, 2), null, 2)}`;
  } catch (error) {
    testResults.value = `采购付款API测试失败: ${error}`;
  } finally {
    loading.value.purchasePayments = false;
  }
}
</script>

<style scoped>
.test-api {
  padding: 20px;
}

.test-results {
  margin-top: 20px;
  padding: 10px;
  background: #f5f5f5;
  border-radius: 4px;
}

.test-results pre {
  white-space: pre-wrap;
  word-wrap: break-word;
  max-height: 400px;
  overflow-y: auto;
}
</style>
