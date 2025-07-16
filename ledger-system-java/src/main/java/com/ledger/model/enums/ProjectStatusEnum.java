package com.ledger.model.enums;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 项目状态枚举
 *
 * @author 9K
 */
public enum ProjectStatusEnum {

    /**
     * 0:在建 1:完工整改 2:竣工验收 3:竣工结算 4:质保期内 5:完结
     */
    UNDER_CONSTRUCTION("在建", 0),
    COMPLETION_RECTIFICATION("完工整改", 1),
    COMPLETION_ACCEPTANCE("竣工验收", 2),
    COMPLETION_SETTLEMENT("竣工结算", 3),
    WITHIN_THE_WARRANTY_PERIOD("质保期内", 4),
    COMPLETION("完结", 5);

    private final String text;

    private final Integer value;

    ProjectStatusEnum(String text, Integer value) {
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
    public static ProjectStatusEnum getEnumByValue(Integer value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (ProjectStatusEnum anEnum : ProjectStatusEnum.values()) {
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
