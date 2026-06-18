package com.niu.mall.enums.order;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum OrderTypePayEnum {

    pintuan("pintuan", "拼团", true),
    ;

    private final String type;
    private final String name;
    private final boolean isCustom;

    public OrderTypePayEnum getEnumByType(String type) {
        return Arrays.stream(OrderTypePayEnum.values())
                .filter(e -> e.getType().equals(type))
                .findFirst()
                .orElse(null);
    }
}
