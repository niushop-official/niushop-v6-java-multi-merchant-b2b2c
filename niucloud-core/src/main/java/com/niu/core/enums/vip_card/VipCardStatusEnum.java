package com.niu.core.enums.vip_card;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * vip卡 状态
 */
@Getter
@AllArgsConstructor
public enum VipCardStatusEnum {
    ON(1, "开启"),
    OFF(2, "关闭");

    private final Integer code;
    private final String name;


    public static Map<String, String> getMap() {
        return Arrays.stream(VipCardStatusEnum.values())
                .collect(Collectors.toMap(e -> e.getCode().toString(), VipCardStatusEnum::getName));
    }

    public static VipCardStatusEnum getEnumByCode(Integer code) {
        return Arrays.stream(VipCardStatusEnum.values())
                .filter(e -> code.equals(e.getCode()))
                .findFirst()
                .orElse(null);
    }

    public static String getNameByCode(Integer code) {
        return Arrays.stream(VipCardStatusEnum.values())
                .filter(e -> code.equals(e.getCode()))
                .map(VipCardStatusEnum::getName)
                .findFirst()
                .orElse("");
    }

    public static Integer getCodeByName(String name) {
        return Arrays.stream(VipCardStatusEnum.values())
                .filter(e -> name.equals(e.getName()))
                .map(VipCardStatusEnum::getCode)
                .findFirst()
                .orElse(null);
    }
}
