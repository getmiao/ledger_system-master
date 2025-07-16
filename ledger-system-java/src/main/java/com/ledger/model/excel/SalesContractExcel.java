package com.ledger.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 销售合同excel类
 *
 * @author 9K
 */
@Data
public class SalesContractExcel implements Serializable {
    /**
     * 主键
     */
    @ExcelProperty("主键")
    private Long id;

    /**
     * 年份
     */
    @ExcelProperty("年份")
    private Integer year;

    /**
     * 0:集团内 1:集团外
     */
    @ExcelProperty("项目来源(0:集团内 1:集团外)")
    private Integer projectSource;

    /**
     * 0:在建 1:完工整改 2:竣工验收 3:竣工结算 4:质保期内 5:完结
     */
    @ExcelProperty("项目状态(0:在建 1:完工整改 2:竣工验收 3:竣工结算 4:质保期内 5:完结)")
    private Integer projectStatus;

    /**
     * 项目负责人
     */
    @ExcelProperty("项目负责人")
    private String projectDirector;

    /**
     * 客户名称
     */
    @ExcelProperty("客户名称")
    private String customer;

    /**
     * 项目名称
     */
    @ExcelProperty("项目名称")
    private String projectName;

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
     * 合同类型 0:建设施工 1:货物采购 2:技术服务
     */
    @ExcelProperty("合同类型(0:建设施工 1:货物采购 2:技术服务)")
    private Integer contractType;

    /**
     * 招标方式 0:公开招标 1:竞争性磋商 2:询价比 3:直接委托 4:其他
     */
    @ExcelProperty("招标方式(0:公开招标 1:竞争性磋商 2:询价比 3:直接委托 4:其他)")
    private String biddingMethod;

    /**
     * 合同结算金额
     */
    @ExcelProperty("合同结算金额")
    private BigDecimal settlementAmount;

    /**
     * 合同税率
     */
    @ExcelProperty("合同税率")
    private BigDecimal taxRate;

    /**
     * 合同签订日期
     */
    @ExcelProperty("合同签订日期")
    @DateTimeFormat("yyyy年MM月dd日")
    private Date contractDate;

    /**
     * 合同工期
     */
    @ExcelProperty("合同工期")
    private String contractPeriod;

    /**
     * 累计回款金额
     */
    @ExcelProperty("累计回款金额")
    private BigDecimal projectPayment;

    /**
     * 累计回款比例
     */
    @ExcelProperty("累计回款比例")
    private BigDecimal projectPaymentRate;

    /**
     * 累计开票金额
     */
    @ExcelProperty("累计开票金额")
    private BigDecimal invoiceAmount;

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