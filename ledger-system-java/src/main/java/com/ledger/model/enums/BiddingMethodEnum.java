package com.ledger.model.enums;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 招标方式枚举
 *
 * @author 9K
 */
public enum BiddingMethodEnum {

    /**
     * 招标方式 0:公开招标 1:竞争性磋商 2:询价比 3:直接委托 4:其他
     */
    PUBLIC("公开招标", 0),
    COMPETITIVE_NEGOTIATION("竞争性磋商", 1),
    INQUIRY_RATIO("询价比", 2),
    DIRECT_COMMISSION("直接委托", 3),
    OTHER("其他", 4);

    private final String text;

    private final Integer value;

    BiddingMethodEnum(String text, Integer value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 获取值列表
     *
     * @return
     */
    public static List<Integer> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value
     * @return
     */
    public static BiddingMethodEnum getEnumByValue(Integer value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (BiddingMethodEnum anEnum : BiddingMethodEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }

    public Integer getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}
