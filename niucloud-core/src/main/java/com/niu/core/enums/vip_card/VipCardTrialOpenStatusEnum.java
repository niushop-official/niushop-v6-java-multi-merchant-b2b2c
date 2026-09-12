package com.niu.core.enums.vip_card;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * vip卡 开启试用状态
 */
@Getter
@AllArgsConstructor
public enum VipCardTrialOpenStatusEnum {
    TRIAL_OPEN(1, "开启"),
    TRIAL_CLOSE(0, "关闭");

    private final Integer code;
    private final String name;


    public static Map<String, String> getMap() {
        return Arrays.stream(VipCardTrialOpenStatusEnum.values())
                .collect(Collectors.toMap(e -> e.getCode().toString(), VipCardTrialOpenStatusEnum::getName));
    }

    public static VipCardTrialOpenStatusEnum getEnumByCode(Integer code) {
        return Arrays.stream(VipCardTrialOpenStatusEnum.values())
                .filter(e -> code.equals(e.getCode()))
                .findFirst()
                .orElse(null);
    }

    public static String getNameByCode(Integer code) {
        return Arrays.stream(VipCardTrialOpenStatusEnum.values())
                .filter(e -> code.equals(e.getCode()))
                .map(VipCardTrialOpenStatusEnum::getName)
                .findFirst()
                .orElse("");
    }

    public static Integer getCodeByName(String name) {
        return Arrays.stream(VipCardTrialOpenStatusEnum.values())
                .filter(e -> name.equals(e.getName()))
                .map(VipCardTrialOpenStatusEnum::getCode)
                .findFirst()
                .orElse(null);
    }
}
