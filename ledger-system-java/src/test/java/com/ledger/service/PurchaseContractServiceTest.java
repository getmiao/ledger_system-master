package com.ledger.service;

import com.ledger.model.entity.PurchaseContract;
import com.ledger.model.vo.PurchaseContractVO;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.annotation.Resource;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 采购合同服务测试类
 * 验证财务字段计算逻辑
 */
@SpringBootTest
@ActiveProfiles("test")
public class PurchaseContractServiceTest {

    @Resource
    private PurchaseContractService purchaseContractService;

    @Test
    public void testFinancialCalculations() {
        // 创建测试数据
        PurchaseContract contract = new PurchaseContract();
        contract.setId(1L);
        contract.setSettlementAmount(new BigDecimal("1000000.00")); // 结算金额100万
        contract.setTaxRate(new BigDecimal("13.00")); // 税率13%
        contract.setActualRevenue(new BigDecimal("900000.00")); // 实际营收90万
        
        // 模拟累计付款金额（实际成本）
        BigDecimal totalPayment = new BigDecimal("800000.00"); // 累计付款80万
        
        // 获取VO对象
        PurchaseContractVO vo = purchaseContractService.getPurchaseContractVO(contract, null);
        
        // 验证计划成本计算
        // 计划成本 = 结算金额 / (1 + 税率/100) = 1000000 / (1 + 13/100) = 884955.75
        BigDecimal expectedPlannedCost = new BigDecimal("884955.75");
        assertEquals(0, vo.getPlannedCost().compareTo(expectedPlannedCost), 
            "计划成本计算错误");
        
        // 验证实际毛利润计算
        // 实际毛利润 = 实际营收 - 实际成本 = 900000 - 800000 = 100000
        BigDecimal expectedActualGrossProfit = new BigDecimal("100000.00");
        assertEquals(0, vo.getActualGrossProfit().compareTo(expectedActualGrossProfit), 
            "实际毛利润计算错误");
        
        // 验证实际毛利率计算
        // 实际毛利率 = 实际毛利润 / 实际成本 = 100000 / 800000 = 0.125 = 12.5%
        BigDecimal expectedActualGrossProfitRate = new BigDecimal("0.125");
        assertEquals(0, vo.getActualGrossProfitRate().compareTo(expectedActualGrossProfitRate), 
            "实际毛利率计算错误");
        
        System.out.println("财务字段计算测试通过！");
        System.out.println("计划成本: " + vo.getPlannedCost());
        System.out.println("实际成本: " + vo.getActualCost());
        System.out.println("实际毛利润: " + vo.getActualGrossProfit());
        System.out.println("实际毛利率: " + vo.getActualGrossProfitRate());
    }
} 