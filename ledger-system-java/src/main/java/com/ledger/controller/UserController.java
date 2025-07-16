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
import com.ledger.model.dto.user.UserAddRequest;
import com.ledger.model.dto.user.UserLoginRequest;
import com.ledger.model.dto.user.UserQueryRequest;
import com.ledger.model.dto.user.UserUpdateRequest;
import com.ledger.model.entity.User;
import com.ledger.model.vo.LoginUserVO;
import com.ledger.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import static com.ledger.service.impl.UserServiceImpl.SALT;

/**
 * 用户接口
 *
 * @author 9K
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Resource
    private UserService userService;

    // region 登录相关

    /**
     * 用户登录
     *
     * @param userLoginRequest
     * @param request
     * @return
     */
    @PostMapping("/login")
    public BaseResponse<LoginUserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        LoginUserVO loginUserVO = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(loginUserVO);
    }

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    @GetMapping("/get/login")
    public BaseResponse<LoginUserVO> getLoginUser(HttpServletRequest request) {
        User user = userService.getLoginUser(request);
        return ResultUtils.success(userService.getLoginUserVO(user));
    }

    // endregion

    // region 增删改查

    /**
     * 创建用户
     *
     * @param userAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.SUPER_ROLE)
    public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest, HttpServletRequest request) {
        if (userAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = new User();
        BeanUtils.copyProperties(userAddRequest, user);
        // 默认密码 12345678
        String defaultPassword = "12345678";
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + defaultPassword).getBytes());
        user.setUserPassword(encryptPassword);
        boolean result = userService.save(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(user.getId());
    }

    /**
     * 删除用户
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.SUPER_ROLE)
    public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = userService.removeById(deleteRequest.getId());
        return ResultUtils.success(b);
    }

    /**
     * 更新用户
     *
     * @param userUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.SUPER_ROLE)
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest,
                                            HttpServletRequest request) {
        if (userUpdateRequest == null || userUpdateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        // 只有超级管理员才能修改userRole
        if (!UserConstant.SUPER_ROLE.equals(loginUser.getUserRole()) && userUpdateRequest.getUserRole() != null) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "只有超级管理员才能修改角色权限");
        }
        User user = new User();
        BeanUtils.copyProperties(userUpdateRequest, user);
        // 非超级管理员强制忽略userRole
        if (!UserConstant.SUPER_ROLE.equals(loginUser.getUserRole())) {
            user.setUserRole(null);
        }
        // 密码修改逻辑：如前端传递了oldPassword和userPassword，则校验原密码并允许修改
        if (StringUtils.isNotBlank(userUpdateRequest.getOldPassword()) && StringUtils.isNotBlank(userUpdateRequest.getUserPassword())) {
            User dbUser = userService.getById(userUpdateRequest.getId());
            String oldPwdMd5 = DigestUtils.md5DigestAsHex((SALT + userUpdateRequest.getOldPassword()).getBytes());
            if (!oldPwdMd5.equals(dbUser.getUserPassword())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "原密码错误");
            }
            user.setUserPassword(DigestUtils.md5DigestAsHex((SALT + userUpdateRequest.getUserPassword()).getBytes()));
        } else {
            user.setUserPassword(null); // 不传新密码时不更新密码
        }
        boolean result = userService.updateById(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取用户（仅管理员）
     *
     * @param id
     * @param request
     * @return
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.SUPER_ROLE)
    public BaseResponse<User> getUserById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getById(id);
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(user);
    }

    /**
     * 分页获取用户列表（仅管理员）
     *
     * @param userQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.SUPER_ROLE)
    public BaseResponse<Page<User>> listUserByPage(@RequestBody UserQueryRequest userQueryRequest,
                                                   HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        long current = userQueryRequest.getCurrent();
        long size = userQueryRequest.getPageSize();
        Page<User> userPage = userService.page(new Page<>(current, size),
                userService.getQueryWrapper(userQueryRequest, loginUser));
        return ResultUtils.success(userPage);
    }
    // endregion
}
