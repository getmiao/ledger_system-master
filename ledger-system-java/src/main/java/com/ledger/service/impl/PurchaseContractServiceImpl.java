package com.ledger.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ledger.common.ErrorCode;
import com.ledger.constant.CommonConstant;
import com.ledger.exception.ThrowUtils;
import com.ledger.mapper.PurchaseContractMapper;
import com.ledger.model.dto.purchase.PurchaseContractQueryRequest;
import com.ledger.model.entity.PurchaseContract;
import com.ledger.model.entity.PurchasePayment;
import com.ledger.model.entity.SalesContract;
import com.ledger.model.vo.PurchaseContractVO;
import com.ledger.service.PurchaseContractService;
import com.ledger.service.PurchasePaymentService;
import com.ledger.service.SalesContractService;
import com.ledger.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 采购合同服务实现
 *
 * @author 9K
 */
@Service
@Slf4j
public class PurchaseContractServiceImpl extends ServiceImpl<PurchaseContractMapper, PurchaseContract> implements PurchaseContractService {

    @Lazy
    @Resource
    private SalesContractService salesContractService;

    @Lazy
    @Resource
    private PurchasePaymentService purchasePaymentService;

    /**
     * 校验数据
     *
     * @param purchaseContract
     * @param add              对创建的数据进行校验
     */
    @Override
    public void validPurchaseContract(PurchaseContract purchaseContract, boolean add) {
        ThrowUtils.throwIf(purchaseContract == null, ErrorCode.PARAMS_ERROR);
        Long salesId = purchaseContract.getSalesId();
        String subcontractingUnit = purchaseContract.getSubcontractingUnit();
        String director = purchaseContract.getDirector();
        String phone = purchaseContract.getPhone();
        String contractName = purchaseContract.getContractName();
        BigDecimal contractAmount = purchaseContract.getContractAmount();
        BigDecimal settlementAmount = purchaseContract.getSettlementAmount();
        Date contractDate = purchaseContract.getContractDate();
        String contractPeriod = purchaseContract.getContractPeriod();
        BigDecimal receivedInvoiceAmount = purchaseContract.getReceivedInvoiceAmount();
        String purchaseMethod = purchaseContract.getPurchaseMethod();
        Date purchaseStart = purchaseContract.getPurchaseStart();
        Date purchaseEnd = purchaseContract.getPurchaseEnd();
        Date committeeDate = purchaseContract.getCommitteeDate();
        // 创建数据时，参数不能为空
        if (add) {
            // 补充校验规则
            ThrowUtils.throwIf(ObjectUtils.isEmpty(salesId), ErrorCode.PARAMS_ERROR);
            ThrowUtils.throwIf(StringUtils.isBlank(subcontractingUnit), ErrorCode.PARAMS_ERROR);
        }
        // 修改数据时，有参数则校验
        // 补充校验规则
        if (StringUtils.isNotBlank(subcontractingUnit)) {
            ThrowUtils.throwIf(subcontractingUnit.length() > 80, ErrorCode.PARAMS_ERROR, "标题过长");
        }
    }

