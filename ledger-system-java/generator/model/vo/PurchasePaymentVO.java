package com.ledger.model.vo;

import cn.hutool.json.JSONUtil;
import com.ledger.model.entity.PurchasePayment;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 采购回款视图
 *
   * @author 9K
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
@Data
public class PurchasePaymentVO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 标签列表
     */
    private List<String> tagList;

    /**
     * 创建用户信息
     */
    private UserVO user;

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
        List<String> tagList = purchasePaymentVO.getTagList();
        purchasePayment.setTags(JSONUtil.toJsonStr(tagList));
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
        purchasePaymentVO.setTagList(JSONUtil.toList(purchasePayment.getTags(), String.class));
        return purchasePaymentVO;
    }
}
