package com.ledger.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ledger.annotation.AuthCheck;
import com.ledger.common.BaseResponse;
import com.ledger.common.DeleteRequest;
import com.ledger.common.ErrorCode;
import com.ledger.common.ResultUtils;
import com.ledger.constant.UserConstant;
import com.ledger.exception.BusinessException;
import com.ledger.exception.ThrowUtils;
import com.ledger.model.dto.purchasepayment.PurchasePaymentAddRequest;
import com.ledger.model.dto.purchasepayment.PurchasePaymentQueryRequest;
import com.ledger.model.dto.purchasepayment.PurchasePaymentUpdateRequest;
import com.ledger.model.entity.PurchasePayment;
import com.ledger.model.vo.PurchasePaymentVO;
import com.ledger.service.PurchasePaymentService;
import com.ledger.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 采购回款接口
 *
   * @author 9K
 */
@RestController
@RequestMapping("/purchasePayment")
@Slf4j
public class PurchasePaymentController {

    @Resource
    private PurchasePaymentService purchasePaymentService;

    @Resource
    private UserService userService;

    // region 增删改查

    /**
     * 创建采购回款
     *
     * @param purchasePaymentAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addPurchasePayment(@RequestBody PurchasePaymentAddRequest purchasePaymentAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(purchasePaymentAddRequest == null, ErrorCode.PARAMS_ERROR);
        // 在此处将实体类和 DTO 进行转换
        PurchasePayment purchasePayment = new PurchasePayment();
        BeanUtils.copyProperties(purchasePaymentAddRequest, purchasePayment);
        // 数据校验
        purchasePaymentService.validPurchasePayment(purchasePayment, true);
        // 写入数据库
        boolean result = purchasePaymentService.save(purchasePayment);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        // 返回新写入的数据 id
        long newPurchasePaymentId = purchasePayment.getId();
        return ResultUtils.success(newPurchasePaymentId);
    }

    /**
     * 删除采购回款
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deletePurchasePayment(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = deleteRequest.getId();
        // 判断是否存在
        PurchasePayment oldPurchasePayment = purchasePaymentService.getById(id);
        ThrowUtils.throwIf(oldPurchasePayment == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if ( !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = purchasePaymentService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 更新采购回款（仅管理员可用）
     *
     * @param purchasePaymentUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updatePurchasePayment(@RequestBody PurchasePaymentUpdateRequest purchasePaymentUpdateRequest) {
        if (purchasePaymentUpdateRequest == null || purchasePaymentUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        PurchasePayment purchasePayment = new PurchasePayment();
        BeanUtils.copyProperties(purchasePaymentUpdateRequest, purchasePayment);
        // 数据校验
        purchasePaymentService.validPurchasePayment(purchasePayment, false);
        // 判断是否存在
        long id = purchasePaymentUpdateRequest.getId();
        PurchasePayment oldPurchasePayment = purchasePaymentService.getById(id);
        ThrowUtils.throwIf(oldPurchasePayment == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = purchasePaymentService.updateById(purchasePayment);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取采购回款（封装类）
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<PurchasePaymentVO> getPurchasePaymentVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        PurchasePayment purchasePayment = purchasePaymentService.getById(id);
        ThrowUtils.throwIf(purchasePayment == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return ResultUtils.success(purchasePaymentService.getPurchasePaymentVO(purchasePayment, request));
    }


    /**
     * 分页获取采购回款列表（封装类）
     *
     * @param purchasePaymentQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<PurchasePaymentVO>> listPurchasePaymentVOByPage(@RequestBody PurchasePaymentQueryRequest purchasePaymentQueryRequest,
                                                               HttpServletRequest request) {
        long current = purchasePaymentQueryRequest.getCurrent();
        long size = purchasePaymentQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 数据权限处理
        com.ledger.model.entity.User loginUser = userService.getLoginUser(request);
        String role = loginUser.getUserRole();
        if ("user".equalsIgnoreCase(role)) {
            purchasePaymentQueryRequest.setCreateId(loginUser.getId());
        }
        // 查询数据库
        Page<PurchasePayment> purchasePaymentPage = purchasePaymentService.page(new Page<>(current, size),
                purchasePaymentService.getQueryWrapper(purchasePaymentQueryRequest));
        // 获取封装类
        return ResultUtils.success(purchasePaymentService.getPurchasePaymentVOPage(purchasePaymentPage, request));
    }

    // endregion
}
