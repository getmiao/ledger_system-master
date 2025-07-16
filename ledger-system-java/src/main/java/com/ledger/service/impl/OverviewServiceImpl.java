package com.ledger.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ledger.controller.OverviewController.OverviewSummaryVO;
import com.ledger.model.entity.PurchaseContract;
import com.ledger.model.entity.SalesContract;
import com.ledger.model.entity.SalesPayment;
import com.ledger.model.entity.PurchasePayment;
import com.ledger.service.OverviewService;
import com.ledger.service.PurchaseContractService;
import com.ledger.service.PurchasePaymentService;
import com.ledger.service.SalesContractService;
import com.ledger.service.SalesPaymentService;
import com.ledger.model.entity.User;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

@Service
public class OverviewServiceImpl implements OverviewService {

    @Resource
    private SalesContractService salesContractService;
    @Resource
    private SalesPaymentService salesPaymentService;
    @Resource
    private PurchaseContractService purchaseContractService;
    @Resource
    private PurchasePaymentService purchasePaymentService;

    @Override
    public OverviewSummaryVO getOverviewSummary(String viewType, User loginUser) {
        OverviewSummaryVO vo = new OverviewSummaryVO();
        // 1. 过滤条件
        QueryWrapper<SalesContract> salesWrapper = new QueryWrapper<>();
        if ("internal".equals(viewType)) {
            salesWrapper.eq("project_source", 0);
        } else if ("external".equals(viewType)) {
            salesWrapper.eq("project_source", 1);
        }
        // 数据隔离：普通用户只能统计自己
        if (loginUser != null && "user".equals(loginUser.getUserRole())) {
            salesWrapper.eq("create_id", loginUser.getId());
        }
        // 2. 查询所有销售合同，分组
        List<SalesContract> allSales = salesContractService.list(salesWrapper);
        java.util.Set<Long> internalSalesIds = allSales.stream()
            .filter(s -> s.getProjectSource() != null && s.getProjectSource() == 0)
            .map(SalesContract::getId)
            .collect(java.util.stream.Collectors.toSet());
        java.util.Set<Long> externalSalesIds = allSales.stream()
            .filter(s -> s.getProjectSource() != null && s.getProjectSource() == 1)
            .map(SalesContract::getId)
            .collect(java.util.stream.Collectors.toSet());
        // 3. 采购合同分组
        QueryWrapper<PurchaseContract> purchaseWrapper = new QueryWrapper<>();
        if ("internal".equals(viewType)) {
            if (!internalSalesIds.isEmpty()) {
                purchaseWrapper.in("sales_id", internalSalesIds);
            } else {
                purchaseWrapper.eq("sales_id", -1L); // 保证无结果
            }
        } else if ("external".equals(viewType)) {
            if (!externalSalesIds.isEmpty()) {
                purchaseWrapper.in("sales_id", externalSalesIds);
            } else {
                purchaseWrapper.eq("sales_id", -1L);
            }
        }
        // 数据隔离：普通用户只能统计自己
        if (loginUser != null && "user".equals(loginUser.getUserRole())) {
            purchaseWrapper.eq("create_id", loginUser.getId());
        }
        // 2. 累计合同金额
        List<SalesContract> salesList = allSales;
        BigDecimal totalContractAmount = salesList.stream().map(SalesContract::getContractAmount).filter(a -> a != null).reduce(BigDecimal.ZERO, BigDecimal::add);
        vo.setTotalContractAmount(totalContractAmount.toPlainString());
        // 新增：采购合同列表和累计采购合同金额
        List<PurchaseContract> purchaseList = purchaseContractService.list(purchaseWrapper);
        BigDecimal totalPurchaseContractAmount = purchaseList.stream()
            .map(PurchaseContract::getContractAmount)
            .filter(a -> a != null)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        vo.setTotalPurchaseContractAmount(totalPurchaseContractAmount.toPlainString());
        // 3. 累计项目回款
        // 只统计当前视角下销售合同对应的回款金额
        List<Long> contractIds = salesList.stream().map(SalesContract::getId).collect(java.util.stream.Collectors.toList());
        List<SalesPayment> salesPayments;
        if (!contractIds.isEmpty()) {
            QueryWrapper<SalesPayment> paymentWrapper = new QueryWrapper<SalesPayment>().in("sales_id", contractIds);
            if (loginUser != null && "user".equals(loginUser.getUserRole())) {
                paymentWrapper.eq("create_id", loginUser.getId());
            }
            salesPayments = salesPaymentService.list(paymentWrapper);
        } else {
            salesPayments = java.util.Collections.emptyList();
        }
        BigDecimal totalProjectPayment = salesPayments.stream()
            .map(SalesPayment::getAccount)
            .filter(a -> a != null)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        vo.setTotalProjectPayment(totalProjectPayment.toPlainString());
        // 4. 累计回款比例
        String paymentRate = totalContractAmount.compareTo(BigDecimal.ZERO) > 0 ? totalProjectPayment.multiply(BigDecimal.valueOf(100)).divide(totalContractAmount, 2, BigDecimal.ROUND_HALF_UP) + "%" : "0%";
        vo.setTotalPaymentRate(paymentRate);
        // 5. 累计销售开票金额
        BigDecimal totalSalesInvoiceAmount = BigDecimal.ZERO;
        try {
            totalSalesInvoiceAmount = salesList.stream().map(c -> {
                try {
                    java.lang.reflect.Field f = c.getClass().getDeclaredField("invoiceAmount");
                    f.setAccessible(true);
                    Object v = f.get(c);
                    return v instanceof BigDecimal ? (BigDecimal) v : BigDecimal.ZERO;
                } catch (Exception e) {
                    return BigDecimal.ZERO;
                }
            }).filter(a -> a != null).reduce(BigDecimal.ZERO, BigDecimal::add);
        } catch (Exception ignore) {}
        vo.setTotalSalesInvoiceAmount(totalSalesInvoiceAmount.toPlainString());
        // 6. 累计采购开票金额
        BigDecimal totalPurchaseInvoiceAmount = BigDecimal.ZERO;
        try {
            totalPurchaseInvoiceAmount = purchaseList.stream().map(c -> {
                try {
                    java.lang.reflect.Field f = c.getClass().getDeclaredField("receivedInvoiceAmount");
                    f.setAccessible(true);
                    Object v = f.get(c);
                    return v instanceof BigDecimal ? (BigDecimal) v : BigDecimal.ZERO;
                } catch (Exception e) {
                    return BigDecimal.ZERO;
                }
            }).filter(a -> a != null).reduce(BigDecimal.ZERO, BigDecimal::add);
        } catch (Exception ignore) {}
        vo.setTotalPurchaseInvoiceAmount(totalPurchaseInvoiceAmount.toPlainString());
        // 7. 累计毛利润 = 合同金额 - 采购合同金额
        BigDecimal totalGrossProfit = totalContractAmount.subtract(totalPurchaseContractAmount);
        vo.setTotalGrossProfit(totalGrossProfit.toPlainString());
        // 8. 累计毛利率
        String grossProfitRate = totalContractAmount.compareTo(BigDecimal.ZERO) > 0 ? totalGrossProfit.multiply(BigDecimal.valueOf(100)).divide(totalContractAmount, 2, BigDecimal.ROUND_HALF_UP) + "%" : "0%";
        vo.setTotalGrossProfitRate(grossProfitRate);
        // 9. 累计应收账款 = 合同金额 - 项目回款
        BigDecimal totalReceivable = totalContractAmount.subtract(totalProjectPayment);
        vo.setTotalReceivable(totalReceivable.toPlainString());
        // 10. 累计应付账款 = 采购合同金额 - 累计采购付款
        // 只统计当前视角下采购合同对应的付款金额
        List<Long> purchaseContractIds = purchaseList.stream().map(PurchaseContract::getId).collect(java.util.stream.Collectors.toList());
        List<PurchasePayment> purchasePayments;
        if (!purchaseContractIds.isEmpty()) {
            QueryWrapper<PurchasePayment> purchasePaymentWrapper = new QueryWrapper<PurchasePayment>().in("purchase_id", purchaseContractIds);
            if (loginUser != null && "user".equals(loginUser.getUserRole())) {
                purchasePaymentWrapper.eq("create_id", loginUser.getId());
            }
            purchasePayments = purchasePaymentService.list(purchasePaymentWrapper);
        } else {
            purchasePayments = java.util.Collections.emptyList();
        }
        BigDecimal totalPurchasePayment = purchasePayments.stream()
            .map(PurchasePayment::getAccount)
            .filter(a -> a != null)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalPayable = totalPurchaseContractAmount.subtract(totalPurchasePayment);
        vo.setTotalPayable(totalPayable.toPlainString());
        return vo;
    }
} 