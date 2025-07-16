package com.ledger.model.dto.purchasePayment;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 创建采购回款请求
 *
   * @author 9K
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
@Data
public class PurchasePaymentAddRequest implements Serializable {

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 标签列表
     */
    private List<String> tags;

    private static final long serialVersionUID = 1L;
}