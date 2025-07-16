package com.ledger.model.vo;

import com.ledger.model.entity.SalesPayment;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

/**
 * 销售回款视图
 *
 * @author 9K
 */
@Data
public class SalesPaymentVO implements Serializable {
    private Long id;

    /**
     * 销售id
     */
    private Long salesId;

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
     * @param salesPaymentVO
     * @return
     */
    public static SalesPayment voToObj(SalesPaymentVO salesPaymentVO) {
        if (salesPaymentVO == null) {
            return null;
        }
        SalesPayment salesPayment = new SalesPayment();
        BeanUtils.copyProperties(salesPaymentVO, salesPayment);
        return salesPayment;
    }

    /**
     * 对象转封装类
     *
     * @param salesPayment
     * @return
     */
    public static SalesPaymentVO objToVo(SalesPayment salesPayment) {
        if (salesPayment == null) {
            return null;
        }
        SalesPaymentVO salesPaymentVO = new SalesPaymentVO();
        BeanUtils.copyProperties(salesPayment, salesPaymentVO);
        return salesPaymentVO;
    }
}
