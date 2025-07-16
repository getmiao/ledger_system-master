# 合同台账系统 API 集成文档

## 概述

本系统已完全移除静态模拟数据，改为从后端 API 获取真实数据。系统通过分页机制获取所有数据，确保数据完整性和系统性能。

## API 集成架构

### 1. 数据源

系统集成了以下后端 API：

- **销售合同 API**: `/api/salesContract/list/page/vo`
- **采购合同 API**: `/api/purchaseContract/list/page/vo`
- **销售收款 API**: `/api/salesPayment/list/page/vo`
- **采购付款 API**: `/api/purchasePayment/list/page/vo`

### 2. 分页机制

由于后端限制每页最多返回 20 条记录，系统实现了智能分页获取机制：

```typescript
// 分页获取所有数据
export async function getAllSalesContracts() {
  const allContracts: any[] = [];
  let current = 1;
  let hasMore = true;

  while (hasMore) {
    const res = await api.salesContractController.listSalesContractVoByPageUsingPost({
      current,
      pageSize: 20, // 符合后端限制
    });
    
    const records = res?.data?.data?.records || [];
    allContracts.push(...records);
    
    // 检查是否还有更多数据
    const total = res?.data?.data?.total || 0;
    hasMore = allContracts.length < total;
    current++;
  }

  return allContracts;
}
```

### 3. 错误处理

系统实现了完善的错误处理机制：

- **API 调用失败**: 自动记录错误日志，返回空数组
- **数据解析错误**: 提供默认值，确保界面正常显示
- **网络异常**: 显示用户友好的错误提示

## 核心功能模块

### 1. 合同收入分析 (`ContractIncomeAnalysis.vue`)

**功能特性:**
- 按时间维度分析（月度、季度、年度）
- 实时数据图表展示
- 详细数据表格
- 导出功能

**数据处理流程:**
1. 并行获取所有合同和收付款数据
2. 按时间维度分组处理
3. 计算统计指标
4. 生成图表数据

### 2. 财务总览 (`FinanceOverview.vue`)

**功能特性:**
- 关键财务指标展示
- 合同执行情况统计
- 收付款情况对比
- 数据导出功能

**数据来源:**
- 销售合同金额统计
- 采购合同金额统计
- 销售收款统计
- 采购付款统计

## API 数据结构

### 销售合同数据结构
```typescript
interface SalesContract {
  id: number;
  contractName: string;
  contractAmount: number;
  settlementAmount: number;
  contractDate: string;
  projectName: string;
  customer: string;
  // ... 其他字段
}
```

### 采购合同数据结构
```typescript
interface PurchaseContract {
  id: number;
  contractName: string;
  contractAmount: number;
  settlementAmount: number;
  contractDate: string;
  subcontractingUnit: string;
  // ... 其他字段
}
```

### 收付款数据结构
```typescript
interface Payment {
  id: number;
  account: number;
  time: string;
  createTime: string;
  // ... 其他字段
}
```

## 性能优化

### 1. 并行数据获取
```typescript
const [salesContracts, purchaseContracts, salesPayments, purchasePayments] = await Promise.all([
  getAllSalesContracts(),
  getAllPurchaseContracts(),
  getAllSalesPayments(),
  getAllPurchasePayments(),
]);
```

### 2. 数据缓存机制
- 页面级数据缓存
- 避免重复 API 调用
- 提升用户体验

### 3. 错误恢复
- 单个 API 失败不影响其他数据获取
- 提供降级显示方案
- 详细的错误日志记录

## 调试和测试

### 1. 控制台日志
系统提供了详细的控制台日志，便于调试：

```typescript
console.log(`正在获取销售合同第${current}页...`);
console.log(`第${current}页获取到${records.length}条记录`);
console.log(`总共获取到${allContracts.length}条销售合同记录`);
```

### 2. 测试页面
提供了专门的测试页面 (`TestAPI.vue`) 用于验证 API 连接：

