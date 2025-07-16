import request from "@/request";
import api from "@/api";

// 获取销售合同列表
export async function getSalesContracts(params: any = {}) {
  try {
    const res =
      await api.salesContractController.listSalesContractVoByPageUsingPost({
        current: 1,
        pageSize: 20, // 符合后端限制
        ...params,
      });
    return res?.data?.data?.records || [];
  } catch (error) {
    console.error("获取销售合同列表失败:", error);
    return [];
  }
}

// 获取采购合同列表
export async function getPurchaseContracts(params: any = {}) {
  try {
    const res =
      await api.purchaseContractController.listPurchaseContractVoByPageUsingPost(
        {
          current: 1,
          pageSize: 20, // 符合后端限制
          ...params,
        }
      );
    return res?.data?.data?.records || [];
  } catch (error) {
    console.error("获取采购合同列表失败:", error);
    return [];
  }
}

// 获取销售收款列表
export async function getSalesPayments(params: any = {}) {
  try {
    const res =
      await api.salesPaymentController.listSalesPaymentVoByPageUsingPost({
        current: 1,
        pageSize: 20, // 符合后端限制
        ...params,
      });
    return res?.data?.data?.records || [];
  } catch (error) {
    console.error("获取销售收款列表失败:", error);
    return [];
  }
}

// 获取采购付款列表
export async function getPurchasePayments(params: any = {}) {
  try {
    const res =
      await api.purchasePaymentController.listPurchasePaymentVoByPageUsingPost({
        current: 1,
        pageSize: 20, // 符合后端限制
        ...params,
      });
    return res?.data?.data?.records || [];
  } catch (error) {
    console.error("获取采购付款列表失败:", error);
    return [];
  }
}

// 获取所有销售合同数据（分页获取）
export async function getAllSalesContracts() {
  const allContracts: any[] = [];
  let current = 1;
  let hasMore = true;

  while (hasMore) {
    try {
      const params = {
        current: Number(current),
        pageSize: 20,
      };
      const res =
        await api.salesContractController.listSalesContractVoByPageUsingPost(
          params
        );
      const records = res?.data?.data?.records || [];
      allContracts.push(...records);
      const total = res?.data?.data?.total || 0;
      hasMore = allContracts.length < total;
      current++;
    } catch (error) {
      console.error(`获取销售合同第${current}页失败:`, error);
      break;
    }
  }
  return allContracts;
}

// 获取所有采购合同数据（分页获取）
export async function getAllPurchaseContracts() {
  const allContracts: any[] = [];
  let current = 1;
  let hasMore = true;

  while (hasMore) {
    try {
      const params = {
        current: Number(current),
        pageSize: 20,
      };
      const res =
        await api.purchaseContractController.listPurchaseContractVoByPageUsingPost(
          params
        );
      const records = res?.data?.data?.records || [];
      allContracts.push(...records);
      const total = res?.data?.data?.total || 0;
      hasMore = allContracts.length < total;
      current++;
    } catch (error) {
      console.error(`获取采购合同第${current}页失败:`, error);
      break;
    }
  }
  return allContracts;
}

// 获取所有销售收款数据（分页获取）
export async function getAllSalesPayments() {
  const allPayments: any[] = [];
  let current = 1;
  let hasMore = true;

  while (hasMore) {
    try {
      const params = {
        current: Number(current),
        pageSize: 20,
      };
      const res =
        await api.salesPaymentController.listSalesPaymentVoByPageUsingPost(
          params
        );
      const records = res?.data?.data?.records || [];
      allPayments.push(...records);
      const total = res?.data?.data?.total || 0;
      hasMore = allPayments.length < total;
      current++;
    } catch (error) {
      console.error(`获取销售收款第${current}页失败:`, error);
      break;
    }
  }
  return allPayments;
}

