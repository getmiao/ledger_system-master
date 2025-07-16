package com.ledger.model.vo;

import cn.hutool.json.JSONUtil;
import com.ledger.model.entity.SalesPayment;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 销售回款视图
 *
   * @author 9K
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
@Data
public class SalesPaymentVO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 标签列表
     */
    private List<String> tagList;

    /**
     * 创建用户信息
     */
    private UserVO user;

    /**
     * 封装类转对象
     *
     * @param salesPaymentVO
     * @return
     */
    public static SalesPayment voToObj(SalesPaymentVO salesPaymentVO) {
        if (salesPaymentVO == null) {
            return null;
        }
        SalesPayment salesPayment = new SalesPayment();
        BeanUtils.copyProperties(salesPaymentVO, salesPayment);
        List<String> tagList = salesPaymentVO.getTagList();
        salesPayment.setTags(JSONUtil.toJsonStr(tagList));
        return salesPayment;
    }

    /**
     * 对象转封装类
     *
     * @param salesPayment
     * @return
     */
    public static SalesPaymentVO objToVo(SalesPayment salesPayment) {
        if (salesPayment == null) {
            return null;
        }
        SalesPaymentVO salesPaymentVO = new SalesPaymentVO();
        BeanUtils.copyProperties(salesPayment, salesPaymentVO);
        salesPaymentVO.setTagList(JSONUtil.toList(salesPayment.getTags(), String.class));
        return salesPaymentVO;
    }
}
