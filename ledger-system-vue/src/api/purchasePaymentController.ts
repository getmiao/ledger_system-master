/* eslint-disable */
import request from "@/request";

/** addPurchasePayment POST /api/purchasePayment/add */
export async function addPurchasePaymentUsingPost(
  body: API.PurchasePaymentAddRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseLong_>("/purchasePayment/add", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** deletePurchasePayment POST /api/purchasePayment/delete */
export async function deletePurchasePaymentUsingPost(
  body: API.DeleteRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean_>("/purchasePayment/delete", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** getPurchasePaymentVOById GET /api/purchasePayment/get/vo */
export async function getPurchasePaymentVoByIdUsingGet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getPurchasePaymentVOByIdUsingGETParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePurchasePaymentVO_>(
    "/purchasePayment/get/vo",
    {
      method: "GET",
      params: {
        ...params,
      },
      ...(options || {}),
    }
  );
}

/** listPurchasePaymentVOByPage POST /api/purchasePayment/list/page/vo */
export async function listPurchasePaymentVoByPageUsingPost(
  body: API.PurchasePaymentQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePagePurchasePaymentVO_>(
    "/purchasePayment/list/page/vo",
    {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      data: body,
      ...(options || {}),
    }
  );
}

/** updatePurchasePayment POST /api/purchasePayment/update */
export async function updatePurchasePaymentUsingPost(
  body: API.PurchasePaymentUpdateRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean_>("/purchasePayment/update", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}
