package com.ledger.constant;

/**
 * @Description 销售合同常量
 * @Author 9K
 * @Date 2025/6/26 15:29
 */

public interface SalesConstant {

    /**
     * 0:集团内 1:集团外
     */
     Integer WITHIN_GROUP=0;

     Integer OUTSIDE_GROUP=1;

    /**
     * 合同类型 0:建设施工 1:货物采购 2:技术服务
     */
     Integer CONSTRUCTION_CONTRACT=0;
     Integer GOODS_PURCHASE_CONTRACT=1;
     Integer TECHNICAL_SERVICES_CONTRACT=2;
}
