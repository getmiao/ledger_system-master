package com.ledger.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ledger.common.BaseResponse;
import com.ledger.common.DeleteRequest;
import com.ledger.common.ErrorCode;
import com.ledger.common.ResultUtils;
import com.ledger.exception.BusinessException;
import com.ledger.exception.ThrowUtils;
import com.ledger.model.dto.salespayment.SalesPaymentAddRequest;
import com.ledger.model.dto.salespayment.SalesPaymentQueryRequest;
import com.ledger.model.dto.salespayment.SalesPaymentUpdateRequest;
import com.ledger.model.entity.SalesPayment;
import com.ledger.model.vo.SalesPaymentVO;
import com.ledger.service.SalesPaymentService;
import com.ledger.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 销售回款接口
 *
 * @author 9K
 */
@RestController
@RequestMapping("/salesPayment")
@Slf4j
public class SalesPaymentController {

    @Resource
    private SalesPaymentService salesPaymentService;

    @Resource
    private UserService userService;

    // region 增删改查

    /**
     * 创建销售回款
     *
     * @param salesPaymentAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addSalesPayment(@RequestBody SalesPaymentAddRequest salesPaymentAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(salesPaymentAddRequest == null, ErrorCode.PARAMS_ERROR);
        SalesPayment salesPayment = new SalesPayment();
        BeanUtils.copyProperties(salesPaymentAddRequest, salesPayment);
        // 数据校验
        salesPaymentService.validSalesPayment(salesPayment, true);
        // 写入数据库
        boolean result = salesPaymentService.save(salesPayment);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        // 返回新写入的数据 id
        long newSalesPaymentId = salesPayment.getId();
        return ResultUtils.success(newSalesPaymentId);
    }

    /**
     * 删除销售回款
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteSalesPayment(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = deleteRequest.getId();
        // 判断是否存在
        SalesPayment oldSalesPayment = salesPaymentService.getById(id);
        ThrowUtils.throwIf(oldSalesPayment == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = salesPaymentService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 更新销售回款（仅管理员可用）
     *
     * @param salesPaymentUpdateRequest
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateSalesPayment(@RequestBody SalesPaymentUpdateRequest salesPaymentUpdateRequest) {
        if (salesPaymentUpdateRequest == null || salesPaymentUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        SalesPayment salesPayment = new SalesPayment();
        BeanUtils.copyProperties(salesPaymentUpdateRequest, salesPayment);
        // 数据校验
        salesPaymentService.validSalesPayment(salesPayment, false);
        // 判断是否存在
        long id = salesPaymentUpdateRequest.getId();
        SalesPayment oldSalesPayment = salesPaymentService.getById(id);
        ThrowUtils.throwIf(oldSalesPayment == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = salesPaymentService.updateById(salesPayment);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取销售回款（封装类）
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<SalesPaymentVO> getSalesPaymentVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        SalesPayment salesPayment = salesPaymentService.getById(id);
        ThrowUtils.throwIf(salesPayment == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return ResultUtils.success(salesPaymentService.getSalesPaymentVO(salesPayment, request));
    }


    /**
     * 分页获取销售回款列表（封装类）
     *
     * @param salesPaymentQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<SalesPaymentVO>> listSalesPaymentVOByPage(@RequestBody SalesPaymentQueryRequest salesPaymentQueryRequest,
                                                                       HttpServletRequest request) {
        long current = salesPaymentQueryRequest.getCurrent();
        long size = salesPaymentQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 数据权限处理
        com.ledger.model.entity.User loginUser = userService.getLoginUser(request);
        String role = loginUser.getUserRole();
        if ("user".equalsIgnoreCase(role)) {
            salesPaymentQueryRequest.setCreateId(loginUser.getId());
        }
        // 查询数据库
        Page<SalesPayment> salesPaymentPage = salesPaymentService.page(new Page<>(current, size),
                salesPaymentService.getQueryWrapper(salesPaymentQueryRequest));
        // 获取封装类
        return ResultUtils.success(salesPaymentService.getSalesPaymentVOPage(salesPaymentPage, request));
    }

    // endregion

}
