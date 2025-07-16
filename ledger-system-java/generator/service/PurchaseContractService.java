package com.ledger.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ledger.model.dto.purchaseContract.PurchaseContractQueryRequest;
import com.ledger.model.entity.PurchaseContract;
import com.ledger.model.vo.PurchaseContractVO;

import javax.servlet.http.HttpServletRequest;

/**
 * 采购合同服务
 *
   * @author 9K
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
public interface PurchaseContractService extends IService<PurchaseContract> {

    /**
     * 校验数据
     *
     * @param purchaseContract
     * @param add 对创建的数据进行校验
     */
    void validPurchaseContract(PurchaseContract purchaseContract, boolean add);

    /**
     * 获取查询条件
     *
     * @param purchaseContractQueryRequest
     * @return
     */
    QueryWrapper<PurchaseContract> getQueryWrapper(PurchaseContractQueryRequest purchaseContractQueryRequest);
    
    /**
     * 获取采购合同封装
     *
     * @param purchaseContract
     * @param request
     * @return
     */
    PurchaseContractVO getPurchaseContractVO(PurchaseContract purchaseContract, HttpServletRequest request);

    /**
     * 分页获取采购合同封装
     *
     * @param purchaseContractPage
     * @param request
     * @return
     */
    Page<PurchaseContractVO> getPurchaseContractVOPage(Page<PurchaseContract> purchaseContractPage, HttpServletRequest request);
}
