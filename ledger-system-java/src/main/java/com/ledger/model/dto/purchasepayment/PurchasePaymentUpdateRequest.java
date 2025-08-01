package com.ledger.model.dto.purchasepayment;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 更新采购回款请求
 *
   * @author 9K
 */
@Data
public class PurchasePaymentUpdateRequest implements Serializable {
    private Long id;

    /**
     * 金额
     */
    private BigDecimal account;

    /**
     * 流水日期
     */
    private Date time;
    private static final long serialVersionUID = 1L;
}