package com.ledger.model.dto.sales;

import com.ledger.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 查询销售合同请求
 *
   * @author 9K
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SalesContractQueryRequest extends PageRequest implements Serializable {
    /**
     * 主键
     */
    private Long id;

    /**
     * 年份
     */
    private Integer year;

    /**
     * 0:集团内 1:集团外
     */
    private Integer projectSource;

    /**
     * 0:在建 1:完工整改 2:竣工验收 3:竣工结算 4:质保期内 5:完结
     */
    private Integer projectStatus;

    /**
     * 项目负责人
     */
    private String projectDirector;

    /**
     * 客户名称
     */
    private String customer;

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 合同名称
     */
    private String contractName;

    /**
     * 合同金额
     */
    private BigDecimal contractAmount;

    /**
     * 合同类型 0:建设施工 1:货物采购 2:技术服务
     */
    private Integer contractType;

    /**
     * 招标方式 0:公开招标 1:竞争性磋商 2:询价比 3:直接委托 4:其他
     */
    private String biddingMethod;

    /**
     * 合同结算金额
     */
    private BigDecimal settlementAmount;

    /**
     * 合同税率
     */
    private BigDecimal taxRate;

    /**
     * 合同签订日期
     */
    private Date contractDate;

    /**
     * 合同工期
     */
    private String contractPeriod;

    /**
     * 创建人
     */
    private Long createId;

    private static final long serialVersionUID = 1L;
}