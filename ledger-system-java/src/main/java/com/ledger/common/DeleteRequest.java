package com.ledger.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 删除请求
 *
   * @author 9K
 
 */
@Data
public class DeleteRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    private static final long serialVersionUID = 1L;
}