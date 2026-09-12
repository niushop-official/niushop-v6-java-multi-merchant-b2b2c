package com.niu.core.enums.vip_card;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

/**
 * vip卡 订单状态
 */
@Getter
@AllArgsConstructor
public enum VipCardOrderStatusEnum {
    WAIT_PAY(1, "待支付"),
    FINISH(2, "已完成"),
    CLOSE(-1, "已关闭"),
    ;

    private final Integer code;
    private final String name;


    public static Map<String, String> getMap() {
        return Arrays.stream(VipCardOrderStatusEnum.values())
                .collect(Collectors.toMap(e -> e.getCode().toString(), VipCardOrderStatusEnum::getName));
    }

    public static VipCardOrderStatusEnum getEnumByCode(Integer code) {
        return Arrays.stream(VipCardOrderStatusEnum.values())
                .filter(e -> code.equals(e.getCode()))
                .findFirst()
                .orElse(null);
    }

    public static String getNameByCode(Integer code) {
        return Arrays.stream(VipCardOrderStatusEnum.values())
                .filter(e -> code.equals(e.getCode()))
                .map(VipCardOrderStatusEnum::getName)
                .findFirst()
                .orElse("");
    }

    public static Integer getCodeByName(String name) {
        return Arrays.stream(VipCardOrderStatusEnum.values())
                .filter(e -> name.equals(e.getName()))
                .map(VipCardOrderStatusEnum::getCode)
                .findFirst()
                .orElse(null);
    }

    public static Map<Integer, Map<String, Object>> getStatus() {
        return Arrays.stream(VipCardOrderStatusEnum.values())
                .map(e -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", e.getName());
                    map.put("status", e.getCode());
                    map.put("is_refund", 0);
                    map.put("action", new ArrayList<>());
                    List<Map<String, String>> memberAction = new ArrayList<>();
                    if (e.getCode().equals(1)) {
                        memberAction.add(Map.of("name", "支付", "class", "", "params", ""));
                    }
                    map.put("member_action", memberAction);
                    return map;
                }).collect(Collectors.toMap(m -> (Integer) m.getOrDefault("status", 0), m -> m));
    }
}