- 独立测试每个 API 接口
- 显示详细的测试结果
- 便于问题定位和调试

## 部署注意事项

### 1. 后端服务要求
- 确保后端服务正常运行
- 验证 API 接口可访问性
- 检查数据库连接状态

### 2. 网络配置
- 确保前后端跨域配置正确
- 验证 API 基础路径配置
- 检查网络连接稳定性

### 3. 数据权限
- 确保用户有相应数据访问权限
- 验证数据过滤逻辑正确性
- 检查用户角色配置

## 故障排除

### 常见问题

1. **API 返回 40000 错误**
   - 检查请求参数格式
   - 验证分页参数合法性
   - 确认后端服务状态

2. **数据获取不完整**
   - 检查分页逻辑
   - 验证数据总数计算
   - 确认网络连接稳定性

3. **图表显示异常**
   - 检查数据格式转换
   - 验证图表配置参数
   - 确认数据完整性

### 调试步骤

1. 打开浏览器开发者工具
2. 查看控制台日志输出
3. 检查网络请求状态
4. 验证 API 响应数据格式
5. 确认数据处理逻辑正确性

## 更新日志

### v1.2.0 (最新)
- ✅ 修复集团内/集团外筛选逻辑
- ✅ 实现采购合同与销售合同的关联筛选
- ✅ 添加项目类型筛选功能到合同收入分析页面
- ✅ 创建筛选逻辑测试页面
- ✅ 完善数据关联逻辑

### v1.1.0
- ✅ 修复 API 分页参数问题
- ✅ 实现智能分页数据获取
- ✅ 添加详细错误处理和日志
- ✅ 优化数据获取性能
- ✅ 完善调试和测试功能

### v1.0.0
- ✅ 移除所有静态模拟数据
- ✅ 集成后端 API 接口
- ✅ 实现合同收入分析功能
- ✅ 实现财务总览功能
- ✅ 添加数据导出功能

## 筛选逻辑说明

### 集团内/集团外筛选规则

系统根据销售合同的 `projectSource` 字段进行筛选：

- **集团内**: `projectSource = 0`
- **集团外**: `projectSource = 1`

### 数据关联逻辑

1. **销售合同筛选**: 根据 `projectSource` 字段筛选销售合同
2. **采购合同关联**: 采购合同通过 `salesId` 字段关联到销售合同
3. **收款记录关联**: 销售收款通过 `salesId` 字段关联到销售合同
4. **付款记录关联**: 采购付款通过 `purchaseId` 字段关联到采购合同

### 筛选流程

```typescript
// 集团内筛选示例
if (viewType === 'internal') {
  // 1. 筛选集团内销售合同
  const internalSalesContracts = salesContracts.filter(
    contract => contract.projectSource === 0
  );
  
  // 2. 获取集团内销售合同ID列表
  const internalSalesContractIds = internalSalesContracts.map(
    contract => contract.id
  );
  
  // 3. 筛选关联的采购合同
  const internalPurchaseContracts = purchaseContracts.filter(
    contract => contract.salesId && internalSalesContractIds.includes(contract.salesId)
  );
  
  // 4. 筛选关联的收款记录
  const internalSalesPayments = salesPayments.filter(
    payment => payment.salesId && internalSalesContractIds.includes(payment.salesId)
  );
  
  // 5. 筛选关联的付款记录
  const internalPurchaseContractIds = internalPurchaseContracts.map(
    contract => contract.id
  );
  const internalPurchasePayments = purchasePayments.filter(
    payment => payment.purchaseId && internalPurchaseContractIds.includes(payment.purchaseId)
  );
}
```

### 测试工具

提供了专门的测试页面 (`TestFilter.vue`) 用于验证筛选逻辑：

- 实时显示筛选结果
- 展示数据关联关系
- 验证筛选逻辑正确性

---

**注意**: 本系统现在完全依赖后端 API 数据，确保后端服务正常运行是系统正常工作的前提。 