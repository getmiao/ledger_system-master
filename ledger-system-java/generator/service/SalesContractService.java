package com.ledger.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ledger.model.dto.salesContract.SalesContractQueryRequest;
import com.ledger.model.entity.SalesContract;
import com.ledger.model.vo.SalesContractVO;

import javax.servlet.http.HttpServletRequest;

/**
 * 销售合同服务
 *
   * @author 9K
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
public interface SalesContractService extends IService<SalesContract> {

    /**
     * 校验数据
     *
     * @param salesContract
     * @param add 对创建的数据进行校验
     */
    void validSalesContract(SalesContract salesContract, boolean add);

    /**
     * 获取查询条件
     *
     * @param salesContractQueryRequest
     * @return
     */
    QueryWrapper<SalesContract> getQueryWrapper(SalesContractQueryRequest salesContractQueryRequest);
    
    /**
     * 获取销售合同封装
     *
     * @param salesContract
     * @param request
     * @return
     */
    SalesContractVO getSalesContractVO(SalesContract salesContract, HttpServletRequest request);

    /**
     * 分页获取销售合同封装
     *
     * @param salesContractPage
     * @param request
     * @return
     */
    Page<SalesContractVO> getSalesContractVOPage(Page<SalesContract> salesContractPage, HttpServletRequest request);
}
