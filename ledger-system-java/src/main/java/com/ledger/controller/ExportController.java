package com.ledger.controller;

import com.alibaba.excel.EasyExcel;
import com.ledger.model.entity.PurchaseContract;
import com.ledger.model.entity.PurchasePayment;
import com.ledger.model.entity.SalesContract;
import com.ledger.model.entity.SalesPayment;
import com.ledger.model.excel.PurchaseContractExcel;
import com.ledger.model.excel.PurchasePaymentExcel;
import com.ledger.model.excel.SalesContractExcel;
import com.ledger.model.excel.SalesPaymentExcel;
import com.ledger.model.vo.PurchaseContractVO;
import com.ledger.model.vo.PurchasePaymentVO;
import com.ledger.model.vo.SalesContractVO;
import com.ledger.model.vo.SalesPaymentVO;
import com.ledger.service.PurchaseContractService;
import com.ledger.service.PurchasePaymentService;
import com.ledger.service.SalesContractService;
import com.ledger.service.SalesPaymentService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Description 使用easyExcel进行导出
 * @Author 9K
 * @Date 2025/7/4 23:34
 */

@RestController
@RequestMapping("/export")
public class ExportController {

    private static final Logger log = LoggerFactory.getLogger(ExportController.class);

    @Resource
    private PurchaseContractService purchaseContractService;

    @Resource
    private PurchasePaymentService purchasePaymentService;

    @Resource
    private SalesContractService salesContractService;

    @Resource
    private SalesPaymentService salesPaymentService;

    @PostMapping("/purchase/contract")
    public void exportPurchaseContract(@RequestBody List<String> selectedColumn,
                                       HttpServletResponse response) throws IOException {
        // 这里注意 有同学反应使用swagger 会导致各种问题，请直接用浏览器或者用postman
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName =
                URLEncoder.encode("采购合同列表" + System.currentTimeMillis(), "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        List<PurchaseContract> purchaseContractList = purchaseContractService.list();
        List<PurchaseContractVO> dataList = purchaseContractList.stream().map(purchaseContract ->
                        purchaseContractService.getPurchaseContractVO(purchaseContract, null))
                .collect(Collectors.toList());
        EasyExcel.write(response.getOutputStream(), PurchaseContractExcel.class)
                .includeColumnFieldNames(selectedColumn)
                .sheet("模板").doWrite(dataList);
    }

    @PostMapping("/purchase/payment")
    public void exportPurchasePayment(@RequestBody List<String> selectedColumn,
                                      HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("采购付款列表.xlsx", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName +".xlsx");

        List<PurchasePayment> purchasePaymentList = purchasePaymentService.list();
        List<PurchasePaymentVO> dataList = purchasePaymentList.stream()
                .map(purchasePayment -> purchasePaymentService.getPurchasePaymentVO(purchasePayment, null))
                .collect(Collectors.toList());

        try (OutputStream outputStream = response.getOutputStream()) {
            EasyExcel.write(outputStream, PurchasePaymentExcel.class)
                    .includeColumnFieldNames(selectedColumn)
                    .sheet("模板")
                    .doWrite(dataList);
        }
    }

    @PostMapping("/sales/contract")
    public void exportSalesContract(@RequestBody List<String> selectedColumn,
                                    HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");

        String fileName = URLEncoder.encode("销售合同列表.xlsx", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName +".xlsx");

        if (selectedColumn == null || selectedColumn.isEmpty()) {
            response.reset();
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(500);
            response.getWriter().write("{\"code\":500,\"msg\":\"请选择至少一个字段\"}");
            return;
        }

        List<SalesContract> salesContractList = salesContractService.list();
        List<SalesContractVO> dataList = salesContractList.stream()
                .map(salesContract -> salesContractService.getSalesContractVO(salesContract, null))
                .collect(Collectors.toList());

        // 自动填充Excel对象并映射中文字段
        List<SalesContractExcel> excelList = dataList.stream().map(vo -> {
            SalesContractExcel excel = new SalesContractExcel();
            excel.setId(vo.getId());
            excel.setYear(vo.getYear());
            excel.setProjectSource(vo.getProjectSource());
            excel.setProjectStatus(vo.getProjectStatus());
            excel.setProjectDirector(vo.getProjectDirector());
            excel.setCustomer(vo.getCustomer());
            excel.setProjectName(vo.getProjectName());
            excel.setContractName(vo.getContractName());
            excel.setContractAmount(vo.getContractAmount());
            excel.setContractType(vo.getContractType());
            excel.setBiddingMethod(vo.getBiddingMethod());
            excel.setSettlementAmount(vo.getSettlementAmount());
            excel.setTaxRate(vo.getTaxRate());
            excel.setContractDate(vo.getContractDate());
            excel.setContractPeriod(vo.getContractPeriod());
            excel.setProjectPayment(vo.getProjectPayment());
            excel.setProjectPaymentRate(vo.getProjectPaymentRate());
            excel.setInvoiceAmount(vo.getInvoiceAmount());
            // 映射中文
            excel.setProjectSourceText(SalesContractExcel.mapProjectSource(vo.getProjectSource()));
            excel.setProjectStatusText(SalesContractExcel.mapProjectStatus(vo.getProjectStatus()));
            excel.setContractTypeText(SalesContractExcel.mapContractType(vo.getContractType()));
            excel.setBiddingMethodText(SalesContractExcel.mapBiddingMethod(vo.getBiddingMethod()));
            return excel;
        }).collect(Collectors.toList());

        try (OutputStream outputStream = response.getOutputStream()) {
            EasyExcel.write(outputStream, SalesContractExcel.class)
                    .includeColumnFieldNames(selectedColumn)
                    .sheet("模板")
                    .doWrite(excelList);
        } catch (Exception e) {
            log.error("导出销售合同失败", e);
            response.reset();
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(500);
            response.getWriter().write("{\"code\":500,\"msg\":\"导出失败，请检查字段选择或联系管理员\"}");
        }
    }

    @PostMapping("/sales/payment")
    public void exportSalesPayment(@RequestBody List<String> selectedColumn,
                                   HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");

        String fileName = URLEncoder.encode("销售收款列表.xlsx", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName +".xlsx");

        List<SalesPayment> salesPaymentList = salesPaymentService.list();
        List<SalesPaymentVO> dataList = salesPaymentList.stream()
                .map(salesPayment -> salesPaymentService.getSalesPaymentVO(salesPayment, null))
                .collect(Collectors.toList());

        try (OutputStream outputStream = response.getOutputStream()) {
            EasyExcel.write(outputStream, SalesPaymentExcel.class)
                    .includeColumnFieldNames(selectedColumn)
                    .sheet("模板")
                    .doWrite(dataList);
        }
    }



}
