package com.ledger.controller;

import com.ledger.common.BaseResponse;
import com.ledger.common.ResultUtils;
import com.ledger.model.entity.User;
import com.ledger.service.OverviewService;
import com.ledger.service.UserService;
import lombok.Data;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/overview")
public class OverviewController {

    @Resource
    private OverviewService overviewService;

    @Resource
    private UserService userService;

    /**
     * 财务总览聚合数据接口
     * @param viewType 视角 all/internal/external
     * @return 汇总数据
     */
    @GetMapping("/summary")
    public BaseResponse<OverviewSummaryVO> getOverviewSummary(@RequestParam(defaultValue = "all") String viewType, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        OverviewSummaryVO vo = overviewService.getOverviewSummary(viewType, loginUser);
        return ResultUtils.success(vo);
    }

    @Data
    public static class OverviewSummaryVO {
        private String totalContractAmount;
        private String totalProjectPayment;
        private String totalPaymentRate;
        private String totalSalesInvoiceAmount;    // 销售开票金额
        private String totalPurchaseInvoiceAmount; // 采购开票金额
        private String totalPurchaseContractAmount;
        private String totalGrossProfit;
        private String totalGrossProfitRate;
        private String totalReceivable;
        private String totalPayable;
    }
} 