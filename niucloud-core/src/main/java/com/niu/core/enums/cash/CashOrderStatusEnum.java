package com.niu.core.enums.cash;

import cn.hutool.json.JSONArray;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 买单订单状态
 */
@Getter
@AllArgsConstructor
public enum CashOrderStatusEnum {

    WAIT_PAY("待支付", 1),
    FINISH("已完成", 2),
    CLOSE("已关闭", -1);

    private final String name;

    private final Integer status;

    public static Map<Integer, Map<String, Object>> getMap() {
        Map<Integer, Map<String, Object>> map = new HashMap<>();
        for (CashOrderStatusEnum item : CashOrderStatusEnum.values()) {
            Map<String, Object> mapItem = new HashMap<>();
            mapItem.put("name", item.getName());
            mapItem.put("status", item.getStatus());
            mapItem.put("is_refund", 0);
            mapItem.put("action", new JSONArray());
            JSONArray memberAction = new JSONArray();
            if (item == WAIT_PAY) {
                Map<String, String> payAction = new HashMap<>();
                payAction.put("name", "支付");
                payAction.put("class", "");
                payAction.put("params", "");
                memberAction.add(payAction);
            }
            mapItem.put("member_action", memberAction);
            map.put(item.getStatus(), mapItem);
        }
        return map;
    }

    public static Map<String, Object> getStatusInfo(Integer status) {
        if (status == null) {
            return Map.of();
        }
        return getMap().getOrDefault(status, Map.of());
    }

    public static String getNameByStatus(Integer status) {
        for (CashOrderStatusEnum item : CashOrderStatusEnum.values()) {
            if (item.getStatus().equals(status)) {
                return item.getName();
            }
        }
        return "";
    }
}
