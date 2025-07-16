package com.ledger.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户更新请求
 *
 * @author 9K
 */
@Data
public class UserUpdateRequest implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 用户姓名
     */
    private String name;

    /**
     * 用户联系方式
     */
    private String phone;

    /**
     * 用户部门
     */
    private String department;

    /**
     * 用户角色：user/admin/ban
     */
    private String userRole;

    /**
     * 原密码（用于修改密码时校验）
     */
    private String oldPassword;

    /**
     * 新密码（如需修改密码时传递）
     */
    private String userPassword;

    private static final long serialVersionUID = 1L;
}