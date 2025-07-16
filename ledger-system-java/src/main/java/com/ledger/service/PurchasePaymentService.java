package com.ledger.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ledger.model.dto.purchasepayment.PurchasePaymentQueryRequest;
import com.ledger.model.entity.PurchasePayment;
import com.ledger.model.vo.PurchasePaymentVO;

import javax.servlet.http.HttpServletRequest;

/**
 * 采购回款服务
 *
   * @author 9K
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
public interface PurchasePaymentService extends IService<PurchasePayment> {

    /**
     * 校验数据
     *
     * @param purchasePayment
     * @param add 对创建的数据进行校验
     */
    void validPurchasePayment(PurchasePayment purchasePayment, boolean add);

    /**
     * 获取查询条件
     *
     * @param purchasePaymentQueryRequest
     * @return
     */
    QueryWrapper<PurchasePayment> getQueryWrapper(PurchasePaymentQueryRequest purchasePaymentQueryRequest);
    
    /**
     * 获取采购回款封装
     *
     * @param purchasePayment
     * @param request
     * @return
     */
    PurchasePaymentVO getPurchasePaymentVO(PurchasePayment purchasePayment, HttpServletRequest request);

    /**
     * 分页获取采购回款封装
     *
     * @param purchasePaymentPage
     * @param request
     * @return
     */
    Page<PurchasePaymentVO> getPurchasePaymentVOPage(Page<PurchasePayment> purchasePaymentPage, HttpServletRequest request);
}
