/* eslint-disable */
import request from "@/request";

/** addSalesPayment POST /api/salesPayment/add */
export async function addSalesPaymentUsingPost(
  body: API.SalesPaymentAddRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseLong_>("/salesPayment/add", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** deleteSalesPayment POST /api/salesPayment/delete */
export async function deleteSalesPaymentUsingPost(
  body: API.DeleteRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean_>("/salesPayment/delete", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** getSalesPaymentVOById GET /api/salesPayment/get/vo */
export async function getSalesPaymentVoByIdUsingGet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getSalesPaymentVOByIdUsingGETParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseSalesPaymentVO_>("/salesPayment/get/vo", {
    method: "GET",
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** listSalesPaymentVOByPage POST /api/salesPayment/list/page/vo */
export async function listSalesPaymentVoByPageUsingPost(
  body: API.SalesPaymentQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageSalesPaymentVO_>(
    "/salesPayment/list/page/vo",
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

/** updateSalesPayment POST /api/salesPayment/update */
export async function updateSalesPaymentUsingPost(
  body: API.SalesPaymentUpdateRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean_>("/salesPayment/update", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}
