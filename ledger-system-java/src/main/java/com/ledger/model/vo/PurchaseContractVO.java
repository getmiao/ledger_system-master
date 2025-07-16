package com.ledger.model.vo;

import com.ledger.model.entity.PurchaseContract;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

/**
 * 采购合同视图
 *
 * @author 9K
 */
@Data
public class PurchaseContractVO implements Serializable {

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
     * 税率（百分比，如13表示13%）
     */
    private BigDecimal taxRate;

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

    private String salesName;

    // -------------------- 新增财务相关属性 --------------------

    /**
     * 累计付款金额
     */
    private BigDecimal totalPaymentAmount;

    /**
     * 累计开票金额
     */
    private BigDecimal totalInvoiceAmount;

    /**
     * 计划成本
     */
    private BigDecimal plannedCost;

    /**
     * 计划毛利润
     */
    private BigDecimal plannedGrossProfit;

    /**
     * 实际毛利润
     */
    private BigDecimal actualGrossProfit;

    /**
     * 实际成本
     */
    private BigDecimal actualCost;

    /**
     * 计划毛利率（百分比）
     */
    private BigDecimal plannedGrossProfitRate;

    /**
     * 实际毛利率（百分比）
     */
    private BigDecimal actualGrossProfitRate;

    /**
     * 利润偏差金额
     */
    private BigDecimal profitDeviationAmount;

    /**
     * 偏差率（百分比）
     */
    private BigDecimal deviationRate;

    /**
     * 应付账款
     */
    private BigDecimal accountsPayable;

    /**
     * 封装类转对象
     *
     * @param purchaseContractVO
     * @return
     */
    public static PurchaseContract voToObj(PurchaseContractVO purchaseContractVO) {
        if (purchaseContractVO == null) {
            return null;
        }
        PurchaseContract purchaseContract = new PurchaseContract();
        BeanUtils.copyProperties(purchaseContractVO, purchaseContract);
        return purchaseContract;
    }

    /**
     * 对象转封装类
     *
     * @param purchaseContract
     * @return
     */
    public static PurchaseContractVO objToVo(PurchaseContract purchaseContract) {
        if (purchaseContract == null) {
            return null;
        }
        PurchaseContractVO purchaseContractVO = new PurchaseContractVO();
        BeanUtils.copyProperties(purchaseContract, purchaseContractVO);
        return purchaseContractVO;
    }
}