package com.niu.core.enums.vip_card;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * vip卡 时间类型
 */
@Getter
@AllArgsConstructor
public enum VipCardTimeTypeEnum {
    TIME_TYPE_DAY("day", "日"),
    TIME_TYPE_WEEK("week", "周"),
    TIME_TYPE_MONTH("month", "月"),
    TIME_TYPE_YEAR("year", "年"),
    ;

    private final String code;
    private final String name;


    public static Map<String, String> getMap() {
        return Arrays.stream(VipCardTimeTypeEnum.values())
                .collect(Collectors.toMap(VipCardTimeTypeEnum::getCode, VipCardTimeTypeEnum::getName));
    }

    public static VipCardTimeTypeEnum getEnumByCode(String code) {
        if (code == null || code.isEmpty()) {
            return null;
        }
        return Arrays.stream(VipCardTimeTypeEnum.values())
                .filter(e -> code.equals(e.getCode()))
                .findFirst()
                .orElse(null);
    }

    public static String getNameByCode(String code) {
        if (code == null || code.isEmpty()) {
            return "";
        }
        return Arrays.stream(VipCardTimeTypeEnum.values())
                .filter(e -> code.equals(e.getCode()))
                .map(VipCardTimeTypeEnum::getName)
                .findFirst()
                .orElse("");
    }

    public static String getCodeByName(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        return Arrays.stream(VipCardTimeTypeEnum.values())
                .filter(e -> name.equals(e.getName()))
                .map(VipCardTimeTypeEnum::getCode)
                .findFirst()
                .orElse(null);
    }
}
