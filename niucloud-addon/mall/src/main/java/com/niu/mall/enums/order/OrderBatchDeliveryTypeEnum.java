package com.niu.mall.enums.order;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public enum OrderBatchDeliveryTypeEnum {
    DELIVERY("delivery", "批量导入发货", "以表格形式导入订单，进行批量发货"),
    PRINT_DELIVERY("print_delivery", "批量打单发货", "选择多个订单，批量打印面单并发货"),
    SELECT_DELIVERY("select_delivery", "批量选择发货", "针对无需物流的订单，直接选择多个进行批量发货");

    private final String value;
    private final String name;
    private final String desc;

    public static String getNameByValue(String value) {
        return Arrays.stream(OrderBatchDeliveryTypeEnum.values())
                .filter(o -> o.getValue().equals(value))
                .findAny()
                .map(OrderBatchDeliveryTypeEnum::getName)
                .orElse("");
    }

    public static OrderBatchDeliveryTypeEnum getEnumByValue(String value) {
        return Arrays.stream(OrderBatchDeliveryTypeEnum.values())
                .filter(o -> o.getValue().equals(value))
                .findAny()
                .orElse(null);
    }

    public static Map<String, Map<String, String>> getMap() {
//        return Arrays.stream(OrderBatchDeliveryTypeEnum.values())
//                .collect(Collectors.toMap(OrderBatchDeliveryTypeEnum::getValue, OrderBatchDeliveryTypeEnum::getName));
        return Arrays.stream(OrderBatchDeliveryTypeEnum.values())
                .collect(Collectors.toMap(OrderBatchDeliveryTypeEnum::getValue,
                        item -> Map.of("name", item.getName(), "desc", item.getDesc())));
    }
}
