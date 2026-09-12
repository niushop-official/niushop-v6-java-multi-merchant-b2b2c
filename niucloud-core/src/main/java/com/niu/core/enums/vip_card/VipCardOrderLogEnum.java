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
public enum VipCardOrderLogEnum {
    ADMIN("admin", "平台"),
    MEMBER("member", "会员"),
    SYSTEM("system", "系统"),
    ;

    private final String code;
    private final String name;


    public static Map<String, String> getMap() {
        return Arrays.stream(VipCardOrderLogEnum.values())
                .collect(Collectors.toMap(VipCardOrderLogEnum::getCode, VipCardOrderLogEnum::getName));
    }

    public static VipCardOrderLogEnum getEnumByCode(String code) {
        return Arrays.stream(VipCardOrderLogEnum.values())
                .filter(e -> code.equals(e.getCode()))
                .findFirst()
                .orElse(null);
    }

    public static String getNameByCode(String code) {
        return Arrays.stream(VipCardOrderLogEnum.values())
                .filter(e -> code.equals(e.getCode()))
                .map(VipCardOrderLogEnum::getName)
                .findFirst()
                .orElse("");
    }

    public static String getCodeByName(String name) {
        return Arrays.stream(VipCardOrderLogEnum.values())
                .filter(e -> name.equals(e.getName()))
                .map(VipCardOrderLogEnum::getCode)
                .findFirst()
                .orElse(null);
    }
}
