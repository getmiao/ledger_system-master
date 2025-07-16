package com.ledger.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 采购回款excel类
 *
 * @author 9K
 */
@Data
public class PurchasePaymentExcel {

    /**
     * 主键
     */
    @ExcelProperty("主键")
    private Long id;


    /**
     * 金额
     */
    @ExcelProperty("金额")
    private BigDecimal account;

    /**
     * 流水日期
     */
    @ExcelProperty("流水日期")
    @DateTimeFormat("yyyy年MM月dd日")
    private Date time;


    /**
     * 合同名称
     */
    @ExcelProperty("合同名称")
    private String contractName;

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