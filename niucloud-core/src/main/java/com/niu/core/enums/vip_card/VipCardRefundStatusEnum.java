package com.niu.core.enums.vip_card;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * vip卡 退款状态
 */
@Getter
@AllArgsConstructor
public enum VipCardRefundStatusEnum {
    NOT_APPLY(0, "未申请"),
    REFUNDING(1, "退款中"),
    REFUND_COMPLETED(2, "退款完成"),
    REFUND_FAIL(-1, "退款失败"),
    ;

    private final Integer code;
    private final String name;


    public static Map<String, String> getMap() {
        return Arrays.stream(VipCardRefundStatusEnum.values())
                .collect(Collectors.toMap(e -> e.getCode().toString(), VipCardRefundStatusEnum::getName));
    }

    public static VipCardRefundStatusEnum getEnumByCode(Integer code) {
        return Arrays.stream(VipCardRefundStatusEnum.values())
                .filter(e -> code.equals(e.getCode()))
                .findFirst()
                .orElse(null);
    }

    public static String getNameByCode(Integer code) {
        return Arrays.stream(VipCardRefundStatusEnum.values())
                .filter(e -> code.equals(e.getCode()))
                .map(VipCardRefundStatusEnum::getName)
                .findFirst()
                .orElse("");
    }

    public static Integer getCodeByName(String name) {
        return Arrays.stream(VipCardRefundStatusEnum.values())
                .filter(e -> name.equals(e.getName()))
                .map(VipCardRefundStatusEnum::getCode)
                .findFirst()
                .orElse(null);
    }
}
