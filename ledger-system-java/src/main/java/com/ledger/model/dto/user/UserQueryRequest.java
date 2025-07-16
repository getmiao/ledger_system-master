package com.ledger.model.dto.user;

import com.ledger.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 用户查询请求
 *
   * @author 9K
 
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserQueryRequest extends PageRequest implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 用户姓名
     */
    private String name;

    /**
     * 用户手机号
     */
    private String phone;

    /**
     * 用户部门
     */
    private String department;

    /**
     * 用户角色：user/admin/super/ban
     */
    private String userRole;

    private static final long serialVersionUID = 1L;
}