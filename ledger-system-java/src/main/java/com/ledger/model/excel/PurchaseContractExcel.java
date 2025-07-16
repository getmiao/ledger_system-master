package com.ledger.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 采购合同excel类
 *
 * @author 9K
 */
@Data
public class PurchaseContractExcel {

    /**
     * 主键
     */
    @ExcelProperty("主键")
    private Long id;

    /**
     * 分包单位
     */
    @ExcelProperty("分包单位")
    private String subcontractingUnit;

    /**
     * 项目负责人
     */
    @ExcelProperty("项目负责人")
    private String director;

    /**
     * 联系方式
     */
    @ExcelProperty("联系方式")
    private String phone;

    /**
     * 合同名称
     */
    @ExcelProperty("合同名称")
    private String contractName;

    /**
     * 合同金额
     */
    @ExcelProperty("合同金额")
    private BigDecimal contractAmount;

    /**
     * 合同结算金额
     */
    @ExcelProperty("合同结算金额")
    private BigDecimal settlementAmount;

    /**
     * 合同签订日期
     */
    @ExcelProperty("合同签订日期")
    @DateTimeFormat("yyyy年MM月dd日HH时mm分ss秒")
    private Date contractDate;

    /**
     * 合同工期
     */
    @ExcelProperty("合同工期")
    private String contractPeriod;

    /**
     * 已收发票金额
     */
    @ExcelProperty("已收发票金额")
    private BigDecimal receivedInvoiceAmount;

    /**
     * 实际营收
     */
    @ExcelProperty("实际营收")
    private BigDecimal actualRevenue;

    /**
     * 采购方式
     */
    @ExcelProperty("采购方式")
    private String purchaseMethod;

    /**
     * 税率（百分比，如13表示13%）
     */
    @ExcelProperty("税率（百分比，如13表示13%）")
    private BigDecimal taxRate;

    /**
     * 采购招标开始时间
     */
    @ExcelProperty("采购招标开始时间")
    @DateTimeFormat("yyyy年MM月dd日")
    private Date purchaseStart;

    /**
     * 采购招标结束时间
     */
    @ExcelProperty("采购招标结束时间")
    @DateTimeFormat("yyyy年MM月dd日")
    private Date purchaseEnd;

    /**
     * 支委会决策时间
     */
    @ExcelProperty("支委会决策时间")
    @DateTimeFormat("yyyy年MM月dd日")
    private Date committeeDate;

    @ExcelProperty("销售合同名称")
    private String salesName;

    /**
     * 累计付款金额
     */
    @ExcelProperty("累计付款金额")
    private BigDecimal totalPaymentAmount;

    /**
     * 累计开票金额
     */
    @ExcelProperty("累计开票金额")
    private BigDecimal totalInvoiceAmount;

    /**
     * 计划成本
     */
    @ExcelProperty("计划成本")
    private BigDecimal plannedCost;

    /**
     * 计划毛利润
     */
    @ExcelProperty("计划毛利润")
    private BigDecimal plannedGrossProfit;

    /**
     * 实际毛利润
     */
    @ExcelProperty("实际毛利润")
    private BigDecimal actualGrossProfit;

    /**
     * 实际成本
     */
    @ExcelProperty("实际成本")
    private BigDecimal actualCost;

    /**
     * 计划毛利率（百分比）
     */
    @ExcelProperty("计划毛利率（百分比）")
    private BigDecimal plannedGrossProfitRate;

    /**
     * 实际毛利率（百分比）
     */
    @ExcelProperty("实际毛利率（百分比）")
    private BigDecimal actualGrossProfitRate;

    /**
     * 利润偏差金额
     */
    @ExcelProperty("利润偏差金额")
    private BigDecimal profitDeviationAmount;

    /**
     * 偏差率（百分比）
     */
    @ExcelProperty("偏差率（百分比）")
    private BigDecimal deviationRate;

    /**
     * 应付账款
     */
    @ExcelProperty("应付账款")
    private BigDecimal accountsPayable;

    /**
     * 项目来源中文
     */
    @ExcelProperty("项目来源")
    private String projectSourceText;

    /**
     * 项目状态中文
     */
    @ExcelProperty("项目状态")
    private String projectStatusText;

    /**
     * 合同类型中文
     */
    @ExcelProperty("合同类型")
    private String contractTypeText;

    /**
     * 招标方式中文
     */
    @ExcelProperty("招标方式")
    private String biddingMethodText;

    // 静态映射方法
    public static String mapProjectSource(Integer code) {
        if (code == null) return "";
        switch (code) {
            case 0: return "集团内";
            case 1: return "集团外";
            default: return String.valueOf(code);
        }
    }
    public static String mapProjectStatus(Integer code) {
        if (code == null) return "";
        switch (code) {
            case 0: return "在建";
            case 1: return "完工整改";
            case 2: return "竣工验收";
            case 3: return "竣工结算";
            case 4: return "质保期内";
            case 5: return "完结";
            default: return String.valueOf(code);
        }
    }
    public static String mapContractType(Integer code) {
        if (code == null) return "";
        switch (code) {
            case 0: return "建设施工";
            case 1: return "货物采购";
            case 2: return "技术服务";
            default: return String.valueOf(code);
        }
    }
    public static String mapBiddingMethod(String code) {
        if (code == null) return "";
        switch (code) {
            case "0": return "公开招标";
            case "1": return "竞争性磋商";
            case "2": return "询价比";
            case "3": return "直接委托";
            case "4": return "其他";
            default: return code;
        }
    }
}