package com.ledger.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ledger.common.ErrorCode;
import com.ledger.constant.CommonConstant;
import com.ledger.exception.ThrowUtils;
import com.ledger.mapper.SalesContractMapper;
import com.ledger.model.dto.sales.SalesContractQueryRequest;
import com.ledger.model.entity.PurchaseContract;
import com.ledger.model.entity.SalesContract;
import com.ledger.model.vo.SalesContractVO;
import com.ledger.service.PurchaseContractService;
import com.ledger.service.SalesContractService;
import com.ledger.service.SalesPaymentService;
import com.ledger.service.UserService;
import com.ledger.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Lazy;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import com.ledger.model.entity.SalesPayment;

/**
 * 销售合同服务实现
 *
 * @author 9K
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
@Service
@Slf4j
public class SalesContractServiceImpl extends ServiceImpl<SalesContractMapper, SalesContract> implements SalesContractService {

    @Resource
    private UserService userService;

    @Resource
    private PurchaseContractService purchaseContractService;

    @Lazy
    @Resource
    private SalesPaymentService salesPaymentService;

    /**
     * 校验数据
     *
     * @param salesContract
     * @param add           对创建的数据进行校验
     */
    @Override
    public void validSalesContract(SalesContract salesContract, boolean add) {
        ThrowUtils.throwIf(salesContract == null, ErrorCode.PARAMS_ERROR);
        // 从对象中取值
        Object year = salesContract.getYear();
        Integer projectSource = salesContract.getProjectSource();
        Integer projectStatus = salesContract.getProjectStatus();
        String projectDirector = salesContract.getProjectDirector();
        String customer = salesContract.getCustomer();
        String projectName = salesContract.getProjectName();
        String contractName = salesContract.getContractName();
        BigDecimal contractAmount = salesContract.getContractAmount();
        Integer contractType = salesContract.getContractType();
        String biddingMethod = salesContract.getBiddingMethod();
        BigDecimal settlementAmount = salesContract.getSettlementAmount();
        BigDecimal taxRate = salesContract.getTaxRate();
        Date contractDate = salesContract.getContractDate();
        String contractPeriod = salesContract.getContractPeriod();
        Long createId = salesContract.getCreateId();
        // 创建数据时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(ObjectUtils.isEmpty(projectSource), ErrorCode.PARAMS_ERROR);
            ThrowUtils.throwIf(ObjectUtils.isEmpty(projectStatus), ErrorCode.PARAMS_ERROR);
            ThrowUtils.throwIf(StringUtils.isBlank(projectDirector), ErrorCode.PARAMS_ERROR);
            ThrowUtils.throwIf(StringUtils.isBlank(customer), ErrorCode.PARAMS_ERROR);
            ThrowUtils.throwIf(StringUtils.isBlank(contractName), ErrorCode.PARAMS_ERROR);
            ThrowUtils.throwIf(ObjectUtils.isEmpty(contractAmount), ErrorCode.PARAMS_ERROR);
            ThrowUtils.throwIf(ObjectUtils.isEmpty(contractType), ErrorCode.PARAMS_ERROR);
        }
        // 修改数据时，有参数则校验
        if (StringUtils.isNotBlank(projectDirector)) {
            ThrowUtils.throwIf(projectDirector.length() > 80, ErrorCode.PARAMS_ERROR, "标题过长");
        }
    }

    /**
     * 获取查询条件
     *
     * @param salesContractQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<SalesContract> getQueryWrapper(SalesContractQueryRequest salesContractQueryRequest) {
        QueryWrapper<SalesContract> queryWrapper = new QueryWrapper<>();
        if (salesContractQueryRequest == null) {
            return queryWrapper;
        }
        Long id = salesContractQueryRequest.getId();
        Object year = salesContractQueryRequest.getYear();
        Integer projectSource = salesContractQueryRequest.getProjectSource();
        Integer projectStatus = salesContractQueryRequest.getProjectStatus();
        String projectDirector = salesContractQueryRequest.getProjectDirector();
        String customer = salesContractQueryRequest.getCustomer();
        String projectName = salesContractQueryRequest.getProjectName();
        String contractName = salesContractQueryRequest.getContractName();
        BigDecimal contractAmount = salesContractQueryRequest.getContractAmount();
        Integer contractType = salesContractQueryRequest.getContractType();
        String biddingMethod = salesContractQueryRequest.getBiddingMethod();
        BigDecimal settlementAmount = salesContractQueryRequest.getSettlementAmount();
        BigDecimal taxRate = salesContractQueryRequest.getTaxRate();
        Date contractDate = salesContractQueryRequest.getContractDate();
        String contractPeriod = salesContractQueryRequest.getContractPeriod();
        Long createId = salesContractQueryRequest.getCreateId();
        String sortField = salesContractQueryRequest.getSortField();
        String sortOrder = salesContractQueryRequest.getSortOrder();

        // 模糊查询
        queryWrapper.eq(StringUtils.isNotBlank(projectDirector), "project_director", projectDirector);
        queryWrapper.like(StringUtils.isNotBlank(customer), "customer", customer);
        queryWrapper.like(StringUtils.isNotBlank(projectName), "project_name", projectName);
        queryWrapper.like(StringUtils.isNotBlank(contractName), "contract_name", contractName);
        // 精确查询
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(year), "year", year);
        queryWrapper.eq(ObjectUtils.isNotEmpty(projectSource), "project_source", projectSource);
        queryWrapper.eq(ObjectUtils.isNotEmpty(projectStatus), "project_status", projectStatus);
        queryWrapper.eq(ObjectUtils.isNotEmpty(contractAmount), "contract_amount", contractAmount);
        queryWrapper.eq(ObjectUtils.isNotEmpty(contractType), "contract_type", contractType);
        queryWrapper.eq(StringUtils.isNotBlank(biddingMethod), "bidding_method", biddingMethod);
        queryWrapper.eq(ObjectUtils.isNotEmpty(settlementAmount), "settlement_amount", settlementAmount);
        queryWrapper.eq(ObjectUtils.isNotEmpty(taxRate), "tax_rate", taxRate);
        queryWrapper.eq(StringUtils.isNotBlank(contractPeriod), "contract_period", contractPeriod);
        queryWrapper.eq(ObjectUtils.isNotEmpty(createId), "create_id", createId);
        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 获取销售合同封装
     *
     * @param salesContract
     * @param request
     * @return
     */
    @Override
    public SalesContractVO getSalesContractVO(SalesContract salesContract, HttpServletRequest request) {
        // 空检查
        if (salesContract == null) {
            return null;
        }

        // 对象转封装类
        SalesContractVO salesContractVO = SalesContractVO.objToVo(salesContract);
        if (salesContractVO == null) {
            return null;
        }

        // 统计累计回款金额
        List<SalesPayment> paymentList = salesPaymentService.list(
            new LambdaQueryWrapper<SalesPayment>().eq(SalesPayment::getSalesId, salesContract.getId())
        );
        BigDecimal totalPaymentAmount = paymentList.stream()
            .map(payment -> payment.getAccount() != null ? payment.getAccount() : BigDecimal.ZERO)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        // 累计回款金额赋值
        salesContractVO.setProjectPayment(totalPaymentAmount);

        // 计算累计回款比例
        BigDecimal zero = BigDecimal.ZERO;
        BigDecimal hundred = new BigDecimal(100);
        BigDecimal contractAmount = salesContractVO.getContractAmount() != null ? salesContractVO.getContractAmount() : zero;
        BigDecimal settlementAmount = salesContractVO.getSettlementAmount() != null ? salesContractVO.getSettlementAmount() : zero;

        BigDecimal projectPaymentRate = zero;
        if (settlementAmount.compareTo(zero) > 0) {
            projectPaymentRate = totalPaymentAmount
                .divide(settlementAmount, 4, RoundingMode.HALF_UP)
                .multiply(hundred);
        } else if (contractAmount.compareTo(zero) > 0) {
            projectPaymentRate = totalPaymentAmount
                .divide(contractAmount, 4, RoundingMode.HALF_UP)
                .multiply(hundred);
        }
        salesContractVO.setProjectPaymentRate(projectPaymentRate);

        // 查询该销售项目的采购项目合同
        List<PurchaseContract> purchaseContracts = salesContract.getId() != null ?
                purchaseContractService.list(
                        new LambdaQueryWrapper<PurchaseContract>()
                                .eq(PurchaseContract::getSalesId, salesContract.getId())
                ) : Collections.emptyList();

        // 安全计算发票金额
        BigDecimal invoiceAmount = purchaseContracts.stream()
                .map(contract -> contract.getReceivedInvoiceAmount() != null ?
                        contract.getReceivedInvoiceAmount() : zero)
                .reduce(zero, BigDecimal::add);
        salesContractVO.setInvoiceAmount(invoiceAmount);

        //计算计划营收
        // 检查合同金额是否为空
        if (salesContract.getContractAmount() == null) {
            salesContractVO.setPlannedRevenue(BigDecimal.ZERO);
            return salesContractVO;
        }

        // 获取税率，默认为0
        BigDecimal taxRate = salesContract.getTaxRate() != null ?
                salesContract.getTaxRate() :
                BigDecimal.ZERO;

        // 将税率从百分比转换为小数（除以100）
        BigDecimal taxRateDecimal = taxRate.divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);

        // 计算1+税率
        BigDecimal divisor = BigDecimal.ONE.add(taxRateDecimal);

        // 检查除数是否为零（即税率是否为-100%）
        if (divisor.compareTo(BigDecimal.ZERO) == 0) {
            // 处理特殊情况：税率为-100%时的逻辑
            salesContractVO.setPlannedRevenue(salesContract.getContractAmount());
            return salesContractVO;
        }

        // 执行除法运算并设置结果（保留2位小数，四舍五入）
        salesContractVO.setPlannedRevenue(
                salesContract.getContractAmount()
                        .divide(divisor, 2, RoundingMode.HALF_UP)
        );
        return salesContractVO;
    }

    /**
     * 分页获取销售合同封装
     *
     * @param salesContractPage
     * @param request
     * @return
     */
    @Override
    public Page<SalesContractVO> getSalesContractVOPage(Page<SalesContract> salesContractPage, HttpServletRequest request) {
        List<SalesContract> salesContractList = salesContractPage.getRecords();
        Page<SalesContractVO> salesContractVOPage = new Page<>(salesContractPage.getCurrent(), salesContractPage.getSize(), salesContractPage.getTotal());
        if (CollUtil.isEmpty(salesContractList)) {
            return salesContractVOPage;
        }

        // 对象列表 => 封装对象列表
        List<SalesContractVO> salesContractVOList = salesContractList.stream()
                .map(salesContract -> getSalesContractVO(salesContract, request))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        salesContractVOPage.setRecords(salesContractVOList);
        return salesContractVOPage;
    }

}
