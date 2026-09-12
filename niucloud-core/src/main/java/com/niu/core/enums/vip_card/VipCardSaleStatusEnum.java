package com.niu.core.enums.vip_card;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * vip卡 售卖状态
 */
@Getter
@AllArgsConstructor
public enum VipCardSaleStatusEnum {
    ON(1, "售卖"),
    OFF(0, "");

    private final Integer code;
    private final String name;


    public static Map<String, String> getMap() {
        return Arrays.stream(VipCardSaleStatusEnum.values())
                .collect(Collectors.toMap(e -> e.getCode().toString(), VipCardSaleStatusEnum::getName));
    }

    public static VipCardSaleStatusEnum getEnumByCode(Integer code) {
        return Arrays.stream(VipCardSaleStatusEnum.values())
                .filter(e -> code.equals(e.getCode()))
                .findFirst()
                .orElse(null);
    }

    public static String getNameByCode(Integer code) {
        return Arrays.stream(VipCardSaleStatusEnum.values())
                .filter(e -> code.equals(e.getCode()))
                .map(VipCardSaleStatusEnum::getName)
                .findFirst()
                .orElse("");
    }

    public static Integer getCodeByName(String name) {
        return Arrays.stream(VipCardSaleStatusEnum.values())
                .filter(e -> name.equals(e.getName()))
                .map(VipCardSaleStatusEnum::getCode)
                .findFirst()
                .orElse(null);
    }
}
