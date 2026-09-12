package com.niu.core.enums.vip_card;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * vip卡 订单状态
 */
@Getter
@AllArgsConstructor
public enum VipCardOrderActionEnum {
    ORDER_CREATE_ACTION("order_create", "订单创建"),
    ORDER_PAY_ACTION("order_pay", "订单支付"),
    ORDER_CLOSE_ACTION("order_close", "订单关闭"),
    ;

    private final String code;
    private final String name;


    public static Map<String, String> getMap() {
        return Arrays.stream(VipCardOrderActionEnum.values())
                .collect(Collectors.toMap(e -> e.getCode(), VipCardOrderActionEnum::getName));
    }

    public static VipCardOrderActionEnum getEnumByCode(String code) {
        return Arrays.stream(VipCardOrderActionEnum.values())
                .filter(e -> code.equals(e.getCode()))
                .findFirst()
                .orElse(null);
    }

    public static String getNameByCode(String code) {
        return Arrays.stream(VipCardOrderActionEnum.values())
                .filter(e -> code.equals(e.getCode()))
                .map(VipCardOrderActionEnum::getName)
                .findFirst()
                .orElse("");
    }

    public static String getCodeByName(String name) {
        return Arrays.stream(VipCardOrderActionEnum.values())
                .filter(e -> name.equals(e.getName()))
                .map(VipCardOrderActionEnum::getCode)
                .findFirst()
                .orElse(null);
    }
}
