package com.ledger.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ledger.model.dto.salespayment.SalesPaymentQueryRequest;
import com.ledger.model.entity.SalesPayment;
import com.ledger.model.vo.SalesPaymentVO;

import javax.servlet.http.HttpServletRequest;

/**
 * 销售回款服务
 *
   * @author 9K
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
public interface SalesPaymentService extends IService<SalesPayment> {

    /**
     * 校验数据
     *
     * @param salesPayment
     * @param add 对创建的数据进行校验
     */
    void validSalesPayment(SalesPayment salesPayment, boolean add);

    /**
     * 获取查询条件
     *
     * @param salesPaymentQueryRequest
     * @return
     */
    QueryWrapper<SalesPayment> getQueryWrapper(SalesPaymentQueryRequest salesPaymentQueryRequest);
    
    /**
     * 获取销售回款封装
     *
     * @param salesPayment
     * @param request
     * @return
     */
    SalesPaymentVO getSalesPaymentVO(SalesPayment salesPayment, HttpServletRequest request);

    /**
     * 分页获取销售回款封装
     *
     * @param salesPaymentPage
     * @param request
     * @return
     */
    Page<SalesPaymentVO> getSalesPaymentVOPage(Page<SalesPayment> salesPaymentPage, HttpServletRequest request);
}
