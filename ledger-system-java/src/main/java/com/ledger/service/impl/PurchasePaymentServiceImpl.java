package com.ledger.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ledger.common.ErrorCode;
import com.ledger.constant.CommonConstant;
import com.ledger.exception.ThrowUtils;
import com.ledger.mapper.PurchasePaymentMapper;
import com.ledger.model.dto.purchasepayment.PurchasePaymentQueryRequest;
import com.ledger.model.entity.PurchaseContract;
import com.ledger.model.entity.PurchasePayment;
import com.ledger.model.vo.PurchasePaymentVO;
import com.ledger.service.PurchaseContractService;
import com.ledger.service.PurchasePaymentService;
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
 * 采购回款服务实现
 *
 * @author 9K
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
@Service
@Slf4j
public class PurchasePaymentServiceImpl
        extends ServiceImpl<PurchasePaymentMapper, PurchasePayment> implements PurchasePaymentService {

    @Resource
    private PurchaseContractService purchaseContractService;

    /**
     * 校验数据
     *
     * @param purchasePayment
     * @param add             对创建的数据进行校验
     */
    @Override
    public void validPurchasePayment(PurchasePayment purchasePayment, boolean add) {
        ThrowUtils.throwIf(purchasePayment == null, ErrorCode.PARAMS_ERROR);
        Long purchaseId = purchasePayment.getPurchaseId();
        BigDecimal account = purchasePayment.getAccount();
        Date time = purchasePayment.getTime();
        // 创建数据时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(ObjectUtils.isEmpty(purchaseId), ErrorCode.PARAMS_ERROR);
            ThrowUtils.throwIf(ObjectUtils.isEmpty(account), ErrorCode.PARAMS_ERROR);
            ThrowUtils.throwIf(ObjectUtils.isEmpty(time), ErrorCode.PARAMS_ERROR);
        }
    }

    /**
     * 获取查询条件
     *
     * @param purchasePaymentQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<PurchasePayment> getQueryWrapper(PurchasePaymentQueryRequest purchasePaymentQueryRequest) {
        QueryWrapper<PurchasePayment> queryWrapper = new QueryWrapper<>();
        if (purchasePaymentQueryRequest == null) {
            return queryWrapper;
        }
        Long id = purchasePaymentQueryRequest.getId();
        BigDecimal account = purchasePaymentQueryRequest.getAccount();
        Date time = purchasePaymentQueryRequest.getTime();
        String sortField = purchasePaymentQueryRequest.getSortField();
        String sortOrder = purchasePaymentQueryRequest.getSortOrder();
        Long createId = purchasePaymentQueryRequest.getCreateId();

        // 精确查询
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(account), "account", account);
        queryWrapper.eq(ObjectUtils.isNotEmpty(time), "time", time);
        queryWrapper.eq(ObjectUtils.isNotEmpty(createId), "create_id", createId);
        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 获取采购回款封装
     *
     * @param purchasePayment
     * @param request
     * @return
     */
    @Override
    public PurchasePaymentVO getPurchasePaymentVO(PurchasePayment purchasePayment, HttpServletRequest request) {
        // 对象转封装类
        PurchasePaymentVO purchasePaymentVO = PurchasePaymentVO.objToVo(purchasePayment);
        PurchaseContract contract = purchaseContractService
                .getOne(
                        new LambdaQueryWrapper<PurchaseContract>()
                                .eq(PurchaseContract::getId, purchasePayment.getPurchaseId())
                );
        purchasePaymentVO.setContractName(
                contract.getContractName()
        );
        return purchasePaymentVO;
    }

    /**
     * 分页获取采购回款封装
     *
     * @param purchasePaymentPage
     * @param request
     * @return
     */
    @Override
    public Page<PurchasePaymentVO> getPurchasePaymentVOPage(Page<PurchasePayment> purchasePaymentPage, HttpServletRequest request) {
        List<PurchasePayment> purchasePaymentList = purchasePaymentPage.getRecords();
        Page<PurchasePaymentVO> purchasePaymentVOPage = new Page<>(purchasePaymentPage.getCurrent(), purchasePaymentPage.getSize(), purchasePaymentPage.getTotal());
        if (CollUtil.isEmpty(purchasePaymentList)) {
            return purchasePaymentVOPage;
        }
        List<PurchasePaymentVO> purchasePaymentVOList =
                purchasePaymentList.stream()
                .map(payment -> getPurchasePaymentVO(payment, request))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        // endregion
        purchasePaymentVOPage.setRecords(purchasePaymentVOList);
        return purchasePaymentVOPage;
    }

}
