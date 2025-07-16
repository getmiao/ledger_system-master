package com.ledger.model.dto.purchase;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 更新采购合同请求
 *
 * @author 9K
 */
@Data
public class PurchaseContractUpdateRequest implements Serializable {

    /**
     * 主键
     */
    private Long id;

    /**
     * 销售合同id
     */
    private Long salesId;

    /**
     * 分包单位
     */
    private String subcontractingUnit;

    /**
     * 项目负责人
     */
    private String director;

    /**
     * 联系方式
     */
    private String phone;

    /**
     * 合同名称
     */
    private String contractName;

    /**
     * 合同金额
     */
    private BigDecimal contractAmount;

    /**
     * 合同结算金额
     */
    private BigDecimal settlementAmount;

    /**
     * 合同签订日期
     */
    private Date contractDate;

    /**
     * 合同工期
     */
    private String contractPeriod;
    /**
     * 已收发票金额
     */
    private BigDecimal receivedInvoiceAmount;

    /**
     * 计划营收
     */
    private BigDecimal plannedRevenue;

    /**
     * 实际营收
     */
    private BigDecimal actualRevenue;

    /**
     * 采购方式
     */
    private String purchaseMethod;

    /**
     * 实际成本
     */
    private BigDecimal actualCost;

    /**
     * 采购招标开始时间
     */
    private Date purchaseStart;

    /**
     * 采购招标结束时间
     */
    private Date purchaseEnd;

    /**
     * 支委会决策时间
     */
    private Date committeeDate;

    private Long createId;

    private static final long serialVersionUID = 1L;
}