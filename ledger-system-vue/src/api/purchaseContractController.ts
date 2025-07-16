/* eslint-disable */
import request from "@/request";

/** addPurchaseContract POST /api/purchaseContract/add */
export async function addPurchaseContractUsingPost(
  body: API.PurchaseContractAddRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseLong_>("/purchaseContract/add", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** deletePurchaseContract POST /api/purchaseContract/delete */
export async function deletePurchaseContractUsingPost(
  body: API.DeleteRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean_>("/purchaseContract/delete", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** editPurchaseContract POST /api/purchaseContract/edit */
export async function editPurchaseContractUsingPost(
  body: API.PurchaseContractEditRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean_>("/purchaseContract/edit", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** getPurchaseContractVOById GET /api/purchaseContract/get/vo */
export async function getPurchaseContractVoByIdUsingGet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getPurchaseContractVOByIdUsingGETParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePurchaseContractVO_>(
    "/purchaseContract/get/vo",
    {
      method: "GET",
      params: {
        ...params,
      },
      ...(options || {}),
    }
  );
}

/** listPurchaseContractKeyV POST /api/purchaseContract/list/keyV */
export async function listPurchaseContractKeyVUsingPost(
  body: string,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseMapStringLong_>(
    "/purchaseContract/list/keyV",
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

/** listPurchaseContractVOByPage POST /api/purchaseContract/list/page/vo */
export async function listPurchaseContractVoByPageUsingPost(
  body: API.PurchaseContractQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePagePurchaseContractVO_>(
    "/purchaseContract/list/page/vo",
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

/** listMyPurchaseContractVOByPage POST /api/purchaseContract/my/list/page/vo */
export async function listMyPurchaseContractVoByPageUsingPost(
  body: API.PurchaseContractQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePagePurchaseContractVO_>(
    "/purchaseContract/my/list/page/vo",
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

/** updatePurchaseContract POST /api/purchaseContract/update */
export async function updatePurchaseContractUsingPost(
  body: API.PurchaseContractUpdateRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean_>("/purchaseContract/update", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}