    /**
     * 获取查询条件
     *
     * @param purchaseContractQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<PurchaseContract> getQueryWrapper(PurchaseContractQueryRequest purchaseContractQueryRequest) {
        QueryWrapper<PurchaseContract> queryWrapper = new QueryWrapper<>();
        if (purchaseContractQueryRequest == null) {
            return queryWrapper;
        }
        Long id = purchaseContractQueryRequest.getId();
        Long salesId = purchaseContractQueryRequest.getSalesId();
        String subcontractingUnit = purchaseContractQueryRequest.getSubcontractingUnit();
        String director = purchaseContractQueryRequest.getDirector();
        String phone = purchaseContractQueryRequest.getPhone();
        String contractName = purchaseContractQueryRequest.getContractName();
        BigDecimal contractAmount = purchaseContractQueryRequest.getContractAmount();
        BigDecimal settlementAmount = purchaseContractQueryRequest.getSettlementAmount();
        Date contractDate = purchaseContractQueryRequest.getContractDate();
        String contractPeriod = purchaseContractQueryRequest.getContractPeriod();
        String purchaseMethod = purchaseContractQueryRequest.getPurchaseMethod();
        Date purchaseStart = purchaseContractQueryRequest.getPurchaseStart();
        Date purchaseEnd = purchaseContractQueryRequest.getPurchaseEnd();
        Date committeeDate = purchaseContractQueryRequest.getCommitteeDate();
        Long createId = purchaseContractQueryRequest.getCreateId();
        String sortField = purchaseContractQueryRequest.getSortField();
        String sortOrder = purchaseContractQueryRequest.getSortOrder();
        // 模糊查询
        queryWrapper.like(StringUtils.isNotBlank(subcontractingUnit), "subcontracting_unit", subcontractingUnit);
        queryWrapper.like(StringUtils.isNotBlank(director), "director", director);
        queryWrapper.like(StringUtils.isNotBlank(contractName), "contract_name", contractName);
        // 精确查询
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(salesId), "sales_id", salesId);
        queryWrapper.eq(StringUtils.isNotBlank(phone), "phone", phone);
        queryWrapper.eq(StringUtils.isNotBlank(contractPeriod), "contract_period", contractPeriod);
        queryWrapper.eq(StringUtils.isNotBlank(purchaseMethod), "purchase_method", purchaseMethod);
        queryWrapper.eq(ObjectUtils.isNotEmpty(createId), "create_id", createId);
        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 获取采购合同封装
     *
     * @param purchaseContract
     * @param request
     * @return
     */
    @Override
    public PurchaseContractVO getPurchaseContractVO(PurchaseContract purchaseContract, HttpServletRequest request) {
        if (purchaseContract == null) {
            return null;
        }

        // 对象转封装类
        PurchaseContractVO purchaseContractVO = PurchaseContractVO.objToVo(purchaseContract);
        if (purchaseContractVO == null) {
            return null;
        }

        // 初始化默认值
        BigDecimal zero = BigDecimal.ZERO;
        BigDecimal one = BigDecimal.ONE;
        BigDecimal hundred = BigDecimal.valueOf(100);

        // 设置销售合同名称
        if (purchaseContract.getSalesId() != null) {
            SalesContract salesContract = salesContractService.getById(purchaseContract.getSalesId());
            if (salesContract != null && salesContract.getContractName() != null) {
                purchaseContractVO.setSalesName(salesContract.getContractName());
            }
        }

        // 查询付款记录
        List<PurchasePayment> purchasePaymentList = purchasePaymentService.list(
                new QueryWrapper<PurchasePayment>()
                        .eq("purchase_id", purchaseContract.getId()));

        // 计算累计付款金额
        BigDecimal invoiceTotal = purchasePaymentList.stream()
                .map(payment -> payment.getAccount() != null ? payment.getAccount() : zero)
                .reduce(zero, BigDecimal::add);
        purchaseContractVO.setTotalPaymentAmount(invoiceTotal);
        purchaseContractVO.setTotalInvoiceAmount(invoiceTotal);

        // 安全计算计划成本
        BigDecimal settlementAmount = purchaseContractVO.getSettlementAmount() != null ?
                purchaseContractVO.getSettlementAmount() : zero;
        BigDecimal taxRate = purchaseContractVO.getTaxRate() != null ?
                purchaseContractVO.getTaxRate() : zero;

        BigDecimal plannedCost = zero;
        if (taxRate.compareTo(zero) != 0) {
            plannedCost = settlementAmount.divide(
                    one.add(taxRate.divide(hundred, 4, RoundingMode.HALF_UP)),
                    RoundingMode.HALF_UP);
        }
        purchaseContractVO.setPlannedCost(plannedCost);

        // 安全计算计划毛利润
        BigDecimal plannedRevenue = purchaseContractVO.getPlannedRevenue() != null ?
                purchaseContractVO.getPlannedRevenue() : zero;
        BigDecimal plannedGrossProfit = plannedRevenue.subtract(plannedCost);
        purchaseContractVO.setPlannedGrossProfit(plannedGrossProfit);

        // 安全计算计划毛利率
        BigDecimal plannedGrossProfitRate = zero;
        if (plannedCost.compareTo(zero) != 0) {
            plannedGrossProfitRate = plannedGrossProfit.divide(plannedCost, 2, RoundingMode.HALF_UP);
        }
        purchaseContractVO.setPlannedGrossProfitRate(plannedGrossProfitRate);

        // 安全计算实际毛利润
        BigDecimal actualRevenue = purchaseContractVO.getActualRevenue() != null ?
                purchaseContractVO.getActualRevenue() : zero;
        
        // 计算实际成本：使用累计付款金额作为实际成本
        BigDecimal actualCost = invoiceTotal; // 实际成本 = 累计付款金额
        
        BigDecimal actualGrossProfit = actualRevenue.subtract(actualCost).setScale(2, RoundingMode.HALF_UP);
        purchaseContractVO.setActualGrossProfit(actualGrossProfit);
        purchaseContractVO.setActualCost(actualCost);

        // 安全计算实际毛利率
        BigDecimal actualGrossProfitRate = zero;
        if (actualCost.compareTo(zero) != 0) {
            actualGrossProfitRate = actualGrossProfit.divide(actualCost, 2, RoundingMode.HALF_UP);
        }
        purchaseContractVO.setActualGrossProfitRate(actualGrossProfitRate);

        // 计算利润偏差金额
        BigDecimal profitDeviationAmount = actualGrossProfit.subtract(plannedGrossProfit);
        purchaseContractVO.setProfitDeviationAmount(profitDeviationAmount);

        // 安全计算利润偏差率
        BigDecimal deviationRate = zero;
        if (plannedGrossProfit.compareTo(zero) != 0) {
            deviationRate = profitDeviationAmount.divide(plannedGrossProfit, 2, RoundingMode.HALF_UP);
        }
        purchaseContractVO.setDeviationRate(deviationRate);

        // 计算应付账款
        BigDecimal contractAmount = purchaseContract.getContractAmount() != null ?
                purchaseContract.getContractAmount() : zero;
        BigDecimal accountsPayable = contractAmount.subtract(invoiceTotal);
        purchaseContractVO.setAccountsPayable(accountsPayable);

        return purchaseContractVO;
    }

    /**
     * 分页获取采购合同封装
     *
     * @param purchaseContractPage
     * @param request
     * @return
     */
    @Override
    public Page<PurchaseContractVO> getPurchaseContractVOPage(Page<PurchaseContract> purchaseContractPage, HttpServletRequest request) {
        List<PurchaseContract> purchaseContractList = purchaseContractPage.getRecords();
        Page<PurchaseContractVO> purchaseContractVOPage = new Page<>(
                purchaseContractPage.getCurrent(),
                purchaseContractPage.getSize(),
                purchaseContractPage.getTotal()
        );
        if (CollUtil.isEmpty(purchaseContractList)) {
            return purchaseContractVOPage;
        }
        // 修复：使用Stream收集转换后的VO对象
        List<PurchaseContractVO> purchaseContractVOList = purchaseContractList.stream()
                .map(contract -> getPurchaseContractVO(contract, request))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        purchaseContractVOPage.setRecords(purchaseContractVOList);
        return purchaseContractVOPage;
    }

}
