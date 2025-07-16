import request from "@/request";
import { addPurchaseContractUsingPost } from "@/api/purchaseContractController";

export async function getSalesContractById(id: string | number) {
  // 假设已有接口 /salesContract/get/vo?id=xxx
  const res = await request("/salesContract/get/vo", {
    method: "GET",
    params: { id },
  });
  return res.data.data;
}

export async function getPurchaseListBySalesId(sid: string) {
  try {
    // 验证salesId的有效性，但不转换为Number
    if (!sid || sid.trim() === "" || isNaN(Number(sid)) || Number(sid) <= 0) {
      console.warn(`无效的salesId: ${sid}`);
      return [];
    }

    // 使用分页查询API，通过salesId过滤，保持字符串类型
    const res = await request("/purchaseContract/list/page/vo", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      data: {
        salesId: sid, // 保持字符串类型
        current: 1,
        pageSize: 20, // 符合后端限制
      },
    });
    return res?.data?.data?.records || [];
  } catch (error) {
    console.error("获取采购合同列表失败:", error);
    return [];
  }
}

export async function addPurchaseContract(sid: string, data: any) {
  try {
    // 验证salesId的有效性，但不转换为Number
    if (!sid || sid.trim() === "" || isNaN(Number(sid)) || Number(sid) <= 0) {
      console.warn(`无效的salesId: ${sid}`);
      return null;
    }

    const res = await addPurchaseContractUsingPost({
      ...data,
      salesId: sid, // 保持字符串类型
    });
    return res?.data?.data;
  } catch (error) {
    console.error("新增采购合同失败:", error);
    return null;
  }
}
