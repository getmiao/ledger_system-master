package com.ledger.model.dto.purchasepayment;

import com.ledger.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

/**
 * 查询采购回款请求
 *
   * @author 9K
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PurchasePaymentQueryRequest extends PageRequest implements Serializable {
    private Long id;

    /**
     * 金额
     */
    private BigDecimal account;

    /**
     * 流水日期
     */
    private Date time;

    /**
     * 创建时间
     */
    private Timestamp createTime;

    /**
     * 创建人ID
     */
    private Long createId;

    private static final long serialVersionUID = 1L;
}