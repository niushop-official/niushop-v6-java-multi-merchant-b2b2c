package com.niu.core.enums.vip_card;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * vip卡 续期状态
 */
@Getter
@AllArgsConstructor
public enum VipCardRenewStatusEnum {
    RENEW_YES(1, "是"),
    RENEW_NO(0, "否");

    private final Integer code;
    private final String name;


    public static Map<String, String> getMap() {
        return Arrays.stream(VipCardRenewStatusEnum.values())
                .collect(Collectors.toMap(e -> e.getCode().toString(), VipCardRenewStatusEnum::getName));
    }

    public static VipCardRenewStatusEnum getEnumByCode(Integer code) {
        return Arrays.stream(VipCardRenewStatusEnum.values())
                .filter(e -> code.equals(e.getCode()))
                .findFirst()
                .orElse(null);
    }

    public static String getNameByCode(Integer code) {
        return Arrays.stream(VipCardRenewStatusEnum.values())
                .filter(e -> code.equals(e.getCode()))
                .map(VipCardRenewStatusEnum::getName)
                .findFirst()
                .orElse("");
    }

    public static Integer getCodeByName(String name) {
        return Arrays.stream(VipCardRenewStatusEnum.values())
                .filter(e -> name.equals(e.getName()))
                .map(VipCardRenewStatusEnum::getCode)
                .findFirst()
                .orElse(null);
    }
}
