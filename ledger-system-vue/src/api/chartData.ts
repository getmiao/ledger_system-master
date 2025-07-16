import request from "@/request";

export async function getSalesContractList(
  params = { current: 1, pageSize: 20 }
) {
  try {
    // 确保pageSize符合后端限制
    const safeParams = {
      ...params,
      current: Number(params.current) || 1,
      pageSize: Math.min(Number(params.pageSize) || 20, 20), // 最大20
    };

    const res = await request("/salesContract/list/page/vo", {
      method: "POST",
      data: safeParams,
    });
    return res.data.data?.records || [];
  } catch (error) {
    console.error("获取销售合同列表失败:", error);
    return [];
  }
}

export async function getPurchaseContractList(
  params = { current: 1, pageSize: 20 }
) {
  try {
    // 确保pageSize符合后端限制
    const safeParams = {
      ...params,
      current: Number(params.current) || 1,
      pageSize: Math.min(Number(params.pageSize) || 20, 20), // 最大20
    };

    const res = await request("/purchaseContract/list/page/vo", {
      method: "POST",
      data: safeParams,
    });
    return res.data.data?.records || [];
  } catch (error) {
    console.error("获取采购合同列表失败:", error);
    return [];
  }
}
