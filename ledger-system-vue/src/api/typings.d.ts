declare namespace API {
  type BaseResponseBoolean_ = {
    code?: number;
    data?: boolean;
    message?: string;
  };

  type BaseResponseLoginUserVO_ = {
    code?: number;
    data?: LoginUserVO;
    message?: string;
  };

  type BaseResponseLong_ = {
    code?: number;
    data?: number;
    message?: string;
  };

  type BaseResponseMapStringLong_ = {
    code?: number;
    data?: Record<string, any>;
    message?: string;
  };

  type BaseResponsePagePurchaseContractVO_ = {
    code?: number;
    data?: PagePurchaseContractVO_;
    message?: string;
  };

  type BaseResponsePagePurchasePaymentVO_ = {
    code?: number;
    data?: PagePurchasePaymentVO_;
    message?: string;
  };

  type BaseResponsePageSalesContractVO_ = {
    code?: number;
    data?: PageSalesContractVO_;
    message?: string;
  };

  type BaseResponsePageSalesPaymentVO_ = {
    code?: number;
    data?: PageSalesPaymentVO_;
    message?: string;
  };

  type BaseResponsePageUser_ = {
    code?: number;
    data?: PageUser_;
    message?: string;
  };

  type BaseResponsePurchaseContractVO_ = {
    code?: number;
    data?: PurchaseContractVO;
    message?: string;
  };

  type BaseResponsePurchasePaymentVO_ = {
    code?: number;
    data?: PurchasePaymentVO;
    message?: string;
  };

  type BaseResponseSalesContractVO_ = {
    code?: number;
    data?: SalesContractVO;
    message?: string;
  };

  type BaseResponseSalesPaymentVO_ = {
    code?: number;
    data?: SalesPaymentVO;
    message?: string;
  };

  type BaseResponseString_ = {
    code?: number;
    data?: string;
    message?: string;
  };

  type BaseResponseUser_ = {
    code?: number;
    data?: User;
    message?: string;
  };

  type DeleteRequest = {
    id?: number;
  };

  type getPurchaseContractVOByIdUsingGETParams = {
    /** id */
    id?: number;
  };

  type getPurchasePaymentVOByIdUsingGETParams = {
    /** id */
    id?: number;
  };

  type getSalesContractVOByIdUsingGETParams = {
    /** id */
    id?: number;
  };

  type getSalesPaymentVOByIdUsingGETParams = {
    /** id */
    id?: number;
  };

  type getUserByIdUsingGETParams = {
    /** id */
    id?: number;
  };

  type LoginUserVO = {
    createTime?: string;
    department?: string;
    id?: number;
    name?: string;
    phone?: string;
    updateTime?: string;
    userRole?: string;
  };

  type MapStringLong_ = true;

  type OrderItem = {
    asc?: boolean;
    column?: string;
  };

  type PagePurchaseContractVO_ = {
    countId?: string;
    current?: number;
    maxLimit?: number;
    optimizeCountSql?: boolean;
    orders?: OrderItem[];
    pages?: number;
    records?: PurchaseContractVO[];
    searchCount?: boolean;
    size?: number;
    total?: number;
  };

  type PagePurchasePaymentVO_ = {
    countId?: string;
    current?: number;
    maxLimit?: number;
    optimizeCountSql?: boolean;
    orders?: OrderItem[];
    pages?: number;
    records?: PurchasePaymentVO[];
    searchCount?: boolean;
    size?: number;
    total?: number;
  };

  type PageSalesContractVO_ = {
    countId?: string;
    current?: number;
    maxLimit?: number;
    optimizeCountSql?: boolean;
    orders?: OrderItem[];
    pages?: number;
    records?: SalesContractVO[];
    searchCount?: boolean;
    size?: number;
    total?: number;
  };

  type PageSalesPaymentVO_ = {
    countId?: string;
    current?: number;
    maxLimit?: number;
    optimizeCountSql?: boolean;
    orders?: OrderItem[];
    pages?: number;
    records?: SalesPaymentVO[];
    searchCount?: boolean;
    size?: number;
    total?: number;
  };

  type PageUser_ = {
    countId?: string;
    current?: number;
    maxLimit?: number;
    optimizeCountSql?: boolean;
    orders?: OrderItem[];
    pages?: number;
    records?: User[];
    searchCount?: boolean;
    size?: number;
    total?: number;
  };

  type PurchaseContractAddRequest = {
    actualRevenue?: number;
    committeeDate?: string;
    contractAmount?: number;
    contractDate?: string;
    contractName?: string;
    contractPeriod?: string;
    director?: string;
    phone?: string;
    plannedRevenue?: number;
    purchaseEnd?: string;
    purchaseMethod?: string;
    purchaseStart?: string;
    receivedInvoiceAmount?: number;
    salesId?: number;
    settlementAmount?: number;
    subcontractingUnit?: string;
  };

  type PurchaseContractEditRequest = {
    actualRevenue?: number;
    committeeDate?: string;
    contractAmount?: number;
    contractDate?: string;
    contractName?: string;
    contractPeriod?: string;
    director?: string;
    id?: number;
    phone?: string;
    plannedRevenue?: number;
    purchaseEnd?: string;
    purchaseMethod?: string;
    purchaseStart?: string;
    receivedInvoiceAmount?: number;
    salesId?: number;
    settlementAmount?: number;
    subcontractingUnit?: string;
  };

  type PurchaseContractQueryRequest = {
    actualRevenue?: number;
    committeeDate?: string;
    contractAmount?: number;
    contractDate?: string;
    contractName?: string;
    contractPeriod?: string;
    createId?: number;
    current?: number;
    director?: string;
    id?: number;
    pageSize?: number;
    phone?: string;
    plannedRevenue?: number;
    purchaseEnd?: string;
    purchaseMethod?: string;
    purchaseStart?: string;
    receivedInvoiceAmount?: number;
    salesId?: number;
    settlementAmount?: number;
    sortField?: string;
    sortOrder?: string;
    subcontractingUnit?: string;
  };

  type PurchaseContractUpdateRequest = {
    actualRevenue?: number;
    committeeDate?: string;
    contractAmount?: number;
    contractDate?: string;
    contractName?: string;
    contractPeriod?: string;
    createId?: number;
    director?: string;
    id?: number;
    phone?: string;
    plannedRevenue?: number;
    purchaseEnd?: string;
    purchaseMethod?: string;
    purchaseStart?: string;
    receivedInvoiceAmount?: number;
    salesId?: number;
    settlementAmount?: number;
    subcontractingUnit?: string;
  };

  type PurchaseContractVO = {
    accountsPayable?: number;
    actualGrossProfit?: number;
    actualGrossProfitRate?: number;
    actualRevenue?: number;
    committeeDate?: string;
    contractAmount?: number;
    contractDate?: string;
    contractName?: string;
    contractPeriod?: string;
    createId?: number;
    createTime?: Timestamp;
    deviationRate?: number;
    director?: string;
    id?: number;
    phone?: string;
    plannedCost?: number;
    plannedGrossProfit?: number;
    plannedGrossProfitRate?: number;
    plannedRevenue?: number;
    profitDeviationAmount?: number;
    purchaseEnd?: string;
    purchaseMethod?: string;
    purchaseStart?: string;
    receivedInvoiceAmount?: number;
    salesId?: number;
    salesName?: string;
    settlementAmount?: number;
    subcontractingUnit?: string;
    taxRate?: number;
    totalInvoiceAmount?: number;
    totalPaymentAmount?: number;
    updateTime?: Timestamp;
  };

  type PurchasePaymentAddRequest = {
    account?: number;
    purchaseId?: number;
    time?: string;
  };

  type PurchasePaymentQueryRequest = {
    account?: number;
    createTime?: Timestamp1;
    current?: number;
    id?: number;
    pageSize?: number;
    sortField?: string;
    sortOrder?: string;
    time?: string;
  };

  type PurchasePaymentUpdateRequest = {
    account?: number;
    id?: number;
    time?: string;
  };

  type PurchasePaymentVO = {
    account?: number;
    contractName?: string;
    createTime?: Timestamp;
    id?: number;
    purchaseId?: number;
    time?: string;
  };

  type SalesContractAddRequest = {
    biddingMethod?: string;
    contractAmount?: number;
    contractDate?: string;
    contractName?: string;
    contractPeriod?: string;
    contractType?: number;
    customer?: string;
    projectDirector?: string;
    projectName?: string;
    projectSource?: number;
    projectStatus?: number;
    settlementAmount?: number;
    taxRate?: number;
    year?: number;
  };

  type SalesContractQueryRequest = {
    biddingMethod?: string;
    contractAmount?: number;
    contractDate?: string;
    contractName?: string;
    contractPeriod?: string;
    contractType?: number;
    createId?: number;
    current?: number;
    customer?: string;
    id?: number;
    pageSize?: number;
    projectDirector?: string;
    projectName?: string;
    projectSource?: number;
    projectStatus?: number;
    settlementAmount?: number;
    sortField?: string;
    sortOrder?: string;
    taxRate?: number;
    year?: number;
  };

  type SalesContractUpdateRequest = {
    biddingMethod?: string;
    contractAmount?: number;
    contractDate?: string;
    contractName?: string;
    contractPeriod?: string;
    contractType?: number;
    createId?: number;
    customer?: string;
    id?: number;
    projectDirector?: string;
    projectName?: string;
    projectSource?: number;
    projectStatus?: number;
    settlementAmount?: number;
    taxRate?: number;
    year?: number;
  };

  type SalesContractVO = {
    biddingMethod?: string;
    contractAmount?: number;
    contractDate?: string;
    contractName?: string;
    contractPeriod?: string;
    contractType?: number;
    createId?: number;
    createTime?: Timestamp;
    customer?: string;
    id?: number;
    invoiceAmount?: number;
    projectDirector?: string;
    projectName?: string;
    projectPayment?: number;
    projectPaymentRate?: number;
    projectSource?: number;
    projectStatus?: number;
    settlementAmount?: number;
    taxRate?: number;
    updateTime?: Timestamp;
    year?: number;
  };

  type SalesPaymentAddRequest = {
    account?: number;
    salesId?: number;
    time?: string;
  };

  type SalesPaymentQueryRequest = {
    account?: number;
    createTime?: Timestamp1;
    current?: number;
    id?: number;
    pageSize?: number;
    sortField?: string;
    sortOrder?: string;
    time?: string;
  };

  type SalesPaymentUpdateRequest = {
    account?: number;
    id?: number;
    time?: string;
  };

  type SalesPaymentVO = {
    account?: number;
    contractName?: string;
    createTime?: Timestamp;
    id?: number;
    salesId?: number;
    time?: string;
  };

  type Timestamp = {
    date?: number;
    day?: number;
    hours?: number;
    minutes?: number;
    month?: number;
    nanos?: number;
    seconds?: number;
    time?: number;
    timezoneOffset?: number;
    year?: number;
  };

  type Timestamp1 = {
    date?: number;
    hours?: number;
    minutes?: number;
    month?: number;
    nanos?: number;
    seconds?: number;
    time?: number;
    year?: number;
  };

  type uploadFileUsingPOSTParams = {
    biz?: string;
  };

  type User = {
    createTime?: string;
    department?: string;
    id?: number;
    isDelete?: number;
    name?: string;
    phone?: string;
    updateTime?: string;
    userAccount?: string;
    userPassword?: string;
    userRole?: string;
  };

  type UserAddRequest = {
    department?: string;
    name?: string;
    phone?: string;
    userAccount?: string;
    userRole?: string;
  };

  type UserLoginRequest = {
    userAccount?: string;
    userPassword?: string;
  };

  type UserQueryRequest = {
    current?: number;
    department?: string;
    id?: number;
    name?: string;
    pageSize?: number;
    phone?: string;
    sortField?: string;
    sortOrder?: string;
    userRole?: string;
  };

  type UserUpdateRequest = {
    department?: string;
    id?: number;
    name?: string;
    phone?: string;
    userRole?: string;
  };
}
