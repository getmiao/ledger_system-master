package com.ledger.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 已登录用户视图（脱敏）
 *
   * @author 9K
 
 **/
@Data
public class LoginUserVO implements Serializable {

    /**
     * 用户 id
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

    private static final long serialVersionUID = 1L;
}