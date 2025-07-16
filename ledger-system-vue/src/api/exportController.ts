/* eslint-disable */
import request from "@/request";

/** exportPurchaseContract POST /api/export/purchase/contract */
export async function exportPurchaseContractUsingPost(
  body: string[],
  options?: { [key: string]: any }
) {
  return request("/export/purchase/contract", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    responseType: "blob",
    ...(options || {}),
  });
}

/** exportPurchasePayment POST /api/export/purchase/payment */
export async function exportPurchasePaymentUsingPost(
  body: string[],
  options?: { [key: string]: any }
) {
  return request<any>("/export/purchase/payment", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    responseType: "blob",
    ...(options || {}),
  });
}

/** exportSalesContract POST /api/export/sales/contract */
export async function exportSalesContractUsingPost(
  body: string[],
  options?: { [key: string]: any }
) {
  return request<any>("/export/sales/contract", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    responseType: "blob",
    ...(options || {}),
  });
}

/** exportSalesPayment POST /api/export/sales/payment */
export async function exportSalesPaymentUsingPost(
  body: string[],
  options?: { [key: string]: any }
) {
  return request<any>("/export/sales/payment", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    responseType: "blob",
    ...(options || {}),
  });
}



export async function getOverviewSummary(viewType = "all") {
  return request(`/overview/summary?viewType=${viewType}`);
}
