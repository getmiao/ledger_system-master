package com.ledger.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户创建请求
 *
   * @author 9K
 
 */
@Data
public class UserAddRequest implements Serializable {

    /**
     * 账号
     */
    private String userAccount;

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
     * 用户角色: user, admin
     */
    private String userRole;

    private static final long serialVersionUID = 1L;
}