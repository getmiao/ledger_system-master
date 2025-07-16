/* eslint-disable */
import request from "@/request";

/** addSalesContract POST /api/salesContract/add */
export async function addSalesContractUsingPost(
  body: API.SalesContractAddRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseLong_>("/salesContract/add", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** deleteSalesContract POST /api/salesContract/delete */
export async function deleteSalesContractUsingPost(
  body: API.DeleteRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean_>("/salesContract/delete", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** getSalesContractVOById GET /api/salesContract/get/vo */
export async function getSalesContractVoByIdUsingGet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getSalesContractVOByIdUsingGETParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseSalesContractVO_>(
    "/salesContract/get/vo",
    {
      method: "GET",
      params: {
        ...params,
      },
      ...(options || {}),
    }
  );
}

/** listSalesContractKeyV POST /api/salesContract/list/keyV */
export async function listSalesContractKeyVUsingPost(
  body: string,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseMapStringLong_>(
    "/salesContract/list/keyV",
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

/** listSalesContractVOByPage POST /api/salesContract/list/page/vo */
export async function listSalesContractVoByPageUsingPost(
  body: API.SalesContractQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageSalesContractVO_>(
    "/salesContract/list/page/vo",
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

/** listMySalesContractVOByPage POST /api/salesContract/my/list/page/vo */
export async function listMySalesContractVoByPageUsingPost(
  body: API.SalesContractQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageSalesContractVO_>(
    "/salesContract/my/list/page/vo",
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

/** updateSalesContract POST /api/salesContract/update */
export async function updateSalesContractUsingPost(
  body: API.SalesContractUpdateRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean_>("/salesContract/update", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}
