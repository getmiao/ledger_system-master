package com.ledger.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ledger.common.ErrorCode;
import com.ledger.constant.CommonConstant;
import com.ledger.exception.ThrowUtils;
import com.ledger.mapper.SalesPaymentMapper;
import com.ledger.model.dto.salespayment.SalesPaymentQueryRequest;
import com.ledger.model.entity.SalesContract;
import com.ledger.model.entity.SalesPayment;
import com.ledger.model.vo.SalesPaymentVO;
import com.ledger.service.SalesContractService;
import com.ledger.service.SalesPaymentService;
import com.ledger.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 销售回款服务实现
 *
 * @author 9K
 */
@Service
@Slf4j
public class SalesPaymentServiceImpl extends ServiceImpl<SalesPaymentMapper, SalesPayment> implements SalesPaymentService {

    @Resource
    private SalesContractService salesContractService;

    /**
     * 校验数据
     *
     * @param salesPayment
     * @param add          对创建的数据进行校验
     */
    @Override
    public void validSalesPayment(SalesPayment salesPayment, boolean add) {
        ThrowUtils.throwIf(salesPayment == null, ErrorCode.PARAMS_ERROR);
        Long salesId = salesPayment.getSalesId();
        BigDecimal account = salesPayment.getAccount();
        Date time = salesPayment.getTime();
        // 创建数据时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(ObjectUtils.isEmpty(salesId), ErrorCode.PARAMS_ERROR);
            ThrowUtils.throwIf(ObjectUtils.isEmpty(account), ErrorCode.PARAMS_ERROR);
            ThrowUtils.throwIf(ObjectUtils.isEmpty(time), ErrorCode.PARAMS_ERROR);
        }
    }

    /**
     * 获取查询条件
     *
     * @param salesPaymentQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<SalesPayment> getQueryWrapper(SalesPaymentQueryRequest salesPaymentQueryRequest) {
        QueryWrapper<SalesPayment> queryWrapper = new QueryWrapper<>();
        if (salesPaymentQueryRequest == null) {
            return queryWrapper;
        }
        Long id = salesPaymentQueryRequest.getId();
        BigDecimal account = salesPaymentQueryRequest.getAccount();
        Date time = salesPaymentQueryRequest.getTime();
        Long createId = salesPaymentQueryRequest.getCreateId();
        String sortField = salesPaymentQueryRequest.getSortField();
        String sortOrder = salesPaymentQueryRequest.getSortOrder();

        // 精确查询
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(account), "account", account);
        queryWrapper.eq(ObjectUtils.isNotEmpty(time), "time", time);
        queryWrapper.eq(ObjectUtils.isNotEmpty(createId), "create_id", createId);
        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
        return queryWrapper;
    }

    /**
     * 获取销售回款封装
     *
     * @param salesPayment
     * @param request
     * @return
     */
    @Override
    public SalesPaymentVO getSalesPaymentVO(SalesPayment salesPayment, HttpServletRequest request) {
        // 对象转封装类
        SalesPaymentVO salesPaymentVO = SalesPaymentVO.objToVo(salesPayment);
        SalesContract contract = salesContractService.getOne(new LambdaQueryWrapper<SalesContract>().eq(SalesContract::getId, salesPayment.getSalesId()));
        salesPaymentVO.setContractName(contract.getContractName());
        return salesPaymentVO;
    }

    /**
     * 分页获取销售回款封装
     *
     * @param salesPaymentPage
     * @param request
     * @return
     */
    @Override
    public Page<SalesPaymentVO> getSalesPaymentVOPage(Page<SalesPayment> salesPaymentPage, HttpServletRequest request) {
        List<SalesPayment> salesPaymentList = salesPaymentPage.getRecords();
        Page<SalesPaymentVO> salesPaymentVOPage = new Page<>(salesPaymentPage.getCurrent(), salesPaymentPage.getSize(), salesPaymentPage.getTotal());
        if (CollUtil.isEmpty(salesPaymentList)) {
            return salesPaymentVOPage;
        }
        List<SalesPaymentVO> salesPaymentVOList =
                salesPaymentList.stream()
                        .map(payment -> getSalesPaymentVO(payment, request))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
        // endregion
        salesPaymentVOPage.setRecords(salesPaymentVOList);
        return salesPaymentVOPage;
    }

}
