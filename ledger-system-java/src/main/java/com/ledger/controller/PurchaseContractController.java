package com.ledger.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ledger.common.BaseResponse;
import com.ledger.common.DeleteRequest;
import com.ledger.common.ErrorCode;
import com.ledger.common.ResultUtils;
import com.ledger.exception.BusinessException;
import com.ledger.exception.ThrowUtils;
import com.ledger.model.dto.purchase.PurchaseContractAddRequest;
import com.ledger.model.dto.purchase.PurchaseContractEditRequest;
import com.ledger.model.dto.purchase.PurchaseContractQueryRequest;
import com.ledger.model.dto.purchase.PurchaseContractUpdateRequest;
import com.ledger.model.entity.PurchaseContract;
import com.ledger.model.entity.User;
import com.ledger.model.vo.PurchaseContractVO;
import com.ledger.service.PurchaseContractService;
import com.ledger.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 采购合同接口
 *
   * @author 9K
 */
@RestController
@RequestMapping("/purchaseContract")
@Slf4j
public class PurchaseContractController {

    @Resource
    private PurchaseContractService purchaseContractService;

    @Resource
    private UserService userService;

    // region 增删改查

    /**
     * 创建采购合同
     *
     * @param purchaseContractAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addPurchaseContract(@RequestBody PurchaseContractAddRequest purchaseContractAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(purchaseContractAddRequest == null, ErrorCode.PARAMS_ERROR);
        PurchaseContract purchaseContract = new PurchaseContract();
        BeanUtils.copyProperties(purchaseContractAddRequest, purchaseContract);
        // 数据校验
        purchaseContractService.validPurchaseContract(purchaseContract, true);
        // 填充默认值
        User loginUser = userService.getLoginUser(request);
        purchaseContract.setCreateId(loginUser.getId());
        // 写入数据库
        boolean result = purchaseContractService.save(purchaseContract);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        // 返回新写入的数据 id
        long newPurchaseContractId = purchaseContract.getId();
        return ResultUtils.success(newPurchaseContractId);
    }

    /**
     * 删除采购合同
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deletePurchaseContract(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        PurchaseContract oldPurchaseContract = purchaseContractService.getById(id);
        ThrowUtils.throwIf(oldPurchaseContract == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if ( !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = purchaseContractService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 更新采购合同（仅管理员可用）
     *
     * @param purchaseContractUpdateRequest
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updatePurchaseContract(@RequestBody PurchaseContractUpdateRequest purchaseContractUpdateRequest,
                                                        HttpServletRequest request) {
        if (purchaseContractUpdateRequest == null || purchaseContractUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        if (!purchaseContractUpdateRequest.getCreateId().equals(user.getId())
                && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        PurchaseContract purchaseContract = new PurchaseContract();
        BeanUtils.copyProperties(purchaseContractUpdateRequest, purchaseContract);
        // 数据校验
        purchaseContractService.validPurchaseContract(purchaseContract, false);
        // 判断是否存在
        long id = purchaseContractUpdateRequest.getId();
        PurchaseContract oldPurchaseContract = purchaseContractService.getById(id);
        ThrowUtils.throwIf(oldPurchaseContract == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = purchaseContractService.updateById(purchaseContract);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取采购合同（封装类）
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<PurchaseContractVO> getPurchaseContractVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        PurchaseContract purchaseContract = purchaseContractService.getById(id);
        ThrowUtils.throwIf(purchaseContract == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return ResultUtils.success(purchaseContractService.getPurchaseContractVO(purchaseContract, request));
    }


    /**
     * 分页获取采购合同列表（封装类）
     *
     * @param purchaseContractQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<PurchaseContractVO>> listPurchaseContractVOByPage(@RequestBody PurchaseContractQueryRequest purchaseContractQueryRequest,
                                                               HttpServletRequest request) {
        long current = purchaseContractQueryRequest.getCurrent();
        long size = purchaseContractQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 数据权限处理
        User loginUser = userService.getLoginUser(request);
        String role = loginUser.getUserRole();
        if ("user".equalsIgnoreCase(role)) {
            purchaseContractQueryRequest.setCreateId(loginUser.getId());
        }
        // 查询数据库
        Page<PurchaseContract> purchaseContractPage = purchaseContractService.page(new Page<>(current, size),
                purchaseContractService.getQueryWrapper(purchaseContractQueryRequest));
        // 获取封装类
        return ResultUtils.success(purchaseContractService.getPurchaseContractVOPage(purchaseContractPage, request));
    }

    /**
     * 分页获取当前登录用户创建的采购合同列表
     *
     * @param purchaseContractQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<PurchaseContractVO>> listMyPurchaseContractVOByPage(@RequestBody PurchaseContractQueryRequest purchaseContractQueryRequest,
                                                                 HttpServletRequest request) {
        ThrowUtils.throwIf(purchaseContractQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 补充查询条件，只查询当前登录用户的数据
        User loginUser = userService.getLoginUser(request);
        purchaseContractQueryRequest.setCreateId(loginUser.getId());
        long current = purchaseContractQueryRequest.getCurrent();
        long size = purchaseContractQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<PurchaseContract> purchaseContractPage = purchaseContractService.page(new Page<>(current, size),
                purchaseContractService.getQueryWrapper(purchaseContractQueryRequest));
        // 获取封装类
        return ResultUtils.success(purchaseContractService.getPurchaseContractVOPage(purchaseContractPage, request));
    }

    /**
     * 编辑采购合同（给用户使用）
     *
     * @param purchaseContractEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editPurchaseContract(@RequestBody PurchaseContractEditRequest purchaseContractEditRequest, HttpServletRequest request) {
        if (purchaseContractEditRequest == null || purchaseContractEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        PurchaseContract purchaseContract = new PurchaseContract();
        BeanUtils.copyProperties(purchaseContractEditRequest, purchaseContract);
        // 数据校验
        purchaseContractService.validPurchaseContract(purchaseContract, false);
        User loginUser = userService.getLoginUser(request);
        // 判断是否存在
        long id = purchaseContractEditRequest.getId();
        PurchaseContract oldPurchaseContract = purchaseContractService.getById(id);
        ThrowUtils.throwIf(oldPurchaseContract == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = purchaseContractService.updateById(purchaseContract);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    // endregion


    /**
     * key:value
     */
    @PostMapping("/list/keyV")
    public BaseResponse<Map<String, Long>> listPurchaseContractKeyV(@RequestBody(required = false) String contractName) {
        LambdaQueryWrapper<PurchaseContract> wrapper = new LambdaQueryWrapper<>();
        // 如果传入了合同名称，则添加模糊查询条件
        if (StringUtils.isNotBlank(contractName)) {
            wrapper.like(PurchaseContract::getContractName, contractName);
        }
        // 只查询id和contractName字段
        wrapper.select(PurchaseContract::getId, PurchaseContract::getContractName);
        // 获取合同列表
        List<PurchaseContract> purchaseContracts = purchaseContractService.list(wrapper);
        // 转换为Map<合同名称, 合同ID>
        Map<String, Long> resultMap = purchaseContracts.stream()
                .collect(Collectors.toMap(
                        PurchaseContract::getContractName,
                        PurchaseContract::getId,
                        (existing, replacement) -> existing
                ));
        return ResultUtils.success(resultMap);
    }
}
