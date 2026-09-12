package com.niu.core.enums.vip_card;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * vip卡 过期状态
 */
@Getter
@AllArgsConstructor
public enum VipCardExpireStatusEnum {
    EXPIRE_YES(1, "是"),
    EXPIRE_NO(0, "否");

    private final Integer code;
    private final String name;


    public static Map<String, String> getMap() {
        return Arrays.stream(VipCardExpireStatusEnum.values())
                .collect(Collectors.toMap(e -> e.getCode().toString(), VipCardExpireStatusEnum::getName));
    }

    public static VipCardExpireStatusEnum getEnumByCode(Integer code) {
        return Arrays.stream(VipCardExpireStatusEnum.values())
                .filter(e -> code.equals(e.getCode()))
                .findFirst()
                .orElse(null);
    }

    public static String getNameByCode(Integer code) {
        return Arrays.stream(VipCardExpireStatusEnum.values())
                .filter(e -> code.equals(e.getCode()))
                .map(VipCardExpireStatusEnum::getName)
                .findFirst()
                .orElse("");
    }

    public static Integer getCodeByName(String name) {
        return Arrays.stream(VipCardExpireStatusEnum.values())
                .filter(e -> name.equals(e.getName()))
                .map(VipCardExpireStatusEnum::getCode)
                .findFirst()
                .orElse(null);
    }
}
