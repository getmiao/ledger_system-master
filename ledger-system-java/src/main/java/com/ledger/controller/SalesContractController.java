package com.ledger.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ledger.common.BaseResponse;
import com.ledger.common.DeleteRequest;
import com.ledger.common.ErrorCode;
import com.ledger.common.ResultUtils;
import com.ledger.exception.BusinessException;
import com.ledger.exception.ThrowUtils;
import com.ledger.model.dto.sales.SalesContractAddRequest;
import com.ledger.model.dto.sales.SalesContractQueryRequest;
import com.ledger.model.dto.sales.SalesContractUpdateRequest;
import com.ledger.model.entity.SalesContract;
import com.ledger.model.entity.User;
import com.ledger.model.vo.SalesContractVO;
import com.ledger.service.SalesContractService;
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
 * 销售合同接口
 *
 * @author 9K
 */
@RestController
@RequestMapping("/salesContract")
@Slf4j
public class SalesContractController {

    @Resource
    private SalesContractService salesContractService;

    @Resource
    private UserService userService;

    // region 增删改查

    /**
     * 创建销售合同
     *
     * @param salesContractAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addSalesContract(@RequestBody SalesContractAddRequest salesContractAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(salesContractAddRequest == null, ErrorCode.PARAMS_ERROR);
        // 在此处将实体类和 DTO 进行转换
        SalesContract salesContract = new SalesContract();
        BeanUtils.copyProperties(salesContractAddRequest, salesContract);
        // 数据校验
        salesContractService.validSalesContract(salesContract, true);
        // 填充默认值
        User loginUser = userService.getLoginUser(request);
        salesContract.setCreateId(loginUser.getId());
        // 写入数据库
        boolean result = salesContractService.save(salesContract);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        // 返回新写入的数据 id
        long newSalesContractId = salesContract.getId();
        return ResultUtils.success(newSalesContractId);
    }

    /**
     * 删除销售合同
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteSalesContract(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        SalesContract oldSalesContract = salesContractService.getById(id);
        ThrowUtils.throwIf(oldSalesContract == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldSalesContract.getCreateId().equals(user.getId())
                && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = salesContractService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 更新销售合同
     *
     * @param salesContractUpdateRequest
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateSalesContract(@RequestBody SalesContractUpdateRequest salesContractUpdateRequest
            , HttpServletRequest request) {
        if (salesContractUpdateRequest == null || salesContractUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        if (!salesContractUpdateRequest.getCreateId().equals(user.getId())
                && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 在此处将实体类和 DTO 进行转换
        SalesContract salesContract = new SalesContract();
        BeanUtils.copyProperties(salesContractUpdateRequest, salesContract);
        // 数据校验
        salesContractService.validSalesContract(salesContract, false);
        // 判断是否存在
        long id = salesContractUpdateRequest.getId();
        SalesContract oldSalesContract = salesContractService.getById(id);
        ThrowUtils.throwIf(oldSalesContract == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = salesContractService.updateById(salesContract);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取销售合同（封装类）
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<SalesContractVO> getSalesContractVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        SalesContract salesContract = salesContractService.getById(id);
        ThrowUtils.throwIf(salesContract == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return ResultUtils.success(salesContractService.getSalesContractVO(salesContract, request));
    }

    /**
     * 分页获取销售合同列表（封装类）
     *
     * @param salesContractQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<SalesContractVO>> listSalesContractVOByPage(@RequestBody SalesContractQueryRequest salesContractQueryRequest,
                                                                         HttpServletRequest request) {
        long current = salesContractQueryRequest.getCurrent();
        long size = salesContractQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 数据权限处理
        User loginUser = userService.getLoginUser(request);
        String role = loginUser.getUserRole();
        if ("user".equalsIgnoreCase(role)) {
            salesContractQueryRequest.setCreateId(loginUser.getId());
        }
        // 查询数据库
        Page<SalesContract> salesContractPage = salesContractService.page(new Page<>(current, size),
                salesContractService.getQueryWrapper(salesContractQueryRequest));
        // 获取封装类
        return ResultUtils.success(salesContractService.getSalesContractVOPage(salesContractPage, request));
    }

    /**
     * 分页获取当前登录用户创建的销售合同列表
     *
     * @param salesContractQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<SalesContractVO>> listMySalesContractVOByPage(@RequestBody SalesContractQueryRequest salesContractQueryRequest,
                                                                           HttpServletRequest request) {
        ThrowUtils.throwIf(salesContractQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 补充查询条件，只查询当前登录用户的数据
        User loginUser = userService.getLoginUser(request);
        salesContractQueryRequest.setCreateId(loginUser.getId());
        long current = salesContractQueryRequest.getCurrent();
        long size = salesContractQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<SalesContract> salesContractPage = salesContractService.page(new Page<>(current, size),
                salesContractService.getQueryWrapper(salesContractQueryRequest));
        // 获取封装类
        return ResultUtils.success(salesContractService.getSalesContractVOPage(salesContractPage, request));
    }
    // endregion

    /**
     * key:value
     */
    @PostMapping("/list/keyV")
    public BaseResponse<Map<String, Long>> listSalesContractKeyV(@RequestBody(required = false) String contractName) {
        LambdaQueryWrapper<SalesContract> wrapper = new LambdaQueryWrapper<>();
        // 如果传入了合同名称，则添加模糊查询条件
        if (StringUtils.isNotBlank(contractName)) {
            wrapper.like(SalesContract::getContractName, contractName);
        }
        // 只查询id和contractName字段
        wrapper.select(SalesContract::getId, SalesContract::getContractName);
        // 获取合同列表
        List<SalesContract> salesContracts = salesContractService.list(wrapper);
        // 转换为Map<合同名称, 合同ID>
        Map<String, Long> resultMap = salesContracts.stream()
                .collect(Collectors.toMap(
                        SalesContract::getContractName,
                        SalesContract::getId,
                        (existing, replacement) -> existing
                ));
        return ResultUtils.success(resultMap);
    }
}
