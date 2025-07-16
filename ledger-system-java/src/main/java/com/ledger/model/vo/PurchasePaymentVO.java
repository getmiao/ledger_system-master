package com.ledger.model.vo;

import com.ledger.model.entity.PurchasePayment;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

/**
 * 采购回款视图
 *
   * @author 9K
 */
@Data
public class PurchasePaymentVO implements Serializable {

    /**
     *
     */
    private Long id;

    /**
     * 销售id
     */
    private Long purchaseId;

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

    private String contractName;

    /**
     * 封装类转对象
     *
     * @param purchasePaymentVO
     * @return
     */
    public static PurchasePayment voToObj(PurchasePaymentVO purchasePaymentVO) {
        if (purchasePaymentVO == null) {
            return null;
        }
        PurchasePayment purchasePayment = new PurchasePayment();
        BeanUtils.copyProperties(purchasePaymentVO, purchasePayment);
        return purchasePayment;
    }

    /**
     * 对象转封装类
     *
     * @param purchasePayment
     * @return
     */
    public static PurchasePaymentVO objToVo(PurchasePayment purchasePayment) {
        if (purchasePayment == null) {
            return null;
        }
        PurchasePaymentVO purchasePaymentVO = new PurchasePaymentVO();
        BeanUtils.copyProperties(purchasePayment, purchasePaymentVO);
        return purchasePaymentVO;
    }
}