// 获取所有采购付款数据（分页获取）
export async function getAllPurchasePayments() {
  const allPayments: any[] = [];
  let current = 1;
  let hasMore = true;

  while (hasMore) {
    try {
      const params = {
        current: Number(current),
        pageSize: 20,
      };
      const res =
        await api.purchasePaymentController.listPurchasePaymentVoByPageUsingPost(
          params
        );
      const records = res?.data?.data?.records || [];
      allPayments.push(...records);
      const total = res?.data?.data?.total || 0;
      hasMore = allPayments.length < total;
      current++;
    } catch (error) {
      console.error(`获取采购付款第${current}页失败:`, error);
      break;
    }
  }
  return allPayments;
}

// 获取合同收入分析数据
export async function getContractIncomeAnalysis(params: {
  viewType?: string;
  timeDimension: string;
  startDate?: string;
  endDate?: string;
  contractType?: string;
}) {
  try {
    console.log("开始获取合同收入分析数据...");

    // 获取所有相关数据
    const [salesContracts, purchaseContracts, salesPayments, purchasePayments] =
      await Promise.all([
        getAllSalesContracts(),
        getAllPurchaseContracts(),
        getAllSalesPayments(),
        getAllPurchasePayments(),
      ]);

    console.log(
      `数据获取完成: 销售合同${salesContracts.length}条, 采购合同${purchaseContracts.length}条, 销售收款${salesPayments.length}条, 采购付款${purchasePayments.length}条`
    );

    // 根据viewType筛选数据
    let filteredSalesContracts = salesContracts;
    let filteredPurchaseContracts = purchaseContracts;
    let filteredSalesPayments = salesPayments;
    let filteredPurchasePayments = purchasePayments;

    if (params.viewType === "internal") {
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
    } else if (params.viewType === "external") {
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
    // params.viewType === 'all' 时使用所有数据

    console.log(
      `筛选结果: 销售合同${filteredSalesContracts.length}条, 采购合同${filteredPurchaseContracts.length}条, 销售收款${filteredSalesPayments.length}条, 采购付款${filteredPurchasePayments.length}条`
    );

    // 根据时间维度处理数据
    const analysisData = processAnalysisData(
      filteredSalesContracts,
      filteredPurchaseContracts,
      filteredSalesPayments,
      filteredPurchasePayments,
      params
    );

    console.log("数据分析处理完成:", analysisData);
    return analysisData;
  } catch (error) {
    console.error("获取合同收入分析数据失败:", error);
    return {
      periods: [],
      data: [],
      statistics: {
        totalIncome: 0,
        totalPayment: 0,
        netIncome: 0,
        contractCount: 0,
        incomeGrowth: 0,
        paymentGrowth: 0,
        netIncomeGrowth: 0,
      },
    };
  }
}

// 处理分析数据
function processAnalysisData(
  salesContracts: any[],
  purchaseContracts: any[],
  salesPayments: any[],
  purchasePayments: any[],
  params: {
    timeDimension: string;
    startDate?: string;
    endDate?: string;
    contractType?: string;
  }
) {
  const { timeDimension, startDate, endDate, contractType } = params;

  // 过滤时间范围
  const filteredSalesPayments = filterByDateRange(
    salesPayments,
    startDate,
    endDate
  );
  const filteredPurchasePayments = filterByDateRange(
    purchasePayments,
    startDate,
    endDate
  );

  // 按时间维度分组
  const groupedData = groupByTimeDimension(
    filteredSalesPayments,
    filteredPurchasePayments,
    timeDimension
  );

  // 生成分析数据
  const periods = Object.keys(groupedData).sort();
  const data = periods.map((period, index) => {
    const periodData = groupedData[period];
    const previousPeriod = index > 0 ? groupedData[periods[index - 1]] : null;

    const salesIncome = periodData.salesIncome || 0;
    const purchasePayment = periodData.purchasePayment || 0;
    const netAmount = salesIncome - purchasePayment;
    const contractCount = periodData.contractCount || 0;

    // 计算增长率
    let growthRate = 0;
    if (previousPeriod) {
      const previousNetAmount =
        (previousPeriod.salesIncome || 0) -
        (previousPeriod.purchasePayment || 0);
      if (previousNetAmount !== 0) {
        growthRate =
          ((netAmount - previousNetAmount) / Math.abs(previousNetAmount)) * 100;
      }
    }

    return {
      period,
      salesIncome,
      purchasePayment,
      netAmount,
      contractCount,
      growthRate,
    };
  });

  // 计算统计信息
  const statistics = calculateStatistics(data);

  return {
    periods,
    data,
    statistics,
  };
}

// 按时间范围过滤数据
function filterByDateRange(data: any[], startDate?: string, endDate?: string) {
  if (!startDate && !endDate) return data;

  return data.filter((item) => {
    const itemDate = new Date(item.time || item.createTime);
    const start = startDate ? new Date(startDate) : null;
    const end = endDate ? new Date(endDate) : null;

    if (start && itemDate < start) return false;
    if (end && itemDate > end) return false;
    return true;
  });
}

// 按时间维度分组
function groupByTimeDimension(
  salesPayments: any[],
  purchasePayments: any[],
  timeDimension: string
) {
  const grouped: Record<string, any> = {};

  // 处理销售收款
  salesPayments.forEach((payment) => {
    const period = getTimePeriod(
      payment.time || payment.createTime,
      timeDimension
    );
    if (!grouped[period]) {
      grouped[period] = {
        salesIncome: 0,
        purchasePayment: 0,
        contractCount: 0,
      };
    }
    grouped[period].salesIncome += Number(payment.account) || 0;
  });

  // 处理采购付款
  purchasePayments.forEach((payment) => {
    const period = getTimePeriod(
      payment.time || payment.createTime,
      timeDimension
    );
    if (!grouped[period]) {
      grouped[period] = {
        salesIncome: 0,
        purchasePayment: 0,
        contractCount: 0,
      };
    }
    grouped[period].purchasePayment += Number(payment.account) || 0;
  });

  return grouped;
}

// 获取时间周期
function getTimePeriod(dateString: string, timeDimension: string) {
  const date = new Date(dateString);
  let quarter;
  switch (timeDimension) {
    case "month":
      return date.toLocaleDateString("zh-CN", {
        year: "numeric",
        month: "2-digit",
      });
    case "quarter":
      quarter = Math.floor((date.getMonth() + 3) / 3);
      return `${date.getFullYear()}Q${quarter}`;
    case "year":
      return date.getFullYear().toString();
    default:
      return date.toLocaleDateString("zh-CN", {
        year: "numeric",
        month: "2-digit",
      });
  }
}

// 计算统计信息
function calculateStatistics(data: any[]) {
  if (data.length === 0) {
    return {
      totalIncome: 0,
      totalPayment: 0,
      netIncome: 0,
      contractCount: 0,
      incomeGrowth: 0,
      paymentGrowth: 0,
      netIncomeGrowth: 0,
    };
  }

  const totalIncome = data.reduce((sum, item) => sum + item.salesIncome, 0);
  const totalPayment = data.reduce(
    (sum, item) => sum + item.purchasePayment,
    0
  );
  const netIncome = totalIncome - totalPayment;
  const contractCount = data.reduce((sum, item) => sum + item.contractCount, 0);

  // 计算增长率
  const latest = data[data.length - 1];
  const previous = data.length > 1 ? data[data.length - 2] : null;

  let incomeGrowth = 0;
  let paymentGrowth = 0;
  let netIncomeGrowth = 0;

  if (previous) {
    incomeGrowth =
      previous.salesIncome !== 0
        ? ((latest.salesIncome - previous.salesIncome) / previous.salesIncome) *
          100
        : 0;
    paymentGrowth =
      previous.purchasePayment !== 0
        ? ((latest.purchasePayment - previous.purchasePayment) /
            previous.purchasePayment) *
          100
        : 0;
    const previousNetAmount = previous.salesIncome - previous.purchasePayment;
    netIncomeGrowth =
      previousNetAmount !== 0
        ? ((latest.netAmount - previousNetAmount) /
            Math.abs(previousNetAmount)) *
          100
        : 0;
  }

  return {
    totalIncome,
    totalPayment,
    netIncome,
    contractCount,
    incomeGrowth,
    paymentGrowth,
    netIncomeGrowth,
  };
}
